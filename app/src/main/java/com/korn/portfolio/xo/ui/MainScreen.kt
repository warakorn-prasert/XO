@file:Suppress("FunctionName")

package com.korn.portfolio.xo.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel

enum class Destination {
    PAST_GAMES,
    INSPECT,
    PLAYING
}

@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    var currentDest by rememberSaveable { mutableStateOf(Destination.PAST_GAMES) }

    val viewModel: SharedViewModel = viewModel(factory = SharedViewModel.Factory)

    when (currentDest) {
        Destination.PAST_GAMES -> {
            val games by viewModel.games.collectAsState(emptyList())
            PastGames(
                games = games,
                onInspect = { game ->
                    viewModel.gameToPlay = game to null
                    currentDest = Destination.INSPECT
                },
                onDelete = { game ->
                    viewModel.deleteGame(game)
                },
                onPlay = { game, bot ->
                    viewModel.gameToPlay = game to bot
                    currentDest = Destination.PLAYING
                },
                modifier = modifier.fillMaxSize()
            )
        }
        Destination.INSPECT ->
            Inspect(
                game = viewModel.gameToPlay!!.first,
                onExit = {
                    currentDest = Destination.PAST_GAMES
                },
                modifier = modifier.fillMaxSize()
            )
        Destination.PLAYING ->
            Playing(
                game = viewModel.gameToPlay!!.first,
                bot = viewModel.gameToPlay!!.second,
                onCellClick = { player, x, y ->
                    viewModel.gameToPlay = viewModel.gameToPlay!!.run {
                        first.addMove(player, x, y) to second
                    }
                },
                onSaveGame = { game ->
                    viewModel.saveGame(game)
                },
                onExit = {
                    currentDest = Destination.PAST_GAMES
                },
                modifier = modifier.fillMaxSize()
            )
    }
}