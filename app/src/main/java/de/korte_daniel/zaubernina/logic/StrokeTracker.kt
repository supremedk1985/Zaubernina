package de.korte_daniel.zaubernina.logic

import de.korte_daniel.zaubernina.domain.GlyphPoint
import de.korte_daniel.zaubernina.domain.abstand

/** Wie genau die Linie getroffen werden muss. Radien in Einheiten der 1000er-Zeichenbox. */
enum class Genauigkeit(val toleranz: Float) {
    LEICHT(155f),
    NORMAL(115f),
    GENAU(78f),
}

/** Was der Tracker nach einer Berührung sagt. */
enum class Zug {
    /** Noch nicht losgelaufen — der Finger muss zuerst den Startpunkt treffen. */
    WARTET,

    /** Der Finger ist auf der Linie und kommt voran. */
    LAEUFT,

    /** Der Finger ist abgekommen. Der Fortschritt bleibt erhalten, die Spur wird blass. */
    DANEBEN,

    /** Der Strich ist zu Ende geschrieben. */
    FERTIG,
}

/**
 * Prüft, ob ein Strich richtig nachgefahren wird: in der richtigen Richtung, ohne
 * Abkürzung, ohne Rückwärtsgehen.
 *
 * Bewusst rein — keine Compose-, keine Android-Typen. Dieselbe Konvention wie CurruBikes
 * `GpsFilter`: die Entscheidungslogik ist auf der JVM testbar, die UI zeichnet nur, was
 * hier herauskommt.
 *
 * Das Verfahren: Der Strich liegt als Kette gleich weit auseinanderliegender Stützpunkte
 * vor. Gemerkt wird nur, bis zu welchem Punkt das Kind gekommen ist. Bei jeder Bewegung
 * wird in den nächsten [vorausschau] Punkten der WEITESTE gesucht, der noch im
 * Toleranzradius liegt. Damit ist die Prüfung großzügig (kleine Wackler fallen nicht auf),
 * erzwingt aber Richtung und Reihenfolge — und die begrenzte Vorausschau verhindert, dass
 * eine Gerade quer über einen Bogen als "nachgefahren" durchgeht.
 *
 * @param punkte die Stützpunkte des Strichs in Schreibrichtung
 * @param toleranz Radius, in dem eine Berührung als Treffer gilt
 * @param vorausschau wie viele Stützpunkte eine einzelne Bewegung höchstens überspringen darf
 * @param startToleranz Radius um den Startpunkt; etwas großzügiger, weil das Kind erst
 *        einmal hinfinden muss
 */
class StrokeTracker(
    private val punkte: List<GlyphPoint>,
    private val toleranz: Float = Genauigkeit.NORMAL.toleranz,
    private val vorausschau: Int = 12,
    private val startToleranz: Float = toleranz * 1.35f,
    /** Punkt auf dem i: ein Tippen genügt, es wird nichts nachgefahren. */
    private val tupfer: Boolean = false,
) {
    init {
        require(punkte.size >= 2) { "Ein Strich braucht mindestens zwei Punkte" }
    }

    /** Index des zuletzt erreichten Stützpunkts, -1 solange nicht gestartet wurde. */
    var fortschritt: Int = -1
        private set

    var zustand: Zug = Zug.WARTET
        private set

    /** 0..1 — wie viel des Strichs geschrieben ist. Für die Länge der Leuchtspur. */
    val anteil: Float
        get() = if (fortschritt <= 0) 0f else fortschritt.toFloat() / (punkte.size - 1)

    val fertig: Boolean get() = zustand == Zug.FERTIG

    /** Der Finger geht auf den Bildschirm. Zählt nur, wenn er den Startpunkt trifft. */
    fun senke(p: GlyphPoint): Zug {
        if (zustand == Zug.FERTIG) return zustand
        val getroffen = abstand(p, punkte.first()) <= startToleranz
        zustand = when {
            // Ein Tupfer ist mit dem Tippen erledigt.
            getroffen && tupfer -> {
                fortschritt = punkte.lastIndex
                Zug.FERTIG
            }
            getroffen -> {
                fortschritt = 0
                Zug.LAEUFT
            }
            else -> {
                fortschritt = -1
                Zug.WARTET
            }
        }
        return zustand
    }

    /** Der Finger bewegt sich. */
    fun ziehe(p: GlyphPoint): Zug {
        if (zustand == Zug.FERTIG) return zustand
        // Solange nicht gestartet wurde, kann ein Vorbeiziehen den Strich nicht beginnen —
        // sonst würde ein Wisch quer über den Bildschirm mittendrin einsteigen.
        if (zustand == Zug.WARTET) return zustand

        // Gesucht ist der NÄCHSTGELEGENE Stützpunkt im Fenster, nicht der weiteste. Der
        // weiteste wäre falsch: die Toleranz (rund 115) ist viel größer als der Abstand
        // zweier Stützpunkte (rund 6), ein stillstehender Finger würde also allein durch
        // Wackeln ein Sechstel des Strichs "schreiben". Der nächstgelegene Punkt bildet
        // dagegen ab, wo der Finger wirklich ist; die Toleranz wirkt dann als Breite des
        // Korridors, in dem er bleiben muss.
        //
        // Das Fenster beginnt bei fortschritt (nicht dahinter), damit Stehenbleiben
        // erlaubt ist — und weil es damit nie rückwärts gehen kann.
        val bis = (fortschritt + vorausschau).coerceAtMost(punkte.lastIndex)
        var naechster = fortschritt
        var kleinster = abstand(p, punkte[fortschritt])
        for (i in (fortschritt + 1)..bis) {
            val d = abstand(p, punkte[i])
            if (d < kleinster) {
                kleinster = d
                naechster = i
            }
        }

        zustand = if (kleinster <= toleranz) {
            fortschritt = naechster
            if (fortschritt == punkte.lastIndex || amZiel(p)) {
                fortschritt = punkte.lastIndex
                Zug.FERTIG
            } else {
                Zug.LAEUFT
            }
        } else {
            Zug.DANEBEN
        }
        return zustand
    }

    /**
     * Nachsicht am Strichende: Wer den letzten Zehntel geschrieben hat und mit dem Finger
     * beim Endpunkt steht, hat den Strich geschafft — auch wenn er den allerletzten
     * Stützpunkt nicht exakt getroffen hat. Ohne das scheitert ein Kind daran, dass es
     * kurz vor Schluss loslässt, und der ganze Strich beginnt von vorn.
     *
     * Der Fortschrittsanteil in der Bedingung ist wichtig: ohne ihn könnte ein Zeichen mit
     * zusammenfallendem Anfang und Ende (das O) mit einer Berührung "fertig" sein.
     */
    private fun amZiel(p: GlyphPoint): Boolean =
        fortschritt >= (punkte.lastIndex * 0.9f).toInt() && abstand(p, punkte.last()) <= toleranz

    /**
     * Der Finger geht hoch. Ein unvollendeter Strich beginnt von vorn — ein Strich ist
     * eine durchgehende Bewegung, und genau das soll geübt werden. (Wäre das zu streng,
     * ist hier der eine Ort, an dem man es ändert.)
     */
    fun hebe(): Zug {
        if (zustand != Zug.FERTIG) {
            fortschritt = -1
            zustand = Zug.WARTET
        }
        return zustand
    }

    fun zuruecksetzen() {
        fortschritt = -1
        zustand = Zug.WARTET
    }
}
