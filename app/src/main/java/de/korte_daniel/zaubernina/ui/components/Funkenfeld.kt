package de.korte_daniel.zaubernina.ui.components

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/** Ein einzelner Funke. Bewusst veränderlich: davon leben ein paar hundert, jedes Bild. */
class Funke(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    var alter: Float,
    val lebensdauer: Float,
    val groesse: Float,
    val farbe: Color,
    val drehung: Float,
) {
    /** 1 am Anfang, 0 am Ende — steuert Größe und Deckkraft. */
    val frische: Float get() = (1f - alter / lebensdauer).coerceIn(0f, 1f)
    val lebt: Boolean get() = alter < lebensdauer
}

/**
 * Die Funken der Zauberlinie. Reine Rechnerei ohne Compose-Bezug, damit sie sich prüfen
 * lässt und damit das Zeichnen nichts weiter tut als die Liste durchzugehen.
 *
 * [hoechstzahl] ist eine harte Obergrenze: Ein Kind, das minutenlang über den Bildschirm
 * fährt, darf keine unbegrenzt wachsende Liste erzeugen. Bei Überschreitung werden die
 * ältesten Funken verworfen — sie sind ohnehin fast unsichtbar.
 */
class Funkenfeld(
    private val hoechstzahl: Int = 200,
    private val zufall: Random = Random.Default,
) {
    private val _funken = ArrayList<Funke>(hoechstzahl)
    val funken: List<Funke> get() = _funken

    /** Streut [anzahl] Funken am Fingerpunkt. */
    fun speise(x: Float, y: Float, farben: List<Color>, anzahl: Int = 3, staerke: Float = 1f) {
        repeat(anzahl) {
            val winkel = zufall.nextFloat() * 2f * Math.PI.toFloat()
            val tempo = (60f + zufall.nextFloat() * 190f) * staerke
            _funken += Funke(
                x = x + (zufall.nextFloat() - 0.5f) * 26f,
                y = y + (zufall.nextFloat() - 0.5f) * 26f,
                vx = cos(winkel) * tempo,
                vy = sin(winkel) * tempo,
                alter = 0f,
                lebensdauer = 0.45f + zufall.nextFloat() * 0.65f,
                groesse = 7f + zufall.nextFloat() * 16f,
                farbe = farben[zufall.nextInt(farben.size)],
                drehung = zufall.nextFloat() * 90f,
            )
        }
        if (_funken.size > hoechstzahl) {
            _funken.subList(0, _funken.size - hoechstzahl).clear()
        }
    }

    /** Ein Zeitschritt. [dt] in Sekunden. */
    fun schritt(dt: Float) {
        if (dt <= 0f) return
        val bremse = 0.86f
        val iterator = _funken.iterator()
        while (iterator.hasNext()) {
            val f = iterator.next()
            f.alter += dt
            if (!f.lebt) {
                iterator.remove()
                continue
            }
            f.x += f.vx * dt
            f.y += f.vy * dt
            // Leichtes Absinken und Ausbremsen: die Funken rieseln, sie fliegen nicht weg.
            f.vy += 120f * dt
            f.vx *= bremse
            f.vy *= bremse
        }
    }

    fun leeren() = _funken.clear()

    val anzahl: Int get() = _funken.size
}

/** Vierzackiger Stern als Punktliste — dieselbe Form wie in den Entwürfen. */
fun sternPunkte(mitte: Offset, radius: Float, drehungGrad: Float): List<Offset> {
    val bogen = drehungGrad / 180f * Math.PI.toFloat()
    val innen = radius * 0.24f
    return (0 until 8).map { i ->
        val r = if (i % 2 == 0) radius else innen
        val w = bogen + i * (Math.PI.toFloat() / 4f)
        Offset(mitte.x + cos(w) * r, mitte.y + sin(w) * r)
    }
}
