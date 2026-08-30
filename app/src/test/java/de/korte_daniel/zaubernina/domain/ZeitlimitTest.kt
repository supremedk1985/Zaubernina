package de.korte_daniel.zaubernina.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ZeitlimitTest {

    @Test
    fun `ohne Limit ist nie Schluss`() {
        assertFalse(zeitVorbei(limitMinuten = 0, heuteSekunden = 0))
        assertFalse(zeitVorbei(limitMinuten = 0, heuteSekunden = 999_999))
    }

    @Test
    fun `Schluss genau ab der letzten Sekunde des Limits`() {
        assertFalse(zeitVorbei(limitMinuten = 10, heuteSekunden = 599))
        assertTrue(zeitVorbei(limitMinuten = 10, heuteSekunden = 600))
        assertTrue(zeitVorbei(limitMinuten = 10, heuteSekunden = 601))
    }

    @Test
    fun `geuebte Minuten runden auf`() {
        assertEquals(0, geuebteMinuten(0))
        assertEquals(1, geuebteMinuten(1))
        assertEquals(1, geuebteMinuten(60))
        assertEquals(2, geuebteMinuten(61))
    }

    @Test
    fun `die Stufen beginnen mit Aus und steigen an`() {
        assertEquals(0, ZEITLIMIT_STUFEN_MINUTEN.first())
        assertEquals(ZEITLIMIT_STUFEN_MINUTEN.sorted(), ZEITLIMIT_STUFEN_MINUTEN)
        assertEquals("Kein Limit", zeitlimitName(0))
        assertEquals("15 Minuten am Tag", zeitlimitName(15))
    }
}
