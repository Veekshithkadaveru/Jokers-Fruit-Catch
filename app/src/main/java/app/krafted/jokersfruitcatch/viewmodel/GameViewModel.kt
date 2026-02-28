package app.krafted.jokersfruitcatch.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import app.krafted.jokersfruitcatch.data.AppDatabase
import app.krafted.jokersfruitcatch.data.HighScore
import app.krafted.jokersfruitcatch.game.FruitType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class GamePhase {
    PLAYING,
    WHEEL,
    RESULT,
    GAME_OVER
}

data class DifficultyConfig(
    val speedMultiplier: Float,
    val bombChanceMultiplier: Float,
    val spawnIntervalMs: Long
)

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val highScoreDao = AppDatabase.getDatabase(application).highScoreDao()

    val topScores: Flow<List<HighScore>> = highScoreDao.getTopScores()
    val highestScore: Flow<Int?> = highScoreDao.getHighestScore()

    fun saveScore(highScore: HighScore) {
        viewModelScope.launch {
            highScoreDao.insert(highScore)
        }
    }

    companion object {
        private const val INITIAL_LIVES = 3
        private const val MAX_SPEED_MULTIPLIER = 2.5f
        private const val SPEED_INCREMENT_PER_ROUND = 0.15f
        private const val BOMB_INTRO_ROUND = 1
        private const val MAX_DIFFICULTY_ROUND = 10
        private const val BASE_SPAWN_INTERVAL_MS = 900L
        private const val MIN_SPAWN_INTERVAL_MS = 400L
        const val FRUITS_PER_ROUND = 25
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

    private val _roundFruitCount = MutableStateFlow(0)
    val roundFruitCount: StateFlow<Int> = _roundFruitCount.asStateFlow()

    fun onFruitCaught(type: FruitType) {
        if (_gamePhase.value != GamePhase.PLAYING) return
        val points = type.points
        _roundScore.value += points
        _score.value += points
        incrementRoundFruitCount()
    }

    fun onFruitMissed() {
        if (_gamePhase.value != GamePhase.PLAYING) return
        loseLife()
        incrementRoundFruitCount()
    }

    fun onBombCaught() {
        if (_gamePhase.value != GamePhase.PLAYING) return
        loseLife()
    }

    private fun incrementRoundFruitCount() {
        _roundFruitCount.value += 1
        if (_roundFruitCount.value >= FRUITS_PER_ROUND && _gamePhase.value == GamePhase.PLAYING) {
            _gamePhase.value = GamePhase.WHEEL
        }
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
        _roundFruitCount.value = 0
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
        _roundFruitCount.value = 0
        _lives.value = INITIAL_LIVES
        _round.value = 1
        _multiplier.value = 1f
        _difficultyConfig.value = computeDifficulty(1)
        _gamePhase.value = GamePhase.PLAYING
    }

    private fun computeDifficulty(round: Int): DifficultyConfig {
        val effectiveRound = round.coerceAtMost(MAX_DIFFICULTY_ROUND)

        val speed = (1.0f + (effectiveRound - 1) * SPEED_INCREMENT_PER_ROUND)
            .coerceAtMost(MAX_SPEED_MULTIPLIER)

        val bombMultiplier = if (round < BOMB_INTRO_ROUND) {
            0f
        } else {
            (1.0f + (round - BOMB_INTRO_ROUND) * 1.5f)
                .coerceAtMost(MAX_DIFFICULTY_ROUND.toFloat())
        }

        val spawnInterval = (BASE_SPAWN_INTERVAL_MS - (effectiveRound - 1) * 60L)
            .coerceAtLeast(MIN_SPAWN_INTERVAL_MS)

        return DifficultyConfig(
            speedMultiplier = speed,
            bombChanceMultiplier = bombMultiplier,
            spawnIntervalMs = spawnInterval
        )
    }
}
