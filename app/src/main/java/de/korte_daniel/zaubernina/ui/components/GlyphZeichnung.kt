package de.korte_daniel.zaubernina.ui.components

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke as StrichStil
import de.korte_daniel.zaubernina.domain.GlyphPoint
import de.korte_daniel.zaubernina.domain.Stroke
import de.korte_daniel.zaubernina.domain.StrokeSegment
import de.korte_daniel.zaubernina.ui.theme.ZauberFarben
import kotlin.math.atan2

/** Übersetzt zwischen der 1000er-Zeichenbox und dem Bildschirm. */
class Boxabbildung(val skala: Float, val versatzX: Float = 0f, val versatzY: Float = 0f) {
    fun zumSchirm(p: GlyphPoint) = Offset(versatzX + p.x * skala, versatzY + p.y * skala)
    fun zurBox(o: Offset) = GlyphPoint((o.x - versatzX) / skala, (o.y - versatzY) / skala)
    fun laenge(inBox: Float) = inBox * skala
}

/** Der senkrechte Bereich, der immer sichtbar bleibt: Oberlinie bis Unterlinie, mit Luft. */
private const val BAND_OBEN = 90f
private const val BAND_UNTEN = 1040f

/**
 * Legt das Zeichen so groß wie möglich in die Fläche.
 *
 * Zwei verschiedene Bezugsgrößen, und das mit Absicht:
 *   - SENKRECHT das feste Schriftband (Oberlinie bis Unterlinie), nicht die Höhe des
 *     einzelnen Zeichens. Sonst wäre ein kleines "a" so groß wie ein großes "N", und das
 *     Kind bekäme ein falsches Gefühl für die Größenverhältnisse.
 *   - WAAGERECHT die tatsächliche Breite des Zeichens, damit ein schmales "i" den
 *     Bildschirm genauso ausfüllt wie ein breites "m".
 */
fun boxabbildungFuer(
    breiteVonBis: ClosedFloatingPointRange<Float>,
    flaecheBreite: Float,
    flaecheHoehe: Float,
): Boxabbildung {
    val rand = SCHABLONE_BREITE * 0.7f
    val zeichenBreite = (breiteVonBis.endInclusive - breiteVonBis.start) + rand * 2f
    val bandHoehe = BAND_UNTEN - BAND_OBEN
    val skala = minOf(flaecheBreite / zeichenBreite, flaecheHoehe / bandHoehe)

    val mitteBoxX = (breiteVonBis.start + breiteVonBis.endInclusive) / 2f
    val mitteBoxY = (BAND_OBEN + BAND_UNTEN) / 2f
    return Boxabbildung(
        skala = skala,
        versatzX = flaecheBreite / 2f - mitteBoxX * skala,
        versatzY = flaecheHoehe / 2f - mitteBoxY * skala,
    )
}

/** Baut den Compose-Pfad eines Strichs. */
fun Stroke.zuPath(ab: Boxabbildung): Path {
    val pfad = Path()
    val s = zumSchirm(ab, start)
    pfad.moveTo(s.x, s.y)
    for (segment in segmente) {
        when (segment) {
            is StrokeSegment.Linie -> {
                val e = zumSchirm(ab, segment.bis)
                pfad.lineTo(e.x, e.y)
            }
            is StrokeSegment.Bogen -> {
                val k1 = zumSchirm(ab, segment.kontrolle1)
                val k2 = zumSchirm(ab, segment.kontrolle2)
                val e = zumSchirm(ab, segment.bis)
                pfad.cubicTo(k1.x, k1.y, k2.x, k2.y, e.x, e.y)
            }
        }
    }
    return pfad
}

private fun zumSchirm(ab: Boxabbildung, p: GlyphPoint) = ab.zumSchirm(p)

/** Das Stück eines Pfads von Anfang bis [anteil] (0..1). */
fun teilPfad(pfad: Path, anteil: Float): Path {
    val ziel = Path()
    if (anteil <= 0f) return ziel
    val messer = PathMeasure()
    messer.setPath(pfad, false)
    val bis = (messer.length * anteil.coerceIn(0f, 1f))
    messer.getSegment(0f, bis, ziel, true)
    return ziel
}

/** Breite der Schablone und der Spur, in Boxeinheiten. */
const val SCHABLONE_BREITE = 94f
const val SPUR_BREITE = 34f
const val SCHEIN_BREITE = 60f

/** Zeichnet die Schablone eines Strichs — der helle Korridor, in dem das Kind bleiben soll. */
fun DrawScope.zeichneSchablone(pfad: Path, ab: Boxabbildung, farben: ZauberFarben) {
    drawPath(
        path = pfad,
        color = farben.schablone,
        style = StrichStil(
            width = ab.laenge(SCHABLONE_BREITE),
            cap = StrokeCap.Round,
            join = StrokeJoin.Round,
        ),
    )
    if (farben.mittellinie != Color.Transparent) {
        drawPath(
            path = pfad,
            color = farben.mittellinie,
            style = StrichStil(
                width = ab.laenge(8f),
                cap = StrokeCap.Round,
                pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(
                    floatArrayOf(ab.laenge(6f), ab.laenge(34f)),
                ),
            ),
        )
    }
}

/**
 * Die Zauberlinie: erst mehrere breite, blasse Lagen als Schein, dann der helle Kern.
 *
 * Bewusst KEIN Weichzeichner: `BlurMaskFilter` ist auf hardwarebeschleunigten Flächen
 * nicht überall zuverlässig, und `RenderEffect` gibt es erst ab Android 12 — die App läuft
 * ab Android 10. Drei übereinandergelegte Lagen sehen fast gleich aus und funktionieren
 * überall.
 */
