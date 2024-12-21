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
                    alpha = Int.MIN_VALUE,
                    beta = Int.MAX_VALUE,
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
    alpha: Int,
    beta: Int,
    isMax: Boolean,
    player: String
): Pair<Int, Int> {
    // No empty space
    // (also have to check before calling getMoves())
    if (board.all { y -> y.none { xy -> xy == null } })
        return 0 to depth

    val score = when (winner(board, winCondition)) {
        true -> if (player == playerX) 10 else -10
        false -> if (player == playerO) 10 else -10
        null -> 0
    }

    if (score != 0 || depth == 4 /* speed is limit at winCondition=4 */)
        return score to depth

    val moves = getMoves(board)
    var bestAlpha = alpha
    var bestBeta = beta

    return if (isMax) {
        var best = Int.MIN_VALUE
        var bestDepth = Int.MAX_VALUE
        for ((x, y) in moves) {
            if (board[y][x] == null) {
                board[y][x] = player == playerX
                minimax(board, depth + 1, bestAlpha, bestBeta, false, player).let {
                    best = best.coerceAtLeast(it.first)
                    bestDepth = it.second
                }
                board[y][x] = null

                bestAlpha = alpha.coerceAtLeast(best)
                if (best >= beta)
                    break
            }
        }
        best to bestDepth
    } else {
        var best = Int.MAX_VALUE
        var bestDepth = Int.MAX_VALUE
        for ((x, y) in moves) {
            if (board[y][x] == null) {
                board[y][x] = player != playerX
                minimax(board, depth + 1, bestAlpha, bestBeta, true, player).let {
                    best = best.coerceAtMost(it.first)
                    bestDepth = it.second
                }
                board[y][x] = null

                bestBeta = beta.coerceAtMost(best)
                if (best <= alpha)
                    break
            }
        }
        best to bestDepth
    }
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
    if (moves.isEmpty()) moves.add(unusedMoves.random())

    return moves
}