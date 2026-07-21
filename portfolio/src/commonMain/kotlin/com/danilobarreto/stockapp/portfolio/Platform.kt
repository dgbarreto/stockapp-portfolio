package com.danilobarreto.stockapp.portfolio

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
