package app.krafted.jokersfruitcatch.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.krafted.jokersfruitcatch.ui.theme.JokerGold
import app.krafted.jokersfruitcatch.ui.theme.JokerGoldDark
import app.krafted.jokersfruitcatch.ui.theme.JokerOrange
import app.krafted.jokersfruitcatch.ui.theme.JokerPurple
import app.krafted.jokersfruitcatch.ui.theme.JokerPurpleDeep
import kotlinx.coroutines.delay
import kotlin.random.Random

private data class WheelSegment(
    val multiplier: Float,
    val label: String,
    val color: Color,
    val weight: Int
)

private val SEGMENTS = listOf(
    WheelSegment(1f, "1x", Color(0xFF888899), 40),
    WheelSegment(2f, "2x", Color(0xFF4488FF), 30),
    WheelSegment(3f, "3x", Color(0xFF9B30FF), 18),
    WheelSegment(5f, "5x", Color(0xFFFFD700), 10),
    WheelSegment(0.5f, "0.5x", Color(0xFFFF3344), 2)
)

private fun buildWheelLayout(): List<WheelSegment> {
    val layout = mutableListOf<WheelSegment>()
    for (segment in SEGMENTS) {
        val count = (segment.weight * 20) / 100
        repeat(count.coerceAtLeast(1)) { layout.add(segment) }
    }
    return layout.shuffled(Random(42))
}

private val WHEEL_LAYOUT = buildWheelLayout()
private val SLICE_ANGLE = 360f / WHEEL_LAYOUT.size

