package dev.alek6dev.chessclock.ui.theme

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Réglure horizontale — seule texture de la planche v1.2 : filets fins réguliers,
 * déclinés plus sombres sur les zones de jeu (cuir) et dans leur ton d'origine sur le papier.
 */
@Composable
fun RuledBackground(
    baseColor: Color,
    lineColor: Color,
    spacing: Dp = 4.dp,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier.fillMaxSize().background(baseColor)) {
        val step = spacing.toPx()
        var y = 0f
        while (y < size.height) {
            drawLine(
                color = lineColor,
                start = Offset(0f, y),
                end = Offset(size.width, y),
                strokeWidth = 1f,
            )
            y += step
        }
    }
}

/** Unique tache d'encre du papier vieilli — jamais plus d'une par surface, en haut à droite. */
@Composable
fun InkStainOverlay(modifier: Modifier = Modifier, color: Color = Color(0x148C3B2E)) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val center = Offset(size.width * 0.74f, size.height * 0.12f)
        val radius = size.minDimension * 0.6f
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(color, Color.Transparent),
                center = center,
                radius = radius,
            ),
            radius = radius,
            center = center,
        )
    }
}
