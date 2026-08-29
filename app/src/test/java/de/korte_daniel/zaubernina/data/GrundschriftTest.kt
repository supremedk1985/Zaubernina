package de.korte_daniel.zaubernina.data

import de.korte_daniel.zaubernina.data.grundschrift.GRUNDSCHRIFT
import de.korte_daniel.zaubernina.data.grundschrift.WOERTER
import de.korte_daniel.zaubernina.data.grundschrift.glyph
import de.korte_daniel.zaubernina.domain.BOX
import de.korte_daniel.zaubernina.domain.abstand
import de.korte_daniel.zaubernina.logic.Genauigkeit
import de.korte_daniel.zaubernina.logic.StrokeTracker
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Die Zeichendaten sind von Hand geschrieben — hier wird geprüft, was daran schiefgehen
 * kann, ohne dass es jemand merkt. Ein fehlender Buchstabe zum Beispiel führt nicht zu
 * einem Absturz: der Bildschirm bleibt einfach leer.
 */
class GrundschriftTest {

    @Test
    fun `alle zehn Ziffern sind gezeichnet`() {
        for (z in '0'..'9') {
            assertNotNull("Ziffer '$z' ist nicht gezeichnet", glyph(z))
        }
    }

    @Test
    fun `das komplette grosse Alphabet ist gezeichnet`() {
        for (b in 'A'..'Z') {
            assertNotNull("Buchstabe '$b' ist nicht gezeichnet", glyph(b))
        }
        for (b in "ÄÖÜ") {
            assertNotNull("Umlaut '$b' ist nicht gezeichnet", glyph(b))
        }
    }

    @Test
    fun `auch Ziffern stehen zwischen Oberlinie und Grundlinie`() {
        for (z in '0'..'9') {
            val alle = glyph(z)!!.striche.flatMap { it.abtasten(60) }
            assertTrue("$z beginnt bei ${alle.minOf { it.y }}", alle.minOf { it.y } in 130f..165f)
            assertTrue("$z endet bei ${alle.maxOf { it.y }}", alle.maxOf { it.y } in 835f..890f)
        }
    }

    @Test
    fun `jeder Buchstabe jedes Uebungswortes ist gezeichnet`() {
        for (wort in WOERTER) {
            for (zeichen in wort) {
                assertNotNull("'$zeichen' aus \"$wort\" ist nicht gezeichnet", glyph(zeichen))
            }
        }
    }

    @Test
    fun `kein Strich ist zu kurz zum Nachfahren`() {
        for ((zeichen, glyphe) in GRUNDSCHRIFT) {
            glyphe.striche.forEachIndexed { i, strich ->
                val punkte = strich.abtasten(120)
                assertTrue("$zeichen, Strich ${i + 1}: zu wenige Stützpunkte", punkte.size >= 2)
                val laenge = punkte.zipWithNext { a, b -> abstand(a, b) }.sum()
                // Ein Tupfer ist absichtlich winzig, alles andere muss eine Strecke sein.
                val mindestens = if (strich.tupfer) 1f else 150f
                assertTrue(
                    "$zeichen, Strich ${i + 1}: nur $laenge lang",
                    laenge >= mindestens,
                )
            }
        }
    }

    @Test
    fun `alle Punkte liegen in der Zeichenbox`() {
        // Fängt Zahlendreher in den Koordinaten: ein Buchstabe, der aus dem Bild läuft.
        for ((zeichen, glyphe) in GRUNDSCHRIFT) {
            for (strich in glyphe.striche) {
                for (punkt in strich.abtasten(60)) {
                    assertTrue(
                        "$zeichen: Punkt (${punkt.x}, ${punkt.y}) liegt außerhalb der Box",
                        punkt.x in -1f..(BOX + 1f) && punkt.y in -1f..(BOX + 1f),
                    )
                }
            }
        }
    }

    @Test
    fun `Grossbuchstaben stehen zwischen Oberlinie und Grundlinie`() {
        val grosse = GRUNDSCHRIFT.filterKeys { it.isUpperCase() }
        assertTrue("Es sollten Großbuchstaben da sein", grosse.isNotEmpty())
        for ((zeichen, glyphe) in grosse) {
            val alle = glyphe.striche.flatMap { it.abtasten(60) }
            val oben = alle.minOf { it.y }
            val unten = alle.maxOf { it.y }
            if (zeichen == 'J') {
                // Das große J ist laut Grundschrift-Kartei der einzige Großbuchstabe mit
                // Unterlänge — es endet im Unterlängenband, nicht auf der Grundlinie.
                assertTrue("J beginnt bei $oben", oben in 145f..155f)
                assertTrue("J muss unter die Grundlinie reichen, endet bei $unten", unten in 860f..990f)
                continue
            }
            if (zeichen in "ÄÖÜ") {
                // Die Umlautpunkte liegen ÜBER der Oberlinie — der Grundbuchstabe selbst
                // wird über die Nicht-Tupfer-Striche geprüft.
                val koerper = glyphe.striche.filterNot { it.tupfer }.flatMap { it.abtasten(60) }
                assertTrue("$zeichen: Punkte fehlen oben", oben < 120f)
                assertTrue("$zeichen beginnt bei ${koerper.minOf { it.y }}", koerper.minOf { it.y } in 145f..155f)
            } else {
                assertTrue("$zeichen beginnt bei $oben statt an der Oberlinie 150", oben in 145f..155f)
            }
            assertTrue("$zeichen endet bei $unten statt an der Grundlinie 850", unten in 840f..860f)
        }
    }

    @Test
    fun `jeder Strich laesst sich sauber nachfahren`() {
        // Ein Finger, der die Linie exakt entlangfährt, muss JEDEN Strich fertig
        // schreiben können. Das klingt selbstverständlich, ist es aber nicht: die
        // Zickzack-Buchstaben (A M N V W) kehren an ihren Spitzen scharf um, und der
        // Tracker darf an so einer Kehre weder hängen bleiben noch vorzeitig auf den
        // Rückweg springen. Genau dafür ist dieser Test da — er hätte auch jeden
        // Zeichenfehler gefangen, bei dem ein Strich sich selbst überlappt.
        for ((zeichen, glyphe) in GRUNDSCHRIFT) {
            glyphe.striche.forEachIndexed { i, strich ->
                val punkte = strich.abtasten(120)
                val tracker = StrokeTracker(punkte, toleranz = Genauigkeit.NORMAL.toleranz, tupfer = strich.tupfer)
                tracker.senke(punkte.first())
                for (p in punkte) tracker.ziehe(p)
                assertTrue("$zeichen, Strich ${i + 1}: sauberes Nachfahren wird nicht fertig", tracker.fertig)
            }
        }
    }

    @Test
    fun `jeder Strich beginnt dort wo das Kind ihn beginnen soll`() {
        // Der erste Stützpunkt MUSS der angegebene Startpunkt sein — daran hängt der
        // Startring auf dem Bildschirm und die Startprüfung im StrokeTracker.
        for ((zeichen, glyphe) in GRUNDSCHRIFT) {
            glyphe.striche.forEachIndexed { i, strich ->
                val erster = strich.abtasten(30).first()
                assertTrue(
                    "$zeichen, Strich ${i + 1}: Abtastung beginnt nicht am Startpunkt",
                    abstand(erster, strich.start) < 0.01f,
                )
            }
        }
    }
}
