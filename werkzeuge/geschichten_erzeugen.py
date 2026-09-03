#!/usr/bin/env python3
"""Erzeugt data/Geschichten.kt (Kotlin, typsicher, ohne JSON-Parser) aus den Texten unten.

Regeln für die Texte:
- Silben mit | trennen (Sprechsilben, wie in Fibeln: Pin|gu|in, Rob|be, schwim|men).
- Anführungszeichen „so“ — nie das ASCII-Zeichen ".
- Je Geschichte 12 Seiten, Haltepunkte nach Seite 4 und 8 (kurz/mittel/lang).
- Sätze kurz (5–9 Wörter), Wörter der 1./2. Klasse, viel wörtliche Rede.
Aufruf: python3 werkzeuge/geschichten_erzeugen.py   (schreibt die Kotlin-Datei neu; danach bauen)
"""
import json, os, re, sys

ZIEL = os.path.join(os.path.dirname(__file__), "..", "app", "src", "main", "java", "de", "korte_daniel", "zaubernina", "data", "Geschichten.kt")

GESCHICHTEN = []

def geschichte(id, titel, tier, thema, held, seiten, haltepunkte=(4, 8), bilder=None):
    """bilder: je Seite eine kleine Emoji-Szene (2-4 Zeichen). Gezeichnet statt fotografiert,
    ohne Netz - und für ein Kind sofort lesbar: Pinguin + Fisch + Wind = die Seite vom Wind."""
    assert bilder is None or len(bilder) == len(seiten), id
    GESCHICHTEN.append(dict(id=id, titel=titel, tier=tier, thema=thema, held=held,
                            haltepunkte=list(haltepunkte), seiten=[s.strip("\n") for s in seiten],
                            bilder=bilder or [""] * len(seiten)))

geschichte("pinguin-abenteuer", "Pi|a und der ver|lo|re|ne Fisch", "pinguin", "abenteuer", "Pia", [
"""Pi|a ist ein klei|ner Pin|gu|in.
Sie wohnt am Rand vom Eis.
Je|den Mor|gen springt sie ins Was|ser.
Das Was|ser ist kalt. Pi|a mag das.""",
"""Heu|te fängt Pi|a ei|nen Fisch.
Er ist sil|bern und glänzt.
„Den brin|ge ich Ma|ma“, sagt Pi|a.
Sie legt den Fisch auf das Eis.""",
"""Da kommt ein Wind.
Der Wind ist stark.
Er schiebt den Fisch ü|ber das Eis.
Der Fisch rutscht und rutscht.
„Halt!“, ruft Pi|a.""",
"""Pi|a läuft dem Fisch nach.
Sie wat|schelt so schnell sie kann.
Doch der Fisch ist weg.
Nur ei|ne Spur ist noch da.
Pi|a folgt der Spur.""",
"""Die Spur führt zu ei|nem Berg aus Schnee.
O|ben sitzt ei|ne Mö|we.
„Hast du mei|nen Fisch ge|se|hen?“, fragt Pi|a.
Die Mö|we schüt|telt den Kopf.
„Nein. A|ber ich hel|fe dir su|chen.“""",
"""Die Mö|we fliegt hoch.
Sie schaut nach links und nach rechts.
„Da un|ten!“, ruft sie.
„Bei den gro|ßen Stei|nen!“
Pi|a rennt los.""",
"""Bei den Stei|nen liegt der Fisch.
A|ber er ist nicht al|lein.
Ei|ne klei|ne Rob|be hält ihn fest.
Sie zit|tert.
„Ich ha|be so Hun|ger“, sagt die Rob|be.""",
"""Pi|a ist erst wü|tend.
Dann sieht sie die Rob|be an.
Die Rob|be ist noch klei|ner als sie.
„Wie heißt du?“, fragt Pi|a.
„Ron|ja“, sagt die Rob|be lei|se.""",
"""Pi|a denkt nach.
Ma|ma war|tet auf den Fisch.
A|ber Ron|ja hat Hun|ger. Jetzt.
„Iss ihn“, sagt Pi|a.
„Ich fan|ge ei|nen neu|en.“""",
"""Ron|ja isst den Fisch.
Dann lä|chelt sie.
„Dan|ke, Pi|a. Ich kom|me mit.
Zu zweit fängt man mehr.“
Sie sprin|gen zu|sam|men ins Was|ser.""",
"""Und wirk|lich: Sie fan|gen drei Fi|sche.
Ei|nen für Ma|ma.
Ei|nen für Pa|pa.
Und ei|nen für die Mö|we,
die so gut ge|sucht hat.""",
"""Am A|bend sitzt Pi|a bei Ma|ma.
„Ich ha|be den Fisch ver|lo|ren“, sagt sie.
„A|ber ich ha|be ei|ne Freun|din ge|fun|den.“
Ma|ma lacht.
„Das ist mehr als ein Fisch.“
En|de.""",
], bilder=["🐧❄️🌊", "🐧🐟", "💨🐟❄️", "🐧👣❄️", "🐧⛰️🕊️", "🕊️👀🪨", "🪨🐟🦭", "🐧🦭", "🐧🤔🐟", "🐧🦭🌊", "🐟🐟🐟", "🐧🐧🌙"])

