package dev.alek6dev.chessclock.ui.game

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.Dp
import dev.alek6dev.chessclock.R

/** Les 4 pions fournis (svg/Pion-*.svg), importés tels quels en Vector Drawable. */
enum class PawnVariant {
    WHITE_FULL,
    WHITE_CONTOUR,
    BLACK_FULL,
    BLACK_CONTOUR,
}

/**
 * Pion d'échecs — assets fournis par l'utilisateur (svg/Pion-*.svg → res/drawable), pas de
 * tracé recréé à la main. "Contour" pour les cas où le pion se fond avec le fond de sa propre
 * zone, "Full" quand le contraste est déjà suffisant (badge adversaire).
 */
@Composable
fun PawnIcon(variant: PawnVariant, height: Dp, alpha: Float = 1f, modifier: Modifier = Modifier) {
    val drawableId = when (variant) {
        PawnVariant.WHITE_FULL -> R.drawable.pion_blanc_full
        PawnVariant.WHITE_CONTOUR -> R.drawable.pion_blanc_contour_noir
        PawnVariant.BLACK_FULL -> R.drawable.pion_noir_full
        PawnVariant.BLACK_CONTOUR -> R.drawable.pion_noir_contour_blanc
    }
    Image(
        imageVector = ImageVector.vectorResource(id = drawableId),
        contentDescription = null,
        modifier = modifier
            .width(height * (75f / 103f))
            .height(height)
            .alpha(alpha),
    )
}
