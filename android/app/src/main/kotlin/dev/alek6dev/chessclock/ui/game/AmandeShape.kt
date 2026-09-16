package dev.alek6dev.chessclock.ui.game

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection

/**
 * Encoche du bouton pause — seule courbe de toute l'app (planche § Bouton pause & encoche).
 * Deux arcs symétriques qui se referment en pointe sur le bord gauche, bombement maximal au
 * centre. Tracé de référence (boîte 34 × 210) mis à l'échelle de la taille réelle du composant.
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
            cubicTo(w * 18f / 34f, h * 62f / 210f, w, h * 82f / 210f, w, h * 105f / 210f)
            cubicTo(w, h * 128f / 210f, w * 18f / 34f, h * 148f / 210f, 0f, h)
            close()
        }
        return Outline.Generic(path)
    }
}
