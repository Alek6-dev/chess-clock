package dev.alek6dev.chessclock.ui.game

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp

/**
 * Pion d'échecs vectoriel — exception assumée au principe "aucune pièce dessinée" de la
 * planche, à la demande explicite du produit pour identifier visuellement les chronos par
 * camp. Tracé calqué sur les maquettes officielles (tête ronde, collerette, taille cintrée,
 * base évasée à bord plat), dans une boîte de référence 100 x 140 mise à l'échelle par
 * [height].
 *
 * [contourColor] : toujours le ton opposé au fond du pion (ivoire pour un pion encre, encre
 * pour un pion ivoire). Sur son propre camp, le fond du pion se fond avec le fond de sa zone
 * (même couleur) — seul ce contour le rend visible.
 */
@Composable
fun PawnIcon(fillColor: Color, contourColor: Color, height: Dp, modifier: Modifier = Modifier) {
    val width = height * (100f / 140f)
    Canvas(modifier = modifier.size(width = width, height = height)) {
        val u = size.height / 140f
        val body = Path().apply {
            moveTo(50 * u, 0f)
            cubicTo(66 * u, 0f, 74 * u, 10 * u, 74 * u, 20 * u)
            cubicTo(74 * u, 28 * u, 70 * u, 34 * u, 70 * u, 36 * u)
            cubicTo(78 * u, 40 * u, 83 * u, 44 * u, 83 * u, 54 * u)
            lineTo(83 * u, 73 * u)
            cubicTo(83 * u, 76 * u, 76 * u, 76 * u, 63 * u, 77 * u)
            cubicTo(68 * u, 85 * u, 74 * u, 95 * u, 80 * u, 116 * u)
            cubicTo(85 * u, 122 * u, 91 * u, 126 * u, 91 * u, 131 * u)
            lineTo(91 * u, 140 * u)
            lineTo(9 * u, 140 * u)
            lineTo(9 * u, 131 * u)
            cubicTo(9 * u, 126 * u, 15 * u, 122 * u, 20 * u, 116 * u)
            cubicTo(26 * u, 95 * u, 32 * u, 85 * u, 37 * u, 77 * u)
            cubicTo(24 * u, 76 * u, 17 * u, 76 * u, 17 * u, 73 * u)
            lineTo(17 * u, 54 * u)
            cubicTo(17 * u, 44 * u, 22 * u, 40 * u, 30 * u, 36 * u)
            cubicTo(26 * u, 34 * u, 26 * u, 28 * u, 26 * u, 20 * u)
            cubicTo(26 * u, 10 * u, 34 * u, 0f, 50 * u, 0f)
            close()
        }
        drawPath(body, color = fillColor)
        drawPath(body, color = contourColor, style = Stroke(width = 2 * u))
    }
}
