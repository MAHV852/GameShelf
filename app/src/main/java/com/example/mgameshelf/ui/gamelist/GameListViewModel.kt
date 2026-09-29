package com.example.mgameshelf.ui.gamelist

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mgameshelf.data.AppDatabase
import com.example.mgameshelf.data.Game
import com.example.mgameshelf.data.GameRepository
import com.example.mgameshelf.data.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel de la pantalla principal (lista de juegos), con biblioteca
 * separada por usuario, búsqueda en tiempo real y filtro por plataforma.
 *
 * FLUJO DE DATOS:
 * 1) repository.getGames(usuarioId) -> todos los juegos de este usuario.
 * 2) _searchQuery -> texto de la barra de búsqueda.
 * 3) _selectedPlatform -> plataforma elegida en la barra lateral
 *    (null = "todas las plataformas", sin filtrar).
 * Cualquier cambio en cualquiera de los tres recalcula filteredGames
 * automáticamente, en background, sin bloquear la UI.
 */
class GameListViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: GameRepository = GameRepository(
        AppDatabase.getDatabase(application).gameDao()
    )

    private val session = SessionManager(application)
    private val usuarioId: Int = session.obtenerUsuarioId()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // null = sin filtro de plataforma (se muestran todas).
    private val _selectedPlatform = MutableStateFlow<String?>(null)
    val selectedPlatform: StateFlow<String?> = _selectedPlatform.asStateFlow()

    val filteredGames: StateFlow<List<Game>> = combine(
        repository.getGames(usuarioId),
        _searchQuery,
        _selectedPlatform
    ) { juegos, query, plataforma ->
        juegos
            .filter { juego ->
                query.isBlank() || juego.titulo.contains(query, ignoreCase = true)
            }
            .filter { juego ->
                plataforma == null || juego.plataforma == plataforma
            }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    fun onSearchQueryChange(nuevoTexto: String) {
        _searchQuery.value = nuevoTexto
    }

    /** null limpia el filtro (vuelve a mostrar todas las plataformas). */
    fun onPlatformSelected(plataforma: String?) {
        _selectedPlatform.value = plataforma
    }

    fun agregarJuego(game: Game): Boolean {
        if (game.titulo.isBlank()) return false
        viewModelScope.launch {
            repository.insertGame(game.copy(usuarioId = usuarioId))
        }
        return true
    }

    fun actualizarJuego(game: Game): Boolean {
        if (game.titulo.isBlank()) return false
        viewModelScope.launch {
            repository.updateGame(game)
        }
        return true
    }

    fun eliminarJuego(game: Game) {
        viewModelScope.launch {
            repository.deleteGame(game)
        }
    }

    fun cerrarSesion() {
        session.cerrarSesion()
    }
}