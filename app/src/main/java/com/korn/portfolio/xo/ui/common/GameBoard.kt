@file:Suppress("FunctionName")

package com.korn.portfolio.xo.ui.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun GameBoard(
    board: List<List<Boolean?>>,
    enabled: Boolean,
    onCellClick: (x: Int, y: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier
            .leftBorder()
            .topBorder()
            .aspectRatio(1f)
    ) {
        repeat(board.size) { y ->
            Row(
                Modifier
                    .bottomBorder()
                    .weight(1f)
            ) {
                repeat(board.first().size) { x ->
                    Box(
                        Modifier
                            .let {
                                if (enabled && board[y][x] == null)
                                    it.clickable {
                                        onCellClick(x, y)
                                    }
                                else
                                    it
                            }
                            .let {
                                when {
                                    board[y][x] == true -> it.drawX()
                                    board[y][x] == false -> it.drawO()
                                    else -> it
                                }
                            }
                            .rightBorder()
                            .weight(1f)
                            .fillMaxSize()
                    )
                }
            }
        }
    }
}

@Composable
fun Modifier.leftBorder(
    width: Dp = 1.dp,
    color: Color = MaterialTheme.colorScheme.outline
) = this then Modifier.drawBehind {
    drawLine(
        color = color,
        start = Offset(0f, 0f),
        end = Offset(0f, size.height),
        strokeWidth = width.value * density
    )
}

@Composable
fun Modifier.rightBorder(
    width: Dp = 1.dp,
    color: Color = MaterialTheme.colorScheme.outline
) = this then Modifier.drawBehind {
    drawLine(
        color = color,
        start = Offset(size.width, 0f),
        end = Offset(size.width, size.height),
        strokeWidth = width.value * density
    )
}

@Composable
fun Modifier.topBorder(
    width: Dp = 1.dp,
    color: Color = MaterialTheme.colorScheme.outline
) = this then Modifier.drawBehind {
    drawLine(
        color = color,
        start = Offset(0f, 0f),
        end = Offset(size.width, 0f),
        strokeWidth = width.value * density
    )
}

@Composable
fun Modifier.bottomBorder(
    width: Dp = 1.dp,
    color: Color = MaterialTheme.colorScheme.outline
) = this then Modifier.drawBehind {
    drawLine(
        color = color,
        start = Offset(0f, size.height),
        end = Offset(size.width, size.height),
        strokeWidth = width.value * density
    )
}

fun Modifier.drawX(
    width: Dp = 1.dp,
    color: Color = Color.Red
) = this then Modifier.drawBehind {
    drawLine(
        color,
        Offset(0f, 0f),
        Offset(size.width, size.height),
        width.value * density
    )
    drawLine(
        color,
        Offset(size.width, 0f),
        Offset(0f, size.height),
        width.value * density
    )
}

fun Modifier.drawO(
    width: Dp = 1.dp,
    color: Color = Color.Blue
) = this then Modifier.drawBehind {
    drawCircle(
        color,
        size.width / 2f * 0.75f,
        style = Stroke(width.value * density)
    )
}