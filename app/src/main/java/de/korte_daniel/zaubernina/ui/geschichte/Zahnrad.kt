package de.korte_daniel.zaubernina.ui.geschichte

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke as StrichStil
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import de.korte_daniel.zaubernina.ui.components.EmojiText
import de.korte_daniel.zaubernina.ui.theme.ZauberMasse
import de.korte_daniel.zaubernina.ui.theme.ZauberText
import de.korte_daniel.zaubernina.ui.theme.ZauberTheme
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/** Ein Eintrag auf einem Zahnrad: Bild und Wort. */
data class Zahn(val symbol: String, val text: String, val unterzeile: String? = null)

/**
 * Ein drehbares Zahnrad — Daniels Bild für die Geschichtenauswahl: „Zahnräder mit Tieren,
 * Elementen und noch was, und je nachdem, wie ich sie drehe, kommt eine Geschichte raus."
 *
 * Antippen dreht einen Zahn weiter, Ziehen nach oben/unten ebenso. Das Rad dreht sich dabei
 * sichtbar (animiert), in der Mitte steht der gewählte Eintrag. Es gibt keinen falschen Zug:
 * Alles, was auf dem Rad steht, ergibt eine Geschichte.
 */
@Composable
fun Zahnrad(
    zaehne: List<Zahn>,
    index: Int,
    onIndex: (Int) -> Unit,
    durchmesser: Dp,
    modifier: Modifier = Modifier,
) {
    val farben = ZauberTheme.farben
    val m = ZauberMasse.aktuell
    val anzahlZaehne = 12
    // Drehwinkel wächst mit jedem Schritt — auch rückwärts — und wird animiert.
    var schritte by remember { mutableFloatStateOf(0f) }
    val winkel by animateFloatAsState(targetValue = schritte * (360f / anzahlZaehne), animationSpec = tween(320), label = "zahnrad")
    val gewaehlt = zaehne.getOrNull(index.coerceIn(0, (zaehne.size - 1).coerceAtLeast(0)))

    fun weiter(richtung: Int) {
        if (zaehne.isEmpty()) return
        schritte += richtung
        onIndex(((index + richtung) % zaehne.size + zaehne.size) % zaehne.size)
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier) {
        Box(
            modifier = Modifier
                .size(durchmesser)
                .pointerInput(zaehne.size) {
                    var summe = 0f
                    detectVerticalDragGestures(
                        onDragStart = { summe = 0f },
                        onVerticalDrag = { _, delta ->
                            summe += delta
                            if (summe > 48f) { weiter(1); summe = 0f }
                            if (summe < -48f) { weiter(-1); summe = 0f }
                        },
                    )
                }
                .pointerInput(zaehne.size) {
                    detectTapGestures(onTap = { weiter(1) })
                },
            contentAlignment = Alignment.Center,
        ) {
            Canvas(modifier = Modifier.size(durchmesser)) {
                val mitte = Offset(size.width / 2f, size.height / 2f)
                val aussen = size.minDimension * 0.48f
                val innen = size.minDimension * 0.40f
                rotate(winkel, mitte) {
                    val pfad = Path()
                    val schrittWinkel = 2.0 * PI / anzahlZaehne
                    for (i in 0 until anzahlZaehne) {
                        val a0 = i * schrittWinkel
                        val a1 = a0 + schrittWinkel * 0.35
                        val a2 = a0 + schrittWinkel * 0.5
                        val a3 = a0 + schrittWinkel * 0.85
                        fun p(r: Float, a: Double) = Offset(mitte.x + r * cos(a).toFloat(), mitte.y + r * sin(a).toFloat())
                        if (i == 0) pfad.moveTo(p(aussen, a0).x, p(aussen, a0).y)
                        pfad.lineTo(p(aussen, a1).x, p(aussen, a1).y)
                        pfad.lineTo(p(innen, a2).x, p(innen, a2).y)
                        pfad.lineTo(p(innen, a3).x, p(innen, a3).y)
                        pfad.lineTo(p(aussen, a0 + schrittWinkel).x, p(aussen, a0 + schrittWinkel).y)
                    }
                    pfad.close()
                    drawPath(pfad, farben.akzent.copy(alpha = 0.22f))
                    drawPath(pfad, farben.akzent, style = StrichStil(width = 3f * density))
                }
                drawCircle(farben.flaeche, radius = innen * 0.86f, center = mitte)
                drawCircle(farben.akzent.copy(alpha = 0.5f), radius = innen * 0.86f, center = mitte, style = StrichStil(width = 2f * density))
            }
            if (gewaehlt != null) {
                EmojiText(gewaehlt.symbol, m.sp(44))
            }
        }
        if (gewaehlt != null) {
            ZauberText(gewaehlt.text, m.sp(18), farben.schrift, FontWeight.SemiBold, Modifier.padding(top = m.dp(6)))
            if (gewaehlt.unterzeile != null) {
                ZauberText(gewaehlt.unterzeile, m.sp(13), farben.schriftSchwach)
            }
        }
    }
}
