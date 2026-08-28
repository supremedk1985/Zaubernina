package de.korte_daniel.zaubernina.domain

import kotlin.math.hypot

/**
 * Alle Zeichen leben in einer normierten Box von 1000 × 1000 Einheiten. Erst beim Zeichnen
 * wird auf die tatsächliche Bildschirmgröße skaliert — dadurch sind die Strichdaten von
 * Gerät und Bildschirmgröße unabhängig, und Toleranzen lassen sich in denselben Einheiten
 * angeben.
 *
 * Die Schreiblinien der Grundschrift liegen darin bei:
 *   Oberlinie 150 · Mittellinie (x-Höhe) 400 · Grundlinie 850 · Unterlinie 980
 */
const val BOX = 1000f

/** Ein Punkt in der Zeichenbox. */
data class GlyphPoint(val x: Float, val y: Float)

fun abstand(a: GlyphPoint, b: GlyphPoint): Float = hypot(a.x - b.x, a.y - b.y)

/** Ein Wegstück eines Strichs, immer in Schreibrichtung. */
sealed interface StrokeSegment {
    data class Linie(val bis: GlyphPoint) : StrokeSegment
    data class Bogen(
        val kontrolle1: GlyphPoint,
        val kontrolle2: GlyphPoint,
        val bis: GlyphPoint,
    ) : StrokeSegment
}

/**
 * Ein Strich: ein durchgehender Zug, den das Kind in EINER Bewegung schreibt, ohne den
 * Finger abzusetzen. Die Reihenfolge der Striche in [Glyph.striche] ist die Reihenfolge,
 * in der geschrieben wird — sie ist Teil der Schulschrift und nicht beliebig.
 *
 * [pfeileBei] sind Positionen von 0 bis 1 entlang des Strichs, an denen ein Richtungspfeil
 * gezeichnet wird.
 */
data class Stroke(
    val start: GlyphPoint,
    val segmente: List<StrokeSegment>,
    val pfeileBei: List<Float> = listOf(0.42f, 0.82f),
    /**
     * Ein Tupfer statt eines Zugs: der Punkt auf dem i, die Punkte über ä ö ü. Er wird
     * getippt, nicht nachgefahren, und deshalb als gefüllter Kreis gezeichnet statt als
     * Linie. Ohne diese Unterscheidung wäre ein Punkt nicht zu schaffen — eine
     * Berührung ohne Bewegung kommt in keinem Strich ans Ziel.
     */
    val tupfer: Boolean = false,
) {
    /**
     * Zerlegt den Strich in ungefähr gleich weit auseinanderliegende Stützpunkte — die
     * Grundlage sowohl fürs Zeichnen als auch für [de.korte_daniel.zaubernina.logic.StrokeTracker].
     *
     * "Ungefähr" heißt: je Wegstück wird die Länge geschätzt und die Punktzahl anteilig
     * verteilt. Gleichmäßige Abstände sind wichtig, weil die Toleranz ein fester Radius
     * ist — bei ungleichen Abständen wären Bögen strenger als Geraden.
     */
    fun abtasten(anzahl: Int = 120): List<GlyphPoint> {
        require(anzahl >= 2) { "Mindestens Start und Ende" }
        if (segmente.isEmpty()) return listOf(start)

        val laengen = mutableListOf<Float>()
        var von = start
        for (segment in segmente) {
            laengen += schaetzeLaenge(von, segment)
            von = segment.endpunkt()
        }
        val gesamt = laengen.sum()
        if (gesamt <= 0f) return listOf(start)

        val punkte = mutableListOf(start)
        von = start
        // Ein Punkt weniger, weil der Startpunkt schon drin ist.
        var verbleibend = anzahl - 1
        for ((i, segment) in segmente.withIndex()) {
            val letzter = i == segmente.lastIndex
            val teil = if (letzter) verbleibend else ((anzahl - 1) * (laengen[i] / gesamt)).toInt().coerceAtLeast(1)
            for (schritt in 1..teil) {
                punkte += punktAuf(von, segment, schritt.toFloat() / teil)
            }
            verbleibend -= teil
            von = segment.endpunkt()
            if (verbleibend <= 0 && !letzter) break
        }
        return punkte
    }
}

/**
 * Ein Zeichen. [zeichen] ist der Buchstabe oder die Ziffer selbst; [name] ist, wie das
 * Zeichen ausgesprochen wird (für die Sprachausgabe und den Knopf "Hör mal").
 */
data class Glyph(
    val zeichen: Char,
    val name: String,
    val striche: List<Stroke>,
) {
    val strichzahl: Int get() = striche.size
}

// ---- Geometrie, absichtlich ohne android.graphics: so ist alles auf der JVM testbar ----

internal fun StrokeSegment.endpunkt(): GlyphPoint = when (this) {
    is StrokeSegment.Linie -> bis
    is StrokeSegment.Bogen -> bis
}

internal fun punktAuf(von: GlyphPoint, segment: StrokeSegment, t: Float): GlyphPoint = when (segment) {
    is StrokeSegment.Linie -> GlyphPoint(
        von.x + (segment.bis.x - von.x) * t,
        von.y + (segment.bis.y - von.y) * t,
    )
    is StrokeSegment.Bogen -> {
        val u = 1f - t
        val a = u * u * u
        val b = 3f * u * u * t
        val c = 3f * u * t * t
        val d = t * t * t
        GlyphPoint(
            a * von.x + b * segment.kontrolle1.x + c * segment.kontrolle2.x + d * segment.bis.x,
            a * von.y + b * segment.kontrolle1.y + c * segment.kontrolle2.y + d * segment.bis.y,
        )
    }
}

/** Länge einer Geraden exakt, die eines Bogens über einen Streckenzug aus 16 Stücken. */
internal fun schaetzeLaenge(von: GlyphPoint, segment: StrokeSegment): Float = when (segment) {
    is StrokeSegment.Linie -> abstand(von, segment.bis)
    is StrokeSegment.Bogen -> {
        var laenge = 0f
        var vorher = von
        for (i in 1..16) {
            val jetzt = punktAuf(von, segment, i / 16f)
            laenge += abstand(vorher, jetzt)
            vorher = jetzt
        }
        laenge
    }
}
