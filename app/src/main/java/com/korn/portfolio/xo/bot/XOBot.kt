package com.korn.portfolio.xo.bot

import com.korn.portfolio.xo.repo.Game
import com.korn.portfolio.xo.repo.winner

fun Game.botMove(botName: String): Game {
    require(botName == playerX || botName == playerO) {
        "Bot name must match playerX or playerY."
    }

    val board: MutableList<MutableList<Boolean?>> = mutableListOf()
    moves.last().forEach {
        board.add(it.toMutableList())
    }

    val (bestX, bestY, _) = minimax(
        board = board,
        depth = 0,
        alpha = Int.MIN_VALUE,
        beta = Int.MAX_VALUE,
        isMax = true,
        player = botName
    )

    return addMove(botName, bestX, bestY)
}

/**
 * Techniques
 * - Minimax with alpha-beta pruning
 * - Reduce search tree breadth (only choose moves around played moves)
 * - Reduce search tree depth (max depth depends on board size and unplayed moves)
 */
private fun Game.minimax(
    board: MutableList<MutableList<Boolean?>>,
    depth: Int,
    alpha: Int,
    beta: Int,
    isMax: Boolean,
    player: String
): Triple<Int, Int, Int> {  // x, y, score
    val unusedMoves = getMoves(board)

    // game over
    val scoreUnit = boardSize * boardSize + 1
    val score = when (winner(board, winCondition)) {
        true -> if (player == playerX) scoreUnit - depth else depth - scoreUnit
        false -> if (player == playerO) scoreUnit - depth else depth - scoreUnit
        null -> 0
    }
    val allUnusedMoves = board.flatten().filter { it == null }
    val maxDepth =
        if (boardSize == 3) 9
        // Tested on emulators and Samsung Galaxy Note 10
        // (Might need to test on weaker physical devices.)
        else (boardSize * boardSize / allUnusedMoves.size.coerceAtLeast(1) + 2)
            .coerceAtMost(unusedMoves.size * 2)
    if (score != 0 || allUnusedMoves.isEmpty() || depth == maxDepth)
        return Triple(-1, -1, score)

    // playable
    var bestX = -1
    var bestY = -1
    var bestScore = if (isMax) Int.MIN_VALUE else Int.MAX_VALUE
    var bestAlpha = alpha
    var bestBeta = beta

    for ((x, y) in unusedMoves) {
        if (isMax) {
            board[y][x] = player == playerX
            val (_, _, newScore) = minimax(board, depth + 1, bestAlpha, bestBeta, false, player)
            board[y][x] = null
            if (newScore > bestScore) {
                bestScore = newScore
                bestX = x
                bestY = y
            }
            if (bestAlpha < bestScore) bestAlpha = bestScore
            if (bestScore >= bestBeta) break
        } else {
            board[y][x] = player != playerX
            val (_, _, newScore) = minimax(board, depth + 1, bestAlpha, bestBeta, true, player)
            board[y][x] = null
            if (newScore < bestScore) {
                bestScore = newScore
                bestX = x
                bestY = y
            }
            if (bestBeta > bestScore) bestBeta = bestScore
            if (bestScore <= bestAlpha) break
        }
    }

    return Triple(bestX, bestY, bestScore)
}

// Limit breadth of search tree
private fun getMoves(board: List<List<Boolean?>>): Set<Pair<Int, Int>> {
    val moves: MutableSet<Pair<Int, Int>> =
        board.indices.let { idxs ->
            idxs.flatMap { x ->
                idxs.map { y -> x to y }
            }
        }
            .filter { (x, y) -> board[y][x] == null }
            .toMutableSet()

    if (board.size == 3) return moves

    val unusedMoves = moves.toSet()
    moves.clear()

    // add moves around used moves
    for ((x, y) in unusedMoves) {
        val willUse = (x - 1 >= 0 && board[y][x - 1] != null)
                || (y - 1 >= 0 && board[y - 1][x] != null)
                || (x + 1 < board.size && board[y][x + 1] != null)
                || (y + 1 < board.size && board[y + 1][x] != null)
                || (x - 1 >= 0 && y - 1 >= 0 && board[y - 1][x - 1] != null)
                || (x - 1 >= 0 && y + 1 < board.size && board[y + 1][x - 1] != null)
                || (x + 1 < board.size && y - 1 >= 0 && board[y - 1][x + 1] != null)
                || (x + 1 < board.size && y + 1 < board.size && board[y + 1][x + 1] != null)

        if (willUse) moves.add(x to y)
    }
    if (moves.isEmpty() && unusedMoves.isNotEmpty())
        // Might have to add more, by based on some values.
        moves.add(unusedMoves.random())

    // without shuffle, bot will prefer min x and y because of alpha-beta pruning
    return moves.shuffled().toSet()
}