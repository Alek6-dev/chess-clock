package dev.alek6dev.chessclock.ui.game

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.changedToUp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.unit.dp
import kotlin.math.abs

private val SWIPE_RESET_THRESHOLD = 80.dp
private val TAP_TOLERANCE = 24.dp

// Le geste doit partir de la zone de la vague/bouton pause (bord gauche à l'écran), pas de
// n'importe où sur le chrono — sinon un simple passage de main appuyé pourrait se confondre
// avec un swipe de reset.
private val SWIPE_ORIGIN_ZONE = 96.dp

/**
 * Tap sur sa zone = passe la main. Swipe du bouton pause vers l'autre bord (gauche -> droite
 * à l'écran, distance minimale distincte d'un tap) = reset direct (issue #6). Le swipe ne
 * compte que s'il démarre près de la vague, à gauche de l'écran.
 *
 * [isRotated] : la zone des Noirs est tournée à 180°, donc pointerInput reçoit des coordonnées
 * dans son repère local déjà inversé par rapport à l'écran réel. "Bord gauche de l'écran" y
 * correspond au bord DROIT du repère local (x proche de la largeur totale, pas de 0).
 *
 * onTap/onSwipeReset ne sont PAS des clés de pointerInput : ce sont des lambdas recréées à
 * chaque recomposition (le chrono tourne chaque seconde), donc les utiliser comme clé
 * redémarrait la détection de geste en continu et pouvait couper un swipe en plein milieu.
 * rememberUpdatedState permet de toujours appeler la version la plus récente sans jamais
 * relancer la coroutine de détection.
 */
fun Modifier.tapOrSwipeReset(isRotated: Boolean, onTap: () -> Unit, onSwipeReset: () -> Unit): Modifier =
    composed {
        val currentOnTap = rememberUpdatedState(onTap)
        val currentOnSwipeReset = rememberUpdatedState(onSwipeReset)
        this.pointerInput(isRotated) {
            val swipeThresholdPx = SWIPE_RESET_THRESHOLD.toPx()
            val tapTolerancePx = TAP_TOLERANCE.toPx()
            val originZonePx = SWIPE_ORIGIN_ZONE.toPx()
            detectTapOrHorizontalSwipe(
                isRotated = isRotated,
                swipeThresholdPx = swipeThresholdPx,
                tapTolerancePx = tapTolerancePx,
                originZonePx = originZonePx,
                onTap = { currentOnTap.value() },
                onSwipeReset = { currentOnSwipeReset.value() },
            )
        }
    }

private suspend fun PointerInputScope.detectTapOrHorizontalSwipe(
    isRotated: Boolean,
    swipeThresholdPx: Float,
    tapTolerancePx: Float,
    originZonePx: Float,
    onTap: () -> Unit,
    onSwipeReset: () -> Unit,
) {
    awaitEachGesture {
        val down = awaitFirstDown()
        val startedNearWave = if (isRotated) {
            down.position.x > size.width - originZonePx
        } else {
            down.position.x < originZonePx
        }
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
                    startedNearWave && swipedTowardOtherEdge -> onSwipeReset()
                    abs(totalDragX) < tapTolerancePx -> onTap()
                }
                break
            }
            totalDragX += change.positionChange().x
            change.consume()
        }
    }
}
