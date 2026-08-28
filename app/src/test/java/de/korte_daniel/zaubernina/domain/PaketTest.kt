package de.korte_daniel.zaubernina.domain

import de.korte_daniel.zaubernina.data.grundschrift.glyph
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PaketTest {

    @Test
    fun `jedes Wort jedes Pakets ist komplett zeichenbar`() {
        for (paket in listOf(Paket.SCHNECKE, Paket.STANDARD, Paket.FUCHS)) {
            for (wort in woerterFuer(paket, emptyList())) {
                for (zeichen in wort) {
                    assertTrue("'$zeichen' aus \"$wort\" (${paket.name}) fehlt", glyph(zeichen) != null)
                }
            }
        }
    }

    @Test
    fun `leere eigene Liste faellt auf die Schnecke zurueck`() {
        // Nie eine leere Reise auf dem Bildschirm.
        assertEquals(woerterFuer(Paket.SCHNECKE, emptyList()), woerterFuer(Paket.EIGENE, emptyList()))
        assertEquals(listOf("OMA"), woerterFuer(Paket.EIGENE, listOf("OMA")))
    }

    @Test
    fun `levelFuer nummeriert ab eins`() {
        val level = levelFuer(listOf("OMA", "OPA"))
        assertEquals(1, level[0].nummer)
        assertEquals("OPA", level[1].wort)
    }

    @Test
    fun `bereinigeWort macht gross und laesst nur Gezeichnetes durch`() {
        val pruefer = { c: Char -> glyph(c) != null }
        assertEquals("OMA", bereinigeWort("  oma ", pruefer))
        assertEquals("BÄR", bereinigeWort("Bär", pruefer))
        assertEquals("STRASSE", bereinigeWort("Straße", pruefer))
        assertNull("Leer geht nicht", bereinigeWort("   ", pruefer))
        assertNull("Ziffern gehören nicht in Wörter? Doch — aber '!' nicht", bereinigeWort("OMA!", pruefer))
        assertNull("Zu lang", bereinigeWort("DONAUDAMPFSCHIFF", pruefer))
    }
}
