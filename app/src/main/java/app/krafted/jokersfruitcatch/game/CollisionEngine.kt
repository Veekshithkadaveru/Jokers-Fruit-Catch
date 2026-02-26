package app.krafted.jokersfruitcatch.game

class CollisionEngine {

    data class FrameResult(
        val caughtFruits: List<Fruit>,
        val missedFruits: List<Fruit>
    )

    fun processFrame(
        fruits: List<Fruit>,
        basketX: Float,
        basketY: Float,
        basketWidth: Float,
        basketHeight: Float,
        screenHeight: Int
    ): FrameResult {
        val caught = mutableListOf<Fruit>()
        val missed = mutableListOf<Fruit>()

        for (fruit in fruits) {
            when {
                checkCatch(fruit, basketX, basketY, basketWidth, basketHeight) -> {
                    caught.add(fruit)
                }
                fruit.y > screenHeight -> {
                    missed.add(fruit)
                }
            }
        }

        return FrameResult(caught, missed)
    }

    private fun checkCatch(
        fruit: Fruit,
        basketX: Float,
        basketY: Float,
        basketWidth: Float,
        basketHeight: Float
    ): Boolean {
        return fruit.x + fruit.size > basketX &&
                fruit.x < basketX + basketWidth &&
                fruit.y + fruit.size >= basketY &&
                fruit.y < basketY + basketHeight
    }
}
