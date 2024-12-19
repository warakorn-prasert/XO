package com.korn.portfolio.xo

import com.korn.portfolio.xo.repo.Game
import org.junit.Assert.assertEquals
import org.junit.Test

class GameTest {
    private val game = Game(
        boardSize = 5,
        winCondition = 5,
        playerX = "p1",
        playerO = "p2",
        moves = listOf(
            List(5) { List(5) { null } }
        )
    )

    @Test(expected = IllegalArgumentException::class)
    fun `create game with invalid boardSize`() {
        val game = game.copy(boardSize = 0)
        assert(false)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `create game with invalid winCondition`() {
        val game = game.copy(winCondition = game.boardSize + 1)
        assert(false)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `create game with invalid moves`() {
        val game = game.copy(boardSize = 3)
        assert(false)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `add invalid player name`() {
        game.addMove(player = "p3", x = 0, y = 0)
        assert(false)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `add invalid move`() {
        game.addMove(player = "p1", x = 5, y = 5)
        assert(false)
    }

    @Test
    fun `no winner`() {
        assertEquals(null, game.winner)
    }

    @Test
    fun `correct horizontal winner`() {
        val game1 = game.copy(winCondition = 3, moves = listOf(
            List(5) { List(5) { null } },
            listOf(
                listOf(true, true, true, null, null),
                listOf(null, null, null, null, null),
                listOf(null, null, null, null, null),
                listOf(null, null, null, null, null),
                listOf(null, null, null, null, null)
            )
        ))
        assertEquals("p1", game1.winner)

        val game2 = game.copy(winCondition = 3, moves = listOf(
            List(5) { List(5) { null } },
            listOf(
                listOf(null, null, null, null, null),
                listOf(null, false, false, false, null),
                listOf(null, null, null, null, null),
                listOf(null, null, null, null, null),
                listOf(null, null, null, null, null)
            )
        ))
        assertEquals("p2", game2.winner)

        val game3 = game.copy(winCondition = 3, moves = listOf(
            List(5) { List(5) { null } },
            listOf(
                listOf(null, null, null, null, null),
                listOf(null, null, null, null, null),
                listOf(null, null, true, true, true),
                listOf(null, null, null, null, null),
                listOf(null, null, null, null, null)
            )
        ))
        assertEquals("p1", game3.winner)
    }

    @Test
    fun `correct vertical winner`() {
        val game1 = game.copy(winCondition = 3, moves = listOf(
            List(5) { List(5) { null } },
            listOf(
                listOf(null, null, null, null, null),
                listOf(null, null, null, null, null),
                listOf(false, null, null, null, null),
                listOf(false, null, null, null, null),
                listOf(false, null, null, null, null)
            )
        ))
        assertEquals("p2", game1.winner)

        val game2 = game.copy(winCondition = 3, moves = listOf(
            List(5) { List(5) { null } },
            listOf(
                listOf(null, null, null, null, null),
                listOf(null, true, null, null, null),
                listOf(null, true, null, null, null),
                listOf(null, true, null, null, null),
                listOf(null, null, null, null, null)
            )
        ))
        assertEquals("p1", game2.winner)

        val game3 = game.copy(winCondition = 3, moves = listOf(
            List(5) { List(5) { null } },
            listOf(
                listOf(null, null, false, null, null),
                listOf(null, null, false, null, null),
                listOf(null, null, false, null, null),
                listOf(null, null, null, null, null),
                listOf(null, null, null, null, null)
            )
        ))
        assertEquals("p2", game3.winner)
    }

    @Test
    fun `correct diagonal winner`() {
        val game1 = game.copy(winCondition = 3, moves = listOf(
            List(5) { List(5) { null } },
            listOf(
                listOf(true, null, null, null, null),
                listOf(null, true, null, null, null),
                listOf(null, null, true, null, null),
                listOf(null, null, null, null, null),
                listOf(null, null, null, null, null)
            )
        ))
        assertEquals("p1", game1.winner)

        val game2 = game.copy(winCondition = 3, moves = listOf(
            List(5) { List(5) { null } },
            listOf(
                listOf(null, null, null, null, null),
                listOf(null, null, null, null, null),
                listOf(null, null, null, false, null),
                listOf(null, null, false, null, null),
                listOf(null, false, null, null, null)
            )
        ))
        assertEquals("p2", game2.winner)

        val game3 = game.copy(winCondition = 3, moves = listOf(
            List(5) { List(5) { null } },
            listOf(
                listOf(null, null, null, null, false),
                listOf(null, null, null, false, null),
                listOf(null, null, false, null, null),
                listOf(null, null, null, null, null),
                listOf(null, null, null, null, null)
            )
        ))
        assertEquals("p2", game3.winner)
    }
}