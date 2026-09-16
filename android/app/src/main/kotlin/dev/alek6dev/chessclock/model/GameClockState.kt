package dev.alek6dev.chessclock.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * État d'une partie en cours (issues #2, #3, #4, #5). Les Blancs sont toujours actifs
 * au démarrage (règle des échecs). Un seul chrono décompte à la fois ; à zéro, la partie
 * s'arrête définitivement.
 */
class GameClockState(whiteTime: GameTime, blackTime: GameTime) {
    var whiteSeconds by mutableIntStateOf(whiteTime.totalSeconds)
        private set
    var blackSeconds by mutableIntStateOf(blackTime.totalSeconds)
        private set
    var activePlayer by mutableStateOf(Player.WHITE)
        private set
    var isOver by mutableStateOf(false)
        private set

    /** Le joueur dont le temps est écoulé, une fois la partie terminée. */
    val timedOutPlayer: Player? get() = if (isOver) activePlayer else null

    fun secondsFor(player: Player): Int = if (player == Player.WHITE) whiteSeconds else blackSeconds

    /** Appelé une fois par seconde tant que la partie est en cours. */
    fun tick() {
        if (isOver) return
        if (activePlayer == Player.WHITE) {
            whiteSeconds = (whiteSeconds - 1).coerceAtLeast(0)
            if (whiteSeconds == 0) isOver = true
        } else {
            blackSeconds = (blackSeconds - 1).coerceAtLeast(0)
            if (blackSeconds == 0) isOver = true
        }
    }

    /** Tap sur la zone active : passe la main. */
    fun pass(tappedPlayer: Player) {
        if (isOver || tappedPlayer != activePlayer) return
        activePlayer = activePlayer.opponent
    }
}
