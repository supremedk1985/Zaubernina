package de.korte_daniel.zaubernina.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke as StrichStil
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import de.korte_daniel.zaubernina.ui.theme.ZauberMasse
import de.korte_daniel.zaubernina.ui.theme.ZauberText
import de.korte_daniel.zaubernina.ui.theme.ZauberTheme

/**
 * Die geteilten Bausteine der neuen Bildschirme. Vorher zeichnete jeder Bildschirm seinen
 * Zurück-Pfeil und seine Sterne selbst (dreimal derselbe Stern in drei Dateien). Alles hier
 * skaliert über [ZauberMasse] mit — Tablet und Handy aus einer Quelle.
 */

/** Runder Zurück-Knopf mit Pfeil — in jedem Bildschirm an derselben Stelle: oben links. */
@Composable
fun ZurueckKnopf(onClick: () -> Unit, modifier: Modifier = Modifier) {
    val farben = ZauberTheme.farben
    val m = ZauberMasse.aktuell
    Box(
        modifier = modifier
            .size(m.dp(48))
            .clip(CircleShape)
            .background(farben.flaeche)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.size(m.dp(20))) {
            val pfad = Path().apply {
                moveTo(size.width * 0.68f, size.height * 0.12f)
                lineTo(size.width * 0.30f, size.height * 0.5f)
                lineTo(size.width * 0.68f, size.height * 0.88f)
            }
            drawPath(pfad, farben.schrift, style = StrichStil(width = 2.6f * density))
        }
    }
}

/** Kopfzeile: Zurück links, Titel in der Mitte, Platz für einen Fortschritt rechts. */
@Composable
fun Kopfzeile(
    titel: String,
    onZurueck: () -> Unit,
    modifier: Modifier = Modifier,
    rechts: @Composable () -> Unit = {},
) {
    val farben = ZauberTheme.farben
    val m = ZauberMasse.aktuell
    Row(
        modifier = modifier.fillMaxWidth().height(m.dp(56)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        ZurueckKnopf(onClick = onZurueck)
        ZauberText(titel, m.sp(16), farben.schriftSchwach)
        Box(modifier = Modifier.size(m.dp(48)), contentAlignment = Alignment.Center) { rechts() }
    }
}

/** Die Punktreihe zum nächsten Stern: [imBlock] von [anzahl] gefüllt. */
@Composable
fun SternFortschritt(imBlock: Int, anzahl: Int, modifier: Modifier = Modifier) {
    val farben = ZauberTheme.farben
    val m = ZauberMasse.aktuell
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(m.dp(3))) {
        repeat(anzahl) { i ->
            Box(
                modifier = Modifier
                    .size(m.dp(9))
                    .clip(CircleShape)
                    .background(if (i < imBlock) farben.akzent else farben.schrift.copy(alpha = 0.15f)),
            )
        }
    }
}

/** Ein großer, voller Knopf über die ganze Breite — der eine „Weiter" eines Bildschirms. */
@Composable
fun GrosserKnopf(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    aktiv: Boolean = true,
) {
    val farben = ZauberTheme.farben
    val m = ZauberMasse.aktuell
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(m.dp(64))
            .clip(RoundedCornerShape(farben.ecke * 1.4f))
            .background(if (aktiv) farben.akzent else farben.flaeche)
            .clickable(enabled = aktiv, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        ZauberText(text, m.sp(21), if (aktiv) farben.aufAkzent else farben.schriftSchwach, FontWeight.SemiBold)
    }
}

/** Eine antippbare Karte; [gewaehlt] zeichnet den Akzentrand. */
@Composable
fun Kachel(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    gewaehlt: Boolean = false,
    inhalt: @Composable () -> Unit,
) {
    val farben = ZauberTheme.farben
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(farben.ecke * 1.2f))
            .background(if (gewaehlt) farben.akzent.copy(alpha = 0.18f) else farben.flaeche)
            .border(
                width = if (gewaehlt) 3.dp else 1.5.dp,
                color = if (gewaehlt) farben.akzent else farben.schrift.copy(alpha = 0.10f),
                shape = RoundedCornerShape(farben.ecke * 1.2f),
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        inhalt()
    }
}

/**
 * Ein Emoji als Bild. Bewusst NICHT die Themenschrift: Emoji kommen aus der Systemschrift,
 * brauchen weder Netz noch Datei und sind auf jedem Android-Gerät gleich verständlich.
 */
@Composable
fun EmojiText(text: String, groesse: TextUnit, modifier: Modifier = Modifier) {
    BasicText(text = text, modifier = modifier, style = TextStyle(fontSize = groesse, textAlign = TextAlign.Center))
}

/** Eine Kachel mit großem Emoji und Beschriftung — die Bausteine der Startseite und Auswahlen. */
@Composable
fun SymbolKachel(
    symbol: String,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    gewaehlt: Boolean = false,
    unterzeile: String? = null,
) {
    val farben = ZauberTheme.farben
    val m = ZauberMasse.aktuell
    Kachel(onClick = onClick, modifier = modifier, gewaehlt = gewaehlt) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = m.dp(10), vertical = m.dp(14)),
        ) {
            EmojiText(symbol, m.sp(46))
            ZauberText(text, m.sp(18), farben.schrift, FontWeight.SemiBold, Modifier.padding(top = m.dp(8)))
            if (unterzeile != null) {
                ZauberText(unterzeile, m.sp(13), farben.schriftSchwach, modifier = Modifier.padding(top = m.dp(2)))
            }
        }
    }
}

/** Ein gefüllter Stern (die eine Sternform der App, siehe [sternPunkte]). */
fun DrawScope.zeichneSternGefuellt(mitte: Offset, radius: Float, farbe: Color) {
    val punkte = sternPunkte(mitte, radius, 0f)
    val pfad = Path().apply {
        moveTo(punkte[0].x, punkte[0].y)
        for (j in 1 until punkte.size) lineTo(punkte[j].x, punkte[j].y)
        close()
    }
    drawPath(pfad, farbe)
}

/** Sternreihe: [anzahl] Sterne nebeneinander, z. B. am Ende eines Spiels. */
@Composable
fun Sternreihe(anzahl: Int, modifier: Modifier = Modifier) {
    val farben = ZauberTheme.farben
    val m = ZauberMasse.aktuell
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(m.dp(8))) {
        repeat(anzahl) {
            Canvas(modifier = Modifier.size(m.dp(44))) {
                zeichneSternGefuellt(Offset(size.width / 2f, size.height / 2f), size.minDimension * 0.46f, farben.akzent)
            }
        }
    }
}
