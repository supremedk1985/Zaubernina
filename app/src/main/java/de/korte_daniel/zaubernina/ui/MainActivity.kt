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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.platform.LocalContext
import de.korte_daniel.zaubernina.data.FortschrittSpeicher
import de.korte_daniel.zaubernina.data.Zustand
import de.korte_daniel.zaubernina.domain.ALPHABET
import de.korte_daniel.zaubernina.domain.alphabetBuchstabe
import de.korte_daniel.zaubernina.domain.alphabetRundeVoll
import de.korte_daniel.zaubernina.domain.anzeigeWort
import de.korte_daniel.zaubernina.domain.levelFuer
import de.korte_daniel.zaubernina.domain.sterneFuerAlphabetRunde
import de.korte_daniel.zaubernina.domain.sterneFuerZahl
import de.korte_daniel.zaubernina.domain.woerterFuer
import de.korte_daniel.zaubernina.domain.zeitVorbei
import de.korte_daniel.zaubernina.domain.zifferFuer
import de.korte_daniel.zaubernina.ui.level.AlphabetFertigBildschirm
import de.korte_daniel.zaubernina.ui.level.GeschafftBildschirm
import de.korte_daniel.zaubernina.ui.level.ReiseBildschirm
import de.korte_daniel.zaubernina.ui.level.ZeitVorbeiBildschirm
import de.korte_daniel.zaubernina.ui.level.ZaehlBildschirm
import de.korte_daniel.zaubernina.ui.parent.EinstellungenBildschirm
import de.korte_daniel.zaubernina.ui.parent.Elternschloss
import de.korte_daniel.zaubernina.ui.rechnen.RechnenBildschirm
import de.korte_daniel.zaubernina.ui.start.StartBildschirm
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
    /** Wer übt heute? Immer der erste Bildschirm — Daniels Vorgabe. */
    data object Start : Ansicht
    data object Reise : Ansicht
    data class Ueben(val level: Int) : Ansicht
    data class Geschafft(val level: Int, val neueSterne: Int) : Ansicht

    /** Eine Zwischendurch-Zahl schreiben, dann zählen. */
    data class Zahl(val level: Int) : Ansicht
    data class Zaehlen(val level: Int, val bonusStern: Boolean) : Ansicht

    /** Der ABC-Stern: die laufende Runde durchs Alphabet. */
    data object Alphabet : Ansicht
    data class AlphabetFertig(val neueSterne: Int) : Ansicht

    /** Der Rechenmodus. */
    data object Rechnen : Ansicht

    /** Die Rechenaufgabe vor dem Elternbereich. */
    data object Schloss : Ansicht
    data object Einstellungen : Ansicht
}

