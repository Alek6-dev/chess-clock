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
 * Tap sur sa zone = passe la main. Swipe horizontal (distance minimale distincte d'un tap)
 * = reset direct (issue #6). Les deux gestes doivent rester clairement séparés.
 */
fun Modifier.tapOrSwipeReset(onTap: () -> Unit, onSwipeReset: () -> Unit): Modifier =
    this.pointerInput(onTap, onSwipeReset) {
        val swipeThresholdPx = SWIPE_RESET_THRESHOLD.toPx()
        val tapTolerancePx = TAP_TOLERANCE.toPx()
        detectTapOrHorizontalSwipe(
            swipeThresholdPx = swipeThresholdPx,
            tapTolerancePx = tapTolerancePx,
            onTap = onTap,
            onSwipeReset = onSwipeReset,
        )
    }

private suspend fun PointerInputScope.detectTapOrHorizontalSwipe(
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
                // Swipe gauche -> droite uniquement (issue #6). Les coordonnées du pointeur
                // sont déjà dans le repère local de la zone (donc post-rotation pour les
                // Noirs) : un totalDragX positif correspond bien à un geste gauche -> droite
                // du point de vue du joueur qui lit cette moitié, dans les deux cas.
                when {
                    totalDragX > swipeThresholdPx -> onSwipeReset()
                    abs(totalDragX) < tapTolerancePx -> onTap()
                }
                break
            }
            totalDragX += change.positionChange().x
            change.consume()
        }
    }
}
