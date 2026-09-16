package dev.alek6dev.chessclock.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

/**
 * Les écrans de l'app dessinent leurs propres couleurs/formes (angles vifs, matières) plutôt
 * que de s'appuyer sur les styles Material par défaut. Ce thème ne sert qu'à cadrer les éléments
 * système restants (ripple, sélection de texte) sur la même palette.
 */
private val ChessClockColorScheme = darkColorScheme(
    background = ChessClockColors.InkNight,
    surface = ChessClockColors.InkSurface,
    primary = ChessClockColors.Brass,
    onBackground = ChessClockColors.Ivory,
    onSurface = ChessClockColors.Ivory,
)

@Composable
fun ChessClockTheme(content: @Composable () -> Unit) {
    // La planche impose une identité fixe : pas de bascule clair/sombre système.
    val context = LocalContext.current
    val haptics = remember { createHaptics(context) }
    CompositionLocalProvider(LocalHaptics provides haptics) {
        MaterialTheme(
            colorScheme = ChessClockColorScheme,
            content = content,
        )
    }
}
