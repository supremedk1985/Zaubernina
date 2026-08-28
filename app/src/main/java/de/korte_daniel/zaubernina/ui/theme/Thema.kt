package de.korte_daniel.zaubernina.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Die drei Handschriften aus dem Entwurf. Auswählbar im Elternbereich.
 *
 * Aufgebaut wie `BikeColors` in CurruBike: ein Wertesatz je Thema, alle Composables lesen
 * ihn über [ZauberTheme.farben]. Nirgends im Code stehen feste Farben.
 */
enum class Thema(val anzeigename: String, val beschreibung: String) {
    NACHTHIMMEL("Nachthimmel", "Dunkel, die Spur ein Komet mit Funkenschweif"),
    KREIDETAFEL("Kreidetafel", "Grüne Tafel, glühender Kreidestaub"),
    PAPIER("Papier und Farbe", "Helles Heft mit Lineatur, Spur als Farbband"),
}

@Immutable
data class ZauberFarben(
    /** Ein Wert = einfarbig, mehrere = Verlauf von der Mitte nach außen. */
    val hintergrund: List<Color>,
    /** Die breite Schablone, in der das Kind bleiben soll. */
    val schablone: Color,
    /** Feine gestrichelte Mittellinie in der Schablone. Transparent = keine. */
    val mittellinie: Color,
    /** Ein Wert = einfarbige Spur, mehrere = Farbverlauf entlang des Strichs. */
    val spurKern: List<Color>,
    val spurSchein: Color,
    val funken: List<Color>,
    val pfeil: Color,
    val startpunkt: Color,
    /** Hintergrund von Knöpfen und Leisten. */
    val flaeche: Color,
    /** Rand von Knöpfen. Transparent = randlos. */
    val flaecheRand: Color,
    val schrift: Color,
    val schriftSchwach: Color,
    val akzent: Color,
    val aufAkzent: Color,
    /** Schreiblinien im Hintergrund des Zeichens. Transparent = keine. */
    val schreiblinien: Color,
    val ecke: Dp,
    val dunkel: Boolean,
) {
    /** Baut den Hintergrundpinsel für eine konkrete Fläche. */
    fun hintergrundPinsel(size: Size): Brush =
        if (hintergrund.size == 1) {
            Brush.linearGradient(listOf(hintergrund[0], hintergrund[0]))
        } else {
            Brush.radialGradient(
                colors = hintergrund,
                center = Offset(size.width * 0.5f, size.height * 0.14f),
                radius = maxOf(size.width, size.height) * 1.1f,
            )
        }
}

private val NACHTHIMMEL_FARBEN = ZauberFarben(
    hintergrund = listOf(Color(0xFF241A52), Color(0xFF141034), Color(0xFF0A0C22)),
    schablone = Color(0x21FFFFFF),
    mittellinie = Color.Transparent,
    spurKern = listOf(Color(0xFFFFF6DC)),
    spurSchein = Color(0xFFFFC53D),
    funken = listOf(Color(0xFFFFF6DC), Color(0xFFFFC53D), Color(0xFF8FD5FF)),
    pfeil = Color(0x6BFFFFFF),
    startpunkt = Color(0x80FFFFFF),
    flaeche = Color(0x17FFFFFF),
    flaecheRand = Color.Transparent,
    schrift = Color(0xFFFFFFFF),
    schriftSchwach = Color(0x73FFFFFF),
    akzent = Color(0xFFFFC53D),
    aufAkzent = Color(0xFF241A52),
    schreiblinien = Color.Transparent,
    ecke = 16.dp,
    dunkel = true,
)

private val KREIDETAFEL_FARBEN = ZauberFarben(
    hintergrund = listOf(Color(0xFF2E3E35)),
    schablone = Color(0x1FF4F1E4),
    mittellinie = Color(0x42F4F1E4),
    spurKern = listOf(Color(0xFFF4F1E4)),
    spurSchein = Color(0xFFF5E6A8),
    funken = listOf(Color(0xFFF4F1E4), Color(0xFFF5E6A8), Color(0xFFE7E3D2)),
    pfeil = Color(0xFFE8837B),
    startpunkt = Color(0xFFE8837B),
    flaeche = Color(0x00000000),
    flaecheRand = Color(0x59F4F1E4),
    schrift = Color(0xFFF4F1E4),
    schriftSchwach = Color(0x8CF4F1E4),
    akzent = Color(0xFFF5E6A8),
    aufAkzent = Color(0xFF2E3E35),
    schreiblinien = Color.Transparent,
    ecke = 8.dp,
    dunkel = true,
)

private val PAPIER_FARBEN = ZauberFarben(
    hintergrund = listOf(Color(0xFFFFF9EE)),
    schablone = Color(0xFFE8DFC9),
    mittellinie = Color.Transparent,
    // Die Spur läuft durch den Farbverlauf — jeder Strich sieht anders aus.
    spurKern = listOf(Color(0xFFF5A623), Color(0xFFE0562F), Color(0xFF9B4DCA)),
    spurSchein = Color(0x40F5A623),
    funken = listOf(Color(0xFFF5A623), Color(0xFFE0562F), Color(0xFF9B4DCA), Color(0xFF6FB3E0)),
    pfeil = Color(0xFF6FB3E0),
    startpunkt = Color(0xFF6FB3E0),
    flaeche = Color(0xFFFFFFFF),
    flaecheRand = Color(0xFFEDE3CE),
    schrift = Color(0xFF3A352C),
    schriftSchwach = Color(0xFFA79E8A),
    akzent = Color(0xFFE0562F),
    aufAkzent = Color(0xFFFFFFFF),
    schreiblinien = Color(0xFFEFE6D2),
    ecke = 20.dp,
    dunkel = false,
)

fun Thema.farben(): ZauberFarben = when (this) {
    Thema.NACHTHIMMEL -> NACHTHIMMEL_FARBEN
    Thema.KREIDETAFEL -> KREIDETAFEL_FARBEN
    Thema.PAPIER -> PAPIER_FARBEN
}

fun Thema.schrift(): FontFamily = when (this) {
    Thema.NACHTHIMMEL -> Fredoka
    Thema.KREIDETAFEL -> PatrickHand
    Thema.PAPIER -> Baloo2
}

val LocalZauberFarben: ProvidableCompositionLocal<ZauberFarben> =
    staticCompositionLocalOf { NACHTHIMMEL_FARBEN }

val LocalZauberSchrift: ProvidableCompositionLocal<FontFamily> =
    staticCompositionLocalOf { Fredoka }

object ZauberTheme {
    val farben: ZauberFarben
        @Composable get() = LocalZauberFarben.current
    val schrift: FontFamily
        @Composable get() = LocalZauberSchrift.current
}

@Composable
fun ZauberTheme(thema: Thema, inhalt: @Composable () -> Unit) {
    CompositionLocalProvider(
        LocalZauberFarben provides thema.farben(),
        LocalZauberSchrift provides thema.schrift(),
        content = inhalt,
    )
}
