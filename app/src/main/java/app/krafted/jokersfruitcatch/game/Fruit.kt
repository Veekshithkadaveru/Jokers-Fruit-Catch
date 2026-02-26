package app.krafted.jokersfruitcatch.game

enum class FruitType(val points: Int, val baseWeight: Int) {
    APPLE(10, 40),
    ORANGE(15, 30),
    GRAPES(20, 18),
    STRAWBERRY(25, 10),
    BOMB(0, 2)
}

data class Fruit(
    val type: FruitType,
    var x: Float,
    var y: Float,
    var speed: Float,
    val size: Float
)
