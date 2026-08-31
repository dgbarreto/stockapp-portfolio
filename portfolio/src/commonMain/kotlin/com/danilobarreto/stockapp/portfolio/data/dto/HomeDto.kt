package com.danilobarreto.stockapp.portfolio.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class MonthlyDividendsDto(
    val totalValue: Double,
    val paymentsCount: Int,
)

@Serializable
data class PortfolioHistoryPointDto(
    val month: String,
    val totalValue: Double,
)