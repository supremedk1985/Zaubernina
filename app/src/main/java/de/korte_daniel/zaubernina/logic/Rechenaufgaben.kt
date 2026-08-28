package de.korte_daniel.zaubernina.logic

import de.korte_daniel.zaubernina.domain.Klasse
import kotlin.random.Random

/**
 * Eine Rechenaufgabe für den Rechenmodus. [anzeige] ist, was das Kind sieht;
 * bei der Vorschule ist es leer, dort wird stattdessen eine Sternmenge gezeigt
 * ([menge]) und die Anzahl geschrieben.
 *
 * Beantwortet wird durch SCHREIBEN der Lösung mit der Zauberlinie — das ist der
 * rote Faden der App. Vorher wählt das Kind die Lösung aus drei Vorschlägen,
 * damit auch wirklich gerechnet wird und nicht nur nachgefahren.
 */
data class Rechenaufgabe(
    val anzeige: String,
    val loesung: Int,
    val auswahl: List<Int>,
    val menge: Int? = null,
) {
    init {
        require(loesung in auswahl) { "Die Lösung muss unter den Vorschlägen sein" }
        require(auswahl.size == 3 && auswahl.distinct().size == 3) { "Drei verschiedene Vorschläge" }
    }
}

/**
 * Erzeugt eine Aufgabe passend zur Klasse. Reine Funktion mit einspeisbarem Zufall,
 * damit die Eigenschaften testbar sind (Lösungsbereich, keine negativen Ergebnisse,
 * Vorschläge eindeutig und plausibel).
 */
fun erzeugeRechenaufgabe(klasse: Klasse, zufall: Random = Random.Default): Rechenaufgabe = when (klasse) {
    Klasse.VORSCHULE -> {
        val n = zufall.nextInt(1, 7)
        Rechenaufgabe(anzeige = "", loesung = n, auswahl = mischeAuswahl(n, 1, 6, zufall), menge = n)
    }

    Klasse.KLASSE_1 -> plusMinus(bereich = 10, zufall = zufall)

    Klasse.KLASSE_2 -> plusMinus(bereich = 20, zufall = zufall)
}

private fun plusMinus(bereich: Int, zufall: Random): Rechenaufgabe {
    val plus = zufall.nextBoolean()
    return if (plus) {
        val a = zufall.nextInt(1, bereich)
        val b = zufall.nextInt(1, bereich - a + 1)
        Rechenaufgabe("$a + $b", a + b, mischeAuswahl(a + b, 0, bereich, zufall))
    } else {
        val a = zufall.nextInt(2, bereich + 1)
        val b = zufall.nextInt(1, a)
        Rechenaufgabe("$a − $b", a - b, mischeAuswahl(a - b, 0, bereich, zufall))
    }
}

/**
 * Die Lösung plus zwei nahe, aber falsche Vorschläge — nah, weil ein weit entfernter
 * Vorschlag keine Denkarbeit verlangt. Alle im gültigen Bereich, keine Dubletten.
 */
private fun mischeAuswahl(loesung: Int, kleinstes: Int, groesstes: Int, zufall: Random): List<Int> {
    val falsche = mutableSetOf<Int>()
    while (falsche.size < 2) {
        val abstand = zufall.nextInt(1, 4) * if (zufall.nextBoolean()) 1 else -1
        val kandidat = loesung + abstand
        if (kandidat in kleinstes..groesstes && kandidat != loesung) falsche += kandidat
        // Falls der Bereich zu eng ist (Lösung am Rand), weiter probieren — bei den
        // Bereichen hier gibt es immer mindestens zwei gültige Nachbarn.
    }
    return (falsche + loesung).shuffled(zufall)
}

/** Alle 5 richtigen Aufgaben gibt es einen Stern. */
const val AUFGABEN_JE_STERN = 5
