package de.korte_daniel.zaubernina.logic

import de.korte_daniel.zaubernina.domain.GlyphPoint
import de.korte_daniel.zaubernina.domain.Stroke
import de.korte_daniel.zaubernina.domain.StrokeSegment
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class StrokeTrackerTest {

    /** Der linke Stamm des großen N: senkrecht von der Oberlinie zur Grundlinie. */
    private val geraderStrich = Stroke(
        start = GlyphPoint(250f, 150f),
        segmente = listOf(StrokeSegment.Linie(GlyphPoint(250f, 850f))),
    )

    /** Der Bogen des kleinen n — für die Frage, ob eine Abkürzung durchgeht. */
    private val bogen = Stroke(
        start = GlyphPoint(300f, 480f),
        segmente = listOf(
            StrokeSegment.Bogen(GlyphPoint(300f, 390f), GlyphPoint(560f, 390f), GlyphPoint(560f, 480f)),
        ),
    )

    private fun tracker(strich: Stroke = geraderStrich, genauigkeit: Genauigkeit = Genauigkeit.NORMAL) =
        StrokeTracker(strich.abtasten(120), toleranz = genauigkeit.toleranz)

    /** Fährt den Strich in [schritte] Etappen sauber nach. */
    private fun fahreNach(t: StrokeTracker, punkte: List<GlyphPoint>, schritte: Int = 60) {
        t.senke(punkte.first())
        for (i in 1..schritte) {
            t.ziehe(punkte[(punkte.lastIndex * i / schritte)])
        }
    }

    @Test
    fun `nur der Startpunkt beginnt den Strich`() {
        val t = tracker()
        assertEquals(Zug.WARTET, t.senke(GlyphPoint(700f, 700f)))
        assertEquals(-1, t.fortschritt)

        assertEquals(Zug.LAEUFT, t.senke(GlyphPoint(250f, 150f)))
        assertEquals(0, t.fortschritt)
    }

    @Test
    fun `ein sauber nachgefahrener Strich wird fertig`() {
        val punkte = geraderStrich.abtasten(120)
        val t = tracker()
        fahreNach(t, punkte)
        assertTrue(t.fertig)
        assertEquals(1f, t.anteil, 0.001f)
    }

    @Test
    fun `ein stillstehender Finger schreibt nichts`() {
        val t = tracker()
        t.senke(GlyphPoint(250f, 150f))
        // Zwanzigmal am selben Fleck wackeln, immer innerhalb der Toleranz.
        repeat(20) { t.ziehe(GlyphPoint(250f + (it % 3), 150f + (it % 2))) }
        assertFalse(t.fertig)
        assertTrue("Wackeln darf kaum Fortschritt bringen, war: ${t.anteil}", t.anteil < 0.05f)
    }

    @Test
    fun `rueckwaerts zaehlt nicht`() {
        val punkte = geraderStrich.abtasten(120)
        val t = tracker()
        t.senke(punkte.first())
        // Schrittweise bis etwa zur Mitte — nicht in einem Sprung, siehe der Test darunter.
        for (i in 1..30) t.ziehe(punkte[i * 2])
        val erreicht = t.fortschritt
        assertTrue("Vorbedingung: die Mitte muss erreicht sein, war $erreicht", erreicht > 40)

        t.ziehe(punkte[20])
        assertEquals("Der Fortschritt darf nicht zurückgehen", erreicht, t.fortschritt)
    }

    @Test
    fun `ein Sprung weiter als die Vorausschau wird nicht anerkannt`() {
        // Das ist die Bremse gegen Tippen statt Nachfahren: Wer den Finger aufsetzt und
        // ihn in einem Zug ans andere Ende wirft, hat den Strich nicht geschrieben.
        // Ein echter Finger bewegt sich bei 60 Bildern je Sekunde nur ein bis vier
        // Stützpunkte weit — die Vorausschau von 12 ist also reichlich bemessen.
        val punkte = geraderStrich.abtasten(120)
        val t = tracker()
        t.senke(punkte.first())
        assertEquals(Zug.DANEBEN, t.ziehe(punkte[60]))
        assertEquals(0, t.fortschritt)
    }

    @Test
    fun `eine Abkuerzung quer ueber den Bogen wird nicht anerkannt`() {
        val punkte = bogen.abtasten(120)
        val t = tracker(bogen)
        t.senke(punkte.first())
        // Direkt in gerader Linie zum Endpunkt: die Sehne liegt weit unter dem Scheitel.
        val mitteDerSehne = GlyphPoint(
            (punkte.first().x + punkte.last().x) / 2f,
            (punkte.first().y + punkte.last().y) / 2f,
        )
        assertEquals(Zug.DANEBEN, t.ziehe(mitteDerSehne))
        assertFalse(t.fertig)
    }

    @Test
    fun `daneben behaelt den Fortschritt und man kann zurueckkommen`() {
        val punkte = geraderStrich.abtasten(120)
        val t = tracker()
        t.senke(punkte.first())
        t.ziehe(punkte[40])
        val erreicht = t.fortschritt

        assertEquals(Zug.DANEBEN, t.ziehe(GlyphPoint(900f, 400f)))
        assertEquals("Abkommen darf nichts löschen", erreicht, t.fortschritt)

        assertEquals(Zug.LAEUFT, t.ziehe(punkte[erreicht]))
        assertEquals(erreicht, t.fortschritt)
    }

    @Test
    fun `Anheben beginnt den Strich von vorn`() {
        val punkte = geraderStrich.abtasten(120)
        val t = tracker()
        t.senke(punkte.first())
        t.ziehe(punkte[50])
        assertEquals(Zug.WARTET, t.hebe())
        assertEquals(-1, t.fortschritt)
        assertEquals(0f, t.anteil, 0.001f)
    }

    @Test
    fun `Anheben nach dem Ende macht nichts kaputt`() {
        val punkte = geraderStrich.abtasten(120)
        val t = tracker()
        fahreNach(t, punkte)
        assertEquals(Zug.FERTIG, t.hebe())
        assertTrue(t.fertig)
    }

    @Test
    fun `ein Wisch ohne Startberuehrung beginnt nichts`() {
        val punkte = geraderStrich.abtasten(120)
        val t = tracker()
        // Kein senke() — nur ziehen, mitten auf der Linie.
        repeat(30) { i -> t.ziehe(punkte[i * 4]) }
        assertEquals(Zug.WARTET, t.zustand)
        assertEquals(-1, t.fortschritt)
    }

    @Test
    fun `kurz vor Schluss loslassen zaehlt trotzdem`() {
        val punkte = geraderStrich.abtasten(120)
        val t = tracker()
        t.senke(punkte.first())
        for (i in 1..30) t.ziehe(punkte[punkte.lastIndex * i / 30])
        assertTrue(t.fertig)

        // Und die Gegenprobe: auf halber Strecke stehenbleiben ist NICHT fertig.
        val t2 = tracker()
        t2.senke(punkte.first())
        for (i in 1..15) t2.ziehe(punkte[(punkte.lastIndex / 2) * i / 15])
        assertFalse(t2.fertig)
    }

    @Test
    fun `Genauigkeit wirkt - was leicht durchgeht faellt bei genau durch`() {
        val punkte = geraderStrich.abtasten(120)
        val danebenUm = 130f // zwischen GENAU (78) und LEICHT (155)
        // Seitlich neben einem Punkt, der noch in der Vorausschau liegt — sonst misst der
        // Test die Vorausschau statt der Toleranz.
        val seitlichNeben = GlyphPoint(punkte[6].x + danebenUm, punkte[6].y)

        val leicht = tracker(genauigkeit = Genauigkeit.LEICHT)
        leicht.senke(punkte.first())
        assertEquals(Zug.LAEUFT, leicht.ziehe(seitlichNeben))

        val genau = tracker(genauigkeit = Genauigkeit.GENAU)
        genau.senke(punkte.first())
        assertEquals(Zug.DANEBEN, genau.ziehe(seitlichNeben))
    }

    @Test
    fun `der Anteil waechst monoton`() {
        val punkte = geraderStrich.abtasten(120)
        val t = tracker()
        t.senke(punkte.first())
        var vorher = t.anteil
        for (i in 1..60) {
            t.ziehe(punkte[punkte.lastIndex * i / 60])
            assertTrue("Anteil ist gesunken bei Schritt $i", t.anteil >= vorher)
            vorher = t.anteil
        }
    }
}
