package de.korte_daniel.zaubernina.ui.level

import androidx.compose.foundation.Canvas
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.korte_daniel.zaubernina.data.grundschrift.glyph
import de.korte_daniel.zaubernina.domain.LEVEL
import de.korte_daniel.zaubernina.domain.naechstesLevel
import de.korte_daniel.zaubernina.ui.components.zeichneWortLeuchtend
import de.korte_daniel.zaubernina.ui.theme.ZauberText
import de.korte_daniel.zaubernina.ui.theme.ZauberTheme

/**
 * Der große Auftritt am Ende eines Levels: das ganze Wort steht leuchtend da, drei Sterne,
 * und die Ankündigung des nächsten Levels.
 *
 * Der Jubel nach einem einzelnen Buchstaben bleibt bewusst kurz — sonst nutzt sich das hier
 * ab, und genau das soll die Belohnung sein.
 */
@Composable
fun GeschafftBildschirm(
    levelIndex: Int,
    neueSterne: Int,
    onWeiter: () -> Unit,
    onZurReise: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val farben = ZauberTheme.farben
    val level = LEVEL[levelIndex]
    val naechstes = naechstesLevel(levelIndex)
    val zeichen = remember(level) { level.wort.mapNotNull { glyph(it) } }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ZauberText(
            text = "LEVEL ${level.nummer} GESCHAFFT",
            groesse = 19.sp,
            farbe = farben.schriftSchwach,
            gewicht = FontWeight.Medium,
            modifier = Modifier.padding(top = 36.dp),
        )

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(190.dp)
                .padding(top = 26.dp),
        ) {
            zeichneWortLeuchtend(zeichen, farben)
        }

        if (neueSterne > 0) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 24.dp),
            ) {
                repeat(neueSterne) { i ->
                    val gross = i == neueSterne / 2
                    Canvas(modifier = Modifier.size(if (gross) 56.dp else 44.dp)) {
                        val mitte = Offset(size.width / 2f, size.height / 2f)
                        drawCircle(farben.akzent, size.minDimension * 0.55f, mitte, alpha = 0.16f)
                        zeichneSternGefuellt(mitte, size.minDimension * 0.44f, farben.akzent)
                    }
                }
            }
        } else {
            ZauberText(
                text = "Schon geschafft — schön, dass du es nochmal gemacht hast",
                groesse = 15.sp,
                farbe = farben.schriftSchwach,
                modifier = Modifier.padding(top = 26.dp),
            )
        }

        Box(modifier = Modifier.weight(1f))

        if (naechstes != null) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(farben.ecke * 1.3f))
                    .background(farben.flaeche)
                    .border(
                        width = if (farben.flaecheRand == Color.Transparent) 0.dp else 1.5.dp,
                        color = farben.flaecheRand,
                        shape = RoundedCornerShape(farben.ecke * 1.3f),
                    )
                    .padding(horizontal = 18.dp, vertical = 14.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(farben.akzent.copy(alpha = 0.18f)),
                )
                Column(modifier = Modifier.weight(1f)) {
                    ZauberText("JETZT KOMMT", 12.sp, farben.schriftSchwach)
                    ZauberText(
                        text = "Level ${LEVEL[naechstes].nummer} · ${LEVEL[naechstes].wort}",
                        groesse = 20.sp,
                        farbe = farben.schrift,
                        gewicht = FontWeight.SemiBold,
                        modifier = Modifier.padding(top = 2.dp),
                    )
                }
            }
        } else {
            ZauberText(
                text = "Du hast alle Wörter geschafft!",
                groesse = 20.sp,
                farbe = farben.akzent,
                gewicht = FontWeight.SemiBold,
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(66.dp)
                    .clip(RoundedCornerShape(farben.ecke * 1.3f))
                    .border(2.dp, farben.schrift.copy(alpha = 0.28f), RoundedCornerShape(farben.ecke * 1.3f))
                    .clickable(onClick = onZurReise),
                contentAlignment = Alignment.Center,
            ) {
                ZauberText("Zur Reise", 18.sp, farben.schrift)
            }
            if (naechstes != null) {
                Box(
                    modifier = Modifier
                        .weight(2f)
                        .height(66.dp)
                        .clip(RoundedCornerShape(farben.ecke * 1.3f))
                        .background(farben.akzent)
                        .clickable(onClick = onWeiter),
                    contentAlignment = Alignment.Center,
                ) {
                    ZauberText("Weiter", 21.sp, farben.aufAkzent, FontWeight.Bold)
                }
            }
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.zeichneSternGefuellt(
    mitte: Offset,
    radius: Float,
    farbe: Color,
) {
    val punkte = de.korte_daniel.zaubernina.ui.components.sternPunkte(mitte, radius, 0f)
    val pfad = androidx.compose.ui.graphics.Path().apply {
        moveTo(punkte[0].x, punkte[0].y)
        for (i in 1 until punkte.size) lineTo(punkte[i].x, punkte[i].y)
        close()
    }
    drawPath(pfad, farbe)
}
