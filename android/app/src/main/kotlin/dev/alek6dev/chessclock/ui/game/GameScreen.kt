package dev.alek6dev.chessclock.ui.game

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.matchParentSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.alek6dev.chessclock.R
import dev.alek6dev.chessclock.model.GameClockState
import dev.alek6dev.chessclock.model.GameTime
import dev.alek6dev.chessclock.model.Player
import dev.alek6dev.chessclock.ui.theme.ChessClockColors
import dev.alek6dev.chessclock.ui.theme.ChessClockFonts
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
    var isPaused by remember { mutableStateOf(false) }

    // Un seul chrono décompte à la fois ; la boucle repart à chaque changement de main, et se
    // coupe entièrement pendant la pause (aucun tick n'est consommé pendant ce temps).
    LaunchedEffect(state.activePlayer, state.isOver, isPaused) {
        if (!isPaused) {
            while (!state.isOver) {
                delay(1000)
                state.tick()
            }
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
                    showReplayButton = false,
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
                )
            }

            if (state.isOver) {
                GameOverHalf(
                    player = Player.WHITE,
                    isLoser = state.timedOutPlayer == Player.WHITE,
                    seconds = state.secondsFor(Player.WHITE),
                    isRotated = false,
                    showReplayButton = true,
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
                )
            }
        }

        if (!state.isOver && !isPaused) {
            PauseButton(onTap = { isPaused = true }, modifier = Modifier.align(Alignment.CenterStart))
        }

        PauseOverlay(
            isPaused = isPaused && !state.isOver,
            onResume = { isPaused = false },
            onConfigure = onReset,
        )
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
) {
    val isBlack = player == Player.BLACK
    // Le fond de zone EST la couleur du camp (maquettes officielles) : plus de matière
    // cuir/papier séparée. Le pion de son propre camp s'y fond donc entièrement — seul son
    // contour (ton opposé, variante "Contour" fournie) le rend visible.
    val baseColor = if (isBlack) ChessClockColors.InkSurface else ChessClockColors.Ivory
    val ruleColor = if (isBlack) Color(0x08F0E6D2) else Color(0x0D6B5236)
    val activeColor = if (isBlack) ChessClockColors.Ivory else ChessClockColors.InkSurface
    val inactiveColor = activeColor.copy(alpha = 0.6f)
    val campPawn = if (isBlack) PawnVariant.BLACK_CONTOUR else PawnVariant.WHITE_CONTOUR
    val opponentPawn = if (isBlack) PawnVariant.WHITE_FULL else PawnVariant.BLACK_FULL
    val pawnAlpha = if (isActive) 1f else 0.6f

    Box(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer { rotationZ = if (isRotated) 180f else 0f }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onTap,
            ),
    ) {
        RuledBackground(baseColor = baseColor, lineColor = ruleColor, modifier = Modifier.fillMaxSize())

        // Pion au-dessus, chrono en dessous — authoré identique pour les deux zones : la
        // rotation de la zone du haut se charge de l'inverser visuellement à l'écran.
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.align(Alignment.Center),
        ) {
            PawnIcon(variant = campPawn, height = 40.dp, alpha = pawnAlpha)
            Spacer(Modifier.height(8.dp))
            TabularTimeText(
                text = formatTime(ownSeconds),
                color = if (isActive) activeColor else inactiveColor,
                fontSize = 72.sp,
            )
        }

        OpponentBadge(
            seconds = opponentSeconds,
            pawnVariant = opponentPawn,
            textColor = activeColor,
            modifier = Modifier.align(Alignment.BottomEnd).padding(22.dp),
        )
    }
}

@Composable
private fun OpponentBadge(
    seconds: Int,
    pawnVariant: PawnVariant,
    textColor: Color,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        PawnIcon(variant = pawnVariant, height = 18.dp)
        TabularTimeText(
            text = formatTime(seconds),
            color = textColor,
            fontSize = 15.sp,
        )
    }
}

