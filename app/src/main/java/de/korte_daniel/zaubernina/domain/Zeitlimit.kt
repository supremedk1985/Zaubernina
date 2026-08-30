package de.korte_daniel.zaubernina.domain

/*
 * Das tägliche Übungs-Zeitlimit. Es wird PRO KIND eingestellt und PRO KIND und Tag
 * gezählt — die Eltern geben es im Elternbereich als Minutenzahl ein, 0 heißt
 * „kein Limit".
 *
 * Gezählt wird nur echte Übungszeit: solange ein Kind angemeldet ist und die App im
 * Vordergrund läuft. Start-Bildschirm und Elternbereich zählen nicht, und der
 * Elternbereich bleibt auch nach Ablauf erreichbar — sonst könnte niemand das Limit
 * wieder lockern.
 */

/**
 * Macht aus der Elterneingabe eine Minutenzahl — oder null, wenn sie keine ist.
 * Erlaubt sind 1 bis 240 Minuten; wer mehr als vier Stunden zulassen will,
 * braucht kein Limit.
 */
fun bereinigeZeitlimit(eingabe: String): Int? {
    val minuten = eingabe.trim().toIntOrNull() ?: return null
    return if (minuten in 1..240) minuten else null
}

fun zeitlimitName(minuten: Int): String =
    if (minuten == 0) "Kein Limit" else if (minuten == 1) "1 Minute am Tag" else "$minuten Minuten am Tag"

/** Ist für heute Schluss? */
fun zeitVorbei(limitMinuten: Int, heuteSekunden: Int): Boolean =
    limitMinuten > 0 && heuteSekunden >= limitMinuten * 60

/** „Heute schon 12 Minuten geübt" — für den Elternbereich, aufgerundet ab der ersten Sekunde. */
fun geuebteMinuten(heuteSekunden: Int): Int = (heuteSekunden + 59) / 60
