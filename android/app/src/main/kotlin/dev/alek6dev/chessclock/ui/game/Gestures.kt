package dev.alek6dev.chessclock.ui.game

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
    androidx.compose.foundation.gestures.awaitEachGesture {
        val down = androidx.compose.foundation.gestures.awaitFirstDown()
        var totalDragX = 0f
        while (true) {
            val event = awaitPointerEvent()
            val change = event.changes.firstOrNull { it.id == down.id } ?: break
            if (change.changedToUp()) {
                // Direction ignorée : les zones peuvent être affichées à 180°, seule la distance
                // horizontale distingue un swipe volontaire d'un simple tap.
                when {
                    abs(totalDragX) > swipeThresholdPx -> onSwipeReset()
                    abs(totalDragX) < tapTolerancePx -> onTap()
                }
                break
            }
            totalDragX += change.positionChange().x
            change.consume()
        }
    }
}
