package com.danilobarreto.stockapp.portfolio.sample

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
import com.danilobarreto.stockapp.portfolio.presentation.HomeScreen
import com.danilobarreto.stockapp.portfolio.presentation.HomeViewModel
import kotlinx.coroutines.launch

private sealed interface SampleScreen {
    data object Home : SampleScreen
    data object Dashboard : SampleScreen
}

@Composable
fun SampleApp() {
    val tokenStorage = remember { TokenStorage() }
    val httpClient = remember { createSampleHttpClient(tokenStorage) }
    val coroutineScope = rememberCoroutineScope()

    val authRepository = remember {
        AuthRepositoryImpl(AuthApiClient(httpClient, sampleBaseUrl()), tokenStorage)
    }
    val portfolioRepository = remember {
        PortfolioRepositoryImpl(PositionsApiClient(httpClient, sampleBaseUrl()))
    }
    val loginViewModel = remember { LoginViewModel(authRepository) }

    val isLoggedIn by authRepository.isLoggedIn.collectAsState()
    var screen by remember { mutableStateOf<SampleScreen>(SampleScreen.Home) }
    // No app real esse estado vem do UiPreferences (persistido, stockapp-app); no sample basta
    // um estado local compartilhado entre Início e Carteira.
    var balanceVisible by remember { mutableStateOf(true) }

    StockAppTheme {
        if (!isLoggedIn) {
            LoginScreen(
                viewModel = loginViewModel,
                onLoginSuccess = { /* isLoggedIn muda e recompõe pro dashboard sozinho */ },
                onNavigateToRegister = { /* sample é só login, de propósito */ }
            )
        } else {
            Column {
                Row(modifier = Modifier.fillMaxWidth().safeContentPadding().padding(8.dp)) {
                    TextButton(onClick = { screen = SampleScreen.Home }) { Text("Início") }
                    TextButton(onClick = { screen = SampleScreen.Dashboard }) { Text("Carteira") }
                    // Token antigo persistido de teste anterior pode ficar inválido/expirado
                    // sem que `isLoggedIn` perceba (ele só checa se existe token salvo, não se
                    // ainda é válido) — esse botão limpa o token e força passar pelo login de novo.
                    TextButton(onClick = { coroutineScope.launch { authRepository.logout() } }) { Text("Sair") }
                }
                when (screen) {
                    SampleScreen.Home -> {
                        val homeViewModel = remember { HomeViewModel(portfolioRepository) }
                        HomeScreen(
                            userName = "Investidor",
                            balanceVisible = balanceVisible,
                            onToggleBalance = { balanceVisible = !balanceVisible },
                            viewModel = homeViewModel,
                            onNovaOrdem = {},
                            onImportarB3 = {},
                            onValuation = {},
                            onCotacoes = { screen = SampleScreen.Dashboard },
                            onVerCarteira = { screen = SampleScreen.Dashboard },
                        )
                    }
                    SampleScreen.Dashboard -> {
                        val dashboardViewModel = remember { DashboardViewModel(portfolioRepository) }
                        DashboardScreen(
                            viewModel = dashboardViewModel,
                            balanceVisible = balanceVisible,
                            onToggleBalance = { balanceVisible = !balanceVisible },
                            onAddOrder = {},
                            onImport = {},
                            onViewValuation = {},
                        )
                    }
                }
            }
        }
    }
}
