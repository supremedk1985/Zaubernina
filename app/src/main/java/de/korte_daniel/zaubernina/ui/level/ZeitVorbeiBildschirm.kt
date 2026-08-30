package de.korte_daniel.zaubernina.ui.level

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.korte_daniel.zaubernina.domain.Avatar
import de.korte_daniel.zaubernina.ui.Vorleser
import de.korte_daniel.zaubernina.ui.components.AvatarBild
import de.korte_daniel.zaubernina.ui.theme.ZauberText
import de.korte_daniel.zaubernina.ui.theme.ZauberTheme

/**
 * Für heute ist Schluss: das Zeitlimit ist erreicht. Bewusst freundlich und ohne
 * jeden Vorwurf — das Kind hat ja geübt. Von hier geht es nur zurück zur Kinderwahl;
 * der kleine Eltern-Zugang unten führt hinters Rechenschloss, damit das Limit auch
 * jetzt noch geändert werden kann.
 */
@Composable
fun ZeitVorbeiBildschirm(
    name: String,
    avatar: Avatar,
    onZurKinderwahl: () -> Unit,
    onElternbereich: () -> Unit,
    vorleser: Vorleser? = null,
    modifier: Modifier = Modifier,
) {
    val farben = ZauberTheme.farben

    LaunchedEffect(Unit) { vorleser?.sprich("Für heute ist Schluss. Bis morgen, $name!") }

    Column(
        modifier = modifier.fillMaxSize().padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(140.dp))
        Box(
            modifier = Modifier
                .size(130.dp)
                .clip(CircleShape)
                .background(farben.akzent.copy(alpha = 0.14f)),
            contentAlignment = Alignment.Center,
        ) {
            AvatarBild(avatar = avatar, farbe = farben.akzent, modifier = Modifier.size(78.dp))
        }
        Spacer(modifier = Modifier.height(26.dp))
        ZauberText("Für heute ist Schluss", 30.sp, farben.schrift, FontWeight.Bold)
        Spacer(modifier = Modifier.height(10.dp))
        ZauberText(
            text = "Du hast heute fleißig geübt, $name.\nBis morgen!",
            groesse = 17.sp,
            farbe = farben.schriftSchwach,
        )
        Spacer(modifier = Modifier.height(36.dp))
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(farben.ecke))
                .background(farben.akzent)
                .clickable(onClick = onZurKinderwahl)
                .padding(horizontal = 34.dp, vertical = 14.dp),
        ) {
            ZauberText("Gute Nacht!", 19.sp, farben.aufAkzent, FontWeight.SemiBold)
        }
        Spacer(modifier = Modifier.weight(1f))
        // Unauffällig, wie der Elternbereich selbst: kein Knopf, nur eine Zeile.
        ZauberText(
            text = "Elternbereich",
            groesse = 14.sp,
            farbe = farben.schriftSchwach,
            modifier = Modifier
                .clickable(onClick = onElternbereich)
                .padding(16.dp),
        )
    }
}
