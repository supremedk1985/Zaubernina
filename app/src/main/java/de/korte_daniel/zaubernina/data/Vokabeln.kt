package de.korte_daniel.zaubernina.data

// ACHTUNG: erzeugte Datei — Texte stehen in werkzeuge/vokabeln_erzeugen.py, dort ändern.

/** Ein Wort in drei Sprachen. [deSilben] mit Trennern für das Lesen-Spiel, [bild] Emoji oder leer, [farbe] Hex oder leer. */
data class Vokabel(
    val deSilben: String,
    val artikel: String,
    val en: String,
    val es: String,
    val bild: String,
    val farbe: String,
) {
    val de: String get() = deSilben.replace("|", "")
    /** Kennung für die Fehlerstatistik: das deutsche Wort. */
    val kennung: String get() = de
    /** „einen Hund", „eine Katze", „ein Buch" — für Lesesätze. Leer ohne Artikel. */
    val akkusativ: String get() = when (artikel) {
        "der" -> "einen $de"
        "die" -> "eine $de"
        "das" -> "ein $de"
        else -> de
    }
}

/** Ein Wortfeld mit Satzmustern je Sprache ({akk} = Akkusativ deutsch, {en}/{es} = Wort). Leer = keine Sätze in dieser Sprache. */
data class Wortthema(
    val kennung: String,
    val name: String,
    val symbol: String,
    val satzDe: String,
    val satzEn: String,
    val satzEs: String,
    val woerter: List<Vokabel>,
)

