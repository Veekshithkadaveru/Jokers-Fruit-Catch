package app.krafted.jokersfruitcatch.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.krafted.jokersfruitcatch.game.GameView
import app.krafted.jokersfruitcatch.ui.theme.JokerGold
import app.krafted.jokersfruitcatch.ui.theme.JokerOrange
import app.krafted.jokersfruitcatch.ui.theme.JokerPink
import app.krafted.jokersfruitcatch.ui.theme.JokerPurple
import app.krafted.jokersfruitcatch.viewmodel.GamePhase
import app.krafted.jokersfruitcatch.viewmodel.GameViewModel
import kotlinx.coroutines.delay

@Composable
fun GameScreen(
    viewModel: GameViewModel,
    onRoundEnd: () -> Unit = {},
    onGameOver: () -> Unit = {}
) {
    val score by viewModel.score.collectAsStateWithLifecycle()
    val roundScore by viewModel.roundScore.collectAsStateWithLifecycle()
    val lives by viewModel.lives.collectAsStateWithLifecycle()
    val round by viewModel.round.collectAsStateWithLifecycle()
    val gamePhase by viewModel.gamePhase.collectAsStateWithLifecycle()
    val difficulty by viewModel.difficultyConfig.collectAsStateWithLifecycle()
    val fruitCount by viewModel.roundFruitCount.collectAsStateWithLifecycle()

    var gameViewRef by remember { mutableStateOf<GameView?>(null) }
    var viewReady by remember { mutableStateOf(false) }

    //  Apply difficulty when it changes
    LaunchedEffect(difficulty, viewReady) {
        if (!viewReady) return@LaunchedEffect
        gameViewRef?.applyDifficulty(
            difficulty.speedMultiplier,
            difficulty.bombChanceMultiplier,
            difficulty.spawnIntervalMs
        )
    }

    LaunchedEffect(gamePhase, viewReady) {
        if (!viewReady) return@LaunchedEffect
        when (gamePhase) {
            GamePhase.WHEEL -> {
                gameViewRef?.setPaused(true)
                delay(800)
                onRoundEnd()
            }

            GamePhase.GAME_OVER -> {
                gameViewRef?.setPaused(true)
                delay(2000) // Show Game Over for 2 seconds
                onGameOver()
            }

            GamePhase.PLAYING -> {
                gameViewRef?.setPaused(false)
            }

            else -> { /* RESULT handled by navigation */
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { context ->
                GameView(context).apply {
                    onFruitCaught = { type -> viewModel.onFruitCaught(type) }
                    onFruitMissed = { viewModel.onFruitMissed() }
                    onBombCaught = { viewModel.onBombCaught() }
                    applyDifficulty(
                        difficulty.speedMultiplier,
                        difficulty.bombChanceMultiplier,
                        difficulty.spawnIntervalMs
                    )

                    setPaused(gamePhase != GamePhase.PLAYING)
                    gameViewRef = this
                    viewReady = true
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        GameHUD(
            score = score,
            lives = lives,
            round = round,
            fruitCount = fruitCount,
            fruitsPerRound = GameViewModel.FRUITS_PER_ROUND,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(top = 44.dp, start = 12.dp, end = 12.dp)
        )

        AnimatedVisibility(
            visible = gamePhase == GamePhase.WHEEL,
            enter = scaleIn(tween(400)) + fadeIn(tween(300)),
            exit = scaleOut(tween(300)) + fadeOut(tween(200)),
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0x99000000)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "ROUND $round",
                        style = TextStyle(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            letterSpacing = 4.sp
                        )
                    )
                    Text(
                        text = "COMPLETE!",
                        style = TextStyle(
                            fontSize = 44.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 4.sp,
                            brush = Brush.verticalGradient(
                                listOf(JokerGold, JokerOrange)
                            ),
                            shadow = Shadow(
                                color = Color.Black,
                                offset = Offset(3f, 4f),
                                blurRadius = 6f
                            )
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Score: +$roundScore",
                        style = TextStyle(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = JokerGold
                        )
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = gamePhase == GamePhase.GAME_OVER,
            enter = scaleIn(tween(500)) + fadeIn(tween(400)),
            exit = fadeOut(tween(300)),
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xBB000000)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "GAME OVER",
                        style = TextStyle(
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 4.sp,
                            brush = Brush.verticalGradient(
                                listOf(Color(0xFFFF4444), Color(0xFFCC0000))
                            ),
                            shadow = Shadow(
                                color = Color.Black,
                                offset = Offset(3f, 4f),
                                blurRadius = 8f
                            )
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Final Score: $score",
                        style = TextStyle(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = JokerGold
                        )
                    )
                }
            }
        }
    }
}


@Composable
private fun GameHUD(
    score: Int,
    lives: Int,
    round: Int,
    fruitCount: Int,
    fruitsPerRound: Int,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ── Round badge ──
            HudBadge {
                Text(
                    text = "ROUND $round",
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 2.sp,
                        color = JokerGold
                    )
                )
            }

            // ── Score ──
            HudBadge {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "SCORE ",
                        style = TextStyle(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFCCBBDD)
                        )
                    )
                    Text(
                        text = "$score",
                        style = TextStyle(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    )
                }
            }

            // ── Lives ──
            HudBadge {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    for (i in 1..3) {
                        HeartIcon(filled = i <= lives, size = 18)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // ── Round progress bar ──
        RoundProgressBar(
            current = fruitCount,
            total = fruitsPerRound,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp)
        )
    }
}

