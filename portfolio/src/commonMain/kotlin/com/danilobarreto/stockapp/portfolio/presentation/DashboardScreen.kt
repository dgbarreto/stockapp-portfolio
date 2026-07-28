package com.danilobarreto.stockapp.portfolio.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.danilobarreto.stockapp.designsystem.components.StockAppAvatar
import com.danilobarreto.stockapp.designsystem.components.StockAppErrorBanner
import com.danilobarreto.stockapp.designsystem.theme.StockAppColors
import com.danilobarreto.stockapp.designsystem.theme.StockAppTypography
import com.danilobarreto.stockapp.designsystem.util.toDecimalString
import com.danilobarreto.stockapp.portfolio.domain.AssetType
import com.danilobarreto.stockapp.portfolio.domain.PortfolioSummary
import com.danilobarreto.stockapp.portfolio.domain.PositionSummary

private val allocationPalette = listOf(
    StockAppColors.textSuccess,
    StockAppColors.textAccent,
    StockAppColors.textWarning,
    StockAppColors.textDanger,
)

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onAddPosition: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(StockAppColors.surface1)
            .safeContentPadding()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Carteira", style = StockAppTypography.titleLarge, color = StockAppColors.textPrimary)
            Button(onClick = onAddPosition, shape = MaterialTheme.shapes.medium) {
                Text("+ Nova posição")
            }
        }

        when (val state = uiState) {
            is DashboardUiState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.padding(top = 24.dp))
            }
            is DashboardUiState.Error -> {
                StockAppErrorBanner(state.message, modifier = Modifier.padding(top = 24.dp))
            }
            is DashboardUiState.Success -> {
                DashboardContent(state.summary)
            }
        }
    }
}

@Composable
private fun DashboardContent(summary: PortfolioSummary) {
    Column(modifier = Modifier.padding(top = 14.dp)) {
        Text("Valor total", style = StockAppTypography.labelMedium, color = StockAppColors.textSecondary)
        Text(
            "R$ ${summary.totalValue.toDecimalString()}",
            style = StockAppTypography.titleLarge,
            color = StockAppColors.textPrimary,
            modifier = Modifier.padding(top = 3.dp)
        )
        summary.profitPercent?.let { percent ->
            val color = if (percent >= 0) StockAppColors.textSuccess else StockAppColors.textDanger
            val sign = if (percent >= 0) "+" else ""
            Text(
                "$sign${percent.toDecimalString()}% (R$ ${summary.profitValue.toDecimalString()})",
                style = StockAppTypography.labelMedium,
                color = color,
                modifier = Modifier.padding(top = 3.dp)
            )
        }

        Row(modifier = Modifier.fillMaxWidth().padding(top = 20.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            MetricCard(modifier = Modifier.weight(1f), label = "Investido", value = "R$ ${summary.investedValue.toDecimalString()}")
            MetricCard(
                modifier = Modifier.weight(1f),
                label = "Rentabilidade",
                value = summary.profitPercent?.let { "${it.toDecimalString()}%" } ?: "—",
                valueColor = summary.profitPercent?.let { if (it >= 0) StockAppColors.textSuccess else StockAppColors.textDanger },
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 24.dp, bottom = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text("Ativos", style = StockAppTypography.titleMedium, color = StockAppColors.textPrimary)
            Text(
                "${summary.positions.size} posições",
                style = StockAppTypography.labelMedium,
                color = StockAppColors.textMuted,
            )
        }

        if (summary.positions.isEmpty()) {
            Text(
                "Nenhuma posição cadastrada ainda.",
                style = StockAppTypography.bodyMedium,
                color = StockAppColors.textMuted,
                modifier = Modifier.padding(top = 8.dp)
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(StockAppColors.surface2, shape = RoundedCornerShape(14.dp))
            ) {
                summary.positions.forEachIndexed { index, position ->
                    PositionRow(position, allocationPalette[index % allocationPalette.size])
                }
            }

            Text(
                "Distribuição",
                style = StockAppTypography.titleMedium,
                color = StockAppColors.textPrimary,
                modifier = Modifier.padding(top = 24.dp, bottom = 8.dp)
            )
            AllocationBar(summary.positions)
            Column(modifier = Modifier.padding(top = 4.dp)) {
                summary.positions.forEachIndexed { index, position ->
                    AllocationLegendRow(position, allocationPalette[index % allocationPalette.size])
                }
            }
        }
    }
}

@Composable
private fun MetricCard(label: String, value: String, modifier: Modifier = Modifier, valueColor: Color? = null) {
    Column(
        modifier = modifier
            .background(StockAppColors.surface2, shape = RoundedCornerShape(8.dp))
            .padding(11.dp)
    ) {
        Text(label, style = StockAppTypography.labelSmall, color = StockAppColors.textSecondary)
        Text(
            value,
            style = StockAppTypography.bodyMedium,
            color = valueColor ?: StockAppColors.textPrimary,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
private fun PositionRow(position: PositionSummary, fallbackColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        StockAppAvatar(
            imageUrl = position.logoUrl,
            fallbackText = position.ticker,
            fallbackBackgroundColor = fallbackColor.copy(alpha = 0.12f),
            fallbackTextColor = fallbackColor,
        )
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(position.ticker, style = StockAppTypography.bodyMedium, color = StockAppColors.textPrimary)
                if (position.assetType == AssetType.FII) {
                    Text(
                        "FII",
                        style = StockAppTypography.labelSmall,
                        color = StockAppColors.textAccent,
                        modifier = Modifier
                            .background(StockAppColors.bgAccent, shape = RoundedCornerShape(100))
                            .padding(horizontal = 6.dp, vertical = 1.dp)
                    )
                }
            }
            Text(
                "${position.quantity} un · PM R$ ${position.avgPrice.toDecimalString()}",
                style = StockAppTypography.labelSmall,
                color = StockAppColors.textMuted,
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                position.currentPrice?.let { "R$ ${it.toDecimalString()}" } ?: "—",
                style = StockAppTypography.bodyMedium,
                color = StockAppColors.textPrimary,
            )
            position.profitPercent?.let { percent ->
                val color = if (percent >= 0) StockAppColors.textSuccess else StockAppColors.textDanger
                val sign = if (percent >= 0) "+" else ""
                Text("$sign${percent.toDecimalString()}%", style = StockAppTypography.labelSmall, color = color)
            }
        }
    }
}

@Composable
private fun AllocationBar(positions: List<PositionSummary>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(8.dp)
            .background(StockAppColors.border, shape = RoundedCornerShape(4.dp)),
    ) {
        positions.forEachIndexed { index, position ->
            val weight = (position.allocationPercent ?: 0.0).toFloat().coerceAtLeast(0f)
            if (weight > 0f) {
                Box(
                    modifier = Modifier
                        .weight(weight)
                        .fillMaxSize()
                        .background(allocationPalette[index % allocationPalette.size])
                )
            }
        }
    }
}

@Composable
private fun AllocationLegendRow(position: PositionSummary, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(color, shape = RoundedCornerShape(50))
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(position.ticker, style = StockAppTypography.labelMedium, color = StockAppColors.textPrimary)
        }
        Text(
            position.allocationPercent?.let { "${it.toDecimalString()}%" } ?: "—",
            style = StockAppTypography.labelMedium,
            color = StockAppColors.textSecondary,
        )
    }
}