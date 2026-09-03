package de.korte_daniel.zaubernina.ui.geschichte

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import de.korte_daniel.zaubernina.data.Geschichte
import de.korte_daniel.zaubernina.logic.SilbenWort
import de.korte_daniel.zaubernina.logic.zerlegeSeite
import de.korte_daniel.zaubernina.logic.zerlegeZeile
import de.korte_daniel.zaubernina.ui.Vorleser
import de.korte_daniel.zaubernina.ui.components.EmojiText
import de.korte_daniel.zaubernina.ui.components.GrosserKnopf
import de.korte_daniel.zaubernina.ui.components.Kopfzeile
import de.korte_daniel.zaubernina.ui.components.Sternreihe
import de.korte_daniel.zaubernina.ui.theme.LocalZauberSchrift
import de.korte_daniel.zaubernina.ui.theme.ZauberMasse
import de.korte_daniel.zaubernina.ui.theme.ZauberText
import de.korte_daniel.zaubernina.ui.theme.ZauberTheme

/**
 * Lea liest vor. Große Druckschrift, kurze Zeilen, Silben abwechselnd gefärbt (Fibel-Prinzip),
 * je Seite ein Bild. Bleibt sie an einem Wort hängen, tippt sie es an — die App spricht NUR
 * dieses Wort, leise und ohne Aufsehen. Wie oft das nötig war, bekommen die Eltern zu sehen,
 * nicht das Publikum. Kein Zeitdruck: die Minuten werden nur am Ende genannt, als Leistung.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun VorleseBildschirm(
    geschichte: Geschichte,
    laenge: Int,
    leserName: String,
    silbenFaerben: Boolean,
    vorleser: Vorleser?,
    onZurueck: () -> Unit,
    onFertig: (minuten: Int, hilfen: Map<String, Int>) -> Unit,
    modifier: Modifier = Modifier,
) {
    val farben = ZauberTheme.farben
    val m = ZauberMasse.aktuell
    val seiten = geschichte.seitenFuer(laenge)
    val bilder = geschichte.bilder
    // Seite 0 = Titelblatt, 1..n = Text, n+1 = Ende
    var seite by remember { mutableIntStateOf(0) }
    val start = remember { System.currentTimeMillis() }
    val hilfen = remember { mutableStateMapOf<String, Int>() }
    val silbenFarben = listOf(farben.schrift, farben.akzent)

    fun hilfe(wort: SilbenWort) {
        val rein = wort.rein
        if (rein.isEmpty()) return
        vorleser?.sprich(rein)
        hilfen[rein] = (hilfen[rein] ?: 0) + 1
    }

    Column(
        modifier = modifier.fillMaxSize().padding(horizontal = m.dp(18), vertical = m.dp(12)),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Kopfzeile(
            titel = when {
                seite == 0 -> geschichte.titel.replace("|", "")
                seite > seiten.size -> "Ende"
                else -> "Seite $seite von ${seiten.size}"
            },
            onZurueck = onZurueck,
        )

        Column(
            modifier = Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            when {
                seite == 0 -> {
                    EmojiText(geschichte.tier.symbol, m.sp(96), Modifier.padding(top = m.dp(20)))
                    Silbentext(zerlegeZeile(geschichte.titel), m.sp(34), silbenFaerben, silbenFarben, ::hilfe, Modifier.padding(top = m.dp(18)))
                    ZauberText("Es liest: $leserName", m.sp(18), farben.schriftSchwach, modifier = Modifier.padding(top = m.dp(22)))
                    ZauberText(
                        text = "Bleibst du an einem Wort hängen? Tipp es an, dann sage ich es dir.",
                        groesse = m.sp(15),
                        farbe = farben.schriftSchwach,
                        modifier = Modifier.padding(top = m.dp(8), start = m.dp(12), end = m.dp(12)),
                    )
                }
                seite > seiten.size -> {
                    val minuten = (((System.currentTimeMillis() - start) / 60_000L).toInt()).coerceAtLeast(1)
                    EmojiText("🎉", m.sp(80), Modifier.padding(top = m.dp(24)))
                    ZauberText("Geschafft, $leserName!", m.sp(28), farben.schrift, FontWeight.Bold, Modifier.padding(top = m.dp(12)))
                    ZauberText("Du hast $minuten ${if (minuten == 1) "Minute" else "Minuten"} vorgelesen.", m.sp(18), farben.schriftSchwach, modifier = Modifier.padding(top = m.dp(8)))
                    Sternreihe(1, Modifier.padding(top = m.dp(20)))
                }
                else -> {
                    val bild = bilder.getOrNull(seite - 1).orEmpty()
                    if (bild.isNotEmpty()) EmojiText(bild, m.sp(52), Modifier.padding(top = m.dp(8), bottom = m.dp(14)))
                    val zeilen = zerlegeSeite(seiten[seite - 1])
                    Column(verticalArrangement = Arrangement.spacedBy(m.dp(10)), modifier = Modifier.fillMaxWidth()) {
                        zeilen.forEach { zeile ->
                            Silbentext(zeile, m.sp(30), silbenFaerben, silbenFarben, ::hilfe, Modifier.fillMaxWidth())
                        }
                    }
                }
            }
        }

        when {
            seite > seiten.size -> GrosserKnopf("Fertig", onClick = {
                val minuten = (((System.currentTimeMillis() - start) / 60_000L).toInt()).coerceAtLeast(1)
                onFertig(minuten, hilfen.toMap())
            })
            else -> Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = m.dp(8)),
                horizontalArrangement = Arrangement.spacedBy(m.dp(12)),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (seite > 0) {
                    // Bewusst KEIN zweiter Pfeil wie oben links (der beendet) - eine Seite
                    // zurück ist ein kleiner Knopf mit Buchsymbol.
                    Box(
                        modifier = Modifier
                            .size(m.dp(64))
                            .clip(RoundedCornerShape(farben.ecke * 1.4f))
                            .background(farben.flaeche)
                            .clickable { seite-- },
                        contentAlignment = Alignment.Center,
                    ) { ZauberText("◀", m.sp(20), farben.schriftSchwach) }
                }
                GrosserKnopf(
                    text = when {
                        seite == 0 -> "Los geht's"
                        seite == seiten.size -> "Ende"
                        else -> "Weiter"
                    },
                    onClick = { seite++ },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

/**
 * Eine Zeile Wörter, jedes Wort ein Block aus Silben in Wechselfarbe. Die Blöcke fließen —
 * auf dem Handy in zwei Zeilen, auf dem Tablet in einer. Antippen eines Wortes = Hilfe.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun Silbentext(
    woerter: List<SilbenWort>,
    groesse: androidx.compose.ui.unit.TextUnit,
    faerben: Boolean,
    farben: List<androidx.compose.ui.graphics.Color>,
    onWort: (SilbenWort) -> Unit,
    modifier: Modifier = Modifier,
) {
    val m = ZauberMasse.aktuell
    val schrift = LocalZauberSchrift.current
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(m.dp(10)),
        verticalArrangement = Arrangement.spacedBy(m.dp(4)),
    ) {
        woerter.forEach { wort ->
            val text = buildAnnotatedString {
                wort.silben.forEachIndexed { i, silbe ->
                    val farbe = if (faerben) farben[i % farben.size] else farben[0]
                    withStyle(SpanStyle(color = farbe)) { append(silbe) }
                }
            }
            Box(modifier = Modifier.clickable { onWort(wort) }.padding(vertical = m.dp(2))) {
                BasicText(
                    text = text,
                    style = TextStyle(fontSize = groesse, fontWeight = FontWeight.Medium, fontFamily = schrift, lineHeight = groesse * 1.25f),
                )
            }
        }
    }
}
