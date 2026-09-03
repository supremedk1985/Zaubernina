package de.korte_daniel.zaubernina.ui.start

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import de.korte_daniel.zaubernina.data.BenutzerDaten
import de.korte_daniel.zaubernina.ui.components.AvatarBild
import de.korte_daniel.zaubernina.ui.theme.ZauberMasse
import de.korte_daniel.zaubernina.ui.theme.ZauberText
import de.korte_daniel.zaubernina.ui.theme.ZauberTheme

/**
 * Der erste Bildschirm: Wer übt heute? Daniels Vorgabe — beim Start MUSS gewählt werden,
 * auch wenn es nur einen Benutzer gibt. Das hält den Ablauf vorhersagbar, und ein Kind
 * tippt gern auf sein eigenes Bild.
 */
@Composable
fun StartBildschirm(
    benutzer: List<BenutzerDaten>,
    onWaehlen: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val farben = ZauberTheme.farben
    val m = ZauberMasse.aktuell

    Column(
        modifier = modifier.fillMaxSize().padding(m.dp(24)),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ZauberText(
            text = "Zaubernina",
            groesse = m.sp(34),
            farbe = farben.schrift,
            gewicht = FontWeight.Bold,
            modifier = Modifier.padding(top = m.dp(48)),
        )
        ZauberText(
            text = "Wer übt heute?",
            groesse = m.sp(19),
            farbe = farben.schriftSchwach,
            modifier = Modifier.padding(top = m.dp(10)),
        )

        Box(modifier = Modifier.weight(0.35f))

        Column(
            verticalArrangement = Arrangement.spacedBy(m.dp(22)),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            benutzer.chunked(if (m.weit) 3 else 2).forEach { reihe ->
                Row(horizontalArrangement = Arrangement.spacedBy(m.dp(28))) {
                    reihe.forEach { daten ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clip(CircleShape)
                                .clickable { onWaehlen(daten.benutzer.id) }
                                .padding(m.dp(10)),
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(m.dp(110))
                                    .clip(CircleShape)
                                    .background(farben.akzent.copy(alpha = 0.14f)),
                                contentAlignment = Alignment.Center,
                            ) {
                                AvatarBild(
                                    avatar = daten.benutzer.avatar,
                                    farbe = farben.akzent,
                                    modifier = Modifier.size(m.dp(66)),
                                )
                            }
                            ZauberText(
                                text = daten.benutzer.name,
                                groesse = m.sp(21),
                                farbe = farben.schrift,
                                gewicht = FontWeight.SemiBold,
                                modifier = Modifier.padding(top = m.dp(8)),
                            )
                        }
                    }
                }
            }
        }

        Box(modifier = Modifier.weight(0.65f))
    }
}
