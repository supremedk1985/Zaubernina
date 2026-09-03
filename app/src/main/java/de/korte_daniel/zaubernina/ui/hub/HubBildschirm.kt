package de.korte_daniel.zaubernina.ui.hub

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import de.korte_daniel.zaubernina.data.BenutzerDaten
import de.korte_daniel.zaubernina.domain.Spiel
import de.korte_daniel.zaubernina.domain.spieleFuer
import de.korte_daniel.zaubernina.ui.Vorleser
import de.korte_daniel.zaubernina.ui.components.AvatarBild
import de.korte_daniel.zaubernina.ui.components.EmojiText
import de.korte_daniel.zaubernina.ui.components.SymbolKachel
import de.korte_daniel.zaubernina.ui.theme.ZauberMasse
import de.korte_daniel.zaubernina.ui.theme.ZauberText
import de.korte_daniel.zaubernina.ui.theme.ZauberTheme

/**
 * Die Startseite eines Kindes: Wer bin ich, wie viele Sterne habe ich, was möchte ich üben?
 * Große Kacheln mit Bild, eine je Spiel — welche, entscheidet die Klassenstufe ([spieleFuer]).
 * Jede Kachel spricht ihren Namen, wenn man sie antippt: Nina liest noch nicht.
 */
@Composable
fun HubBildschirm(
    kind: BenutzerDaten,
    vorleser: Vorleser?,
    onSpiel: (Spiel) -> Unit,
    onKindWechseln: () -> Unit,
    onElternbereich: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val farben = ZauberTheme.farben
    val m = ZauberMasse.aktuell
    val spiele = spieleFuer(kind.klasse)

    Column(
        modifier = modifier.fillMaxSize().padding(horizontal = m.dp(18), vertical = m.dp(12)),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().height(m.dp(64)),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            // Avatar + Name: antippen = anderes Kind wählen
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clip(CircleShape).clickable(onClick = onKindWechseln).padding(end = m.dp(10)),
            ) {
                Box(
                    modifier = Modifier.size(m.dp(52)).clip(CircleShape).background(farben.akzent.copy(alpha = 0.14f)),
                    contentAlignment = Alignment.Center,
                ) {
                    AvatarBild(avatar = kind.benutzer.avatar, farbe = farben.akzent, modifier = Modifier.size(m.dp(30)))
                }
                ZauberText(kind.benutzer.name, m.sp(22), farben.schrift, FontWeight.SemiBold, Modifier.padding(start = m.dp(10)))
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(m.dp(10))) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clip(CircleShape).background(farben.flaeche).padding(horizontal = m.dp(12), vertical = m.dp(6)),
                ) {
                    EmojiText("⭐", m.sp(16))
                    ZauberText("${kind.sterne}", m.sp(16), farben.schrift, FontWeight.SemiBold, Modifier.padding(start = m.dp(4)))
                }
                Box(
                    modifier = Modifier.size(m.dp(44)).clip(CircleShape).background(farben.flaeche).clickable(onClick = onElternbereich),
                    contentAlignment = Alignment.Center,
                ) {
                    EmojiText("⚙️", m.sp(18))
                }
            }
        }

        ZauberText("Was möchtest du üben?", m.sp(24), farben.schrift, FontWeight.Bold, Modifier.padding(top = m.dp(14), bottom = m.dp(14)))

        Column(
            modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(m.dp(12)),
        ) {
            spiele.chunked(m.spalten).forEach { reihe ->
                Row(horizontalArrangement = Arrangement.spacedBy(m.dp(12)), modifier = Modifier.fillMaxWidth()) {
                    reihe.forEach { spiel ->
                        val name = spiel.nameFuer(kind.klasse)
                        SymbolKachel(
                            symbol = spiel.symbol,
                            text = name,
                            onClick = {
                                vorleser?.sprich(name)
                                onSpiel(spiel)
                            },
                            modifier = Modifier.weight(1f).height(m.dp(150)),
                        )
                    }
                    // Letzte Reihe auffüllen, damit die Kacheln gleich breit bleiben
                    repeat(m.spalten - reihe.size) { Box(modifier = Modifier.weight(1f)) }
                }
            }
        }
    }
}
