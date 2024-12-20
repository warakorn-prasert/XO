@file:Suppress("FunctionName")

package com.korn.portfolio.xo.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.korn.portfolio.xo.R
import com.korn.portfolio.xo.repo.Game

private const val FAB_HEIGHT_DP = 56
private const val FAB_PADDING_DP = 16

private const val DEFAULT_BOARD_SIZE = 3
private const val MIN_BOARD_SIZE = 3
private const val MAX_BOARD_SIZE = 10

private const val MIN_WIN_CONDITION = 3

private const val DEFAULT_PLAYER_X = "Player X"
private const val DEFAULT_PLAYER_O = "Player O"
private const val MAX_PLAYER_LENGTH = 10

private const val CROWN_EMOJI = "\uD83D\uDC51"
private const val SAD_EMOJI = "\uD83D\uDE22"
private const val CROSSED_SWORD_EMOJI = "⚔\uFE0F"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PastGames(
    games: List<Game>,
    onInspect: (Game) -> Unit,
    onDelete: (Game) -> Unit,
    onPlay: (Game) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.display_title))
                }
            )
        },
        floatingActionButton = {
            var showPlayDialog by rememberSaveable { mutableStateOf(false) }
            ExtendedFloatingActionButton(
                onClick = { showPlayDialog = true }
            ) {
                Text(stringResource(R.string.play_button_text))
            }
            if (showPlayDialog)
                PlayDialog(
                    onDismissRequest = { showPlayDialog = false },
                    onPlay = onPlay
                )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            contentPadding = PaddingValues(bottom = (FAB_HEIGHT_DP + FAB_PADDING_DP).dp)
        ) {
            itemsIndexed(items = games, key = { _, it -> it.id }) { idx, game ->
                if (idx > 0)
                    HorizontalDivider(Modifier.padding(horizontal = 24.dp))
                GameItem(
                    game = game,
                    onInspect = { onInspect(game) },
                    onDelete = { onDelete(game) }
                )
            }
        }
    }
}

