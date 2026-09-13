package dev.alek6dev.chessclock.ui.setup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.alek6dev.chessclock.model.GameTime
import dev.alek6dev.chessclock.ui.components.WheelNumberPicker

private val DEFAULT_TIME = GameTime(minutes = 5, seconds = 0)

@Composable
fun GameSetupScreen(
    onStartGame: (whiteTime: GameTime, blackTime: GameTime) -> Unit,
    modifier: Modifier = Modifier,
) {
    var sameTimeForBoth by remember { mutableStateOf(true) }
    var whiteTime by remember { mutableStateOf(DEFAULT_TIME) }
    var blackTime by remember { mutableStateOf(DEFAULT_TIME) }

    fun onSameTimeToggled(checked: Boolean) {
        sameTimeForBoth = checked
        // Si on repasse en "temps identique", le temps des Blancs s'applique aux deux.
        if (checked) {
            blackTime = whiteTime
        }
    }

    fun updateWhiteTime(newTime: GameTime) {
        whiteTime = newTime
        if (sameTimeForBoth) {
            blackTime = newTime
        }
    }

    val canStart = whiteTime.isValid && blackTime.isValid

    Column(
        modifier = modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "Choisis le temps de ta partie",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = sameTimeForBoth,
                onCheckedChange = ::onSameTimeToggled,
            )
            Text("Temps identique pour les deux joueurs")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (sameTimeForBoth) {
            TimeWheelRow(
                label = null,
                time = whiteTime,
                onTimeChange = ::updateWhiteTime,
            )
        } else {
            TimeWheelRow(
                label = "Blancs",
                time = whiteTime,
                onTimeChange = ::updateWhiteTime,
            )
            Spacer(modifier = Modifier.height(24.dp))
            TimeWheelRow(
                label = "Noirs",
                time = blackTime,
                onTimeChange = { blackTime = it },
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { onStartGame(whiteTime, blackTime) },
            enabled = canStart,
        ) {
            Text("Démarrer")
        }
    }
}

@Composable
private fun TimeWheelRow(
    label: String?,
    time: GameTime,
    onTimeChange: (GameTime) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier) {
        if (label != null) {
            Text(label, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            WheelNumberPicker(
                range = 0..59,
                value = time.minutes,
                onValueChange = { onTimeChange(time.copy(minutes = it)) },
            )
            Text(" min  ")
            WheelNumberPicker(
                range = 0..59,
                value = time.seconds,
                onValueChange = { onTimeChange(time.copy(seconds = it)) },
            )
            Text(" s")
        }
    }
}
