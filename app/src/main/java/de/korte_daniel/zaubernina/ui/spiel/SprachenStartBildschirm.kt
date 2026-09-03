package de.korte_daniel.zaubernina.ui.spiel

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import de.korte_daniel.zaubernina.data.WORTTHEMEN
import de.korte_daniel.zaubernina.data.Wortthema
import de.korte_daniel.zaubernina.domain.Fremdsprache
import de.korte_daniel.zaubernina.ui.Vorleser
import de.korte_daniel.zaubernina.ui.components.Kopfzeile
import de.korte_daniel.zaubernina.ui.components.SymbolKachel
import de.korte_daniel.zaubernina.ui.theme.ZauberMasse
import de.korte_daniel.zaubernina.ui.theme.ZauberText
import de.korte_daniel.zaubernina.ui.theme.ZauberTheme
import java.util.Locale

/**
 * Sprache wählen (zwei Fahnen), dann ein Wortfeld — und los. Die Sprache wird gemerkt,
 * damit Lea beim nächsten Mal direkt bei ihren Wortfeldern landet.
 */
@Composable
fun SprachenStartBildschirm(
    sprache: Fremdsprache,
    vorleser: Vorleser?,
    onSprache: (Fremdsprache) -> Unit,
    onStart: (Fremdsprache, Wortthema) -> Unit,
    onZurueck: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val farben = ZauberTheme.farben
    val m = ZauberMasse.aktuell
    var gewaehlt by remember { mutableStateOf(sprache) }

    Column(
        modifier = modifier.fillMaxSize().padding(horizontal = m.dp(18), vertical = m.dp(12)),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Kopfzeile(titel = "Sprachen", onZurueck = onZurueck)
        Column(
            modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            ZauberText("Welche Sprache?", m.sp(22), farben.schrift, FontWeight.Bold, Modifier.padding(top = m.dp(8), bottom = m.dp(12)))
            Row(horizontalArrangement = Arrangement.spacedBy(m.dp(12)), modifier = Modifier.fillMaxWidth()) {
                Fremdsprache.entries.forEach { s ->
                    SymbolKachel(
                        symbol = s.symbol,
                        text = s.anzeigename,
                        gewaehlt = gewaehlt == s,
                        onClick = {
                            gewaehlt = s
                            onSprache(s)
                            // Die Sprache begrüßt in ihrer eigenen Stimme — der erste Klang der neuen Sprache.
                            vorleser?.sprich(if (s == Fremdsprache.ENGLISCH) "Hello!" else "¡Hola!", Locale.forLanguageTag(s.sprachkennung))
                        },
                        modifier = Modifier.weight(1f).height(m.dp(120)),
                    )
                }
            }
            ZauberText("Worüber?", m.sp(22), farben.schrift, FontWeight.Bold, Modifier.padding(top = m.dp(24), bottom = m.dp(12)))
            Column(verticalArrangement = Arrangement.spacedBy(m.dp(12)), modifier = Modifier.fillMaxWidth()) {
                WORTTHEMEN.chunked(m.spalten).forEach { reihe ->
                    Row(horizontalArrangement = Arrangement.spacedBy(m.dp(12)), modifier = Modifier.fillMaxWidth()) {
                        reihe.forEach { thema ->
                            SymbolKachel(
                                symbol = thema.symbol,
                                text = thema.name,
                                onClick = {
                                    vorleser?.sprich(thema.name)
                                    onStart(gewaehlt, thema)
                                },
                                modifier = Modifier.weight(1f).height(m.dp(124)),
                            )
                        }
                        repeat(m.spalten - reihe.size) { Box(modifier = Modifier.weight(1f)) }
                    }
                }
            }
            Box(modifier = Modifier.padding(bottom = m.dp(16)))
        }
    }
}