@Composable
private fun PlayDialog(
    onDismissRequest: () -> Unit,
    onPlay: (Game) -> Unit
) {
    Dialog(onDismissRequest) {
        Card {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())  // for landscape phone
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.play_dialog_title),
                        style = MaterialTheme.typography.titleMedium
                    )
                    IconButton(
                        onClick = onDismissRequest,
                        modifier = Modifier.align(Alignment.CenterEnd)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Close,
                            contentDescription = stringResource(R.string.play_dialog_close_button_description)
                        )
                    }
                }

                var boardSize by rememberSaveable { mutableStateOf(DEFAULT_BOARD_SIZE.toString()) }
                val isBoardSizeNumber = boardSize.toIntOrNull() != null
                val invalidBoardSize = !isBoardSizeNumber
                        || boardSize.toInt() !in MIN_BOARD_SIZE..MAX_BOARD_SIZE

                var winCondition by rememberSaveable(boardSize) { mutableStateOf(boardSize) }
                val invalidWinCondition = !isBoardSizeNumber
                        || winCondition.toIntOrNull()
                            ?.let { it !in MIN_WIN_CONDITION..boardSize.toInt() }
                            ?: false

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    val focus = LocalFocusManager.current
                    OutlinedTextField(
                        value = boardSize,
                        onValueChange = { boardSize = it },
                        modifier = Modifier.weight(1f),
                        label = { Text(stringResource(R.string.choose_board_size)) },
                        supportingText = { Text("Max $MAX_BOARD_SIZE") },
                        isError = invalidBoardSize,
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(onDone = { focus.clearFocus() })
                    )
                    OutlinedTextField(
                        value = winCondition,
                        onValueChange = { winCondition = it },
                        modifier = Modifier.weight(1f),
                        label = { Text(stringResource(R.string.choose_win_condition)) },
                        supportingText = { Text("Min $MIN_WIN_CONDITION") },
                        isError = invalidWinCondition,
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(onDone = { focus.clearFocus() })
                    )
                }

                var playerX by rememberSaveable { mutableStateOf(DEFAULT_PLAYER_X) }
                var playerO by rememberSaveable { mutableStateOf(DEFAULT_PLAYER_O) }

                val invalidPlayerX = playerX.isBlank() || playerX == playerO
                val invalidPlayerO = playerO.isBlank() || playerO == playerX

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    val focus = LocalFocusManager.current
                    OutlinedTextField(
                        value = playerX,
                        onValueChange = { if (it.length <= MAX_PLAYER_LENGTH) playerX = it },
                        modifier = Modifier.weight(1f),
                        label = { Text(stringResource(R.string.choose_player_X)) },
                        supportingText = { Text("Max $MAX_PLAYER_LENGTH chars") },
                        isError = invalidPlayerX,
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { focus.clearFocus() })
                    )
                    OutlinedTextField(
                        value = playerO,
                        onValueChange = { if (it.length <= MAX_PLAYER_LENGTH) playerO = it },
                        modifier = Modifier.weight(1f),
                        label = { Text(stringResource(R.string.choose_player_O)) },
                        supportingText = { Text("Max $MAX_PLAYER_LENGTH chars") },
                        isError = invalidPlayerO,
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { focus.clearFocus() })
                    )
                }

                Text(
                    text = stringResource(R.string.player_X_start_first),
                    color = MaterialTheme.colorScheme.outline,
                    fontStyle = FontStyle.Italic,
                    style = MaterialTheme.typography.labelSmall
                )

                TextButton(
                    onClick = {
                        onPlay(Game(
                            boardSize = boardSize.toInt(),
                            winCondition = winCondition.toInt(),
                            playerX = playerX,
                            playerO = playerO
                        ))
                    },
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    enabled = !(invalidBoardSize || invalidWinCondition || invalidPlayerX || invalidPlayerO)
                ) {
                    Text(stringResource(R.string.start_game_button))
                }
            }
        }
    }
}

@Composable
private fun GameItem(
    game: Game,
    onInspect: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.padding(start = 16.dp, top = 12.dp, bottom = 12.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            val loser = if (game.winner == game.playerX) game.playerO else game.playerX
            val result = game.winner
                ?.let { "$CROWN_EMOJI $it $SAD_EMOJI $loser" }
                ?: "$CROSSED_SWORD_EMOJI Draw - ${game.playerX} vs ${game.playerO}"
            Text(
                text = result,
                fontWeight = FontWeight.Bold
            )
            Text("${game.boardSize} x ${game.boardSize} win at ${game.winCondition}")
        }
        Spacer(Modifier.weight(1f))
        IconButton(onClick = onInspect) {
            Icon(
                painter = painterResource(R.drawable.ic_inspect),
                contentDescription = stringResource(R.string.inspect_button_description)
            )
        }
        IconButton(onClick = onDelete) {
            Icon(
                imageVector = Icons.Outlined.Delete,
                contentDescription = stringResource(R.string.delete_button_description)
            )
        }
    }
}

@Preview
@Composable
private fun PastGamesPreview() {
    PastGames(
        games = listOf(
            Game(
                boardSize = 3,
                winCondition = 3,
                playerX = "Player 1",
                playerO = "Player 2"
            ),
            Game(
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
            Game(
                boardSize = 4,
                winCondition = 4,
                playerX = "Player A",
                playerO = "Player B",
                moves = listOf(listOf(
                    listOf(true, null, false, true),
                    listOf(true, null, false, null),
                    listOf(true, true, false, null),
                    listOf(null, false, false, null)
                ))
            ),
        ),
        onInspect = {},
        onDelete = {},
        onPlay = {}
    )
}

@Preview
@Composable
private fun PlayDialogPreview() {
    PlayDialog(
        onDismissRequest = {},
        onPlay = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun GameItemPreview() {
    GameItem(
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
        onDelete = {},
        onInspect = {}
    )
}