package de.korte_daniel.zaubernina.ui.rechnen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke as StrichStil
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.korte_daniel.zaubernina.domain.Klasse
import de.korte_daniel.zaubernina.logic.AUFGABEN_JE_STERN
import de.korte_daniel.zaubernina.logic.Genauigkeit
import de.korte_daniel.zaubernina.logic.Rechenaufgabe
import de.korte_daniel.zaubernina.logic.erzeugeRechenaufgabe
import de.korte_daniel.zaubernina.logic.zerlegeRechenaufgabe
import de.korte_daniel.zaubernina.ui.Vorleser
import de.korte_daniel.zaubernina.ui.components.sternPunkte
import de.korte_daniel.zaubernina.ui.theme.ZauberText
import de.korte_daniel.zaubernina.ui.theme.ZauberTheme
import de.korte_daniel.zaubernina.ui.tracing.UebungsBildschirm

/**
 * Der Rechenmodus. Ablauf je Aufgabe: rechnen (aus drei Vorschlägen wählen), dann die
 * Lösung mit der Zauberlinie SCHREIBEN — das Schreiben bleibt der rote Faden der App.
 * Ein falscher Vorschlag wackelt nur; kein Rot, kein Zähler, keine Strafe.
 *
 * Alle [AUFGABEN_JE_STERN] richtigen Aufgaben gibt es einen Stern ([onRichtig] meldet
 * jede einzelne an den Speicher).
 */
@Composable
fun RechnenBildschirm(
    klasse: Klasse,
    genauigkeit: Genauigkeit,
    richtigBisher: Int,
    onRichtig: () -> Unit,
    onZurueck: () -> Unit,
    modifier: Modifier = Modifier,
    vorleser: Vorleser? = null,
    onFehler: (String) -> Unit = {},
) {
    val farben = ZauberTheme.farben
    var aufgabe by remember { mutableStateOf(erzeugeRechenaufgabe(klasse)) }
    var falschGetippt by remember(aufgabe) { mutableStateOf<Int?>(null) }
    var schreiben by remember(aufgabe) { mutableStateOf(false) }
    var geloest by remember { mutableIntStateOf(0) }

    if (schreiben) {
        // Die Lösung schreiben — derselbe Bildschirm wie überall, nur die Aufgabe steht oben.
        UebungsBildschirm(
            wort = aufgabe.loesung.toString(),
            kopfzeile = { if (aufgabe.menge != null) "Schreib die Zahl" else "${aufgabe.anzeige} = ${aufgabe.loesung}" },
            genauigkeit = genauigkeit,
            onZurueck = { schreiben = false },
            onFertig = {
                onRichtig()
                geloest++
                aufgabe = erzeugeRechenaufgabe(klasse)
            },
            ohneZwischenjubel = aufgabe.loesung < 10,
        )
        return
    }

    Column(
        modifier = modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 12.dp),
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
            ZauberText("Rechnen · ${klasse.anzeigename}", 15.sp, farben.schriftSchwach)
            // Fortschritt zum nächsten Stern
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                val imBlock = (richtigBisher + geloest) % AUFGABEN_JE_STERN
                repeat(AUFGABEN_JE_STERN) { i ->
                    Box(
                        modifier = Modifier
                            .size(9.dp)
                            .clip(CircleShape)
                            .background(if (i < imBlock) farben.akzent else farben.schrift.copy(alpha = 0.15f)),
                    )
                }
            }
        }

        Box(modifier = Modifier.weight(0.5f))

        if (aufgabe.menge != null) {
            ZauberText("Wie viele Sterne siehst du?", 21.sp, farben.schrift, FontWeight.SemiBold)
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 24.dp),
            ) {
                (0 until aufgabe.menge!!).chunked(3).forEach { reihe ->
                    Row(horizontalArrangement = Arrangement.spacedBy(18.dp)) {
                        reihe.forEach { _ ->
                            Canvas(modifier = Modifier.size(58.dp)) {
                                val m = Offset(size.width / 2f, size.height / 2f)
                                val punkte = sternPunkte(m, size.minDimension * 0.44f, 0f)
                                val pfad = Path().apply {
                                    moveTo(punkte[0].x, punkte[0].y)
                                    for (j in 1 until punkte.size) lineTo(punkte[j].x, punkte[j].y)
                                    close()
                                }
                                drawPath(pfad, farben.akzent)
                            }
                        }
                    }
                }
            }
        } else {
            ZauberText(aufgabe.anzeige, 64.sp, farben.schrift, FontWeight.Bold)
            // Der Zahlenstrahl (seit 0.2): Mathe fällt Lea schwer - die Startzahl ist immer
            // markiert, die Sprünge kommen nach dem ersten Fehler, über die Zehn in zwei Bögen.
            zerlegeRechenaufgabe(aufgabe.anzeige)?.let { schritt ->
                Zahlenstrahl(
                    bis = if (klasse == Klasse.KLASSE_1) 10 else 20,
                    schritt = schritt,
                    zeigeSpruenge = falschGetippt != null,
                    modifier = Modifier.fillMaxWidth().padding(top = 18.dp),
                )
            }
        }

        Box(modifier = Modifier.weight(0.5f))

        ZauberText(
            text = if (falschGetippt != null) "Probier es nochmal!" else "Was kommt heraus?",
            groesse = 17.sp,
            farbe = farben.schriftSchwach,
            modifier = Modifier.padding(bottom = 14.dp),
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth().padding(bottom = 18.dp),
        ) {
            aufgabe.auswahl.forEach { wert ->
                val falsch = falschGetippt == wert
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(76.dp)
                        .clip(RoundedCornerShape(farben.ecke * 1.2f))
                        .background(if (falsch) farben.flaeche else farben.akzent.copy(alpha = 0.15f))
                        .border(
                            width = 2.dp,
                            color = if (falsch) farben.schrift.copy(alpha = 0.12f) else farben.akzent.copy(alpha = 0.55f),
                            shape = RoundedCornerShape(farben.ecke * 1.2f),
                        )
                        .clickable {
                            if (wert == aufgabe.loesung) {
                                schreiben = true
                            } else {
                                falschGetippt = wert
                                onFehler(aufgabe.anzeige.ifEmpty { "Menge ${aufgabe.menge}" })
                                zerlegeRechenaufgabe(aufgabe.anzeige)?.let { vorleser?.sprich(it.hinweis()) }
                            }
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    ZauberText(
                        text = "$wert",
                        groesse = 30.sp,
                        gewicht = FontWeight.Bold,
                        farbe = if (falsch) farben.schriftSchwach.copy(alpha = 0.4f) else farben.schrift,
                    )
                }
            }
        }
    }
}
