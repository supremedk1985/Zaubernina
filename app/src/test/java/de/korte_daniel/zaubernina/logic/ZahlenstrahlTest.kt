package de.korte_daniel.zaubernina.logic

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ZahlenstrahlTest {

    @Test
    fun `Aufgaben werden gelesen, Mengen nicht`() {
        assertEquals(Rechenschritt(3, true, 4), zerlegeRechenaufgabe("3 + 4"))
        assertEquals(Rechenschritt(9, false, 5), zerlegeRechenaufgabe("9 − 5"))
        assertEquals(Rechenschritt(12, false, 7), zerlegeRechenaufgabe("12 - 7"))
        assertNull(zerlegeRechenaufgabe(""))
    }

    @Test
    fun `ohne Zehneruebergang ein Sprung`() {
        assertEquals(listOf(3 to 7), Rechenschritt(3, true, 4).spruenge())
        assertEquals(listOf(9 to 4), Rechenschritt(9, false, 5).spruenge())
        assertEquals(listOf(12 to 17), Rechenschritt(12, true, 5).spruenge())
    }

    @Test
    fun `ueber die Zehn in zwei Spruengen - erst bis zur 10, dann weiter`() {
        assertEquals(listOf(7 to 10, 10 to 12), Rechenschritt(7, true, 5).spruenge())
        assertEquals(listOf(13 to 10, 10 to 6), Rechenschritt(13, false, 7).spruenge())
        assertTrue(Rechenschritt(7, true, 5).hinweis().contains("bis zur 10"))
    }

    @Test
    fun `genau auf die Zehn ist ein Sprung`() {
        assertEquals(listOf(7 to 10), Rechenschritt(7, true, 3).spruenge())
        assertEquals(listOf(10 to 4), Rechenschritt(10, false, 6).spruenge())
    }
}
