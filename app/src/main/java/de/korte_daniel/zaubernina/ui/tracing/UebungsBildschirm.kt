package de.korte_daniel.zaubernina.ui.tracing

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import de.korte_daniel.zaubernina.data.grundschrift.BREITESTE_ZEICHENBREITE
import de.korte_daniel.zaubernina.data.grundschrift.glyph
import de.korte_daniel.zaubernina.domain.Glyph
import de.korte_daniel.zaubernina.logic.Genauigkeit
import de.korte_daniel.zaubernina.logic.StrokeTracker
import de.korte_daniel.zaubernina.logic.Zug
import de.korte_daniel.zaubernina.ui.components.Funkenfeld
import de.korte_daniel.zaubernina.ui.components.SCHABLONE_BREITE
import de.korte_daniel.zaubernina.ui.components.boxabbildungFuer
import de.korte_daniel.zaubernina.ui.components.sternPunkte
import de.korte_daniel.zaubernina.ui.components.teilPfad
import de.korte_daniel.zaubernina.ui.components.zeichneHilfen
import de.korte_daniel.zaubernina.ui.components.zeichneSchablone
import de.korte_daniel.zaubernina.ui.components.zeichneSchreiblinien
import de.korte_daniel.zaubernina.ui.components.zeichneSpur
import de.korte_daniel.zaubernina.ui.components.zeichneTupfer
import de.korte_daniel.zaubernina.ui.components.zuPath
import de.korte_daniel.zaubernina.ui.theme.ZauberText
import de.korte_daniel.zaubernina.ui.theme.ZauberTheme

/**
 * Zeichen nachfahren: jedes mit Schablone, Startpunkt, Pfeilen und der Zauberlinie unter
 * dem Finger. Für ein Wort führt die Kachelzeile oben durch die Buchstaben; für eine
 * einzelne Zwischendurch-Zahl gibt es keine Kacheln und keinen Zwischenjubel — nach dem
 * Zeichen geht es direkt weiter (in den Zählmoment).
 *
 * Noch nicht drin: die Stufen „Zeigen" und „Aus dem Kopf", Töne und Haptik.
 */
