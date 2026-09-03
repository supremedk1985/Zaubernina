package de.korte_daniel.zaubernina.logic

/**
 * Ein Wort mit seinen Sprechsilben, so wie es im Text steht: „Pin|gu|in" → [Pin, gu, in].
 * Satzzeichen bleiben an der letzten Silbe („Fisch." → [Fisch.]) — beim Vorlesen und in
 * der Hilfe-Statistik zählt nur [rein].
 */
data class SilbenWort(val silben: List<String>) {
    val text: String get() = silben.joinToString("")
    /** Das Wort ohne Satzzeichen und Anführungszeichen. */
    val rein: String get() = text.trim { !it.isLetterOrDigit() }
}

/** Zerlegt eine Zeile in Wörter (an Leerzeichen) und die Wörter in Silben (an |). */
fun zerlegeZeile(zeile: String): List<SilbenWort> =
    zeile.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }.map { wort ->
        SilbenWort(wort.split('|').filter { it.isNotEmpty() })
    }

/** Eine Seite: Zeilen (an Zeilenumbrüchen), darin Wörter. */
fun zerlegeSeite(seite: String): List<List<SilbenWort>> = seite.split('\n').map(::zerlegeZeile)

/** Geschätzte Vorlesezeit einer Leseanfängerin (2. Klasse, laut, mit Publikum): ~45 Wörter/Minute. */
fun vorleseMinuten(woerter: Int): Int = ((woerter + 44) / 45).coerceAtLeast(1)
