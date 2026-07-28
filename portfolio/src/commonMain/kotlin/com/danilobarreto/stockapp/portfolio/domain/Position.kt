package com.danilobarreto.stockapp.portfolio.domain

data class Position(
    val id: String,
    val ticker: String,
    val assetType: AssetType,
    val quantity: Int,
    val avgPrice: Double,
    val createdAt: String,
    val updatedAt: String,
)