fun DrawScope.zeichneSpur(
    pfad: Path,
    ab: Boxabbildung,
    farben: ZauberFarben,
    deckkraft: Float = 1f,
) {
    val lagen = listOf(1.95f to 0.10f, 1.5f to 0.15f, 1.18f to 0.22f)
    for ((faktor, alpha) in lagen) {
        drawPath(
            path = pfad,
            color = farben.spurSchein,
            alpha = alpha * deckkraft,
            style = StrichStil(
                width = ab.laenge(SCHEIN_BREITE) * faktor,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round,
            ),
        )
    }
    drawPath(
        path = pfad,
        color = farben.spurSchein,
        alpha = 0.85f * deckkraft,
        style = StrichStil(
            width = ab.laenge(SCHEIN_BREITE),
            cap = StrokeCap.Round,
            join = StrokeJoin.Round,
        ),
    )
    val kernStil = StrichStil(
        width = ab.laenge(SPUR_BREITE),
        cap = StrokeCap.Round,
        join = StrokeJoin.Round,
    )
    if (farben.spurKern.size == 1) {
        drawPath(pfad, farben.spurKern[0], alpha = deckkraft, style = kernStil)
    } else {
        // Farbband: der Verlauf läuft über die ganze Zeichenfläche, damit ein Strich in
        // der linken Hälfte andere Farben bekommt als einer in der rechten.
        drawPath(
            path = pfad,
            brush = Brush.linearGradient(
                colors = farben.spurKern,
                start = Offset(0f, 0f),
                end = Offset(size.width, size.height),
            ),
            alpha = deckkraft,
            style = kernStil,
        )
    }
}

/** Der gefüllte Punkt auf dem i. */
fun DrawScope.zeichneTupfer(mitte: Offset, ab: Boxabbildung, farbe: Color, alpha: Float = 1f) {
    drawCircle(color = farbe, radius = ab.laenge(SPUR_BREITE * 0.95f), center = mitte, alpha = alpha)
}

/** Startpunkt und Richtungspfeile — die Hilfen der Stufe „Nachfahren". */
fun DrawScope.zeichneHilfen(pfad: Path, strich: Stroke, ab: Boxabbildung, farben: ZauberFarben) {
    val messer = PathMeasure()
    messer.setPath(pfad, false)
    val laenge = messer.length
    if (laenge <= 0f) return

    for (bei in strich.pfeileBei) {
        val d = laenge * bei
        val punkt = messer.getPosition(d)
        val richtung = messer.getTangent(d)
        val winkel = atan2(richtung.y, richtung.x)
        zeichnePfeil(punkt, winkel, ab, farben.pfeil)
    }

    // Startpunkt: ein Ring, damit klar ist, wo der Finger aufsetzt.
    val start = messer.getPosition(0f)
    drawCircle(
        color = farben.startpunkt,
        radius = ab.laenge(52f),
        center = start,
        style = StrichStil(width = ab.laenge(9f)),
    )
    drawCircle(color = farben.startpunkt, radius = ab.laenge(16f), center = start, alpha = 0.55f)
}

/** Ein Winkel („Häkchen") quer zur Laufrichtung, wie in den Entwürfen. */
private fun DrawScope.zeichnePfeil(mitte: Offset, winkel: Float, ab: Boxabbildung, farbe: Color) {
    val arm = ab.laenge(40f)
    val spitze = ab.laenge(20f)
    val pfad = Path()
    // Zwei Schenkel, die nach hinten zeigen — die Spitze liegt in Laufrichtung.
    val hinten = winkel + Math.PI.toFloat()
    val links = hinten - 0.62f
    val rechts = hinten + 0.62f
    val s = Offset(
        mitte.x + kotlin.math.cos(winkel) * spitze,
        mitte.y + kotlin.math.sin(winkel) * spitze,
    )
    pfad.moveTo(s.x + kotlin.math.cos(links) * arm, s.y + kotlin.math.sin(links) * arm)
    pfad.lineTo(s.x, s.y)
    pfad.lineTo(s.x + kotlin.math.cos(rechts) * arm, s.y + kotlin.math.sin(rechts) * arm)
    drawPath(
        path = pfad,
        color = farbe,
        style = StrichStil(width = ab.laenge(24f), cap = StrokeCap.Round, join = StrokeJoin.Round),
    )
}

/** Die Schreiblinien des Hefts — nur in Themen, die sie haben wollen. */
fun DrawScope.zeichneSchreiblinien(ab: Boxabbildung, farben: ZauberFarben) {
    if (farben.schreiblinien == Color.Transparent) return
    val breite = ab.laenge(3f)
    fun linie(yBox: Float, gestrichelt: Boolean) {
        val y = ab.zumSchirm(GlyphPoint(0f, yBox)).y
        drawLine(
            color = farben.schreiblinien,
            start = Offset(0f, y),
            end = Offset(size.width, y),
            strokeWidth = breite,
            pathEffect = if (gestrichelt) {
                androidx.compose.ui.graphics.PathEffect.dashPathEffect(
                    floatArrayOf(ab.laenge(16f), ab.laenge(16f)),
                )
            } else {
                null
            },
        )
    }
    linie(150f, false)
    linie(400f, true)
    linie(850f, false)
}
