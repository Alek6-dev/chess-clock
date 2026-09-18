package dev.alek6dev.chessclock.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.alek6dev.chessclock.ui.theme.ChessClockColors
import dev.alek6dev.chessclock.ui.theme.ChessClockFonts
import kotlin.math.roundToInt

private val ITEM_HEIGHT = 50.dp

/**
 * Roue à défilement/snap, mesurée sur les maquettes officielles : la valeur sélectionnée a
 * son propre encart plein derrière elle et sa propre couleur de texte (34pt) — un
 * NumberPicker natif ne peut pas donner deux couleurs de texte différentes dans une même
 * roue (valeur sélectionnée vs voisines), d'où ce composant en Compose pur.
 */
@Composable
fun WheelNumberPicker(
    range: IntRange,
    value: Int,
    onValueChange: (Int) -> Unit,
    boxColor: Color = ChessClockColors.InkSurface,
    selectedTextColor: Color = ChessClockColors.Ivory,
    modifier: Modifier = Modifier,
) {
    val values = remember(range) { range.toList() }
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = (value - range.first).coerceIn(0, values.lastIndex))
    val flingBehavior = rememberSnapFlingBehavior(listState)
    val density = LocalDensity.current
    val itemHeightPx = with(density) { ITEM_HEIGHT.toPx() }

    val centerIndex by remember {
        derivedStateOf {
            val offsetFraction = listState.firstVisibleItemScrollOffset / itemHeightPx
            (listState.firstVisibleItemIndex + offsetFraction.roundToInt()).coerceIn(0, values.lastIndex)
        }
    }

    // La roue elle-même fait autorité une fois le scroll posé : on ne remonte la valeur au
    // parent qu'au repos, jamais pendant le défilement.
    LaunchedEffect(listState.isScrollInProgress) {
        if (!listState.isScrollInProgress) {
            val settled = values.getOrNull(centerIndex) ?: return@LaunchedEffect
            if (settled != value) onValueChange(settled)
        }
    }

    // Synchronise un changement externe (ex. recocher "temps identique") sans court-circuiter
    // un défilement en cours de l'utilisateur.
    LaunchedEffect(value) {
        if (!listState.isScrollInProgress) {
            val idx = (value - range.first).coerceIn(0, values.lastIndex)
            if (idx != centerIndex) listState.scrollToItem(idx)
        }
    }

    Box(modifier = modifier.height(ITEM_HEIGHT * 3), contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .width(64.dp)
                .height(ITEM_HEIGHT)
                .background(boxColor),
        )
        LazyColumn(
            state = listState,
            flingBehavior = flingBehavior,
            contentPadding = PaddingValues(vertical = ITEM_HEIGHT),
            modifier = Modifier.fillMaxSize(),
        ) {
            itemsIndexed(values) { index, v ->
                val isSelected = index == centerIndex
                Box(modifier = Modifier.fillMaxWidth().height(ITEM_HEIGHT), contentAlignment = Alignment.Center) {
                    Text(
                        text = v.toString().padStart(2, '0'),
                        color = if (isSelected) selectedTextColor else ChessClockColors.InkSurface,
                        fontFamily = ChessClockFonts.InstrumentSerif,
                        fontSize = if (isSelected) 34.sp else 24.sp,
                    )
                }
            }
        }
    }
}
