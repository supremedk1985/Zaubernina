package de.korte_daniel.zaubernina.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import de.korte_daniel.zaubernina.domain.mitZahl
import de.korte_daniel.zaubernina.domain.nachLevel
import de.korte_daniel.zaubernina.domain.sterneFuerZahl
import de.korte_daniel.zaubernina.domain.sterneFuer
import de.korte_daniel.zaubernina.logic.Genauigkeit
import de.korte_daniel.zaubernina.ui.theme.Thema
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Alles, was die App sich merkt. Es ist wenig, und es bleibt absichtlich wenig:
 * kein Konto, keine Namen außer denen in der Wortliste, nichts, was das Gerät verlässt.
 */
data class Fortschritt(
    /** Wie viele Level geschafft sind. Daraus folgt, was offen ist — siehe domain/Level.kt. */
    val geschafft: Int = 0,
    /** Bitsatz der geschriebenen Zwischendurch-Zahlen (Bit i = Ziffer nach Level i). */
    val zahlen: Int = 0,
    val sterne: Int = 0,
    val thema: Thema = Thema.NACHTHIMMEL,
    val genauigkeit: Genauigkeit = Genauigkeit.NORMAL,
)

private val Context.speicher: DataStore<Preferences> by preferencesDataStore(name = "zaubernina")

private val SCHLUESSEL_GESCHAFFT = intPreferencesKey("geschaffte_level")
private val SCHLUESSEL_STERNE = intPreferencesKey("sterne")
private val SCHLUESSEL_ZAHLEN = intPreferencesKey("geschriebene_zahlen")
private val SCHLUESSEL_THEMA = stringPreferencesKey("thema")
private val SCHLUESSEL_GENAUIGKEIT = stringPreferencesKey("genauigkeit")

/**
 * Liest und schreibt den Fortschritt. Mit Schlössern ist das Speichern keine Kür: Ein Kind,
 * das nach jedem App-Start wieder bei Level 1 eingesperrt wäre, gibt auf.
 *
 * Unbekannte Werte in der Ablage (etwa ein Thema, das es nicht mehr gibt) fallen still auf
 * die Vorgabe zurück, statt die App zum Absturz zu bringen.
 */
class FortschrittSpeicher(private val context: Context) {

    val fortschritt: Flow<Fortschritt> = context.speicher.data.map { p ->
        Fortschritt(
            geschafft = p[SCHLUESSEL_GESCHAFFT] ?: 0,
            zahlen = p[SCHLUESSEL_ZAHLEN] ?: 0,
            sterne = p[SCHLUESSEL_STERNE] ?: 0,
            thema = p[SCHLUESSEL_THEMA]?.let { name ->
                Thema.entries.firstOrNull { it.name == name }
            } ?: Thema.NACHTHIMMEL,
            genauigkeit = p[SCHLUESSEL_GENAUIGKEIT]?.let { name ->
                Genauigkeit.entries.firstOrNull { it.name == name }
            } ?: Genauigkeit.NORMAL,
        )
    }

    /**
     * Ein Level ist zu Ende geschrieben. Freischalten und Sterne verteilen entscheidet
     * beides domain/Level.kt — ein noch einmal gespieltes altes Level ändert nichts.
     */
    suspend fun levelGeschafft(index: Int) {
        context.speicher.edit { p ->
            val bisher = p[SCHLUESSEL_GESCHAFFT] ?: 0
            p[SCHLUESSEL_GESCHAFFT] = nachLevel(index, bisher)
            val dazu = sterneFuer(index, bisher)
            if (dazu > 0) p[SCHLUESSEL_STERNE] = (p[SCHLUESSEL_STERNE] ?: 0) + dazu
        }
    }

    /** Eine Zwischendurch-Zahl ist geschrieben: Bit setzen, Zusatzstern nur beim ersten Mal. */
    suspend fun zahlGeschrieben(index: Int) {
        context.speicher.edit { p ->
            val bisher = p[SCHLUESSEL_ZAHLEN] ?: 0
            val dazu = sterneFuerZahl(bisher, index)
            p[SCHLUESSEL_ZAHLEN] = mitZahl(bisher, index)
            if (dazu > 0) p[SCHLUESSEL_STERNE] = (p[SCHLUESSEL_STERNE] ?: 0) + dazu
        }
    }

    suspend fun setzeThema(thema: Thema) {
        context.speicher.edit { it[SCHLUESSEL_THEMA] = thema.name }
    }

    suspend fun setzeGenauigkeit(genauigkeit: Genauigkeit) {
        context.speicher.edit { it[SCHLUESSEL_GENAUIGKEIT] = genauigkeit.name }
    }

    /**
     * Setzt NUR den Spielstand zurück, nicht die Einstellungen. Wer den Fortschritt
     * löscht, will selten auch das Thema und die Genauigkeit neu wählen müssen.
     */
    suspend fun fortschrittZuruecksetzen() {
        context.speicher.edit { p ->
            p[SCHLUESSEL_GESCHAFFT] = 0
            p[SCHLUESSEL_ZAHLEN] = 0
            p[SCHLUESSEL_STERNE] = 0
        }
    }
}
