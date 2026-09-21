package com.danilobarreto.stockapp.portfolio.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.danilobarreto.stockapp.designsystem.components.StockAppAvatar
import com.danilobarreto.stockapp.designsystem.components.StockAppErrorBanner
import com.danilobarreto.stockapp.designsystem.components.StockAppPrimaryButton
import com.danilobarreto.stockapp.designsystem.components.StockAppSegmentedControl
import com.danilobarreto.stockapp.designsystem.icons.StockAppIcons
import com.danilobarreto.stockapp.designsystem.theme.StockAppColors
import com.danilobarreto.stockapp.designsystem.theme.StockAppShapes
import com.danilobarreto.stockapp.designsystem.theme.StockAppTypography
import com.danilobarreto.stockapp.designsystem.util.toDecimalString
import com.danilobarreto.stockapp.portfolio.domain.AssetType
import com.danilobarreto.stockapp.portfolio.domain.PortfolioSummary
import com.danilobarreto.stockapp.portfolio.domain.PositionSummary

// Paleta dedicada à distribuição — não reaproveita textSuccess/textDanger pra não confundir
// "alocação" com "resultado" (ganho/perda) em outras partes da tela.
private val allocationPalette = listOf(
    StockAppColors.primary,
    StockAppColors.primaryDeep,
    StockAppColors.textWarning,
    StockAppColors.textMuted,
)

private enum class PositionFilter { ALL, STOCK, FII }

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onAddOrder: () -> Unit,
    onImport: () -> Unit,
    onViewValuation: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    var balanceVisible by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) { viewModel.load() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(StockAppColors.surface1)
            .verticalScroll(rememberScrollState())
    ) {
        DashboardHeader(
            uiState = uiState,
            balanceVisible = balanceVisible,
            onToggleBalance = { balanceVisible = !balanceVisible },
        )

        when (val state = uiState) {
            is DashboardUiState.Loading -> {
                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is DashboardUiState.Error -> {
                StockAppErrorBanner(state.message, modifier = Modifier.padding(16.dp))
            }
            is DashboardUiState.Success -> {
                DashboardContent(
                    summary = state.summary,
                    balanceVisible = balanceVisible,
                    onAddOrder = onAddOrder,
                    onImport = onImport,
                    onViewValuation = onViewValuation,
                )
            }
        }
    }
}

@Composable
private fun DashboardHeader(
    uiState: DashboardUiState,
    balanceVisible: Boolean,
    onToggleBalance: () -> Unit,
) {
    val summary = (uiState as? DashboardUiState.Success)?.summary

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(StockAppColors.primary, shape = StockAppShapes.headerBottomRadius)
            .safeContentPadding()
            .padding(horizontal = 20.dp, vertical = 14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                "Carteira",
                style = StockAppTypography.headerTitle,
                color = StockAppColors.onPrimary,
            )
            HeaderIconButton(
                icon = if (balanceVisible) StockAppIcons.Eye else StockAppIcons.EyeOff,
                contentDescription = if (balanceVisible) "Ocultar saldo" else "Mostrar saldo",
                onClick = onToggleBalance,
            )
        }

        Text(
            "Valor total",
            style = StockAppTypography.bodySmall,
            color = StockAppColors.onPrimary.copy(alpha = 0.8f),
            modifier = Modifier.padding(top = 16.dp),
        )
        Text(
            if (!balanceVisible) "R$ ••••••" else summary?.let { "R$ ${it.totalValue.toDecimalString()}" } ?: "R$ —",
            style = StockAppTypography.displayMedium,
            color = StockAppColors.onPrimary,
            modifier = Modifier.padding(top = 2.dp),
        )
        summary?.profitPercent?.let { percent ->
            if (!balanceVisible) return@let
            val sign = if (percent >= 0) "+" else ""
            Text(
                "$sign${percent.toDecimalString()}% · R$ ${summary.profitValue.toDecimalString()} desde a compra",
                style = StockAppTypography.bodySmall,
                color = StockAppColors.onPrimary.copy(alpha = 0.9f),
                modifier = Modifier.padding(top = 6.dp),
            )
        }
    }
}

@Composable
private fun HeaderIconButton(icon: ImageVector, contentDescription: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(StockAppColors.onPrimary.copy(alpha = 0.14f))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, contentDescription = contentDescription, tint = StockAppColors.onPrimary, modifier = Modifier.size(20.dp))
    }
}

