package com.danilobarreto.stockapp.portfolio.data.dto

import com.danilobarreto.stockapp.portfolio.domain.AssetType
import com.danilobarreto.stockapp.portfolio.domain.MonthlyDividends
import com.danilobarreto.stockapp.portfolio.domain.PortfolioHistoryPoint
import com.danilobarreto.stockapp.portfolio.domain.PortfolioSummary
import com.danilobarreto.stockapp.portfolio.domain.Position
import com.danilobarreto.stockapp.portfolio.domain.PositionSummary

fun PositionDto.toDomain(): Position = Position(
    id = id,
    ticker = ticker,
    assetType = AssetType.valueOf(assetType),
    quantity = quantity,
    avgPrice = avgPrice,
    createdAt = createdAt,
    updatedAt = updatedAt,
)

fun PortfolioSummaryDto.toDomain(): PortfolioSummary = PortfolioSummary(
    totalValue = totalValue,
    investedValue = investedValue,
    profitValue = profitValue,
    profitPercent = profitPercent,
    positions = positions.map { it.toDomain() },
)

fun PositionSummaryItemDto.toDomain(): PositionSummary = PositionSummary(
    id = id,
    ticker = ticker,
    companyName = companyName,
    assetType = AssetType.valueOf(assetType),
    quantity = quantity,
    avgPrice = avgPrice,
    currentPrice = currentPrice,
    currentValue = currentValue,
    profitPercent = profitPercent,
    allocationPercent = allocationPercent,
    logoUrl = logoUrl,
    dividendPerShareTtm = dividendPerShareTtm,
    eps = eps,
    bookValuePerShare = bookValuePerShare,
    priceToSalesRatio = priceToSalesRatio,
    earningsCagr5y = earningsCagr5y,
)

fun MonthlyDividendsDto.toDomain(): MonthlyDividends = MonthlyDividends(
    totalValue = totalValue,
    paymentsCount = paymentsCount,
)

fun PortfolioHistoryPointDto.toDomain(): PortfolioHistoryPoint = PortfolioHistoryPoint(
    month = month,
    totalValue = totalValue,
)