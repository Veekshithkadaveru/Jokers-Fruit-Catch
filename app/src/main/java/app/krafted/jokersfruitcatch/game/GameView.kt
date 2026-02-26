package app.krafted.jokersfruitcatch.game

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import app.krafted.jokersfruitcatch.R

class GameView(context: Context) : SurfaceView(context), SurfaceHolder.Callback {

    private var gameThread: GameThread? = null

    var onBasketMove: ((Float) -> Unit)? = null
    private var basketX: Float = 0f
    private var basketWidth: Float = 0f
    private var basketHeight: Float = 0f
    private var basketY: Float = 0f
    private var basketInitialized = false

    private var fruitSpawner: FruitSpawner? = null
    private var screenWidth = 0
    private var screenHeight = 0


    private var fruitSize = 0f

    private val bitmaps = mutableMapOf<FruitType, Bitmap>()


    private var backgroundBitmap: Bitmap? = null
    private var basketBitmap: Bitmap? = null


    private val debugPaint = Paint().apply {
        color = Color.WHITE
        textSize = 50f
        isAntiAlias = true
    }

    private val fallbackBasketPaint = Paint().apply {
        color = Color.parseColor("#8B4513")
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    init {
        holder.addCallback(this)
        isFocusable = true
    }


    private fun loadBitmaps(targetSize: Int) {
        val rawIds = mapOf(
            FruitType.APPLE      to R.drawable.apple,
            FruitType.ORANGE     to R.drawable.orange,
            FruitType.GRAPES     to R.drawable.grapes,
            FruitType.STRAWBERRY to R.drawable.strawberry,
            FruitType.BOMB       to R.drawable.bomb
        )
        rawIds.forEach { (type, resId) ->
            bitmaps[type]?.recycle()
            val raw = BitmapFactory.decodeResource(context.resources, resId)
            bitmaps[type] = Bitmap.createScaledBitmap(raw, targetSize, targetSize, true)
            if (raw != bitmaps[type]) raw.recycle()
        }
    }

    inner class GameThread(private val surfaceHolder: SurfaceHolder) : Thread() {
        var running = false

        override fun run() {
            while (running) {
                val startTime = System.currentTimeMillis()
                var canvas: Canvas? = null

                try {
                    canvas = surfaceHolder.lockCanvas()
                    if (canvas != null) {
                        synchronized(surfaceHolder) {
                            update()
                            drawOnCanvas(canvas)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    if (canvas != null) {
                        try {
                            surfaceHolder.unlockCanvasAndPost(canvas)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }

                val targetTime = 1000 / 60
                val totalTime = System.currentTimeMillis() - startTime
                if (totalTime < targetTime) {
                    try {
                        sleep(targetTime - totalTime)
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        gameThread = GameThread(holder)
        gameThread?.running = true
        gameThread?.start()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        screenWidth = width
        screenHeight = height

        basketWidth = width * 0.25f
        basketHeight = basketWidth * 0.5f
        basketY = height - basketHeight - 200f
        if (!basketInitialized) {
            basketX = (width - basketWidth) / 2f
            basketInitialized = true
        }

        if (backgroundBitmap == null || backgroundBitmap!!.width != width || backgroundBitmap!!.height != height) {
            backgroundBitmap?.recycle()
            val raw = BitmapFactory.decodeResource(context.resources, R.drawable.game_background)
            backgroundBitmap = Bitmap.createScaledBitmap(raw, width, height, true)
            if (raw != backgroundBitmap) raw.recycle()
        }

        if (basketBitmap == null || basketBitmap!!.width != basketWidth.toInt() || basketBitmap!!.height != basketHeight.toInt()) {
            basketBitmap?.recycle()
            val rawBasket = BitmapFactory.decodeResource(context.resources, R.drawable.basket)
            if (rawBasket != null) {
                basketBitmap = Bitmap.createScaledBitmap(rawBasket, basketWidth.toInt(), basketHeight.toInt(), true)
                if (rawBasket != basketBitmap) rawBasket.recycle()
            }
        }

        val newFruitSize = (width * 0.15f).toInt().coerceAtLeast(60)
        if (newFruitSize != fruitSize.toInt()) {
            fruitSize = newFruitSize.toFloat()
            loadBitmaps(newFruitSize)
        }

        if (fruitSpawner == null) {
            fruitSpawner = FruitSpawner(screenWidth, fruitSize)
        } else {
            fruitSpawner?.screenWidth = screenWidth
            fruitSpawner?.fruitSize = fruitSize
        }
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        var retry = true
        gameThread?.running = false
        while (retry) {
            try {
                gameThread?.join()
                retry = false
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
        gameThread = null
    }

    private fun update() {
        fruitSpawner?.update()
        if (screenHeight > 0) {
            fruitSpawner?.removeOffScreenFruits(screenHeight)
        }
    }

    private fun drawOnCanvas(canvas: Canvas) {
        // Draw background
        val bg = backgroundBitmap
        if (bg != null) {
            canvas.drawBitmap(bg, 0f, 0f, null)
        } else {
            canvas.drawColor(Color.DKGRAY)
        }

        canvas.drawText(
            "Fruits spawned: ${fruitSpawner?.activeFruits?.size ?: 0}",
            50f, 100f, debugPaint
        )

        fruitSpawner?.activeFruits?.forEach { fruit ->
            val bitmap = bitmaps[fruit.type]
            if (bitmap != null) {
                canvas.drawBitmap(
                    bitmap,
                    null,
                    RectF(fruit.x, fruit.y, fruit.x + fruit.size, fruit.y + fruit.size),
                    null
                )
            }
        }

        // Draw basket
        val basketImg = basketBitmap
        if (basketImg != null) {
            canvas.drawBitmap(basketImg, basketX, basketY, null)
        } else {

            canvas.drawRoundRect(
                RectF(basketX, basketY, basketX + basketWidth, basketY + basketHeight),
                20f, 20f,
                fallbackBasketPaint
            )
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                var newX = event.x - (basketWidth / 2f)
                newX = newX.coerceIn(0f, screenWidth - basketWidth.coerceAtLeast(1f))
                basketX = newX
                onBasketMove?.invoke(basketX)
            }
        }
        return true
    }
}
