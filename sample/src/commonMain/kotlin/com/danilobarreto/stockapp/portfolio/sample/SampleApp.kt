package com.danilobarreto.stockapp.portfolio.sample

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.danilobarreto.stockapp.auth.data.AuthApiClient
import com.danilobarreto.stockapp.auth.data.AuthRepositoryImpl
import com.danilobarreto.stockapp.auth.data.TokenStorage
import com.danilobarreto.stockapp.auth.presentation.LoginScreen
import com.danilobarreto.stockapp.auth.presentation.LoginViewModel
import com.danilobarreto.stockapp.designsystem.theme.StockAppTheme
import com.danilobarreto.stockapp.portfolio.data.PortfolioRepositoryImpl
import com.danilobarreto.stockapp.portfolio.data.PositionsApiClient
import com.danilobarreto.stockapp.portfolio.presentation.DashboardScreen
import com.danilobarreto.stockapp.portfolio.presentation.DashboardViewModel

private sealed interface SampleScreen {
    data object Dashboard : SampleScreen
    data object AddPosition : SampleScreen
}

@Composable
fun SampleApp() {
    val tokenStorage = remember { TokenStorage() }
    val httpClient = remember { createSampleHttpClient(tokenStorage) }

    val authRepository = remember {
        AuthRepositoryImpl(AuthApiClient(httpClient, sampleBaseUrl()), tokenStorage)
    }
    val portfolioRepository = remember {
        PortfolioRepositoryImpl(PositionsApiClient(httpClient, sampleBaseUrl()))
    }
    val loginViewModel = remember { LoginViewModel(authRepository) }

    val isLoggedIn by authRepository.isLoggedIn.collectAsState()
    var screen by remember { mutableStateOf<SampleScreen>(SampleScreen.Dashboard) }

    StockAppTheme {
        if (!isLoggedIn) {
            LoginScreen(
                viewModel = loginViewModel,
                onLoginSuccess = { /* isLoggedIn muda e recompõe pro dashboard sozinho */ },
                onNavigateToRegister = { /* sample é só login, de propósito */ }
            )
        } else {
            val dashboardViewModel = remember { DashboardViewModel(portfolioRepository) }
            DashboardScreen(
                viewModel = dashboardViewModel,
            )
        }
    }
}