package dev.alek6dev.chessclock.ui.theme

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext

/**
 * Deux retours haptiques normatifs (planche § Mouvement) : léger au passage de main,
 * double à la fin de partie. Rien d'autre dans l'app.
 */
class Haptics(private val vibrator: Vibrator?) {
    fun light() {
        vibrator?.vibrateEffect(VibrationEffect.createOneShot(20, 60))
    }

    fun doubleImpact() {
        vibrator?.vibrateEffect(
            VibrationEffect.createWaveform(longArrayOf(0, 30, 90, 30), intArrayOf(0, 180, 0, 180), -1),
        )
    }

    private fun Vibrator.vibrateEffect(effect: VibrationEffect) {
        if (hasVibrator()) vibrate(effect)
    }
}

fun createHaptics(context: Context): Haptics {
    val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        manager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }
    return Haptics(vibrator)
}

val LocalHaptics = staticCompositionLocalOf<Haptics> {
    error("Haptics non fourni : ChessClockTheme doit englober le contenu.")
}
