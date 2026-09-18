package dev.alek6dev.chessclock

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import dev.alek6dev.chessclock.model.GameTime
import dev.alek6dev.chessclock.ui.game.GameScreen
import dev.alek6dev.chessclock.ui.setup.GameSetupScreen
import dev.alek6dev.chessclock.ui.splash.SplashScreen
import dev.alek6dev.chessclock.ui.theme.ChessClockTheme

private sealed interface Screen {
    data object Splash : Screen
    data object Setup : Screen
    data class Playing(val whiteTime: GameTime, val blackTime: GameTime) : Screen
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChessClockTheme {
                ChessClockApp()
            }
        }
    }
}

@Composable
private fun ChessClockApp() {
    var screen by remember { mutableStateOf<Screen>(Screen.Splash) }

    when (val current = screen) {
        is Screen.Splash -> SplashScreen(onContinue = { screen = Screen.Setup })

        is Screen.Setup -> GameSetupScreen(
            onStartGame = { whiteTime, blackTime ->
                screen = Screen.Playing(whiteTime, blackTime)
            },
        )

        is Screen.Playing -> GameScreen(
            whiteTime = current.whiteTime,
            blackTime = current.blackTime,
            onReset = { screen = Screen.Setup },
        )
    }
}
