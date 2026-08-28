package de.korte_daniel.zaubernina.ui

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.platform.LocalContext
import de.korte_daniel.zaubernina.data.Fortschritt
import de.korte_daniel.zaubernina.data.FortschrittSpeicher
import de.korte_daniel.zaubernina.domain.LEVEL
import de.korte_daniel.zaubernina.domain.naechstesLevel
import de.korte_daniel.zaubernina.domain.sterneFuer
import de.korte_daniel.zaubernina.ui.level.GeschafftBildschirm
import de.korte_daniel.zaubernina.ui.level.ReiseBildschirm
import de.korte_daniel.zaubernina.ui.theme.ZauberTheme
import de.korte_daniel.zaubernina.ui.tracing.UebungsBildschirm
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Beim Üben soll der Bildschirm nicht ausgehen — ein Kind fährt langsam nach und
        // berührt dabei minutenlang dieselbe Stelle.
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContent { Zaubernina() }
    }
}

/** Wo die App gerade steht. Bewusst eine kleine versiegelte Klasse statt einer Navigations-Bibliothek. */
private sealed interface Ansicht {
    data object Reise : Ansicht
    data class Ueben(val level: Int) : Ansicht
    data class Geschafft(val level: Int, val neueSterne: Int) : Ansicht
}

@Composable
private fun Zaubernina() {
    val context = LocalContext.current
    val speicher = remember { FortschrittSpeicher(context) }
    val bereich = rememberCoroutineScope()
    val fortschritt by speicher.fortschritt.collectAsState(initial = Fortschritt())

    var ansicht by remember { mutableStateOf<Ansicht>(Ansicht.Reise) }

    ZauberTheme(fortschritt.thema) {
        val farben = ZauberTheme.farben
        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawBehind { drawRect(farben.hintergrundPinsel(size)) },
        ) {
            Box(modifier = Modifier.fillMaxSize().systemBarsPadding()) {
                when (val jetzt = ansicht) {
                    is Ansicht.Reise -> ReiseBildschirm(
                        geschafft = fortschritt.geschafft,
                        sterne = fortschritt.sterne,
                        thema = fortschritt.thema,
                        onLevelWaehlen = { ansicht = Ansicht.Ueben(it) },
                        onThemaWechsel = { neu -> bereich.launch { speicher.setzeThema(neu) } },
                    )

                    is Ansicht.Ueben -> UebungsBildschirm(
                        level = LEVEL[jetzt.level],
                        genauigkeit = fortschritt.genauigkeit,
                        onZurueck = { ansicht = Ansicht.Reise },
                        onLevelFertig = {
                            // Die Sterne müssen VOR dem Speichern gerechnet werden — danach
                            // ist das Level schon als geschafft vermerkt und es gäbe keine mehr.
                            val neueSterne = sterneFuer(jetzt.level, fortschritt.geschafft)
                            bereich.launch { speicher.levelGeschafft(jetzt.level) }
                            ansicht = Ansicht.Geschafft(jetzt.level, neueSterne)
                        },
                    )

                    is Ansicht.Geschafft -> GeschafftBildschirm(
                        levelIndex = jetzt.level,
                        neueSterne = jetzt.neueSterne,
                        onWeiter = {
                            val naechstes = naechstesLevel(jetzt.level)
                            ansicht = if (naechstes != null) Ansicht.Ueben(naechstes) else Ansicht.Reise
                        },
                        onZurReise = { ansicht = Ansicht.Reise },
                    )
                }
            }
        }
    }
}
