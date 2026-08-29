package de.korte_daniel.zaubernina.domain

/**
 * Die Wortpakete. Daniels Vorgabe vom 2026-08-28: Standard, Schnecke (leichter),
 * Fuchs (schwerer) und Eigene. Die Schnecke ist langsam und gemütlich, der Fuchs
 * schlau und schnell — daher die Namen.
 */
enum class Paket(val anzeigename: String, val beschreibung: String) {
    SCHNECKE("Schnecke", "Kurze, leichte Wörter zum Anfangen"),
    STANDARD("Standard", "Die Namen der Familie"),
    FUCHS("Fuchs", "Lange Wörter für schlaue Füchse"),
    EIGENE("Eigene", "Deine eigene Wortliste aus dem Elternbereich"),
}

/** Kurz und mit einfachen Formen — viele Geraden, wenige Bögen. */
private val SCHNECKE_WOERTER = listOf(
    "OMA", "OPA", "BALL", "AUTO", "HAUS", "HUND", "KATZE", "SONNE", "MOND", "STERN",
)

/** Die sieben Namen der Familie — das ursprüngliche Spiel. */
private val STANDARD_WOERTER = listOf("NINA", "MAMA", "PAPA", "LEA", "MIRA", "DANIEL", "NATHALIE")

/** Lang und mit den schweren Buchstaben (S, G, W, Z …). */
private val FUCHS_WOERTER = listOf(
    "FUCHS", "SCHULE", "FLUGZEUG", "REGENBOGEN", "GEBURTSTAG", "PURZELBAUM",
    "WEIHNACHTEN", "KINDERGARTEN", "ZAUBERNINA", "SCHMETTERLING",
)

/**
 * Die Wortliste eines Pakets. [eigene] kommt aus dem Elternbereich; ist sie leer, fällt
 * EIGENE auf die Schnecke zurück, damit nie eine leere Reise auf dem Bildschirm steht.
 */
fun woerterFuer(paket: Paket, eigene: List<String>): List<String> = when (paket) {
    Paket.SCHNECKE -> SCHNECKE_WOERTER
    Paket.STANDARD -> STANDARD_WOERTER
    Paket.FUCHS -> FUCHS_WOERTER
    Paket.EIGENE -> eigene.ifEmpty { SCHNECKE_WOERTER }
}

/** Baut aus einer Wortliste die Level der Reise. */
fun levelFuer(woerter: List<String>): List<Level> =
    woerter.mapIndexed { i, wort -> Level(nummer = i + 1, wort = wort) }

/** Alle fest eingebauten Wörter — die Prüfliste, dass jedes Zeichen gezeichnet ist. */
val ALLE_PAKET_WOERTER: List<String> = SCHNECKE_WOERTER + STANDARD_WOERTER + FUCHS_WOERTER

/**
 * Macht aus einer Elterneingabe ein gültiges Übungswort — oder null.
 * Großschreibung erzwungen, nur gezeichnete Zeichen erlaubt, höchstens 12 Zeichen
 * (mehr passt auf keinem Handy in die Kachelzeile).
 */
fun bereinigeWort(eingabe: String, zeichenVorhanden: (Char) -> Boolean): String? {
    val wort = eingabe.trim().uppercase()
        .replace("ß", "SS")
    if (wort.isEmpty() || wort.length > 12) return null
    if (!wort.all { zeichenVorhanden(it) }) return null
    return wort
}
