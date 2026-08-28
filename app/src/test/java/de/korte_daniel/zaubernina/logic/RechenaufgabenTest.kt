package de.korte_daniel.zaubernina.logic

import de.korte_daniel.zaubernina.domain.Klasse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class RechenaufgabenTest {

    private fun viele(klasse: Klasse, n: Int = 2000): List<Rechenaufgabe> {
        val zufall = Random(11)
        return (1..n).map { erzeugeRechenaufgabe(klasse, zufall) }
    }

    @Test
    fun `Vorschule zaehlt Mengen von eins bis sechs`() {
        for (a in viele(Klasse.VORSCHULE)) {
            assertTrue("Menge ${a.menge}", a.menge in 1..6)
            assertEquals(a.menge, a.loesung)
            assertTrue("Anzeige muss leer sein", a.anzeige.isEmpty())
        }
    }

    @Test
    fun `Klasse 1 bleibt im Zehnerraum und nie negativ`() {
        for (a in viele(Klasse.KLASSE_1)) {
            assertTrue("${a.anzeige} = ${a.loesung}", a.loesung in 0..10)
        }
    }

    @Test
    fun `Klasse 2 bleibt im Zwanzigerraum und nie negativ`() {
        for (a in viele(Klasse.KLASSE_2)) {
            assertTrue("${a.anzeige} = ${a.loesung}", a.loesung in 0..20)
        }
    }

    @Test
    fun `die Vorschlaege sind drei verschiedene und enthalten die Loesung`() {
        for (klasse in Klasse.entries) {
            for (a in viele(klasse, 500)) {
                assertEquals(3, a.auswahl.size)
                assertEquals(3, a.auswahl.distinct().size)
                assertTrue(a.loesung in a.auswahl)
                assertTrue("Kein Vorschlag darf negativ sein: ${a.auswahl}", a.auswahl.all { it >= 0 })
            }
        }
    }

    @Test
    fun `die falschen Vorschlaege liegen nah an der Loesung`() {
        // Ein Vorschlag weit weg verlangt keine Denkarbeit — höchstens drei daneben.
        for (a in viele(Klasse.KLASSE_2, 500)) {
            for (v in a.auswahl) {
                assertTrue("$v zu weit weg von ${a.loesung}", kotlin.math.abs(v - a.loesung) <= 3)
            }
        }
    }

    @Test
    fun `die Loesung steht nicht immer an derselben Stelle`() {
        val positionen = viele(Klasse.KLASSE_1, 300).map { it.auswahl.indexOf(it.loesung) }.toSet()
        assertEquals(setOf(0, 1, 2), positionen)
    }
}
