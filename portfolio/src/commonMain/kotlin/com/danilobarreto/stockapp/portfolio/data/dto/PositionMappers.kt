package com.danilobarreto.stockapp.portfolio.data.dto

import com.danilobarreto.stockapp.portfolio.domain.Position

fun PositionDto.toDomain(): Position = Position(
    id = id,
    ticker = ticker,
    quantity = quantity,
    avgPrice = avgPrice,
    createdAt = createdAt,
    updatedAt = updatedAt,
)