geschichte("robbe-abenteuer", "Ron|ja und die Rei|se zur Eis|in|sel", "robbe", "abenteuer", "Ronja", [
"""Ron|ja ist ei|ne klei|ne Rob|be.
Sie hat ein wei|ches, grau|es Fell.
Am liebs|ten liegt sie auf ei|nem Fel|sen.
Dort ist es warm.""",
"""Weit drau|ßen im Meer liegt ei|ne In|sel.
Sie ist ganz aus Eis.
„Da will ich hin“, sagt Ron|ja.
„Das ist zu weit“, sagt Ma|ma.
Ron|ja will es trotz|dem.""",
"""Am Mor|gen schwimmt Ron|ja los.
Das Was|ser ist tief und blau.
Sie taucht und schnauft.
Die In|sel wird grö|ßer.
A|ber Ron|ja wird mü|de.""",
"""Da kommt ein Brett ge|schwom|men.
Es ist aus Holz.
Ron|ja klet|tert dar|auf.
„Puh“, sagt sie.
Das Brett trägt sie ein Stück.""",
"""Plötz|lich wird das Meer un|ru|hig.
Ei|ne gro|ße Wel|le kommt.
Das Brett wa|ckelt.
Ron|ja hält sich fest.
Die Wel|le trägt sie hoch und run|ter.""",
"""Dann ist es wie|der still.
Ron|ja schaut sich um.
Die In|sel ist ganz nah!
Sie springt ins Was|ser.
Mit drei Stö|ßen ist sie da.""",
"""Die In|sel glit|zert.
Ü|ber|all ist Eis.
Und ü|ber|all sind Pin|gu|i|ne.
Ein klei|ner Pin|gu|in kommt nä|her.
„Wer bist du?“, fragt er.""",
"""„Ich bin Ron|ja. Ich kom|me von weit her.“
„Ich bin Pit“, sagt der Pin|gu|in.
„Willst du mit uns rut|schen?“
Ron|ja nickt.
Sie rutscht auf dem Bauch den Berg run|ter. Ju|hu!""",
"""Sie rut|schen den gan|zen Tag.
Dann wird es dun|kel.
Ron|ja denkt an Ma|ma.
„Ich muss nach Hau|se“, sagt sie.
„A|ber der Weg ist so weit.“""",
"""Pit ruft die an|de|ren Pin|gu|i|ne.
„Wir brin|gen dich!“
Al|le sprin|gen ins Was|ser.
Sie schwim|men im Kreis um Ron|ja.
So ist sie nie al|lein.""",
"""Die Ster|ne zei|gen den Weg.
Ron|ja schwimmt in der Mit|te.
Sie ist gar nicht mehr mü|de.
Da vorn ist ihr Fel|sen!
Und da sitzt Ma|ma.""",
"""Ma|ma drückt Ron|ja ganz fest.
„Du warst weg“, sagt sie.
„Ja“, sagt Ron|ja.
„A|ber ich ha|be Freun|de mit|ge|bracht.“
Die Pin|gu|i|ne win|ken.
En|de.""",
], bilder=["🦭🪨☀️", "🏝️❄️🌊", "🦭🌊💦", "🪵🦭🌊", "🌊🌊🪵", "🦭🏝️", "❄️🐧🐧🐧", "🦭🐧⛷️", "🌙🦭🐧", "🐧🐧🦭🐧", "⭐🌊🪨", "🦭🦭🐧"])

