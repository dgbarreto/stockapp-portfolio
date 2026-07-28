package com.danilobarreto.stockapp.portfolio.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.danilobarreto.stockapp.portfolio.data.parsePositionErrorMessage
import com.danilobarreto.stockapp.portfolio.domain.AssetType
import com.danilobarreto.stockapp.portfolio.domain.PortfolioRepository
import io.ktor.client.plugins.ClientRequestException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface AddPositionUiState {
    data object Idle : AddPositionUiState
    data object Loading : AddPositionUiState
    data object Success : AddPositionUiState
    data class Error(val message: String) : AddPositionUiState
}

class AddPositionViewModel(
    private val repository: PortfolioRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<AddPositionUiState>(AddPositionUiState.Idle)
    val uiState: StateFlow<AddPositionUiState> = _uiState.asStateFlow()

    fun save(ticker: String, assetType: AssetType, quantity: Int, avgPrice: Double) {
        viewModelScope.launch {
            _uiState.value = AddPositionUiState.Loading
            _uiState.value = try {
                repository.createPosition(ticker.uppercase(), assetType, quantity, avgPrice)
                AddPositionUiState.Success
            } catch (e: ClientRequestException) {
                AddPositionUiState.Error(parsePositionErrorMessage(e))
            } catch (e: Exception) {
                AddPositionUiState.Error(e.message ?: "Erro ao salvar a posição")
            }
        }
    }
}