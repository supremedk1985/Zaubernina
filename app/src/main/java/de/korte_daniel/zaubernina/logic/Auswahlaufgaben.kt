package de.korte_daniel.zaubernina.logic

import de.korte_daniel.zaubernina.data.ANLAUTTIERE
import de.korte_daniel.zaubernina.data.Vokabel
import de.korte_daniel.zaubernina.data.WORTTHEMEN
import de.korte_daniel.zaubernina.data.Wortthema
import de.korte_daniel.zaubernina.domain.Fremdsprache
import kotlin.random.Random

/**
 * Das gemeinsame Aufgabenformat von Hören, Lesen und Sprachen: EINE Frage, DREI Antworten,
 * genau eine richtig. Drei, weil mehr ein Kind überfordert und zwei Raten wäre (Leitplanke).
 *
 * Alles, was gesprochen wird, steht hier als Text mit Sprachkennung — die Oberfläche spricht
 * es nur aus. So bleibt die Logik ohne Android testbar.
 */
data class Antwort(
    val kennung: String,
    /** Emoji als Bild, oder leer. */
    val symbol: String = "",
    /** Vektorbild (Anlauttiere), oder 0. */
    val bild: Int = 0,
    /** Farbfläche als Hex, oder leer. */
    val farbe: String = "",
    /** Sichtbarer Text, oder leer. */
    val text: String = "",
)

data class Auswahlaufgabe(
    /** Kennung für die Fehlerstatistik der Eltern, z. B. „M" oder „Pinguin". */
    val kennung: String,
    /** Groß sichtbare Frage — darf | als Silbentrenner enthalten. */
    val frageText: String = "",
    val frageSymbol: String = "",
    val frageBild: Int = 0,
    /** Wird beim Erscheinen gesprochen und über den Lautsprecher-Knopf wiederholt. */
    val ansage: String,
    val ansageSprache: String = "de",
    val antworten: List<Antwort>,
    val richtig: String,
    /** Nach dem ersten Fehler gesprochen: ein Hinweis, keine Lösung. */
    val hinweis: String,
    /** Nach dem zweiten Fehler gesprochen, während die richtige Antwort leuchtet. */
    val loesung: String,
) {
    init {
        require(antworten.size == 3) { "genau drei Antworten" }
        require(antworten.map { it.kennung }.toSet().size == 3) { "Antworten verschieden" }
        require(antworten.any { it.kennung == richtig }) { "richtige Antwort dabei" }
    }
}

/** Woher die Aufgaben kommen. [schwierigkeit] 0..2, gesteuert von [Anpassung]. */
interface Aufgabenquelle {
    val titel: String
    fun naechste(schwierigkeit: Int, zufall: Random = Random.Default): Auswahlaufgabe
}

/**
 * Die stille Anpassung: drei richtige in Folge → eine Stufe schwerer, zwei Fehler in einer
 * Aufgabe → eine Stufe leichter. Das Kind sieht davon nichts (Leitplanke: kein „Abstieg").
 */
class Anpassung(start: Int = 0) {
    var stufe: Int = start.coerceIn(0, 2)
        private set
    private var richtigFolge = 0

    fun richtig() {
        richtigFolge++
        if (richtigFolge >= 3) { stufe = (stufe + 1).coerceAtMost(2); richtigFolge = 0 }
    }

    fun zweiterFehler() {
        richtigFolge = 0
        stufe = (stufe - 1).coerceAtLeast(0)
    }
}

private fun <T> List<T>.ziehe(anzahl: Int, zufall: Random, ohne: (T) -> Boolean = { false }): List<T> =
    filterNot(ohne).shuffled(zufall).take(anzahl)

// ───────────────────────── Hören: Anlaute (Vorschule) ─────────────────────────

/** Buchstaben, die für Fünfjährige ähnlich klingen — auf Stufe 0 nie zusammen in einer Aufgabe. */
private val AEHNLICH = listOf(setOf('M', 'N'), setOf('B', 'P', 'D'), setOf('G', 'K'), setOf('F', 'W', 'V'), setOf('S', 'Z'), setOf('E', 'I'), setOf('A', 'O', 'U'))

private fun aehnlich(a: Char, b: Char) = AEHNLICH.any { a in it && b in it }

/** „Welches Bild fängt mit M an?" — drei Anlauttiere, eines passt. */
class AnlautQuelle : Aufgabenquelle {
    override val titel = "Hören"
    private val buchstaben = ANLAUTTIERE.keys.sorted()

