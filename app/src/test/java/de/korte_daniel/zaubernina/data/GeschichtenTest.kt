package de.korte_daniel.zaubernina.data

import de.korte_daniel.zaubernina.logic.zerlegeSeite
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** Die Bibliothek ist Inhalt für Kinder — hier wird geprüft, dass sie technisch sauber ist. */
class GeschichtenTest {

    @Test
    fun `jede Geschichte hat 12 Seiten, 12 Bilder und die Haltepunkte 4 und 8`() {
        GESCHICHTEN.forEach { g ->
            assertEquals(g.id, 12, g.seiten.size)
            assertEquals(g.id, 12, g.bilder.size)
            assertEquals(g.id, listOf(4, 8), g.haltepunkte)
        }
    }

    @Test
    fun `drei Laengen aus einer Geschichte`() {
        val g = GESCHICHTEN.first()
        assertEquals(4, g.seitenFuer(0).size)
        assertEquals(8, g.seitenFuer(1).size)
        assertEquals(12, g.seitenFuer(2).size)
        assertTrue(g.woerterFuer(0) < g.woerterFuer(1) && g.woerterFuer(1) < g.woerterFuer(2))
    }

    @Test
    fun `kein Wort hat leere Silben oder ASCII-Anfuehrungszeichen`() {
        GESCHICHTEN.forEach { g ->
            g.seiten.forEach { seite ->
                assertFalse(g.id, seite.contains('"'))
                zerlegeSeite(seite).flatten().forEach { wort ->
                    assertTrue("${g.id}: ${wort.text}", wort.silben.all { it.isNotEmpty() })
                }
            }
        }
    }

    @Test
    fun `Saetze bleiben kurz - hoechstens 12 Woerter`() {
        GESCHICHTEN.forEach { g ->
            g.seiten.forEach { seite ->
                seite.replace("|", "").split(Regex("[.!?]\\s")).forEach { satz ->
                    assertTrue("${g.id}: $satz", satz.trim().split(Regex("\\s+")).size <= 12)
                }
            }
        }
    }

    @Test
    fun `jedes Tier hat mindestens zwei Themen und die Suche findet sie`() {
        Tier.entries.forEach { tier ->
            val themen = themenFuer(tier)
            assertTrue(tier.name, themen.size >= 2)
            themen.forEach { assertNotNull(geschichte(tier, it)) }
        }
    }

    @Test
    fun `Kennungen sind eindeutig`() {
        assertEquals(GESCHICHTEN.size, GESCHICHTEN.map { it.id }.toSet().size)
    }
}
