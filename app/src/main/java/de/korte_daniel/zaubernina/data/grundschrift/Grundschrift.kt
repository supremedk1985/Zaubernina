package de.korte_daniel.zaubernina.data.grundschrift

import de.korte_daniel.zaubernina.domain.Glyph
import de.korte_daniel.zaubernina.domain.GlyphPoint
import de.korte_daniel.zaubernina.domain.Stroke
import de.korte_daniel.zaubernina.domain.StrokeSegment.Bogen
import de.korte_daniel.zaubernina.domain.StrokeSegment.Linie

/*
 * DIE ZEICHEN DER GRUNDSCHRIFT — von Hand gezeichnet.
 *
 * Alles liegt in der normierten 1000er-Box (siehe domain/Glyph.kt):
 *
 *      150  Oberlinie      — Höhe der Großbuchstaben und der Oberlängen (b d h k l t)
 *      400  Mittellinie    — Höhe der kleinen Buchstaben (x-Höhe)
 *      850  Grundlinie     — hier stehen alle Buchstaben auf
 *      980  Unterlinie     — Tiefe der Unterlängen (g j p q y)
 *
 * DIE REIHENFOLGE DER STRICHE IST DER INHALT, nicht die Form. Ein Buchstabe, der richtig
 * aussieht, aber in der falschen Reihenfolge geschrieben wird, übt das Falsche ein. Die
 * Reihenfolgen hier folgen der üblichen Grundschrift-Vorgabe:
 *
 *   - von oben nach unten, von links nach rechts
 *   - Senkrechte vor Querstrich vor Bogen
 *   - Punkte und i-Tüpfelchen zuletzt
 *
 * DANIEL MUSS DAS GEGEN DAS SCHULHEFT PRÜFEN. Weicht die Schule ab, sind es hier ein paar
 * Zahlen — der Aufbau bleibt.
 */

private const val OBERLINIE = 150f
private const val MITTELLINIE = 400f
private const val GRUNDLINIE = 850f

private fun p(x: Float, y: Float) = GlyphPoint(x, y)

/** Großes I: ein einziger Zug von oben nach unten. Das einfachste Zeichen überhaupt. */
private val I = Glyph(
    zeichen = 'I',
    name = "I",
    striche = listOf(
        Stroke(
            start = p(500f, OBERLINIE),
            segmente = listOf(Linie(p(500f, GRUNDLINIE))),
        ),
    ),
)

/**
 * Großes N: linker Stamm herunter, Schräge herunter, rechter Stamm herunter.
 * Beide Stämme von oben nach unten — nicht der eine hoch und der andere runter.
 */
private val N_GROSS = Glyph(
    zeichen = 'N',
    name = "N",
    striche = listOf(
        Stroke(
            start = p(250f, OBERLINIE),
            segmente = listOf(Linie(p(250f, GRUNDLINIE))),
        ),
        Stroke(
            start = p(250f, OBERLINIE),
            segmente = listOf(Linie(p(750f, GRUNDLINIE))),
        ),
        Stroke(
            start = p(750f, OBERLINIE),
            segmente = listOf(Linie(p(750f, GRUNDLINIE))),
        ),
    ),
)

/** Kleines i: erst der Stamm, dann der Punkt. Der Punkt wird getippt, nicht gezogen. */
private val I_KLEIN = Glyph(
    zeichen = 'i',
    name = "i",
    striche = listOf(
        Stroke(
            start = p(500f, MITTELLINIE),
            segmente = listOf(Linie(p(500f, GRUNDLINIE))),
        ),
        Stroke(
            start = p(500f, 270f),
            // Ein winziges Wegstück, damit der Tupfer dieselbe Datenform hat wie ein Zug.
            segmente = listOf(Linie(p(500f, 276f))),
            pfeileBei = emptyList(),
            tupfer = true,
        ),
    ),
)

/**
 * Kleines n: Stamm herunter, dann von der Mitte des Stamms in einem Bogen hinauf,
 * herüber und rechts wieder herunter. Der Bogen setzt NICHT oben am Stamm an, sondern
 * ein Stück darunter — daher der Start bei 480.
 */
