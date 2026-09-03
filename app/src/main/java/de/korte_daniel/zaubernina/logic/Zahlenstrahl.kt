package de.korte_daniel.zaubernina.logic

/** Eine Plus- oder Minusaufgabe in Zahlen zerlegt — für den Zahlenstrahl. */
data class Rechenschritt(val start: Int, val plus: Boolean, val schritte: Int) {
    val ziel: Int get() = if (plus) start + schritte else start - schritte

    /**
     * Die Sprünge auf dem Zahlenstrahl, wie die Schule sie lehrt: über die Zehn in ZWEI
     * Sprüngen („erst bis zur 10, dann weiter"), sonst in einem.
     */
    fun spruenge(): List<Pair<Int, Int>> {
        val zehner = if (plus) ((start / 10) + 1) * 10 else (start - 1) / 10 * 10
        val ueberZehn = if (plus) start < zehner && ziel > zehner else start > zehner && ziel < zehner && zehner > 0
        return if (ueberZehn) listOf(start to zehner, zehner to ziel) else listOf(start to ziel)
    }

    /** Was die Stimme nach dem ersten Fehler sagt. */
    fun hinweis(): String {
        val s = spruenge()
        val richtung = if (plus) "weiter" else "zurück"
        return if (s.size == 2) {
            val bisZehner = kotlin.math.abs(s[0].second - s[0].first)
            val rest = kotlin.math.abs(s[1].second - s[1].first)
            "Fang bei $start an. Erst $bisZehner $richtung bis zur ${s[0].second}, dann noch $rest $richtung."
        } else {
            "Fang bei $start an und geh $schritte Schritte $richtung."
        }
    }
}

/** Liest „3 + 4" oder „9 − 5" (auch mit - oder –). null bei allem anderen. */
fun zerlegeRechenaufgabe(anzeige: String): Rechenschritt? {
    val treffer = Regex("""^\s*(\d+)\s*([+\-−–])\s*(\d+)\s*$""").find(anzeige) ?: return null
    val (a, op, b) = treffer.destructured
    return Rechenschritt(a.toInt(), op == "+", b.toInt())
}
