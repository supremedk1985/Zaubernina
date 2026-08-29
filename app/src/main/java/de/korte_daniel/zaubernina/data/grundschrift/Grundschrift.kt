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
 * aussieht, aber in der falschen Reihenfolge geschrieben wird, übt das Falsche ein.
 *
 * QUELLE seit dem 2026-08-29: die Bewegungsverläufe der GRUNDSCHRIFT-KARTEI des
 * Grundschulverbands (Lehrerkommentar zu Teil 1, grundschulverband.de) — der in
 * NRW-Grundschulen verbreitete Standard. Daniel hatte zu Recht moniert, dass die erste
 * Fassung nach allgemeinen Faustregeln gezeichnet war. Kernpunkte der Kartei:
 *
 *   - Abstriche zuerst, Querstriche DANACH (T: Stamm vor Deckel; H „in der Regel beide
 *     Abstriche, danach der Querstrich")
 *   - Zickzack-Buchstaben (V W M, auch A) laufen als EIN Zug mit Haltepunkten — beim N
 *     wird der rechte Stamm als AUFSTRICH nach oben geschrieben
 *   - Ovale gegen den Uhrzeigersinn (C O Q G, Ziffer 0); B D P R Bögen im Uhrzeigersinn,
 *     bei R hängen Bogen und Bein in einem Zug
 *   - Mittel-Querstriche (E F H) und die Zackentiefen von M/W liegen auf der Mittellinie
 *   - Punkte und Umlautpunkte zuletzt
 *
 * Ziffern nach dem Ziffernschreibkurs (Merksprüche, schulimpulse.de) — alle bestätigt.
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

/**
 * A: diagonaler AUFSTRICH von unten links zur Spitze, Haltepunkt, Abstrich nach unten
 * rechts — ein Zug. Dann der Querbalken. (Kartei: „beginnt mit einem diagonalen
 * Aufstrich von der unteren Grenze des Mittelbandes".)
 */
private val A_GROSS = Glyph(
    zeichen = 'A', name = "A",
    striche = listOf(
        Stroke(
            start = p(230f, GRUNDLINIE),
            segmente = listOf(Linie(p(500f, OBERLINIE)), Linie(p(770f, GRUNDLINIE))),
            pfeileBei = listOf(0.24f, 0.76f),
        ),
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
        // Der Mittelbalken liegt auf der Mittellinie — wie bei F und H (Grundschrift).
        zug(p(280f, MITTELLINIE), p(640f, MITTELLINIE), EIN_PFEIL),
        zug(p(280f, GRUNDLINIE), p(700f, GRUNDLINIE), EIN_PFEIL),
    ),
)

/** H: beide Stämme herunter, dann der Querbalken. */
private val H_GROSS = Glyph(
    zeichen = 'H', name = "H",
    striche = listOf(
        zug(p(270f, OBERLINIE), p(270f, GRUNDLINIE)),
        zug(p(730f, OBERLINIE), p(730f, GRUNDLINIE)),
        // Querstrich „auf der oberen Grenze des Mittelbandes" (Kartei) = Mittellinie.
        zug(p(270f, MITTELLINIE), p(730f, MITTELLINIE), EIN_PFEIL),
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
 * M: linker Stamm herunter; dann in EINEM Zug vom Anfangspunkt: Diagonale hinunter bis
 * zur MITTELLINIE, hinauf, und der rechte Stamm gerade herunter (Kartei: „Folge von vier
 * Auf- und Abstrichen", die Zacke reicht nur „bis zur oberen Grenze des Mittelbandes").
 */
private val M_GROSS = Glyph(
    zeichen = 'M', name = "M",
    striche = listOf(
        zug(p(230f, OBERLINIE), p(230f, GRUNDLINIE)),
        Stroke(
            start = p(230f, OBERLINIE),
            segmente = listOf(Linie(p(500f, MITTELLINIE)), Linie(p(770f, OBERLINIE)), Linie(p(770f, GRUNDLINIE))),
            pfeileBei = listOf(0.2f, 0.55f, 0.9f),
        ),
    ),
)

/**
 * N: linker Stamm herunter; dann in EINEM Zug: Diagonale hinunter und der rechte Stamm
 * als AUFSTRICH gerade nach oben (Kartei: „nach einem kurzen Haltepunkt folgt der letzte
 * Aufstrich gerade nach oben"). Der rechte Stamm wird beim N von unten geschrieben.
 */
private val N_GROSS = Glyph(
    zeichen = 'N', name = "N",
    striche = listOf(
        zug(p(250f, OBERLINIE), p(250f, GRUNDLINIE)),
        Stroke(
            start = p(250f, OBERLINIE),
            segmente = listOf(Linie(p(750f, GRUNDLINIE)), Linie(p(750f, OBERLINIE))),
            pfeileBei = listOf(0.26f, 0.76f),
        ),
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

/**
 * R: Stamm; dann in EINEM Zug der Halbkreis im Uhrzeigersinn bis zur Stammmitte und
 * von dort das Bein diagonal hinunter (Kartei: der Bogen „schließt bündig an", „nach
 * einem kurzen Haltepunkt wird von dieser Position aus ein diagonaler Strich" gezogen).
 */
private val R_GROSS = Glyph(
    zeichen = 'R', name = "R",
    striche = listOf(
        zug(p(280f, OBERLINIE), p(280f, GRUNDLINIE)),
        Stroke(
            start = p(280f, OBERLINIE),
            segmente = listOf(
                Bogen(p(600f, OBERLINIE), p(700f, 230f), p(700f, 330f)),
                Bogen(p(700f, 430f), p(600f, 500f), p(280f, 500f)),
                Linie(p(740f, GRUNDLINIE)),
            ),
            pfeileBei = listOf(0.24f, 0.62f, 0.9f),
        ),
    ),
)

/** T: erst der Stamm, dann der Deckel — Kartei: „einfacher Abstrich mit nachfolgendem Querstrich". */
private val T_GROSS = Glyph(
    zeichen = 'T', name = "T",
    striche = listOf(
        zug(p(500f, OBERLINIE), p(500f, GRUNDLINIE)),
        zug(p(250f, OBERLINIE), p(750f, OBERLINIE), EIN_PFEIL),
    ),
)


// ─────────────── Die restlichen Großbuchstaben (Nachtstapel 2026-08-28) ───────────────
// Daniel will eigene Wörter hinterlegen können — dafür muss das ganze Alphabet da sein.
// Bögen als kubische Näherung; Endpunkte liegen EXAKT auf den Schreiblinien, weil die
// Bandprüfung sonst zu Recht meckert (Kontrollpunkte liegen nicht auf der Kurve).

private const val KREIS_GRIFF = 0.5523f

/** B: Stamm, dann beide Bäuche in einem Zug. */
private val B_GROSS = Glyph(
    zeichen = 'B', name = "B",
    striche = listOf(
        zug(p(280f, OBERLINIE), p(280f, GRUNDLINIE)),
        Stroke(
            start = p(280f, OBERLINIE),
            segmente = listOf(
                Bogen(p(560f, OBERLINIE), p(640f, 220f), p(640f, 320f)),
                Bogen(p(640f, 430f), p(560f, 495f), p(280f, 495f)),
                Bogen(p(600f, 495f), p(700f, 560f), p(700f, 660f)),
                Bogen(p(700f, 790f), p(590f, GRUNDLINIE), p(280f, GRUNDLINIE)),
            ),
            pfeileBei = listOf(0.2f, 0.7f),
        ),
    ),
)

/** C: ein offener Bogen gegen den Uhrzeigersinn, oben beginnend. */
private val C_GROSS = Glyph(
    zeichen = 'C', name = "C",
    striche = listOf(
        Stroke(
            start = p(700f, 280f),
            segmente = listOf(
                Bogen(p(670f, 200f), p(600f, OBERLINIE), p(500f, OBERLINIE)),
                Bogen(p(384f, OBERLINIE), p(290f, 307f), p(290f, 500f)),
                Bogen(p(290f, 693f), p(384f, GRUNDLINIE), p(500f, GRUNDLINIE)),
                Bogen(p(600f, GRUNDLINIE), p(670f, 800f), p(700f, 720f)),
            ),
            pfeileBei = listOf(0.18f, 0.68f),
        ),
    ),
)

/** F: Stamm, Deckel, Mittelbalken. */
private val F_GROSS = Glyph(
    zeichen = 'F', name = "F",
    striche = listOf(
        zug(p(280f, OBERLINIE), p(280f, GRUNDLINIE)),
        zug(p(280f, OBERLINIE), p(700f, OBERLINIE), EIN_PFEIL),
        zug(p(280f, MITTELLINIE), p(640f, MITTELLINIE), EIN_PFEIL),
    ),
)

/**
 * G: das Dreiviertel-Linksoval gegen den Uhrzeigersinn; rechts kommt der Bogen bis zur
 * MITTELLINIE hoch und geht dort rechtwinklig in den Querstrich nach innen über — ein
 * Zug (Kartei: „hier geht sie rechtwinklig über in einen Querstrich").
 */
private val G_GROSS = Glyph(
    zeichen = 'G', name = "G",
    striche = listOf(
        Stroke(
            start = p(700f, 270f),
            segmente = listOf(
                Bogen(p(670f, 195f), p(600f, OBERLINIE), p(500f, OBERLINIE)),
                Bogen(p(384f, OBERLINIE), p(290f, 307f), p(290f, 500f)),
                Bogen(p(290f, 693f), p(384f, GRUNDLINIE), p(500f, GRUNDLINIE)),
                Bogen(p(640f, GRUNDLINIE), p(710f, 700f), p(710f, MITTELLINIE)),
                Linie(p(500f, MITTELLINIE)),
            ),
            pfeileBei = listOf(0.16f, 0.62f),
        ),
    ),
)

/**
 * J: Stamm herunter BIS UNTER die Grundlinie, dort der Linksbogen gegen den
 * Uhrzeigersinn (Kartei: das große J reicht „vom Oberlängen- bis zum Unterlängenband",
 * der Bogen „endet in der Mitte des Unterlängenbandes"). Das große J ist der einzige
 * Großbuchstabe mit Unterlänge.
 */
private val J_GROSS = Glyph(
    zeichen = 'J', name = "J",
    striche = listOf(
        Stroke(
            start = p(650f, OBERLINIE),
            segmente = listOf(
                Linie(p(650f, 800f)),
                Bogen(p(650f, 940f), p(560f, 985f), p(470f, 975f)),
                Bogen(p(400f, 965f), p(360f, 940f), p(350f, 900f)),
            ),
            pfeileBei = listOf(0.3f, 0.8f),
        ),
    ),
)

/**
 * K: Stamm, dann beide Arme in einem Zug — der Knick BERÜHRT den Stamm auf der
 * Mittellinie (Kartei: „berührt den ersten Abstrich auf der unteren Grenze des
 * Oberlängenbandes").
 */
private val K_GROSS = Glyph(
    zeichen = 'K', name = "K",
    striche = listOf(
        zug(p(280f, OBERLINIE), p(280f, GRUNDLINIE)),
        Stroke(
            start = p(700f, OBERLINIE),
            segmente = listOf(Linie(p(290f, MITTELLINIE)), Linie(p(700f, GRUNDLINIE))),
            pfeileBei = listOf(0.26f, 0.76f),
        ),
    ),
)

/** O: der Kreis gegen den Uhrzeigersinn, oben beginnend — wie beim kleinen a. */
private val O_GROSS = Glyph(
    zeichen = 'O', name = "O",
    striche = listOf(
        Stroke(
            start = p(500f, OBERLINIE),
            segmente = listOf(
                Bogen(p(384f, OBERLINIE), p(290f, 307f), p(290f, 500f)),
                Bogen(p(290f, 693f), p(384f, GRUNDLINIE), p(500f, GRUNDLINIE)),
                Bogen(p(616f, GRUNDLINIE), p(710f, 693f), p(710f, 500f)),
                Bogen(p(710f, 307f), p(616f, OBERLINIE), p(500f, OBERLINIE)),
            ),
            pfeileBei = listOf(0.16f, 0.62f),
        ),
    ),
)

/** Q: das O, dann das Schwänzchen. */
private val Q_GROSS = Glyph(
    zeichen = 'Q', name = "Q",
    striche = O_GROSS.striche + listOf(
        zug(p(560f, 690f), p(730f, GRUNDLINIE), EIN_PFEIL),
    ),
)

/** S: die Schlange in einem Zug, oben rechts beginnend. */
private val S_GROSS = Glyph(
    zeichen = 'S', name = "S",
    striche = listOf(
        Stroke(
            start = p(690f, 255f),
            segmente = listOf(
                Bogen(p(660f, 175f), p(560f, 148f), p(475f, OBERLINIE)),
                Bogen(p(355f, 155f), p(310f, 240f), p(330f, 330f)),
                Bogen(p(350f, 430f), p(470f, 470f), p(540f, 500f)),
                Bogen(p(620f, 535f), p(690f, 600f), p(690f, 690f)),
                Bogen(p(690f, 805f), p(560f, 862f), p(440f, GRUNDLINIE)),
                Bogen(p(365f, 843f), p(320f, 795f), p(310f, 735f)),
            ),
            pfeileBei = listOf(0.14f, 0.62f),
        ),
    ),
)

/** U: hinunter, der Bogen unten, wieder hinauf — ein Zug. */
private val U_GROSS = Glyph(
    zeichen = 'U', name = "U",
    striche = listOf(
        Stroke(
            start = p(300f, OBERLINIE),
            segmente = listOf(
                Linie(p(300f, 600f)),
                Bogen(p(300f, 790f), p(390f, GRUNDLINIE), p(500f, GRUNDLINIE)),
                Bogen(p(610f, GRUNDLINIE), p(700f, 790f), p(700f, 600f)),
                Linie(p(700f, OBERLINIE)),
            ),
            pfeileBei = listOf(0.2f, 0.78f),
        ),
    ),
)

/** V: hinunter und hinauf in einem Zug. */
private val V_GROSS = Glyph(
    zeichen = 'V', name = "V",
    striche = listOf(
        Stroke(
            start = p(240f, OBERLINIE),
            segmente = listOf(Linie(p(500f, GRUNDLINIE)), Linie(p(760f, OBERLINIE))),
            pfeileBei = listOf(0.26f, 0.76f),
        ),
    ),
)

/**
 * W: zweimal hinunter und hinauf — ein Zug. Die mittlere Zacke reicht nur bis zur
 * MITTELLINIE (Kartei: „Aufstrich bis zur oberen Grenze des Mittelbandes").
 */
private val W_GROSS = Glyph(
    zeichen = 'W', name = "W",
    striche = listOf(
        Stroke(
            start = p(180f, OBERLINIE),
            segmente = listOf(
                Linie(p(340f, GRUNDLINIE)),
                Linie(p(500f, MITTELLINIE)),
                Linie(p(660f, GRUNDLINIE)),
                Linie(p(820f, OBERLINIE)),
            ),
            pfeileBei = listOf(0.14f, 0.62f),
        ),
    ),
)

/** X: zwei Schrägen, beide von oben. */
private val X_GROSS = Glyph(
    zeichen = 'X', name = "X",
    striche = listOf(
        zug(p(280f, OBERLINIE), p(720f, GRUNDLINIE)),
        zug(p(720f, OBERLINIE), p(280f, GRUNDLINIE)),
    ),
)

/**
 * Y: erste Schräge bis zur MITTELLINIE; die zweite beginnt oben rechts, berührt dort den
 * Endpunkt der ersten und läuft weiter bis zur Grundlinie GENAU UNTER dem Anfang der
 * ersten (Kartei-Beschreibung des großen Y — das Bein ist schräg, nicht senkrecht).
 */
private val Y_GROSS = Glyph(
    zeichen = 'Y', name = "Y",
    striche = listOf(
        zug(p(280f, OBERLINIE), p(500f, MITTELLINIE), EIN_PFEIL),
        Stroke(
            start = p(720f, OBERLINIE),
            segmente = listOf(Linie(p(500f, MITTELLINIE)), Linie(p(280f, GRUNDLINIE))),
            pfeileBei = listOf(0.28f, 0.76f),
        ),
    ),
)

/** Z: Deckel, Schräge, Boden — ein Zug. */
private val Z_GROSS = Glyph(
    zeichen = 'Z', name = "Z",
    striche = listOf(
        Stroke(
            start = p(280f, OBERLINIE),
            segmente = listOf(Linie(p(720f, OBERLINIE)), Linie(p(280f, GRUNDLINIE)), Linie(p(720f, GRUNDLINIE))),
            pfeileBei = listOf(0.16f, 0.55f),
        ),
    ),
)

/** Die zwei Umlautpunkte — getippt, nicht gezogen, und ZULETZT. */
private fun umlautpunkte(): List<Stroke> = listOf(
    Stroke(p(395f, 60f), listOf(Linie(p(395f, 66f))), emptyList(), tupfer = true),
    Stroke(p(605f, 60f), listOf(Linie(p(605f, 66f))), emptyList(), tupfer = true),
)

private val AE_GROSS = Glyph('Ä', "Ä", A_GROSS.striche + umlautpunkte())
private val OE_GROSS = Glyph('Ö', "Ö", O_GROSS.striche + umlautpunkte())
private val UE_GROSS = Glyph('Ü', "Ü", U_GROSS.striche + umlautpunkte())

// ─────────────── Die restlichen Ziffern: 0, 8, 9 ───────────────
// Gebraucht für den Rechenmodus, in dem die Antwort geschrieben wird.

/** 0: die schmale Ellipse gegen den Uhrzeigersinn, oben beginnend. */
private val NULL_ZIFFER = Glyph(
    zeichen = '0', name = "Null",
    striche = listOf(
        Stroke(
            start = p(500f, OBERLINIE),
            segmente = listOf(
                Bogen(p(400f, OBERLINIE), p(320f, 307f), p(320f, 500f)),
                Bogen(p(320f, 693f), p(400f, GRUNDLINIE), p(500f, GRUNDLINIE)),
                Bogen(p(600f, GRUNDLINIE), p(680f, 693f), p(680f, 500f)),
                Bogen(p(680f, 307f), p(600f, OBERLINIE), p(500f, OBERLINIE)),
            ),
            pfeileBei = listOf(0.16f, 0.62f),
        ),
    ),
)

/** 8: die gekreuzte Schleife in einem Zug, oben rechts beginnend. */
private val ACHT = Glyph(
    zeichen = '8', name = "Acht",
    striche = listOf(
        Stroke(
            start = p(650f, 240f),
            segmente = listOf(
                Bogen(p(620f, 165f), p(530f, 145f), p(455f, 155f)),
                Bogen(p(345f, 175f), p(330f, 300f), p(400f, 380f)),
                Bogen(p(470f, 450f), p(620f, 480f), p(660f, 570f)),
                Bogen(p(700f, 680f), p(680f, 800f), p(560f, 848f)),
                Bogen(p(430f, 880f), p(340f, 790f), p(370f, 690f)),
                Bogen(p(400f, 600f), p(520f, 560f), p(590f, 500f)),
                Bogen(p(660f, 440f), p(680f, 330f), p(650f, 240f)),
            ),
            pfeileBei = listOf(0.1f, 0.55f),
        ),
    ),
)

/** 9: der Kringel oben, dann der Stamm — ein Zug. */
private val NEUN = Glyph(
    zeichen = '9', name = "Neun",
    striche = listOf(
        Stroke(
            start = p(680f, 350f),
            segmente = listOf(
                Bogen(p(680f, 200f), p(570f, 148f), p(490f, 152f)),
                Bogen(p(360f, 158f), p(315f, 260f), p(315f, 350f)),
                Bogen(p(315f, 460f), p(420f, 515f), p(505f, 510f)),
                Bogen(p(590f, 500f), p(655f, 450f), p(678f, 390f)),
                Linie(p(650f, GRUNDLINIE)),
            ),
            pfeileBei = listOf(0.14f, 0.8f),
        ),
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

/** Alle zehn Ziffern — 1 bis 7 für die Reise, 0/8/9 dazu für den Rechenmodus. */
val ZIFFERN: List<Glyph> = listOf(NULL_ZIFFER, EINS, ZWEI, DREI, VIER, FUENF, SECHS, SIEBEN, ACHT, NEUN)

// ───────────────────────────── Bestand ─────────────────────────────

/** Das komplette große Alphabet samt Umlauten — Voraussetzung für eigene Wörter. */
val GROSSBUCHSTABEN: List<Glyph> = listOf(
    A_GROSS, B_GROSS, C_GROSS, D_GROSS, E_GROSS, F_GROSS, G_GROSS, H_GROSS, I_GROSS,
    J_GROSS, K_GROSS, L_GROSS, M_GROSS, N_GROSS, O_GROSS, P_GROSS, Q_GROSS, R_GROSS,
    S_GROSS, T_GROSS, U_GROSS, V_GROSS, W_GROSS, X_GROSS, Y_GROSS, Z_GROSS,
    AE_GROSS, OE_GROSS, UE_GROSS,
)

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
val WOERTER: List<String> get() = de.korte_daniel.zaubernina.domain.ALLE_PAKET_WOERTER

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
