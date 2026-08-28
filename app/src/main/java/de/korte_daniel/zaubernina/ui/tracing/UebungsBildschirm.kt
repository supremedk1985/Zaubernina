package de.korte_daniel.zaubernina.ui.tracing

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke as StrichStil
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.korte_daniel.zaubernina.data.grundschrift.glyph
import de.korte_daniel.zaubernina.domain.Glyph
import de.korte_daniel.zaubernina.logic.Genauigkeit
import de.korte_daniel.zaubernina.logic.StrokeTracker
import de.korte_daniel.zaubernina.logic.Zug
import de.korte_daniel.zaubernina.ui.components.boxabbildungFuer
import de.korte_daniel.zaubernina.ui.components.SCHABLONE_BREITE
import de.korte_daniel.zaubernina.ui.components.Funkenfeld
import de.korte_daniel.zaubernina.ui.components.sternPunkte
import de.korte_daniel.zaubernina.ui.components.teilPfad
import de.korte_daniel.zaubernina.ui.components.zeichneHilfen
import de.korte_daniel.zaubernina.ui.components.zeichneSchablone
import de.korte_daniel.zaubernina.ui.components.zeichneSchreiblinien
import de.korte_daniel.zaubernina.ui.components.zeichneSpur
import de.korte_daniel.zaubernina.ui.components.zeichneTupfer
import de.korte_daniel.zaubernina.ui.components.zuPath
import de.korte_daniel.zaubernina.ui.theme.Thema
import de.korte_daniel.zaubernina.ui.theme.ZauberTheme
import de.korte_daniel.zaubernina.ui.theme.ZauberText
import androidx.compose.foundation.Canvas

/**
 * Der Kernbildschirm: ein Zeichen groß in der Mitte, Schablone mit Startpunkt und Pfeilen,
 * und die Zauberlinie unter dem Finger.
 *
 * Noch nicht drin (kommt nach der ersten Rückmeldung): die Stufen „Zeigen" und „Aus dem
 * Kopf", Töne, Haptik, die Zeichenauswahl und der Elternbereich. Die Themenwahl unten ist
 * ein Behelf zum Ausprobieren — sie gehört später in den Elternbereich.
 */
