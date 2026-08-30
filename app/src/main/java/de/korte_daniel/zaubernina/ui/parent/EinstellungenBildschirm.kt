package de.korte_daniel.zaubernina.ui.parent

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.korte_daniel.zaubernina.data.BenutzerDaten
import de.korte_daniel.zaubernina.domain.Avatar
import de.korte_daniel.zaubernina.domain.Klasse
import de.korte_daniel.zaubernina.domain.Paket
import de.korte_daniel.zaubernina.domain.bereinigeWort
import de.korte_daniel.zaubernina.domain.bereinigeZeitlimit
import de.korte_daniel.zaubernina.domain.geuebteMinuten
import de.korte_daniel.zaubernina.domain.zeitlimitName
import de.korte_daniel.zaubernina.domain.woerterFuer
import de.korte_daniel.zaubernina.data.grundschrift.glyph
import de.korte_daniel.zaubernina.logic.Genauigkeit
import de.korte_daniel.zaubernina.ui.components.AvatarBild
import de.korte_daniel.zaubernina.ui.theme.LocalZauberSchrift
import de.korte_daniel.zaubernina.ui.theme.Thema
import de.korte_daniel.zaubernina.ui.theme.ZauberText
import de.korte_daniel.zaubernina.ui.theme.ZauberTheme

/**
 * Der Elternbereich. Liegt hinter dem [Elternschloss] und sieht absichtlich nüchtern aus —
 * kein Funkeln, keine großen Knöpfe. Was hier steht, soll ein Kind nicht anziehen.
 *
 * Hier ist der einzige Ort mit Systemtastatur (Name und eigene Wörter) — im Kindteil der
 * App gibt es keine.
 */
