package de.korte_daniel.zaubernina.ui.level

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke as StrichStil
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.korte_daniel.zaubernina.ui.components.sternPunkte
import de.korte_daniel.zaubernina.ui.theme.ZauberText
import de.korte_daniel.zaubernina.ui.theme.ZauberTheme

/**
 * Der Zählmoment nach einer geschriebenen Zahl: so viele Sterne antippen, wie die Ziffer
 * sagt. Schreiben und Mengenverständnis in einem Zug — die Zahl ist nicht nur eine Form,
 * sondern eine Anzahl.
 *
 * Die Sterne müssen der Reihe nach irgendwo angetippt werden (welcher zuerst, ist egal);
 * unter jedem angetippten steht seine Zahl. Sind alle da, erscheint der Weiter-Knopf —
 * mit dem Zusatzstern, wenn es das erste Mal war.
 */
@Composable
fun ZaehlBildschirm(
    ziffer: Int,
    bonusStern: Boolean,
    onFertig: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val farben = ZauberTheme.farben
    var angetippt by remember { mutableIntStateOf(0) }
    val fertig = angetippt >= ziffer

    Column(
        modifier = modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ZauberText(
            text = "DU HAST DIE $ziffer GESCHRIEBEN",
            groesse = 17.sp,
            farbe = farben.schriftSchwach,
            gewicht = FontWeight.Medium,
            modifier = Modifier.padding(top = 34.dp),
        )
        ZauberText(
            text = "$ziffer",
            groesse = 120.sp,
            farbe = farben.akzent,
            gewicht = FontWeight.Bold,
            modifier = Modifier.padding(top = 6.dp),
        )
        ZauberText(
            text = if (fertig) "Genau $ziffer!" else "Zähl mit — tipp die Sterne an!",
            groesse = 21.sp,
            farbe = farben.schrift,
            gewicht = FontWeight.SemiBold,
            modifier = Modifier.padding(top = 10.dp),
        )

        // Die Sterne, in Reihen zu höchstens vier.
        Column(
            verticalArrangement = Arrangement.spacedBy(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 26.dp),
        ) {
            (0 until ziffer).chunked(4).forEach { reihe ->
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    reihe.forEach { i ->
                        val gold = i < angetippt
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Canvas(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(RoundedCornerShape(20.dp))
                                    .clickable(enabled = !gold && !fertig) { angetippt++ },
                            ) {
                                val mitte = Offset(size.width / 2f, size.height / 2f)
                                val punkte = sternPunkte(mitte, size.minDimension * 0.42f, 0f)
                                val pfad = Path().apply {
                                    moveTo(punkte[0].x, punkte[0].y)
                                    for (j in 1 until punkte.size) lineTo(punkte[j].x, punkte[j].y)
                                    close()
                                }
                                if (gold) {
                                    drawCircle(farben.akzent, size.minDimension * 0.5f, mitte, alpha = 0.18f)
                                    drawPath(pfad, farben.akzent)
                                } else {
                                    drawPath(
                                        pfad,
                                        farben.schrift.copy(alpha = 0.4f),
                                        style = StrichStil(width = 2.2f * density),
                                    )
                                }
                            }
                            ZauberText(
                                text = if (gold) "${i + 1}" else "",
                                groesse = 20.sp,
                                farbe = farben.akzent,
                                gewicht = FontWeight.Bold,
                            )
                        }
                    }
                }
            }
        }

        Box(modifier = Modifier.weight(1f))

        if (fertig) {
            if (bonusStern) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(9.dp),
                    modifier = Modifier
                        .clip(RoundedCornerShape(farben.ecke))
                        .background(farben.akzent.copy(alpha = 0.14f))
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                ) {
                    Canvas(modifier = Modifier.size(20.dp)) {
                        val mitte = Offset(size.width / 2f, size.height / 2f)
                        val punkte = sternPunkte(mitte, size.minDimension / 2f, 0f)
                        val pfad = Path().apply {
                            moveTo(punkte[0].x, punkte[0].y)
                            for (j in 1 until punkte.size) lineTo(punkte[j].x, punkte[j].y)
                            close()
                        }
                        drawPath(pfad, farben.akzent)
                    }
                    ZauberText("Ein Zusatzstern für dich!", 17.sp, farben.akzent, FontWeight.SemiBold)
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 14.dp, bottom = 10.dp)
                    .height(64.dp)
                    .clip(RoundedCornerShape(farben.ecke * 1.3f))
                    .background(farben.akzent)
                    .clickable(onClick = onFertig),
                contentAlignment = Alignment.Center,
            ) {
                ZauberText("Weiter", 20.sp, farben.aufAkzent, FontWeight.Bold)
            }
        }
    }
}
