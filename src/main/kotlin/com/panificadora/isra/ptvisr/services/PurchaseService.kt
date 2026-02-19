package com.panificadora.isra.ptvisr.services

import com.panificadora.isra.ptvisr.dtos.PurchaseFormDto
import com.panificadora.isra.ptvisr.models.Purchase
import com.panificadora.isra.ptvisr.models.PurchaseDetail
import com.panificadora.isra.ptvisr.repositories.ProductRepository
import com.panificadora.isra.ptvisr.repositories.PurchaseRepository
import com.panificadora.isra.ptvisr.repositories.SupplierRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
class PurchaseService(
    private val purchaseRepository: PurchaseRepository,
    private val productRepository: ProductRepository,
    private val supplierRepository: SupplierRepository
) {

    @Transactional
    fun savePurchase(purchaseFormDto: PurchaseFormDto): Purchase {
        val supplier = purchaseFormDto.supplierId?.let {
            supplierRepository.findById(it).orElseThrow {
                IllegalArgumentException("Supplier with ID ${it} not found")
            }
        }

        val purchase = Purchase(
            supplier = supplier,
            purchaseDate = purchaseFormDto.purchaseDate,
            total = BigDecimal.ZERO // Will be calculated from details
        )

        val purchaseDetails = mutableListOf<PurchaseDetail>()
        var calculatedTotal = BigDecimal.ZERO

        for (detailDto in purchaseFormDto.purchaseDetails) {
            val product = productRepository.findById(detailDto.productId).orElseThrow {
                IllegalArgumentException("Product with ID ${detailDto.productId} not found")
            }

            // Create PurchaseDetail
            val purchaseDetail = PurchaseDetail(
                purchase = purchase,
                product = product,
                quantity = detailDto.quantity,
                price = detailDto.price
            )
            purchaseDetails.add(purchaseDetail)

            // Update product stock
            product.stock = product.stock.add(detailDto.quantity)
            productRepository.save(product) // Save updated product stock

            calculatedTotal = calculatedTotal.add(detailDto.quantity.multiply(detailDto.price))
        }

        purchase.details.addAll(purchaseDetails)
        purchase.total = calculatedTotal

        // Save purchase and its details (due to cascade settings)
        return purchaseRepository.save(purchase)
    }
}