private val N_KLEIN = Glyph(
    zeichen = 'n',
    name = "n",
    striche = listOf(
        Stroke(
            start = p(350f, MITTELLINIE),
            segmente = listOf(Linie(p(350f, GRUNDLINIE))),
        ),
        Stroke(
            start = p(350f, 480f),
            segmente = listOf(
                Bogen(p(350f, 395f), p(610f, 395f), p(610f, 480f)),
                Linie(p(610f, GRUNDLINIE)),
            ),
            pfeileBei = listOf(0.30f, 0.78f),
        ),
    ),
)

/**
 * Kleines a: erst der runde Bauch gegen den Uhrzeigersinn, dann der Strich rechts.
 * Der Kreis beginnt oben (bei "zwölf Uhr") und läuft nach links — so steht es in den
 * meisten Grundschrift-Vorlagen. Beginnt eure Schule bei "eins" oder "zwei Uhr",
 * ändert sich nur der Startpunkt und die Reihenfolge der vier Bögen.
 */
private const val A_MITTE_X = 450f
private const val A_MITTE_Y = 625f
private const val A_RADIUS = 225f
private const val A_GRIFF = A_RADIUS * 0.5523f // Kreisnäherung durch vier kubische Bögen

private val A_KLEIN = Glyph(
    zeichen = 'a',
    name = "a",
    striche = listOf(
        Stroke(
            start = p(A_MITTE_X, A_MITTE_Y - A_RADIUS),
            segmente = listOf(
                // oben -> links
                Bogen(
                    p(A_MITTE_X - A_GRIFF, A_MITTE_Y - A_RADIUS),
                    p(A_MITTE_X - A_RADIUS, A_MITTE_Y - A_GRIFF),
                    p(A_MITTE_X - A_RADIUS, A_MITTE_Y),
                ),
                // links -> unten
                Bogen(
                    p(A_MITTE_X - A_RADIUS, A_MITTE_Y + A_GRIFF),
                    p(A_MITTE_X - A_GRIFF, A_MITTE_Y + A_RADIUS),
                    p(A_MITTE_X, A_MITTE_Y + A_RADIUS),
                ),
                // unten -> rechts
                Bogen(
                    p(A_MITTE_X + A_GRIFF, A_MITTE_Y + A_RADIUS),
                    p(A_MITTE_X + A_RADIUS, A_MITTE_Y + A_GRIFF),
                    p(A_MITTE_X + A_RADIUS, A_MITTE_Y),
                ),
                // rechts -> oben, zurück zum Start
                Bogen(
                    p(A_MITTE_X + A_RADIUS, A_MITTE_Y - A_GRIFF),
                    p(A_MITTE_X + A_GRIFF, A_MITTE_Y - A_RADIUS),
                    p(A_MITTE_X, A_MITTE_Y - A_RADIUS),
                ),
            ),
            pfeileBei = listOf(0.16f, 0.62f),
        ),
        Stroke(
            start = p(A_MITTE_X + A_RADIUS, MITTELLINIE),
            segmente = listOf(Linie(p(A_MITTE_X + A_RADIUS, GRUNDLINIE))),
        ),
    ),
)

/**
 * Was bisher gezeichnet ist. Der erste Satz soll die 18 Zeichen der sieben Namen
 * umfassen (D L M N P · a e h i l m n p r t · 1 2 3) — diese fünf reichen für "Nina"
 * und decken die schwierigen Fälle ab: ein Zug, drei Züge, ein Tupfer, ein Bogen und
 * ein geschlossener Kreis.
 */
val GRUNDSCHRIFT: Map<Char, Glyph> = listOf(I, N_GROSS, I_KLEIN, N_KLEIN, A_KLEIN)
    .associateBy { it.zeichen }

/** Die Wörter, die geübt werden. Später im Elternbereich erweiterbar. */
val WOERTER: List<String> = listOf("Nina")

fun glyph(zeichen: Char): Glyph? = GRUNDSCHRIFT[zeichen]
