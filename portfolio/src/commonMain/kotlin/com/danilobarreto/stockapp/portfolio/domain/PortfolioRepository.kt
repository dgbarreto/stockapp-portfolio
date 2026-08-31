package com.danilobarreto.stockapp.portfolio.domain

interface PortfolioRepository {
    suspend fun getPositions(): List<Position>
    suspend fun getSummary(): PortfolioSummary
    suspend fun getMonthlyDividends(): MonthlyDividends
    suspend fun getHistory(months: Int = 12): List<PortfolioHistoryPoint>
}