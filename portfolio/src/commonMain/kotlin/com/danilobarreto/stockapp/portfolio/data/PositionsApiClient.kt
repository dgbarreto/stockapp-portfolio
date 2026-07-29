package com.danilobarreto.stockapp.portfolio.data

import com.danilobarreto.stockapp.portfolio.data.dto.PortfolioSummaryDto
import com.danilobarreto.stockapp.portfolio.data.dto.PositionDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class PositionsApiClient(
    private val httpClient: HttpClient,
    private val baseUrl: String,
) {
    suspend fun getPositions(): List<PositionDto> =
        httpClient.get("$baseUrl/positions").body()

    suspend fun getSummary(): PortfolioSummaryDto =
        httpClient.get("$baseUrl/positions/summary").body()
}