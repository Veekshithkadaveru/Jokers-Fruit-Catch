package app.krafted.jokersfruitcatch.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.krafted.jokersfruitcatch.R
import app.krafted.jokersfruitcatch.data.HighScore
import app.krafted.jokersfruitcatch.ui.theme.JokerGold
import app.krafted.jokersfruitcatch.ui.theme.JokerGoldDark
import app.krafted.jokersfruitcatch.ui.theme.JokerOrange
import app.krafted.jokersfruitcatch.ui.theme.JokerPink
import app.krafted.jokersfruitcatch.ui.theme.JokerPurple
import app.krafted.jokersfruitcatch.ui.theme.JokerPurpleDeep

@Composable
fun ResultScreen(
    roundScore: Int,
    totalScore: Int,
    multiplier: Float,
    isGameOver: Boolean,
    onNextRoundClick: () -> Unit,
    onSaveScoreClick: (HighScore) -> Unit,
    onMainMenuClick: () -> Unit,
    onPlayAgainClick: () -> Unit
) {

    BackHandler(enabled = true) { /* consume back press */ }

    var showSaveDialog by remember { mutableStateOf(false) }
    var playerName by remember { mutableStateOf("") }
    var scoreSaved by remember { mutableStateOf(false) }
    
    // Determine Joker Girl's reaction based on the multiplier and game state
    val reactionImageRes = if (isGameOver || multiplier < 1.0f) {
        R.drawable.joker_girl_sad
    } else if (multiplier >= 2.0f) {
        R.drawable.joker_girl_happy
    } else {
        R.drawable.joker_girl_neutral
    }

    val reactionText = when {
        isGameOver -> "GAME OVER! BETTER LUCK NEXT TIME!"
        multiplier >= 5.0f -> "JACKPOT! AMAZING SPIN!"
        multiplier >= 2.0f -> "GREAT JOB! BONUS SECURED!"
        multiplier < 1.0f -> "OH NO! THE JOKER GOT YOU!"
        else -> "STEADY PROGRESS!"
    }

    val headerText = if (isGameOver) "GAME OVER" else "ROUND COMPLETE"
    val headerColor = if (isGameOver) JokerPink else Color(0xFF00FF88)

    // Background animation
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
        // Grid overlay
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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            
            // Header Text with Glow
            Text(
                text = headerText,
                style = TextStyle(
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp,
                    color = headerColor,
                    shadow = Shadow(
                        color = headerColor.copy(alpha = 0.6f),
                        blurRadius = 20f
                    )
                ),
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Mascot Image Container
            Box(
                modifier = Modifier
                    .size(220.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.4f))
                    .border(
                        width = 4.dp,
                        brush = Brush.sweepGradient(
                            listOf(
                                JokerPink,
                                JokerPurple,
                                Color(0xFF00FF88),
                                JokerPink
                            )
                        ),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = reactionImageRes),
                    contentDescription = "Joker Girl Reaction",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp)
                        .clip(CircleShape)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = reactionText,
                style = TextStyle(
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = JokerGold,
                    textAlign = TextAlign.Center,
                    shadow = Shadow(
                        color = JokerGoldDark,
                        blurRadius = 15f
                    )
                )
            )

            Spacer(modifier = Modifier.height(36.dp))

            // Score Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 2.dp,
                        color = JokerPurple.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(16.dp)
                    ),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF150029).copy(alpha = 0.8f)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ScoreRow("Current Round", roundScore.toString(), Color.White)
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    val multiplierColor = if (multiplier >= 2.0f) Color(0xFF00FF88)
                                          else if (multiplier < 1.0f) JokerPink
                                          else Color.White
                    ScoreRow("Wheel Multiplier", "${multiplier}x", multiplierColor)
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color.Transparent, JokerPurple, Color.Transparent)
                                )
                            )
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "TOTAL SCORE",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = JokerGold.copy(alpha = 0.7f),
                        letterSpacing = 2.sp
                    )
                    
                    Text(
                        text = totalScore.toString(),
                        style = TextStyle(
                            fontSize = 54.sp,
                            fontWeight = FontWeight.Black,
                            brush = Brush.verticalGradient(
                                listOf(JokerGold, JokerOrange, JokerGoldDark)
                            ),
                            shadow = Shadow(
                                color = JokerOrange.copy(alpha = 0.5f),
                                blurRadius = 15f
                            )
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Action Buttons
            if (isGameOver) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        NeonButton(
                            text = if (scoreSaved) "SCORE SAVED" else "SAVE SCORE",
                            onClick = { if (!scoreSaved) showSaveDialog = true },
                            color = if (scoreSaved) Color.Gray else Color(0xFF00FF88),
                            modifier = Modifier.weight(1f)
                        )
                        
                        Spacer(modifier = Modifier.width(16.dp))
                        
                        NeonButton(
                            text = "PLAY AGAIN",
                            onClick = onPlayAgainClick,
                            color = JokerGold,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    NeonButton(
                        text = "MAIN MENU",
                        onClick = onMainMenuClick,
                        color = JokerPink,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            } else {
                NeonButton(
                    text = "NEXT ROUND",
                    onClick = onNextRoundClick,
                    color = Color(0xFF00FF88),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    if (showSaveDialog) {
        AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            containerColor = Color(0xFF150029),
            titleContentColor = JokerGold,
            textContentColor = Color.White,
            title = { 
                Text(
                    text = "SAVE SCORE", 
                    fontWeight = FontWeight.Black,
                    fontSize = 24.sp
                ) 
            },
            text = {
                Column {
                    Text("Enter name for a score of $totalScore:", color = Color.LightGray)
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = playerName,
                        onValueChange = { if (it.length <= 12) playerName = it },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.LightGray,
                            focusedBorderColor = JokerGold,
                            unfocusedBorderColor = JokerPurple,
                            cursorColor = JokerGold
                        )
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val finalName = if (playerName.isBlank()) "Anonymous" else playerName
                        val newScore = HighScore(playerName = finalName, score = totalScore)
                        onSaveScoreClick(newScore)
                        scoreSaved = true
                        showSaveDialog = false
                    }
                ) {
                    Text("SAVE", color = JokerGold, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            },
            dismissButton = {
                TextButton(onClick = { showSaveDialog = false }) {
                    Text("CANCEL", color = JokerPink, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            }
        )
    }
}

@Composable
fun ScoreRow(label: String, value: String, valueColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label, 
            fontSize = 18.sp, 
            color = Color.LightGray,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = value, 
            fontSize = 26.sp, 
            fontWeight = FontWeight.Black, 
            color = valueColor,
            style = TextStyle(
                shadow = Shadow(color = valueColor.copy(alpha = 0.5f), blurRadius = 10f)
            )
        )
    }
}

@Composable
fun NeonButton(
    text: String,
    onClick: () -> Unit,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(64.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.15f))
            .border(2.dp, color, RoundedCornerShape(8.dp))
            .semantics { role = Role.Button }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp,
                color = color,
                shadow = Shadow(
                    color = color.copy(alpha = 0.8f),
                    blurRadius = 15f
                )
            )
        )
    }
}
