package com.example.mgameshelf.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * DAO para la entidad Game.
 * Las escrituras son suspend; la lectura es un Flow reactivo.
 */
@Dao
interface GameDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGame(game: Game): Long

    @Update
    suspend fun updateGame(game: Game)

    @Delete
    suspend fun deleteGame(game: Game)

    /**
     * Emite los juegos de un usuario específico, ordenados por id DESC.
     * Se re-emite automáticamente cuando la tabla cambia.
     */
    @Query("SELECT * FROM games WHERE usuarioId = :usuarioId ORDER BY id DESC")
    fun getGamesByUsuario(usuarioId: Int): Flow<List<Game>>
}