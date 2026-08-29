package de.korte_daniel.zaubernina.data.grundschrift

import de.korte_daniel.zaubernina.domain.Glyph
import de.korte_daniel.zaubernina.domain.GlyphPoint
import de.korte_daniel.zaubernina.domain.Stroke
import de.korte_daniel.zaubernina.domain.StrokeSegment
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
 *   - Zickzack-Buchstaben (A M N V W) laufen als EIN Zug mit Haltepunkten an den Spitzen
 *     (Bewegungsgruppe V „Zickzacklinie"). A, M und N beginnen dabei UNTEN LINKS mit
 *     einem Aufstrich — so lehren es auch die Schreiblehrgänge (sofatutor, bayerische
 *     Druckschrift): „Du beginnst auf der unteren Schreiblinie und ziehst einen geraden
 *     Strich nach oben." Korrigiert am 2026-08-29 nach Daniels zweitem Einspruch; die
 *     Kartei nennt für M/N auch eine Variante mit Stamm-Abstrich zuerst, die Schule
 *     schreibt sie aber in einem Zug von unten.
 *   - Ovale gegen den Uhrzeigersinn (C O Q G, Ziffer 0); B D P R Bögen im Uhrzeigersinn,
 *     bei R hängen Bogen und Bein in einem Zug
 *   - Mittel-Querstriche (E F H) und die M-Zacke liegen auf der Mittellinie; die
 *     W-Mittelspitze reicht dagegen bis ganz OBEN (Kartei-Zeichnung und sofatutor:
 *     „zwei große V direkt nebeneinander")
 *   - Punkte und Umlautpunkte zuletzt
 *
 * Ziffern nach dem Ziffernschreibkurs (Merksprüche, schulimpulse.de) — alle bestätigt.
 */

private const val OBERLINIE = 150f
private const val MITTELLINIE = 400f
private const val GRUNDLINIE = 850f
private const val UNTERLINIE = 980f

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
 * M: EIN Zug, beginnend UNTEN LINKS — Aufstrich gerade hinauf, Diagonale hinunter bis
 * zur MITTELLINIE, Diagonale hinauf, rechter Stamm gerade herunter. So lehrt es der
 * Schreiblehrgang (sofatutor: „Du beginnst auf der unteren Schreiblinie des
 * Erdgeschosses und ziehst einen geraden Strich nach oben … schräg nach rechts unten
 * bis zur oberen Schreiblinie des Erdgeschosses …"), und die Grundschrift-Kartei nennt
 * genau diesen Anfangspunkt als gleichwertige Variante. Die Zackentiefe Mittellinie
 * steht in Kartei UND Lehrgang („obere Grenze des Mittelbandes").
 */
private val M_GROSS = Glyph(
    zeichen = 'M', name = "M",
    striche = listOf(
        Stroke(
            start = p(230f, GRUNDLINIE),
            segmente = listOf(
                Linie(p(230f, OBERLINIE)),
                Linie(p(500f, MITTELLINIE)),
                Linie(p(770f, OBERLINIE)),
                Linie(p(770f, GRUNDLINIE)),
            ),
            pfeileBei = listOf(0.12f, 0.38f, 0.62f, 0.88f),
        ),
    ),
)

/**
 * N: EIN Zug, beginnend UNTEN LINKS — Aufstrich gerade hinauf, Diagonale hinunter,
 * rechter Stamm als Aufstrich gerade hinauf. So lehrt es der Schreiblehrgang
 * (sofatutor: „Du ziehst einen geraden Strich zur oberen Schreiblinie des Dachs. Von
 * dort malst du einen schrägen Strich nach rechts unten … Zum Schluss zeichnest du
 * wieder einen geraden Strich nach oben."). Der Schluss-Aufstrich steht wörtlich auch
 * in der Grundschrift-Kartei; nur der Anfang ist dort als Abstrich-Variante notiert.
 */
private val N_GROSS = Glyph(
    zeichen = 'N', name = "N",
    striche = listOf(
        Stroke(
            start = p(250f, GRUNDLINIE),
            segmente = listOf(
                Linie(p(250f, OBERLINIE)),
                Linie(p(750f, GRUNDLINIE)),
                Linie(p(750f, OBERLINIE)),
            ),
            pfeileBei = listOf(0.16f, 0.5f, 0.84f),
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
 * W: zweimal hinunter und hinauf — ein Zug, „zwei große V direkt nebeneinander"
 * (sofatutor). Die Mittelspitze reicht bis ganz OBEN zur Oberlinie — so steht das W
 * auch in der Grundschrift-Buchstabentafel (nachgemessen: Mittelspitze auf gleicher
 * Höhe wie die äußeren Spitzen). Die frühere Fassung mit Mittelspitze auf der
 * Mittellinie war eine Fehldeutung des Kartei-Textes zum M.
 */
private val W_GROSS = Glyph(
    zeichen = 'W', name = "W",
    striche = listOf(
        Stroke(
            start = p(180f, OBERLINIE),
            segmente = listOf(
                Linie(p(340f, GRUNDLINIE)),
                Linie(p(500f, OBERLINIE)),
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

// ──────────────────────────── Kleinbuchstaben ────────────────────────────
// Seit 2026-08-29 im Einsatz: in den Einstellungen kann je Kind „Groß und klein"
// gewählt werden, dann werden die Wörter normal geschrieben (Nina statt NINA).
//
// Dieselben Regeln wie bei den Großbuchstaben (sofatutor-Lehrgang): Stämme von
// oben nach unten, Ovale gegen den Uhrzeigersinn, Anbau-Bögen setzen auf halber
// Stammhöhe an (Muster des kleinen n), Punkte zuletzt. Kein Strich läuft über
// sich selbst zurück — wo die Schreibschrift absetzt, sind es hier zwei Züge.
//
// Maße: x-Band 400–850 (Bandmitte 625), Oberlängen bis 150 (t nur bis 270),
// Unterlängen (g j p q y) bis 980, i/j-Punkt und Umlautpunkte um 270–285.

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

/**
 * Der runde Bauch der Ovalbuchstaben (a d g o q ä ö): ein Ring gegen den Uhrzeigersinn,
 * oben beginnend — vier kubische Viertelbögen (Kreisnäherung, Kappa 0,5523).
 */
private const val RING_X = 450f
private const val RING_Y = 625f
private const val RING_R = 225f
private const val RING_GRIFF = RING_R * 0.5523f

private fun ringSegmente(): List<StrokeSegment> = listOf(
    Bogen(p(RING_X - RING_GRIFF, RING_Y - RING_R), p(RING_X - RING_R, RING_Y - RING_GRIFF), p(RING_X - RING_R, RING_Y)),
    Bogen(p(RING_X - RING_R, RING_Y + RING_GRIFF), p(RING_X - RING_GRIFF, RING_Y + RING_R), p(RING_X, RING_Y + RING_R)),
    Bogen(p(RING_X + RING_GRIFF, RING_Y + RING_R), p(RING_X + RING_R, RING_Y + RING_GRIFF), p(RING_X + RING_R, RING_Y)),
    Bogen(p(RING_X + RING_R, RING_Y - RING_GRIFF), p(RING_X + RING_GRIFF, RING_Y - RING_R), p(RING_X, RING_Y - RING_R)),
)

private fun ringZug() = Stroke(
    start = p(RING_X, RING_Y - RING_R),
    segmente = ringSegmente(),
    pfeileBei = listOf(0.16f, 0.62f),
)

/** Kleines a: erst der runde Bauch gegen den Uhrzeigersinn ab zwölf Uhr, dann der Strich rechts. */
private val A_KLEIN = Glyph(
    zeichen = 'a', name = "a",
    striche = listOf(
        ringZug(),
        zug(p(RING_X + RING_R, MITTELLINIE), p(RING_X + RING_R, GRUNDLINIE), EIN_PFEIL),
    ),
)

/** Kleines b: langer Stamm, dann der Bauch — er setzt wie beim n auf halber Höhe an. */
private val B_KLEIN = Glyph(
    zeichen = 'b', name = "b",
    striche = listOf(
        zug(p(330f, OBERLINIE), p(330f, GRUNDLINIE)),
        Stroke(
            start = p(330f, 455f),
            segmente = listOf(
                Bogen(p(545f, 405f), p(650f, 490f), p(650f, 625f)),
                Bogen(p(650f, 760f), p(545f, 845f), p(330f, 825f)),
            ),
            pfeileBei = listOf(0.3f, 0.75f),
        ),
    ),
)

/** Kleines c: der offene Bogen gegen den Uhrzeigersinn, wie das große C eine Etage tiefer. */
private val C_KLEIN = Glyph(
    zeichen = 'c', name = "c",
    striche = listOf(
        Stroke(
            start = p(620f, 462f),
            segmente = listOf(
                Bogen(p(555f, 412f), p(505f, 400f), p(450f, 400f)),
                Bogen(p(326f, 400f), p(225f, 501f), p(225f, 625f)),
                Bogen(p(225f, 749f), p(326f, 850f), p(450f, 850f)),
                Bogen(p(505f, 850f), p(556f, 838f), p(618f, 792f)),
            ),
            pfeileBei = listOf(0.3f, 0.7f),
        ),
    ),
)

/** Kleines d: erst der Bauch, dann der lange Stamm von ganz oben. */
private val D_KLEIN = Glyph(
    zeichen = 'd', name = "d",
    striche = listOf(
        ringZug(),
        zug(p(RING_X + RING_R, OBERLINIE), p(RING_X + RING_R, GRUNDLINIE)),
    ),
)

/** Kleines e: der Querstrich, dann ohne Absetzen im Bogen herum — ein Zug. */
private val E_KLEIN = Glyph(
    zeichen = 'e', name = "e",
    striche = listOf(
        Stroke(
            start = p(250f, 608f),
            segmente = listOf(
                Linie(p(648f, 608f)),
                Bogen(p(648f, 480f), p(570f, 400f), p(450f, 400f)),
                Bogen(p(326f, 400f), p(225f, 501f), p(225f, 625f)),
                Bogen(p(225f, 749f), p(326f, 850f), p(450f, 850f)),
                Bogen(p(505f, 850f), p(560f, 836f), p(618f, 795f)),
            ),
            pfeileBei = listOf(0.14f, 0.5f, 0.85f),
        ),
    ),
)

/** Kleines f: oben der Haken nach links, der Stamm hinunter, dann der Querstrich. */
private val F_KLEIN = Glyph(
    zeichen = 'f', name = "f",
    striche = listOf(
        Stroke(
            start = p(600f, 235f),
            segmente = listOf(
                Bogen(p(545f, 168f), p(470f, OBERLINIE), p(430f, OBERLINIE)),
                Bogen(p(365f, OBERLINIE), p(330f, 205f), p(330f, 270f)),
                Linie(p(330f, GRUNDLINIE)),
            ),
            pfeileBei = listOf(0.2f, 0.7f),
        ),
        zug(p(210f, MITTELLINIE), p(480f, MITTELLINIE), EIN_PFEIL),
    ),
)

/** Kleines g: der Bauch, dann der Strich in den Keller mit dem Bogen nach links. */
private val G_KLEIN = Glyph(
    zeichen = 'g', name = "g",
    striche = listOf(
        ringZug(),
        Stroke(
            start = p(RING_X + RING_R, MITTELLINIE),
            segmente = listOf(
                Linie(p(675f, 820f)),
                Bogen(p(675f, 930f), p(590f, 980f), p(530f, 980f)),
                Bogen(p(465f, 980f), p(428f, 948f), p(425f, 905f)),
            ),
            pfeileBei = listOf(0.3f, 0.8f),
        ),
    ),
)

/** Kleines h: wie das n, nur mit langem Stamm. */
private val H_KLEIN = Glyph(
    zeichen = 'h', name = "h",
    striche = listOf(
        zug(p(330f, OBERLINIE), p(330f, GRUNDLINIE)),
        Stroke(
            start = p(330f, 480f),
            segmente = listOf(
                Bogen(p(330f, 395f), p(610f, 395f), p(610f, 480f)),
                Linie(p(610f, GRUNDLINIE)),
            ),
            pfeileBei = listOf(0.30f, 0.78f),
        ),
    ),
)

/** Kleines j: Strich in den Keller mit Linksbogen, der Punkt zuletzt. */
private val J_KLEIN = Glyph(
    zeichen = 'j', name = "j",
    striche = listOf(
        Stroke(
            start = p(500f, MITTELLINIE),
            segmente = listOf(
                Linie(p(500f, 860f)),
                Bogen(p(500f, 950f), p(430f, 982f), p(380f, 975f)),
                Bogen(p(340f, 968f), p(315f, 945f), p(310f, 915f)),
            ),
            pfeileBei = listOf(0.35f, 0.8f),
        ),
        Stroke(p(500f, 270f), listOf(Linie(p(500f, 276f))), emptyList(), tupfer = true),
    ),
)

/** Kleines k: langer Stamm, dann beide Arme in einem Zug mit Knick am Stamm. */
private val K_KLEIN = Glyph(
    zeichen = 'k', name = "k",
    striche = listOf(
        zug(p(330f, OBERLINIE), p(330f, GRUNDLINIE)),
        Stroke(
            start = p(620f, MITTELLINIE),
            segmente = listOf(Linie(p(345f, 625f)), Linie(p(620f, GRUNDLINIE))),
            pfeileBei = listOf(0.22f, 0.78f),
        ),
    ),
)

/** Kleines l: ein einziger langer Abstrich. */
private val L_KLEIN = Glyph(
    zeichen = 'l', name = "l",
    striche = listOf(zug(p(500f, OBERLINIE), p(500f, GRUNDLINIE))),
)

/** Kleines m: der Stamm, dann zwei Bögen — jeder setzt auf halber Höhe des vorigen an. */
private val M_KLEIN = Glyph(
    zeichen = 'm', name = "m",
    striche = listOf(
        zug(p(270f, MITTELLINIE), p(270f, GRUNDLINIE), EIN_PFEIL),
        Stroke(
            start = p(270f, 480f),
            segmente = listOf(
                Bogen(p(270f, 395f), p(475f, 395f), p(475f, 480f)),
                Linie(p(475f, GRUNDLINIE)),
            ),
            pfeileBei = listOf(0.30f, 0.8f),
        ),
        Stroke(
            start = p(475f, 480f),
            segmente = listOf(
                Bogen(p(475f, 395f), p(680f, 395f), p(680f, 480f)),
                Linie(p(680f, GRUNDLINIE)),
            ),
            pfeileBei = listOf(0.30f, 0.8f),
        ),
    ),
)

/** Kleines o: der Ring gegen den Uhrzeigersinn, oben beginnend. */
private val O_KLEIN = Glyph(
    zeichen = 'o', name = "o",
    striche = listOf(ringZug()),
)

/** Kleines p: Stamm in den Keller, dann der Bauch wie beim b. */
private val P_KLEIN = Glyph(
    zeichen = 'p', name = "p",
    striche = listOf(
        zug(p(330f, MITTELLINIE), p(330f, UNTERLINIE)),
        Stroke(
            start = p(330f, 455f),
            segmente = listOf(
                Bogen(p(545f, 405f), p(650f, 490f), p(650f, 625f)),
                Bogen(p(650f, 760f), p(545f, 845f), p(330f, 825f)),
            ),
            pfeileBei = listOf(0.3f, 0.75f),
        ),
    ),
)

/** Kleines q: der Bauch, dann der gerade Strich in den Keller. */
private val Q_KLEIN = Glyph(
    zeichen = 'q', name = "q",
    striche = listOf(
        ringZug(),
        zug(p(RING_X + RING_R, MITTELLINIE), p(RING_X + RING_R, UNTERLINIE)),
    ),
)

/** Kleines r: kurzer Stamm, dann der kleine Bogen — offen, ohne Abstrich. */
private val R_KLEIN = Glyph(
    zeichen = 'r', name = "r",
    striche = listOf(
        zug(p(350f, MITTELLINIE), p(350f, GRUNDLINIE), EIN_PFEIL),
        Stroke(
            start = p(350f, 480f),
            segmente = listOf(Bogen(p(350f, 395f), p(575f, 395f), p(590f, 470f))),
            pfeileBei = EIN_PFEIL,
        ),
    ),
)

/** Kleines s: die S-Kurve des großen S, eine Etage tiefer und entsprechend kleiner. */
private val S_KLEIN = Glyph(
    zeichen = 's', name = "s",
    striche = listOf(
        Stroke(
            start = p(622f, 468f),
            segmente = listOf(
                Bogen(p(603f, 416f), p(539f, 399f), p(484f, 400f)),
                Bogen(p(407f, 403f), p(378f, 458f), p(391f, 516f)),
                Bogen(p(404f, 580f), p(481f, 606f), p(526f, 625f)),
                Bogen(p(577f, 648f), p(622f, 689f), p(622f, 747f)),
                Bogen(p(622f, 821f), p(539f, 858f), p(461f, GRUNDLINIE)),
                Bogen(p(413f, 846f), p(384f, 815f), p(378f, 776f)),
            ),
            pfeileBei = listOf(0.14f, 0.62f),
        ),
    ),
)

/** Kleines t: der Stamm beginnt über der Mittellinie, aber unter der Oberlinie; dann quer. */
private val T_KLEIN = Glyph(
    zeichen = 't', name = "t",
    striche = listOf(
        zug(p(450f, 270f), p(450f, GRUNDLINIE), EIN_PFEIL),
        zug(p(320f, MITTELLINIE), p(580f, MITTELLINIE), EIN_PFEIL),
    ),
)

/** Kleines u: hinunter, im Bogen hinauf bis zur Mittellinie, dann der Abstrich rechts. */
private val U_KLEIN = Glyph(
    zeichen = 'u', name = "u",
    striche = listOf(
        Stroke(
            start = p(330f, MITTELLINIE),
            segmente = listOf(
                Linie(p(330f, 690f)),
                Bogen(p(330f, 795f), p(415f, 850f), p(470f, 850f)),
                Bogen(p(545f, 850f), p(610f, 790f), p(610f, 700f)),
                Linie(p(610f, MITTELLINIE)),
            ),
            pfeileBei = listOf(0.2f, 0.62f),
        ),
        zug(p(610f, MITTELLINIE), p(610f, GRUNDLINIE), EIN_PFEIL),
    ),
)

/** Kleines v: das Zickzack des großen V, eine Etage tiefer. */
private val V_KLEIN = Glyph(
    zeichen = 'v', name = "v",
    striche = listOf(
        Stroke(
            start = p(330f, MITTELLINIE),
            segmente = listOf(Linie(p(500f, GRUNDLINIE)), Linie(p(670f, MITTELLINIE))),
            pfeileBei = listOf(0.25f, 0.75f),
        ),
    ),
)

/** Kleines w: zweimal hinunter und hinauf, die Mittelspitze bis zur Mittellinie. */
private val W_KLEIN = Glyph(
    zeichen = 'w', name = "w",
    striche = listOf(
        Stroke(
            start = p(280f, MITTELLINIE),
            segmente = listOf(
                Linie(p(390f, GRUNDLINIE)),
                Linie(p(500f, MITTELLINIE)),
                Linie(p(610f, GRUNDLINIE)),
                Linie(p(720f, MITTELLINIE)),
            ),
            pfeileBei = listOf(0.14f, 0.62f),
        ),
    ),
)

/** Kleines x: zwei gekreuzte Abstriche, beide von oben. */
private val X_KLEIN = Glyph(
    zeichen = 'x', name = "x",
    striche = listOf(
        zug(p(330f, MITTELLINIE), p(670f, GRUNDLINIE), EIN_PFEIL),
        zug(p(670f, MITTELLINIE), p(330f, GRUNDLINIE), EIN_PFEIL),
    ),
)

/** Kleines y: wie das v, aber der zweite Abstrich läuft durch bis in den Keller. */
private val Y_KLEIN = Glyph(
    zeichen = 'y', name = "y",
    striche = listOf(
        zug(p(330f, MITTELLINIE), p(505f, GRUNDLINIE), EIN_PFEIL),
        zug(p(680f, MITTELLINIE), p(455f, UNTERLINIE), EIN_PFEIL),
    ),
)

/** Kleines z: hin, schräg hinunter, wieder hin — ein Zug. */
private val Z_KLEIN = Glyph(
    zeichen = 'z', name = "z",
    striche = listOf(
        Stroke(
            start = p(330f, MITTELLINIE),
            segmente = listOf(Linie(p(660f, MITTELLINIE)), Linie(p(330f, GRUNDLINIE)), Linie(p(660f, GRUNDLINIE))),
            pfeileBei = listOf(0.15f, 0.5f, 0.85f),
        ),
    ),
)

/** Die Umlautpunkte der Kleinbuchstaben — tiefer als bei den Großen, über dem x-Band. */
private fun kleineUmlautpunkte(): List<Stroke> = listOf(
    Stroke(p(365f, 275f), listOf(Linie(p(365f, 281f))), emptyList(), tupfer = true),
    Stroke(p(585f, 275f), listOf(Linie(p(585f, 281f))), emptyList(), tupfer = true),
)

private val AE_KLEIN = Glyph('ä', "ä", A_KLEIN.striche + kleineUmlautpunkte())
private val OE_KLEIN = Glyph('ö', "ö", O_KLEIN.striche + kleineUmlautpunkte())
private val UE_KLEIN = Glyph('ü', "ü", U_KLEIN.striche + kleineUmlautpunkte())



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

val KLEINBUCHSTABEN: List<Glyph> = listOf(
    A_KLEIN, B_KLEIN, C_KLEIN, D_KLEIN, E_KLEIN, F_KLEIN, G_KLEIN, H_KLEIN, I_KLEIN,
    J_KLEIN, K_KLEIN, L_KLEIN, M_KLEIN, N_KLEIN, O_KLEIN, P_KLEIN, Q_KLEIN, R_KLEIN,
    S_KLEIN, T_KLEIN, U_KLEIN, V_KLEIN, W_KLEIN, X_KLEIN, Y_KLEIN, Z_KLEIN,
    AE_KLEIN, OE_KLEIN, UE_KLEIN,
)

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
