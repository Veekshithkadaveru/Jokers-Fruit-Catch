package app.krafted.jokersfruitcatch.game

enum class FruitType(val points: Int, val baseWeight: Int) {
    APPLE(10, 35),
    ORANGE(15, 25),
    GRAPES(20, 16),
    STRAWBERRY(25, 8),
    BOMB(0, 8)
}

data class Fruit(
    val type: FruitType,
    var x: Float,
    var y: Float,
    var speed: Float,
    val size: Float
)
