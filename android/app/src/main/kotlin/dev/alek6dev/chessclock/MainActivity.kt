package dev.alek6dev.chessclock

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import dev.alek6dev.chessclock.ui.setup.GameSetupScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface {
                    GameSetupScreen(
                        onStartGame = { whiteTime, blackTime ->
                            // Stub temporaire : le vrai lancement de partie
                            // (écran des chronos) arrive avec l'issue #2.
                            Toast.makeText(
                                this,
                                "Blancs ${whiteTime.minutes}:${whiteTime.seconds} — " +
                                    "Noirs ${blackTime.minutes}:${blackTime.seconds}",
                                Toast.LENGTH_LONG,
                            ).show()
                        },
                    )
                }
            }
        }
    }
}
