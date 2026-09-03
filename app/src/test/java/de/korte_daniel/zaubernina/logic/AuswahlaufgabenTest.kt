package de.korte_daniel.zaubernina.logic

import de.korte_daniel.zaubernina.data.WORTTHEMEN
import de.korte_daniel.zaubernina.domain.Fremdsprache
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class AuswahlaufgabenTest {

    private val zufall = Random(7)

    @Test
    fun `Anlaute - drei verschiedene Tiere, das richtige dabei, Stufe 0 ohne aehnliche Laute`() {
        val q = AnlautQuelle()
        repeat(200) {
            val a = q.naechste(0, zufall)
            assertEquals(3, a.antworten.size)
            assertTrue(a.antworten.any { it.kennung == a.richtig })
            assertTrue(a.antworten.all { it.bild != 0 })
            val ziel = a.richtig.single()
            a.antworten.filter { it.kennung != a.richtig }.forEach { falsch ->
                val b = falsch.kennung.single()
                assertFalse("$ziel mit $b", setOf(ziel, b) in listOf(setOf('M', 'N'), setOf('B', 'P'), setOf('B', 'D'), setOf('P', 'D'), setOf('G', 'K')))
            }
            assertTrue(a.ansage.contains(ziel))
            assertTrue(a.loesung.contains(ziel))
        }
    }

    @Test
    fun `Lesen - Stufe 0 zeigt Silben, Stufe 1 nicht, Stufe 2 einen Satz`() {
        val q = LeseQuelle()
        val s0 = (1..30).map { q.naechste(0, zufall) }
        assertTrue(s0.any { it.frageText.contains('|') })
        val s1 = (1..30).map { q.naechste(1, zufall) }
        assertTrue(s1.none { it.frageText.contains('|') })
        val s2 = (1..30).map { q.naechste(2, zufall) }
        assertTrue(s2.all { it.frageText.endsWith(".") && it.frageText.contains(' ') })
    }

    @Test
    fun `Lesen - falsche Bilder haben andere Anfangsbuchstaben`() {
        val q = LeseQuelle()
        repeat(200) {
            val a = q.naechste(0, zufall)
            val erster = a.richtig.first()
            a.antworten.filter { it.kennung != a.richtig }.forEach { assertFalse(it.kennung.first() == erster) }
        }
    }

    @Test
    fun `Sprachen - Ansage in der Fremdsprache, Antworten aus dem Wortfeld`() {
        val tiere = WORTTHEMEN.first { it.kennung == "TIERE" }
        val q = SprachenQuelle(Fremdsprache.SPANISCH, tiere)
        repeat(50) {
            val a = q.naechste(0, zufall)
            assertEquals("es-ES", a.ansageSprache)
            assertEquals("🔊", a.frageSymbol)
            assertTrue(a.antworten.all { ant -> tiere.woerter.any { it.de == ant.kennung } })
            val wort = tiere.woerter.first { it.de == a.richtig }
            assertEquals(wort.es, a.ansage)
        }
        val satz = q.naechste(2, zufall)
        assertTrue(satz.ansage.startsWith("Veo un "))
        val farben = SprachenQuelle(Fremdsprache.ENGLISCH, WORTTHEMEN.first { it.kennung == "FARBEN" }).naechste(1, zufall)
        assertTrue(farben.antworten.all { it.farbe.startsWith("#") })
        assertTrue(farben.frageText.isNotEmpty())
    }

    @Test
    fun `Anpassung - drei richtige hoch, zwei Fehler runter, nie unter 0 oder ueber 2`() {
        val a = Anpassung()
        assertEquals(0, a.stufe)
        a.zweiterFehler(); assertEquals(0, a.stufe)
        repeat(3) { a.richtig() }; assertEquals(1, a.stufe)
        repeat(6) { a.richtig() }; assertEquals(2, a.stufe)
        a.zweiterFehler(); assertEquals(1, a.stufe)
    }

    @Test
    fun `Akkusativ nach Artikel`() {
        val hund = WORTTHEMEN.first { it.kennung == "TIERE" }.woerter.first { it.de == "Hund" }
        assertEquals("einen Hund", hund.akkusativ)
        val katze = WORTTHEMEN.first { it.kennung == "TIERE" }.woerter.first { it.de == "Katze" }
        assertEquals("eine Katze", katze.akkusativ)
    }
}
