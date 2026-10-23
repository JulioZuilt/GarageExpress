package com.example.garageexpress.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val EsquemaOscuro = darkColorScheme(
    primary = CyanPrimario,
    onPrimary = AzulFondo,
    secondary = AmarilloAcento,
    onSecondary = AzulFondo,
    tertiary = CyanClaro,
    background = AzulFondo,
    onBackground = GrisTexto,
    surface = AzulSuperficie,
    onSurface = GrisTexto,
    surfaceVariant = AzulSuperficie2,
    onSurfaceVariant = GrisTenue,
    outline = AzulBorde,
    error = NaranjaStockBajo,
    onError = AzulFondo
)

@Composable
fun GarageExpressTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = EsquemaOscuro,
        typography = Typography,
        content = content
    )
}
