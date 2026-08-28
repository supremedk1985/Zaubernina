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
 *   - Senkrechte zuerst, dann Querstriche, dann Bögen
 *   - Punkte und i-Tüpfelchen zuletzt
 *
 * DANIEL MUSS DAS GEGEN DAS SCHULHEFT PRÜFEN. Weicht die Schule ab, sind es hier ein paar
 * Zahlen — der Aufbau bleibt. Die Stellen, an denen es üblicherweise Unterschiede gibt,
 * sind bei den betroffenen Buchstaben vermerkt.
 */

private const val OBERLINIE = 150f
private const val MITTELLINIE = 400f
private const val GRUNDLINIE = 850f

private fun p(x: Float, y: Float) = GlyphPoint(x, y)

/** Ein gerader Zug von einem Punkt zum anderen. */
private fun zug(von: GlyphPoint, nach: GlyphPoint, pfeile: List<Float> = listOf(0.42f, 0.82f)) =
    Stroke(start = von, segmente = listOf(Linie(nach)), pfeileBei = pfeile)

/** Ein kurzer Zug bekommt nur einen Pfeil — zwei wären auf so wenig Strecke Gedränge. */
private val EIN_PFEIL = listOf(0.5f)

// ───────────────────────── Großbuchstaben ─────────────────────────

/** A: die beiden Schrägen von der Spitze herunter, dann der Querbalken. */
private val A_GROSS = Glyph(
    zeichen = 'A', name = "A",
    striche = listOf(
        zug(p(500f, OBERLINIE), p(230f, GRUNDLINIE)),
        zug(p(500f, OBERLINIE), p(770f, GRUNDLINIE)),
        zug(p(320f, 620f), p(680f, 620f), EIN_PFEIL),
    ),
)

/** D: Stamm herunter, dann der Bauch von oben im Bogen herum bis unten an den Stamm. */
private val D_GROSS = Glyph(
    zeichen = 'D', name = "D",
    striche = listOf(
        zug(p(270f, OBERLINIE), p(270f, GRUNDLINIE)),
        Stroke(
            start = p(270f, OBERLINIE),
            segmente = listOf(
                Bogen(p(620f, OBERLINIE), p(760f, 300f), p(760f, 500f)),
                Bogen(p(760f, 700f), p(620f, GRUNDLINIE), p(270f, GRUNDLINIE)),
            ),
            pfeileBei = listOf(0.28f, 0.74f),
        ),
    ),
)

/** E: Stamm herunter, dann die drei Balken von oben nach unten. */
private val E_GROSS = Glyph(
    zeichen = 'E', name = "E",
    striche = listOf(
        zug(p(280f, OBERLINIE), p(280f, GRUNDLINIE)),
        zug(p(280f, OBERLINIE), p(700f, OBERLINIE), EIN_PFEIL),
        zug(p(280f, 500f), p(640f, 500f), EIN_PFEIL),
        zug(p(280f, GRUNDLINIE), p(700f, GRUNDLINIE), EIN_PFEIL),
    ),
)

/** H: beide Stämme herunter, dann der Querbalken. */
private val H_GROSS = Glyph(
    zeichen = 'H', name = "H",
    striche = listOf(
        zug(p(270f, OBERLINIE), p(270f, GRUNDLINIE)),
        zug(p(730f, OBERLINIE), p(730f, GRUNDLINIE)),
        zug(p(270f, 500f), p(730f, 500f), EIN_PFEIL),
    ),
)

/** I: ein einziger Zug von oben nach unten. Das einfachste Zeichen überhaupt. */
private val I_GROSS = Glyph(
    zeichen = 'I', name = "I",
    striche = listOf(zug(p(500f, OBERLINIE), p(500f, GRUNDLINIE))),
)

/**
 * L: EIN Zug — herunter und ohne Absetzen nach rechts.
 * Manche Schulen lehren zwei getrennte Striche; dann wird aus dem einen Stroke hier
 * ein Paar wie beim E.
 */
private val L_GROSS = Glyph(
    zeichen = 'L', name = "L",
    striche = listOf(
        Stroke(
            start = p(290f, OBERLINIE),
            segmente = listOf(Linie(p(290f, GRUNDLINIE)), Linie(p(700f, GRUNDLINIE))),
            pfeileBei = listOf(0.32f, 0.86f),
        ),
    ),
)

