package de.korte_daniel.zaubernina.domain

/**
 * Die Avatare sind gezeichnete Symbole, keine Fotos. Das ist eine bewusste Entscheidung:
 * Die App hat keine Berechtigungen und soll keine bekommen — ein Foto-Avatar brächte
 * Medienzugriff und gespeicherte Kinderbilder mit sich. Ein gemalter Stern tut es auch.
 */
enum class Avatar(val anzeigename: String) {
    STERN("Stern"),
    MOND("Mond"),
    HERZ("Herz"),
    BLUME("Blume"),
    RAKETE("Rakete"),
    WOLKE("Wolke"),
}

/** Ein Kind (oder Erwachsener), das mit der App übt. */
data class Benutzer(
    val id: Int,
    val name: String,
    val avatar: Avatar,
)

/** Wie die Schwierigkeit der Rechenaufgaben heißt, wenn Eltern sie auswählen. */
enum class Klasse(val anzeigename: String, val beschreibung: String) {
    VORSCHULE("Vorschule", "Sterne zählen und die Zahl schreiben"),
    KLASSE_1("1. Klasse", "Plus und minus bis 10"),
    KLASSE_2("2. Klasse", "Plus und minus bis 20"),
}
