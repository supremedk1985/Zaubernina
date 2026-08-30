package de.korte_daniel.zaubernina.domain

/**
 * Das tägliche Übungs-Zeitlimit. Es wird PRO KIND eingestellt und PRO KIND und Tag
 * gezählt — die Eltern stellen es im Elternbereich ein, 0 heißt „kein Limit".
 *
 * Gezählt wird nur echte Übungszeit: solange ein Kind angemeldet ist und die App im
 * Vordergrund läuft. Start-Bildschirm und Elternbereich zählen nicht, und der
 * Elternbereich bleibt auch nach Ablauf erreichbar — sonst könnte niemand das Limit
 * wieder lockern.
 */
val ZEITLIMIT_STUFEN_MINUTEN = listOf(0, 10, 15, 20, 30, 45)

fun zeitlimitName(minuten: Int): String =
    if (minuten == 0) "Kein Limit" else "$minuten Minuten am Tag"

/** Ist für heute Schluss? */
fun zeitVorbei(limitMinuten: Int, heuteSekunden: Int): Boolean =
    limitMinuten > 0 && heuteSekunden >= limitMinuten * 60

/** „Heute schon 12 Minuten geübt" — für den Elternbereich, aufgerundet ab der ersten Sekunde. */
fun geuebteMinuten(heuteSekunden: Int): Int = (heuteSekunden + 59) / 60
