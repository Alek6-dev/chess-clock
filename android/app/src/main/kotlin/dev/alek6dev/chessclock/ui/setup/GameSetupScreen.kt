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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.alek6dev.chessclock.model.GameTime
import dev.alek6dev.chessclock.ui.components.WheelNumberPicker
import dev.alek6dev.chessclock.ui.game.PawnIcon
import dev.alek6dev.chessclock.ui.theme.ChessClockColors
import dev.alek6dev.chessclock.ui.theme.ChessClockFonts
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
            lineColor = ChessClockColors.LineRule.copy(alpha = 0.09f),
            modifier = Modifier.fillMaxSize(),
        )

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
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(Modifier.height(8.dp))
            Box(Modifier.fillMaxWidth().height(1.dp).background(ChessClockColors.Leather.copy(alpha = 0.45f)))
            Spacer(Modifier.height(44.dp))

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
    // Par défaut (mode "temps identique", aucun camp) : encart encre + texte ivoire.
    // Par joueur : encart et texte dans la couleur du camp de cette roue.
    val boxColor = if (swatch == CampSwatch.WHITE) ChessClockColors.Ivory else ChessClockColors.InkSurface
    val selectedTextColor = if (swatch == CampSwatch.WHITE) ChessClockColors.InkSurface else ChessClockColors.Ivory

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (swatch == null) {
            ComboPawnIcon(height = 60.dp)
        } else {
            val pawnColor = if (swatch == CampSwatch.WHITE) ChessClockColors.Ivory else ChessClockColors.InkSurface
            val pawnContour = if (swatch == CampSwatch.WHITE) ChessClockColors.InkSurface else ChessClockColors.Ivory
            PawnIcon(fillColor = pawnColor, contourColor = pawnContour, height = 60.dp)
        }
        Spacer(Modifier.width(14.dp))
        WheelNumberPicker(
            range = 0..59,
            value = time.minutes,
            onValueChange = { onTimeChange(time.copy(minutes = it)) },
            boxColor = boxColor,
            selectedTextColor = selectedTextColor,
            modifier = Modifier.width(90.dp),
        )
        Text(
            text = ":",
            color = ChessClockColors.InkSurface,
            fontFamily = ChessClockFonts.InstrumentSerif,
            fontSize = 34.sp,
            modifier = Modifier.padding(horizontal = 10.dp),
        )
        WheelNumberPicker(
            range = 0..59,
            value = time.seconds,
            onValueChange = { onTimeChange(time.copy(seconds = it)) },
            boxColor = boxColor,
            selectedTextColor = selectedTextColor,
            modifier = Modifier.width(90.dp),
        )
    }
}

/** Icône par défaut (mode "temps identique") : les deux pions se chevauchent, un par camp. */
@Composable
private fun ComboPawnIcon(height: Dp, modifier: Modifier = Modifier) {
    Box(modifier = modifier.width(height * 0.9f).height(height)) {
        PawnIcon(
            fillColor = ChessClockColors.InkSurface,
            contourColor = ChessClockColors.Ivory,
            height = height,
            modifier = Modifier.align(Alignment.CenterEnd),
        )
        PawnIcon(
            fillColor = ChessClockColors.Ivory,
            contourColor = ChessClockColors.InkSurface,
            height = height,
            modifier = Modifier.align(Alignment.CenterStart),
        )
    }
}

@Composable
private fun StartButton(enabled: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (enabled) {
                    Modifier
                        .background(ChessClockColors.InkSurface)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onClick,
                        )
                } else {
                    Modifier.border(width = 1.dp, color = ChessClockColors.InkSurface)
                },
            )
            .padding(vertical = 22.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = if (enabled) "COMMENCER" else "TEMPS NON VALIDE",
            color = if (enabled) ChessClockColors.Paper else ChessClockColors.InkSurface,
            fontFamily = ChessClockFonts.EBGaramond,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            letterSpacing = 2.sp,
            textAlign = TextAlign.Center,
        )
    }
}