@Composable
private fun HudBadge(
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .drawBehind {

                drawRoundRect(
                    brush = Brush.verticalGradient(
                        listOf(
                            Color(0x55201040),
                            Color(0x66100820),
                            Color(0x55201040)
                        )
                    ),
                    cornerRadius = CornerRadius(14.dp.toPx())
                )

                drawRoundRect(
                    brush = Brush.verticalGradient(
                        listOf(
                            Color.White.copy(alpha = 0.18f),
                            Color.Transparent
                        ),
                        endY = size.height * 0.5f
                    ),
                    size = Size(size.width, size.height * 0.45f),
                    cornerRadius = CornerRadius(14.dp.toPx())
                )

                drawRoundRect(
                    brush = Brush.verticalGradient(
                        listOf(
                            Color.White.copy(alpha = 0.35f),
                            Color.White.copy(alpha = 0.08f),
                            Color.White.copy(alpha = 0.15f)
                        )
                    ),
                    cornerRadius = CornerRadius(14.dp.toPx()),
                    style = Stroke(width = 1.dp.toPx())
                )
            }
            .padding(horizontal = 14.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

@Composable
private fun HeartIcon(filled: Boolean, size: Int) {
    Canvas(modifier = Modifier.size(size.dp)) {
        val heartPath = androidx.compose.ui.graphics.Path().apply {
            val w = this@Canvas.size.width
            val h = this@Canvas.size.height
            moveTo(w * 0.5f, h * 0.85f)
            cubicTo(w * 0.15f, h * 0.55f, -w * 0.05f, h * 0.2f, w * 0.5f, h * 0.35f)
            moveTo(w * 0.5f, h * 0.85f)
            cubicTo(w * 0.85f, h * 0.55f, w * 1.05f, h * 0.2f, w * 0.5f, h * 0.35f)
        }
        if (filled) {
            drawPath(
                path = heartPath,
                brush = Brush.verticalGradient(
                    listOf(Color(0xFFFF4466), Color(0xFFCC0033))
                )
            )
        } else {
            drawPath(
                path = heartPath,
                color = Color(0x44FFFFFF),
                style = Stroke(width = 2f)
            )
        }
    }
}

@Composable
private fun RoundProgressBar(
    current: Int,
    total: Int,
    modifier: Modifier = Modifier
) {
    val progress = (current.toFloat() / total.toFloat()).coerceIn(0f, 1f)
    val animatedProgress = remember { Animatable(0f) }

    LaunchedEffect(progress) {
        animatedProgress.animateTo(
            targetValue = progress,
            animationSpec = tween(300, easing = FastOutSlowInEasing)
        )
    }

    Canvas(
        modifier = modifier.height(10.dp)
    ) {
        val barWidth = size.width
        val barHeight = size.height
        val cornerRad = CornerRadius(barHeight / 2, barHeight / 2)

        drawRoundRect(
            brush = Brush.verticalGradient(
                listOf(
                    Color(0x55201040),
                    Color(0x66100820),
                    Color(0x55201040)
                )
            ),
            cornerRadius = cornerRad
        )

        drawRoundRect(
            brush = Brush.verticalGradient(
                listOf(
                    Color.White.copy(alpha = 0.25f),
                    Color.White.copy(alpha = 0.06f),
                    Color.White.copy(alpha = 0.12f)
                )
            ),
            cornerRadius = cornerRad,
            style = Stroke(width = 1f)
        )

        if (animatedProgress.value > 0f) {
            val fillWidth = barWidth * animatedProgress.value
            drawRoundRect(
                brush = Brush.horizontalGradient(
                    listOf(JokerPurple, JokerPink, JokerOrange, JokerGold),
                    endX = barWidth
                ),
                size = Size(fillWidth, barHeight),
                cornerRadius = cornerRad
            )

            // Gloss on fill
            drawRoundRect(
                brush = Brush.verticalGradient(
                    listOf(
                        Color.White.copy(alpha = 0.35f),
                        Color.Transparent
                    ),
                    endY = barHeight * 0.45f
                ),
                size = Size(fillWidth, barHeight * 0.45f),
                cornerRadius = cornerRad
            )
        }
    }
}