@Composable
private fun PauseButton(onTap: () -> Unit, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxHeight().width(28.dp)) {
        Box(
            modifier = Modifier
                .size(width = 28.dp, height = 94.dp)
                .align(Alignment.Center)
                .tapOrSwipeToTrigger(onTrigger = onTap),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                imageVector = vectorResource(id = R.drawable.pause_button_shape),
                contentDescription = null,
                modifier = Modifier.matchParentSize(),
            )
            Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                Box(Modifier.size(width = 3.5.dp, height = 14.dp).background(ChessClockColors.InkSurface))
                Box(Modifier.size(width = 3.5.dp, height = 14.dp).background(ChessClockColors.InkSurface))
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
    showReplayButton: Boolean,
    onRematch: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isBlack = player == Player.BLACK
    Box(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer { rotationZ = if (isRotated) 180f else 0f }
            .then(if (isLoser) Modifier.background(ChessClockColors.LacquerRed) else Modifier),
    ) {
        if (!isLoser) {
            val baseColor = if (isBlack) ChessClockColors.InkSurface else ChessClockColors.Ivory
            val ruleColor = if (isBlack) Color(0x08F0E6D2) else Color(0x0D6B5236)
            RuledBackground(baseColor = baseColor, lineColor = ruleColor, modifier = Modifier.fillMaxSize())
        }

        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (isLoser) {
                // Le pion garde la couleur de son camp même sur fond de laque rouge (Noirs =
                // encre, Blancs = ivoire) — le contour opposé assure la lisibilité.
                val loserPawn = if (isBlack) PawnVariant.BLACK_CONTOUR else PawnVariant.WHITE_CONTOUR
                PawnIcon(variant = loserPawn, height = 40.dp)
                Text(
                    text = "DÉFAITE",
                    color = ChessClockColors.Ivory,
                    fontFamily = ChessClockFonts.InstrumentSerif,
                    fontSize = 40.sp,
                    modifier = Modifier.padding(top = 18.dp),
                )
                Text(
                    text = "Temps écoulé",
                    color = ChessClockColors.TextOnLacquer,
                    fontFamily = ChessClockFonts.EBGaramond,
                    fontSize = 15.sp,
                    modifier = Modifier.padding(top = 4.dp),
                )
            } else {
                val winnerPawn = if (isBlack) PawnVariant.BLACK_CONTOUR else PawnVariant.WHITE_CONTOUR
                PawnIcon(variant = winnerPawn, height = 40.dp)
                TabularTimeText(
                    text = formatTime(seconds),
                    color = if (isBlack) ChessClockColors.Ivory else ChessClockColors.InkSurface,
                    fontSize = 72.sp,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }

            // REJOUER est toujours dans la moitié Blancs (bas de l'écran), jamais chez les
            // Noirs — vérifié sur les deux scénarios (Blancs ou Noirs perdant) des maquettes
            // officielles : sa position ne dépend pas de qui a gagné.
            if (showReplayButton) {
                Text(
                    text = "REJOUER",
                    color = ChessClockColors.Paper,
                    fontFamily = ChessClockFonts.EBGaramond,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    letterSpacing = 2.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(top = 40.dp)
                        .background(ChessClockColors.InkSurface)
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

/**
 * Modale de pause (issue #6) : déclenchée par swipe ou par le bouton pause, comportement
 * identique dans les deux cas. Transition d'entrée "tirée" depuis la gauche vers la droite.
 */
@Composable
private fun PauseOverlay(isPaused: Boolean, onResume: () -> Unit, onConfigure: () -> Unit) {
    if (!isPaused) return

    val progress = remember(isPaused) { Animatable(0f) }
    LaunchedEffect(isPaused) {
        progress.animateTo(1f, animationSpec = tween(durationMillis = 320, easing = FastOutSlowInEasing))
    }
    val density = LocalDensity.current
    val slidePx = with(density) { 420.dp.toPx() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ChessClockColors.InkNight.copy(alpha = 0.55f * progress.value))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {}, // capte les taps derrière la modale, ne fait rien
            ),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .graphicsLayer { translationX = (progress.value - 1f) * slidePx }
                .background(ChessClockColors.Paper)
                .padding(24.dp),
        ) {
            Text(
                text = "Pause",
                color = ChessClockColors.InkSurface,
                fontFamily = ChessClockFonts.InstrumentSerif,
                fontSize = 34.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(16.dp))
            Box(Modifier.fillMaxWidth().height(1.dp).background(ChessClockColors.LineRule))
            Spacer(Modifier.height(20.dp))
            Text(
                text = "Votre partie est en pause, les deux chronomètres sont arrêtés.",
                color = ChessClockColors.InkSurface,
                fontFamily = ChessClockFonts.EBGaramond,
                fontSize = 16.sp,
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = "Souhaitez-vous reprendre votre partie en cours ou en configurer une nouvelle ?",
                color = ChessClockColors.InkSurface,
                fontFamily = ChessClockFonts.EBGaramond,
                fontSize = 16.sp,
            )
            Spacer(Modifier.height(28.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                Text(
                    text = "REPRENDRE",
                    color = ChessClockColors.Paper,
                    fontFamily = ChessClockFonts.EBGaramond,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    letterSpacing = 1.sp,
                    maxLines = 1,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .weight(1f)
                        .background(ChessClockColors.InkSurface)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onResume,
                        )
                        .padding(horizontal = 6.dp, vertical = 16.dp),
                )
                Text(
                    text = "CONFIGURER",
                    color = ChessClockColors.InkSurface,
                    fontFamily = ChessClockFonts.EBGaramond,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    letterSpacing = 1.sp,
                    maxLines = 1,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .weight(1f)
                        .border(width = 1.dp, color = ChessClockColors.InkSurface)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onConfigure,
                        )
                        .padding(horizontal = 6.dp, vertical = 16.dp),
                )
            }
        }
    }
}