geschichte("pinguin-gutenacht", "Pi|a kann nicht schla|fen", "pinguin", "gutenacht", "Pia", [
"""Es ist A|bend am Eis.
Die Son|ne geht un|ter.
Al|le Pin|gu|i|ne ste|hen dicht zu|sam|men.
So bleibt es warm.""",
"""Pi|a steht bei Ma|ma und Pa|pa.
Sie soll schla|fen.
A|ber ih|re Au|gen wol|len nicht zu.
„Ich bin nicht mü|de“, sagt sie.""",
"""„Dann hör zu“, sagt Pa|pa lei|se.
„Hörst du das Meer?“
Pi|a hört hin.
Das Meer macht: Schhh. Schhh.
Wie ein Lied.""",
"""„Hörst du den Wind?“, fragt Ma|ma.
Der Wind pfeift ganz sanft.
Er streicht ü|ber Pi|as Fe|dern.
Pi|a gähnt.
A|ber die Au|gen blei|ben of|fen.""",
"""Da kommt die Mö|we vor|bei.
Sie setzt sich auf ei|nen Stein.
„Du bist ja noch wach“, sagt sie.
„Soll ich dir et|was zei|gen?“
Pi|a nickt.""",
"""Die Mö|we zeigt nach o|ben.
Der Him|mel ist dun|kel|blau.
Und ü|ber|all sind Ster|ne.
Klei|ne und gro|ße.
„Zähl sie“, sagt die Mö|we.""",
"""Pi|a zählt.
Eins, zwei, drei, vier, fünf.
Sechs, sie|ben, acht.
Bei neun gähnt sie wie|der.
Bei zehn wer|den die Au|gen schwer.""",
"""„Weißt du, was die Ster|ne ma|chen?“, fragt die Mö|we.
„Sie pas|sen auf.
Auf al|le, die schla|fen.
Auch auf dich.“
Pi|a lä|chelt.""",
"""Die Mö|we fliegt lei|se da|von.
Pi|a legt den Kopf an Ma|ma.
Ma|ma ist weich und warm.
Pa|pa summt ein Lied.
Ganz lei|se.""",
"""Pi|a denkt an den Tag.
An das kal|te Was|ser.
An die Fi|sche.
An das Rut|schen auf dem Bauch.
Es war ein gu|ter Tag.""",
"""Schhh, macht das Meer.
Der Wind streicht ü|ber das Eis.
Die Ster|ne pas|sen auf.
Pi|a at|met tief ein.
Und tief aus.""",
"""Dann sind die Au|gen zu.
Pi|a schläft.
Ma|ma gibt ihr ei|nen Kuss auf den Kopf.
„Gu|te Nacht, klei|ner Pin|gu|in.“
Gu|te Nacht.
En|de.""",
], bilder=["🌇❄️🐧🐧", "🐧👀✨", "🌊🎵", "💨🪶🐧", "🕊️🪨🐧", "🌌⭐⭐", "🐧🔢⭐", "⭐👀😴", "🐧🐧🎵", "🐧💭🐟", "🌊💨⭐", "😴🐧🌙"])

