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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import de.korte_daniel.zaubernina.logic.LOESUNG_STELLEN
import de.korte_daniel.zaubernina.logic.erzeugeElternfrage
import de.korte_daniel.zaubernina.ui.theme.ZauberText
import de.korte_daniel.zaubernina.ui.theme.ZauberTheme

/**
 * Die Hürde vor dem Elternbereich: eine Rechenaufgabe aus dem kleinen Einmaleins.
 *
 * Eigene Zifferntasten statt der Systemtastatur — die wäre auf einem Kindergerät der
 * einzige Ort in der App, an dem plötzlich fremde Bedienelemente auftauchen, und sie
 * verdeckt die Aufgabe. Geprüft wird von selbst, sobald zwei Ziffern dastehen; falsch
 * heißt: neue Aufgabe, kein Vorwurf, kein Zähler.
 */
@Composable
fun Elternschloss(
    onGeoeffnet: () -> Unit,
    onAbbrechen: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val farben = ZauberTheme.farben
    var frage by remember { mutableStateOf(erzeugeElternfrage()) }
    var eingabe by remember { mutableStateOf("") }
    var danebenGewesen by remember { mutableStateOf(false) }

    fun tippe(ziffer: String) {
        if (eingabe.length >= LOESUNG_STELLEN) return
        val neu = eingabe + ziffer
        eingabe = neu
        if (neu.length == LOESUNG_STELLEN) {
            if (frage.stimmt(neu)) {
                onGeoeffnet()
            } else {
                danebenGewesen = true
                frage = erzeugeElternfrage()
                eingabe = ""
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().height(50.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(farben.flaeche)
                    .clickable(onClick = onAbbrechen),
                contentAlignment = Alignment.Center,
            ) {
                ZauberText("✕", 20.sp, farben.schrift)
            }
        }

        Box(modifier = Modifier.weight(0.6f))

        ZauberText("Nur für Erwachsene", 24.sp, farben.schrift, FontWeight.SemiBold)
        ZauberText(
            text = if (danebenGewesen) "Das war nicht richtig — hier ist eine neue Aufgabe" else "Wie viel ist das?",
            groesse = 15.sp,
            farbe = farben.schriftSchwach,
            modifier = Modifier.padding(top = 8.dp),
        )

        ZauberText(
            text = frage.text,
            groesse = 54.sp,
            farbe = farben.schrift,
            gewicht = FontWeight.Bold,
            modifier = Modifier.padding(top = 26.dp),
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(top = 22.dp),
        ) {
            repeat(LOESUNG_STELLEN) { i ->
                val gefuellt = i < eingabe.length
                Box(
                    modifier = Modifier
                        .size(width = 56.dp, height = 68.dp)
                        .clip(RoundedCornerShape(farben.ecke))
                        .background(farben.flaeche)
                        .border(
                            width = 2.dp,
                            color = if (gefuellt) farben.akzent else farben.schrift.copy(alpha = 0.18f),
                            shape = RoundedCornerShape(farben.ecke),
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    ZauberText(
                        text = eingabe.getOrNull(i)?.toString() ?: "",
                        groesse = 32.sp,
                        farbe = farben.schrift,
                        gewicht = FontWeight.Bold,
                    )
                }
            }
        }

        Box(modifier = Modifier.weight(1f))

        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
        ) {
            listOf(listOf("1", "2", "3"), listOf("4", "5", "6"), listOf("7", "8", "9")).forEach { reihe ->
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    reihe.forEach { z -> Zifferntaste(z, Modifier.weight(1f)) { tippe(z) } }
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                Box(modifier = Modifier.weight(1f))
                Zifferntaste("0", Modifier.weight(1f)) { tippe("0") }
                Zifferntaste("←", Modifier.weight(1f)) { eingabe = eingabe.dropLast(1) }
            }
        }
    }
}

@Composable
private fun Zifferntaste(beschriftung: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val farben = ZauberTheme.farben
    Box(
        modifier = modifier
            .height(64.dp)
            .clip(RoundedCornerShape(farben.ecke))
            .background(farben.flaeche)
            .border(
                width = if (farben.flaecheRand == Color.Transparent) 0.dp else 1.5.dp,
                color = farben.flaecheRand,
                shape = RoundedCornerShape(farben.ecke),
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        ZauberText(beschriftung, 26.sp, farben.schrift, FontWeight.Medium)
    }
}
