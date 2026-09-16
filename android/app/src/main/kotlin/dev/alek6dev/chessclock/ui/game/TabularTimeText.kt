package dev.alek6dev.chessclock.ui.game

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import dev.alek6dev.chessclock.ui.theme.ChessClockFonts

/**
 * Affiche un temps "m:ss" sans aucun sursaut : Instrument Serif n'a pas de chiffres à
 * largeur fixe (le "1" fait moins de la moitié de la largeur du "0"), donc un Text() simple
 * change de largeur — et donc de position une fois centré — à chaque tick de seconde.
 * Chaque chiffre est ici posé dans un emplacement de largeur fixe (celle du "0", le plus
 * large), les deux-points et l'espace pris par les minutes restant, eux, naturellement
 * stables sur la plage 0-59.
 */
@Composable
fun TabularTimeText(text: String, color: Color, fontSize: TextUnit, modifier: Modifier = Modifier) {
    val textMeasurer = rememberTextMeasurer()
    val style = TextStyle(fontFamily = ChessClockFonts.InstrumentSerif, fontSize = fontSize)
    val digitWidthPx = remember(fontSize) {
        (0..9).maxOf { textMeasurer.measure(it.toString(), style).size.width }
    }
    val digitWidth = with(LocalDensity.current) { digitWidthPx.toDp() }

    Row(modifier = modifier) {
        for (char in text) {
            if (char.isDigit()) {
                Box(modifier = Modifier.width(digitWidth), contentAlignment = Alignment.Center) {
                    Text(text = char.toString(), color = color, style = style)
                }
            } else {
                Text(text = char.toString(), color = color, style = style)
            }
        }
    }
}
