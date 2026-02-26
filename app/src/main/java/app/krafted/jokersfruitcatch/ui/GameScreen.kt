package app.krafted.jokersfruitcatch.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import app.krafted.jokersfruitcatch.game.GameView

@Composable
fun GameScreen() {
    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { context ->
                GameView(context)
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}
