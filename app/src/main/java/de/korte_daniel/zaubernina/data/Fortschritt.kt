package de.korte_daniel.zaubernina.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import de.korte_daniel.zaubernina.domain.Avatar
import de.korte_daniel.zaubernina.domain.Benutzer
import de.korte_daniel.zaubernina.domain.Klasse
import de.korte_daniel.zaubernina.domain.Paket
import de.korte_daniel.zaubernina.domain.ALPHABET
import de.korte_daniel.zaubernina.domain.mitZahl
import de.korte_daniel.zaubernina.domain.sterneFuerAlphabetRunde
import de.korte_daniel.zaubernina.domain.nachLevel
import de.korte_daniel.zaubernina.domain.sterneFuerZahl
import de.korte_daniel.zaubernina.logic.AUFGABEN_JE_STERN
import de.korte_daniel.zaubernina.logic.Genauigkeit
import de.korte_daniel.zaubernina.ui.theme.Thema
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Der Spielstand EINES Benutzers in EINEM Wortpaket. Der Fortschritt ist je Paket
 * getrennt — wer vom Standard auf den Fuchs wechselt, fängt dort vorn an und verliert
 * nichts, wenn er zurückwechselt.
 */
data class Spielstand(
    val geschafft: Int = 0,
    val zahlen: Int = 0,
)

/** Alles, was zu einem Benutzer gehört. */
data class BenutzerDaten(
    val benutzer: Benutzer,
    val paket: Paket = Paket.STANDARD,
    val klasse: Klasse = Klasse.VORSCHULE,
    val sterne: Int = 0,
    /** Richtige Rechenaufgaben insgesamt — alle [AUFGABEN_JE_STERN] gibt es einen Stern. */
    val rechenRichtig: Int = 0,
    /** Wie weit die laufende ABC-Runde ist (geschriebene Buchstaben, 0..28). */
    val alphabetIndex: Int = 0,
    /** Wie oft das ganze Alphabet schon durchgeschrieben wurde. */
    val alphabetRunden: Int = 0,
    val staende: Map<Paket, Spielstand> = emptyMap(),
) {
    fun stand(paket: Paket): Spielstand = staende[paket] ?: Spielstand()
    val aktuellerStand: Spielstand get() = stand(paket)
}

/** Der komplette gespeicherte Zustand der App. */
data class Zustand(
    val benutzer: List<BenutzerDaten> = emptyList(),
    val thema: Thema = Thema.NACHTHIMMEL,
    val genauigkeit: Genauigkeit = Genauigkeit.NORMAL,
    val eigeneWoerter: List<String> = emptyList(),
) {
    fun daten(benutzerId: Int): BenutzerDaten? = benutzer.firstOrNull { it.benutzer.id == benutzerId }
}

private val Context.speicher: DataStore<Preferences> by preferencesDataStore(name = "zaubernina")

// Globale Schlüssel
private val SCHLUESSEL_BENUTZER_IDS = stringPreferencesKey("benutzer_ids")
private val SCHLUESSEL_THEMA = stringPreferencesKey("thema")
private val SCHLUESSEL_GENAUIGKEIT = stringPreferencesKey("genauigkeit")
private val SCHLUESSEL_EIGENE = stringPreferencesKey("eigene_woerter")

// Schlüssel je Benutzer. DataStore kennt keine Tabellen — die Benutzer-ID steckt im Namen.
private fun kName(id: Int) = stringPreferencesKey("u${id}_name")
private fun kAvatar(id: Int) = stringPreferencesKey("u${id}_avatar")
private fun kPaket(id: Int) = stringPreferencesKey("u${id}_paket")
private fun kKlasse(id: Int) = stringPreferencesKey("u${id}_klasse")
private fun kSterne(id: Int) = intPreferencesKey("u${id}_sterne")
private fun kRechen(id: Int) = intPreferencesKey("u${id}_rechen_richtig")
private fun kAlphabetIndex(id: Int) = intPreferencesKey("u${id}_alphabet_index")
private fun kAlphabetRunden(id: Int) = intPreferencesKey("u${id}_alphabet_runden")
private fun kGeschafft(id: Int, paket: Paket) = intPreferencesKey("u${id}_${paket.name}_geschafft")
private fun kZahlen(id: Int, paket: Paket) = intPreferencesKey("u${id}_${paket.name}_zahlen")

// Die Schlüssel der Ein-Benutzer-Fassung — nur noch für die Wanderung beim ersten Start.
private val ALT_GESCHAFFT = intPreferencesKey("geschaffte_level")
private val ALT_ZAHLEN = intPreferencesKey("geschriebene_zahlen")
private val ALT_STERNE = intPreferencesKey("sterne")

