package com.danilobarreto.stockapp.portfolio.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.danilobarreto.stockapp.designsystem.components.StockAppErrorBanner
import com.danilobarreto.stockapp.designsystem.components.StockAppPrimaryButton
import com.danilobarreto.stockapp.designsystem.components.StockAppTextField
import com.danilobarreto.stockapp.designsystem.theme.StockAppColors
import com.danilobarreto.stockapp.designsystem.theme.StockAppTypography
import com.danilobarreto.stockapp.portfolio.domain.AssetType

@Composable
fun AddPositionScreen(
    viewModel: AddPositionViewModel,
    onBack: () -> Unit,
    onSaved: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    var ticker by remember { mutableStateOf("") }
    var quantityText by remember { mutableStateOf("") }
    var avgPriceText by remember { mutableStateOf("") }

    LaunchedEffect(uiState) {
        if (uiState is AddPositionUiState.Success) onSaved()
    }

    val quantity = quantityText.toIntOrNull()
    val avgPrice = avgPriceText.replace(",", ".").toDoubleOrNull()
    val canSave = ticker.isNotBlank() && (quantity ?: 0) > 0 && (avgPrice ?: 0.0) > 0.0
    var selectedAssetType by remember { mutableStateOf(AssetType.STOCK) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(StockAppColors.surface1)
            .safeContentPadding()
            .verticalScroll(rememberScrollState())
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(StockAppColors.surface2)
                .clickable { onBack() },
            contentAlignment = Alignment.Center,
        ) {
            Text("←", style = StockAppTypography.bodyMedium, color = StockAppColors.textPrimary)
        }

        Column {
            Text("Nova posição", style = StockAppTypography.titleLarge, color = StockAppColors.textPrimary)
            Text(
                "Adicione uma ação ou FII que você já tem na carteira",
                style = StockAppTypography.bodyMedium,
                color = StockAppColors.textSecondary,
                modifier = Modifier.padding(top = 6.dp),
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(StockAppColors.border, RoundedCornerShape(10.dp))
                .padding(3.dp)
        ) {
            listOf(AssetType.STOCK to "Ação", AssetType.FII to "FII").forEach { (type, label) ->
                Text(
                    label,
                    style = StockAppTypography.bodyMedium,
                    color = if (selectedAssetType == type) StockAppColors.textPrimary else StockAppColors.textSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { selectedAssetType = type }
                        .background(if (selectedAssetType == type) StockAppColors.surface2 else StockAppColors.border, RoundedCornerShape(8.dp))
                        .padding(vertical = 8.dp)
                )
            }
        }

        StockAppTextField(
            label = "Ticker",
            value = ticker,
            onValueChange = { ticker = it.uppercase() },
            placeholder = "PETR4",
        )

        StockAppTextField(
            label = "Quantidade",
            value = quantityText,
            onValueChange = { input -> quantityText = input.filter { it.isDigit() } },
            placeholder = "100",
            keyboardType = KeyboardType.Number,
        )

        StockAppTextField(
            label = "Preço médio (R$)",
            value = avgPriceText,
            onValueChange = { avgPriceText = it },
            placeholder = "32,40",
            keyboardType = KeyboardType.Decimal,
        )

        Text(
            "O preço atual é buscado automaticamente pelo ticker. Você só informa quantidade e quanto pagou em média.",
            style = StockAppTypography.labelMedium,
            color = StockAppColors.textAccent,
            modifier = Modifier
                .background(StockAppColors.bgAccent, shape = MaterialTheme.shapes.medium)
                .padding(12.dp)
        )

        if (uiState is AddPositionUiState.Error) {
            StockAppErrorBanner((uiState as AddPositionUiState.Error).message)
        }

        StockAppPrimaryButton(
            text = "Salvar posição",
            onClick = { viewModel.save(ticker, selectedAssetType, quantity ?: 0, avgPrice ?: 0.0) },
            loading = uiState is AddPositionUiState.Loading,
            enabled = canSave,
        )
    }
}