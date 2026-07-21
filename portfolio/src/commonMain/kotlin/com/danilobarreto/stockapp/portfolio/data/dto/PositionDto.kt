package com.danilobarreto.stockapp.portfolio.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class PositionDto(
    val id: String,
    val ticker: String,
    val quantity: Int,
    val avgPrice: Double,
    val createdAt: String,
    val updatedAt: String,
)

@Serializable
data class CreatePositionRequestDto(
    val ticker: String,
    val quantity: Int,
    val avgPrice: Double,
)

@Serializable
data class UpdatePositionRequestDto(
    val quantity: Int? = null,
    val avgPrice: Double? = null,
)

@Serializable
data class ErrorResponseDto(
    val statusCode: Int,
    val message: String,
    val error: String? = null,
)