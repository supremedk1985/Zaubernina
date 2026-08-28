package de.korte_daniel.zaubernina.ui.parent

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.korte_daniel.zaubernina.domain.LEVEL
import de.korte_daniel.zaubernina.logic.Genauigkeit
import de.korte_daniel.zaubernina.ui.theme.Thema
import de.korte_daniel.zaubernina.ui.theme.ZauberText
import de.korte_daniel.zaubernina.ui.theme.ZauberTheme

/**
 * Der Elternbereich. Liegt hinter dem [Elternschloss] und sieht absichtlich nüchtern aus —
 * kein Funkeln, keine großen Knöpfe. Was hier steht, soll ein Kind nicht anziehen.
 */
@Composable
fun EinstellungenBildschirm(
    thema: Thema,
    genauigkeit: Genauigkeit,
    geschafft: Int,
    sterne: Int,
    onThemaWechsel: (Thema) -> Unit,
    onGenauigkeitWechsel: (Genauigkeit) -> Unit,
    onFortschrittZuruecksetzen: () -> Unit,
    onSchliessen: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val farben = ZauberTheme.farben
    var loeschenBestaetigen by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp, vertical = 14.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().height(52.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            ZauberText("Elternbereich", 22.sp, farben.schrift, FontWeight.SemiBold)
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(farben.flaeche)
                    .clickable(onClick = onSchliessen),
                contentAlignment = Alignment.Center,
            ) {
                ZauberText("✕", 19.sp, farben.schrift)
            }
        }

        Abschnitt("AUSSEHEN", farben.schriftSchwach)
        ZauberText(
            text = "Wie die App aussieht, während dein Kind schreibt.",
            groesse = 14.sp,
            farbe = farben.schriftSchwach,
            modifier = Modifier.padding(bottom = 10.dp),
        )
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Thema.entries.forEach { t ->
                Wahlzeile(
                    titel = t.anzeigename,
                    beschreibung = t.beschreibung,
                    gewaehlt = t == thema,
                    onClick = { onThemaWechsel(t) },
                )
            }
        }

        Abschnitt("GENAUIGKEIT", farben.schriftSchwach)
        ZauberText(
            text = "Wie genau die Linie getroffen werden muss. Zu genau frustriert, zu leicht lernt nichts.",
            groesse = 14.sp,
            farbe = farben.schriftSchwach,
            modifier = Modifier.padding(bottom = 10.dp),
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            listOf(
                Genauigkeit.LEICHT to "Leicht",
                Genauigkeit.NORMAL to "Normal",
                Genauigkeit.GENAU to "Genau",
            ).forEach { (wert, name) ->
                val aktiv = wert == genauigkeit
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .clip(RoundedCornerShape(farben.ecke))
                        .background(if (aktiv) farben.akzent else farben.flaeche)
                        .border(
                            width = if (farben.flaecheRand == Color.Transparent) 0.dp else 1.5.dp,
                            color = if (aktiv) Color.Transparent else farben.flaecheRand,
                            shape = RoundedCornerShape(farben.ecke),
                        )
                        .clickable { onGenauigkeitWechsel(wert) },
                    contentAlignment = Alignment.Center,
                ) {
                    ZauberText(name, 15.sp, if (aktiv) farben.aufAkzent else farben.schriftSchwach)
                }
            }
        }

        Abschnitt("FORTSCHRITT", farben.schriftSchwach)
        ZauberText(
            text = "$geschafft von ${LEVEL.size} Leveln geschafft · $sterne Sterne",
            groesse = 15.sp,
            farbe = farben.schrift,
            modifier = Modifier.padding(bottom = 10.dp),
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .clip(RoundedCornerShape(farben.ecke))
                .border(1.5.dp, Color(0xFFB3382F).copy(alpha = 0.6f), RoundedCornerShape(farben.ecke))
                .clickable {
                    if (loeschenBestaetigen) {
                        onFortschrittZuruecksetzen()
                        loeschenBestaetigen = false
                    } else {
                        loeschenBestaetigen = true
                    }
                },
            contentAlignment = Alignment.Center,
        ) {
            ZauberText(
                text = if (loeschenBestaetigen) "Wirklich? Nochmal tippen." else "Fortschritt zurücksetzen",
                groesse = 15.sp,
                farbe = Color(0xFFE06A6A),
            )
        }

        Abschnitt("DATEN", farben.schriftSchwach)
        ZauberText(
            text = "Diese App hat keinen Internetzugang — die Berechtigung fehlt im Programm. " +
                "Sie kann nichts senden. Es werden keine Daten erhoben, gespeichert oder " +
                "übertragen. Keine Werbung, keine Käufe.",
            groesse = 13.sp,
            farbe = farben.schriftSchwach,
            modifier = Modifier.padding(bottom = 16.dp),
        )
    }
}

@Composable
private fun Abschnitt(titel: String, farbe: Color) {
    ZauberText(
        text = titel,
        groesse = 13.sp,
        farbe = farbe,
        gewicht = FontWeight.Medium,
        modifier = Modifier.padding(top = 26.dp, bottom = 6.dp),
    )
}

@Composable
private fun Wahlzeile(titel: String, beschreibung: String, gewaehlt: Boolean, onClick: () -> Unit) {
    val farben = ZauberTheme.farben
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(farben.ecke))
            .background(farben.flaeche)
            .border(
                width = 2.dp,
                color = if (gewaehlt) farben.akzent else Color.Transparent,
                shape = RoundedCornerShape(farben.ecke),
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 13.dp),
    ) {
        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(CircleShape)
                .background(if (gewaehlt) farben.akzent else Color.Transparent)
                .border(2.dp, if (gewaehlt) farben.akzent else farben.schriftSchwach, CircleShape),
        )
        Column(modifier = Modifier.weight(1f)) {
            ZauberText(titel, 17.sp, farben.schrift, FontWeight.Medium)
            ZauberText(beschreibung, 13.sp, farben.schriftSchwach, modifier = Modifier.padding(top = 2.dp))
        }
    }
}
