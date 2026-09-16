package dev.alek6dev.chessclock.model

enum class Player {
    WHITE,
    BLACK;

    val opponent: Player get() = if (this == WHITE) BLACK else WHITE
}
