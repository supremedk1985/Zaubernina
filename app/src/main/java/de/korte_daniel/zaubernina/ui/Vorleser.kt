package de.korte_daniel.zaubernina.ui

import android.content.Context
import android.speech.tts.TextToSpeech
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import java.util.Locale

/**
 * Liest Buchstaben und Zahlen mit Androids eingebauter Sprachausgabe vor. Das braucht
 * weder eine Berechtigung noch Netz — gesprochen wird mit der Stimme, die auf dem Gerät
 * installiert ist (der Manifest-Eintrag <queries> macht die Sprachdienste nur sichtbar,
 * er erlaubt nichts). Gibt es keine Sprachausgabe, bleibt die App einfach stumm, und
 * das Üben funktioniert unverändert.
 *
 * Die Sprachausgabe meldet sich erst nach einem asynchronen Start einsatzbereit. Was
 * vorher gesprochen werden soll, wird gemerkt und beim Start nachgeholt — sonst bliebe
 * ausgerechnet der allererste Buchstabe stumm.
 */
class Vorleser(context: Context) {

    private var bereit = false
    private var gemerkt: String? = null
    private var stimme: TextToSpeech? = null

    init {
        stimme = TextToSpeech(context) { status ->
            val s = stimme
            if (status == TextToSpeech.SUCCESS && s != null) {
                // Schlägt Deutsch fehl, spricht die Voreinstellung des Geräts — auf
                // einem deutsch eingerichteten Gerät ist das dieselbe Stimme.
                s.setLanguage(Locale.GERMAN)
                // Etwas langsamer als Erwachsenentempo: die Zuhörerin ist fünf.
                s.setSpeechRate(0.85f)
                bereit = true
                gemerkt?.let { sprich(it) }
                gemerkt = null
            }
        }
    }

    /** Spricht [text] sofort und bricht dafür ab, was gerade noch gesprochen wird. */
    fun sprich(text: String) {
        if (bereit) {
            stimme?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "zaubernina")
        } else {
            gemerkt = text
        }
    }

    fun schliessen() {
        bereit = false
        stimme?.stop()
        stimme?.shutdown()
        stimme = null
    }
}

/** Ein [Vorleser], der so lange lebt wie die Composition, in der er erzeugt wurde. */
@Composable
fun rememberVorleser(): Vorleser {
    val context = LocalContext.current
    val vorleser = remember { Vorleser(context) }
    DisposableEffect(Unit) {
        onDispose { vorleser.schliessen() }
    }
    return vorleser
}
