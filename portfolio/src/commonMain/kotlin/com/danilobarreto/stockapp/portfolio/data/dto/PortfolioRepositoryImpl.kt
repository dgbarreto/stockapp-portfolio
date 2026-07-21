package com.danilobarreto.stockapp.portfolio.data

import com.danilobarreto.stockapp.portfolio.data.dto.CreatePositionRequestDto
import com.danilobarreto.stockapp.portfolio.data.dto.UpdatePositionRequestDto
import com.danilobarreto.stockapp.portfolio.data.dto.toDomain
import com.danilobarreto.stockapp.portfolio.domain.PortfolioRepository
import com.danilobarreto.stockapp.portfolio.domain.Position

class PortfolioRepositoryImpl(
    private val apiClient: PositionsApiClient,
) : PortfolioRepository {
    override suspend fun getPositions(): List<Position> =
        apiClient.getPositions().map { it.toDomain() }

    override suspend fun createPosition(ticker: String, quantity: Int, avgPrice: Double): Position =
        apiClient.createPosition(CreatePositionRequestDto(ticker, quantity, avgPrice)).toDomain()

    override suspend fun updatePosition(id: String, quantity: Int?, avgPrice: Double?): Position =
        apiClient.updatePosition(id, UpdatePositionRequestDto(quantity, avgPrice)).toDomain()

    override suspend fun deletePosition(id: String): Position =
        apiClient.deletePosition(id).toDomain()
}