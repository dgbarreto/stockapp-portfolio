package com.danilobarreto.stockapp.portfolio.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class PositionDto(
    val id: String,
    val ticker: String,
    val assetType: String,
    val quantity: Int,
    val avgPrice: Double,
    val createdAt: String,
    val updatedAt: String,
)