geschichte("robbe-mutig", "Ron|ja und das tie|fe Was|ser", "robbe", "mutig", "Ronja", [
"""Ron|ja liegt auf ih|rem Fel|sen.
Un|ten ist das Meer.
Nah am Fel|sen ist es hell und flach.
Dort schwimmt Ron|ja gern.""",
"""Wei|ter drau|ßen wird das Was|ser dun|kel.
Dort ist es tief.
Ron|ja mag das nicht.
„Da un|ten ist es schwarz“, sagt sie.
„Da kann al|les sein.“""",
"""Die gro|ßen Rob|ben tau|chen tief.
Sie ho|len die bes|ten Fi|sche.
„Komm mit“, sagt Ma|ma.
Ron|ja schüt|telt den Kopf.
Ihr Bauch fühlt sich ko|misch an.""",
"""Am Nach|mit|tag spielt Ron|ja mit ei|nem Ball.
Es ist ein Stück Holz, rund und glatt.
Sie stößt ihn mit der Na|se.
Der Ball rollt.
Und fällt ins tie|fe Was|ser.""",
"""Ron|ja schaut hin|un|ter.
Der Ball sinkt lang|sam.
Dun|kel|grün. Dann dun|kel|blau.
„Nein!“, ruft Ron|ja.
A|ber sie springt nicht.""",
"""Da kommt Pia, der Pin|gu|in.
„Was ist los?“
„Mein Ball. Er ist da un|ten.
Und da ist es so dun|kel.“
Pi|a schaut ins Was|ser.""",
"""„Ich hab auch mal Angst ge|habt“, sagt Pi|a.
„Vor dem Rut|schen am gro|ßen Berg.“
„Und dann?“, fragt Ron|ja.
„Dann bin ich ein klei|nes Stück ge|rutscht.
Nur ein klei|nes.“""",
"""Ron|ja denkt nach.
Ein klei|nes Stück.
Das geht viel|leicht.
Sie holt Luft.
Und taucht. Nur ein biss|chen.""",
"""Un|ter Was|ser ist es gar nicht schwarz.
Es ist blau.
Klei|ne Fi|sche blit|zen vor|bei.
Licht tanzt auf dem Sand.
Ron|ja staunt.""",
"""Sie taucht noch ein Stück.
Und noch eins.
Da liegt der Ball!
Ron|ja stößt ihn mit der Na|se.
Er steigt nach o|ben.""",
"""Ron|ja kommt hoch.
Sie prus|tet und lacht.
„Es ist blau!“, ruft sie.
„Da un|ten ist es blau!“
Pi|a klatscht mit den Flü|geln.""",
"""Am A|bend taucht Ron|ja mit Ma|ma.
Ganz tief.
Sie holt ei|nen Fisch.
Den bes|ten von al|len.
„Mu|tig“, sagt Ma|ma.
Ron|ja weiß: Mu|tig heißt nicht kei|ne Angst.
Mu|tig heißt: ein klei|nes Stück.
En|de.""",
], bilder=["🦭🪨🌊", "🌊🌑❓", "🦭🦭🐟", "🦭🪵⚽", "⚽⬇️🌊", "🐧❓🦭", "🐧💬🦭", "🦭💨💦", "🌊🐟💡", "⚽⬆️🦭", "🦭😄🐧", "🦭🦭🐟🌙"])

geschichte("pinguin-freunde", "Pi|a und der neu|e Pin|gu|in", "pinguin", "freunde", "Pia", [
"""Am Eis ist et|was los.
Ein neu|er Pin|gu|in ist da.
Er steht ganz al|lein am Rand.
Er ist et|was grö|ßer als Pi|a.
Und er sagt kein Wort.""",
"""„Wer ist das?“, fragt Pi|a.
„Er heißt Ol|li“, sagt Ma|ma.
„Er kommt von ei|nem an|de|ren Eis.
Sei nett zu ihm.“
Pi|a schaut zu Ol|li. Ol|li schaut weg.""",
"""Pi|a geht zu ihm.
„Willst du spie|len?“
Ol|li zuckt mit den Flü|geln.
„Ich kenn hier nie|mand.“
„Jetzt kennst du mich“, sagt Pi|a.""",
"""Sie ge|hen zum Rutsch|berg.
Pi|a rutscht als Ers|te.
Ol|li rutscht hin|ter|her.
Er ist schnell! Viel schnel|ler als Pi|a.
Pi|a staunt.""",
"""Am Was|ser ist es an|ders.
Pi|a springt so|fort hin|ein.
Ol|li bleibt ste|hen.
„Ich kann nicht so gut schwim|men“, sagt er lei|se.
„Bei uns war das Was|ser an|ders.“""",
"""Pi|a kommt zu|rück ans Eis.
„Das macht nichts“, sagt sie.
„Ich zeig dir, wie es hier geht.
Und du zeigst mir das Rut|schen.“
Ol|li grinst zum ers|ten Mal.""",
"""Pi|a zeigt Ol|li den fla|chen Teil.
Dort ist das Was|ser ru|hig.
Ol|li tritt hin|ein. Erst ein Fuß.
Dann der an|de|re.
Dann der gan|ze Ol|li.""",
"""Sie schwim|men ne|ben|ein|an|der.
Lang|sam. Dann schnel|ler.
Ol|li taucht so|gar.
„Das geht ja!“, ruft er.
Pi|a klatscht.""",
"""Am Nach|mit|tag üben sie Rut|schen.
Ol|li zeigt Pi|a ei|nen Trick.
Kopf run|ter, Flü|gel an den Kör|per.
Pi|a rutscht so weit wie noch nie.
Bis fast ins Was|ser.""",
"""Da kom|men die an|de|ren Pin|gu|i|ne.
„Wer ist das?“, fra|gen sie.
„Das ist Ol|li“, sagt Pi|a.
„Mein Freund. Er kann su|per rut|schen.“
Ol|li wird ein biss|chen rot.""",
"""Al|le wol|len den Trick ler|nen.
Ol|li zeigt ihn je|dem.
Der gan|ze Berg ist voll mit Pin|gu|i|nen.
Sie rut|schen und la|chen.
Ol|li lacht am lau|tes|ten.""",
"""Am A|bend steht Ol|li nicht mehr al|lein.
Er steht mit|ten|drin.
„Dan|ke“, sagt er zu Pi|a.
„Wo|für?“, fragt Pi|a.
„Dass du ge|fragt hast.“
En|de.""",
], bilder=["🐧🧊🐧", "🐧👀🐧", "🐧💬🐧", "⛷️🐧🐧", "🌊🐧😟", "🐧🤝🐧", "🐧👣🌊", "🐧🐧🌊", "⛷️🐧💡", "🐧🐧🐧❓", "⛰️🐧🐧🐧😄", "🐧🐧🌙"])

