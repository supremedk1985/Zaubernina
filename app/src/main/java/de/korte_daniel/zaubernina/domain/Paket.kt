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

/**
 * Kurz und mit einfachen Formen. Erweitert am 2026-08-29: NEUE WÖRTER IMMER HINTEN
 * ANHÄNGEN, nie mittendrin einfügen — der Fortschritt merkt sich nur, WIE VIELE Level
 * geschafft sind. Wer mitten in der Liste einfügt, verschiebt einem Kind die Reise.
 */
private val SCHNECKE_WOERTER = listOf(
    "OMA", "OPA", "BALL", "AUTO", "HAUS", "HUND", "KATZE", "SONNE", "MOND", "STERN",
    // die zweite Runde: Tiere und Kurzes, wieder von leicht nach schwerer
    "KUH", "EIS", "BÄR", "ENTE", "MAUS", "IGEL", "BAUM", "EULE", "FISCH", "VOGEL",
    // die dritte Runde: Dinge aus dem Kinderzimmer und dem Garten
    "TÜR", "HAND", "BUCH", "BLUME", "WOLKE", "APFEL", "BANANE",
)

/** Erst die sieben Namen der Familie — das ursprüngliche Spiel —, dann der Rest der Familie. */
private val STANDARD_WOERTER = listOf(
    "NINA", "MAMA", "PAPA", "LEA", "MIRA", "DANIEL", "NATHALIE",
    "OMA", "OPA", "TANTE", "ONKEL", "FAMILIE",
    "BRUDER", "FREUND", "SCHWESTER",
)

/** Lang und mit den schweren Buchstaben (S, G, W, Z …). */
private val FUCHS_WOERTER = listOf(
    "FUCHS", "SCHULE", "FLUGZEUG", "REGENBOGEN", "GEBURTSTAG", "PURZELBAUM",
    "WEIHNACHTEN", "KINDERGARTEN", "ZAUBERNINA", "SCHMETTERLING",
    // die zweite Runde: Tiere, Feste und Zungenbrecher fürs Auge
    "ELEFANT", "PINGUIN", "GIRAFFE", "KROKODIL", "NIKOLAUS", "OSTERHASE",
    "FEUERWEHR", "SPIELPLATZ", "LUFTBALLON", "SCHOKOLADE", "PRINZESSIN", "DINOSAURIER",
    // die dritte Runde: Fahrzeuge, Feste und Fabelwesen
    "BAGGER", "EINHORN", "TRAKTOR", "DRACHEN", "LATERNE", "FAHRRAD",
    "SCHNEEMANN", "MARIENKÄFER", "SEEPFERDCHEN", "HUBSCHRAUBER",
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

/**
 * Wie ein Wort dem Kind gezeigt wird: in Großbuchstaben (NINA) oder normal geschrieben
 * (Nina). Gespeichert und gezählt wird IMMER die Großform — die Kleinschreibung ist
 * reine Darstellung, deshalb bleibt jeder Fortschritt beim Umschalten erhalten.
 */
fun anzeigeWort(wort: String, kleinschreibung: Boolean): String =
    if (kleinschreibung && wort.isNotEmpty()) wort.first() + wort.drop(1).lowercase() else wort

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
