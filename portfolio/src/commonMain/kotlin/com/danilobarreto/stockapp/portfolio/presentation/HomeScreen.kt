package com.danilobarreto.stockapp.portfolio.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.danilobarreto.stockapp.designsystem.components.StockAppAreaLineChart
import com.danilobarreto.stockapp.designsystem.components.StockAppAvatar
import com.danilobarreto.stockapp.designsystem.components.StockAppErrorBanner
import com.danilobarreto.stockapp.designsystem.theme.StockAppColors
import com.danilobarreto.stockapp.designsystem.theme.StockAppShapes
import com.danilobarreto.stockapp.designsystem.theme.StockAppTypography
import com.danilobarreto.stockapp.designsystem.util.toDecimalString
import com.danilobarreto.stockapp.portfolio.domain.PortfolioHistoryPoint
import com.danilobarreto.stockapp.portfolio.domain.PositionSummary
import com.danilobarreto.stockapp.designsystem.icons.StockAppIcons

@Composable
fun HomeScreen(
    userName: String,
    viewModel: HomeViewModel,
    onNovaOrdem: () -> Unit,
    onImportarB3: () -> Unit,
    onValuation: () -> Unit,
    onCotacoes: () -> Unit,
    onVerCarteira: () -> Unit,
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
        HomeHeader(
            userName = userName,
            uiState = uiState,
            balanceVisible = balanceVisible,
            onToggleBalance = { balanceVisible = !balanceVisible },
        )

        when (val state = uiState) {
            is HomeUiState.Loading -> {
                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is HomeUiState.Error -> {
                StockAppErrorBanner(state.message, modifier = Modifier.padding(16.dp))
            }
            is HomeUiState.Success -> {
                HomeContent(
                    state = state,
                    onNovaOrdem = onNovaOrdem,
                    onImportarB3 = onImportarB3,
                    onValuation = onValuation,
                    onCotacoes = onCotacoes,
                    onVerCarteira = onVerCarteira,
                )
            }
        }
    }
}

