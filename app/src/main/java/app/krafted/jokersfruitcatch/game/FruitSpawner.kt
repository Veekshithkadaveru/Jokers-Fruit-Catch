package app.krafted.jokersfruitcatch.game

import kotlin.random.Random

class FruitSpawner(var screenWidth: Int, var fruitSize: Float = 150f) {
    private val _activeFruits = mutableListOf<Fruit>()
    private val lock = Any()

    val activeFruits: List<Fruit> get() = synchronized(lock) { _activeFruits.toList() }
    private var lastSpawnTime = 0L
    var spawnIntervalMs = 900L
    private var baseSpeed = 12f

    var speedMultiplier = 1.0f
    var bombChanceMultiplier = 1.0f
    @Volatile var paused = false

    fun update() {
        if (paused) return

        val currentTime = System.currentTimeMillis()
        synchronized(lock) {
            if (currentTime - lastSpawnTime > spawnIntervalMs) {
                spawnFruit()
                lastSpawnTime = currentTime
            }

            for (fruit in _activeFruits) {
                fruit.y += fruit.speed * speedMultiplier
            }
        }
    }

    fun clearAllFruits() {
        synchronized(lock) {
            _activeFruits.clear()
        }
    }

    fun removeOffScreenFruits(screenHeight: Int) {
        synchronized(lock) {
            _activeFruits.removeAll { it.y > screenHeight.toFloat() }
        }
    }

    fun removeFruits(fruits: Collection<Fruit>) {
        synchronized(lock) {
            _activeFruits.removeAll(fruits.toSet())
        }
    }

    private fun spawnFruit() {
        if (screenWidth <= 0) return

        val fruitType = getRandomFruitType()
        val x = Random.nextFloat() * (screenWidth - fruitSize).coerceAtLeast(0f)
        val speed = baseSpeed + Random.nextFloat() * 5f

        _activeFruits.add(Fruit(fruitType, x, -fruitSize, speed, fruitSize))
    }

    private fun getRandomFruitType(): FruitType {
        val adjustedBombWeight = (FruitType.BOMB.baseWeight * bombChanceMultiplier).toInt()
        val totalWeight = FruitType.entries.sumOf {
            if (it == FruitType.BOMB) adjustedBombWeight else it.baseWeight
        }

        if (totalWeight <= 0) return FruitType.APPLE

        var randomValue = Random.nextInt(totalWeight)
        for (type in FruitType.entries) {
            val weight = if (type == FruitType.BOMB) adjustedBombWeight else type.baseWeight
            if (randomValue < weight) {
                return type
            }
            randomValue -= weight
        }
        return FruitType.APPLE
    }
}
