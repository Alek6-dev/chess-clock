package dev.alek6dev.chessclock.ui.game

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import dev.alek6dev.chessclock.ui.theme.ChessClockColors

/**
 * Pion d'échecs vectoriel — exception assumée au principe "aucune pièce dessinée" de la
 * planche, à la demande explicite du produit pour identifier visuellement les chronos par
 * camp. Dessiné à la main (pas d'asset bitmap, pas d'icône stock), tracé de référence dans
 * une boîte 100 x 140 mise à l'échelle par [height].
 */
@Composable
fun PawnIcon(fillColor: Color, height: Dp, modifier: Modifier = Modifier) {
    val width = height * (100f / 140f)
    Canvas(modifier = modifier.size(width = width, height = height)) {
        val u = size.height / 140f
        val body = Path().apply {
            moveTo(50 * u, 4 * u)
            cubicTo(59 * u, 4 * u, 66 * u, 11 * u, 66 * u, 20 * u)
            cubicTo(66 * u, 26 * u, 63.5f * u, 31 * u, 59.5f * u, 34 * u)
            lineTo(62 * u, 40 * u)
            cubicTo(68 * u, 46 * u, 74 * u, 60 * u, 76 * u, 76 * u)
            cubicTo(76.8f * u, 82 * u, 78 * u, 86 * u, 78 * u, 88 * u)
            lineTo(78 * u, 94 * u)
            cubicTo(78 * u, 97.5f * u, 75 * u, 100 * u, 71.5f * u, 100 * u)
            lineTo(28.5f * u, 100 * u)
            cubicTo(25 * u, 100 * u, 22 * u, 97.5f * u, 22 * u, 94 * u)
            lineTo(22 * u, 88 * u)
            cubicTo(22 * u, 86 * u, 23.2f * u, 82 * u, 24 * u, 76 * u)
            cubicTo(26 * u, 60 * u, 32 * u, 46 * u, 38 * u, 40 * u)
            lineTo(40.5f * u, 34 * u)
            cubicTo(36.5f * u, 31 * u, 34 * u, 26 * u, 34 * u, 20 * u)
            cubicTo(34 * u, 11 * u, 41 * u, 4 * u, 50 * u, 4 * u)
            close()
        }
        val strokeWidth = 2 * u
        drawPath(body, color = fillColor)
        drawPath(body, color = ChessClockColors.LineRule, style = Stroke(width = strokeWidth))

        val baseTopLeft = Offset(14 * u, 100 * u)
        val baseSize = Size(72 * u, 15 * u)
        val baseCorner = CornerRadius(2 * u, 2 * u)
        drawRoundRect(color = fillColor, topLeft = baseTopLeft, size = baseSize, cornerRadius = baseCorner)
        drawRoundRect(
            color = ChessClockColors.LineRule,
            topLeft = baseTopLeft,
            size = baseSize,
            cornerRadius = baseCorner,
            style = Stroke(width = strokeWidth),
        )
    }
}
