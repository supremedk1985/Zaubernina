package de.korte_daniel.zaubernina.domain

/**
 * Der ABC-Stern auf der Reise: das ganze Alphabet einmal von vorn bis hinten
 * durchschreiben — unabhängig von den Wort-Leveln und ihren Schlössern, jederzeit offen.
 *
 * Die Reihenfolge ist die alphabetische; die Umlaute stehen am Ende, wie in der
 * Anlauttabelle der Schule. Der Fortschritt ([index] = geschriebene Buchstaben der
 * laufenden Runde) bleibt über App-Starts erhalten — bei 29 Buchstaben schafft ein Kind
 * das nicht in einer Sitzung, und nichts frustriert mehr, als wieder beim A anzufangen.
 */
val ALPHABET: List<Char> = ('A'..'Z').toList() + listOf('Ä', 'Ö', 'Ü')

/** Fünf Sterne für die erste vollendete Runde — danach ist die Belohnung das Können. */
fun sterneFuerAlphabetRunde(rundenBisher: Int): Int = if (rundenBisher == 0) 5 else 0

/** Der Buchstabe, der als Nächstes dran ist. */
fun alphabetBuchstabe(index: Int): Char = ALPHABET[index.coerceIn(0, ALPHABET.lastIndex)]

/** true, wenn mit diesem Buchstaben die Runde voll ist. */
fun alphabetRundeVoll(index: Int): Boolean = index + 1 >= ALPHABET.size
