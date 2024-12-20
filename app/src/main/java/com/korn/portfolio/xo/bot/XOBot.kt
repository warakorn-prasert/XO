package com.korn.portfolio.xo.bot

import com.korn.portfolio.xo.repo.Game
import com.korn.portfolio.xo.repo.winner

/**
 * Uses minimax algorithm
 * [From GeeksforGeeks.](https://www.geeksforgeeks.org/finding-optimal-move-in-tic-tac-toe-using-minimax-algorithm-in-game-theory/)
 * - Note: Although the definition is correct,
 *         GeeksforGeeks' minimax code does not consider depth, making it fail in some cases.
 * - Example of fail case:
 * ```
 *  [ o, o, _ ] With x playing next, their minimax chooses (0, 2)
 *  [ x, x, _ ] because it's the first one that gets the highest points.
 *  [ _, _, _ ] This is fixed by adding depth into the decision.
 * ```
 */

fun Game.botMove(botName: String): Game {
    require(botName == playerX || botName == playerO) {
        "Bot name must match playerX or playerY."
    }

    val board: MutableList<MutableList<Boolean?>> = mutableListOf()
    moves.last().forEach {
        board.add(it.toMutableList())
    }
    var bestVal = Int.MIN_VALUE
    var bestY = -1
    var bestX = -1
    var bestDepth = Int.MAX_VALUE
    repeat(boardSize) { y ->
        repeat(boardSize) { x ->
            if (board[y][x] == null) {
                board[y][x] = botName == playerX
                val (moveVal, depth) = minimax(
                    board = board,
                    depth = 0,
                    isMax = false,
                    player = botName
                )
                board[y][x] = null
                if (moveVal > bestVal || (moveVal == bestVal && depth < bestDepth)) {
                    bestY = y
                    bestX = x
                    bestVal = moveVal
                    bestDepth = depth
                }
            }
        }
    }

    return addMove(botName, bestX, bestY)
}

private fun Game.minimax(
    board: MutableList<MutableList<Boolean?>>,
    depth: Int,
    isMax: Boolean,
    player: String
): Pair<Int, Int> {
    val score = when (winner(board, winCondition)) {
        true -> if (player == playerX) 10 else -10
        false -> if (player == playerO) 10 else -10
        null -> 0
    }
    if (score != 0) return score to depth
    if (board.all { y -> y.none { xy -> xy == null } }) return 0 to depth

    return if (isMax) {
        var best = Int.MIN_VALUE
        var bestDepth = Int.MAX_VALUE
        repeat(boardSize) { y ->
            repeat(boardSize) { x ->
                if (board[y][x] == null) {
                    board[y][x] = player == playerX
                    minimax(board, depth + 1, false, player).let {
                        best = best.coerceAtLeast(it.first)
                        bestDepth = it.second
                    }
                    board[y][x] = null
                }
            }
        }
        best to bestDepth
    } else {
        var best = Int.MAX_VALUE
        var bestDepth = Int.MAX_VALUE
        repeat(boardSize) { y ->
            repeat(boardSize) { x ->
                if (board[y][x] == null) {
                    board[y][x] = player != playerX
                    minimax(board, depth + 1, true, player).let {
                        best = best.coerceAtMost(it.first)
                        bestDepth = it.second
                    }
                    board[y][x] = null
                }
            }
        }
        best to bestDepth
    }
}