/**
 * M: linker Stamm herunter, dann das V in EINEM Zug (hinunter und wieder hinauf),
 * dann der rechte Stamm herunter. Der Aufwärtszug in der Mitte gehört dazu — er ist
 * die einzige Stelle im Alphabet, an der nach oben geschrieben wird.
 */
private val M_GROSS = Glyph(
    zeichen = 'M', name = "M",
    striche = listOf(
        zug(p(230f, OBERLINIE), p(230f, GRUNDLINIE)),
        Stroke(
            start = p(230f, OBERLINIE),
            segmente = listOf(Linie(p(500f, 600f)), Linie(p(770f, OBERLINIE))),
            pfeileBei = listOf(0.28f, 0.78f),
        ),
        zug(p(770f, OBERLINIE), p(770f, GRUNDLINIE)),
    ),
)

/** N: linker Stamm herunter, Schräge herunter, rechter Stamm herunter. */
private val N_GROSS = Glyph(
    zeichen = 'N', name = "N",
    striche = listOf(
        zug(p(250f, OBERLINIE), p(250f, GRUNDLINIE)),
        zug(p(250f, OBERLINIE), p(750f, GRUNDLINIE)),
        zug(p(750f, OBERLINIE), p(750f, GRUNDLINIE)),
    ),
)

/** P: Stamm herunter, dann der Bauch von oben bis zur Mitte zurück an den Stamm. */
private val P_GROSS = Glyph(
    zeichen = 'P', name = "P",
    striche = listOf(
        zug(p(280f, OBERLINIE), p(280f, GRUNDLINIE)),
        Stroke(
            start = p(280f, OBERLINIE),
            segmente = listOf(
                Bogen(p(620f, OBERLINIE), p(730f, 230f), p(730f, 330f)),
                Bogen(p(730f, 430f), p(620f, 510f), p(280f, 510f)),
            ),
            pfeileBei = listOf(0.3f, 0.78f),
        ),
    ),
)

/** R: wie P, dazu das Bein von der Bauchunterkante schräg nach unten rechts. */
private val R_GROSS = Glyph(
    zeichen = 'R', name = "R",
    striche = listOf(
        zug(p(280f, OBERLINIE), p(280f, GRUNDLINIE)),
        Stroke(
            start = p(280f, OBERLINIE),
            segmente = listOf(
                Bogen(p(600f, OBERLINIE), p(700f, 230f), p(700f, 330f)),
                Bogen(p(700f, 430f), p(600f, 510f), p(280f, 510f)),
            ),
            pfeileBei = listOf(0.3f, 0.78f),
        ),
        zug(p(450f, 510f), p(740f, GRUNDLINIE)),
    ),
)

/** T: erst der Balken oben, dann der Stamm herunter. */
private val T_GROSS = Glyph(
    zeichen = 'T', name = "T",
    striche = listOf(
        zug(p(250f, OBERLINIE), p(750f, OBERLINIE), EIN_PFEIL),
        zug(p(500f, OBERLINIE), p(500f, GRUNDLINIE)),
    ),
)

// ───────── Kleinbuchstaben: schon gezeichnet, aber noch nicht im Einsatz ─────────
// Daniels Entscheidung vom 2026-08-28: erst einmal nur Großbuchstaben. Diese drei
// bleiben stehen, weil sie fertig und geprüft sind — sie kosten nichts und sind da,
// sobald die Kleinbuchstaben drankommen.

/** Kleines i: erst der Stamm, dann der Punkt. Der Punkt wird getippt, nicht gezogen. */
private val I_KLEIN = Glyph(
    zeichen = 'i', name = "i",
    striche = listOf(
        zug(p(500f, MITTELLINIE), p(500f, GRUNDLINIE)),
        Stroke(
            start = p(500f, 270f),
            // Ein winziges Wegstück, damit der Tupfer dieselbe Datenform hat wie ein Zug.
            segmente = listOf(Linie(p(500f, 276f))),
            pfeileBei = emptyList(),
            tupfer = true,
        ),
    ),
)

