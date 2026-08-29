package de.korte_daniel.zaubernina.domain

import de.korte_daniel.zaubernina.data.grundschrift.glyph
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AlphabetTest {

    @Test
    fun `das Alphabet ist vollstaendig und chronologisch`() {
        assertEquals(29, ALPHABET.size)
        assertEquals(('A'..'Z').toList(), ALPHABET.take(26))
        assertEquals(listOf('Ä', 'Ö', 'Ü'), ALPHABET.drop(26))
    }

    @Test
    fun `jeder Buchstabe des Alphabets ist gezeichnet`() {
        for (b in ALPHABET) {
            assertNotNull("'$b' fehlt", glyph(b))
        }
    }

    @Test
    fun `fuenf Sterne nur fuer die erste Runde`() {
        assertEquals(5, sterneFuerAlphabetRunde(0))
        assertEquals(0, sterneFuerAlphabetRunde(1))
        assertEquals(0, sterneFuerAlphabetRunde(7))
    }

    @Test
    fun `die Runde ist mit dem letzten Buchstaben voll`() {
        assertFalse(alphabetRundeVoll(0))
        assertFalse(alphabetRundeVoll(ALPHABET.size - 2))
        assertTrue(alphabetRundeVoll(ALPHABET.size - 1))
    }

    @Test
    fun `alphabetBuchstabe klemmt an den Raendern`() {
        assertEquals('A', alphabetBuchstabe(0))
        assertEquals('Ü', alphabetBuchstabe(ALPHABET.size - 1))
        assertEquals('Ü', alphabetBuchstabe(999))
        assertEquals('A', alphabetBuchstabe(-1))
    }
}