@Composable
private fun Zaubernina() {
    val context = LocalContext.current
    val speicher = remember { FortschrittSpeicher(context) }
    val bereich = rememberCoroutineScope()
    val zustand by speicher.zustand.collectAsState(initial = Zustand())

    LaunchedEffect(Unit) { speicher.stelleErstenBenutzerSicher() }

    // Eine Sprachausgabe für die ganze App: so lebt sie über Bildschirmwechsel hinweg
    // weiter und ein angefangener Buchstabe wird nicht mitten im Wort abgeschnitten.
    val vorleser = rememberVorleser()

    var ansicht by remember { mutableStateOf<Ansicht>(Ansicht.Start) }
    var aktiverId by remember { mutableStateOf<Int?>(null) }

    // Der aktive Benutzer und seine Reise. Solange die Ablage noch lädt oder niemand
    // gewählt ist, bleibt die App auf dem Startbildschirm.
    val aktiver = aktiverId?.let { zustand.daten(it) }

    // ───────── Zeitlimit ─────────
    // Gezählt wird nur, während ein Kind angemeldet ist und in seinem Teil der App
    // steckt — Startbildschirm, Rechenschloss und Elternbereich zählen nicht. Der
    // Zehn-Sekunden-Takt hält die Schreiblast klein; mehr als zehn Sekunden können
    // beim Beenden also nicht verloren gehen.
    val zaehltGerade = aktiver != null &&
        ansicht !is Ansicht.Start && ansicht !is Ansicht.Schloss && ansicht !is Ansicht.Einstellungen
    val lebenszyklus = androidx.compose.ui.platform.LocalLifecycleOwner.current
    LaunchedEffect(aktiverId, zaehltGerade) {
        if (!zaehltGerade) return@LaunchedEffect
        val id = aktiverId ?: return@LaunchedEffect
        while (true) {
            kotlinx.coroutines.delay(10_000)
            // Im Hintergrund (Home-Taste, Bildschirm aus) steht die Uhr.
            if (lebenszyklus.lifecycle.currentState.isAtLeast(androidx.lifecycle.Lifecycle.State.RESUMED)) {
                speicher.zaehleUebungszeit(id, 10)
            }
        }
    }
    val limitErreicht = aktiver != null && zeitVorbei(aktiver.limitMinuten, aktiver.heuteSekunden)
    val woerter = aktiver?.let { woerterFuer(it.paket, it.eigeneWoerter) } ?: emptyList()
    // Die Level tragen das Wort gleich in der gewünschten Schreibweise — Reise, Üben
    // und Jubel zeigen es dann überall gleich. Gezählt wird nur die Levelnummer, der
    // Fortschritt bleibt beim Umschalten also erhalten.
    val levelListe = levelFuer(woerter.map { anzeigeWort(it, aktiver?.kleinschreibung == true) })
    val stand = aktiver?.aktuellerStand

    ZauberTheme(zustand.thema) {
        val farben = ZauberTheme.farben
        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawBehind { drawRect(farben.hintergrundPinsel(size)) },
        ) {
            Box(modifier = Modifier.fillMaxSize().systemBarsPadding()) {
                val gesperrt = limitErreicht && aktiver != null &&
                    ansicht !is Ansicht.Start && ansicht !is Ansicht.Schloss && ansicht !is Ansicht.Einstellungen
                if (gesperrt) {
                    ZeitVorbeiBildschirm(
                        name = aktiver!!.benutzer.name,
                        avatar = aktiver.benutzer.avatar,
                        onZurKinderwahl = {
                            aktiverId = null
                            ansicht = Ansicht.Start
                        },
                        onElternbereich = { ansicht = Ansicht.Schloss },
                        vorleser = vorleser,
                    )
                } else when (val jetzt = ansicht) {
                    is Ansicht.Start -> StartBildschirm(
                        benutzer = zustand.benutzer,
                        onWaehlen = { id ->
                            aktiverId = id
                            ansicht = Ansicht.Reise
                        },
                    )

                    is Ansicht.Reise -> {
                        if (aktiver == null || stand == null) {
                            ansicht = Ansicht.Start
                        } else {
                            ReiseBildschirm(
                                level = levelListe,
                                geschafft = stand.geschafft,
                                zahlen = stand.zahlen,
                                sterne = aktiver.sterne,
                                benutzerName = aktiver.benutzer.name,
                                avatar = aktiver.benutzer.avatar,
                                alphabetIndex = aktiver.alphabetIndex,
                                alphabetRunden = aktiver.alphabetRunden,
                                onLevelWaehlen = { ansicht = Ansicht.Ueben(it) },
                                onZahlWaehlen = { ansicht = Ansicht.Zahl(it) },
                                onAlphabet = { ansicht = Ansicht.Alphabet },
                                onElternbereich = { ansicht = Ansicht.Schloss },
                                onRechnen = { ansicht = Ansicht.Rechnen },
                                onBenutzerWechsel = {
                                    aktiverId = null
                                    ansicht = Ansicht.Start
                                },
                            )
                        }
                    }

                    is Ansicht.Ueben -> {
                        val level = levelListe.getOrNull(jetzt.level)
                        if (level == null || aktiver == null || stand == null) {
                            ansicht = Ansicht.Reise
                        } else {
                            UebungsBildschirm(
                                wort = level.wort,
                                kopfzeile = { i -> "Level ${level.nummer} · Buchstabe ${i + 1} von ${level.wort.length}" },
                                genauigkeit = zustand.genauigkeit,
                                vorleser = vorleser,
                                onZurueck = { ansicht = Ansicht.Reise },
                                onFertig = {
                                    val neueSterne = if (jetzt.level == stand.geschafft) 3 else 0
                                    bereich.launch {
                                        speicher.levelGeschafft(aktiver.benutzer.id, aktiver.paket, jetzt.level, levelListe.size)
                                    }
                                    ansicht = Ansicht.Geschafft(jetzt.level, neueSterne)
                                },
                            )
                        }
                    }

                    // Die Zahlen leben in ihrer eigenen Farbwelt — lokal übergelegt,
                    // das globale Thema bleibt unangetastet (Muster aus CurruBike).
                    is Ansicht.Zahl -> CompositionLocalProvider(
                        LocalZauberFarben provides farben.fuerZahlen(),
                    ) {
                        UebungsBildschirm(
                            wort = "${zifferFuer(jetzt.level)}",
                            kopfzeile = { "Zauberzahl" },
                            genauigkeit = zustand.genauigkeit,
                            vorleser = vorleser,
                            onZurueck = { ansicht = Ansicht.Reise },
                            onFertig = {
                                val bonus = stand != null && sterneFuerZahl(stand.zahlen, jetzt.level) > 0
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
                                aktiver?.let { a ->
                                    bereich.launch { speicher.zahlGeschrieben(a.benutzer.id, a.paket, jetzt.level) }
                                }
                                ansicht = Ansicht.Reise
                            },
                        )
                    }

                    is Ansicht.Alphabet -> {
                        if (aktiver == null) {
                            ansicht = Ansicht.Start
                        } else {
                            val index = aktiver.alphabetIndex
                            UebungsBildschirm(
                                wort = alphabetBuchstabe(index).toString(),
                                kopfzeile = { "Das ABC · Buchstabe ${index + 1} von ${ALPHABET.size}" },
                                genauigkeit = zustand.genauigkeit,
                                vorleser = vorleser,
                                onZurueck = { ansicht = Ansicht.Reise },
                                onFertig = {
                                    val rundeVoll = alphabetRundeVoll(index)
                                    val bonus = if (rundeVoll) sterneFuerAlphabetRunde(aktiver.alphabetRunden) else 0
                                    bereich.launch { speicher.alphabetBuchstabeGeschrieben(aktiver.benutzer.id) }
                                    // Bleiben: der nächste Buchstabe kommt von selbst, weil
                                    // alphabetIndex im Zustand weiterwandert. Nur die volle
                                    // Runde bekommt ihren eigenen Auftritt.
                                    if (rundeVoll) ansicht = Ansicht.AlphabetFertig(bonus)
                                },
                                jubelLetzter = "Toll gemacht!",
                                knopfLetzter = "Weiter",
                            )
                        }
                    }

                    is Ansicht.AlphabetFertig -> AlphabetFertigBildschirm(
                        neueSterne = jetzt.neueSterne,
                        onZurReise = { ansicht = Ansicht.Reise },
                    )

                    is Ansicht.Rechnen -> CompositionLocalProvider(
                        LocalZauberFarben provides farben.fuerZahlen(),
                    ) {
                        if (aktiver == null) {
                            ansicht = Ansicht.Start
                        } else {
                            RechnenBildschirm(
                                klasse = aktiver.klasse,
                                genauigkeit = zustand.genauigkeit,
                                richtigBisher = aktiver.rechenRichtig,
                                onRichtig = { bereich.launch { speicher.rechenaufgabeRichtig(aktiver.benutzer.id) } },
                                onZurueck = { ansicht = Ansicht.Reise },
                            )
                        }
                    }

                    is Ansicht.Geschafft -> GeschafftBildschirm(
                        levelListe = levelListe,
                        levelIndex = jetzt.level,
                        neueSterne = jetzt.neueSterne,
                        onWeiter = {
                            val naechstes = (jetzt.level + 1).takeIf { it <= levelListe.lastIndex }
                            ansicht = if (naechstes != null) Ansicht.Ueben(naechstes) else Ansicht.Reise
                        },
                        onZurReise = { ansicht = Ansicht.Reise },
                    )

                    is Ansicht.Schloss -> Elternschloss(
                        onGeoeffnet = { ansicht = Ansicht.Einstellungen },
                        onAbbrechen = { ansicht = Ansicht.Reise },
                    )

                    is Ansicht.Einstellungen -> {
                        if (aktiver == null) {
                            ansicht = Ansicht.Start
                        } else {
                            EinstellungenBildschirm(
                                aktiverBenutzer = aktiver,
                                alleBenutzer = zustand.benutzer,
                                thema = zustand.thema,
                                genauigkeit = zustand.genauigkeit,
                                eigeneWoerter = aktiver.eigeneWoerter,
                                onThemaWechsel = { neu -> bereich.launch { speicher.setzeThema(neu) } },
                                onGenauigkeitWechsel = { neu -> bereich.launch { speicher.setzeGenauigkeit(neu) } },
                                onPaketWechsel = { neu -> bereich.launch { speicher.setzePaket(aktiver.benutzer.id, neu) } },
                                onKlasseWechsel = { neu -> bereich.launch { speicher.setzeKlasse(aktiver.benutzer.id, neu) } },
                                onKleinschreibungWechsel = { neu -> bereich.launch { speicher.setzeKleinschreibung(aktiver.benutzer.id, neu) } },
                                onZeitlimitWechsel = { neu -> bereich.launch { speicher.setzeZeitlimit(aktiver.benutzer.id, neu) } },
                                onWortHinzu = { wort ->
                                    bereich.launch { speicher.setzeEigeneWoerter(aktiver.benutzer.id, aktiver.eigeneWoerter + wort) }
                                },
                                onWortWeg = { wort ->
                                    bereich.launch { speicher.setzeEigeneWoerter(aktiver.benutzer.id, aktiver.eigeneWoerter - wort) }
                                },
                                onBenutzerNeu = { name, avatar ->
                                    bereich.launch { speicher.benutzerAnlegen(name, avatar) }
                                },
                                onBenutzerLoeschen = { id ->
                                    bereich.launch { speicher.benutzerLoeschen(id) }
                                    if (id == aktiverId) {
                                        aktiverId = null
                                        ansicht = Ansicht.Start
                                    }
                                },
                                onFortschrittZuruecksetzen = {
                                    bereich.launch { speicher.fortschrittZuruecksetzen(aktiver.benutzer.id) }
                                },
                                onSchliessen = { ansicht = Ansicht.Reise },
                            )
                        }
                    }
                }
            }
        }
    }
}
