package dev.alek6dev.chessclock.ui.game

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection

/**
 * Encoche du bouton pause — seule courbe de toute l'app. Mesurée directement sur la maquette
 * officielle (pixel par pixel) : une amande tangente au bord gauche, large de ~52 pt pour
 * ~84 pt de haut (nettement plus trapue qu'un premier essai à vue, pas un fuseau étroit).
 */
class AmandeShape : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density,
    ): Outline {
        val w = size.width
        val h = size.height
        val path = Path().apply {
            moveTo(0f, 0f)
            cubicTo(w * 0.85f, h * 0.08f, w, h * 0.30f, w, h * 0.5f)
            cubicTo(w, h * 0.70f, w * 0.85f, h * 0.92f, 0f, h)
            close()
        }
        return Outline.Generic(path)
    }
}
