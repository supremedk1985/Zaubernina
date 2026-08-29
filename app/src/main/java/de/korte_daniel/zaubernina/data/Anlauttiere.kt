package de.korte_daniel.zaubernina.data

import androidx.annotation.DrawableRes
import de.korte_daniel.zaubernina.R

/**
 * Das Anlauttier eines Buchstabens: das Bild und der Name, der dazu gesprochen wird.
 *
 * Die Bilder stammen aus dem OpenMoji-Katalog (openmoji.org, CC BY-SA 4.0) und liegen
 * als Vector Drawables in `res/drawable` — gezeichnete Formen, keine Fotos. Sie kosten
 * die App rund 40 KB und brauchen weder Netz noch eine Berechtigung. Die Namensnennung
 * verlangt die Lizenz; sie steht im Elternbereich.
 */
data class Anlauttier(@DrawableRes val bild: Int, val wort: String) {
    /** Was die Sprachausgabe sagt: „N wie Nashorn". */
    fun satz(zeichen: Char): String = "$zeichen wie $wort"
}

/**
 * Fünf Buchstaben stehen bewusst NICHT in dieser Liste: X und Y haben im Deutschen kein
 * Tier, Ä Ö Ü kein Anlautwort. Dort bleibt der Platz leer und es wird nur der Buchstabe
 * gesprochen — besser als ein erfundenes Wort, das ein Kind nie wieder hört.
 */
private val TIERE: Map<Char, Anlauttier> = mapOf(
    'A' to Anlauttier(R.drawable.tier_affe, "Affe"),
    'B' to Anlauttier(R.drawable.tier_baer, "Bär"),
    'C' to Anlauttier(R.drawable.tier_chamaeleon, "Chamäleon"),
    'D' to Anlauttier(R.drawable.tier_delfin, "Delfin"),
    'E' to Anlauttier(R.drawable.tier_elefant, "Elefant"),
    'F' to Anlauttier(R.drawable.tier_fuchs, "Fuchs"),
    'G' to Anlauttier(R.drawable.tier_giraffe, "Giraffe"),
    'H' to Anlauttier(R.drawable.tier_hund, "Hund"),
    'I' to Anlauttier(R.drawable.tier_igel, "Igel"),
    'J' to Anlauttier(R.drawable.tier_jaguar, "Jaguar"),
    'K' to Anlauttier(R.drawable.tier_katze, "Katze"),
    'L' to Anlauttier(R.drawable.tier_loewe, "Löwe"),
    'M' to Anlauttier(R.drawable.tier_maus, "Maus"),
    'N' to Anlauttier(R.drawable.tier_nashorn, "Nashorn"),
    'O' to Anlauttier(R.drawable.tier_oktopus, "Oktopus"),
    'P' to Anlauttier(R.drawable.tier_pinguin, "Pinguin"),
    'Q' to Anlauttier(R.drawable.tier_qualle, "Qualle"),
    'R' to Anlauttier(R.drawable.tier_reh, "Reh"),
    'S' to Anlauttier(R.drawable.tier_schaf, "Schaf"),
    'T' to Anlauttier(R.drawable.tier_tiger, "Tiger"),
    'U' to Anlauttier(R.drawable.tier_uhu, "Uhu"),
    'V' to Anlauttier(R.drawable.tier_vogel, "Vogel"),
    'W' to Anlauttier(R.drawable.tier_wal, "Wal"),
    'Z' to Anlauttier(R.drawable.tier_zebra, "Zebra"),
)

/** Das Tier zu einem Zeichen, oder null (Ziffern, X, Y, Ä, Ö, Ü). */
fun anlauttier(zeichen: Char): Anlauttier? = TIERE[zeichen]

/** Wie viele Buchstaben ein Tier haben — für den Test und den Elternbereich. */
val ANLAUTTIER_ANZAHL: Int = TIERE.size

/** Nur für den Test: die Zuordnung von außen sichtbar. */
val ANLAUTTIERE: Map<Char, Anlauttier> get() = TIERE
