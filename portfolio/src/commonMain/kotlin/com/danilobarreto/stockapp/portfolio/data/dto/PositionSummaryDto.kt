package com.danilobarreto.stockapp.portfolio.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class PortfolioSummaryDto(
    val totalValue: Double,
    val investedValue: Double,
    val profitValue: Double,
    val profitPercent: Double?,
    val positions: List<PositionSummaryItemDto>,
)

@Serializable
data class PositionSummaryItemDto(
    val id: String,
    val ticker: String,
    val assetType: String,
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