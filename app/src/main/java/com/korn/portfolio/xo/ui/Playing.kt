@file:Suppress("FunctionName")

package com.korn.portfolio.xo.ui

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.korn.portfolio.xo.R
import com.korn.portfolio.xo.bot.botMove
import com.korn.portfolio.xo.repo.Game
import com.korn.portfolio.xo.ui.common.GameBoard
import com.korn.portfolio.xo.ui.common.bottomBorder
import com.korn.portfolio.xo.ui.common.drawO
import com.korn.portfolio.xo.ui.common.drawX
import com.korn.portfolio.xo.ui.common.leftBorder
import com.korn.portfolio.xo.ui.common.rightBorder
import com.korn.portfolio.xo.ui.common.topBorder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Player 1 is X, always starts first.
val Game.currentPlayer: String
    get() = if (moves.size % 2 == 1) playerX else playerO

private const val BOT_DELAY_MILLIS = 300L
private const val BOT_BOX_ROTATE_MILLS = 2000

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun Playing(
    game: Game,
    bot: String?,
    onCellClick: (player: String, x: Int, y: Int) -> Unit,
    onSaveGame: (Game) -> Unit,
    onExit: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onExit() }

    val isPlaying = game.winner == null && game.moves.last().any { it.any { cell -> cell == null } }
    LaunchedEffect(isPlaying) {
        if (!isPlaying) onSaveGame(game)
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text("${game.boardSize} x ${game.boardSize}, win at ${game.winCondition}")
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
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val isBotThinking = game.currentPlayer == bot
            FlowRow(
                modifier = Modifier
                    .padding(start = 16.dp, top = 8.dp, end = 16.dp)
                    .width(IntrinsicSize.Max),
                horizontalArrangement = Arrangement.spacedBy(48.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                Text(
                    text = "${game.currentPlayer}'s turn",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.displaySmall
                )
                val textSize = with(LocalDensity.current) {
                    MaterialTheme.typography.displaySmall.fontSize.toDp()
                }
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    val degree by animateFloatAsState(
                        targetValue = if (isBotThinking) 360f else 0f,
                        animationSpec = InfiniteRepeatableSpec(tween(BOT_BOX_ROTATE_MILLS)),
                        label = stringResource(R.string.bot_box_degree_anim_label)
                    )
                    Box(Modifier
                        .let {
                            if (isBotThinking) it.rotate(degree)
                            else it
                        }
                        .size(textSize * 1.5f)
                        .leftBorder()
                        .topBorder()
                        .rightBorder()
                        .bottomBorder()
                        .let {
                            if (game.currentPlayer == game.playerX) it.drawX()
                            else it.drawO()
                        }
                        .aspectRatio(1f)
                    )
                }
            }

            val scope = rememberCoroutineScope()
            LaunchedEffect(isBotThinking) {
                scope.launch(Dispatchers.Default) {
                    if (game.currentPlayer == bot && isPlaying) {
                        val oldBoard = game.moves.last()
                        val newBoard = game.botMove(bot).moves.last()
                        var botX = -1
                        var botY = -1
                        for (yy in 0..<game.boardSize) {
                            for (xx in 0..<game.boardSize) {
                                if (newBoard[yy][xx] != oldBoard[yy][xx]) {
                                    botX = xx
                                    botY = yy
                                }
                            }
                        }
                        delay(BOT_DELAY_MILLIS)
                        onCellClick(bot, botX, botY)
                    }
                }
            }

            GameBoard(
                board = game.moves.last(),
                enabled = isPlaying && !isBotThinking,
                onCellClick = { x, y -> onCellClick(game.currentPlayer, x, y) },
                modifier = Modifier.padding(40.dp)
            )
        }
        if (!isPlaying)
            ResultDialog(game, onExit)
    }
}

@Composable
private fun ResultDialog(
    game: Game,
    onExit: () -> Unit
) {
    Dialog(onDismissRequest = onExit) {
        Card {
            Column(
                modifier = Modifier.padding(32.dp),
                verticalArrangement = Arrangement.spacedBy(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text =
                        if (game.winner != null) "${game.winner} won!"
                        else stringResource(R.string.draw_message),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.displayMedium
                )
                TextButton(onExit) {
                    Text(stringResource(R.string.back_to_home))
                }
            }
        }
    }
}

@Preview
@Preview(
    uiMode = Configuration.ORIENTATION_LANDSCAPE,
    device = "spec:id=reference_phone,shape=Normal,width=891,height=411,unit=dp,dpi=420"
)
@Composable
private fun PlayingPreview() {
    Playing(
        game = Game(
            boardSize = 3,
            winCondition = 3,
            playerX = "Player 1",
            playerO = "Player 2",
            moves = listOf(listOf(
                listOf(null, true, null),
                listOf(false, true, null),
                listOf(null, null, false)
            ))
        ),
        bot = null,
        onCellClick = { _, _, _ ->},
        onSaveGame = {},
        onExit = {}
    )
}

@Preview
@Composable
private fun LongNamePreview() {
    Playing(
        game = Game(
            boardSize = 3,
            winCondition = 3,
            playerX = "Player 11111111111",
            playerO = "Player 2",
            moves = listOf(listOf(
                listOf(null, true, null),
                listOf(false, true, null),
                listOf(null, null, false)
            ))
        ),
        bot = null,
        onCellClick = { _, _, _ ->},
        onSaveGame = {},
        onExit = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun GameBoardPreview() {
    GameBoard(
        board = listOf(
            listOf(null, true, null),
            listOf(false, true, null),
            listOf(null, null, false)
        ),
        enabled = true,
        onCellClick = { _, _ -> }
    )
}

@Preview
@Composable
private fun ResultDialogPreview() {
    ResultDialog(
        game = Game(
            boardSize = 3,
            winCondition = 3,
            playerX = "Player 1",
            playerO = "Player 2",
            moves = listOf(listOf(
                listOf(null, true, null),
                listOf(false, true, null),
                listOf(null, true, false)
            ))
        ),
        onExit = {}
    )
}