package com.example.mgameshelf.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Estados posibles de un juego.
 */
object GameStatus {
    const val PLAYING = "Jugando"
    const val COMPLETED = "Completado"
    const val ON_HOLD = "En Pausa"
    const val DROPPED = "Abandonado"
    const val BACKLOG = "Pendiente"

    val ALL = listOf(PLAYING, COMPLETED, ON_HOLD, DROPPED, BACKLOG)
}

/**
 * Entidad que representa la tabla "games".
 *
 * @property imagenUri Ruta local (filesDir) de la portada elegida por el usuario.
 *                     null si no tiene portada (se muestra placeholder).
 * @property usuarioId ID del usuario dueño del juego (para separar bibliotecas).
 */
@Entity(tableName = "games")
data class Game(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val titulo: String,
    val plataforma: String,
    val estado: String = GameStatus.BACKLOG,
    val calificacion: Int = 0,
    val resena: String = "",
    val imagenUri: String? = null,
    val usuarioId: Int = 0
)