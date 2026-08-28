package de.korte_daniel.zaubernina.domain

/**
 * Ein Level ist ein Wort. Man schreibt es Buchstabe für Buchstabe; ist der letzte fertig,
 * ist das Level geschafft und das nächste geht auf.
 */
data class Level(val nummer: Int, val wort: String)

/**
 * Die Reihenfolge der Reise. Sie ist NICHT nach Schwierigkeit sortiert, sondern danach,
 * was ein Kind schreiben WILL: der eigene Name zuerst, dann Mama und Papa, dann die
 * Geschwister, und erst am Ende die langen Namen.
 *
 * (Nach Schwierigkeit wäre LEA das leichteste — drei Buchstaben, acht Striche — und
 * NATHALIE das schwerste mit acht Buchstaben und zwanzig Strichen.)
 */
val LEVEL: List<Level> = listOf(
    "NINA", "MAMA", "PAPA", "LEA", "MIRA", "DANIEL", "NATHALIE",
).mapIndexed { i, wort -> Level(nummer = i + 1, wort = wort) }

/*
 * DER GANZE FORTSCHRITT IST EINE EINZIGE ZAHL: wie viele Level geschafft sind.
 *
 * Daraus folgt alles andere. Das ist Absicht — der erste Entwurf merkte sich stattdessen
 * "das höchste offene Level", und damit ließ sich das LETZTE Level beliebig oft
 * wiederholen, weil es nichts mehr freischalten konnte und die Sternvergabe daran hing.
 * Mit einer Zählung kann das nicht passieren.
 *
 *   geschafft = 0  ->  nur Level 1 ist offen, keines geschafft
 *   geschafft = 3  ->  Level 1 bis 3 geschafft, Level 4 ist offen, Rest zu
 *   geschafft = 7  ->  alle geschafft (bei sieben Leveln)
 */

/** Darf angetippt werden: alle geschafften und das nächste. */
fun levelOffen(index: Int, geschafft: Int): Boolean = index <= geschafft

/** Trägt einen Stern: schon einmal zu Ende geschrieben. */
fun levelIstGeschafft(index: Int, geschafft: Int): Boolean = index < geschafft

/**
 * Nach einem zu Ende geschriebenen Level. Ein noch einmal gespieltes altes Level ändert
 * nichts — sonst könnte man Sterne sammeln, ohne etwas Neues zu können.
 */
fun nachLevel(index: Int, geschafft: Int): Int =
    if (index == geschafft) (geschafft + 1).coerceAtMost(LEVEL.size) else geschafft

/** Drei Sterne, aber nur beim ersten Mal. */
fun sterneFuer(index: Int, geschafft: Int): Int = if (index == geschafft) 3 else 0

/** Das nächste Level nach diesem, oder null, wenn die Reise zu Ende ist. */
fun naechstesLevel(index: Int): Int? = (index + 1).takeIf { it <= LEVEL.lastIndex }

/*
 * DIE ZAHLEN ZWISCHENDURCH. Nach jedem geschafften Wort liegt die Ziffer des Levels als
 * freiwilliger Zwischenstopp auf dem Weg — sie sperrt NICHTS (Daniels Entscheidung vom
 * 2026-08-28), bringt aber beim ersten Schreiben einen Zusatzstern.
 *
 * Gemerkt wird ein Bitsatz: Bit i gesetzt = die Ziffer nach Level i ist geschrieben.
 * Damit gilt derselbe Melk-Schutz wie bei den Wörtern — beim zweiten Mal gibt es nichts.
 */

/** Eine Zahl ist erreichbar, sobald das Wort davor geschafft ist. */
fun zahlOffen(index: Int, geschafft: Int): Boolean = index < geschafft

fun zahlIstGeschrieben(zahlen: Int, index: Int): Boolean = (zahlen shr index) and 1 == 1

fun mitZahl(zahlen: Int, index: Int): Int = zahlen or (1 shl index)

/** Ein Zusatzstern, aber nur beim ersten Mal. */
fun sterneFuerZahl(zahlen: Int, index: Int): Int = if (zahlIstGeschrieben(zahlen, index)) 0 else 1

/** Die Ziffer, die nach Level [index] geübt wird — schlicht die Levelnummer. */
fun zifferFuer(index: Int): Int = index + 1
