package de.korte_daniel.zaubernina.domain

/**
 * Die Unterspiele der App — das, was auf der Startseite eines Kindes als Kachel steht.
 * Welche ein Kind sieht, hängt an seiner [Klasse]: Nina (Vorschule) hört Anlaute und zählt,
 * Lea (1./2. Klasse) liest Silben, rechnet und lernt Sprachen. Schreiben und die
 * Vorlesegeschichte gibt es für alle.
 */
enum class Spiel(val symbol: String, val beschreibung: String) {
    SCHREIBEN("✍️", "Buchstaben und Wörter nachfahren"),
    HOEREN("👂", "Womit fängt das Wort an?"),
    RECHNEN("🔢", "Zählen, plus und minus"),
    LESEN("📖", "Silben lesen und das Bild finden"),
    SPRACHEN("🌍", "Englisch und Spanisch"),
    GESCHICHTE("🐧", "Eine Geschichte vorlesen"),
    ;

    fun nameFuer(klasse: Klasse): String = when (this) {
        SCHREIBEN -> "Schreiben"
        HOEREN -> "Hören"
        RECHNEN -> if (klasse == Klasse.VORSCHULE) "Zählen" else "Rechnen"
        LESEN -> "Lesen"
        SPRACHEN -> "Sprachen"
        GESCHICHTE -> "Geschichte"
    }
}

fun spieleFuer(klasse: Klasse): List<Spiel> = when (klasse) {
    Klasse.VORSCHULE -> listOf(Spiel.SCHREIBEN, Spiel.HOEREN, Spiel.RECHNEN, Spiel.GESCHICHTE)
    Klasse.KLASSE_1, Klasse.KLASSE_2 -> listOf(Spiel.SCHREIBEN, Spiel.LESEN, Spiel.RECHNEN, Spiel.SPRACHEN, Spiel.GESCHICHTE)
}

/** Die Fremdsprachen des Sprachen-Spiels. [sprachkennung] ist die BCP-47-Kennung für die Sprachausgabe. */
enum class Fremdsprache(val anzeigename: String, val symbol: String, val sprachkennung: String) {
    ENGLISCH("Englisch", "🇬🇧", "en-GB"),
    SPANISCH("Spanisch", "🇪🇸", "es-ES"),
}
