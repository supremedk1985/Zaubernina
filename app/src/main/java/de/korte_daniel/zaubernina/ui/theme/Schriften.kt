@file:OptIn(androidx.compose.ui.text.ExperimentalTextApi::class)

package de.korte_daniel.zaubernina.ui.theme

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import de.korte_daniel.zaubernina.R

// Statisch gebündelt (SIL OFL 1.1), nicht nachgeladen: die App hat keine
// Internet-Berechtigung und muss ohne Netz vollständig richtig aussehen.
// Die Lizenztexte liegen im Projekt unter lizenzen/.
//
// Fredoka und Baloo 2 sind VARIABLE Schriften — eine Datei enthält alle Strichstärken.
// Deshalb steht dieselbe Ressource mehrfach in der Familie, jeweils mit einer anderen
// Einstellung der Gewichtsachse. Patrick Hand hat nur einen Schnitt.

private fun gewicht(wert: Int) = FontVariation.Settings(FontVariation.weight(wert))

val Fredoka = FontFamily(
    Font(R.font.fredoka, FontWeight.Normal, variationSettings = gewicht(400)),
    Font(R.font.fredoka, FontWeight.Medium, variationSettings = gewicht(500)),
    Font(R.font.fredoka, FontWeight.SemiBold, variationSettings = gewicht(600)),
    Font(R.font.fredoka, FontWeight.Bold, variationSettings = gewicht(700)),
)

val Baloo2 = FontFamily(
    Font(R.font.baloo2, FontWeight.Normal, variationSettings = gewicht(400)),
    Font(R.font.baloo2, FontWeight.Medium, variationSettings = gewicht(500)),
    Font(R.font.baloo2, FontWeight.SemiBold, variationSettings = gewicht(600)),
    Font(R.font.baloo2, FontWeight.Bold, variationSettings = gewicht(700)),
)

val PatrickHand = FontFamily(
    Font(R.font.patrick_hand, FontWeight.Normal),
)
