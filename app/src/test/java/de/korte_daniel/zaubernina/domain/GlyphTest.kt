package de.korte_daniel.zaubernina.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.abs

class GlyphTest {

    private val gerade = Stroke(
        start = GlyphPoint(250f, 150f),
        segmente = listOf(StrokeSegment.Linie(GlyphPoint(250f, 850f))),
    )

    /** Zwei Wegstücke sehr unterschiedlicher Länge — hier fällt eine naive Abtastung auf. */
    private val eckeMitBogen = Stroke(
        start = GlyphPoint(100f, 100f),
        segmente = listOf(
            StrokeSegment.Linie(GlyphPoint(100f, 800f)),
            StrokeSegment.Bogen(GlyphPoint(100f, 900f), GlyphPoint(200f, 900f), GlyphPoint(260f, 860f)),
        ),
    )

    @Test
    fun `Abtastung beginnt am Start und endet am Ende`() {
        val punkte = gerade.abtasten(120)
        assertEquals(250f, punkte.first().x, 0.01f)
        assertEquals(150f, punkte.first().y, 0.01f)
        assertEquals(250f, punkte.last().x, 0.01f)
        assertEquals(850f, punkte.last().y, 0.01f)
    }

    @Test
    fun `Abtastung liefert ungefaehr die gewuenschte Punktzahl`() {
        for (gewuenscht in listOf(20, 60, 120, 200)) {
            val n = eckeMitBogen.abtasten(gewuenscht).size
            assertTrue(
                "Bei $gewuenscht angefragten Punkten kamen $n heraus",
                abs(n - gewuenscht) <= 2,
            )
        }
    }

    @Test
    fun `die Abstaende sind ueber Wegstuecke hinweg ungefaehr gleich`() {
        // Das ist der Punkt der anteiligen Verteilung: die Toleranz im StrokeTracker ist ein
        // fester Radius. Wären die Punkte auf dem kurzen Bogen viel dichter als auf der
        // langen Geraden, wäre der Bogen strenger zu treffen als die Gerade.
        val punkte = eckeMitBogen.abtasten(120)
        val abstaende = punkte.zipWithNext { a, b -> abstand(a, b) }
        val mittel = abstaende.average().toFloat()
        val groesster = abstaende.max()
        assertTrue(
            "Größter Abstand $groesster gegen Mittel $mittel",
            groesster < mittel * 2.2f,
        )
    }

    @Test
    fun `ein Bogen ist laenger als seine Sehne`() {
        val bogen = StrokeSegment.Bogen(
            GlyphPoint(300f, 390f), GlyphPoint(560f, 390f), GlyphPoint(560f, 480f),
        )
        val von = GlyphPoint(300f, 480f)
        val laenge = schaetzeLaenge(von, bogen)
        val sehne = abstand(von, bogen.bis)
        assertTrue("Bogen $laenge, Sehne $sehne", laenge > sehne)
    }

    @Test
    fun `Glyph kennt seine Strichzahl`() {
        val n = Glyph(
            zeichen = 'N',
            name = "N",
            striche = listOf(gerade, gerade, gerade),
        )
        assertEquals(3, n.strichzahl)
    }
}
