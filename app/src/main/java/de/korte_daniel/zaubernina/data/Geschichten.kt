package de.korte_daniel.zaubernina.data

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
    Geschichte(
        id = "pinguin-abenteuer",
        titel = "Pi|a und der ver|lo|re|ne Fisch",
        tier = Tier.PINGUIN,
        thema = GeschichtenThema.ABENTEUER,
        held = "Pia",
        haltepunkte = listOf(4, 8),
        seiten = listOf(
        "Pi|a ist ein klei|ner Pin|gu|in.\nSie wohnt am Rand vom Eis.\nJe|den Mor|gen springt sie ins Was|ser.\nDas Was|ser ist kalt. Pi|a mag das.",
        "Heu|te fängt Pi|a ei|nen Fisch.\nEr ist sil|bern und glänzt.\n„Den brin|ge ich Ma|ma“, sagt Pi|a.\nSie legt den Fisch auf das Eis.",
        "Da kommt ein Wind.\nDer Wind ist stark.\nEr schiebt den Fisch ü|ber das Eis.\nDer Fisch rutscht und rutscht.\n„Halt!“, ruft Pi|a.",
        "Pi|a läuft dem Fisch nach.\nSie wat|schelt so schnell sie kann.\nDoch der Fisch ist weg.\nNur ei|ne Spur ist noch da.\nPi|a folgt der Spur.",
        "Die Spur führt zu ei|nem Berg aus Schnee.\nO|ben sitzt ei|ne Mö|we.\n„Hast du mei|nen Fisch ge|se|hen?“, fragt Pi|a.\nDie Mö|we schüt|telt den Kopf.\n„Nein. A|ber ich hel|fe dir su|chen.“",
        "Die Mö|we fliegt hoch.\nSie schaut nach links und nach rechts.\n„Da un|ten!“, ruft sie.\n„Bei den gro|ßen Stei|nen!“\nPi|a rennt los.",
        "Bei den Stei|nen liegt der Fisch.\nA|ber er ist nicht al|lein.\nEi|ne klei|ne Rob|be hält ihn fest.\nSie zit|tert.\n„Ich ha|be so Hun|ger“, sagt die Rob|be.",
        "Pi|a ist erst wü|tend.\nDann sieht sie die Rob|be an.\nDie Rob|be ist noch klei|ner als sie.\n„Wie heißt du?“, fragt Pi|a.\n„Ron|ja“, sagt die Rob|be lei|se.",
        "Pi|a denkt nach.\nMa|ma war|tet auf den Fisch.\nA|ber Ron|ja hat Hun|ger. Jetzt.\n„Iss ihn“, sagt Pi|a.\n„Ich fan|ge ei|nen neu|en.“",
        "Ron|ja isst den Fisch.\nDann lä|chelt sie.\n„Dan|ke, Pi|a. Ich kom|me mit.\nZu zweit fängt man mehr.“\nSie sprin|gen zu|sam|men ins Was|ser.",
        "Und wirk|lich: Sie fan|gen drei Fi|sche.\nEi|nen für Ma|ma.\nEi|nen für Pa|pa.\nUnd ei|nen für die Mö|we,\ndie so gut ge|sucht hat.",
        "Am A|bend sitzt Pi|a bei Ma|ma.\n„Ich ha|be den Fisch ver|lo|ren“, sagt sie.\n„A|ber ich ha|be ei|ne Freun|din ge|fun|den.“\nMa|ma lacht.\n„Das ist mehr als ein Fisch.“\nEn|de.",
        ),
        bilder = listOf("🐧❄️🌊", "🐧🐟", "💨🐟❄️", "🐧👣❄️", "🐧⛰️🕊️", "🕊️👀🪨", "🪨🐟🦭", "🐧🦭", "🐧🤔🐟", "🐧🦭🌊", "🐟🐟🐟", "🐧🐧🌙"),
    ),
    Geschichte(
        id = "robbe-abenteuer",
        titel = "Ron|ja und die Rei|se zur Eis|in|sel",
        tier = Tier.ROBBE,
        thema = GeschichtenThema.ABENTEUER,
        held = "Ronja",
        haltepunkte = listOf(4, 8),
        seiten = listOf(
        "Ron|ja ist ei|ne klei|ne Rob|be.\nSie hat ein wei|ches, grau|es Fell.\nAm liebs|ten liegt sie auf ei|nem Fel|sen.\nDort ist es warm.",
        "Weit drau|ßen im Meer liegt ei|ne In|sel.\nSie ist ganz aus Eis.\n„Da will ich hin“, sagt Ron|ja.\n„Das ist zu weit“, sagt Ma|ma.\nRon|ja will es trotz|dem.",
        "Am Mor|gen schwimmt Ron|ja los.\nDas Was|ser ist tief und blau.\nSie taucht und schnauft.\nDie In|sel wird grö|ßer.\nA|ber Ron|ja wird mü|de.",
        "Da kommt ein Brett ge|schwom|men.\nEs ist aus Holz.\nRon|ja klet|tert dar|auf.\n„Puh“, sagt sie.\nDas Brett trägt sie ein Stück.",
        "Plötz|lich wird das Meer un|ru|hig.\nEi|ne gro|ße Wel|le kommt.\nDas Brett wa|ckelt.\nRon|ja hält sich fest.\nDie Wel|le trägt sie hoch und run|ter.",
        "Dann ist es wie|der still.\nRon|ja schaut sich um.\nDie In|sel ist ganz nah!\nSie springt ins Was|ser.\nMit drei Stö|ßen ist sie da.",
        "Die In|sel glit|zert.\nÜ|ber|all ist Eis.\nUnd ü|ber|all sind Pin|gu|i|ne.\nEin klei|ner Pin|gu|in kommt nä|her.\n„Wer bist du?“, fragt er.",
        "„Ich bin Ron|ja. Ich kom|me von weit her.“\n„Ich bin Pit“, sagt der Pin|gu|in.\n„Willst du mit uns rut|schen?“\nRon|ja nickt.\nSie rutscht auf dem Bauch den Berg run|ter. Ju|hu!",
        "Sie rut|schen den gan|zen Tag.\nDann wird es dun|kel.\nRon|ja denkt an Ma|ma.\n„Ich muss nach Hau|se“, sagt sie.\n„A|ber der Weg ist so weit.“",
        "Pit ruft die an|de|ren Pin|gu|i|ne.\n„Wir brin|gen dich!“\nAl|le sprin|gen ins Was|ser.\nSie schwim|men im Kreis um Ron|ja.\nSo ist sie nie al|lein.",
        "Die Ster|ne zei|gen den Weg.\nRon|ja schwimmt in der Mit|te.\nSie ist gar nicht mehr mü|de.\nDa vorn ist ihr Fel|sen!\nUnd da sitzt Ma|ma.",
        "Ma|ma drückt Ron|ja ganz fest.\n„Du warst weg“, sagt sie.\n„Ja“, sagt Ron|ja.\n„A|ber ich ha|be Freun|de mit|ge|bracht.“\nDie Pin|gu|i|ne win|ken.\nEn|de.",
        ),
        bilder = listOf("🦭🪨☀️", "🏝️❄️🌊", "🦭🌊💦", "🪵🦭🌊", "🌊🌊🪵", "🦭🏝️", "❄️🐧🐧🐧", "🦭🐧⛷️", "🌙🦭🐧", "🐧🐧🦭🐧", "⭐🌊🪨", "🦭🦭🐧"),
    ),
    Geschichte(
        id = "pinguin-gutenacht",
        titel = "Pi|a kann nicht schla|fen",
        tier = Tier.PINGUIN,
        thema = GeschichtenThema.GUTENACHT,
        held = "Pia",
        haltepunkte = listOf(4, 8),
        seiten = listOf(
        "Es ist A|bend am Eis.\nDie Son|ne geht un|ter.\nAl|le Pin|gu|i|ne ste|hen dicht zu|sam|men.\nSo bleibt es warm.",
        "Pi|a steht bei Ma|ma und Pa|pa.\nSie soll schla|fen.\nA|ber ih|re Au|gen wol|len nicht zu.\n„Ich bin nicht mü|de“, sagt sie.",
        "„Dann hör zu“, sagt Pa|pa lei|se.\n„Hörst du das Meer?“\nPi|a hört hin.\nDas Meer macht: Schhh. Schhh.\nWie ein Lied.",
        "„Hörst du den Wind?“, fragt Ma|ma.\nDer Wind pfeift ganz sanft.\nEr streicht ü|ber Pi|as Fe|dern.\nPi|a gähnt.\nA|ber die Au|gen blei|ben of|fen.",
        "Da kommt die Mö|we vor|bei.\nSie setzt sich auf ei|nen Stein.\n„Du bist ja noch wach“, sagt sie.\n„Soll ich dir et|was zei|gen?“\nPi|a nickt.",
        "Die Mö|we zeigt nach o|ben.\nDer Him|mel ist dun|kel|blau.\nUnd ü|ber|all sind Ster|ne.\nKlei|ne und gro|ße.\n„Zähl sie“, sagt die Mö|we.",
        "Pi|a zählt.\nEins, zwei, drei, vier, fünf.\nSechs, sie|ben, acht.\nBei neun gähnt sie wie|der.\nBei zehn wer|den die Au|gen schwer.",
        "„Weißt du, was die Ster|ne ma|chen?“, fragt die Mö|we.\n„Sie pas|sen auf.\nAuf al|le, die schla|fen.\nAuch auf dich.“\nPi|a lä|chelt.",
        "Die Mö|we fliegt lei|se da|von.\nPi|a legt den Kopf an Ma|ma.\nMa|ma ist weich und warm.\nPa|pa summt ein Lied.\nGanz lei|se.",
        "Pi|a denkt an den Tag.\nAn das kal|te Was|ser.\nAn die Fi|sche.\nAn das Rut|schen auf dem Bauch.\nEs war ein gu|ter Tag.",
        "Schhh, macht das Meer.\nDer Wind streicht ü|ber das Eis.\nDie Ster|ne pas|sen auf.\nPi|a at|met tief ein.\nUnd tief aus.",
        "Dann sind die Au|gen zu.\nPi|a schläft.\nMa|ma gibt ihr ei|nen Kuss auf den Kopf.\n„Gu|te Nacht, klei|ner Pin|gu|in.“\nGu|te Nacht.\nEn|de.",
        ),
        bilder = listOf("🌇❄️🐧🐧", "🐧👀✨", "🌊🎵", "💨🪶🐧", "🕊️🪨🐧", "🌌⭐⭐", "🐧🔢⭐", "⭐👀😴", "🐧🐧🎵", "🐧💭🐟", "🌊💨⭐", "😴🐧🌙"),
    ),
    Geschichte(
        id = "robbe-mutig",
        titel = "Ron|ja und das tie|fe Was|ser",
        tier = Tier.ROBBE,
        thema = GeschichtenThema.MUTIG,
        held = "Ronja",
        haltepunkte = listOf(4, 8),
        seiten = listOf(
        "Ron|ja liegt auf ih|rem Fel|sen.\nUn|ten ist das Meer.\nNah am Fel|sen ist es hell und flach.\nDort schwimmt Ron|ja gern.",
        "Wei|ter drau|ßen wird das Was|ser dun|kel.\nDort ist es tief.\nRon|ja mag das nicht.\n„Da un|ten ist es schwarz“, sagt sie.\n„Da kann al|les sein.“",
        "Die gro|ßen Rob|ben tau|chen tief.\nSie ho|len die bes|ten Fi|sche.\n„Komm mit“, sagt Ma|ma.\nRon|ja schüt|telt den Kopf.\nIhr Bauch fühlt sich ko|misch an.",
        "Am Nach|mit|tag spielt Ron|ja mit ei|nem Ball.\nEs ist ein Stück Holz, rund und glatt.\nSie stößt ihn mit der Na|se.\nDer Ball rollt.\nUnd fällt ins tie|fe Was|ser.",
        "Ron|ja schaut hin|un|ter.\nDer Ball sinkt lang|sam.\nDun|kel|grün. Dann dun|kel|blau.\n„Nein!“, ruft Ron|ja.\nA|ber sie springt nicht.",
        "Da kommt Pia, der Pin|gu|in.\n„Was ist los?“\n„Mein Ball. Er ist da un|ten.\nUnd da ist es so dun|kel.“\nPi|a schaut ins Was|ser.",
        "„Ich hab auch mal Angst ge|habt“, sagt Pi|a.\n„Vor dem Rut|schen am gro|ßen Berg.“\n„Und dann?“, fragt Ron|ja.\n„Dann bin ich ein klei|nes Stück ge|rutscht.\nNur ein klei|nes.“",
        "Ron|ja denkt nach.\nEin klei|nes Stück.\nDas geht viel|leicht.\nSie holt Luft.\nUnd taucht. Nur ein biss|chen.",
        "Un|ter Was|ser ist es gar nicht schwarz.\nEs ist blau.\nKlei|ne Fi|sche blit|zen vor|bei.\nLicht tanzt auf dem Sand.\nRon|ja staunt.",
        "Sie taucht noch ein Stück.\nUnd noch eins.\nDa liegt der Ball!\nRon|ja stößt ihn mit der Na|se.\nEr steigt nach o|ben.",
        "Ron|ja kommt hoch.\nSie prus|tet und lacht.\n„Es ist blau!“, ruft sie.\n„Da un|ten ist es blau!“\nPi|a klatscht mit den Flü|geln.",
        "Am A|bend taucht Ron|ja mit Ma|ma.\nGanz tief.\nSie holt ei|nen Fisch.\nDen bes|ten von al|len.\n„Mu|tig“, sagt Ma|ma.\nRon|ja weiß: Mu|tig heißt nicht kei|ne Angst.\nMu|tig heißt: ein klei|nes Stück.\nEn|de.",
        ),
        bilder = listOf("🦭🪨🌊", "🌊🌑❓", "🦭🦭🐟", "🦭🪵⚽", "⚽⬇️🌊", "🐧❓🦭", "🐧💬🦭", "🦭💨💦", "🌊🐟💡", "⚽⬆️🦭", "🦭😄🐧", "🦭🦭🐟🌙"),
    ),
    Geschichte(
        id = "pinguin-freunde",
        titel = "Pi|a und der neu|e Pin|gu|in",
        tier = Tier.PINGUIN,
        thema = GeschichtenThema.FREUNDE,
        held = "Pia",
        haltepunkte = listOf(4, 8),
        seiten = listOf(
        "Am Eis ist et|was los.\nEin neu|er Pin|gu|in ist da.\nEr steht ganz al|lein am Rand.\nEr ist et|was grö|ßer als Pi|a.\nUnd er sagt kein Wort.",
        "„Wer ist das?“, fragt Pi|a.\n„Er heißt Ol|li“, sagt Ma|ma.\n„Er kommt von ei|nem an|de|ren Eis.\nSei nett zu ihm.“\nPi|a schaut zu Ol|li. Ol|li schaut weg.",
        "Pi|a geht zu ihm.\n„Willst du spie|len?“\nOl|li zuckt mit den Flü|geln.\n„Ich kenn hier nie|mand.“\n„Jetzt kennst du mich“, sagt Pi|a.",
        "Sie ge|hen zum Rutsch|berg.\nPi|a rutscht als Ers|te.\nOl|li rutscht hin|ter|her.\nEr ist schnell! Viel schnel|ler als Pi|a.\nPi|a staunt.",
        "Am Was|ser ist es an|ders.\nPi|a springt so|fort hin|ein.\nOl|li bleibt ste|hen.\n„Ich kann nicht so gut schwim|men“, sagt er lei|se.\n„Bei uns war das Was|ser an|ders.“",
        "Pi|a kommt zu|rück ans Eis.\n„Das macht nichts“, sagt sie.\n„Ich zeig dir, wie es hier geht.\nUnd du zeigst mir das Rut|schen.“\nOl|li grinst zum ers|ten Mal.",
        "Pi|a zeigt Ol|li den fla|chen Teil.\nDort ist das Was|ser ru|hig.\nOl|li tritt hin|ein. Erst ein Fuß.\nDann der an|de|re.\nDann der gan|ze Ol|li.",
        "Sie schwim|men ne|ben|ein|an|der.\nLang|sam. Dann schnel|ler.\nOl|li taucht so|gar.\n„Das geht ja!“, ruft er.\nPi|a klatscht.",
        "Am Nach|mit|tag üben sie Rut|schen.\nOl|li zeigt Pi|a ei|nen Trick.\nKopf run|ter, Flü|gel an den Kör|per.\nPi|a rutscht so weit wie noch nie.\nBis fast ins Was|ser.",
        "Da kom|men die an|de|ren Pin|gu|i|ne.\n„Wer ist das?“, fra|gen sie.\n„Das ist Ol|li“, sagt Pi|a.\n„Mein Freund. Er kann su|per rut|schen.“\nOl|li wird ein biss|chen rot.",
        "Al|le wol|len den Trick ler|nen.\nOl|li zeigt ihn je|dem.\nDer gan|ze Berg ist voll mit Pin|gu|i|nen.\nSie rut|schen und la|chen.\nOl|li lacht am lau|tes|ten.",
        "Am A|bend steht Ol|li nicht mehr al|lein.\nEr steht mit|ten|drin.\n„Dan|ke“, sagt er zu Pi|a.\n„Wo|für?“, fragt Pi|a.\n„Dass du ge|fragt hast.“\nEn|de.",
        ),
        bilder = listOf("🐧🧊🐧", "🐧👀🐧", "🐧💬🐧", "⛷️🐧🐧", "🌊🐧😟", "🐧🤝🐧", "🐧👣🌊", "🐧🐧🌊", "⛷️🐧💡", "🐧🐧🐧❓", "⛰️🐧🐧🐧😄", "🐧🐧🌙"),
    ),
    Geschichte(
        id = "robbe-gutenacht",
        titel = "Ron|ja und das Lied vom Meer",
        tier = Tier.ROBBE,
        thema = GeschichtenThema.GUTENACHT,
        held = "Ronja",
        haltepunkte = listOf(4, 8),
        seiten = listOf(
        "Der Tag ist vor|bei.\nDie Son|ne ist rot und geht ins Meer.\nRon|ja liegt auf ih|rem Fel|sen.\nDer Fel|sen ist noch warm.",
        "Ma|ma legt sich ne|ben sie.\n„Zeit zu schla|fen“, sagt sie.\nRon|ja rollt sich hin und her.\n„Ich kann noch nicht.\nMein Kopf ist so voll.“",
        "„Was ist denn drin?“, fragt Ma|ma.\n„Das Tau|chen. Die Fi|sche.\nDer Pin|gu|in von heu|te.\nUnd die gro|ße Wel|le.“\n„Das ist viel“, sagt Ma|ma.",
        "„Weißt du was?“, sagt Ma|ma.\n„Wir ge|ben je|de Sa|che dem Meer.\nDas Meer passt gut dar|auf auf.\nMor|gen holst du sie wie|der.“\nRon|ja nickt.",
        "„Das Tau|chen“, sagt Ron|ja.\nSie schaut aufs Was|ser.\nEi|ne klei|ne Wel|le kommt.\nUnd nimmt das Tau|chen mit.\nSchhh.",
        "„Die Fi|sche“, sagt Ron|ja.\nNoch ei|ne Wel|le.\nSchhh.\n„Der Pin|gu|in.“\nSchhh. Die Wel|le nimmt ihn mit.",
        "„Die gro|ße Wel|le“, sagt Ron|ja lei|se.\nDa muss sie fast la|chen.\nEi|ne Wel|le, die ei|ne Wel|le holt.\nSchhh.\nIhr Kopf wird leicht.",
        "Jetzt ist es still.\nNur das Meer singt noch.\nSchhh. Schhh.\nIm|mer das glei|che Lied.\nRon|ja kennt es seit sie klein ist.",
        "Der Mond kommt raus.\nEr malt ei|nen Weg aufs Was|ser.\nSil|bern und lang.\n„Da schwim|men die Träu|me drauf“, sagt Ma|ma.\nRon|ja schaut dem Weg nach.",
        "Ih|re Au|gen wer|den schwer.\nMa|ma streicht ü|ber ihr Fell.\nGanz lang|sam.\nWie das Meer ü|ber den Sand.\nHin und her.",
        "Ron|ja gähnt.\n„Ma|ma?“\n„Ja?“\n„Mor|gen hol ich al|les wie|der.“\n„Ja. Al|les.“",
        "Dann schläft Ron|ja.\nDas Meer singt wei|ter.\nSchhh. Schhh.\nEs passt auf.\nAuf das Tau|chen, die Fi|sche, den Pin|gu|in.\nUnd auf Ron|ja.\nGu|te Nacht.\nEn|de.",
        ),
        bilder = listOf("🌇🌊🦭", "🦭🦭💭", "💭🐟🐧🌊", "🦭🌊🤲", "🌊🐟💤", "🌊🐧💤", "🌊🌊😄", "🌊🎵", "🌙🌊✨", "🦭🦭🤲", "🦭💬", "😴🦭🌊🌙"),
    ),)

fun geschichte(tier: Tier, thema: GeschichtenThema): Geschichte? =
    GESCHICHTEN.firstOrNull { it.tier == tier && it.thema == thema }

fun themenFuer(tier: Tier): List<GeschichtenThema> =
    GeschichtenThema.entries.filter { thema -> GESCHICHTEN.any { it.tier == tier && it.thema == thema } }