geschichte("robbe-gutenacht", "Ron|ja und das Lied vom Meer", "robbe", "gutenacht", "Ronja", [
"""Der Tag ist vor|bei.
Die Son|ne ist rot und geht ins Meer.
Ron|ja liegt auf ih|rem Fel|sen.
Der Fel|sen ist noch warm.""",
"""Ma|ma legt sich ne|ben sie.
„Zeit zu schla|fen“, sagt sie.
Ron|ja rollt sich hin und her.
„Ich kann noch nicht.
Mein Kopf ist so voll.“""",
"""„Was ist denn drin?“, fragt Ma|ma.
„Das Tau|chen. Die Fi|sche.
Der Pin|gu|in von heu|te.
Und die gro|ße Wel|le.“
„Das ist viel“, sagt Ma|ma.""",
"""„Weißt du was?“, sagt Ma|ma.
„Wir ge|ben je|de Sa|che dem Meer.
Das Meer passt gut dar|auf auf.
Mor|gen holst du sie wie|der.“
Ron|ja nickt.""",
"""„Das Tau|chen“, sagt Ron|ja.
Sie schaut aufs Was|ser.
Ei|ne klei|ne Wel|le kommt.
Und nimmt das Tau|chen mit.
Schhh.""",
"""„Die Fi|sche“, sagt Ron|ja.
Noch ei|ne Wel|le.
Schhh.
„Der Pin|gu|in.“
Schhh. Die Wel|le nimmt ihn mit.""",
"""„Die gro|ße Wel|le“, sagt Ron|ja lei|se.
Da muss sie fast la|chen.
Ei|ne Wel|le, die ei|ne Wel|le holt.
Schhh.
Ihr Kopf wird leicht.""",
"""Jetzt ist es still.
Nur das Meer singt noch.
Schhh. Schhh.
Im|mer das glei|che Lied.
Ron|ja kennt es seit sie klein ist.""",
"""Der Mond kommt raus.
Er malt ei|nen Weg aufs Was|ser.
Sil|bern und lang.
„Da schwim|men die Träu|me drauf“, sagt Ma|ma.
Ron|ja schaut dem Weg nach.""",
"""Ih|re Au|gen wer|den schwer.
Ma|ma streicht ü|ber ihr Fell.
Ganz lang|sam.
Wie das Meer ü|ber den Sand.
Hin und her.""",
"""Ron|ja gähnt.
„Ma|ma?“
„Ja?“
„Mor|gen hol ich al|les wie|der.“
„Ja. Al|les.“""",
"""Dann schläft Ron|ja.
Das Meer singt wei|ter.
Schhh. Schhh.
Es passt auf.
Auf das Tau|chen, die Fi|sche, den Pin|gu|in.
Und auf Ron|ja.
Gu|te Nacht.
En|de.""",
], bilder=["🌇🌊🦭", "🦭🦭💭", "💭🐟🐧🌊", "🦭🌊🤲", "🌊🐟💤", "🌊🐧💤", "🌊🌊😄", "🌊🎵", "🌙🌊✨", "🦭🦭🤲", "🦭💬", "😴🦭🌊🌙"])

def pruefe(g):
    fehler = []
    if len(g["seiten"]) != 12: fehler.append(f"{g['id']}: {len(g['seiten'])} Seiten statt 12")
    for i, s in enumerate(g["seiten"], 1):
        if '"' in s: fehler.append(f"{g['id']} Seite {i}: ASCII-Anführungszeichen")
        for satz in re.split(r"[.!?]\s", s.replace("|", "")):
            if len(satz.split()) > 12: fehler.append(f"{g['id']} Seite {i}: langer Satz ({len(satz.split())} Wörter)")
    return fehler

