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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.korte_daniel.zaubernina.data.BenutzerDaten
import de.korte_daniel.zaubernina.ui.components.AvatarBild
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

    Column(
        modifier = modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ZauberText(
            text = "Zaubernina",
            groesse = 34.sp,
            farbe = farben.schrift,
            gewicht = FontWeight.Bold,
            modifier = Modifier.padding(top = 48.dp),
        )
        ZauberText(
            text = "Wer übt heute?",
            groesse = 19.sp,
            farbe = farben.schriftSchwach,
            modifier = Modifier.padding(top = 10.dp),
        )

        Box(modifier = Modifier.weight(0.35f))

        Column(
            verticalArrangement = Arrangement.spacedBy(22.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            benutzer.chunked(2).forEach { reihe ->
                Row(horizontalArrangement = Arrangement.spacedBy(28.dp)) {
                    reihe.forEach { daten ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clip(CircleShape)
                                .clickable { onWaehlen(daten.benutzer.id) }
                                .padding(10.dp),
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(110.dp)
                                    .clip(CircleShape)
                                    .background(farben.akzent.copy(alpha = 0.14f)),
                                contentAlignment = Alignment.Center,
                            ) {
                                AvatarBild(
                                    avatar = daten.benutzer.avatar,
                                    farbe = farben.akzent,
                                    modifier = Modifier.size(66.dp),
                                )
                            }
                            ZauberText(
                                text = daten.benutzer.name,
                                groesse = 21.sp,
                                farbe = farben.schrift,
                                gewicht = FontWeight.SemiBold,
                                modifier = Modifier.padding(top = 8.dp),
                            )
                        }
                    }
                }
            }
        }

        Box(modifier = Modifier.weight(0.65f))
    }
}
