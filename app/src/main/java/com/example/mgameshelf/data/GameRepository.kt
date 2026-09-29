package com.example.mgameshelf.data

import kotlinx.coroutines.flow.Flow

/**
 * Repositorio: única capa que conoce el DAO.
 * El ViewModel pide juegos filtrados por usuario.
 */
class GameRepository(private val gameDao: GameDao) {

    /** Flujo reactivo con los juegos del usuario indicado. */
    fun getGames(usuarioId: Int): Flow<List<Game>> = gameDao.getGamesByUsuario(usuarioId)

    suspend fun insertGame(game: Game) = gameDao.insertGame(game)

    suspend fun updateGame(game: Game) = gameDao.updateGame(game)

    suspend fun deleteGame(game: Game) = gameDao.deleteGame(game)
}