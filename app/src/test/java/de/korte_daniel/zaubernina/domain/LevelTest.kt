package de.korte_daniel.zaubernina.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Die Regeln der Reise. Sie sind einfach genug, dass man sie für offensichtlich hält —
 * und genau deshalb hatte der erste Entwurf einen Fehler: Er merkte sich "das höchste
 * offene Level" statt "wie viele geschafft sind", und damit ließ sich das LETZTE Level
 * beliebig oft wiederholen und jedes Mal Sterne kassieren.
 */
class LevelTest {

    private val letztes = LEVEL.lastIndex

    @Test
    fun `am Anfang ist nur das erste Level offen`() {
        assertTrue(levelOffen(0, 0))
        assertFalse(levelOffen(1, 0))
        assertFalse(levelIstGeschafft(0, 0))
    }

    @Test
    fun `ein geschafftes Level oeffnet genau das naechste`() {
        val nach = nachLevel(0, 0)
        assertEquals(1, nach)
        assertTrue(levelIstGeschafft(0, nach))
        assertTrue(levelOffen(1, nach))
        assertFalse(levelOffen(2, nach))
    }

    @Test
    fun `ein altes Level noch einmal zu spielen aendert nichts`() {
        val geschafft = 3
        assertEquals(geschafft, nachLevel(0, geschafft))
        assertEquals(geschafft, nachLevel(2, geschafft))
        assertEquals(0, sterneFuer(0, geschafft))
        assertEquals(0, sterneFuer(2, geschafft))
    }

    @Test
    fun `Sterne gibt es nur beim ersten Mal`() {
        assertEquals(3, sterneFuer(0, 0))
        assertEquals(0, sterneFuer(0, 1))
    }

    @Test
    fun `das letzte Level laesst sich nicht melken`() {
        // Der Fehler des ersten Entwurfs: Das letzte Level konnte nichts mehr freischalten,
        // also blieb der gespeicherte Wert stehen — und jede Wiederholung gab wieder Sterne.
        val vorDemLetzten = letztes
        assertEquals(3, sterneFuer(letztes, vorDemLetzten))

        val danach = nachLevel(letztes, vorDemLetzten)
        assertEquals(LEVEL.size, danach)
        assertEquals("Zweiter Durchgang darf nichts mehr geben", 0, sterneFuer(letztes, danach))
        assertEquals(danach, nachLevel(letztes, danach))
    }

    @Test
    fun `sind alle geschafft ist auch alles offen und alles hat einen Stern`() {
        val alle = LEVEL.size
        for (i in LEVEL.indices) {
            assertTrue("Level $i müsste offen sein", levelOffen(i, alle))
            assertTrue("Level $i müsste geschafft sein", levelIstGeschafft(i, alle))
        }
    }

    @Test
    fun `nach dem letzten Level kommt keines mehr`() {
        assertEquals(1, naechstesLevel(0))
        assertNull(naechstesLevel(letztes))
    }

    @Test
    fun `die Reise hat Level ab Nummer eins und keine Luecken`() {
        LEVEL.forEachIndexed { i, level ->
            assertEquals(i + 1, level.nummer)
            assertTrue("Ein Level ohne Wort", level.wort.isNotEmpty())
        }
    }
}

/** Die Regeln der freiwilligen Zwischendurch-Zahlen. */
class ZahlenTest {

    @Test
    fun `eine Zahl geht erst nach ihrem Wort auf`() {
        org.junit.Assert.assertFalse(zahlOffen(0, 0))
        org.junit.Assert.assertTrue(zahlOffen(0, 1))
        org.junit.Assert.assertFalse(zahlOffen(1, 1))
    }

    @Test
    fun `eine Zahl sperrt nichts`() {
        // Daniels Entscheidung: freiwillig. Das nächste Wort hängt NUR am Wort davor —
        // hier steht die Gegenprobe, dass keine Zahlfunktion in levelOffen hineinspielt.
        val geschafft = 2
        org.junit.Assert.assertTrue(levelOffen(2, geschafft))
        org.junit.Assert.assertTrue(zahlOffen(0, geschafft) && zahlOffen(1, geschafft))
    }

    @Test
    fun `der Zusatzstern kommt nur beim ersten Mal`() {
        var zahlen = 0
        org.junit.Assert.assertEquals(1, sterneFuerZahl(zahlen, 0))
        zahlen = mitZahl(zahlen, 0)
        org.junit.Assert.assertEquals(0, sterneFuerZahl(zahlen, 0))
        org.junit.Assert.assertEquals(1, sterneFuerZahl(zahlen, 3))
    }

    @Test
    fun `der Bitsatz merkt sich jede Zahl einzeln`() {
        var zahlen = 0
        zahlen = mitZahl(zahlen, 0)
        zahlen = mitZahl(zahlen, 4)
        org.junit.Assert.assertTrue(zahlIstGeschrieben(zahlen, 0))
        org.junit.Assert.assertFalse(zahlIstGeschrieben(zahlen, 1))
        org.junit.Assert.assertTrue(zahlIstGeschrieben(zahlen, 4))
        // Doppelt setzen ändert nichts.
        org.junit.Assert.assertEquals(zahlen, mitZahl(zahlen, 0))
    }

    @Test
    fun `die Ziffer ist die Levelnummer`() {
        LEVEL.forEachIndexed { i, level ->
            org.junit.Assert.assertEquals(level.nummer, zifferFuer(i))
        }
    }
}
