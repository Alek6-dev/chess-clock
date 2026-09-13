package dev.alek6dev.chessclock.ui.components

import android.widget.NumberPicker
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

/**
 * Sélecteur "à roue" façon minuteur natif (repose sur android.widget.NumberPicker,
 * le même composant que l'horloge/l'alarme système Android).
 */
@Composable
fun WheelNumberPicker(
    range: IntRange,
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            NumberPicker(context).apply {
                minValue = range.first
                maxValue = range.last
                setFormatter { it.toString().padStart(2, '0') }
                this.value = value
                setOnValueChangedListener { _, _, newValue -> onValueChange(newValue) }
            }
        },
        update = { picker ->
            if (picker.value != value) {
                picker.value = value
            }
        },
    )
}
