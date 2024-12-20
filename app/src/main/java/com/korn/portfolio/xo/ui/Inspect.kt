@file:Suppress("FunctionName")

package com.korn.portfolio.xo.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.korn.portfolio.xo.R
import com.korn.portfolio.xo.repo.Game
import com.korn.portfolio.xo.ui.common.GameBoard
import com.korn.portfolio.xo.ui.common.bottomBorder
import com.korn.portfolio.xo.ui.common.drawO
import com.korn.portfolio.xo.ui.common.drawX
import com.korn.portfolio.xo.ui.common.leftBorder
import com.korn.portfolio.xo.ui.common.rightBorder
import com.korn.portfolio.xo.ui.common.topBorder

private fun Game.currentPlayer(idx: Int): String = this
    .copy(moves = moves.subList(0, idx + 1))
    .currentPlayer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Inspect(
    game: Game,
    onExit: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onExit() }
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text("Inspect - ${game.boardSize} x ${game.boardSize}, win at ${game.winCondition}")
                },
                navigationIcon = {
                    IconButton(onExit) {
                        Icon(
                            imageVector = Icons.Outlined.Close,
                            contentDescription = stringResource(R.string.exit_game_button_description)
                        )
                    }
                }
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(64.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            var currentIdx by rememberSaveable { mutableIntStateOf(0) }
            val currentPlayer = game.currentPlayer(currentIdx)
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = when {
                        currentIdx < game.moves.size - 1 -> "$currentPlayer's turn"
                        game.winner != null -> "${game.winner} won!"
                        else -> stringResource(R.string.draw_message)
                    },
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.displaySmall
                )
            }
            GameBoard(
                board = game.moves[currentIdx],
                enabled = false,
                onCellClick = { _, _ -> },
                modifier = Modifier.weight(2f)
            )
            Box(
                modifier = Modifier
                    .weight(0.5f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick = { currentIdx-- },
                    modifier = Modifier.align(Alignment.CenterStart),
                    enabled = currentIdx > 0
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowLeft,
                        contentDescription = stringResource(R.string.inspect_previous_move_button_description),
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Box(
                    modifier = Modifier
                        .leftBorder()
                        .topBorder()
                        .rightBorder()
                        .bottomBorder()
                        .let {
                            if (currentPlayer == game.playerX) it.drawX()
                            else it.drawO()
                        }
                        .fillMaxWidth(0.3f)
                        .aspectRatio(1f)
                )
                IconButton(
                    onClick = { currentIdx++ },
                    modifier = Modifier.align(Alignment.CenterEnd),
                    enabled = currentIdx < game.moves.size - 1
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                        contentDescription = stringResource(R.string.inspect_next_move_button_description),
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun InspectPreview() {
    Inspect(
        game = Game(
            boardSize = 3,
            winCondition = 3,
            playerX = "Player 1",
            playerO = "Player 2",
            moves = listOf(
                listOf(
                    listOf(null, null, null),
                    listOf(null, null, null),
                    listOf(null, null, null)
                ),
                listOf(
                    listOf(null, null, null),
                    listOf(null, true, null),
                    listOf(null, null, null)
                )
            )
        ),
        onExit = {}
    )
}