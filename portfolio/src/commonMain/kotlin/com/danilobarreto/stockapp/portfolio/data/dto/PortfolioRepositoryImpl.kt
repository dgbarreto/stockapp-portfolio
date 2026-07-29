package com.danilobarreto.stockapp.portfolio.data

import com.danilobarreto.stockapp.portfolio.data.dto.toDomain
import com.danilobarreto.stockapp.portfolio.domain.PortfolioRepository
import com.danilobarreto.stockapp.portfolio.domain.PortfolioSummary
import com.danilobarreto.stockapp.portfolio.domain.Position

class PortfolioRepositoryImpl(
    private val apiClient: PositionsApiClient,
) : PortfolioRepository {
    override suspend fun getPositions(): List<Position> =
        apiClient.getPositions().map { it.toDomain() }

    override suspend fun getSummary(): PortfolioSummary =
        apiClient.getSummary().toDomain()
}