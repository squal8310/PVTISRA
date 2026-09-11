package com.panificadora.isra.ptvisr.services

import com.panificadora.isra.ptvisr.dtos.DashboardDto
import com.panificadora.isra.ptvisr.dtos.toMobileDto
import com.panificadora.isra.ptvisr.repositories.ProductRepository
import com.panificadora.isra.ptvisr.repositories.SaleRepository
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class DashboardService(
    private val saleRepository: SaleRepository,
    private val productRepository: ProductRepository
) {
    
    fun getDashboardData(): DashboardDto {
        val hoy = LocalDate.now()
        val inicioMes = hoy.withDayOfMonth(1)
        val finMes = hoy.withDayOfMonth(hoy.lengthOfMonth())
        
        // Ventas de hoy
        val ventasHoy = saleRepository.findBySaleDateBetween(
            hoy.atStartOfDay(),
            hoy.atTime(23, 59, 59)
        )
        val totalVentasHoy = ventasHoy.sumOf { sale -> sale.totalAmount }
        val numVentasHoy = ventasHoy.size
        
        // Ventas del mes
        val ventasMes = saleRepository.findBySaleDateBetween(
            inicioMes.atStartOfDay(),
            finMes.atTime(23, 59, 59)
        )
        val totalVentasMes = ventasMes.sumOf { sale -> sale.totalAmount }
        val numVentasMes = ventasMes.size
        
        // Productos con stock bajo
        val productosStockBajo = productRepository.findAll().filter { product ->
            val stockLimitValue = product.stockLimit
            stockLimitValue != null && product.stock <= stockLimitValue && product.stock > 0
        }.map { it.toMobileDto() }
        
        // Productos sin ventas (productos que nunca han sido vendidos)
        val productosSinVentas = productRepository.findProductsNeverSold().map { it.toMobileDto() }
        
        return DashboardDto(
            ventasHoy = totalVentasHoy,
            numVentasHoy = numVentasHoy,
            ventasMes = totalVentasMes,
            numVentasMes = numVentasMes,
            stockBajo = productosStockBajo,
            sinVentas = productosSinVentas
        )
    }
}