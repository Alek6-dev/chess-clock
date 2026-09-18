package dev.alek6dev.chessclock.ui.splash

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.alek6dev.chessclock.ui.theme.ChessClockColors
import dev.alek6dev.chessclock.ui.theme.ChessClockFonts
import dev.alek6dev.chessclock.ui.theme.RuledBackground

/**
 * Écran d'accueil (maquette officielle) : losange, wordmark, invite à toucher l'écran, orbe
 * qui pulse pour signaler que l'écran entier est tapable.
 */
@Composable
fun SplashScreen(onContinue: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onContinue,
            ),
    ) {
        RuledBackground(
            baseColor = ChessClockColors.InkNight,
            lineColor = ChessClockColors.LineRule.copy(alpha = 0.25f),
            modifier = Modifier.fillMaxSize(),
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.align(Alignment.Center),
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .graphicsLayer { rotationZ = 45f }
                    .background(ChessClockColors.Paper),
            )
            Spacer(Modifier.height(24.dp))
            Text(
                text = "Chess Clock",
                color = ChessClockColors.Ivory,
                fontFamily = ChessClockFonts.InstrumentSerif,
                fontSize = 48.sp,
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 64.dp),
        ) {
            Text(
                text = "Toucher l'écran pour commencer",
                color = ChessClockColors.TextMuted,
                fontFamily = ChessClockFonts.EBGaramond,
                fontSize = 15.sp,
            )
            Spacer(Modifier.height(20.dp))
            PulsingOrb()
        }
    }
}

@Composable
private fun PulsingOrb(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "splash-orb-pulse")
    val scale by transition.animateFloat(
        initialValue = 1f,
        targetValue = 1.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400, easing = LinearOutSlowInEasing),
        ),
        label = "scale",
    )
    val ringAlpha by transition.animateFloat(
        initialValue = 0.5f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400, easing = LinearOutSlowInEasing),
        ),
        label = "alpha",
    )

    Box(modifier = modifier.size(56.dp), contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    alpha = ringAlpha
                }
                .background(ChessClockColors.Ivory, CircleShape),
        )
        Box(
            modifier = Modifier
                .size(56.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(ChessClockColors.Ivory, ChessClockColors.Brass),
                    ),
                    CircleShape,
                ),
        )
    }
}
