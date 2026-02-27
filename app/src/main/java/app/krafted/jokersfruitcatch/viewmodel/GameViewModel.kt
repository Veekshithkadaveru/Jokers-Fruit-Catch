package app.krafted.jokersfruitcatch.viewmodel

import androidx.lifecycle.ViewModel
import app.krafted.jokersfruitcatch.game.FruitType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class GamePhase {
    PLAYING,
    WHEEL,
    RESULT,
    GAME_OVER
}

data class DifficultyConfig(
    val speedMultiplier: Float,
    val bombChanceMultiplier: Float
)

class GameViewModel : ViewModel() {

    companion object {
        private const val INITIAL_LIVES = 3
        private const val MAX_SPEED_MULTIPLIER = 1.7f
        private const val SPEED_INCREMENT_PER_ROUND = 0.1f
        private const val BOMB_INTRO_ROUND = 3
        private const val MAX_DIFFICULTY_ROUND = 8
    }

    private val _score = MutableStateFlow(0)
    val score: StateFlow<Int> = _score.asStateFlow()

    private val _roundScore = MutableStateFlow(0)
    val roundScore: StateFlow<Int> = _roundScore.asStateFlow()

    private val _lives = MutableStateFlow(INITIAL_LIVES)
    val lives: StateFlow<Int> = _lives.asStateFlow()

    private val _round = MutableStateFlow(1)
    val round: StateFlow<Int> = _round.asStateFlow()

    private val _multiplier = MutableStateFlow(1f)
    val multiplier: StateFlow<Float> = _multiplier.asStateFlow()

    private val _gamePhase = MutableStateFlow(GamePhase.PLAYING)
    val gamePhase: StateFlow<GamePhase> = _gamePhase.asStateFlow()

    private val _difficultyConfig = MutableStateFlow(computeDifficulty(1))
    val difficultyConfig: StateFlow<DifficultyConfig> = _difficultyConfig.asStateFlow()

    fun onFruitCaught(type: FruitType) {
        if (_gamePhase.value != GamePhase.PLAYING) return
        val points = type.points
        _roundScore.value += points
        _score.value += points
    }

    fun onFruitMissed() {
        if (_gamePhase.value != GamePhase.PLAYING) return
        loseLife()
    }

    fun onBombCaught() {
        if (_gamePhase.value != GamePhase.PLAYING) return
        loseLife()
    }

    private fun loseLife() {
        val newLives = (_lives.value - 1).coerceAtLeast(0)
        _lives.value = newLives
        if (newLives <= 0) {
            _gamePhase.value = GamePhase.GAME_OVER
        }
    }

    fun applyMultiplier(multiplier: Float) {
        _multiplier.value = multiplier
        val bonus = (_roundScore.value * (multiplier - 1f)).toInt()
        _score.value += bonus
    }

    fun advanceRound() {
        _round.value += 1
        _roundScore.value = 0
        _multiplier.value = 1f
        _difficultyConfig.value = computeDifficulty(_round.value)
        _gamePhase.value = GamePhase.PLAYING
    }

    fun setGamePhase(phase: GamePhase) {
        _gamePhase.value = phase
    }

    fun resetGame() {
        _score.value = 0
        _roundScore.value = 0
        _lives.value = INITIAL_LIVES
        _round.value = 1
        _multiplier.value = 1f
        _difficultyConfig.value = computeDifficulty(1)
        _gamePhase.value = GamePhase.PLAYING
    }

    private fun computeDifficulty(round: Int): DifficultyConfig {
        val effectiveRound = round.coerceAtMost(MAX_DIFFICULTY_ROUND)

        // Speed: +10% per round, starting at 1.0, capped at 1.7
        val speed = (1.0f + (effectiveRound - 1) * SPEED_INCREMENT_PER_ROUND)
            .coerceAtMost(MAX_SPEED_MULTIPLIER)

        // Bombs: disabled before round 3, then ramp up each round
        val bombMultiplier = if (round < BOMB_INTRO_ROUND) {
            0f
        } else {
            (round - BOMB_INTRO_ROUND + 1).toFloat()
                .coerceAtMost((MAX_DIFFICULTY_ROUND - BOMB_INTRO_ROUND + 1).toFloat())
        }

        return DifficultyConfig(
            speedMultiplier = speed,
            bombChanceMultiplier = bombMultiplier
        )
    }
}