private inline fun <reified E : Enum<E>> lese(name: String?, vorgabe: E): E =
    enumValues<E>().firstOrNull { it.name == name } ?: vorgabe

class FortschrittSpeicher(private val context: Context) {

    val zustand: Flow<Zustand> = context.speicher.data.map { p ->
        val ids = (p[SCHLUESSEL_BENUTZER_IDS] ?: "").split(",").mapNotNull { it.toIntOrNull() }
        Zustand(
            benutzer = ids.map { id ->
                BenutzerDaten(
                    benutzer = Benutzer(
                        id = id,
                        name = p[kName(id)] ?: "Kind",
                        avatar = lese(p[kAvatar(id)], Avatar.STERN),
                    ),
                    paket = lese(p[kPaket(id)], Paket.STANDARD),
                    klasse = lese(p[kKlasse(id)], Klasse.VORSCHULE),
                    sterne = p[kSterne(id)] ?: 0,
                    rechenRichtig = p[kRechen(id)] ?: 0,
                    alphabetIndex = p[kAlphabetIndex(id)] ?: 0,
                    alphabetRunden = p[kAlphabetRunden(id)] ?: 0,
                    staende = Paket.entries.associateWith { paket ->
                        Spielstand(
                            geschafft = p[kGeschafft(id, paket)] ?: 0,
                            zahlen = p[kZahlen(id, paket)] ?: 0,
                        )
                    },
                )
            },
            thema = lese(p[SCHLUESSEL_THEMA], Thema.NACHTHIMMEL),
            genauigkeit = lese(p[SCHLUESSEL_GENAUIGKEIT], Genauigkeit.NORMAL),
            eigeneWoerter = (p[SCHLUESSEL_EIGENE] ?: "").split(",").filter { it.isNotBlank() },
        )
    }

    /**
     * Sorgt dafür, dass es mindestens einen Benutzer gibt — und nimmt dabei den
     * Spielstand der Ein-Benutzer-Fassung mit: Wer schon Level geschafft hatte, findet
     * sie beim ersten Benutzer (Nina) wieder. Läuft bei jedem App-Start, tut aber nur
     * beim allerersten etwas.
     */
    suspend fun stelleErstenBenutzerSicher() {
        context.speicher.edit { p ->
            if ((p[SCHLUESSEL_BENUTZER_IDS] ?: "").isNotBlank()) return@edit
            p[SCHLUESSEL_BENUTZER_IDS] = "0"
            p[kName(0)] = "Nina"
            p[kAvatar(0)] = Avatar.STERN.name
            p[kPaket(0)] = Paket.STANDARD.name
            // Wanderung: der alte Fortschritt gehörte inhaltlich zum Standard-Paket.
            p[ALT_GESCHAFFT]?.let { p[kGeschafft(0, Paket.STANDARD)] = it }
            p[ALT_ZAHLEN]?.let { p[kZahlen(0, Paket.STANDARD)] = it }
            p[ALT_STERNE]?.let { p[kSterne(0)] = it }
            p.remove(ALT_GESCHAFFT); p.remove(ALT_ZAHLEN); p.remove(ALT_STERNE)
        }
    }

    // ───────── Benutzerverwaltung (Elternbereich) ─────────

    suspend fun benutzerAnlegen(name: String, avatar: Avatar) {
        context.speicher.edit { p ->
            val ids = (p[SCHLUESSEL_BENUTZER_IDS] ?: "").split(",").mapNotNull { it.toIntOrNull() }
            val id = (ids.maxOrNull() ?: -1) + 1
            p[SCHLUESSEL_BENUTZER_IDS] = (ids + id).joinToString(",")
            p[kName(id)] = name.trim().ifEmpty { "Kind" }
            p[kAvatar(id)] = avatar.name
        }
    }

    suspend fun benutzerAendern(id: Int, name: String, avatar: Avatar) {
        context.speicher.edit { p ->
            p[kName(id)] = name.trim().ifEmpty { "Kind" }
            p[kAvatar(id)] = avatar.name
        }
    }

    /** Löscht den Benutzer samt seines gesamten Spielstands. Der letzte ist unlöschbar. */
    suspend fun benutzerLoeschen(id: Int) {
        context.speicher.edit { p ->
            val ids = (p[SCHLUESSEL_BENUTZER_IDS] ?: "").split(",").mapNotNull { it.toIntOrNull() }
            if (ids.size <= 1) return@edit
            p[SCHLUESSEL_BENUTZER_IDS] = ids.filterNot { it == id }.joinToString(",")
            p.remove(kName(id)); p.remove(kAvatar(id)); p.remove(kPaket(id)); p.remove(kKlasse(id))
            p.remove(kSterne(id)); p.remove(kRechen(id))
            p.remove(kAlphabetIndex(id)); p.remove(kAlphabetRunden(id))
            Paket.entries.forEach { paket ->
                p.remove(kGeschafft(id, paket)); p.remove(kZahlen(id, paket))
            }
        }
    }

