@file:Suppress("FunctionName")

package com.korn.portfolio.xo.ui

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
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
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            var currentIdx by rememberSaveable { mutableIntStateOf(0) }
            val currentPlayer = game.currentPlayer(currentIdx)
            Row(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .height(IntrinsicSize.Max),
                horizontalArrangement = Arrangement.spacedBy(48.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = when {
                        currentIdx < game.moves.size - 1 -> "$currentPlayer's turn"
                        game.winner != null -> "${game.winner} won!"
                        else -> stringResource(R.string.draw_message)
                    },
                    style = MaterialTheme.typography.displaySmall
                )
                if (currentIdx < game.moves.size - 1)
                    Box(Modifier
                        .leftBorder()
                        .topBorder()
                        .rightBorder()
                        .bottomBorder()
                        .let { it2 ->
                            when (currentPlayer) {
                                game.playerX -> it2.drawX()
                                game.playerO -> it2.drawO()
                                else -> it2
                            }
                        }
                        .aspectRatio(1f)
                    )
            }
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 40.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowLeft,
                    contentDescription = stringResource(R.string.inspect_previous_move_button_description),
                    modifier = Modifier
                        .clip(CircleShape)
                        .let {
                            if (currentIdx > 0) it.clickable { currentIdx-- }
                            else it
                        }
                        .aspectRatio(1f)
                        .weight(1f)
                )
                GameBoard(
                    board = game.moves[currentIdx],
                    enabled = false,
                    onCellClick = { _, _ -> },
                    modifier = Modifier.weight(5f),
                    matchHeightConstraintsFirst = true
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                    contentDescription = stringResource(R.string.inspect_next_move_button_description),
                    modifier = Modifier
                        .clip(CircleShape)
                        .let {
                            if (currentIdx < game.moves.size - 1) it.clickable { currentIdx++ }
                            else it
                        }
                        .aspectRatio(1f)
                        .weight(1f)
                )
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