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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.platform.LocalContext
import de.korte_daniel.zaubernina.data.Fortschritt
import de.korte_daniel.zaubernina.data.FortschrittSpeicher
import de.korte_daniel.zaubernina.domain.LEVEL
import de.korte_daniel.zaubernina.domain.naechstesLevel
import de.korte_daniel.zaubernina.domain.sterneFuer
import de.korte_daniel.zaubernina.domain.sterneFuerZahl
import de.korte_daniel.zaubernina.domain.zifferFuer
import de.korte_daniel.zaubernina.ui.level.GeschafftBildschirm
import de.korte_daniel.zaubernina.ui.level.ReiseBildschirm
import de.korte_daniel.zaubernina.ui.level.ZaehlBildschirm
import de.korte_daniel.zaubernina.ui.parent.EinstellungenBildschirm
import de.korte_daniel.zaubernina.ui.parent.Elternschloss
import de.korte_daniel.zaubernina.ui.theme.LocalZauberFarben
import de.korte_daniel.zaubernina.ui.theme.ZauberTheme
import de.korte_daniel.zaubernina.ui.theme.fuerZahlen
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

    /** Eine Zwischendurch-Zahl schreiben, dann zählen. */
    data class Zahl(val level: Int) : Ansicht
    data class Zaehlen(val level: Int, val bonusStern: Boolean) : Ansicht

    /** Die Rechenaufgabe vor dem Elternbereich. */
    data object Schloss : Ansicht
    data object Einstellungen : Ansicht
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
                        zahlen = fortschritt.zahlen,
                        sterne = fortschritt.sterne,
                        onLevelWaehlen = { ansicht = Ansicht.Ueben(it) },
                        onZahlWaehlen = { ansicht = Ansicht.Zahl(it) },
                        onElternbereich = { ansicht = Ansicht.Schloss },
                    )

                    is Ansicht.Ueben -> UebungsBildschirm(
                        wort = LEVEL[jetzt.level].wort,
                        kopfzeile = { i -> "Level ${LEVEL[jetzt.level].nummer} · Buchstabe ${i + 1} von ${LEVEL[jetzt.level].wort.length}" },
                        genauigkeit = fortschritt.genauigkeit,
                        onZurueck = { ansicht = Ansicht.Reise },
                        onFertig = {
                            // Die Sterne müssen VOR dem Speichern gerechnet werden — danach
                            // ist das Level schon als geschafft vermerkt und es gäbe keine mehr.
                            val neueSterne = sterneFuer(jetzt.level, fortschritt.geschafft)
                            bereich.launch { speicher.levelGeschafft(jetzt.level) }
                            ansicht = Ansicht.Geschafft(jetzt.level, neueSterne)
                        },
                    )

                    // Die Zahlen leben in ihrer eigenen Farbwelt — lokal übergelegt,
                    // das globale Thema bleibt unangetastet (Muster aus CurruBike).
                    is Ansicht.Zahl -> CompositionLocalProvider(
                        LocalZauberFarben provides farben.fuerZahlen(),
                    ) {
                        UebungsBildschirm(
                            wort = "${zifferFuer(jetzt.level)}",
                            kopfzeile = { "Zauberzahl" },
                            genauigkeit = fortschritt.genauigkeit,
                            onZurueck = { ansicht = Ansicht.Reise },
                            onFertig = {
                                val bonus = sterneFuerZahl(fortschritt.zahlen, jetzt.level) > 0
                                ansicht = Ansicht.Zaehlen(jetzt.level, bonus)
                            },
                            ohneZwischenjubel = true,
                        )
                    }

                    is Ansicht.Zaehlen -> CompositionLocalProvider(
                        LocalZauberFarben provides farben.fuerZahlen(),
                    ) {
                        ZaehlBildschirm(
                            ziffer = zifferFuer(jetzt.level),
                            bonusStern = jetzt.bonusStern,
                            onFertig = {
                                bereich.launch { speicher.zahlGeschrieben(jetzt.level) }
                                ansicht = Ansicht.Reise
                            },
                        )
                    }

                    is Ansicht.Geschafft -> GeschafftBildschirm(
                        levelIndex = jetzt.level,
                        neueSterne = jetzt.neueSterne,
                        onWeiter = {
                            val naechstes = naechstesLevel(jetzt.level)
                            ansicht = if (naechstes != null) Ansicht.Ueben(naechstes) else Ansicht.Reise
                        },
                        onZurReise = { ansicht = Ansicht.Reise },
                    )

                    is Ansicht.Schloss -> Elternschloss(
                        onGeoeffnet = { ansicht = Ansicht.Einstellungen },
                        onAbbrechen = { ansicht = Ansicht.Reise },
                    )

                    is Ansicht.Einstellungen -> EinstellungenBildschirm(
                        thema = fortschritt.thema,
                        genauigkeit = fortschritt.genauigkeit,
                        geschafft = fortschritt.geschafft,
                        sterne = fortschritt.sterne,
                        onThemaWechsel = { neu -> bereich.launch { speicher.setzeThema(neu) } },
                        onGenauigkeitWechsel = { neu -> bereich.launch { speicher.setzeGenauigkeit(neu) } },
                        onFortschrittZuruecksetzen = { bereich.launch { speicher.fortschrittZuruecksetzen() } },
                        onSchliessen = { ansicht = Ansicht.Reise },
                    )
                }
            }
        }
    }
}
