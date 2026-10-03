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
    val companyName: String? = null,
    val assetType: AssetType,
    val quantity: Int,
    val avgPrice: Double,
    val currentPrice: Double?,
    val currentValue: Double?,
    val profitPercent: Double?,
    val allocationPercent: Double?,
    val logoUrl: String?,
    val dividendPerShareTtm: Double?,
    val eps: Double?,
    val bookValuePerShare: Double?,
    val priceToSalesRatio: Double?,
    val earningsCagr5y: Double?,
)

data class MonthlyDividends(
    val totalValue: Double,
    val paymentsCount: Int,
)

data class PortfolioHistoryPoint(
    val month: String, // "2025-09"
    val totalValue: Double,
)