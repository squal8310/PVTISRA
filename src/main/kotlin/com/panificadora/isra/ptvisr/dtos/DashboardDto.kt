package com.panificadora.isra.ptvisr.dtos

data class DashboardDto(
    val ventasHoy: Double,
    val numVentasHoy: Int,
    val ventasMes: Double,
    val numVentasMes: Int,
    val stockBajo: List<MobileProductDto>,
    val sinVentas: List<MobileProductDto>
)