package de.korte_daniel.zaubernina.logic

import org.junit.Assert.assertEquals
import org.junit.Test

class SilbenTest {

    @Test
    fun `Silben werden am Strich getrennt`() {
        val w = zerlegeZeile("Pin|gu|in").single()
        assertEquals(listOf("Pin", "gu", "in"), w.silben)
        assertEquals("Pinguin", w.text)
    }

    @Test
    fun `Satzzeichen bleiben an der letzten Silbe, rein ist ohne sie`() {
        val w = zerlegeZeile("„Halt!“,").single()
        assertEquals("„Halt!“,", w.text)
        assertEquals("Halt", w.rein)
    }

    @Test
    fun `eine Seite zerfaellt in Zeilen und Woerter`() {
        val seite = zerlegeSeite("Pi|a ist klein.\nSie mag das.")
        assertEquals(2, seite.size)
        assertEquals(3, seite[0].size)
        assertEquals("Pia", seite[0][0].rein)
    }

    @Test
    fun `Vorleseminuten runden auf und sind mindestens eins`() {
        assertEquals(1, vorleseMinuten(10))
        assertEquals(1, vorleseMinuten(45))
        assertEquals(2, vorleseMinuten(46))
        assertEquals(7, vorleseMinuten(290))
    }
}
