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

class GameViewModel : ViewModel() {

    companion object {
        private const val INITIAL_LIVES = 3
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
        _gamePhase.value = GamePhase.PLAYING
    }
}
