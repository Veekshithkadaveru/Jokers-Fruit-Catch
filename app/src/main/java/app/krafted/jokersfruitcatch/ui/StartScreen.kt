package app.krafted.jokersfruitcatch.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.krafted.jokersfruitcatch.R
import app.krafted.jokersfruitcatch.ui.theme.JokerGold
import app.krafted.jokersfruitcatch.ui.theme.JokerGoldDark
import app.krafted.jokersfruitcatch.ui.theme.JokerOrange
import app.krafted.jokersfruitcatch.ui.theme.JokerPink
import app.krafted.jokersfruitcatch.ui.theme.JokerPurple
import app.krafted.jokersfruitcatch.ui.theme.JokerPurpleDeep

@Composable
fun StartScreen(
    highScore: Int = 0,
    onPlayClick: () -> Unit
) {

    val mascotScale = remember { Animatable(0f) }
    val titleScale = remember { Animatable(0f) }
    val titleAlpha = remember { Animatable(0f) }
    val buttonsAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        mascotScale.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing)
        )
        titleScale.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing)
        )
        titleAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 400)
        )
        buttonsAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 500)
        )
    }

    // ── Looping animations ──
    val infiniteTransition = rememberInfiniteTransition(label = "startScreen")

    val mascotBob by infiniteTransition.animateFloat(
        initialValue = -6f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "mascotBob"
    )

    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    val playButtonScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "playPulse"
    )

    val shimmerOffset by infiniteTransition.animateFloat(
        initialValue = -1f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer"
    )

    val titleGlow by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "titleGlow"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        // ── Full-screen background ──
        Image(
            painter = painterResource(id = R.drawable.start_background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.weight(0.1f))

            // ── Joker Girl Mascot ──
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .scale(mascotScale.value)
                    .offset(y = mascotBob.dp)
            ) {
                // Outer glow ring
                Box(
                    modifier = Modifier
                        .size(220.dp)
                        .alpha(glowAlpha)
                        .border(
                            width = 3.dp,
                            brush = Brush.sweepGradient(
                                listOf(JokerGold, JokerOrange, JokerPink, JokerGold)
                            ),
                            shape = CircleShape
                        )
                )
                Image(
                    painter = painterResource(id = R.drawable.joker_girl),
                    contentDescription = "Joker Girl",
                    modifier = Modifier
                        .size(200.dp)
                        .clip(CircleShape)
                        .border(2.dp, JokerGold, CircleShape),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .graphicsLayer {
                        scaleX = titleScale.value
                        scaleY = titleScale.value
                    }
                    .alpha(titleAlpha.value)
            ) {
                // Banner background shape
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth(0.92f)
                        .height(130.dp)
                ) {
                    val bannerBrush = Brush.verticalGradient(
                        listOf(
                            Color(0xCC1A0030),
                            Color(0xDD2D004D),
                            Color(0xCC1A0030)
                        )
                    )
                    drawRoundRect(
                        brush = bannerBrush,
                        cornerRadius = CornerRadius(24f, 24f)
                    )
                    // Gold border
                    drawRoundRect(
                        brush = Brush.verticalGradient(
                            listOf(
                                JokerGold.copy(alpha = titleGlow * 0.9f),
                                JokerOrange.copy(alpha = titleGlow * 0.7f),
                                JokerGold.copy(alpha = titleGlow * 0.9f)
                            )
                        ),
                        cornerRadius = CornerRadius(24f, 24f),
                        style = Stroke(width = 3f)
                    )
                    // Inner highlight line
                    drawRoundRect(
                        brush = Brush.verticalGradient(
                            listOf(
                                Color.White.copy(alpha = 0.15f),
                                Color.Transparent,
                                Color.Transparent
                            )
                        ),
                        topLeft = Offset(6f, 6f),
                        size = Size(size.width - 12f, size.height - 12f),
                        cornerRadius = CornerRadius(20f, 20f)
                    )
                }

                // Title text stack
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(vertical = 16.dp)
                ) {
                    Text(
                        text = "JOKER'S",
                        style = TextStyle(
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 5.sp,
                            brush = Brush.verticalGradient(
                                listOf(
                                    Color(0xFFFFE566),
                                    JokerGold,
                                    JokerOrange,
                                    JokerGoldDark
                                )
                            ),
                            shadow = Shadow(
                                color = Color(0xFF000000),
                                offset = Offset(3f, 4f),
                                blurRadius = 6f
                            )
                        ),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "FRUIT CATCH",
                        style = TextStyle(
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 8.sp,
                            brush = Brush.verticalGradient(
                                listOf(
                                    Color.White,
                                    Color(0xFFE8E0F0),
                                    Color(0xFFD0C0E8)
                                )
                            ),
                            shadow = Shadow(
                                color = Color(0xCC000000),
                                offset = Offset(2f, 3f),
                                blurRadius = 5f
                            )
                        ),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // ── High Score badge ──
            if (highScore > 0) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .alpha(titleAlpha.value)
                        .drawBehind {
                            drawRoundRect(
                                color = Color(0x66000000),
                                cornerRadius = CornerRadius(40f, 40f)
                            )
                            drawRoundRect(
                                brush = Brush.horizontalGradient(
                                    listOf(
                                        JokerGold.copy(alpha = 0.5f),
                                        JokerOrange.copy(alpha = 0.5f)
                                    )
                                ),
                                cornerRadius = CornerRadius(40f, 40f),
                                style = Stroke(width = 1.5f)
                            )
                        }
                        .padding(horizontal = 24.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "BEST",
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp,
                            color = Color(0xFFCCBBDD)
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "$highScore",
                        style = TextStyle(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp,
                            color = JokerGold
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.weight(0.08f))

            val interactionSource = remember { MutableInteractionSource() }
            val isPressed by interactionSource.collectIsPressedAsState()
            val pressYOffset = if (isPressed) 4f else 0f
            val pressShadowShrink = if (isPressed) 2f else 8f

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .alpha(buttonsAlpha.value)
                    .graphicsLayer {
                        scaleX = playButtonScale
                        scaleY = playButtonScale
                    }
            ) {
                // Outer glow layer (behind everything)
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth(0.78f)
                        .height(90.dp)
                ) {
                    // Diffuse colored glow
                    drawRoundRect(
                        brush = Brush.radialGradient(
                            listOf(
                                JokerGold.copy(alpha = glowAlpha * 0.3f),
                                JokerOrange.copy(alpha = glowAlpha * 0.15f),
                                Color.Transparent
                            ),
                            center = Offset(size.width / 2, size.height / 2),
                            radius = size.width * 0.7f
                        ),
                        cornerRadius = CornerRadius(50f, 50f)
                    )
                }

                // Clickable button stack
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth(0.72f)
                        .height(76.dp)
                        .graphicsLayer {
                            translationY = pressYOffset
                        }
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null,
                            onClick = onPlayClick
                        )
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val w = size.width
                        val h = size.height
                        val outerRadius = CornerRadius(h / 2, h / 2)
                        val innerInset = 5f
                        val innerRadius = CornerRadius((h - innerInset * 2) / 2, (h - innerInset * 2) / 2)

                        drawRoundRect(
                            color = Color(0xFF0D001A),
                            topLeft = Offset(0f, pressShadowShrink),
                            size = Size(w, h),
                            cornerRadius = outerRadius
                        )

                        drawRoundRect(
                            brush = Brush.verticalGradient(
                                listOf(
                                    Color(0xFFFFE566),
                                    JokerGold,
                                    JokerGoldDark,
                                    Color(0xFF8B6914)
                                )
                            ),
                            cornerRadius = outerRadius
                        )

                        drawRoundRect(
                            brush = Brush.verticalGradient(
                                listOf(
                                    Color(0xFF180028),
                                    Color(0xFF2A0048)
                                )
                            ),
                            topLeft = Offset(3.5f, 3.5f),
                            size = Size(w - 7f, h - 7f),
                            cornerRadius = CornerRadius((h - 7f) / 2, (h - 7f) / 2)
                        )

                        drawRoundRect(
                            brush = Brush.verticalGradient(
                                listOf(
                                    Color(0xFFB44FFF),
                                    Color(0xFF8B2FC8),
                                    JokerPurple,
                                    Color(0xFF5A0B96),
                                    JokerPurpleDeep
                                )
                            ),
                            topLeft = Offset(innerInset, innerInset),
                            size = Size(w - innerInset * 2, h - innerInset * 2),
                            cornerRadius = innerRadius
                        )

                        drawRoundRect(
                            brush = Brush.verticalGradient(
                                listOf(
                                    Color.White.copy(alpha = 0.35f),
                                    Color.White.copy(alpha = 0.08f),
                                    Color.Transparent
                                ),
                                startY = innerInset,
                                endY = h * 0.55f
                            ),
                            topLeft = Offset(innerInset + 4f, innerInset + 1f),
                            size = Size(w - innerInset * 2 - 8f, (h - innerInset * 2) * 0.45f),
                            cornerRadius = CornerRadius(innerRadius.x - 2f, innerRadius.y - 2f)
                        )

                        drawRoundRect(
                            brush = Brush.verticalGradient(
                                listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.25f)
                                ),
                                startY = h * 0.65f,
                                endY = h - innerInset
                            ),
                            topLeft = Offset(innerInset, h * 0.65f),
                            size = Size(w - innerInset * 2, h * 0.35f - innerInset),
                            cornerRadius = innerRadius
                        )

                        drawRoundRect(
                            brush = Brush.horizontalGradient(
                                listOf(
                                    Color.Transparent,
                                    JokerGold.copy(alpha = 0.4f),
                                    JokerGold.copy(alpha = 0.6f),
                                    JokerGold.copy(alpha = 0.4f),
                                    Color.Transparent
                                )
                            ),
                            topLeft = Offset(innerInset + 20f, innerInset),
                            size = Size(w - innerInset * 2 - 40f, 2f),
                            cornerRadius = CornerRadius(1f, 1f)
                        )

                        val shimmerX = shimmerOffset * w
                        drawRoundRect(
                            brush = Brush.horizontalGradient(
                                listOf(
                                    Color.Transparent,
                                    Color.White.copy(alpha = 0.06f),
                                    Color.White.copy(alpha = 0.2f),
                                    Color.White.copy(alpha = 0.06f),
                                    Color.Transparent
                                ),
                                startX = shimmerX - 100f,
                                endX = shimmerX + 100f
                            ),
                            topLeft = Offset(innerInset, innerInset),
                            size = Size(w - innerInset * 2, h - innerInset * 2),
                            cornerRadius = innerRadius
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        // Play triangle icon
                        Canvas(modifier = Modifier.size(22.dp)) {
                            val path = androidx.compose.ui.graphics.Path().apply {
                                moveTo(size.width * 0.15f, size.height * 0.1f)
                                lineTo(size.width * 0.95f, size.height * 0.5f)
                                lineTo(size.width * 0.15f, size.height * 0.9f)
                                close()
                            }
                            // Shadow
                            drawPath(
                                path = path,
                                color = Color(0x88000000)
                            )
                            // Gold fill
                            drawPath(
                                path = path,
                                brush = Brush.verticalGradient(
                                    listOf(
                                        Color(0xFFFFE566),
                                        JokerGold,
                                        JokerGoldDark
                                    )
                                )
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "PLAY",
                            style = TextStyle(
                                fontSize = 30.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 8.sp,
                                brush = Brush.verticalGradient(
                                    listOf(
                                        Color(0xFFFFE566),
                                        JokerGold,
                                        JokerOrange,
                                        JokerGoldDark
                                    )
                                ),
                                shadow = Shadow(
                                    color = Color(0xDD000000),
                                    offset = Offset(2f, 3f),
                                    blurRadius = 5f
                                )
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(0.15f))
        }
    }
}
