package app.krafted.jokersfruitcatch.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.krafted.jokersfruitcatch.data.HighScore
import kotlinx.coroutines.flow.Flow
import app.krafted.jokersfruitcatch.ui.theme.JokerGold
import app.krafted.jokersfruitcatch.ui.theme.JokerGoldDark
import app.krafted.jokersfruitcatch.ui.theme.JokerPink
import app.krafted.jokersfruitcatch.ui.theme.JokerPurple
import app.krafted.jokersfruitcatch.ui.theme.JokerPurpleDeep
import app.krafted.jokersfruitcatch.ui.theme.JokerOrange

@Composable
fun LeaderboardScreen(
    topScoresFlow: Flow<List<HighScore>>,
    onBackClick: () -> Unit
) {
    val scores by topScoresFlow.collectAsStateWithLifecycle(initialValue = emptyList())

    val infiniteTransition = rememberInfiniteTransition(label = "bgAnim")
    val bgOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "bgSlide"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF1A0033),
                        JokerPurpleDeep,
                        Color(0xFF0F001A)
                    )
                )
            )
    ) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { alpha = 0.1f }
                .drawWithContent {
                    drawContent()
                    val gridSize = 60f
                    for (x in 0..(size.width / gridSize).toInt() + 1) {
                        drawLine(
                            color = JokerPurple,
                            start = Offset(x * gridSize, 0f),
                            end = Offset(x * gridSize, size.height),
                            strokeWidth = 2f
                        )
                    }
                    for (y in 0..(size.height / gridSize).toInt() + 1) {
                        val yPos = (y * gridSize + bgOffset) % size.height
                        drawLine(
                            color = JokerPink,
                            start = Offset(0f, yPos),
                            end = Offset(size.width, yPos),
                            strokeWidth = 2f
                        )
                    }
                }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            
            Spacer(modifier = Modifier.height(32.dp))

            // Header Text with Glow
            Text(
                text = "TOP RANKS",
                style = TextStyle(
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 4.sp,
                    color = JokerGold,
                    shadow = Shadow(
                        color = JokerGoldDark.copy(alpha = 0.8f),
                        blurRadius = 25f
                    )
                ),
                modifier = Modifier.padding(bottom = 24.dp)
            )

            if (scores.isEmpty()) {
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "NO HIGH SCORES YET\nPLAY A GAME TO RANK UP!",
                        style = TextStyle(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.LightGray,
                            textAlign = TextAlign.Center,
                            letterSpacing = 2.sp
                        )
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    itemsIndexed(scores, key = { _, item -> item.id }) { index, score ->
                        ScoreItem(rank = index + 1, highScore = score)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(JokerPink.copy(alpha = 0.15f))
                    .border(2.dp, JokerPink, RoundedCornerShape(8.dp))
                    .clickable { onBackClick() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "BACK TO START",
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp,
                        color = JokerPink,
                        shadow = Shadow(
                            color = JokerPink.copy(alpha = 0.8f),
                            blurRadius = 15f
                        )
                    )
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun ScoreItem(rank: Int, highScore: HighScore) {
    val isTop3 = rank <= 3

    val rankColor = when (rank) {
        1 -> JokerGold
        2 -> Color(0xFFE0E0E0)
        3 -> JokerOrange
        else -> Color(0xFF00FF88)
    }

    val containerBg = if (isTop3) {
        Brush.horizontalGradient(
            listOf(
                Color(0xFF150029).copy(alpha = 0.9f),
                rankColor.copy(alpha = 0.15f),
                Color(0xFF150029).copy(alpha = 0.9f)
            )
        )
    } else {
        Brush.horizontalGradient(
            listOf(
                Color(0xFF150029).copy(alpha = 0.7f),
                Color(0xFF150029).copy(alpha = 0.7f)
            )
        )
    }

    val borderWidth = if (isTop3) 2.dp else 1.dp
    val borderColor = if (isTop3) rankColor else JokerPurple.copy(alpha = 0.5f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .background(containerBg, RoundedCornerShape(12.dp))
            .border(borderWidth, borderColor, RoundedCornerShape(12.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rank
            Text(
                text = "#$rank",
                style = TextStyle(
                    fontSize = if (rank == 1) 32.sp else 24.sp,
                    fontWeight = FontWeight.Black,
                    color = rankColor,
                    shadow = Shadow(
                        color = rankColor.copy(alpha = 0.5f),
                        blurRadius = 10f
                    )
                ),
                modifier = Modifier.weight(0.2f)
            )

            // Name
            Text(
                text = highScore.playerName,
                style = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = 1.sp
                ),
                modifier = Modifier.weight(0.5f)
            )

            // Score
            Text(
                text = "${highScore.score}",
                style = TextStyle(
                    fontSize = if (rank == 1) 28.sp else 22.sp,
                    fontWeight = FontWeight.Black,
                    color = if (isTop3) JokerGold else Color.LightGray,
                    textAlign = TextAlign.End,
                    shadow = if (isTop3) Shadow(
                        color = JokerOrange.copy(alpha = 0.5f),
                        blurRadius = 10f
                    ) else null
                ),
                modifier = Modifier.weight(0.3f)
            )
        }
    }
}
