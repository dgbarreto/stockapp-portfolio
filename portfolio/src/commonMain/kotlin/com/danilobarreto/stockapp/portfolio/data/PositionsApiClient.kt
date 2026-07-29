package com.danilobarreto.stockapp.portfolio.data

import com.danilobarreto.stockapp.portfolio.data.dto.CreatePositionRequestDto
import com.danilobarreto.stockapp.portfolio.data.dto.ErrorResponseDto
import com.danilobarreto.stockapp.portfolio.data.dto.PortfolioSummaryDto
import com.danilobarreto.stockapp.portfolio.data.dto.PositionDto
import com.danilobarreto.stockapp.portfolio.data.dto.UpdatePositionRequestDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class PositionsApiClient(
    private val httpClient: HttpClient,
    private val baseUrl: String,
) {
    suspend fun getPositions(): List<PositionDto> =
        httpClient.get("$baseUrl/positions").body()

    suspend fun getSummary(): PortfolioSummaryDto =
        httpClient.get("$baseUrl/positions/summary").body()
}