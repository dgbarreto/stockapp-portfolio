package com.danilobarreto.stockapp.portfolio.domain

interface PortfolioRepository {
    suspend fun getPositions(): List<Position>
    suspend fun createPosition(ticker: String, quantity: Int, avgPrice: Double): Position
    suspend fun updatePosition(id: String, quantity: Int?, avgPrice: Double?): Position
    suspend fun deletePosition(id: String): Position
}