@Composable
private fun HomeHeader(
    userName: String,
    uiState: HomeUiState,
    balanceVisible: Boolean,
    onToggleBalance: () -> Unit,
) {
    val summary = (uiState as? HomeUiState.Success)?.summary
    val dividends = (uiState as? HomeUiState.Success)?.dividends

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
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StockAppAvatar(
                    imageUrl = null,
                    fallbackText = userName.initials(),
                    fallbackBackgroundColor = StockAppColors.onPrimary.copy(alpha = 0.18f),
                    fallbackTextColor = StockAppColors.onPrimary,
                    size = 40.dp,
                )
                Column {
                    Text("Olá,", style = StockAppTypography.bodySmall, color = StockAppColors.onPrimary.copy(alpha = 0.8f))
                    Text(
                        userName.ifBlank { "Investidor" },
                        style = StockAppTypography.titleMedium,
                        color = StockAppColors.onPrimary,
                    )
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                HeaderIconButton(
                    icon = if (balanceVisible) StockAppIcons.Eye else StockAppIcons.EyeOff,
                    contentDescription = if (balanceVisible) "Ocultar saldo" else "Mostrar saldo",
                    onClick = onToggleBalance,
                )
                HeaderIconButton(
                    icon = StockAppIcons.Bell,
                    contentDescription = "Notificações",
                    onClick = { /* Fase 14 — alertas ainda não existe */ },
                )
            }
        }

        Text(
            "Patrimônio total",
            style = StockAppTypography.bodySmall,
            color = StockAppColors.onPrimary.copy(alpha = 0.8f),
            modifier = Modifier.padding(top = 14.dp),
        )
        Text(
            if (!balanceVisible) "R$ ••••••" else summary?.let { "R$ ${it.totalValue.toDecimalString()}" } ?: "R$ —",
            style = StockAppTypography.displayXLarge,
            color = StockAppColors.onPrimary,
            modifier = Modifier.padding(top = 2.dp),
        )
        summary?.profitPercent?.let { percent ->
            val sign = if (percent >= 0) "+" else ""
            Text(
                "$sign${percent.toDecimalString()}% no total",
                style = StockAppTypography.bodySmall,
                color = StockAppColors.onPrimary.copy(alpha = 0.9f),
                modifier = Modifier.padding(top = 6.dp),
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            TranslucentMetricCard(
                modifier = Modifier.weight(1f),
                label = "Proventos do mês",
                value = dividends?.let { "R$ ${it.totalValue.toDecimalString()}" } ?: "—",
            )
            TranslucentMetricCard(
                modifier = Modifier.weight(1f),
                label = "Investido",
                value = summary?.let { "R$ ${it.investedValue.toDecimalString()}" } ?: "—",
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
private fun TranslucentMetricCard(modifier: Modifier = Modifier, label: String, value: String) {
    Column(
        modifier = modifier
            .background(StockAppColors.onPrimary.copy(alpha = 0.14f), shape = RoundedCornerShape(18.dp))
            .padding(12.dp)
    ) {
        Text(label, style = StockAppTypography.labelSmall, color = StockAppColors.onPrimary.copy(alpha = 0.85f))
        Text(value, style = StockAppTypography.bodyMedium, color = StockAppColors.onPrimary, modifier = Modifier.padding(top = 4.dp))
    }
}

@Composable
private fun HomeContent(
    state: HomeUiState.Success,
    onNovaOrdem: () -> Unit,
    onImportarB3: () -> Unit,
    onValuation: () -> Unit,
    onCotacoes: () -> Unit,
    onVerCarteira: () -> Unit,
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            ShortcutCard(icon = StockAppIcons.Plus, label = "Nova ordem", onClick = onNovaOrdem)
            ShortcutCard(icon = StockAppIcons.Upload, label = "Importar B3", onClick = onImportarB3)
            ShortcutCard(icon = StockAppIcons.Target, label = "Valuation", onClick = onValuation)
            ShortcutCard(icon = StockAppIcons.ChartCandle, label = "Cotações", onClick = onCotacoes)
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp)
                .background(StockAppColors.surface2, shape = StockAppShapes.cardRadiusLarge)
                .padding(16.dp)
        ) {
            Text("Evolução", style = StockAppTypography.titleMedium, color = StockAppColors.textPrimary)
            Text(
                "Últimos ${state.history.size} meses",
                style = StockAppTypography.labelSmall,
                color = StockAppColors.textMuted,
                modifier = Modifier.padding(top = 2.dp, bottom = 12.dp),
            )
            if (state.history.size < 2) {
                Text(
                    "Ainda não há histórico suficiente.",
                    style = StockAppTypography.bodyMedium,
                    color = StockAppColors.textMuted,
                )
            } else {
                StockAppAreaLineChart(
                    values = state.history.map { it.totalValue.toFloat() },
                    modifier = Modifier.fillMaxWidth(),
                )
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    monthLabels(state.history).forEach { label ->
                        Text(label, style = StockAppTypography.labelTable, color = StockAppColors.textMuted)
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
                .background(StockAppColors.surface2, shape = StockAppShapes.cardRadius)
                .padding(vertical = 8.dp)
        ) {
            Text(
                "Seus ativos",
                style = StockAppTypography.titleMedium,
                color = StockAppColors.textPrimary,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            )
            if (state.summary.positions.isEmpty()) {
                Text(
                    "Nenhuma posição cadastrada ainda.",
                    style = StockAppTypography.bodyMedium,
                    color = StockAppColors.textMuted,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                )
            } else {
                state.summary.positions.take(4).forEach { position ->
                    HomeAssetRow(position)
                }
            }
            TextButton(onClick = onVerCarteira, modifier = Modifier.align(Alignment.End).padding(end = 8.dp)) {
                Text("Ver carteira")
            }
        }

        Column(modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 16.dp)) {
            Text(
                "Alertas",
                style = StockAppTypography.titleMedium,
                color = StockAppColors.textPrimary,
                modifier = Modifier.padding(bottom = 8.dp),
            )
            // Estático de propósito — sem dado real por trás ainda (ver docs/decisoes.md, Fase 14).
            AlertRow(
                icon = StockAppIcons.Bell,
                text = "VALE3 passou de R$ 68,00",
                highlighted = true,
            )
            Spacer(modifier = Modifier.height(8.dp))
            AlertRow(
                icon = StockAppIcons.Coin,
                text = "ITUB4 paga dividendo em 3 dias",
                highlighted = false,
            )
        }
    }
}

@Composable
private fun ShortcutCard(icon: ImageVector, label: String, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .width(92.dp)
            .background(StockAppColors.surface2, shape = RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp, horizontal = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(icon, contentDescription = label, tint = StockAppColors.primary, modifier = Modifier.size(20.dp))
        Text(
            label,
            style = StockAppTypography.labelSmall.copy(fontSize = 12.sp),
            color = StockAppColors.textPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp),
        )
    }
}

@Composable
private fun HomeAssetRow(position: PositionSummary) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        StockAppAvatar(
            imageUrl = position.logoUrl,
            fallbackText = position.ticker,
            fallbackBackgroundColor = StockAppColors.primaryTint,
            fallbackTextColor = StockAppColors.primary,
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(position.ticker, style = StockAppTypography.bodyMedium, color = StockAppColors.textPrimary)
            Text("${position.quantity} un", style = StockAppTypography.labelSmall, color = StockAppColors.textMuted)
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
private fun AlertRow(icon: ImageVector, text: String, highlighted: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (highlighted) StockAppColors.primaryTint else StockAppColors.surface2,
                shape = StockAppShapes.cardRadius,
            )
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = if (highlighted) StockAppColors.primary else StockAppColors.textMuted,
            modifier = Modifier.size(20.dp),
        )
        Text(text, style = StockAppTypography.bodyMedium, color = StockAppColors.textPrimary)
    }
}

private val monthAbbreviations =
    listOf("Jan", "Fev", "Mar", "Abr", "Mai", "Jun", "Jul", "Ago", "Set", "Out", "Nov", "Dez")

private fun monthLabels(history: List<PortfolioHistoryPoint>): List<String> {
    if (history.isEmpty()) return emptyList()
    val indices = if (history.size <= 3) history.indices.toList()
    else listOf(0, history.size / 2, history.size - 1)
    return indices.map { index ->
        val monthNumber = history[index].month.substringAfter("-").toIntOrNull() ?: 1
        monthAbbreviations.getOrElse(monthNumber - 1) { "" }
    }
}

private fun String.initials(): String {
    val parts = trim().split(" ").filter { it.isNotBlank() }
    return when {
        parts.isEmpty() -> "?"
        parts.size == 1 -> parts[0].take(1).uppercase()
        else -> (parts.first().take(1) + parts.last().take(1)).uppercase()
    }
}