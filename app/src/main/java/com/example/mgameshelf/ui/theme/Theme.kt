package com.example.mgameshelf.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val GameShelfDarkColorScheme = darkColorScheme(
    primary = MoradoPrincipal,
    onPrimary = Blanco,
    primaryContainer = MoradoPrincipal,
    onPrimaryContainer = Blanco,
    secondary = MoradoClaro,
    onSecondary = FondoPrincipal,
    background = FondoPrincipal,
    onBackground = Blanco,
    surface = FondoSecundario,
    onSurface = Blanco,
    surfaceVariant = FondoSecundario,
    onSurfaceVariant = Gris,
    error = Amarillo
)

@Composable
fun GameShelfTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = GameShelfDarkColorScheme,
        content = content
    )
}