@Composable
private fun DashboardContent(
    summary: PortfolioSummary,
    balanceVisible: Boolean,
    onAddOrder: () -> Unit,
    onImport: () -> Unit,
    onViewValuation: () -> Unit,
) {
    var filter by remember { mutableStateOf(PositionFilter.ALL) }

    Column(modifier = Modifier.padding(16.dp)) {
        // Não está no protótipo (que assume esses atalhos só na Home), mas as telas de
        // Importar/Valuation continuam alcançáveis daqui — viraram pills discretas em vez
        // dos TextButton flutuantes de antes.
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            ActionPill(icon = StockAppIcons.Upload, label = "Importar", onClick = onImport, modifier = Modifier.weight(1f))
            ActionPill(icon = StockAppIcons.Target, label = "Valuation", onClick = onViewValuation, modifier = Modifier.weight(1f))
        }

        StockAppSegmentedControl(
            options = listOf("Tudo", "Ações", "FIIs"),
            selectedIndex = filter.ordinal,
            onOptionSelected = { filter = PositionFilter.entries[it] },
        )

        val positions = summary.positions.filter {
            when (filter) {
                PositionFilter.ALL -> true
                PositionFilter.STOCK -> it.assetType == AssetType.STOCK
                PositionFilter.FII -> it.assetType == AssetType.FII
            }
        }

        if (positions.isEmpty()) {
            EmptyPositionsCard(onAddOrder = onAddOrder, modifier = Modifier.padding(top = 20.dp))
            return@Column
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp)
                .background(StockAppColors.surface2, shape = StockAppShapes.cardRadiusLarge)
                .padding(16.dp)
        ) {
            Text("Distribuição", style = StockAppTypography.titleMedium, color = StockAppColors.textPrimary)
            AllocationBar(positions, modifier = Modifier.padding(top = 14.dp))
            AllocationLegend(positions, modifier = Modifier.padding(top = 12.dp))
        }

        Text(
            "Posições",
            style = StockAppTypography.titleMedium,
            color = StockAppColors.textPrimary,
            modifier = Modifier.padding(top = 24.dp, bottom = 10.dp),
        )
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            positions.forEachIndexed { index, position ->
                PositionCard(position, allocationPalette[index % allocationPalette.size], balanceVisible)
            }
        }
    }
}

@Composable
private fun ActionPill(icon: ImageVector, label: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .background(StockAppColors.surface2, shape = StockAppShapes.controlRadius)
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, contentDescription = null, tint = StockAppColors.primary, modifier = Modifier.size(16.dp))
        Text(
            label,
            style = StockAppTypography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
            color = StockAppColors.textPrimary,
            modifier = Modifier.padding(start = 6.dp),
        )
    }
}

@Composable
private fun EmptyPositionsCard(onAddOrder: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(StockAppColors.surface2, shape = StockAppShapes.cardRadiusLarge)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            "Nenhuma posição por aqui ainda",
            style = StockAppTypography.titleMedium,
            color = StockAppColors.textPrimary,
        )
        Text(
            "Registre sua primeira ordem para começar a acompanhar sua carteira.",
            style = StockAppTypography.bodySmall,
            color = StockAppColors.textMuted,
            modifier = Modifier.padding(top = 6.dp, bottom = 18.dp),
        )
        StockAppPrimaryButton(text = "Nova ordem", onClick = onAddOrder, modifier = Modifier.fillMaxWidth())
    }
}

@Composable
private fun AllocationBar(positions: List<PositionSummary>, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(10.dp)
            .background(StockAppColors.border, shape = RoundedCornerShape(5.dp)),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        positions.forEachIndexed { index, position ->
            val weight = ((position.allocationPercent ?: 0.0) / 100.0).toFloat().coerceAtLeast(0f)
            if (weight > 0f) {
                Box(
                    modifier = Modifier
                        .weight(weight)
                        .fillMaxSize()
                        .background(allocationPalette[index % allocationPalette.size], shape = RoundedCornerShape(5.dp))
                )
            }
        }
    }
}

@Composable
private fun AllocationLegend(positions: List<PositionSummary>, modifier: Modifier = Modifier) {
    // Chips (não linhas cheias) para bater com o protótipo — quebram linha sozinhas
    // porque Row não tem flow nativo no Compose atual; como a lista costuma ser curta,
    // deixamos rolar horizontalmente em vez de importar uma dependência de flow-layout.
    Row(
        modifier = modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        positions.forEachIndexed { index, position ->
            Row(
                modifier = Modifier
                    .background(StockAppColors.bg, shape = RoundedCornerShape(100))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(allocationPalette[index % allocationPalette.size], shape = CircleShape)
                )
                Text(position.ticker, style = StockAppTypography.labelSmall, color = StockAppColors.textPrimary)
                Text(
                    position.allocationPercent?.let { "${it.toDecimalString()}%" } ?: "—",
                    style = StockAppTypography.labelSmall,
                    color = StockAppColors.textMuted,
                )
            }
        }
    }
}

@Composable
private fun PositionCard(position: PositionSummary, fallbackColor: Color, balanceVisible: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(StockAppColors.surface2, shape = StockAppShapes.cardRadius)
            .padding(14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
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

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 12.dp),
            color = StockAppColors.divider,
        )

        Row(modifier = Modifier.fillMaxWidth()) {
            PositionStat(modifier = Modifier.weight(1f), label = "Qtde", value = "${position.quantity}")
            PositionStat(modifier = Modifier.weight(1f), label = "Preço médio", value = "R$ ${position.avgPrice.toDecimalString()}")
            PositionStat(
                modifier = Modifier.weight(1f),
                label = "Posição",
                value = if (!balanceVisible) "••••••" else position.currentValue?.let { "R$ ${it.toDecimalString()}" } ?: "—",
                alignEnd = true,
            )
        }
    }
}

@Composable
private fun PositionStat(label: String, value: String, modifier: Modifier = Modifier, alignEnd: Boolean = false) {
    Column(
        modifier = modifier,
        horizontalAlignment = if (alignEnd) Alignment.End else Alignment.Start,
    ) {
        Text(label, style = StockAppTypography.labelSmall, color = StockAppColors.textMuted)
        Text(
            value,
            style = StockAppTypography.bodySmall,
            color = StockAppColors.textPrimary,
            modifier = Modifier.padding(top = 2.dp),
        )
    }
}