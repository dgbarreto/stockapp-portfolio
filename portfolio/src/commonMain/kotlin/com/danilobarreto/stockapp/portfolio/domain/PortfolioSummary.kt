package com.danilobarreto.stockapp.portfolio.domain

data class PortfolioSummary(
    val totalValue: Double,
    val investedValue: Double,
    val profitValue: Double,
    val profitPercent: Double?,
    val positions: List<PositionSummary>,
)

data class PositionSummary(
    val id: String,
    val ticker: String,
    val quantity: Int,
    val avgPrice: Double,
    val currentPrice: Double?,
    val currentValue: Double?,
    val profitPercent: Double?,
    val allocationPercent: Double?,
)