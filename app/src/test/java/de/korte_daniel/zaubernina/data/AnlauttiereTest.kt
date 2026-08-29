package de.korte_daniel.zaubernina.data

import de.korte_daniel.zaubernina.domain.ALLE_PAKET_WOERTER
import de.korte_daniel.zaubernina.data.grundschrift.GRUNDSCHRIFT
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Die Anlauttiere sind von Hand zugeordnet — hier steht, was dabei schiefgehen kann,
 * ohne dass es jemand merkt: ein Tier am falschen Buchstaben, ein doppeltes Bild oder
 * ein Eintrag für ein Zeichen, das es gar nicht gibt.
 */
class AnlauttiereTest {

    @Test
    fun `jedes Tier passt zum Anlaut seines Buchstabens`() {
        for ((zeichen, tier) in ANLAUTTIERE) {
            assertEquals(
                "$zeichen ist mit ${tier.wort} belegt — der erste Buchstabe passt nicht",
                zeichen,
                tier.wort.first().uppercaseChar(),
            )
        }
    }

    @Test
    fun `jedes Tier hat ein eigenes Bild`() {
        val bilder = ANLAUTTIERE.values.map { it.bild }
        assertEquals("Ein Bild ist doppelt vergeben", bilder.size, bilder.toSet().size)
    }

    @Test
    fun `nur gezeichnete Grossbuchstaben haben ein Tier`() {
        for (zeichen in ANLAUTTIERE.keys) {
            assertTrue("$zeichen hat ein Tier, ist aber gar nicht gezeichnet", zeichen in GRUNDSCHRIFT)
        }
    }

    @Test
    fun `X Y und die Umlaute bleiben bewusst ohne Tier`() {
        // Nicht vergessen, sondern entschieden: im Deutschen gibt es dazu kein
        // Anlautwort. Der Test hält die Entscheidung fest, damit sie nicht
        // versehentlich mit einem erfundenen Wort gefüllt wird.
        for (zeichen in listOf('X', 'Y', 'Ä', 'Ö', 'Ü')) {
            assertNull("$zeichen soll kein Tier haben", anlauttier(zeichen))
        }
        assertEquals(24, ANLAUTTIER_ANZAHL)
    }

    @Test
    fun `Ziffern haben kein Tier`() {
        for (ziffer in '0'..'9') assertNull(anlauttier(ziffer))
    }

    @Test
    fun `der gesprochene Satz lautet Buchstabe wie Wort`() {
        assertEquals("N wie Nashorn", anlauttier('N')!!.satz('N'))
    }

    @Test
    fun `die Anfangsbuchstaben der Uebungswoerter haben moeglichst ein Tier`() {
        // Kein hartes Muss — aber wenn ein Paketwort mit einem Buchstaben beginnt, der
        // ein Tier haben KÖNNTE, soll es auch eins haben. Fängt der Test an zu meckern,
        // ist beim Erweitern der Pakete ein Buchstabe ohne Bild dazugekommen.
        val ohne = ALLE_PAKET_WOERTER.map { it.first() }
            .filter { it !in listOf('X', 'Y', 'Ä', 'Ö', 'Ü') }
            .filter { anlauttier(it) == null }
            .toSet()
        assertTrue("Anfangsbuchstaben ohne Tier: $ohne", ohne.isEmpty())
    }
}
