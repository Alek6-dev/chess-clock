package dev.alek6dev.chessclock.ui.setup

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.alek6dev.chessclock.model.GameTime
import dev.alek6dev.chessclock.ui.components.WheelNumberPicker
import dev.alek6dev.chessclock.ui.theme.ChessClockColors
import dev.alek6dev.chessclock.ui.theme.ChessClockFonts
import dev.alek6dev.chessclock.ui.theme.InkStainOverlay
import dev.alek6dev.chessclock.ui.theme.RuledBackground

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

    Box(modifier = modifier.fillMaxSize()) {
        RuledBackground(
            baseColor = ChessClockColors.Paper,
            lineColor = ChessClockColors.Leather.copy(alpha = 0.07f),
            modifier = Modifier.fillMaxSize(),
        )
        InkStainOverlay(modifier = Modifier.fillMaxSize())

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 30.dp)
                .padding(top = 76.dp, bottom = 44.dp),
        ) {
            Text(
                text = "Configuration",
                color = ChessClockColors.InkSurface,
                fontFamily = ChessClockFonts.InstrumentSerif,
                fontSize = 34.sp,
            )

            Spacer(Modifier.height(22.dp))
            Box(Modifier.fillMaxWidth().height(1.dp).background(ChessClockColors.Leather.copy(alpha = 0.45f)))
            Spacer(Modifier.height(22.dp))

            CheckboxRow(
                checked = sameTimeForBoth,
                onCheckedChange = ::onSameTimeToggled,
            )

            Spacer(Modifier.height(32.dp))

            if (sameTimeForBoth) {
                TimeWheelRow(
                    swatch = null,
                    time = whiteTime,
                    onTimeChange = ::updateWhiteTime,
                    modifier = Modifier.fillMaxWidth(),
                )
            } else {
                TimeWheelRow(
                    swatch = CampSwatch.WHITE,
                    time = whiteTime,
                    onTimeChange = ::updateWhiteTime,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(24.dp))
                TimeWheelRow(
                    swatch = CampSwatch.BLACK,
                    time = blackTime,
                    onTimeChange = { blackTime = it },
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            Spacer(Modifier.weight(1f))

            StartButton(
                enabled = canStart,
                onClick = { onStartGame(whiteTime, blackTime) },
            )
        }
    }
}

@Composable
private fun CheckboxRow(checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = { onCheckedChange(!checked) },
            ),
    ) {
        Box(
            modifier = Modifier
                .size(22.dp)
                .then(
                    if (checked) {
                        Modifier.background(ChessClockColors.InkSurface)
                    } else {
                        Modifier.border(width = 1.dp, color = ChessClockColors.Leather)
                    },
                ),
            contentAlignment = Alignment.Center,
        ) {
            if (checked) {
                Text(
                    text = "✓",
                    color = ChessClockColors.Paper,
                    fontSize = 15.sp,
                )
            }
        }
        Spacer(Modifier.width(14.dp))
        Text(
            text = "Temps identique pour les deux joueurs",
            color = ChessClockColors.InkSurface,
            fontFamily = ChessClockFonts.EBGaramond,
            fontSize = 18.sp,
        )
    }
}

private enum class CampSwatch { WHITE, BLACK }

@Composable
private fun TimeWheelRow(
    swatch: CampSwatch?,
    time: GameTime,
    onTimeChange: (GameTime) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        if (swatch != null) {
            Box(
                modifier = Modifier
                    .size(width = 30.dp, height = 9.dp)
                    .then(
                        if (swatch == CampSwatch.WHITE) {
                            Modifier
                                .background(ChessClockColors.Ivory)
                                .border(width = 1.dp, color = ChessClockColors.Leather)
                        } else {
                            Modifier.background(ChessClockColors.Leather)
                        },
                    ),
            )
            Spacer(Modifier.height(12.dp))
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            WheelNumberPicker(
                range = 0..59,
                value = time.minutes,
                onValueChange = { onTimeChange(time.copy(minutes = it)) },
            )
            Text(
                text = "min",
                color = ChessClockColors.Leather,
                fontFamily = ChessClockFonts.EBGaramond,
                fontSize = 15.sp,
                modifier = Modifier.padding(horizontal = 8.dp),
            )
            WheelNumberPicker(
                range = 0..59,
                value = time.seconds,
                onValueChange = { onTimeChange(time.copy(seconds = it)) },
            )
            Text(
                text = "s",
                color = ChessClockColors.Leather,
                fontFamily = ChessClockFonts.EBGaramond,
                fontSize = 15.sp,
                modifier = Modifier.padding(start = 8.dp),
            )
        }
    }
}

@Composable
private fun StartButton(enabled: Boolean, onClick: () -> Unit) {
    val brassGradient = Brush.linearGradient(
        colors = listOf(
            ChessClockColors.Brass.copy(alpha = 0.7f),
            ChessClockColors.Ivory.copy(alpha = 0.4f),
            ChessClockColors.Brass,
        ),
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (enabled) {
                    Modifier
                        .background(brassGradient)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onClick,
                        )
                } else {
                    Modifier.border(width = 1.dp, color = ChessClockColors.Leather.copy(alpha = 0.45f))
                },
            )
            .padding(vertical = 22.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "DÉMARRER",
            color = if (enabled) ChessClockColors.InkNight else ChessClockColors.Leather.copy(alpha = 0.55f),
            fontFamily = ChessClockFonts.EBGaramond,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            letterSpacing = 2.sp,
            textAlign = TextAlign.Center,
        )
    }
}
