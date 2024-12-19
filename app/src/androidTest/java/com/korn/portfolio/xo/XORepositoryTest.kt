package com.korn.portfolio.xo

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.korn.portfolio.xo.repo.Game
import com.korn.portfolio.xo.repo.XODatabase
import com.korn.portfolio.xo.repo.XORepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class XORepositoryTest {
    private lateinit var db: XODatabase
    private lateinit var repo: XORepository

    private val moves: List<List<List<Boolean?>>> = listOf(
        // 1x1
        List(1) { List(1) { null } },
        List(1) { List(1) { true } },
        List(1) { List(1) { false } },

        // 2x2
        List(2) { List(2) { null } },
        listOf(
            listOf(null, true),
            listOf(null, null)
        ),
        listOf(
            listOf(false, true),
            listOf(null, null)
        ),
        listOf(
            listOf(false, true),
            listOf(null, true)
        ),

        // 3x3
        List(3) { List(3) { null } },
        listOf(
            listOf(true, null, null),
            listOf(null, false, null),
            listOf(false, true, true)
        ),

        // 4x4
        List(4) { List(4) { null } },
        listOf(
            listOf(true, null, null, false),
            listOf(null, false, null, true),
            listOf(false, true, true, null),
            listOf(false, true, true, null)
        )
    )

    private val games = listOf(
        Game(
            boardSize = 1,
            winCondition = 1,
            playerX = "p1",
            playerO = "p2",
            moves = moves.subList(0, 3)
        ),
        Game(
            boardSize = 2,
            winCondition = 2,
            playerX = "p1",
            playerO = "p2",
            moves = moves.subList(3, 7)
        ),
        Game(
            boardSize = 3,
            winCondition = 3,
            playerX = "p1",
            playerO = "p2",
            moves = moves.subList(7, 9)
        ),
        Game(
            boardSize = 4,
            winCondition = 4,
            playerX = "p1",
            playerO = "p2",
            moves = moves.subList(9, 11)
        ),
    )

    @Before
    fun create_db() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, XODatabase::class.java).build()
        repo = XORepository(db.gameDao())
        repo.insertGame(*games.toTypedArray())
    }

    @Test
    fun get_all_games() = runBlocking {
        assertEquals(games, repo.games.first())
    }

    @Test
    fun insert_and_delete_game() = runBlocking {
        val newId = UUID.randomUUID()

        repo.insertGame(games[2].copy(id = newId))
        assert(repo.games.first().any { it.id == newId })

        repo.deleteGame(games[2].copy(id = newId))
        assert(repo.games.first().none { it.id == newId })
    }
}