package com.korn.portfolio.xo.repo

import kotlinx.coroutines.flow.Flow

class XORepository(private val gameDao: GameDao) {
    val games: Flow<List<Game>> = gameDao.getAll()

    suspend fun insertGame(vararg game: Game) {
        gameDao.insert(*game)
    }

    suspend fun deleteGame(vararg game: Game) {
        gameDao.delete(*game)
    }

    suspend fun deleteAllGames() {
        gameDao.deleteAll()
    }
}