    // ───────── Spielstand ─────────

    suspend fun levelGeschafft(benutzerId: Int, paket: Paket, index: Int, levelAnzahl: Int) {
        context.speicher.edit { p ->
            val bisher = p[kGeschafft(benutzerId, paket)] ?: 0
            // nachLevel ist auf die feste Levelzahl der Standardreise geschrieben —
            // hier zählt die Anzahl des aktiven Pakets.
            val neu = if (index == bisher) (bisher + 1).coerceAtMost(levelAnzahl) else bisher
            p[kGeschafft(benutzerId, paket)] = neu
            if (index == bisher) {
                p[kSterne(benutzerId)] = (p[kSterne(benutzerId)] ?: 0) + 3
            }
        }
    }

    suspend fun zahlGeschrieben(benutzerId: Int, paket: Paket, index: Int) {
        context.speicher.edit { p ->
            val bisher = p[kZahlen(benutzerId, paket)] ?: 0
            val dazu = sterneFuerZahl(bisher, index)
            p[kZahlen(benutzerId, paket)] = mitZahl(bisher, index)
            if (dazu > 0) p[kSterne(benutzerId)] = (p[kSterne(benutzerId)] ?: 0) + dazu
        }
    }

    /**
     * Ein Buchstabe der ABC-Runde ist geschrieben. Ist die Runde damit voll, beginnt die
     * nächste bei A — und die ERSTE volle Runde bringt fünf Sterne (danach keine mehr,
     * dieselbe Regel wie überall).
     */
    suspend fun alphabetBuchstabeGeschrieben(benutzerId: Int) {
        context.speicher.edit { p ->
            val index = (p[kAlphabetIndex(benutzerId)] ?: 0) + 1
            if (index >= ALPHABET.size) {
                val runden = p[kAlphabetRunden(benutzerId)] ?: 0
                val dazu = sterneFuerAlphabetRunde(runden)
                p[kAlphabetIndex(benutzerId)] = 0
                p[kAlphabetRunden(benutzerId)] = runden + 1
                if (dazu > 0) p[kSterne(benutzerId)] = (p[kSterne(benutzerId)] ?: 0) + dazu
            } else {
                p[kAlphabetIndex(benutzerId)] = index
            }
        }
    }

    /** Eine richtige Rechenaufgabe. Alle [AUFGABEN_JE_STERN] gibt es einen Stern. */
    suspend fun rechenaufgabeRichtig(benutzerId: Int) {
        context.speicher.edit { p ->
            val neu = (p[kRechen(benutzerId)] ?: 0) + 1
            p[kRechen(benutzerId)] = neu
            if (neu % AUFGABEN_JE_STERN == 0) {
                p[kSterne(benutzerId)] = (p[kSterne(benutzerId)] ?: 0) + 1
            }
        }
    }

    // ───────── Einstellungen ─────────

    suspend fun setzePaket(benutzerId: Int, paket: Paket) {
        context.speicher.edit { it[kPaket(benutzerId)] = paket.name }
    }

    suspend fun setzeKlasse(benutzerId: Int, klasse: Klasse) {
        context.speicher.edit { it[kKlasse(benutzerId)] = klasse.name }
    }

    suspend fun setzeEigeneWoerter(woerter: List<String>) {
        context.speicher.edit { it[SCHLUESSEL_EIGENE] = woerter.joinToString(",") }
    }

    suspend fun setzeThema(thema: Thema) {
        context.speicher.edit { it[SCHLUESSEL_THEMA] = thema.name }
    }

    suspend fun setzeGenauigkeit(genauigkeit: Genauigkeit) {
        context.speicher.edit { it[SCHLUESSEL_GENAUIGKEIT] = genauigkeit.name }
    }

    /** Setzt NUR den Spielstand des einen Benutzers zurück, nicht seine Einstellungen. */
    suspend fun fortschrittZuruecksetzen(benutzerId: Int) {
        context.speicher.edit { p ->
            p[kSterne(benutzerId)] = 0
            p[kRechen(benutzerId)] = 0
            p[kAlphabetIndex(benutzerId)] = 0
            p[kAlphabetRunden(benutzerId)] = 0
            Paket.entries.forEach { paket ->
                p.remove(kGeschafft(benutzerId, paket))
                p.remove(kZahlen(benutzerId, paket))
            }
        }
    }
}
