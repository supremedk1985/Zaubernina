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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.korte_daniel.zaubernina.data.grundschrift.glyph
import de.korte_daniel.zaubernina.ui.components.sternPunkte
import de.korte_daniel.zaubernina.ui.components.zeichneWortLeuchtend
import de.korte_daniel.zaubernina.ui.theme.ZauberText
import de.korte_daniel.zaubernina.ui.theme.ZauberTheme

/**
 * Der Abschluss einer ganzen ABC-Runde — 29 Buchstaben, das ist die längste Strecke der
 * App und verdient den größten Auftritt: das leuchtende ABC und fünf Sterne (beim ersten
 * Mal; danach ist das Können die Belohnung, dieselbe Regel wie überall).
 */
@Composable
fun AlphabetFertigBildschirm(
    neueSterne: Int,
    onZurReise: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val farben = ZauberTheme.farben
    val zeichen = remember { "ABC".mapNotNull { glyph(it) } }

    Column(
        modifier = modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ZauberText(
            text = "DAS GANZE ALPHABET!",
            groesse = 19.sp,
            farbe = farben.schriftSchwach,
            gewicht = FontWeight.Medium,
            modifier = Modifier.padding(top = 40.dp),
        )
        ZauberText(
            text = "Alle ${de.korte_daniel.zaubernina.domain.ALPHABET.size} Buchstaben geschrieben",
            groesse = 15.sp,
            farbe = farben.schriftSchwach,
            modifier = Modifier.padding(top = 6.dp),
        )

        Canvas(
            modifier = Modifier.fillMaxWidth().height(170.dp).padding(top = 24.dp),
        ) {
            zeichneWortLeuchtend(zeichen, farben)
        }

        if (neueSterne > 0) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 26.dp),
            ) {
                repeat(neueSterne) { i ->
                    val gross = i == neueSterne / 2
                    Canvas(modifier = Modifier.size(if (gross) 52.dp else 40.dp)) {
                        val mitte = Offset(size.width / 2f, size.height / 2f)
                        drawCircle(farben.akzent, size.minDimension * 0.55f, mitte, alpha = 0.16f)
                        val punkte = sternPunkte(mitte, size.minDimension * 0.44f, 0f)
                        val pfad = Path().apply {
                            moveTo(punkte[0].x, punkte[0].y)
                            for (j in 1 until punkte.size) lineTo(punkte[j].x, punkte[j].y)
                            close()
                        }
                        drawPath(pfad, farben.akzent)
                    }
                }
            }
        } else {
            ZauberText(
                text = "Schon wieder geschafft — stark!",
                groesse = 16.sp,
                farbe = farben.schriftSchwach,
                modifier = Modifier.padding(top = 26.dp),
            )
        }

        Box(modifier = Modifier.weight(1f))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp)
                .height(66.dp)
                .clip(RoundedCornerShape(farben.ecke * 1.3f))
                .background(farben.akzent)
                .clickable(onClick = onZurReise),
            contentAlignment = Alignment.Center,
        ) {
            ZauberText("Zur Reise", 21.sp, farben.aufAkzent, FontWeight.Bold)
        }
    }
}