val WORTTHEMEN: List<Wortthema> = listOf(
    Wortthema(
        kennung = "FARBEN",
        name = "Farben",
        symbol = "🎨",
        satzDe = "",
        satzEn = "It is {en}.",
        satzEs = "Es {es}.",
        woerter = listOf(
            Vokabel("rot", "", "red", "rojo", "", "#E53935"),
            Vokabel("blau", "", "blue", "azul", "", "#1E88E5"),
            Vokabel("gelb", "", "yellow", "amarillo", "", "#FDD835"),
            Vokabel("grün", "", "green", "verde", "", "#43A047"),
            Vokabel("o|ran|ge", "", "orange", "naranja", "", "#FB8C00"),
            Vokabel("li|la", "", "purple", "morado", "", "#8E24AA"),
            Vokabel("ro|sa", "", "pink", "rosa", "", "#EC407A"),
            Vokabel("schwarz", "", "black", "negro", "", "#212121"),
            Vokabel("weiß", "", "white", "blanco", "", "#FAFAFA"),
            Vokabel("braun", "", "brown", "marrón", "", "#6D4C41"),
        ),
    ),
    Wortthema(
        kennung = "ZAHLEN",
        name = "Zahlen",
        symbol = "🔢",
        satzDe = "",
        satzEn = "I count to {en}.",
        satzEs = "Cuento hasta {es}.",
        woerter = listOf(
            Vokabel("eins", "", "one", "uno", "1️⃣", ""),
            Vokabel("zwei", "", "two", "dos", "2️⃣", ""),
            Vokabel("drei", "", "three", "tres", "3️⃣", ""),
            Vokabel("vier", "", "four", "cuatro", "4️⃣", ""),
            Vokabel("fünf", "", "five", "cinco", "5️⃣", ""),
            Vokabel("sechs", "", "six", "seis", "6️⃣", ""),
            Vokabel("sie|ben", "", "seven", "siete", "7️⃣", ""),
            Vokabel("acht", "", "eight", "ocho", "8️⃣", ""),
            Vokabel("neun", "", "nine", "nueve", "9️⃣", ""),
            Vokabel("zehn", "", "ten", "diez", "🔟", ""),
        ),
    ),
    Wortthema(
        kennung = "TIERE",
        name = "Tiere",
        symbol = "🐾",
        satzDe = "Da ist {akk}.",
        satzEn = "I see a {en}.",
        satzEs = "Veo un {es}.",
        woerter = listOf(
            Vokabel("Hund", "der", "dog", "perro", "🐶", ""),
            Vokabel("Kat|ze", "die", "cat", "gato", "🐱", ""),
            Vokabel("Pferd", "das", "horse", "caballo", "🐴", ""),
            Vokabel("Kuh", "die", "cow", "vaca", "🐮", ""),
            Vokabel("Schwein", "das", "pig", "cerdo", "🐷", ""),
            Vokabel("Maus", "die", "mouse", "ratón", "🐭", ""),
            Vokabel("Vo|gel", "der", "bird", "pájaro", "🐦", ""),
            Vokabel("Fisch", "der", "fish", "pez", "🐟", ""),
            Vokabel("Pin|gu|in", "der", "penguin", "pingüino", "🐧", ""),
            Vokabel("Rob|be", "die", "seal", "foca", "🦭", ""),
            Vokabel("Frosch", "der", "frog", "rana", "🐸", ""),
            Vokabel("Bär", "der", "bear", "oso", "🐻", ""),
        ),
    ),
    Wortthema(
        kennung = "ESSEN",
        name = "Essen",
        symbol = "🍎",
        satzDe = "Da ist {akk}.",
        satzEn = "I like {en}.",
        satzEs = "Me gusta {es}.",
        woerter = listOf(
            Vokabel("Ap|fel", "der", "apple", "manzana", "🍎", ""),
            Vokabel("Ba|na|ne", "die", "banana", "plátano", "🍌", ""),
            Vokabel("Brot", "das", "bread", "pan", "🍞", ""),
            Vokabel("Kä|se", "der", "cheese", "queso", "🧀", ""),
            Vokabel("Milch", "die", "milk", "leche", "🥛", ""),
            Vokabel("Was|ser", "das", "water", "agua", "💧", ""),
            Vokabel("Ei", "das", "egg", "huevo", "🥚", ""),
            Vokabel("Ku|chen", "der", "cake", "pastel", "🍰", ""),
            Vokabel("Eis", "das", "ice cream", "helado", "🍦", ""),
            Vokabel("Piz|za", "die", "pizza", "pizza", "🍕", ""),
            Vokabel("Ka|rot|te", "die", "carrot", "zanahoria", "🥕", ""),
            Vokabel("Erd|bee|re", "die", "strawberry", "fresa", "🍓", ""),
        ),
    ),
    Wortthema(
        kennung = "FAMILIE",
        name = "Familie",
        symbol = "👨‍👩‍👧‍👧",
        satzDe = "Da ist {akk}.",
        satzEn = "This is my {en}.",
        satzEs = "Es mi {es}.",
        woerter = listOf(
            Vokabel("Ma|ma", "die", "mum", "mamá", "👩", ""),
            Vokabel("Pa|pa", "der", "dad", "papá", "👨", ""),
            Vokabel("Schwes|ter", "die", "sister", "hermana", "👧", ""),
            Vokabel("Bru|der", "der", "brother", "hermano", "👦", ""),
            Vokabel("O|ma", "die", "grandma", "abuela", "👵", ""),
            Vokabel("O|pa", "der", "grandpa", "abuelo", "👴", ""),
            Vokabel("Ba|by", "das", "baby", "bebé", "👶", ""),
            Vokabel("Freund", "der", "friend", "amigo", "🧒", ""),
        ),
    ),
    Wortthema(
        kennung = "KOERPER",
        name = "Körper",
        symbol = "🖐️",
        satzDe = "Da ist {akk}.",
        satzEn = "This is my {en}.",
        satzEs = "Es mi {es}.",
        woerter = listOf(
            Vokabel("Hand", "die", "hand", "mano", "🖐️", ""),
            Vokabel("Fuß", "der", "foot", "pie", "🦶", ""),
            Vokabel("Au|ge", "das", "eye", "ojo", "👁️", ""),
            Vokabel("Ohr", "das", "ear", "oreja", "👂", ""),
            Vokabel("Na|se", "die", "nose", "nariz", "👃", ""),
            Vokabel("Mund", "der", "mouth", "boca", "👄", ""),
            Vokabel("Zahn", "der", "tooth", "diente", "🦷", ""),
            Vokabel("Haar", "das", "hair", "pelo", "💇", ""),
        ),
    ),
    Wortthema(
        kennung = "SCHULE",
        name = "Schule",
        symbol = "🎒",
        satzDe = "Da ist {akk}.",
        satzEn = "I have a {en}.",
        satzEs = "Tengo un {es}.",
        woerter = listOf(
            Vokabel("Buch", "das", "book", "libro", "📖", ""),
            Vokabel("Stift", "der", "pen", "bolígrafo", "🖊️", ""),
            Vokabel("Sche|re", "die", "scissors", "tijeras", "✂️", ""),
            Vokabel("Ruck|sack", "der", "backpack", "mochila", "🎒", ""),
            Vokabel("Tisch", "der", "table", "mesa", "🪑", ""),
            Vokabel("Uhr", "die", "clock", "reloj", "🕐", ""),
            Vokabel("Ball", "der", "ball", "pelota", "⚽", ""),
            Vokabel("Bild", "das", "picture", "dibujo", "🖼️", ""),
        ),
    ),
    Wortthema(
        kennung = "WETTER",
        name = "Wetter",
        symbol = "🌤️",
        satzDe = "Da ist {akk}.",
        satzEn = "Today there is {en}.",
        satzEs = "Hoy hay {es}.",
        woerter = listOf(
            Vokabel("Son|ne", "die", "sun", "sol", "☀️", ""),
            Vokabel("Re|gen", "der", "rain", "lluvia", "🌧️", ""),
            Vokabel("Schnee", "der", "snow", "nieve", "❄️", ""),
            Vokabel("Wol|ke", "die", "cloud", "nube", "☁️", ""),
            Vokabel("Wind", "der", "wind", "viento", "🌬️", ""),
            Vokabel("Re|gen|bo|gen", "der", "rainbow", "arcoíris", "🌈", ""),
            Vokabel("Mond", "der", "moon", "luna", "🌙", ""),
            Vokabel("Stern", "der", "star", "estrella", "⭐", ""),
        ),
    ),
)
