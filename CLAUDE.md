# Zaubernina — Lern-App für Nina (5) und Lea (7)

Kotlin / Jetpack Compose, **ohne** Material3, Navigation-Compose, Room, Hilt. Paket
`de.korte_daniel.zaubernina`. Alle Bezeichner, Kommentare und Texte auf Deutsch, mit echten
Umlauten. Feste Download-Adresse im Heimnetz: http://192.168.178.10/zaubernina.apk
(Gradle-Task nach `assembleMinified`). Repo: GitHub `supremedk1985/Zaubernina` (privat).

## Konzept (Stand 2026-09-03, Daniels Auftrag)

Aus der Schreiblern-App für Nina wird eine Lern-App für **beide Töchter**: Nina (5, Vorschule)
und Lea (7, 1./2. Klasse, **Druckschrift**, Mathe fällt ihr schwer). Anspruch: *pädagogisch
wertvoll und doch modern* — bewusst anders als Anton und Co.

**Ablauf für das Kind:** Start → Kind wählen → **Startseite mit Spiel-Kacheln** (welche, hängt
an der Klassenstufe) → ein Spiel → zurück zur Startseite. Jede Kachel spricht ihren Namen,
wenn man sie antippt — für Kinder, die noch nicht lesen.

| Spiel | für wen | Kern |
|---|---|---|
| Schreiben | alle | Buchstaben, Zahlen, Wörter nachfahren (die bisherige Levelreise) |
| Hören (Anlaute) | Vorschule | „Womit fängt das Wort an?" — Buchstabe + drei Anlauttiere |
| Zählen / Rechnen | alle | Vorschule: Mengen zählen; 1./2. Klasse: plus/minus mit **Zahlenstrahl** als Hilfe, Zehnerübergang |
| Lesen | 1./2. Klasse | Silbenwörter (zweifarbig) lesen, Bild finden; Wort antippen = vorlesen |
| Sprachen | 1./2. Klasse | Englisch und Spanisch: erst hören + Bild tippen, dann Wort + Bild, dann kurze Sätze (Muster Lingokids / Duolingo ABC, ohne Lesezwang) |
| Geschichte | alle | **Vorlesegeschichte**: Lea liest der Familie vor. Auswahl über drei **Zahnräder** (Held-Tier, Thema, Länge). Große Druckschrift, Silben zweifarbig, Wort antippen = Hilfe. Jede Seite hat ein Bild (Emoji-Szene) |

**Geschichten (Weg 1, Daniels Entscheidung):** eine **vorgefertigte, kuratierte Bibliothek**
in der App — kein Netz, jede Geschichte von Daniel gesehen. Texte stehen in
`werkzeuge/geschichten_erzeugen.py` und werden nach `data/Geschichten.kt` erzeugt (typsicher,
kein JSON-Parser). Je Geschichte 12 Seiten mit Haltepunkten nach 4 und 8 → drei Längen aus einer
Geschichte. Lieblingstiere: **Pinguin (Lea), Robbe (Nina)**. Ein „Neue Geschichte holen" über
nexus (lokale KI oder Claude) ist als **spätere, freiwillige** Erweiterung gedacht — mit
Elternfreigabe vor dem ersten Vorlesen.

