package com.korn.portfolio.xo

import com.korn.portfolio.xo.bot.botMove
import com.korn.portfolio.xo.repo.Game
import org.junit.Assert.assertEquals
import org.junit.Test

class XOBotTest {
    private val game = Game(
        boardSize = 3,
        winCondition = 3,
        playerX = "pX",
        playerO = "pO"
    )

    private val unplayed = game.copy(moves = listOf(listOf(
        listOf(null, null, null),
        listOf(null, null, null),
        listOf(null, null, null)
    )))

    private val unplayed2 = game.copy(boardSize = 5, winCondition = 5, moves = listOf(listOf(
        listOf(null, null, null, null, null),
        listOf(null, null, null, null, null),
        listOf(null, null, null, null, null),
        listOf(null, null, null, null, null),
        listOf(null, null, null, null, null)
    )))

    @Test
    fun `No error with empty board`() {
        unplayed.botMove(unplayed.playerX)
        unplayed.botMove(unplayed.playerO)
        unplayed2.botMove(unplayed2.playerO)
    }

    private val winnable = listOf(
        game.copy(moves = listOf(listOf(
            listOf(false, false, null),
            listOf(true, true, null),
            listOf(null, null, null)
        ))),
        game.copy(moves = listOf(listOf(
            listOf(true, false, false),
            listOf(false, true, false),
            listOf(null, true, null)
        ))),
        game.copy(boardSize = 4, moves = listOf(listOf(
            listOf(true, null, null, null),
            listOf(null, true, null, true),
            listOf(null, null, null, true),
            listOf(null, false, false, null)
        ))),
        game.copy(boardSize = 4, winCondition = 4, moves = listOf(listOf(
            listOf(true, null, null, true),
            listOf(null, true, null, true),
            listOf(null, null, true, true),
            listOf(false, false, false, null)
        )))
    )

    @Test
    fun `win in move`() {
        winnable.forEach {
            assertEquals(
                it.playerX,
                it.botMove(it.playerX).winner
            )
            assertEquals(
                it.playerO,
                it.botMove(it.playerO).winner
            )
        }
    }

    private val losable = game.copy(moves = listOf(listOf(
        listOf(true, false, true),
        listOf(null, false, null),
        listOf(false, true, true)
    )))

    @Test
    fun `prevent player winning when 2 choices left`() {
        assertEquals(
            false,
            losable.botMove(losable.playerO).moves.last()[1][2]
        )
    }
}