@Composable
fun EinstellungenBildschirm(
    aktiverBenutzer: BenutzerDaten,
    alleBenutzer: List<BenutzerDaten>,
    thema: Thema,
    genauigkeit: Genauigkeit,
    eigeneWoerter: List<String>,
    onThemaWechsel: (Thema) -> Unit,
    onGenauigkeitWechsel: (Genauigkeit) -> Unit,
    onPaketWechsel: (Paket) -> Unit,
    onKlasseWechsel: (Klasse) -> Unit,
    onKleinschreibungWechsel: (Boolean) -> Unit,
    onZeitlimitWechsel: (Int) -> Unit,
    onWortHinzu: (String) -> Unit,
    onWortWeg: (String) -> Unit,
    onBenutzerNeu: (String, Avatar) -> Unit,
    onBenutzerLoeschen: (Int) -> Unit,
    onFortschrittZuruecksetzen: () -> Unit,
    onSchliessen: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val farben = ZauberTheme.farben
    var loeschenBestaetigen by remember { mutableStateOf(false) }
    var neuerName by remember { mutableStateOf("") }
    var neuerAvatar by remember { mutableStateOf(Avatar.MOND) }
    var neuesWort by remember { mutableStateOf("") }
    var wortFehler by remember { mutableStateOf(false) }
    var limitEingabe by remember { mutableStateOf("") }
    var limitFehler by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp, vertical = 14.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().height(52.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            ZauberText("Elternbereich", 22.sp, farben.schrift, FontWeight.SemiBold)
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(farben.flaeche)
                    .clickable(onClick = onSchliessen),
                contentAlignment = Alignment.Center,
            ) {
                ZauberText("✕", 19.sp, farben.schrift)
            }
        }
        ZauberText(
            text = "Eingestellt wird gerade: ${aktiverBenutzer.benutzer.name}",
            groesse = 14.sp,
            farbe = farben.schriftSchwach,
        )

        // ───────── Wortpaket ─────────
        Abschnitt("WORTPAKET", farben.schriftSchwach)
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Paket.entries.forEach { paket ->
                val woerter = woerterFuer(paket, eigeneWoerter)
                Wahlzeile(
                    titel = paket.anzeigename,
                    beschreibung = "${paket.beschreibung} · ${woerter.size} Wörter",
                    gewaehlt = paket == aktiverBenutzer.paket,
                    onClick = { onPaketWechsel(paket) },
                )
            }
        }

        // ───────── Schreibweise ─────────
        Abschnitt("SCHREIBWEISE", farben.schriftSchwach)
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Wahlzeile(
                titel = "Große Buchstaben",
                beschreibung = "NINA — so fangen die meisten Erstklässler an",
                gewaehlt = !aktiverBenutzer.kleinschreibung,
                onClick = { onKleinschreibungWechsel(false) },
            )
            Wahlzeile(
                titel = "Groß und klein",
                beschreibung = "Nina — die Wörter werden normal geschrieben",
                gewaehlt = aktiverBenutzer.kleinschreibung,
                onClick = { onKleinschreibungWechsel(true) },
            )
        }

        // ───────── Zeitlimit ─────────
        Abschnitt("ZEITLIMIT", farben.schriftSchwach)
        ZauberText(
            text = "Minuten am Tag, 1 bis 240 — leer lassen heißt: kein Limit. " +
                "${aktiverBenutzer.benutzer.name} hat heute " +
                "${geuebteMinuten(aktiverBenutzer.heuteSekunden)} Minuten geübt.",
            groesse = 13.sp,
            farbe = farben.schriftSchwach,
            modifier = Modifier.padding(bottom = 8.dp),
        )
        Row(
            modifier = Modifier.fillMaxWidth()
                .clip(RoundedCornerShape(farben.ecke * 0.7f))
                .background(farben.flaeche)
                .padding(horizontal = 14.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ZauberText(
                text = "Aktuell: ${zeitlimitName(aktiverBenutzer.limitMinuten)}",
                groesse = 15.sp,
                farbe = farben.schrift,
                modifier = Modifier.weight(1f),
            )
            if (aktiverBenutzer.limitMinuten > 0) {
                ZauberText(
                    text = "aufheben",
                    groesse = 13.sp,
                    farbe = Color(0xFFE06A6A),
                    modifier = Modifier.clickable {
                        limitEingabe = ""
                        onZeitlimitWechsel(0)
                    },
                )
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(top = 8.dp),
        ) {
            Eingabefeld(
                wert = limitEingabe,
                hinweis = "Minuten, z. B. 20 …",
                onWert = { neu ->
                    // Nur Ziffern, drei Stellen reichen für 240.
                    limitEingabe = neu.filter { it.isDigit() }.take(3)
                    limitFehler = false
                },
                modifier = Modifier.weight(1f),
            )
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(farben.ecke * 0.7f))
                    .background(farben.akzent)
                    .clickable {
                        val minuten = bereinigeZeitlimit(limitEingabe)
                        if (minuten != null) {
                            onZeitlimitWechsel(minuten)
                            limitEingabe = ""
                            limitFehler = false
                        } else {
                            limitFehler = true
                        }
                    }
                    .padding(horizontal = 18.dp, vertical = 12.dp),
            ) {
                ZauberText("Setzen", 15.sp, farben.aufAkzent, FontWeight.SemiBold)
            }
        }
        if (limitFehler) {
            ZauberText(
                text = "Bitte eine Zahl von 1 bis 240 eingeben.",
                groesse = 13.sp,
                farbe = Color(0xFFE06A6A),
                modifier = Modifier.padding(top = 6.dp),
            )
        }

        // ───────── Eigene Wörter ─────────
        Abschnitt("EIGENE WÖRTER", farben.schriftSchwach)
        ZauberText(
            text = "Bis zu 10 Wörter, je höchstens 12 Buchstaben. Umlaute gehen, ß wird zu SS.",
            groesse = 13.sp,
            farbe = farben.schriftSchwach,
            modifier = Modifier.padding(bottom = 8.dp),
        )
        if (eigeneWoerter.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(bottom = 8.dp)) {
                eigeneWoerter.forEach { wort ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(farben.ecke * 0.7f))
                            .background(farben.flaeche)
                            .padding(horizontal = 14.dp, vertical = 9.dp),
                    ) {
                        ZauberText(wort, 16.sp, farben.schrift, modifier = Modifier.weight(1f))
                        ZauberText(
                            text = "entfernen",
                            groesse = 13.sp,
                            farbe = Color(0xFFE06A6A),
                            modifier = Modifier.clickable { onWortWeg(wort) },
                        )
                    }
                }
            }
        }
        if (eigeneWoerter.size < 10) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Eingabefeld(
                    wert = neuesWort,
                    hinweis = "Neues Wort …",
                    onWert = { neuesWort = it; wortFehler = false },
                    modifier = Modifier.weight(1f),
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(farben.ecke * 0.7f))
                        .background(farben.akzent)
                        .clickable {
                            val sauber = bereinigeWort(neuesWort) { glyph(it) != null }
                            if (sauber != null) {
                                onWortHinzu(sauber)
                                neuesWort = ""
                            } else {
                                wortFehler = true
                            }
                        }
                        .padding(horizontal = 18.dp, vertical = 12.dp),
                ) {
                    ZauberText("Dazu", 15.sp, farben.aufAkzent, FontWeight.SemiBold)
                }
            }
            if (wortFehler) {
                ZauberText(
                    text = "Das geht nicht — nur Buchstaben, höchstens 12.",
                    groesse = 13.sp,
                    farbe = Color(0xFFE06A6A),
                    modifier = Modifier.padding(top = 6.dp),
                )
            }
        }

        // ───────── Rechnen ─────────
        Abschnitt("RECHNEN", farben.schriftSchwach)
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Klasse.entries.forEach { klasse ->
                Wahlzeile(
                    titel = klasse.anzeigename,
                    beschreibung = klasse.beschreibung,
                    gewaehlt = klasse == aktiverBenutzer.klasse,
                    onClick = { onKlasseWechsel(klasse) },
                )
            }
        }

        // ───────── Benutzer ─────────
        Abschnitt("KINDER", farben.schriftSchwach)
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            alleBenutzer.forEach { daten ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(farben.ecke * 0.7f))
                        .background(farben.flaeche)
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                ) {
                    AvatarBild(daten.benutzer.avatar, farben.akzent, Modifier.size(26.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        ZauberText(daten.benutzer.name, 16.sp, farben.schrift, FontWeight.Medium)
                        ZauberText("${daten.sterne} Sterne", 12.sp, farben.schriftSchwach)
                    }
                    if (alleBenutzer.size > 1) {
                        ZauberText(
                            text = "löschen",
                            groesse = 13.sp,
                            farbe = Color(0xFFE06A6A),
                            modifier = Modifier.clickable { onBenutzerLoeschen(daten.benutzer.id) },
                        )
                    }
                }
            }
        }
        ZauberText(
            text = "Neues Kind:",
            groesse = 13.sp,
            farbe = farben.schriftSchwach,
            modifier = Modifier.padding(top = 12.dp, bottom = 6.dp),
        )
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Eingabefeld(
                wert = neuerName,
                hinweis = "Name …",
                onWert = { neuerName = it },
                modifier = Modifier.weight(1f),
            )
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(farben.ecke * 0.7f))
                    .background(farben.akzent)
                    .clickable {
                        if (neuerName.isNotBlank()) {
                            onBenutzerNeu(neuerName, neuerAvatar)
                            neuerName = ""
                        }
                    }
                    .padding(horizontal = 18.dp, vertical = 12.dp),
            ) {
                ZauberText("Anlegen", 15.sp, farben.aufAkzent, FontWeight.SemiBold)
            }
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(top = 10.dp),
        ) {
            Avatar.entries.forEach { avatar ->
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(if (avatar == neuerAvatar) farben.akzent.copy(alpha = 0.22f) else farben.flaeche)
                        .border(
                            width = 2.dp,
                            color = if (avatar == neuerAvatar) farben.akzent else Color.Transparent,
                            shape = CircleShape,
                        )
                        .clickable { neuerAvatar = avatar },
                    contentAlignment = Alignment.Center,
                ) {
                    AvatarBild(avatar, farben.akzent, Modifier.size(26.dp))
                }
            }
        }

        // ───────── Aussehen und Genauigkeit ─────────
        Abschnitt("AUSSEHEN", farben.schriftSchwach)
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Thema.entries.forEach { t ->
                Wahlzeile(
                    titel = t.anzeigename,
                    beschreibung = t.beschreibung,
                    gewaehlt = t == thema,
                    onClick = { onThemaWechsel(t) },
                )
            }
        }

        Abschnitt("GENAUIGKEIT", farben.schriftSchwach)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            listOf(
                Genauigkeit.LEICHT to "Leicht",
                Genauigkeit.NORMAL to "Normal",
                Genauigkeit.GENAU to "Genau",
            ).forEach { (wert, name) ->
                val aktiv = wert == genauigkeit
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .clip(RoundedCornerShape(farben.ecke))
                        .background(if (aktiv) farben.akzent else farben.flaeche)
                        .border(
                            width = if (farben.flaecheRand == Color.Transparent) 0.dp else 1.5.dp,
                            color = if (aktiv) Color.Transparent else farben.flaecheRand,
                            shape = RoundedCornerShape(farben.ecke),
                        )
                        .clickable { onGenauigkeitWechsel(wert) },
                    contentAlignment = Alignment.Center,
                ) {
                    ZauberText(name, 15.sp, if (aktiv) farben.aufAkzent else farben.schriftSchwach)
                }
            }
        }

        // ───────── Fortschritt ─────────
        Abschnitt("FORTSCHRITT VON ${aktiverBenutzer.benutzer.name.uppercase()}", farben.schriftSchwach)
        ZauberText(
            text = "${aktiverBenutzer.aktuellerStand.geschafft} Level im aktuellen Paket · " +
                "${aktiverBenutzer.sterne} Sterne · ${aktiverBenutzer.rechenRichtig} Rechenaufgaben",
            groesse = 14.sp,
            farbe = farben.schrift,
            modifier = Modifier.padding(bottom = 10.dp),
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .clip(RoundedCornerShape(farben.ecke))
                .border(1.5.dp, Color(0xFFB3382F).copy(alpha = 0.6f), RoundedCornerShape(farben.ecke))
                .clickable {
                    if (loeschenBestaetigen) {
                        onFortschrittZuruecksetzen()
                        loeschenBestaetigen = false
                    } else {
                        loeschenBestaetigen = true
                    }
                },
            contentAlignment = Alignment.Center,
        ) {
            ZauberText(
                text = if (loeschenBestaetigen) "Wirklich? Nochmal tippen." else "Fortschritt zurücksetzen",
                groesse = 15.sp,
                farbe = Color(0xFFE06A6A),
            )
        }

        Abschnitt("DATEN", farben.schriftSchwach)
        ZauberText(
            text = "Diese App hat keinen Internetzugang — die Berechtigung fehlt im Programm. " +
                "Sie kann nichts senden. Es werden keine Daten erhoben, gespeichert oder " +
                "übertragen. Keine Werbung, keine Käufe. Auch deshalb sind die Avatare " +
                "gemalte Bilder und keine Fotos.",
            groesse = 13.sp,
            farbe = farben.schriftSchwach,
            modifier = Modifier.padding(bottom = 12.dp),
        )

        // Namensnennung — die Lizenz der Tierbilder verlangt sie, und sie gehört
        // dorthin, wo Erwachsene lesen, nicht auf den Übungsbildschirm.
        Abschnitt("BILDER", farben.schriftSchwach)
        ZauberText(
            text = "Die Tiere zu den Buchstaben stammen aus dem OpenMoji-Katalog " +
                "(openmoji.org) der Hochschule für Gestaltung Schwäbisch Gmünd und stehen " +
                "unter der Lizenz CC BY-SA 4.0. Sie liegen als Zeichnungen in der App und " +
                "werden nicht nachgeladen.",
            groesse = 13.sp,
            farbe = farben.schriftSchwach,
            modifier = Modifier.padding(bottom = 16.dp),
        )
    }
}

