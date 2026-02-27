package app.krafted.jokersfruitcatch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import app.krafted.jokersfruitcatch.ui.GameScreen
import app.krafted.jokersfruitcatch.ui.StartScreen
import app.krafted.jokersfruitcatch.ui.WheelScreen
import app.krafted.jokersfruitcatch.ui.theme.JokersFruitCatchTheme
import app.krafted.jokersfruitcatch.viewmodel.GameViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JokersFruitCatchTheme {
                JokersFruitCatchApp()
            }
        }
    }
}

@Composable
fun JokersFruitCatchApp() {
    val navController = rememberNavController()
    val gameViewModel: GameViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "start"
    ) {
        composable("start") {
            StartScreen(
                highScore = 0,
                onPlayClick = {
                    gameViewModel.resetGame()
                    navController.navigate("game") {
                        popUpTo("start") { inclusive = false }
                        launchSingleTop = true
                    }
                }
            )
        }
        composable("game") {
            GameScreen(
                viewModel = gameViewModel,
                onRoundEnd = {
                    navController.navigate("wheel") {
                        launchSingleTop = true
                    }
                },
                onGameOver = {
                    navController.navigate("start") {
                        popUpTo("start") { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
        composable("wheel") {
            val roundScore by gameViewModel.roundScore.collectAsState()
            val round by gameViewModel.round.collectAsState()

            WheelScreen(
                roundScore = roundScore,
                round = round,
                onMultiplierSelected = { multiplier ->
                    gameViewModel.applyMultiplier(multiplier)
                    gameViewModel.advanceRound()
                    navController.navigate("game") {
                        popUpTo("start") { inclusive = false }
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}
