package dev.alek6dev.chessclock.ui.game

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.alek6dev.chessclock.model.GameClockState
import dev.alek6dev.chessclock.model.GameTime
import dev.alek6dev.chessclock.model.Player
import dev.alek6dev.chessclock.ui.theme.ChessClockColors
import dev.alek6dev.chessclock.ui.theme.ChessClockFonts
import dev.alek6dev.chessclock.ui.theme.InkStainOverlay
import dev.alek6dev.chessclock.ui.theme.LocalHaptics
import dev.alek6dev.chessclock.ui.theme.RuledBackground
import kotlinx.coroutines.delay

/**
 * Écran de jeu — issues #2 (démarrer), #3 (passer la main), #4 (affichage temps réel),
 * #5 (fin de partie) et #6 (reset direct par swipe ou bouton pause, sans confirmation).
 */
@Composable
fun GameScreen(
    whiteTime: GameTime,
    blackTime: GameTime,
    onReset: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val haptics = LocalHaptics.current
    val state = remember(whiteTime, blackTime) { GameClockState(whiteTime, blackTime) }

    // Un seul chrono décompte à la fois ; la boucle repart à chaque changement de main.
    LaunchedEffect(state.activePlayer, state.isOver) {
        while (!state.isOver) {
            delay(1000)
            state.tick()
        }
    }

    LaunchedEffect(state.isOver) {
        if (state.isOver) haptics.doubleImpact()
    }

    Box(modifier = modifier.fillMaxSize().background(ChessClockColors.InkNight)) {
        Column(Modifier.fillMaxSize()) {
            if (state.isOver) {
                GameOverHalf(
                    player = Player.BLACK,
                    isLoser = state.timedOutPlayer == Player.BLACK,
                    seconds = state.secondsFor(Player.BLACK),
                    isRotated = true,
                    onRematch = onReset,
                    modifier = Modifier.weight(1f),
                )
            } else {
                PlayerZone(
                    player = Player.BLACK,
                    ownSeconds = state.blackSeconds,
                    opponentSeconds = state.whiteSeconds,
                    isActive = state.activePlayer == Player.BLACK,
                    isRotated = true,
                    modifier = Modifier.weight(1f),
                    onTap = { state.pass(Player.BLACK); haptics.light() },
                    onSwipeReset = onReset,
                )
            }

            if (state.isOver) {
                GameOverHalf(
                    player = Player.WHITE,
                    isLoser = state.timedOutPlayer == Player.WHITE,
                    seconds = state.secondsFor(Player.WHITE),
                    isRotated = false,
                    onRematch = onReset,
                    modifier = Modifier.weight(1f),
                )
            } else {
                PlayerZone(
                    player = Player.WHITE,
                    ownSeconds = state.whiteSeconds,
                    opponentSeconds = state.blackSeconds,
                    isActive = state.activePlayer == Player.WHITE,
                    isRotated = false,
                    modifier = Modifier.weight(1f),
                    onTap = { state.pass(Player.WHITE); haptics.light() },
                    onSwipeReset = onReset,
                )
            }
        }

        if (!state.isOver) {
            PauseButton(onTap = onReset, modifier = Modifier.align(Alignment.CenterStart))
        }
    }
}

private fun formatTime(totalSeconds: Int): String {
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "$minutes:${seconds.toString().padStart(2, '0')}"
}

