package dev.alek6dev.chessclock.ui.theme

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import dev.alek6dev.chessclock.R

/**
 * Deux familles de la planche : Instrument Serif (chronos, titres) et EB Garamond
 * (interface, libellés). Fichiers statiques embarqués dans res/font, licence SIL OFL.
 */
object ChessClockFonts {
    val InstrumentSerif = FontFamily(
        Font(R.font.instrument_serif_regular, FontWeight.Normal),
    )

    val EBGaramond = FontFamily(
        Font(R.font.eb_garamond_regular, FontWeight.Normal),
        Font(R.font.eb_garamond_medium, FontWeight.Medium),
        Font(R.font.eb_garamond_semibold, FontWeight.SemiBold),
        Font(R.font.eb_garamond_italic, FontWeight.Normal, FontStyle.Italic),
    )
}
