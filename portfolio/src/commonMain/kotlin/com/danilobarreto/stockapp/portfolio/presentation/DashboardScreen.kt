package com.danilobarreto.stockapp.portfolio.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
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
import androidx.compose.ui.unit.sp
import com.danilobarreto.stockapp.designsystem.components.StockAppAvatar
import com.danilobarreto.stockapp.designsystem.components.StockAppErrorBanner
import com.danilobarreto.stockapp.designsystem.components.StockAppPrimaryButton
import com.danilobarreto.stockapp.designsystem.components.StockAppSegmentedControl
import com.danilobarreto.stockapp.designsystem.icons.StockAppIcons
import com.danilobarreto.stockapp.designsystem.theme.StockAppColors
import com.danilobarreto.stockapp.designsystem.theme.StockAppShapes
import com.danilobarreto.stockapp.designsystem.theme.StockAppTypography
import com.danilobarreto.stockapp.designsystem.util.toBrNumber
import com.danilobarreto.stockapp.designsystem.util.toBrPercent
import com.danilobarreto.stockapp.designsystem.util.toBrl
import com.danilobarreto.stockapp.designsystem.util.toDecimalString
import com.danilobarreto.stockapp.portfolio.domain.AssetType
import com.danilobarreto.stockapp.portfolio.domain.PortfolioSummary
import com.danilobarreto.stockapp.portfolio.domain.PositionSummary

// Paleta dedicada à distribuição — não reaproveita textSuccess/textDanger pra não confundir
// "alocação" com "resultado" (ganho/perda) em outras partes da tela.
private val allocationPalette = listOf(
    StockAppColors.primary,
    StockAppColors.primaryDeep,
    StockAppColors.accent,
)
private const val MAX_SLICES = 3

private data class AllocationSlice(val label: String, val percent: Double, val color: Color)

private fun buildAllocationSlices(positions: List<PositionSummary>): List<AllocationSlice> {
    val valued = positions
        .mapNotNull { p -> p.currentValue?.takeIf { it > 0 }?.let { p.ticker to it } }
        .sortedByDescending { it.second }
    val total = valued.sumOf { it.second }
    if (total <= 0) return emptyList()

    val top = if (valued.size == MAX_SLICES + 1) valued else valued.take(MAX_SLICES)
    val rest = valued.drop(top.size)

    val slices = top.mapIndexed { i, (ticker, value) ->
        AllocationSlice(ticker, value / total * 100, allocationPalette.getOrElse(i) { StockAppColors.chartOther })
    }
    return if (rest.isEmpty()) slices
    else slices + AllocationSlice("Outros", rest.sumOf { it.second } / total * 100, StockAppColors.chartOther)
}

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
            .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Top))
            .padding(horizontal = 16.dp, vertical = 14.dp)
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
            if (!balanceVisible) "R$ ••••••" else summary?.totalValue?.toBrl() ?: "R$ —",
            style = StockAppTypography.displayMedium,
            color = StockAppColors.onPrimary,
            modifier = Modifier.padding(top = 2.dp),
        )
        summary?.profitPercent?.let { percent ->
            if (!balanceVisible) return@let
            val sign = if (percent >= 0) "+" else ""
            Text(
                "${percent.toBrPercent()} · ${summary.profitValue.toBrl()} desde a compra",
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
            val slices = remember(positions) { buildAllocationSlices(positions) }
            AllocationBar(slices, modifier = Modifier.padding(top = 16.dp))
            AllocationLegend(slices, modifier = Modifier.padding(top = 14.dp))
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 24.dp, bottom = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Posições", style = StockAppTypography.titleMedium, color = StockAppColors.textPrimary)
            Text(
                if (positions.size == 1) "1 posição" else "${positions.size} posições",
                style = StockAppTypography.bodySmall,
                color = StockAppColors.textSecondary,
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            positions.forEachIndexed { index, position ->
                PositionCard(position, balanceVisible)
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
private fun AllocationBar(slices: List<AllocationSlice>, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth().height(10.dp),
        horizontalArrangement = Arrangement.spacedBy(3.dp),
    ) {
        slices.forEach { slice ->
            Box(
                Modifier
                    .weight(slice.percent.toFloat())
                    .fillMaxHeight()
                    .background(slice.color, RoundedCornerShape(5.dp))
            )
        }
    }
}


@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AllocationLegend(slices: List<AllocationSlice>, modifier: Modifier = Modifier) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        slices.forEach { slice ->
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(Modifier.size(10.dp).background(slice.color, RoundedCornerShape(3.dp)))
                Text(
                    "${slice.label} ${slice.percent.toBrNumber(0)}%",
                    style = StockAppTypography.bodyMedium,
                    color = StockAppColors.textPrimary,
                )
            }
        }
    }
}

@Composable
private fun PositionCard(position: PositionSummary, balanceVisible: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(StockAppColors.surface2, shape = StockAppShapes.cardRadius)
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StockAppAvatar(
                imageUrl = position.logoUrl,
                fallbackText = position.ticker.take(4),
                fallbackBackgroundColor = StockAppColors.primaryTint,
                fallbackTextColor = StockAppColors.primaryDeep,
                size = 44.dp,
                textStyle = StockAppTypography.labelMedium.copy(fontWeight = FontWeight.Bold),
            )
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(position.ticker, style = StockAppTypography.titleMedium, color = StockAppColors.textPrimary)
                if (position.assetType == AssetType.FII) { /* badge FII como está */ }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    position.currentPrice?.toBrl() ?: "—",
                    style = StockAppTypography.titleMedium,
                    color = StockAppColors.textPrimary,
                )
                position.profitPercent?.let { percent ->
                    Text(
                        percent.toBrPercent(signed = true),
                        style = StockAppTypography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                        color = if (percent >= 0) StockAppColors.textSuccess else StockAppColors.textDanger,
                    )
                }
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 14.dp), color = StockAppColors.divider)

        Row(modifier = Modifier.fillMaxWidth()) {
            PositionStat(Modifier.weight(1f), "Qtde", "${position.quantity}")
            PositionStat(Modifier.weight(1f), "Preço médio", position.avgPrice.toBrl())
            PositionStat(
                Modifier.weight(1f),
                "Posição",
                if (!balanceVisible) "R$ ••••••" else position.currentValue?.toBrl() ?: "—",
            )
        }
    }
}

@Composable
private fun PositionStat(modifier: Modifier, label: String, value: String) {
    Column(modifier = modifier) {
        Text(label, style = StockAppTypography.labelMedium, color = StockAppColors.textMuted)
        Text(
            value,
            style = StockAppTypography.bodyMedium.copy(fontSize = 15.sp),
            color = StockAppColors.textPrimary,
            modifier = Modifier.padding(top = 4.dp),
        )
    }
}