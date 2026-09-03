package de.korte_daniel.zaubernina.ui.geschichte

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import de.korte_daniel.zaubernina.data.Geschichte
import de.korte_daniel.zaubernina.data.Tier
import de.korte_daniel.zaubernina.data.geschichte
import de.korte_daniel.zaubernina.data.themenFuer
import de.korte_daniel.zaubernina.logic.vorleseMinuten
import de.korte_daniel.zaubernina.ui.Vorleser
import de.korte_daniel.zaubernina.ui.components.GrosserKnopf
import de.korte_daniel.zaubernina.ui.components.Kopfzeile
import de.korte_daniel.zaubernina.ui.theme.ZauberMasse
import de.korte_daniel.zaubernina.ui.theme.ZauberText
import de.korte_daniel.zaubernina.ui.theme.ZauberTheme

/** Die drei Längen einer Geschichte. */
private val LAENGEN = listOf("kurz", "mittel", "lang")
private val LAENGEN_SYMBOLE = listOf("🕐", "🕑", "🕒")

/**
 * Drei Zahnräder — Held, Thema, Länge — und ein Knopf. Was auf den Rädern steht, gibt es
 * auch als Geschichte: Themen, für die das gewählte Tier keine hat, stehen gar nicht drauf.
 */
@Composable
fun GeschichteWahlBildschirm(
    lieblingstier: Tier,
    vorleser: Vorleser?,
    onStart: (Geschichte, Int) -> Unit,
    onZurueck: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val farben = ZauberTheme.farben
    val m = ZauberMasse.aktuell
    val tiere = Tier.entries
    var tierIndex by remember { mutableIntStateOf(tiere.indexOf(lieblingstier).coerceAtLeast(0)) }
    var themaIndex by remember { mutableIntStateOf(0) }
    var laengeIndex by remember { mutableIntStateOf(1) }

    val tier = tiere[tierIndex]
    val themen = themenFuer(tier)
    val thema = themen.getOrNull(themaIndex.coerceIn(0, (themen.size - 1).coerceAtLeast(0)))
    val gewaehlte = thema?.let { geschichte(tier, it) }

    Column(
        modifier = modifier.fillMaxSize().padding(horizontal = m.dp(18), vertical = m.dp(12)),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Kopfzeile(titel = "Geschichte", onZurueck = onZurueck)
        Column(
            modifier = Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            ZauberText("Dreh die Räder!", m.sp(26), farben.schrift, FontWeight.Bold, Modifier.padding(top = m.dp(10)))
            ZauberText("Wer? Worum geht es? Wie lang?", m.sp(15), farben.schriftSchwach, modifier = Modifier.padding(top = m.dp(4), bottom = m.dp(22)))

            val radGroesse = if (m.weit) m.dp(150) else m.dp(104)
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Top,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Zahnrad(
                    zaehne = tiere.map { Zahn(it.symbol, it.anzeigename) },
                    index = tierIndex,
                    onIndex = { neu ->
                        tierIndex = neu
                        vorleser?.sprich(tiere[neu].anzeigename)
                    },
                    durchmesser = radGroesse,
                )
                Zahnrad(
                    zaehne = themen.map { Zahn(it.symbol, it.anzeigename) },
                    index = themaIndex.coerceIn(0, (themen.size - 1).coerceAtLeast(0)),
                    onIndex = { neu ->
                        themaIndex = neu
                        vorleser?.sprich(themen[neu].anzeigename)
                    },
                    durchmesser = radGroesse,
                )
                Zahnrad(
                    zaehne = LAENGEN.indices.map { i ->
                        val minuten = gewaehlte?.let { vorleseMinuten(it.woerterFuer(i)) }
                        Zahn(LAENGEN_SYMBOLE[i], LAENGEN[i], minuten?.let { "etwa $it Min." })
                    },
                    index = laengeIndex,
                    onIndex = { neu ->
                        laengeIndex = neu
                        vorleser?.sprich(LAENGEN[neu])
                    },
                    durchmesser = radGroesse,
                )
            }

            if (gewaehlte != null) {
                ZauberText(
                    text = gewaehlte.titel.replace("|", ""),
                    groesse = m.sp(21),
                    farbe = farben.schrift,
                    gewicht = FontWeight.SemiBold,
                    modifier = Modifier.padding(top = m.dp(26)),
                )
                ZauberText(
                    text = "${gewaehlte.seitenFuer(laengeIndex).size} Seiten",
                    groesse = m.sp(14),
                    farbe = farben.schriftSchwach,
                    modifier = Modifier.padding(top = m.dp(4)),
                )
            }
            Box(modifier = Modifier.padding(bottom = m.dp(16)))
        }
        GrosserKnopf(
            text = "Vorlesen",
            aktiv = gewaehlte != null,
            onClick = { gewaehlte?.let { onStart(it, laengeIndex) } },
            modifier = Modifier.padding(bottom = m.dp(8)),
        )
    }
}
