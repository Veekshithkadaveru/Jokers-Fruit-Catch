package app.krafted.jokersfruitcatch.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import app.krafted.jokersfruitcatch.game.GameView
import app.krafted.jokersfruitcatch.viewmodel.GamePhase
import app.krafted.jokersfruitcatch.viewmodel.GameViewModel

@Composable
fun GameScreen(viewModel: GameViewModel = viewModel()) {
    val score by viewModel.score.collectAsState()
    val lives by viewModel.lives.collectAsState()
    val round by viewModel.round.collectAsState()
    val gamePhase by viewModel.gamePhase.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { context ->
                GameView(context).apply {
                    onFruitCaught = { type -> viewModel.onFruitCaught(type) }
                    onFruitMissed = { viewModel.onFruitMissed() }
                    onBombCaught = { viewModel.onBombCaught() }
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        GameHUD(
            score = score,
            lives = lives,
            round = round,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(top = 48.dp, start = 16.dp, end = 16.dp)
        )

        if (gamePhase == GamePhase.GAME_OVER) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "GAME OVER",
                    color = Color.Red,
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun GameHUD(
    score: Int,
    lives: Int,
    round: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Round $round",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Score: $score",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "\u2764\uFE0F".repeat(lives),
            fontSize = 20.sp
        )
    }
}
