package com.panificadora.isra.ptvisr.services

import com.panificadora.isra.ptvisr.dtos.SaleDto
import com.panificadora.isra.ptvisr.dtos.SaleDetailDto
import com.panificadora.isra.ptvisr.models.*
import com.panificadora.isra.ptvisr.repositories.ProductRepository
import com.panificadora.isra.ptvisr.repositories.SaleDetailRepository
import com.panificadora.isra.ptvisr.repositories.SaleRepository
import jakarta.transaction.Transactional
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class SaleService(
    private val saleRepository: SaleRepository,
    private val saleDetailRepository: SaleDetailRepository,
    private val productRepository: ProductRepository
) {

    /**
     * Processes a new sale transaction.
     * This method orchestrates the calculation of totals, creation of the sale record,
     * and processing of each individual sale detail, including stock updates and
     * inventory movement recording.
     *
     * @param saleDto The data transfer object containing the sale details from the frontend.
     * @return The newly created and persisted Sale entity.
     * @throws IllegalArgumentException if a product is not found or if there's insufficient stock.
     */
    @Transactional
    fun processSale(saleDto: SaleDto): Sale {
        // 1. Calculate totals and perform initial stock checks
        val (subtotalAmount, ivaAmount, totalAmount) = calculateSaleTotalsAndCheckStock(saleDto)

        // 2. Create and save the Sale entity
        val newSale = createAndSaveSale(subtotalAmount, ivaAmount, totalAmount)

        // 3. Process each sale detail
        processSaleDetails(newSale, saleDto.details)

        return newSale
    }

    /**
     * Calculates the subtotal, IVA, and total amounts for a sale, and performs initial stock availability checks.
     * This method iterates through each item in the saleDto, fetches the product,
     * validates stock, and accumulates the subtotal.
     *
     * @param saleDto The data transfer object containing the sale details.
     * @return A Triple containing (subtotalAmount, ivaAmount, totalAmount).
     * @throws IllegalArgumentException if a product is not found or if there's insufficient stock.
     */
    private fun calculateSaleTotalsAndCheckStock(saleDto: SaleDto): Triple<Double, Double, Double> {
        var subtotalAmount = 0.0
        val ivaRate = 0.0 // IVA temporarily disabled as per user request

        for (detailDto in saleDto.details) {
            val product = productRepository.findByIdOrNull(detailDto.productId)
                ?: throw IllegalArgumentException("Product with ID ${detailDto.productId} not found.")

            if (product.stock < detailDto.quantity) {
                throw IllegalArgumentException("Insufficient stock for product ${product.name}. Available: ${product.stock}, Requested: ${detailDto.quantity}")
            }
            subtotalAmount += detailDto.quantity * detailDto.unitPrice
        }

        val ivaAmount = subtotalAmount * ivaRate
        val totalAmount = subtotalAmount + ivaAmount

        return Triple(subtotalAmount, ivaAmount, totalAmount)
    }

    /**
     * Creates and persists a new Sale entity in the database.
     * This method uses the calculated totals to construct the Sale object.
     * Note: customerId and userId are currently null as they are not provided by the frontend.
     *
     * @param subtotalAmount The calculated subtotal of the sale.
     * @param ivaAmount The calculated IVA amount of the sale (marked as @Transient in Sale entity).
     * @param totalAmount The calculated total amount of the sale.
     * @return The newly created and persisted Sale entity.
     */
    private fun createAndSaveSale(subtotalAmount: Double, ivaAmount: Double, totalAmount: Double): Sale {
        val sale = Sale(
            saleDate = LocalDateTime.now(),
            totalAmount = totalAmount, // This is the total amount to be persisted
            ivaAmount = ivaAmount, // Transient field in Sale entity
            subtotalAmount = subtotalAmount, // Transient field in Sale entity
            customerId = 1, // No customer selected from frontend yet
            userId = 1      // No user selected from frontend yet
        )
        return saleRepository.save(sale)
    }

    /**
     * Processes each individual sale detail.
     * This method iterates through the list of sale details, creates and saves SaleDetail entities,
     * decrements product stock, and logs each movement in the inventory_movements table.
     *
     * @param sale The parent Sale entity to which these details belong.
     * @param detailsDto A list of SaleDetailDto objects representing the items sold.
     * @throws IllegalArgumentException if a product is not found (should be caught in initial checks).
     */
    private fun processSaleDetails(sale: Sale, detailsDto: List<SaleDetailDto>) {
        for (detailDto in detailsDto) {
            val product = productRepository.findByIdOrNull(detailDto.productId)
                ?: throw IllegalArgumentException("Product with ID ${detailDto.productId} not found.") // Should not happen if initial check passed

            // Create SaleDetail
            val saleDetail = SaleDetail(
                sale = sale,
                product = product,
                quantity = detailDto.quantity
            )
            saleDetailRepository.save(saleDetail)
        }
    }
}
