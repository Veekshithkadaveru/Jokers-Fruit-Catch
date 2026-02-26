package app.krafted.jokersfruitcatch.game

import kotlin.random.Random

class FruitSpawner(var screenWidth: Int, var fruitSize: Float = 150f) {
    private val _activeFruits = mutableListOf<Fruit>()

    val activeFruits: List<Fruit> get() = _activeFruits
    private var lastSpawnTime = 0L
    private var spawnIntervalMs = 1000L
    private var baseSpeed = 10f
    
    var speedMultiplier = 1.0f
    var bombChanceMultiplier = 1.0f

    fun update() {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastSpawnTime > spawnIntervalMs) {
            spawnFruit()
            lastSpawnTime = currentTime
        }

        val iterator = _activeFruits.iterator()
        while (iterator.hasNext()) {
            val fruit = iterator.next()
            fruit.y += fruit.speed * speedMultiplier
        }
    }

    fun removeOffScreenFruits(screenHeight: Int) {
        _activeFruits.removeAll { it.y > screenHeight.toFloat() }
    }

    fun removeFruits(fruits: Collection<Fruit>) {
        _activeFruits.removeAll(fruits.toSet())
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
        val totalWeight = FruitType.values().sumOf { 
            if (it == FruitType.BOMB) adjustedBombWeight else it.baseWeight 
        }
        
        if (totalWeight <= 0) return FruitType.APPLE
        
        var randomValue = Random.nextInt(totalWeight)
        for (type in FruitType.values()) {
            val weight = if (type == FruitType.BOMB) adjustedBombWeight else type.baseWeight
            if (randomValue < weight) {
                return type
            }
            randomValue -= weight
        }
        return FruitType.APPLE
    }
}
