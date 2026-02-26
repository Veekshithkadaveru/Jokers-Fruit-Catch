package app.krafted.jokersfruitcatch.game

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.SurfaceHolder
import android.view.SurfaceView

class GameView(context: Context) : SurfaceView(context), SurfaceHolder.Callback {

    private var gameThread: GameThread? = null

    init {
        holder.addCallback(this)
        isFocusable = true
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
        // Handle surface size changes here
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
        // Stub for game logic update (fruits, positions, collisions)
    }

    private fun drawOnCanvas(canvas: Canvas) {
        // Draw standard background to verify rendering
        canvas.drawColor(Color.DKGRAY)

        val paint = Paint().apply {
            color = Color.WHITE
            textSize = 50f
            isAntiAlias = true
        }
        canvas.drawText("Game Engine Running...", 100f, 200f, paint)
    }
}