**Elternbereich:** Einblick statt Levelzahlen — was klappt, was hakt (Fehler je Aufgabe, Wörter
mit Vorlesehilfe), Lernwörter der Schule eintragen (Paket „Eigene"), Sprache wählen,
Silbenfärbung an/aus, Zeitlimit. Hinter dem Einmaleins-Schloss.

## Leitplanken — gelten für JEDE Änderung

### 1. Pädagogischer Standard
- **Kurze Einheiten, kein Sog.** Sterne alle fünf richtigen Aufgaben, kein Timer, keine
  Streaks, keine Strafe für Pausen. Ein Kind darf jederzeit aufhören, ohne etwas zu verlieren.
- **Fehler sind der Lernmoment.** Falsch heißt: freundlicher Hinweis, Aufgabe bleibt, zweiter
  Versuch. Nach dem zweiten Fehler wird die Lösung gezeigt UND vorgelesen. Nie Rot, nie „X",
  nie Zähler für Fehler, den das Kind sieht.
- **Anpassung ohne Beschämung.** Klappt etwas dreimal in Folge, wird es schwerer; hakt es, wird
  es leichter — still, ohne Anzeige eines „Abstiegs".
- **Schule ist der Maßstab.** Druckschrift wie im Schreiblehrgang, Silbenfärbung wie in der
  Fibel, Zahlenraum nach Klassenstufe. Daniel sieht die Schulhefte, wir nicht — seine
  Einsprüche zu Strichfolgen, Wortwahl und Schwierigkeit haben immer Vorrang.
- **Altersgerechte Sprache.** Sätze 5–9 Wörter, Wörter der 1./2. Klasse, wörtliche Rede,
  keine Ironie, keine Angst-Szenen ohne Auflösung, jede Geschichte endet gut.
- **Eltern sehen Echtes**, nicht Punkte: welche Buchstaben, Wörter, Aufgaben Mühe machen.
- **Nichts Fremdes im Kinderbereich:** keine Werbung, kein Netz, keine Links, keine Käufe.

### 2. Tablet UND Handy
- Größen NIE als feste Zahl in neuen Bildschirmen: `ZauberMasse.aktuell.sp(..)`/`.dp(..)` aus
  `ui/theme/Masse.kt`. Tablet (≥ 600 dp) skaliert Schrift und Abstände mit 1,2–1,35 und gibt
  Rastern eine Spalte mehr (`masse.spalten`).
- Antippbare Flächen mindestens 56 dp (Kinderfinger), Abstände zwischen Antworten ≥ 12 dp.
- Beide Ausrichtungen müssen funktionieren (die Activity behandelt Rotation selbst).
- **Prüfen auf beiden**: Emulator-Profil Handy (1080×2400) UND einmal breit (Tablet-Profil oder
  `wm size 1600x2560` + `wm density 320`), jedes neue Spiel, vor jedem Commit mit UI-Änderung.

### 3. Kinderfreundliche Bedienung
- **Zurück immer oben links**, rund, gleich aussehend (`ZurueckKnopf`). Eine Hauptaktion pro
  Bildschirm, groß, unten (`GrosserKnopf`).
- **Höchstens drei Antwortmöglichkeiten**, groß, mit Bild. Text nur dort, wo Lesen die Übung ist.
- **Alles Wichtige wird gesprochen**: Kachelnamen beim Antippen, Aufgabenstellung beim Erscheinen,
  Lösung nach dem zweiten Fehler. Vorlesen ist kein Extra, sondern der Zugang für Nichtleser.
- **Keine Systemtastatur im Kinderbereich.** Eingaben sind Tippen, Ziehen, Nachfahren.
- **Rückmeldung freundlich und sofort**: richtig = Funken/Stern + kurzer Ton der Stimme („Genau!"),
  falsch = Kachel wackelt/verblasst, Stimme sagt den Hinweis.
- **Kein Zeitdruck** auf irgendeinem Bildschirm; Tages-Zeitlimit ist Elternsache.
- **Emoji statt Fotos** (Systemschrift, überall verständlich, keine Berechtigung); Vektoren aus
  OpenMoji für die Anlauttiere (Lizenz-Nennung im Elternbereich).

### 4. Technische Regeln
- **Keine einzige Berechtigung im Manifest** — auch kein INTERNET. Wer das ändert, ändert die App.
- Texte mit Silbentrenner `|` nur in der Python-Quelle; `data/Geschichten.kt` wird erzeugt, nie
  von Hand geändert. Nach jeder Textänderung Generator laufen lassen (er prüft Seitenzahl,
  Satzlänge, Anführungszeichen).
- Reine Logik (Aufgaben-Erzeugung, Silben, Fortschrittsregeln) ohne Android-Typen in
  `logic/`/`domain/` — mit JUnit-Test. UI in `ui/<spiel>/`.
- Fortschritt je Kind im DataStore (`data/Fortschritt.kt`): neue Spiele bekommen Zähler über
  `spielRichtig`/`spielFehler`, keine eigenen Sonderwege.
- Bauen und Emulator **nie gleichzeitig** (RAM; am 03.09.2026 legte das das Haus-DNS lahm).
  Reihenfolge: `./gradlew assembleDebug` → `./gradlew --stop` → `emulator-currubike start` →
  `emulator-currubike app app/build/outputs/apk/debug/app-debug.apk`. Das Handy bekommt nur
  `assembleMinified` (veröffentlicht automatisch).
- Fertige Stände committen und pushen — das Home von nexus ist nicht gesichert.

### 5. Durchspielen vor jedem Release (Prüfliste)
1. Kind wählen → Startseite: richtige Kacheln je Klassenstufe, jede spricht beim Antippen.
2. Jedes Spiel: eine Runde richtig, eine falsch (Hinweis, zweiter Versuch, Lösung), Zurück.
3. Geschichte: Zahnräder drehen, jede Länge, Wort antippen (Vorlesen), Ende mit Stern.
4. Elternbereich: Einblick zeigt die eben gemachten Fehler/Hilfen; Zurücksetzen wirkt.
5. Einmal im breiten Layout (Tablet) alle Startseiten-Kacheln und ein Spiel ansehen.
6. `adb logcat -b crash` leer; Unit-Tests grün.