/** Kleines n: Stamm herunter, dann von der Mitte des Stamms im Bogen hinauf und rechts herunter. */
private val N_KLEIN = Glyph(
    zeichen = 'n', name = "n",
    striche = listOf(
        zug(p(350f, MITTELLINIE), p(350f, GRUNDLINIE)),
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

/** Kleines a: erst der runde Bauch gegen den Uhrzeigersinn ab zwölf Uhr, dann der Strich rechts. */
private const val A_MITTE_X = 450f
private const val A_MITTE_Y = 625f
private const val A_RADIUS = 225f
private const val A_GRIFF = A_RADIUS * 0.5523f // Kreisnäherung durch vier kubische Bögen

private val A_KLEIN = Glyph(
    zeichen = 'a', name = "a",
    striche = listOf(
        Stroke(
            start = p(A_MITTE_X, A_MITTE_Y - A_RADIUS),
            segmente = listOf(
                Bogen(p(A_MITTE_X - A_GRIFF, A_MITTE_Y - A_RADIUS), p(A_MITTE_X - A_RADIUS, A_MITTE_Y - A_GRIFF), p(A_MITTE_X - A_RADIUS, A_MITTE_Y)),
                Bogen(p(A_MITTE_X - A_RADIUS, A_MITTE_Y + A_GRIFF), p(A_MITTE_X - A_GRIFF, A_MITTE_Y + A_RADIUS), p(A_MITTE_X, A_MITTE_Y + A_RADIUS)),
                Bogen(p(A_MITTE_X + A_GRIFF, A_MITTE_Y + A_RADIUS), p(A_MITTE_X + A_RADIUS, A_MITTE_Y + A_GRIFF), p(A_MITTE_X + A_RADIUS, A_MITTE_Y)),
                Bogen(p(A_MITTE_X + A_RADIUS, A_MITTE_Y - A_GRIFF), p(A_MITTE_X + A_GRIFF, A_MITTE_Y - A_RADIUS), p(A_MITTE_X, A_MITTE_Y - A_RADIUS)),
            ),
            pfeileBei = listOf(0.16f, 0.62f),
        ),
        zug(p(A_MITTE_X + A_RADIUS, MITTELLINIE), p(A_MITTE_X + A_RADIUS, GRUNDLINIE)),
    ),
)


// ───────────────────────────── Ziffern ─────────────────────────────
// Gleiche Höhe wie die Großbuchstaben (Oberlinie bis Grundlinie). Die Schreibweisen
// folgen der üblichen deutschen Schulschreibung: die 1 mit Anstrich, die 7 mit
// Querstrich. AUCH HIER gilt: Daniel prüft gegen das Schulheft.

/** 1: Anstrich schräg hinauf zur Spitze, dann der Stamm herunter — ein Zug. */
private val EINS = Glyph(
    zeichen = '1', name = "Eins",
    striche = listOf(
        Stroke(
            start = p(350f, 320f),
            segmente = listOf(Linie(p(530f, OBERLINIE)), Linie(p(530f, GRUNDLINIE))),
            pfeileBei = listOf(0.30f, 0.75f),
        ),
    ),
)

/** 2: Bogen oben, Schwung hinunter nach links, Fuß nach rechts — ein Zug. */
private val ZWEI = Glyph(
    zeichen = '2', name = "Zwei",
    striche = listOf(
        Stroke(
            start = p(310f, 360f),
            segmente = listOf(
                Bogen(p(310f, 200f), p(420f, OBERLINIE), p(500f, OBERLINIE)),
                Bogen(p(610f, OBERLINIE), p(690f, 230f), p(690f, 360f)),
                Bogen(p(690f, 480f), p(560f, 580f), p(310f, GRUNDLINIE)),
                Linie(p(690f, GRUNDLINIE)),
            ),
            pfeileBei = listOf(0.22f, 0.72f),
        ),
    ),
)

/** 3: zwei Bögen übereinander — ein Zug. */
private val DREI = Glyph(
    zeichen = '3', name = "Drei",
    striche = listOf(
        Stroke(
            start = p(330f, 240f),
            segmente = listOf(
                Bogen(p(400f, 120f), p(680f, 130f), p(660f, 330f)),
                Bogen(p(660f, 470f), p(540f, 500f), p(470f, 500f)),
                Bogen(p(560f, 500f), p(690f, 540f), p(690f, 670f)),
                Bogen(p(690f, 850f), p(480f, 900f), p(330f, 770f)),
            ),
            pfeileBei = listOf(0.16f, 0.66f),
        ),
    ),
)

/** 4: Schräge hinunter und Querbalken in einem Zug, dann der Stamm. */
private val VIER = Glyph(
    zeichen = '4', name = "Vier",
    striche = listOf(
        Stroke(
            start = p(600f, OBERLINIE),
            segmente = listOf(Linie(p(300f, 580f)), Linie(p(740f, 580f))),
            pfeileBei = listOf(0.28f, 0.80f),
        ),
        zug(p(620f, OBERLINIE), p(620f, GRUNDLINIE)),
    ),
)

/** 5: Stamm und Bauch in einem Zug, der Deckel zuletzt. */
private val FUENF = Glyph(
    zeichen = '5', name = "Fünf",
    striche = listOf(
        Stroke(
            start = p(370f, OBERLINIE),
            segmente = listOf(
                Linie(p(370f, 450f)),
                Bogen(p(560f, 400f), p(700f, 490f), p(700f, 630f)),
                Bogen(p(700f, 820f), p(500f, 915f), p(350f, 780f)),
            ),
            pfeileBei = listOf(0.16f, 0.68f),
        ),
        zug(p(370f, OBERLINIE), p(690f, OBERLINIE), EIN_PFEIL),
    ),
)

/** 6: großer Schwung hinunter, unten die Schlaufe — ein Zug. */
private val SECHS = Glyph(
    zeichen = '6', name = "Sechs",
    striche = listOf(
        Stroke(
            start = p(640f, 160f),
            segmente = listOf(
                Bogen(p(460f, OBERLINIE), p(350f, 330f), p(340f, 540f)),
                Bogen(p(335f, 720f), p(430f, GRUNDLINIE), p(520f, GRUNDLINIE)),
                Bogen(p(650f, GRUNDLINIE), p(700f, 750f), p(700f, 650f)),
                Bogen(p(700f, 520f), p(590f, 470f), p(500f, 480f)),
                Bogen(p(420f, 490f), p(360f, 540f), p(345f, 600f)),
            ),
            pfeileBei = listOf(0.14f, 0.58f),
        ),
    ),
)

/** 7: Deckel und Schräge in einem Zug, dann der deutsche Querstrich. */
private val SIEBEN = Glyph(
    zeichen = '7', name = "Sieben",
    striche = listOf(
        Stroke(
            start = p(300f, OBERLINIE),
            segmente = listOf(Linie(p(700f, OBERLINIE)), Linie(p(430f, GRUNDLINIE))),
            pfeileBei = listOf(0.24f, 0.72f),
        ),
        zug(p(370f, 520f), p(650f, 520f), EIN_PFEIL),
    ),
)

/** Die Ziffern der sieben Levelabschnitte. */
val ZIFFERN: List<Glyph> = listOf(EINS, ZWEI, DREI, VIER, FUENF, SECHS, SIEBEN)

// ───────────────────────────── Bestand ─────────────────────────────

/** Alle Großbuchstaben, die die sieben Namen brauchen: A D E H I L M N P R T. */
val GROSSBUCHSTABEN: List<Glyph> =
    listOf(A_GROSS, D_GROSS, E_GROSS, H_GROSS, I_GROSS, L_GROSS, M_GROSS, N_GROSS, P_GROSS, R_GROSS, T_GROSS)

/** Gezeichnet, aber noch nicht im Einsatz — siehe Hinweis oben. */
val KLEINBUCHSTABEN: List<Glyph> = listOf(I_KLEIN, N_KLEIN, A_KLEIN)

val GRUNDSCHRIFT: Map<Char, Glyph> = (GROSSBUCHSTABEN + KLEINBUCHSTABEN + ZIFFERN).associateBy { it.zeichen }

/**
 * Die Wörter, die geübt werden — vorerst in Großbuchstaben, so wie die meisten
 * Erstklässler anfangen.
 *
 * Die REIHENFOLGE steht nicht hier, sondern in domain/Level.kt: dort ist sie die Reise.
 * Diese Liste ist nur die Prüfliste "welche Wörter müssen zeichenbar sein" und wird
 * daraus abgeleitet, damit beide nicht auseinanderlaufen können.
 */
val WOERTER: List<String> get() = de.korte_daniel.zaubernina.domain.LEVEL.map { it.wort }

fun glyph(zeichen: Char): Glyph? = GRUNDSCHRIFT[zeichen]

/**
 * Die Breite des breitesten Zeichens. Sie ist der gemeinsame Bezug für die Darstellung:
 * Alle Zeichen werden mit DEMSELBEN Maßstab gezeigt, damit ein schmales I nicht größer
 * wirkt als ein breites N.
 */
val BREITESTE_ZEICHENBREITE: Float = GRUNDSCHRIFT.values.maxOf { glyphe ->
    val punkte = glyphe.striche.flatMap { it.abtasten(40) }
    punkte.maxOf { it.x } - punkte.minOf { it.x }
}