    override fun naechste(schwierigkeit: Int, zufall: Random): Auswahlaufgabe {
        val ziel = buchstaben.random(zufall)
        val tier = ANLAUTTIERE.getValue(ziel)
        val andere = buchstaben.ziehe(2, zufall) { it == ziel || (schwierigkeit == 0 && aehnlich(it, ziel)) }
        val antworten = (listOf(ziel) + andere).shuffled(zufall).map { b ->
            val t = ANLAUTTIERE.getValue(b)
            Antwort(kennung = b.toString(), bild = t.bild)
        }
        return Auswahlaufgabe(
            kennung = ziel.toString(),
            frageText = ziel.toString(),
            ansage = "Welches Bild fängt mit $ziel an?",
            antworten = antworten,
            richtig = ziel.toString(),
            hinweis = "Sag die Wörter leise vor dich hin. Welches fängt mit $ziel an?",
            loesung = "${tier.wort} fängt mit $ziel an.",
        )
    }
}

// ───────────────────────── Lesen: Silbenwörter (1./2. Klasse) ─────────────────────────

private val LESEWOERTER: List<Pair<Wortthema, Vokabel>> =
    WORTTHEMEN.flatMap { t -> t.woerter.filter { it.artikel.isNotEmpty() && it.bild.isNotEmpty() }.map { t to it } }

/**
 * Stufe 0: Wort in Silben lesen, Bild finden. Stufe 1: Wort ohne Silbenhilfe.
 * Stufe 2: ein kurzer Satz („Da ist eine Katze."). Die falschen Bilder unterscheiden sich
 * im Anfangsbuchstaben — sonst rät man nach dem ersten Buchstaben statt zu lesen.
 */
class LeseQuelle : Aufgabenquelle {
    override val titel = "Lesen"

    override fun naechste(schwierigkeit: Int, zufall: Random): Auswahlaufgabe {
        val (thema, wort) = LESEWOERTER.random(zufall)
        val andere = LESEWOERTER.map { it.second }.ziehe(2, zufall) { it.de == wort.de || it.de.first() == wort.de.first() || it.bild == wort.bild }
        val antworten = (listOf(wort) + andere).shuffled(zufall).map { Antwort(kennung = it.de, symbol = it.bild) }
        val frage = when (schwierigkeit) {
            0 -> wort.deSilben
            1 -> wort.de
            else -> thema.satzDe.replace("{akk}", wort.akkusativ)
        }
        return Auswahlaufgabe(
            kennung = wort.de,
            frageText = frage,
            ansage = if (schwierigkeit >= 2) "Lies den Satz. Welches Bild passt?" else "Lies das Wort. Welches Bild passt?",
            antworten = antworten,
            richtig = wort.de,
            hinweis = "Lies langsam, Silbe für Silbe. Es fängt so an: ${wort.deSilben.substringBefore('|')}.",
            loesung = "Das Wort heißt ${wort.de}.",
        )
    }
}

// ───────────────────────── Sprachen: Englisch und Spanisch ─────────────────────────

/**
 * Stufe 0: nur hören, Bild tippen. Stufe 1: das fremde Wort steht da und wird gesprochen.
 * Stufe 2: ein kurzer Satz wird gesprochen. Alle Antworten kommen aus demselben Wortfeld —
 * so lernt das Kind „dog, cat, horse" als Gruppe, wie in Lingokids oder Duolingo ABC.
 */
class SprachenQuelle(private val sprache: Fremdsprache, private val thema: Wortthema) : Aufgabenquelle {
    override val titel = sprache.anzeigename

    private fun fremd(v: Vokabel) = if (sprache == Fremdsprache.ENGLISCH) v.en else v.es
    private fun satzmuster() = if (sprache == Fremdsprache.ENGLISCH) thema.satzEn else thema.satzEs

    override fun naechste(schwierigkeit: Int, zufall: Random): Auswahlaufgabe {
        val wort = thema.woerter.random(zufall)
        val andere = thema.woerter.ziehe(2, zufall) { it.de == wort.de }
        val antworten = (listOf(wort) + andere).shuffled(zufall).map { Antwort(kennung = it.de, symbol = it.bild, farbe = it.farbe) }
        val muster = satzmuster()
        val ansage = when {
            schwierigkeit >= 2 && muster.isNotEmpty() -> muster.replace("{en}", wort.en).replace("{es}", wort.es)
            else -> fremd(wort)
        }
        return Auswahlaufgabe(
            kennung = wort.de,
            frageText = if (schwierigkeit >= 1) fremd(wort) else "",
            frageSymbol = if (schwierigkeit == 0) "🔊" else "",
            ansage = ansage,
            ansageSprache = sprache.sprachkennung,
            antworten = antworten,
            richtig = wort.de,
            hinweis = fremd(wort),
            loesung = "${fremd(wort)} heißt ${wort.de}.",
        )
    }
}
