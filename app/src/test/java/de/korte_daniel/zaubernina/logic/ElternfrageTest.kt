package de.korte_daniel.zaubernina.logic

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class ElternfrageTest {

    @Test
    fun `die Loesung ist immer zweistellig`() {
        // Daran hängt die Bedienung: Die Eingabe prüft sich selbst, sobald zwei Ziffern
        // dastehen. Wäre eine Lösung einstellig oder dreistellig, ginge das schief.
        val zufall = Random(1)
        repeat(3000) {
            val frage = erzeugeElternfrage(zufall)
            assertEquals(
                "${frage.text} = ${frage.loesung} hat nicht $LOESUNG_STELLEN Stellen",
                LOESUNG_STELLEN,
                frage.loesung.toString().length,
            )
        }
    }

    @Test
    fun `keine Aufgabe ist fuer ein Schulkind zu leicht`() {
        // Kein Faktor unter drei, kein Ergebnis unter zwölf — sonst könnte ein Kind, das
        // gerade zählen lernt, zufällig durchkommen.
        val zufall = Random(7)
        repeat(3000) {
            val frage = erzeugeElternfrage(zufall)
            assertTrue("Faktor zu klein: ${frage.text}", frage.a >= 3 && frage.b >= 3)
            assertTrue("Ergebnis zu klein: ${frage.text}", frage.loesung >= 12)
        }
    }

    @Test
    fun `die Pruefung nimmt nur die richtige Zahl`() {
        val frage = Elternfrage(a = 7, b = 8)
        assertEquals(56, frage.loesung)
        assertTrue(frage.stimmt("56"))
        assertFalse(frage.stimmt("57"))
        assertFalse(frage.stimmt(""))
        assertFalse(frage.stimmt("5"))
        assertFalse(frage.stimmt("fünfzigsechs"))
    }

    @Test
    fun `die Aufgabe steht als Text da`() {
        assertEquals("7 × 8", Elternfrage(7, 8).text)
    }

    @Test
    fun `es kommt nicht immer dieselbe Aufgabe`() {
        val zufall = Random(42)
        val gesehen = (1..200).map { erzeugeElternfrage(zufall).text }.toSet()
        assertTrue("Nur ${gesehen.size} verschiedene Aufgaben", gesehen.size > 10)
    }
}