def kotlin_string(text):
    return '"' + text.replace("\\", "\\\\").replace('"', '\\"').replace("$", "\\$").replace("\n", "\\n") + '"'

KOPF = """package de.korte_daniel.zaubernina.data

// ACHTUNG: erzeugte Datei. Nicht von Hand ändern — Texte stehen in werkzeuge/geschichten_erzeugen.py,
// dort ändern und das Werkzeug laufen lassen. Silben sind mit | markiert (Sprechsilben wie in Fibeln).

/** Das Tier, das die Hauptrolle spielt. Nina hat die Robbe gewählt, Lea den Pinguin. */
enum class Tier(val anzeigename: String, val symbol: String) {
    PINGUIN("Pinguin", "🐧"),
    ROBBE("Robbe", "🦭"),
}

/** Worum es geht — die Auswahl vor dem Vorlesen. */
enum class GeschichtenThema(val anzeigename: String, val symbol: String) {
    ABENTEUER("Abenteuer", "🧭"),
    FREUNDE("Freunde", "🤝"),
    GUTENACHT("Gute Nacht", "🌙"),
    MUTIG("Mutig sein", "💪"),
}

/**
 * Eine Vorlesegeschichte: 12 kurze Seiten, Haltepunkte nach Seite 4 und 8. So gibt es drei
 * Längen aus EINER Geschichte — kurz endet an einem Punkt, an dem die Geschichte rund ist.
 * Wörter tragen Silbentrenner |, die Anzeige färbt Silben abwechselnd (Fibel-Prinzip).
 */
data class Geschichte(
    val id: String,
    val titel: String,
    val tier: Tier,
    val thema: GeschichtenThema,
    val held: String,
    val haltepunkte: List<Int>,
    val seiten: List<String>,
    /** Je Seite eine Emoji-Szene als Bild. */
    val bilder: List<String>,
) {
    /** Seiten für eine Länge: 0 = kurz (bis 1. Haltepunkt), 1 = mittel, 2 = lang (alle). */
    fun seitenFuer(laenge: Int): List<String> = when (laenge) {
        0 -> seiten.take(haltepunkte[0])
        1 -> seiten.take(haltepunkte[1])
        else -> seiten
    }
    fun woerterFuer(laenge: Int): Int = seitenFuer(laenge).sumOf { s -> s.replace("|", "").lines().sumOf { z -> z.split(' ').count { it.isNotBlank() } } }
}

val GESCHICHTEN: List<Geschichte> = listOf(
"""

FUSS = """)

fun geschichte(tier: Tier, thema: GeschichtenThema): Geschichte? =
    GESCHICHTEN.firstOrNull { it.tier == tier && it.thema == thema }

fun themenFuer(tier: Tier): List<GeschichtenThema> =
    GeschichtenThema.entries.filter { thema -> GESCHICHTEN.any { it.tier == tier && it.thema == thema } }
"""

if __name__ == "__main__":
    alle = []
    teile = []
    for g in GESCHICHTEN:
        alle += pruefe(g)
        seiten = ",\n".join("        " + kotlin_string(s) for s in g["seiten"])
        teile.append(
            f'    Geschichte(\n        id = {kotlin_string(g["id"])},\n        titel = {kotlin_string(g["titel"])},\n'
            f'        tier = Tier.{g["tier"].upper()},\n        thema = GeschichtenThema.{g["thema"].upper()},\n'
            f'        held = {kotlin_string(g["held"])},\n        haltepunkte = listOf({", ".join(map(str, g["haltepunkte"]))}),\n'
            f'        seiten = listOf(\n{seiten},\n        ),\n        bilder = listOf({", ".join(kotlin_string(b) for b in g["bilder"])}),\n    ),'
        )
        w = sum(len(s.replace("|", "").split()) for s in g["seiten"])
        print(f"{g['id']:22} {w:4} Wörter, Seiten {len(g['seiten'])}")
    with open(ZIEL, "w", encoding="utf-8") as f:
        f.write(KOPF + "\n".join(teile) + FUSS)
    for e in alle: print("WARNUNG:", e)
    sys.exit(1 if alle else 0)