@Composable
private fun PlayerZone(
    player: Player,
    ownSeconds: Int,
    opponentSeconds: Int,
    isActive: Boolean,
    isRotated: Boolean,
    modifier: Modifier = Modifier,
    onTap: () -> Unit,
    onSwipeReset: () -> Unit,
) {
    val isBlack = player == Player.BLACK
    val baseColor = if (isBlack) ChessClockColors.Leather else ChessClockColors.Paper
    val ruleColor = if (isBlack) Color(0x2917110D) else Color(0x126B5236)
    val activeColor = if (isBlack) ChessClockColors.Ivory else ChessClockColors.InkSurface
    val inactiveColor = activeColor.copy(alpha = 0.6f)
    val badgeBorderColor = if (isBlack) ChessClockColors.Ivory.copy(alpha = 0.28f) else ChessClockColors.Leather.copy(alpha = 0.45f)
    val badgeDotColor = if (isBlack) ChessClockColors.Ivory else ChessClockColors.LineRule
    val badgeTextColor = if (isBlack) ChessClockColors.Ivory.copy(alpha = 0.8f) else ChessClockColors.Leather

    Box(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer { rotationZ = if (isRotated) 180f else 0f }
            .tapOrSwipeReset(onTap = onTap, onSwipeReset = onSwipeReset),
    ) {
        RuledBackground(baseColor = baseColor, lineColor = ruleColor, modifier = Modifier.fillMaxSize())

        Text(
            text = formatTime(ownSeconds),
            color = if (isActive) activeColor else inactiveColor,
            fontFamily = ChessClockFonts.InstrumentSerif,
            fontSize = 72.sp,
            modifier = Modifier.align(Alignment.Center),
        )

        OpponentBadge(
            seconds = opponentSeconds,
            borderColor = badgeBorderColor,
            dotColor = badgeDotColor,
            textColor = badgeTextColor,
            modifier = Modifier.align(Alignment.BottomEnd).padding(22.dp),
        )
    }
}

@Composable
private fun OpponentBadge(
    seconds: Int,
    borderColor: Color,
    dotColor: Color,
    textColor: Color,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .border(width = 1.dp, color = borderColor)
            .padding(horizontal = 13.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(modifier = Modifier.size(11.dp).background(dotColor))
        Text(
            text = formatTime(seconds),
            color = textColor,
            fontFamily = ChessClockFonts.InstrumentSerif,
            fontSize = 15.sp,
        )
    }
}

@Composable
private fun PauseButton(onTap: () -> Unit, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxHeight().width(34.dp)) {
        Box(
            modifier = Modifier
                .size(width = 34.dp, height = 210.dp)
                .align(Alignment.Center)
                .clip(AmandeShape())
                .background(ChessClockColors.InkSurface)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onTap,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                Box(Modifier.size(width = 3.5.dp, height = 14.dp).background(ChessClockColors.Ivory))
                Box(Modifier.size(width = 3.5.dp, height = 14.dp).background(ChessClockColors.Ivory))
            }
        }
    }
}

@Composable
private fun GameOverHalf(
    player: Player,
    isLoser: Boolean,
    seconds: Int,
    isRotated: Boolean,
    onRematch: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val campLabel = if (player == Player.BLACK) "NOIR" else "BLANC"
    Box(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer { rotationZ = if (isRotated) 180f else 0f }
            .then(if (isLoser) Modifier.background(ChessClockColors.LacquerRed) else Modifier),
    ) {
        if (!isLoser) {
            RuledBackground(
                baseColor = ChessClockColors.Paper,
                lineColor = ChessClockColors.Leather.copy(alpha = 0.07f),
                modifier = Modifier.fillMaxSize(),
            )
            InkStainOverlay(modifier = Modifier.fillMaxSize())
        }

        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = formatTime(seconds),
                color = if (isLoser) ChessClockColors.Ivory else ChessClockColors.InkSurface,
                fontFamily = ChessClockFonts.InstrumentSerif,
                fontSize = 72.sp,
            )
            if (isLoser) {
                Text(
                    text = "TEMPS ÉCOULÉ — $campLabel",
                    color = ChessClockColors.TextOnLacquer,
                    fontFamily = ChessClockFonts.EBGaramond,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    letterSpacing = 2.sp,
                    modifier = Modifier.padding(top = 18.dp),
                )
            } else {
                Text(
                    text = "REJOUER",
                    color = ChessClockColors.InkNight,
                    fontFamily = ChessClockFonts.EBGaramond,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    letterSpacing = 2.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(top = 40.dp)
                        .background(ChessClockColors.Brass)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onRematch,
                        )
                        .padding(horizontal = 52.dp, vertical = 20.dp),
                )
            }
        }
    }
}
