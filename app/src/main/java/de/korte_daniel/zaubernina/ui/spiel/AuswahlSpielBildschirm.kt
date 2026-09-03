package de.korte_daniel.zaubernina.ui.spiel

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import de.korte_daniel.zaubernina.logic.AUFGABEN_JE_STERN
import de.korte_daniel.zaubernina.logic.Anpassung
import de.korte_daniel.zaubernina.logic.Antwort
import de.korte_daniel.zaubernina.logic.Aufgabenquelle
import de.korte_daniel.zaubernina.logic.Auswahlaufgabe
import de.korte_daniel.zaubernina.ui.Vorleser
import de.korte_daniel.zaubernina.ui.components.EmojiText
import de.korte_daniel.zaubernina.ui.components.Kopfzeile
import de.korte_daniel.zaubernina.ui.components.SternFortschritt
import de.korte_daniel.zaubernina.ui.theme.LocalZauberSchrift
import de.korte_daniel.zaubernina.ui.theme.ZauberMasse
import de.korte_daniel.zaubernina.ui.theme.ZauberText
import de.korte_daniel.zaubernina.ui.theme.ZauberTheme
import kotlinx.coroutines.delay
import java.util.Locale

private enum class Lage { OFFEN, EIN_FEHLER, RICHTIG, GELOEST_GEZEIGT }

/**
 * Der gemeinsame Spielbildschirm von Hören, Lesen und Sprachen: oben die Frage (Buchstabe,
 * Wort, Lautsprecher), unten drei große Antworten. Die Regeln aus den Leitplanken:
 * falsch = Kachel verblasst, Hinweis gesprochen, Aufgabe bleibt; zweiter Fehler = richtige
 * Antwort leuchtet und die Lösung wird gesprochen; richtig = „Genau!" und weiter.
 * Kein Zähler für Fehler, den das Kind sieht. Alle [AUFGABEN_JE_STERN] Richtigen ein Stern.
 */
