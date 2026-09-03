package de.korte_daniel.zaubernina.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Der Maßstab der Oberfläche: Auf einem Tablet sind Kinderhände nicht größer, aber der
 * Abstand zum Bildschirm ist es — Schrift und Kacheln wachsen mit, und Raster bekommen
 * eine Spalte mehr. Alle neuen Bildschirme holen ihre Größen hier ab statt aus festen Zahlen.
 */
@Immutable
data class Masse(
    /** Breiter Bildschirm (Tablet, ab 600 dp). */
    val weit: Boolean,
    /** Vergrößerungsfaktor für Schrift und Abstände. */
    val faktor: Float,
) {
    fun sp(wert: Int): TextUnit = (wert * faktor).sp
    fun dp(wert: Int): Dp = (wert * faktor).dp
    /** Spalten für Kachelraster. */
    val spalten: Int get() = if (weit) 3 else 2
}

val LocalMasse = staticCompositionLocalOf { Masse(weit = false, faktor = 1f) }

@Composable
fun rememberMasse(): Masse {
    val breiteDp = LocalConfiguration.current.screenWidthDp
    return remember(breiteDp) {
        when {
            breiteDp >= 840 -> Masse(weit = true, faktor = 1.35f)
            breiteDp >= 600 -> Masse(weit = true, faktor = 1.2f)
            else -> Masse(weit = false, faktor = 1f)
        }
    }
}

object ZauberMasse {
    val aktuell: Masse
        @Composable get() = LocalMasse.current
}
