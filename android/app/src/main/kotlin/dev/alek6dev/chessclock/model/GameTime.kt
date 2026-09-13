package dev.alek6dev.chessclock.model

data class GameTime(val minutes: Int, val seconds: Int) {
    val totalSeconds: Int get() = minutes * 60 + seconds
    val isValid: Boolean get() = totalSeconds > 0
}
