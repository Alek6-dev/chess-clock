package dev.alek6dev.chessclock.ui.game

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.input.pointer.changedToUp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.unit.dp
import kotlin.math.abs

private val SWIPE_THRESHOLD = 80.dp
private val TAP_TOLERANCE = 24.dp

/**
 * Tap OU swipe vers la droite (issue #6) : les deux ont le même effet, et les deux doivent
 * démarrer sur le bouton pause lui-même — le geste mime le fait de "tirer" le poussoir depuis
 * sa position, pas un swipe n'importe où sur l'écran de jeu.
 *
 * onTrigger n'est pas une clé de pointerInput : recréée à chaque recomposition (le chrono
 * tourne chaque seconde), l'utiliser comme clé redémarrerait la détection en continu et
 * pourrait couper un swipe en plein milieu. rememberUpdatedState évite ça.
 */
fun Modifier.tapOrSwipeToTrigger(onTrigger: () -> Unit): Modifier =
    composed {
        val current = rememberUpdatedState(onTrigger)
        this.pointerInput(Unit) {
            val swipeThresholdPx = SWIPE_THRESHOLD.toPx()
            val tapTolerancePx = TAP_TOLERANCE.toPx()
            awaitEachGesture {
                val down = awaitFirstDown()
                var totalDragX = 0f
                while (true) {
                    val event = awaitPointerEvent()
                    val change = event.changes.firstOrNull { it.id == down.id } ?: break
                    if (change.changedToUp()) {
                        if (totalDragX > swipeThresholdPx || abs(totalDragX) < tapTolerancePx) {
                            current.value()
                        }
                        break
                    }
                    totalDragX += change.positionChange().x
                    change.consume()
                }
            }
        }
    }