@Composable
fun UebungsBildschirm(
    wort: String,
    buchstabeIndex: Int,
    thema: Thema,
    genauigkeit: Genauigkeit,
    onThemaWechsel: (Thema) -> Unit,
    onBuchstabeFertig: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val farben = ZauberTheme.farben
    val zeichen = wort.getOrNull(buchstabeIndex) ?: return
    val glyphe: Glyph = glyph(zeichen) ?: return

    val abgetastet = remember(glyphe) { glyphe.striche.map { it.abtasten(120) } }
    // Die tatsächliche Breite dieses Zeichens — sie bestimmt, wie groß es gezeigt wird.
    val zeichenBreite = remember(glyphe) {
        val alle = abgetastet.flatten()
        alle.minOf { it.x }..alle.maxOf { it.x }
    }
    var strichIndex by remember(glyphe) { mutableIntStateOf(0) }
    var anteil by remember(glyphe) { mutableFloatStateOf(0f) }
    var zustand by remember(glyphe) { mutableStateOf(Zug.WARTET) }
    var finger by remember(glyphe) { mutableStateOf<Offset?>(null) }
    var glypheFertig by remember(glyphe) { mutableStateOf(false) }

    val tracker = remember(glyphe, strichIndex, genauigkeit) {
        StrokeTracker(
            punkte = abgetastet[strichIndex],
            toleranz = genauigkeit.toleranz,
            tupfer = glyphe.striche[strichIndex].tupfer,
        )
    }

    val feld = remember { Funkenfeld() }
    var takt by remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) {
        var vorher = 0L
        while (true) {
            withFrameNanos { jetzt ->
                val dt = if (vorher == 0L) 0f else (jetzt - vorher) / 1_000_000_000f
                vorher = jetzt
                feld.schritt(dt.coerceAtMost(0.05f))
                takt++
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        // Das Wort mit dem gerade geübten Buchstaben hervorgehoben.
        Row(
            modifier = Modifier.fillMaxWidth().height(56.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            wort.forEachIndexed { i, c ->
                ZauberText(
                    text = c.toString(),
                    groesse = 30.sp,
                    gewicht = if (i == buchstabeIndex) FontWeight.SemiBold else FontWeight.Medium,
                    farbe = when {
                        i == buchstabeIndex -> farben.akzent
                        i < buchstabeIndex -> farben.schrift
                        else -> farben.schriftSchwach
                    },
                    modifier = Modifier.padding(horizontal = 3.dp),
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center,
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(glyphe, strichIndex, genauigkeit, zeichenBreite) {
                        val ab = boxabbildungFuer(zeichenBreite, size.width.toFloat(), size.height.toFloat())
                        awaitEachGesture {
                            val down = awaitFirstDown()
                            zustand = tracker.senke(ab.zurBox(down.position))
                            anteil = tracker.anteil
                            finger = down.position
                            down.consume()

                            if (tracker.fertig) {
                                feld.speise(down.position.x, down.position.y, farben.funken, anzahl = 18, staerke = 1.4f)
                            }

                            while (!tracker.fertig) {
                                val ereignis = awaitPointerEvent()
                                val wechsel = ereignis.changes.firstOrNull() ?: break
                                if (!wechsel.pressed) break
                                zustand = tracker.ziehe(ab.zurBox(wechsel.position))
                                anteil = tracker.anteil
                                finger = wechsel.position
                                if (zustand == Zug.LAEUFT || zustand == Zug.FERTIG) {
                                    feld.speise(wechsel.position.x, wechsel.position.y, farben.funken)
                                }
                                wechsel.consume()
                            }

                            if (tracker.fertig) {
                                finger?.let { feld.speise(it.x, it.y, farben.funken, anzahl = 14, staerke = 1.3f) }
                                if (strichIndex < glyphe.striche.lastIndex) {
                                    strichIndex++
                                    anteil = 0f
                                    zustand = Zug.WARTET
                                } else {
                                    glypheFertig = true
                                }
                            } else {
                                zustand = tracker.hebe()
                                anteil = 0f
                            }
                            finger = null
                        }
                    },
            ) {
                @Suppress("UNUSED_EXPRESSION") takt // Abhängigkeit: neu zeichnen bei jedem Bild
                val ab = boxabbildungFuer(zeichenBreite, size.width, size.height)

                zeichneSchreiblinien(ab, farben)

                // Schablonen aller Striche — auch der noch nicht geschriebenen.
                glyphe.striche.forEachIndexed { i, strich ->
                    if (strich.tupfer) {
                        drawCircle(
                            color = farben.schablone,
                            radius = ab.laenge(SCHABLONE_BREITE / 2f),
                            center = ab.zumSchirm(strich.start),
                        )
                    } else {
                        zeichneSchablone(strich.zuPath(ab), ab, farben)
                    }
                }

                // Fertig geschriebene Striche stehen voll da.
                for (i in 0 until strichIndex) {
                    val strich = glyphe.striche[i]
                    if (strich.tupfer) {
                        zeichneTupfer(ab.zumSchirm(strich.start), ab, farben.spurKern.first())
                    } else {
                        zeichneSpur(strich.zuPath(ab), ab, farben)
                    }
                }
                if (glypheFertig) {
                    val strich = glyphe.striche.last()
                    if (strich.tupfer) {
                        zeichneTupfer(ab.zumSchirm(strich.start), ab, farben.spurKern.first())
                    } else {
                        zeichneSpur(strich.zuPath(ab), ab, farben)
                    }
                }

                // Der laufende Strich, so weit er geschrieben ist.
                if (!glypheFertig) {
                    val strich = glyphe.striche[strichIndex]
                    val deckkraft = if (zustand == Zug.DANEBEN) 0.4f else 1f
                    if (!strich.tupfer && anteil > 0f) {
                        zeichneSpur(teilPfad(strich.zuPath(ab), anteil), ab, farben, deckkraft)
                    }
                    // Hilfen nur für den Strich, der gerade dran ist.
                    if (strich.tupfer) {
                        drawCircle(
                            color = farben.startpunkt,
                            radius = ab.laenge(52f),
                            center = ab.zumSchirm(strich.start),
                            style = StrichStil(width = ab.laenge(9f)),
                        )
                    } else {
                        zeichneHilfen(strich.zuPath(ab), strich, ab, farben)
                    }
                }

                // Der leuchtende Kopf unter dem Finger.
                finger?.takeIf { zustand == Zug.LAEUFT || zustand == Zug.FERTIG }?.let { f ->
                    drawCircle(farben.spurSchein, ab.laenge(74f), f, alpha = 0.45f)
                    drawCircle(farben.spurKern.first(), ab.laenge(42f), f)
                }

                // Die Funken.
                for (funke in feld.funken) {
                    val punkte = sternPunkte(
                        Offset(funke.x, funke.y),
                        funke.groesse * ab.skala * funke.frische * 1.7f,
                        funke.drehung,
                    )
                    val pfad = Path().apply {
                        moveTo(punkte[0].x, punkte[0].y)
                        for (i in 1 until punkte.size) lineTo(punkte[i].x, punkte[i].y)
                        close()
                    }
                    drawPath(pfad, funke.farbe, alpha = funke.frische)
                }
            }

            if (glypheFertig) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(farben.ecke * 1.5f))
                        .background(if (farben.dunkel) Color(0xCC0A0C22) else Color(0xE6FFF9EE))
                        .padding(horizontal = 34.dp, vertical = 26.dp),
                ) {
                    ZauberText(
                        text = "Toll gemacht!",
                        groesse = 34.sp,
                        gewicht = FontWeight.Bold,
                        farbe = farben.schrift,
                    )
                    Box(
                        modifier = Modifier
                            .padding(top = 18.dp)
                            .clip(RoundedCornerShape(farben.ecke))
                            .background(farben.akzent)
                            .clickable {
                                glypheFertig = false
                                strichIndex = 0
                                anteil = 0f
                                zustand = Zug.WARTET
                                feld.leeren()
                                onBuchstabeFertig()
                            }
                            .padding(horizontal = 30.dp, vertical = 15.dp),
                    ) {
                        ZauberText(
                            text = "Weiter",
                            groesse = 21.sp,
                            gewicht = FontWeight.Bold,
                            farbe = farben.aufAkzent,
                        )
                    }
                }
            }
        }

        // BEHELF: Themenwahl zum Ausprobieren. Gehört später in den Elternbereich.
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Thema.entries.forEach { t ->
                val aktiv = t == thema
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(farben.ecke))
                        .background(if (aktiv) farben.akzent else farben.flaeche)
                        .border(
                            width = if (farben.flaecheRand == Color.Transparent) 0.dp else 1.5.dp,
                            color = if (aktiv) Color.Transparent else farben.flaecheRand,
                            shape = RoundedCornerShape(farben.ecke),
                        )
                        .clickable { onThemaWechsel(t) }
                        .padding(vertical = 13.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    ZauberText(
                        text = t.anzeigename,
                        groesse = 13.sp,
                        gewicht = FontWeight.Medium,
                        farbe = if (aktiv) farben.aufAkzent else farben.schriftSchwach,
                    )
                }
            }
        }
    }
}