@Composable
fun WheelScreen(
    roundScore: Int,
    round: Int,
    onMultiplierSelected: (Float) -> Unit
) {
    var rotation by remember { mutableFloatStateOf(0f) }
    var angularVelocity by remember { mutableFloatStateOf(0f) }
    var isSpinning by remember { mutableStateOf(false) }
    var hasSpun by remember { mutableStateOf(false) }
    var resultMultiplier by remember { mutableStateOf<Float?>(null) }
    var showResult by remember { mutableStateOf(false) }
    var spinCount by remember { mutableIntStateOf(0) }

    LaunchedEffect(spinCount) {
        if (spinCount == 0) return@LaunchedEffect
        while (angularVelocity > 0.3f) {
            rotation += angularVelocity
            angularVelocity *= 0.985f
            delay(16)
        }
        angularVelocity = 0f

        val normalizedAngle = ((360f - (rotation % 360f)) + 360f) % 360f
        val segmentIndex = (normalizedAngle / SLICE_ANGLE).toInt() % WHEEL_LAYOUT.size
        val segment = WHEEL_LAYOUT[segmentIndex]
        resultMultiplier = segment.multiplier
        isSpinning = false

        delay(400)
        showResult = true
    }

    val infiniteTransition = rememberInfiniteTransition(label = "wheel")

    val pointerBob by infiniteTransition.animateFloat(
        initialValue = -3f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pointerBob"
    )

    val glowPulse by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowPulse"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF0D001A),
                        Color(0xFF1A0033),
                        Color(0xFF2A004D),
                        Color(0xFF1A0033)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(0.08f))

            Text(
                text = "ROUND $round BONUS",
                style = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 4.sp,
                    color = Color.White
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "SPIN THE WHEEL!",
                style = TextStyle(
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 3.sp,
                    brush = Brush.verticalGradient(
                        listOf(JokerGold, JokerOrange)
                    ),
                    shadow = Shadow(
                        color = Color.Black,
                        offset = Offset(2f, 3f),
                        blurRadius = 6f
                    )
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Round Score: $roundScore",
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFFCCBBDD)
                )
            )

            Spacer(modifier = Modifier.weight(0.05f))

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(320.dp)
            ) {
                Canvas(modifier = Modifier.size(310.dp)) {
                    drawCircle(
                        brush = Brush.radialGradient(
                            listOf(
                                JokerGold.copy(alpha = glowPulse * 0.2f),
                                Color.Transparent
                            ),
                            radius = size.width * 0.6f
                        )
                    )
                }

                Canvas(
                    modifier = Modifier
                        .size(280.dp)
                        .graphicsLayer { rotationZ = rotation }
                ) {
                    val centerX = size.width / 2
                    val centerY = size.height / 2
                    val radius = size.width / 2

                    WHEEL_LAYOUT.forEachIndexed { index, segment ->
                        val startAngle = index * SLICE_ANGLE - 90f
                        drawArc(
                            color = segment.color,
                            startAngle = startAngle,
                            sweepAngle = SLICE_ANGLE,
                            useCenter = true,
                            topLeft = Offset.Zero,
                            size = size
                        )
                        drawArc(
                            color = Color(0x44000000),
                            startAngle = startAngle,
                            sweepAngle = SLICE_ANGLE,
                            useCenter = true,
                            topLeft = Offset.Zero,
                            size = size,
                            style = Stroke(width = 2f)
                        )

                        val labelAngle = Math.toRadians((startAngle + SLICE_ANGLE / 2).toDouble())
                        val labelRadius = radius * 0.65f
                        val lx = centerX + (labelRadius * kotlin.math.cos(labelAngle)).toFloat()
                        val ly = centerY + (labelRadius * kotlin.math.sin(labelAngle)).toFloat()

                        drawContext.canvas.nativeCanvas.apply {
                            val paint = android.graphics.Paint().apply {
                                color = android.graphics.Color.WHITE
                                textSize = 32f
                                textAlign = android.graphics.Paint.Align.CENTER
                                isFakeBoldText = true
                                isAntiAlias = true
                                setShadowLayer(4f, 1f, 2f, android.graphics.Color.BLACK)
                            }
                            drawText(segment.label, lx, ly + 10f, paint)
                        }
                    }

                    drawCircle(
                        brush = Brush.radialGradient(
                            listOf(JokerGold, JokerGoldDark, Color(0xFF8B6914))
                        ),
                        radius = radius * 0.15f,
                        center = Offset(centerX, centerY)
                    )
                    drawCircle(
                        color = Color(0x44000000),
                        radius = radius * 0.15f,
                        center = Offset(centerX, centerY),
                        style = Stroke(width = 2f)
                    )

                    drawCircle(
                        brush = Brush.sweepGradient(
                            listOf(JokerGold, JokerGoldDark, JokerGold, JokerOrange, JokerGold)
                        ),
                        radius = radius,
                        center = Offset(centerX, centerY),
                        style = Stroke(width = 8f)
                    )
                }

                Canvas(
                    modifier = Modifier
                        .size(30.dp, 36.dp)
                        .align(Alignment.TopCenter)
                        .offset(y = pointerBob.dp + (-10).dp)
                ) {
                    val path = androidx.compose.ui.graphics.Path().apply {
                        moveTo(size.width / 2, size.height)
                        lineTo(0f, 0f)
                        lineTo(size.width, 0f)
                        close()
                    }
                    drawPath(
                        path = path,
                        brush = Brush.verticalGradient(
                            listOf(JokerGold, JokerOrange, Color(0xFFCC6600))
                        )
                    )
                    drawPath(
                        path = path,
                        color = Color(0xFF8B6914),
                        style = Stroke(width = 2f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.4f)
            ) {
                when {
                    !hasSpun -> {
                        val spinInteraction = remember { MutableInteractionSource() }
                        val spinPressed by spinInteraction.collectIsPressedAsState()

                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .graphicsLayer {
                                    scaleX = if (spinPressed) 0.95f else 1f
                                    scaleY = if (spinPressed) 0.95f else 1f
                                }
                                .clickable(
                                    interactionSource = spinInteraction,
                                    indication = null
                                ) {
                                    if (!isSpinning) {
                                        angularVelocity = 15f + Random.nextFloat() * 10f
                                        isSpinning = true
                                        hasSpun = true
                                        spinCount++
                                    }
                                }
                        ) {
                            Canvas(
                                modifier = Modifier
                                    .fillMaxWidth(0.6f)
                                    .height(60.dp)
                            ) {
                                val cornerRad = CornerRadius(size.height / 2, size.height / 2)
                                drawRoundRect(
                                    color = Color(0xFF0D001A),
                                    topLeft = Offset(0f, 6f),
                                    size = size,
                                    cornerRadius = cornerRad
                                )
                                drawRoundRect(
                                    brush = Brush.verticalGradient(
                                        listOf(Color(0xFFFFE566), JokerGold, JokerGoldDark)
                                    ),
                                    cornerRadius = cornerRad
                                )
                                drawRoundRect(
                                    brush = Brush.verticalGradient(
                                        listOf(Color(0xFF180028), Color(0xFF2A0048))
                                    ),
                                    topLeft = Offset(3.5f, 3.5f),
                                    size = Size(size.width - 7f, size.height - 7f),
                                    cornerRadius = CornerRadius(
                                        (size.height - 7f) / 2,
                                        (size.height - 7f) / 2
                                    )
                                )
                                drawRoundRect(
                                    brush = Brush.verticalGradient(
                                        listOf(Color(0xFFB44FFF), JokerPurple, JokerPurpleDeep)
                                    ),
                                    topLeft = Offset(5f, 5f),
                                    size = Size(size.width - 10f, size.height - 10f),
                                    cornerRadius = CornerRadius(
                                        (size.height - 10f) / 2,
                                        (size.height - 10f) / 2
                                    )
                                )
                                drawRoundRect(
                                    brush = Brush.verticalGradient(
                                        listOf(Color.White.copy(alpha = 0.3f), Color.Transparent),
                                        endY = size.height * 0.5f
                                    ),
                                    topLeft = Offset(8f, 5f),
                                    size = Size(size.width - 16f, (size.height - 10f) * 0.4f),
                                    cornerRadius = CornerRadius(size.height / 2, size.height / 2)
                                )
                            }
                            Text(
                                text = "SPIN",
                                style = TextStyle(
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 6.sp,
                                    brush = Brush.verticalGradient(
                                        listOf(Color(0xFFFFE566), JokerGold, JokerGoldDark)
                                    ),
                                    shadow = Shadow(
                                        color = Color(0xCC000000),
                                        offset = Offset(2f, 3f),
                                        blurRadius = 4f
                                    )
                                )
                            )
                        }
                    }

                    isSpinning -> {
                        Text(
                            text = "Spinning...",
                            style = TextStyle(
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFCCBBDD)
                            )
                        )
                    }

                    showResult && resultMultiplier != null -> {
                        val mult = resultMultiplier!!

                        val resultColor = when {
                            mult >= 5f -> JokerGold
                            mult >= 3f -> Color(0xFF9B30FF)
                            mult >= 2f -> Color(0xFF4488FF)
                            mult >= 1f -> Color.White
                            else -> Color(0xFFFF3344)
                        }
                        val resultText = when {
                            mult >= 5f -> "JACKPOT!"
                            mult >= 3f -> "GREAT!"
                            mult >= 2f -> "NICE!"
                            mult >= 1f -> "OK"
                            else -> "JOKER!"
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = resultText,
                                style = TextStyle(
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 3.sp,
                                    color = resultColor,
                                    shadow = Shadow(
                                        color = Color.Black,
                                        offset = Offset(2f, 3f),
                                        blurRadius = 6f
                                    )
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${mult}x MULTIPLIER",
                                style = TextStyle(
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 2.sp,
                                    brush = Brush.verticalGradient(
                                        listOf(JokerGold, JokerOrange)
                                    )
                                )
                            )
                            Spacer(modifier = Modifier.height(2.dp))

                            val bonusScore = (roundScore * (mult - 1f)).toInt()
                            val totalRoundScore = roundScore + bonusScore
                            Text(
                                text = "$roundScore x ${mult} = $totalRoundScore",
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFFCCBBDD)
                                )
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .fillMaxWidth(0.55f)
                                    .height(52.dp)
                                    .clickable { onMultiplierSelected(mult) }
                            ) {
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    val cr = CornerRadius(size.height / 2, size.height / 2)
                                    drawRoundRect(
                                        color = Color(0xFF0D001A),
                                        topLeft = Offset(0f, 5f),
                                        size = size,
                                        cornerRadius = cr
                                    )
                                    drawRoundRect(
                                        brush = Brush.verticalGradient(
                                            listOf(Color(0xFFFFE566), JokerGold, JokerGoldDark)
                                        ),
                                        cornerRadius = cr
                                    )
                                    drawRoundRect(
                                        brush = Brush.verticalGradient(
                                            listOf(Color(0xFF180028), Color(0xFF2A0048))
                                        ),
                                        topLeft = Offset(3f, 3f),
                                        size = Size(size.width - 6f, size.height - 6f),
                                        cornerRadius = CornerRadius(
                                            (size.height - 6f) / 2,
                                            (size.height - 6f) / 2
                                        )
                                    )
                                    drawRoundRect(
                                        brush = Brush.verticalGradient(
                                            listOf(Color(0xFFB44FFF), JokerPurple, JokerPurpleDeep)
                                        ),
                                        topLeft = Offset(5f, 5f),
                                        size = Size(size.width - 10f, size.height - 10f),
                                        cornerRadius = CornerRadius(
                                            (size.height - 10f) / 2,
                                            (size.height - 10f) / 2
                                        )
                                    )
                                    drawRoundRect(
                                        brush = Brush.verticalGradient(
                                            listOf(
                                                Color.White.copy(alpha = 0.3f),
                                                Color.Transparent
                                            ),
                                            endY = size.height * 0.5f
                                        ),
                                        topLeft = Offset(8f, 5f),
                                        size = Size(size.width - 16f, (size.height - 10f) * 0.4f),
                                        cornerRadius = CornerRadius(
                                            size.height / 2,
                                            size.height / 2
                                        )
                                    )
                                }
                                Text(
                                    text = "CONTINUE",
                                    style = TextStyle(
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 4.sp,
                                        brush = Brush.verticalGradient(
                                            listOf(Color(0xFFFFE566), JokerGold, JokerGoldDark)
                                        ),
                                        shadow = Shadow(
                                            color = Color(0xCC000000),
                                            offset = Offset(1f, 2f),
                                            blurRadius = 3f
                                        )
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