@Composable
private fun Abschnitt(titel: String, farbe: Color) {
    ZauberText(
        text = titel,
        groesse = 13.sp,
        farbe = farbe,
        gewicht = FontWeight.Medium,
        modifier = Modifier.padding(top = 26.dp, bottom = 6.dp),
    )
}

@Composable
private fun Eingabefeld(wert: String, hinweis: String, onWert: (String) -> Unit, modifier: Modifier = Modifier) {
    val farben = ZauberTheme.farben
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(farben.ecke * 0.7f))
            .background(farben.flaeche)
            .border(1.5.dp, farben.schrift.copy(alpha = 0.15f), RoundedCornerShape(farben.ecke * 0.7f))
            .padding(horizontal = 14.dp, vertical = 12.dp),
    ) {
        if (wert.isEmpty()) {
            ZauberText(hinweis, 15.sp, farben.schriftSchwach.copy(alpha = 0.6f))
        }
        BasicTextField(
            value = wert,
            onValueChange = onWert,
            singleLine = true,
            textStyle = TextStyle(
                color = farben.schrift,
                fontSize = 15.sp,
                fontFamily = LocalZauberSchrift.current,
            ),
            cursorBrush = SolidColor(farben.akzent),
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun Wahlzeile(titel: String, beschreibung: String, gewaehlt: Boolean, onClick: () -> Unit) {
    val farben = ZauberTheme.farben
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(farben.ecke))
            .background(farben.flaeche)
            .border(
                width = 2.dp,
                color = if (gewaehlt) farben.akzent else Color.Transparent,
                shape = RoundedCornerShape(farben.ecke),
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 13.dp),
    ) {
        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(CircleShape)
                .background(if (gewaehlt) farben.akzent else Color.Transparent)
                .border(2.dp, if (gewaehlt) farben.akzent else farben.schriftSchwach, CircleShape),
        )
        Column(modifier = Modifier.weight(1f)) {
            ZauberText(titel, 17.sp, farben.schrift, FontWeight.Medium)
            ZauberText(beschreibung, 13.sp, farben.schriftSchwach, modifier = Modifier.padding(top = 2.dp))
        }
    }
}
