package app.krafted.jokersfruitcatch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import app.krafted.jokersfruitcatch.ui.GameScreen
import app.krafted.jokersfruitcatch.ui.LeaderboardScreen
import app.krafted.jokersfruitcatch.ui.ResultScreen
import app.krafted.jokersfruitcatch.ui.SplashScreen
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
        startDestination = "splash"
    ) {
        composable("splash") {
            SplashScreen {
                navController.navigate("start") {
                    popUpTo("splash") { inclusive = true }
                    launchSingleTop = true
                }
            }
        }
        composable("start") {
            val highestScore by gameViewModel.highestScore.collectAsStateWithLifecycle(initialValue = 0)
            StartScreen(
                highScore = highestScore ?: 0,
                onPlayClick = {
                    gameViewModel.resetGame()
                    navController.navigate("game") {
                        popUpTo("start") { inclusive = false }
                        launchSingleTop = true
                    }
                },
                onLeaderboardClick = {
                    navController.navigate("leaderboard") { launchSingleTop = true }
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
                    navController.navigate("result") {
                        popUpTo("game") { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
        composable("wheel") {
            val roundScore by gameViewModel.roundScore.collectAsStateWithLifecycle()
            val round by gameViewModel.round.collectAsStateWithLifecycle()

            WheelScreen(
                roundScore = roundScore,
                round = round,
                onMultiplierSelected = { multiplier ->
                    gameViewModel.applyMultiplier(multiplier)
                    navController.navigate("result") {
                        popUpTo("game") { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
        composable("result") {
            val roundScore by gameViewModel.roundScore.collectAsStateWithLifecycle()
            val score by gameViewModel.score.collectAsStateWithLifecycle()
            val multiplier by gameViewModel.multiplier.collectAsStateWithLifecycle()
            val lives by gameViewModel.lives.collectAsStateWithLifecycle()
            val isGameOver = lives <= 0
            val highestScore by gameViewModel.highestScore.collectAsStateWithLifecycle(initialValue = 0)

            // Only trigger high score confetti if the player has actually played and beaten the top score
            val isNewHighScore = isGameOver && score > 0 && score > (highestScore ?: 0)

            ResultScreen(
                roundScore = roundScore,
                totalScore = score,
                multiplier = multiplier,
                isGameOver = isGameOver,
                isNewHighScore = isNewHighScore,
                onNextRoundClick = {
                    gameViewModel.advanceRound()
                    navController.navigate("game") {
                        popUpTo("start") { inclusive = false }
                        launchSingleTop = true
                    }
                },
                onSaveScoreClick = { newScore ->
                    gameViewModel.saveScore(newScore)
                },
                onMainMenuClick = {
                    navController.navigate("start") {
                        popUpTo("start") { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onPlayAgainClick = {
                    gameViewModel.resetGame()
                    navController.navigate("game") {
                        popUpTo("start") { inclusive = false }
                        launchSingleTop = true
                    }
                }
            )
        }
        composable("leaderboard") {
            LeaderboardScreen(
                topScoresFlow = gameViewModel.topScores,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}
