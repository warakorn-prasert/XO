@file:Suppress("FunctionName")

package com.korn.portfolio.xo.ui.common

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.korn.portfolio.xo.R
import com.korn.portfolio.xo.ui.theme.XOTheme

@Composable
fun Modifier.appWaterMark(): Modifier {
    val appIcon = painterResource(R.drawable.ic_android)
    val color = MaterialTheme.colorScheme.surfaceContainer
    val config = LocalConfiguration.current
    var screenWidth = config.screenWidthDp.toFloat()
    var screenHeight = config.screenHeightDp.toFloat()
    val size = with(LocalDensity.current) {
        screenWidth = screenWidth.dp.toPx()
        screenHeight = screenHeight.dp.toPx()
        screenWidth / 2f
    }
    val infiniteTransition = rememberInfiniteTransition(
        label = stringResource(R.string.watermark_anim_degree_label)
    )
    val degree by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(
            durationMillis = 20000,
            easing = LinearEasing
        )),
        label = stringResource(R.string.watermark_anim_degree_label)
    )
    return this then Modifier.drawBehind {
        val x = (screenWidth - size) / 2f
        val y = (screenHeight - size) / 2f
        val center = Rect(Offset.Zero, Offset(screenWidth, screenHeight)).center
        rotate(degree, center) {
            translate(x, y) {
                with(appIcon) {
                    draw(
                        size = Size(size, size),
                        alpha = 1f,
                        colorFilter = ColorFilter.tint(color)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun WaterMarkPreview() {
    XOTheme {
        Surface {
            Column(
                modifier = Modifier
                    .appWaterMark()
                    .fillMaxSize(),
                verticalArrangement = Arrangement.SpaceAround,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                repeat(30) {
                    Text("HELLO")
                }
            }
        }
    }
}