package com.danilobarreto.stockapp.portfolio.domain

interface PortfolioRepository {
    suspend fun getPositions(): List<Position>
    suspend fun getSummary(): PortfolioSummary
}