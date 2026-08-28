package de.korte_daniel.zaubernina.logic

import kotlin.random.Random

/**
 * Die Rechenaufgabe vor dem Elternbereich.
 *
 * Sie ist kein Passwort und soll keines sein — sie ist eine Hürde, die ein Kind im
 * Vorschul- oder ersten Schuljahr nicht nimmt, ein Erwachsener aber im Vorbeigehen. Ein
 * Kleines Einmaleins über der Fünferreihe reicht dafür vollkommen.
 *
 * Die Grenzen sind mit Absicht so gewählt, dass die Lösung IMMER zweistellig ist
 * (kleinste 3 × 4 = 12, größte 9 × 9 = 81). Dadurch weiß die Eingabe, wann sie fertig
 * ist, ohne einen Bestätigungsknopf zu brauchen.
 */
data class Elternfrage(val a: Int, val b: Int) {
    val loesung: Int get() = a * b
    val text: String get() = "$a × $b"

    fun stimmt(eingabe: String): Boolean = eingabe.toIntOrNull() == loesung
}

private const val KLEINSTER_FAKTOR_A = 3
private const val KLEINSTER_FAKTOR_B = 4
private const val GROESSTER_FAKTOR = 9

fun erzeugeElternfrage(zufall: Random = Random.Default): Elternfrage = Elternfrage(
    a = zufall.nextInt(KLEINSTER_FAKTOR_A, GROESSTER_FAKTOR + 1),
    b = zufall.nextInt(KLEINSTER_FAKTOR_B, GROESSTER_FAKTOR + 1),
)

/** Wie viele Ziffern die Lösung hat — die Eingabe prüft sich selbst, sobald sie so lang ist. */
const val LOESUNG_STELLEN = 2