@Composable
fun AuswahlSpielBildschirm(
    quelle: Aufgabenquelle,
    richtigBisher: Int,
    vorleser: Vorleser?,
    onRichtig: (kennung: String) -> Unit,
    onFehler: (kennung: String) -> Unit,
    onZurueck: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val farben = ZauberTheme.farben
    val m = ZauberMasse.aktuell
    val anpassung = remember { Anpassung() }
    var aufgabe by remember { mutableStateOf(quelle.naechste(anpassung.stufe)) }
    var lage by remember(aufgabe) { mutableStateOf(Lage.OFFEN) }
    var verblasst by remember(aufgabe) { mutableStateOf<String?>(null) }

    fun sprich(text: String, sprache: String = "de") {
        if (text.isNotBlank()) vorleser?.sprich(text, Locale.forLanguageTag(sprache))
    }

    // Die Ansage kommt, sobald die Aufgabe da ist — Nina liest die Frage nicht.
    LaunchedEffect(aufgabe) { sprich(aufgabe.ansage, aufgabe.ansageSprache) }

    // Nach richtig/gelöst kurz zeigen, dann die nächste Aufgabe.
    LaunchedEffect(lage) {
        when (lage) {
            Lage.RICHTIG -> { delay(1_300); aufgabe = quelle.naechste(anpassung.stufe) }
            Lage.GELOEST_GEZEIGT -> { delay(2_600); aufgabe = quelle.naechste(anpassung.stufe) }
            else -> Unit
        }
    }

    fun antworte(antwort: Antwort) {
        if (lage == Lage.RICHTIG || lage == Lage.GELOEST_GEZEIGT) return
        if (antwort.kennung == aufgabe.richtig) {
            lage = Lage.RICHTIG
            anpassung.richtig()
            onRichtig(aufgabe.kennung)
            sprich(listOf("Genau!", "Super!", "Richtig!", "Toll!").random())
        } else if (lage == Lage.OFFEN) {
            lage = Lage.EIN_FEHLER
            verblasst = antwort.kennung
            onFehler(aufgabe.kennung)
            sprich(aufgabe.hinweis, if (quelle.titel == "Hören" || quelle.titel == "Lesen") "de" else aufgabe.ansageSprache)
        } else {
            lage = Lage.GELOEST_GEZEIGT
            anpassung.zweiterFehler()
            onFehler(aufgabe.kennung)
            sprich(aufgabe.loesung)
        }
    }

    Column(
        modifier = modifier.fillMaxSize().padding(horizontal = m.dp(18), vertical = m.dp(12)),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Kopfzeile(titel = quelle.titel, onZurueck = onZurueck) {
            // Nur der Speicherstand zählt - er kommt nach jedem Treffer sofort zurück; ein
            // eigener Zähler dazu hätte jeden Treffer doppelt gezeigt (so gesehen am 03.09.).
            SternFortschritt(imBlock = richtigBisher % AUFGABEN_JE_STERN, anzahl = AUFGABEN_JE_STERN)
        }

        Box(modifier = Modifier.weight(0.45f))

        // ───── die Frage ─────
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            if (aufgabe.frageBild != 0) {
                Image(painter = painterResource(aufgabe.frageBild), contentDescription = null, modifier = Modifier.size(m.dp(120)))
            }
            if (aufgabe.frageSymbol.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .size(m.dp(120))
                        .clip(CircleShape)
                        .background(farben.akzent.copy(alpha = 0.14f))
                        .clickable { sprich(aufgabe.ansage, aufgabe.ansageSprache) },
                    contentAlignment = Alignment.Center,
                ) { EmojiText(aufgabe.frageSymbol, m.sp(56)) }
            }
            if (aufgabe.frageText.isNotEmpty()) {
                val kurz = aufgabe.frageText.replace("|", "").length <= 2
                Silbenzeile(aufgabe.frageText, if (kurz) m.sp(120) else if (aufgabe.frageText.length > 14) m.sp(30) else m.sp(44), farben.schrift, farben.akzent)
            }
            // Lautsprecher: die Ansage noch einmal hören — immer da, nie eine Strafe.
            if (aufgabe.frageSymbol.isEmpty()) {
                Box(
                    modifier = Modifier
                        .padding(top = m.dp(14))
                        .size(m.dp(52))
                        .clip(CircleShape)
                        .background(farben.flaeche)
                        .clickable { sprich(aufgabe.ansage, aufgabe.ansageSprache) },
                    contentAlignment = Alignment.Center,
                ) { EmojiText("🔊", m.sp(22)) }
            }
        }

        Box(modifier = Modifier.weight(0.55f))

        ZauberText(
            text = when (lage) {
                Lage.OFFEN -> "Tipp auf das richtige Bild."
                Lage.EIN_FEHLER -> "Probier es noch einmal!"
                Lage.RICHTIG -> "Genau!"
                Lage.GELOEST_GEZEIGT -> "Das war es. Weiter geht's!"
            },
            groesse = m.sp(17),
            farbe = farben.schriftSchwach,
            modifier = Modifier.padding(bottom = m.dp(14)),
        )

        // ───── die drei Antworten ─────
        Row(
            horizontalArrangement = Arrangement.spacedBy(m.dp(12)),
            modifier = Modifier.fillMaxWidth().padding(bottom = m.dp(12)),
        ) {
            aufgabe.antworten.forEach { antwort ->
                val istRichtig = antwort.kennung == aufgabe.richtig
                val leuchtet = (lage == Lage.RICHTIG || lage == Lage.GELOEST_GEZEIGT) && istRichtig
                val blass = verblasst == antwort.kennung
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(m.dp(132))
                        .alpha(if (blass) 0.35f else 1f)
                        .clip(RoundedCornerShape(farben.ecke * 1.2f))
                        .background(if (leuchtet) farben.akzent.copy(alpha = 0.22f) else farben.flaeche)
                        .border(
                            width = if (leuchtet) 3.dp else 1.5.dp,
                            color = if (leuchtet) farben.akzent else farben.schrift.copy(alpha = 0.10f),
                            shape = RoundedCornerShape(farben.ecke * 1.2f),
                        )
                        .clickable(enabled = !blass) { antworte(antwort) },
                    contentAlignment = Alignment.Center,
                ) {
                    AntwortInhalt(antwort)
                }
            }
        }
    }
}

@Composable
private fun AntwortInhalt(antwort: Antwort) {
    val m = ZauberMasse.aktuell
    val farben = ZauberTheme.farben
    when {
        antwort.bild != 0 -> Image(painter = painterResource(antwort.bild), contentDescription = null, modifier = Modifier.size(m.dp(88)))
        antwort.farbe.isNotEmpty() -> Box(
            modifier = Modifier
                .size(m.dp(72))
                .clip(CircleShape)
                .background(Color(android.graphics.Color.parseColor(antwort.farbe)))
                .border(2.dp, farben.schrift.copy(alpha = 0.2f), CircleShape),
        )
        antwort.symbol.isNotEmpty() -> EmojiText(antwort.symbol, m.sp(56))
        else -> ZauberText(antwort.text, m.sp(26), farben.schrift, FontWeight.SemiBold)
    }
}

/** Ein Wort oder Satz, Silben abwechselnd gefärbt, wenn Trenner | vorhanden sind. */
@Composable
fun Silbenzeile(text: String, groesse: androidx.compose.ui.unit.TextUnit, farbeA: Color, farbeB: Color, modifier: Modifier = Modifier) {
    val schrift = LocalZauberSchrift.current
    val inhalt = buildAnnotatedString {
        text.split(' ').forEachIndexed { wi, wort ->
            if (wi > 0) append(" ")
            wort.split('|').forEachIndexed { i, silbe ->
                withStyle(SpanStyle(color = if (i % 2 == 0) farbeA else farbeB)) { append(silbe) }
            }
        }
    }
    BasicText(
        text = inhalt,
        modifier = modifier,
        style = TextStyle(fontSize = groesse, fontWeight = FontWeight.Bold, fontFamily = schrift, textAlign = TextAlign.Center, lineHeight = groesse * 1.2f),
    )
}