@Composable
fun UebungsBildschirm(
    wort: String,
    kopfzeile: (buchstabeIndex: Int) -> String,
    genauigkeit: Genauigkeit,
    onZurueck: () -> Unit,
    onFertig: () -> Unit,
    modifier: Modifier = Modifier,
    ohneZwischenjubel: Boolean = false,
) {
    val farben = ZauberTheme.farben
    var buchstabeIndex by remember(wort) { mutableIntStateOf(0) }
    val zeichen = wort.getOrNull(buchstabeIndex) ?: return
    val glyphe: Glyph = glyph(zeichen) ?: return

    val abgetastet = remember(glyphe) { glyphe.striche.map { it.abtasten(120) } }
    val zeichenBreite = remember(glyphe) {
        val alle = abgetastet.flatten()
        alle.minOf { it.x }..alle.maxOf { it.x }
    }
    var strichIndex by remember(glyphe, buchstabeIndex) { mutableIntStateOf(0) }
    var anteil by remember(glyphe, buchstabeIndex) { mutableFloatStateOf(0f) }
    var zustand by remember(glyphe, buchstabeIndex) { mutableStateOf(Zug.WARTET) }
    var finger by remember(glyphe, buchstabeIndex) { mutableStateOf<Offset?>(null) }
    var buchstabeFertig by remember(glyphe, buchstabeIndex) { mutableStateOf(false) }

    val tracker = remember(glyphe, buchstabeIndex, strichIndex, genauigkeit) {
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
        modifier = modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        Row(
            modifier = Modifier.fillMaxWidth().height(50.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(farben.flaeche)
                    .clickable(onClick = onZurueck),
                contentAlignment = Alignment.Center,
            ) {
                Canvas(modifier = Modifier.size(20.dp)) {
                    val pfad = Path().apply {
                        moveTo(size.width * 0.68f, size.height * 0.12f)
                        lineTo(size.width * 0.30f, size.height * 0.5f)
                        lineTo(size.width * 0.68f, size.height * 0.88f)
                    }
                    drawPath(pfad, farben.schrift, style = StrichStil(width = 2.4f * density))
                }
            }
            ZauberText(
                text = kopfzeile(buchstabeIndex),
                groesse = 15.sp,
                farbe = farben.schriftSchwach,
            )
            Box(modifier = Modifier.size(46.dp))
        }

        // Das Wort mit dem gerade geübten Buchstaben hervorgehoben — bei einem
        // einzelnen Zeichen wäre eine einzelne Kachel nur Rauschen.
        if (wort.length > 1) Row(
            modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            wort.forEachIndexed { i, c ->
                val aktiv = i == buchstabeIndex
                Box(
                    modifier = Modifier
                        .padding(horizontal = 3.dp)
                        .size(if (aktiv) 54.dp else 46.dp)
                        .clip(RoundedCornerShape(farben.ecke))
                        .background(
                            when {
                                aktiv -> farben.schrift.copy(alpha = 0.13f)
                                i < buchstabeIndex -> farben.akzent.copy(alpha = 0.18f)
                                else -> farben.flaeche
                            },
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    ZauberText(
                        text = c.toString(),
                        groesse = if (aktiv) 25.sp else 21.sp,
                        gewicht = if (aktiv) FontWeight.SemiBold else FontWeight.Medium,
                        farbe = when {
                            aktiv -> farben.schrift
                            i < buchstabeIndex -> farben.akzent
                            else -> farben.schriftSchwach.copy(alpha = 0.55f)
                        },
                    )
                }
            }
        }

        Box(
            modifier = Modifier.fillMaxWidth().weight(1f),
            contentAlignment = Alignment.Center,
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(glyphe, buchstabeIndex, strichIndex, genauigkeit, zeichenBreite) {
                        val ab = boxabbildungFuer(zeichenBreite, BREITESTE_ZEICHENBREITE, size.width.toFloat(), size.height.toFloat())
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
                                    buchstabeFertig = true
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
                val ab = boxabbildungFuer(zeichenBreite, BREITESTE_ZEICHENBREITE, size.width, size.height)

                zeichneSchreiblinien(ab, farben)

                glyphe.striche.forEach { strich ->
                    if (strich.tupfer) {
                        drawCircle(farben.schablone, ab.laenge(SCHABLONE_BREITE / 2f), ab.zumSchirm(strich.start))
                    } else {
                        zeichneSchablone(strich.zuPath(ab), ab, farben)
                    }
                }

                val fertigeStriche = if (buchstabeFertig) glyphe.striche.size else strichIndex
                for (i in 0 until fertigeStriche) {
                    val strich = glyphe.striche[i]
                    if (strich.tupfer) {
                        zeichneTupfer(ab.zumSchirm(strich.start), ab, farben.spurKern.first())
                    } else {
                        zeichneSpur(strich.zuPath(ab), ab, farben)
                    }
                }

                if (!buchstabeFertig) {
                    val strich = glyphe.striche[strichIndex]
                    val deckkraft = if (zustand == Zug.DANEBEN) 0.4f else 1f
                    if (!strich.tupfer && anteil > 0f) {
                        zeichneSpur(teilPfad(strich.zuPath(ab), anteil), ab, farben, deckkraft)
                    }
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

                finger?.takeIf { zustand == Zug.LAEUFT || zustand == Zug.FERTIG }?.let { f ->
                    drawCircle(farben.spurSchein, ab.laenge(74f), f, alpha = 0.45f)
                    drawCircle(farben.spurKern.first(), ab.laenge(42f), f)
                }

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

            // Ohne Zwischenjubel (Zahlen): direkt weiter, der Zählmoment übernimmt die Bühne.
            if (buchstabeFertig && ohneZwischenjubel) {
                LaunchedEffect(buchstabeFertig) {
                    kotlinx.coroutines.delay(650)
                    onFertig()
                }
            }

            if (buchstabeFertig && !ohneZwischenjubel) {
                val letzter = buchstabeIndex == wort.lastIndex
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(farben.ecke * 1.5f))
                        .background(if (farben.dunkel) Color(0xCC0A0C22) else Color(0xE6FFF9EE))
                        .padding(horizontal = 34.dp, vertical = 26.dp),
                ) {
                    ZauberText(
                        text = if (letzter) "Fertig!" else "Toll gemacht!",
                        groesse = 32.sp,
                        farbe = farben.schrift,
                        gewicht = FontWeight.Bold,
                    )
                    Box(
                        modifier = Modifier
                            .padding(top = 18.dp)
                            .clip(RoundedCornerShape(farben.ecke))
                            .background(farben.akzent)
                            .clickable {
                                if (letzter) {
                                    onFertig()
                                } else {
                                    feld.leeren()
                                    buchstabeIndex++
                                }
                            }
                            .padding(horizontal = 30.dp, vertical = 15.dp),
                    ) {
                        ZauberText(
                            text = if (letzter) "Zeig mir das Wort" else "Weiter",
                            groesse = 20.sp,
                            farbe = farben.aufAkzent,
                            gewicht = FontWeight.Bold,
                        )
                    }
                }
            }
        }
    }
}
