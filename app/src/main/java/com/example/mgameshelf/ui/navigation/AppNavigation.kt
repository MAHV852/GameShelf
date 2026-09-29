package com.example.mgameshelf.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mgameshelf.ui.gamelist.GameListScreen
import com.example.mgameshelf.ui.login.LoginScreen

/**
 * Grafo de navegación de la app.
 *
 * Login -> GameList: al loguearse, popUpTo(Login, inclusive = true)
 * saca el Login de la pila, así "Atrás" no regresa a él.
 *
 * GameList -> Login (logout): mismo truco en la otra dirección.
 * popUpTo(GameList, inclusive = true) saca la biblioteca de la pila,
 * así "Atrás" desde el Login no regresa a la biblioteca de un usuario
 * que ya cerró sesión.
 */
@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginExitoso = {
                    navController.navigate(Screen.GameList.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.GameList.route) {
            GameListScreen(
                onCerrarSesion = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.GameList.route) { inclusive = true }
                    }
                }
            )
        }
    }
}