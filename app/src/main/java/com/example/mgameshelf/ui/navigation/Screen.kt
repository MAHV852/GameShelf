package com.example.mgameshelf.ui.navigation

/**
 * Rutas de navegación de la app, como constantes en vez de Strings sueltos
 * regados por el código (evita errores de tipeo al navegar).
 */
sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object GameList : Screen("game_list")
}
