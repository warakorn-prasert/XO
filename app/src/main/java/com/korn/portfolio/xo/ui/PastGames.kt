@file:Suppress("FunctionName")

package com.korn.portfolio.xo.ui

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalDensity
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
    onDeleteAll: () -> Unit,
    onPlay: (game: Game, bot: String?) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = Modifier
            .statusBarsPadding()
            .then(modifier),
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.display_title))
                },
                actions = {
                    var showDeleteDialog by remember { mutableStateOf(false) }
                    IconButton({ showDeleteDialog = true }) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = stringResource(R.string.delete_all_button_description)
                        )
                    }
                    if (showDeleteDialog)
                        DeleteAllDialog(
                            onDismissRequest = { showDeleteDialog = false },
                            onDeleteAll = onDeleteAll
                        )
                }
            )
        },
        floatingActionButton = {
            var showPlayDialog by rememberSaveable { mutableStateOf(false) }
            ExtendedFloatingActionButton(
                onClick = { showPlayDialog = true },
                modifier = Modifier.navigationBarsPadding()
            ) {
                Text(stringResource(R.string.play_button_text))
            }
            if (showPlayDialog)
                PlayDialog(
                    onDismissRequest = { showPlayDialog = false },
                    onPlay = onPlay
                )
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { paddingValues ->
        val gameSize = games.size
        val navBarHeight = with(LocalDensity.current) {
            WindowInsets.navigationBars.getBottom(this).toDp()
        }
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 400.dp),
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            contentPadding = PaddingValues(
                bottom = navBarHeight + (FAB_HEIGHT_DP + FAB_PADDING_DP).dp
            )
        ) {
            itemsIndexed(items = games, key = { _, it -> it.id }) { idx, game ->
                Column {
                    GameItem(
                        game = game,
                        onInspect = { onInspect(game) },
                        onDelete = { onDelete(game) }
                    )
                    if (idx < gameSize - 1)
                        HorizontalDivider(Modifier.padding(horizontal = 24.dp))
                }
            }
        }
    }
}

@Composable
private fun DeleteAllDialog(
    onDismissRequest: () -> Unit,
    onDeleteAll: () -> Unit
) {
    Dialog(onDismissRequest) {
        Card {
            Column(
                modifier = Modifier
                    .padding(start = 16.dp, top = 32.dp, end = 16.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.delete_all_game_confirm_text),
                    style = MaterialTheme.typography.headlineMedium
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    IconButton(onDismissRequest) {
                        Icon(
                            imageVector = Icons.Outlined.Close,
                            contentDescription = stringResource(R.string.close_dialog_button_description)
                        )
                    }
                    IconButton({ onDeleteAll(); onDismissRequest() }) {
                        Icon(
                            imageVector = Icons.Outlined.Done,
                            contentDescription = stringResource(R.string.confirm_dialog_button_description)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PlayDialog(
    onDismissRequest: () -> Unit,
    onPlay: (game: Game, bot: String?) -> Unit
) {
    Dialog(onDismissRequest) {
        Card {
            Column(
                modifier = Modifier.padding(vertical = 16.dp),
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

                var playerX by rememberSaveable { mutableStateOf(DEFAULT_PLAYER_X) }
                var playerO by rememberSaveable { mutableStateOf(DEFAULT_PLAYER_O) }

                val invalidPlayerX = playerX.isBlank() || playerX == playerO
                val invalidPlayerO = playerO.isBlank() || playerO == playerX

                var botX by rememberSaveable { mutableStateOf(false) }
                var botO by rememberSaveable { mutableStateOf(true) }

                val scrollState = rememberScrollState()
                Column(
                    modifier = Modifier
                        .let {
                            if (scrollState.canScrollForward || scrollState.canScrollBackward)
                                it.scrollbar(scrollState)
                            else
                                it
                        }
                        .verticalScroll(scrollState)
                        .padding(horizontal = 16.dp)
                        .weight(weight = 1f, fill = false),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
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

                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        val focus = LocalFocusManager.current
                        Column(Modifier.weight(1f)) {
                            OutlinedTextField(
                                value = playerX,
                                onValueChange = {
                                    if (it.length <= MAX_PLAYER_LENGTH) playerX = it
                                },
                                label = { Text(stringResource(R.string.choose_player_X)) },
                                supportingText = { Text("Max $MAX_PLAYER_LENGTH chars") },
                                isError = invalidPlayerX,
                                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                                keyboardActions = KeyboardActions(onDone = { focus.clearFocus() })
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(botX, { botX = it; if (botX && botO) botO = false })
                                Text(
                                    text = "Bot",
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }
                        }
                        Column(Modifier.weight(1f)) {
                            OutlinedTextField(
                                value = playerO,
                                onValueChange = {
                                    if (it.length <= MAX_PLAYER_LENGTH) playerO = it
                                },
                                label = { Text(stringResource(R.string.choose_player_O)) },
                                supportingText = { Text("Max $MAX_PLAYER_LENGTH chars") },
                                isError = invalidPlayerO,
                                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                                keyboardActions = KeyboardActions(onDone = { focus.clearFocus() })
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(botO, { botO = it; if (botO && botX) botX = false })
                                Text(
                                    text = "Bot",
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }
                        }
                    }

                    Text(
                        text = stringResource(R.string.player_X_start_first),
                        color = MaterialTheme.colorScheme.outline,
                        fontStyle = FontStyle.Italic,
                        style = MaterialTheme.typography.labelSmall
                    )
                }

                TextButton(
                    onClick = {
                        val bot = when {
                            botX -> "(Bot) $playerX"
                            botO -> "(Bot) $playerO"
                            else -> null
                        }
                        onPlay(
                            Game(
                                boardSize = boardSize.toInt(),
                                winCondition = winCondition.toInt(),
                                playerX = if (botX) bot!! else playerX,
                                playerO = if (botO) bot!! else playerO
                            ),
                            bot
                        )
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

private const val SCROLL_BAR_WIDTH_DP = 4
private const val SCROLL_BAR_HEIGHT_DP = 40

@Composable
private fun Modifier.scrollbar(scrollState: ScrollState): Modifier {
    val scrollBarColor = MaterialTheme.colorScheme.onSurfaceVariant
    val density = LocalDensity.current
    val barWidth = with(density) { SCROLL_BAR_WIDTH_DP.dp.toPx() }
    val barHeight = with(density) { SCROLL_BAR_HEIGHT_DP.dp.toPx() }
    return this.then(
        Modifier.drawBehind {
            drawRoundRect(
                color = scrollBarColor,
                topLeft = Offset(
                    x = size.width - barWidth,
                    y = (size.height - barHeight) * scrollState.value / scrollState.maxValue
                ),
                size = Size(
                    width = barWidth * 2,  // make top-right and bottom-right not round
                    height = barHeight
                ),
                cornerRadius = CornerRadius(x = barWidth, y = barWidth)
            )
        }
    )
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
        onDeleteAll = {},
        onPlay = { _, _ -> }
    )
}

@Preview
@Composable
private fun DeleteAllDialogPreview() {
    DeleteAllDialog({}, {})
}

@Preview
@Composable
private fun PlayDialogPreview() {
    PlayDialog(
        onDismissRequest = {},
        onPlay = { _, _ -> }
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