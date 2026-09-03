package de.korte_daniel.zaubernina.ui.rechnen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke as StrichStil
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import de.korte_daniel.zaubernina.logic.Rechenschritt
import de.korte_daniel.zaubernina.ui.theme.ZauberMasse
import de.korte_daniel.zaubernina.ui.theme.ZauberTheme

/**
 * Der Zahlenstrahl — Leas Hilfe beim Rechnen. Immer sichtbar mit der Startzahl markiert;
 * nach einem Fehler erscheinen die Sprünge (über die Zehn in zwei Bögen, wie im Unterricht).
 * Er verrät die Lösung nicht von selbst: der Zielpunkt wird erst mit den Sprüngen gezeigt.
 */
@Composable
fun Zahlenstrahl(
    bis: Int,
    schritt: Rechenschritt,
    zeigeSpruenge: Boolean,
    modifier: Modifier = Modifier,
    hoehe: Dp = ZauberMasse.aktuell.dp(96),
) {
    val farben = ZauberTheme.farben
    val m = ZauberMasse.aktuell
    val textGroesse = with(androidx.compose.ui.platform.LocalDensity.current) { m.sp(13).toPx() }
    Canvas(modifier = modifier.fillMaxWidth().height(hoehe)) {
        val rand = 18f * density
        val y = size.height * 0.62f
        val breite = size.width - 2 * rand
        fun x(n: Int) = rand + breite * n / bis
        drawLine(farben.schrift.copy(alpha = 0.5f), Offset(rand, y), Offset(rand + breite, y), strokeWidth = 2.5f * density)
        val schrift = android.graphics.Paint().apply {
            color = farben.schriftSchwach.toArgb()
            textSize = textGroesse
            textAlign = android.graphics.Paint.Align.CENTER
            isAntiAlias = true
        }
        for (n in 0..bis) {
            val gross = n % 5 == 0
            val h = (if (gross) 9f else 5f) * density
            drawLine(farben.schrift.copy(alpha = if (gross) 0.7f else 0.35f), Offset(x(n), y - h), Offset(x(n), y + h), strokeWidth = 2f * density)
            if (gross || bis <= 10) {
                drawContext.canvas.nativeCanvas.drawText("$n", x(n), y + h + textGroesse * 1.1f, schrift)
            }
        }
        // Startzahl
        drawCircle(farben.akzent, radius = 7f * density, center = Offset(x(schritt.start), y))
        if (zeigeSpruenge) {
            schritt.spruenge().forEach { (von, nach) ->
                val links = minOf(x(von), x(nach)); val rechts = maxOf(x(von), x(nach))
                val hoch = minOf(size.height * 0.5f, (rechts - links) * 0.6f).coerceAtLeast(12f * density)
                val pfad = Path().apply { arcTo(Rect(links, y - hoch, rechts, y + hoch), 180f, 180f, forceMoveTo = true) }
                drawPath(pfad, farben.akzent, style = StrichStil(width = 3f * density))
                drawCircle(farben.akzent, radius = 6f * density, center = Offset(x(nach), y))
                // Pfeilspitze am Ziel
                val richtung = if (nach > von) -1f else 1f
                val spitze = Path().apply {
                    moveTo(x(nach), y - 2f * density)
                    lineTo(x(nach) + richtung * 9f * density, y - 12f * density)
                    lineTo(x(nach) - richtung * 5f * density, y - 12f * density)
                    close()
                }
                drawPath(spitze, farben.akzent)
            }
        }
    }
}

