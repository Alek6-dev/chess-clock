package dev.alek6dev.chessclock.ui.game

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.changedToUp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.unit.dp
import kotlin.math.abs

private val SWIPE_RESET_THRESHOLD = 80.dp
private val TAP_TOLERANCE = 24.dp

/**
 * Tap sur sa zone = passe la main. Swipe du bouton pause vers l'autre bord (gauche -> droite
 * à l'écran, distance minimale distincte d'un tap) = reset direct (issue #6).
 *
 * [isRotated] : la zone des Noirs est tournée à 180°, donc pointerInput reçoit des coordonnées
 * dans son repère local déjà inversé par rapport à l'écran réel. Un geste "vers la droite de
 * l'écran" s'y traduit par un déplacement local négatif — sans ça, le swipe ne marcherait que
 * dans un sens sur une zone et dans l'autre sens sur l'autre.
 */
fun Modifier.tapOrSwipeReset(isRotated: Boolean, onTap: () -> Unit, onSwipeReset: () -> Unit): Modifier =
    this.pointerInput(isRotated, onTap, onSwipeReset) {
        val swipeThresholdPx = SWIPE_RESET_THRESHOLD.toPx()
        val tapTolerancePx = TAP_TOLERANCE.toPx()
        detectTapOrHorizontalSwipe(
            isRotated = isRotated,
            swipeThresholdPx = swipeThresholdPx,
            tapTolerancePx = tapTolerancePx,
            onTap = onTap,
            onSwipeReset = onSwipeReset,
        )
    }

private suspend fun PointerInputScope.detectTapOrHorizontalSwipe(
    isRotated: Boolean,
    swipeThresholdPx: Float,
    tapTolerancePx: Float,
    onTap: () -> Unit,
    onSwipeReset: () -> Unit,
) {
    awaitEachGesture {
        val down = awaitFirstDown()
        var totalDragX = 0f
        while (true) {
            val event = awaitPointerEvent()
            val change = event.changes.firstOrNull { it.id == down.id } ?: break
            if (change.changedToUp()) {
                // Vers la droite à l'écran = positif en repère normal, négatif en repère
                // tourné à 180°.
                val swipedTowardOtherEdge = if (isRotated) {
                    totalDragX < -swipeThresholdPx
                } else {
                    totalDragX > swipeThresholdPx
                }
                when {
                    swipedTowardOtherEdge -> onSwipeReset()
                    abs(totalDragX) < tapTolerancePx -> onTap()
                }
                break
            }
            totalDragX += change.positionChange().x
            change.consume()
        }
    }
}
