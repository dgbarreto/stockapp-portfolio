package com.danilobarreto.stockapp.portfolio.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.danilobarreto.stockapp.portfolio.domain.MonthlyDividends
import com.danilobarreto.stockapp.portfolio.domain.PortfolioHistoryPoint
import com.danilobarreto.stockapp.portfolio.domain.PortfolioRepository
import com.danilobarreto.stockapp.portfolio.domain.PortfolioSummary
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Success(
        val summary: PortfolioSummary,
        val dividends: MonthlyDividends,
        val history: List<PortfolioHistoryPoint>,
    ) : HomeUiState
    data class Error(val message: String) : HomeUiState
}

class HomeViewModel(
    private val repository: PortfolioRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    fun load() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            _uiState.value = try {
                coroutineScope {
                    val summaryDeferred = async { repository.getSummary() }
                    val dividendsDeferred = async { repository.getMonthlyDividends() }
                    val historyDeferred = async { repository.getHistory() }
                    HomeUiState.Success(
                        summary = summaryDeferred.await(),
                        dividends = dividendsDeferred.await(),
                        history = historyDeferred.await(),
                    )
                }
            } catch (e: Exception) {
                HomeUiState.Error(e.message ?: "Erro ao carregar a Home")
            }
        }
    }
}