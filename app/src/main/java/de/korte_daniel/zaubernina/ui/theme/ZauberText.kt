package de.korte_daniel.zaubernina.ui.theme

import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit

/**
 * Der einzige Text der App.
 *
 * Bewusst [BasicText] statt Material3: die App zeigt vier Textstellen und braucht weder
 * Material-Farbschema noch dessen Bauteile — die Themen bringen ihre Farben selbst mit.
 * Die ganze Material3-Bibliothek dafür mitzuschleppen, macht das Programm nur größer.
 *
 * Die Schriftart kommt immer aus dem Thema, deshalb steht sie hier nicht als Parameter.
 */
@Composable
fun ZauberText(
    text: String,
    groesse: TextUnit,
    farbe: Color,
    gewicht: FontWeight = FontWeight.Medium,
    modifier: Modifier = Modifier,
) {
    BasicText(
        text = text,
        modifier = modifier,
        style = TextStyle(
            color = farbe,
            fontSize = groesse,
            fontWeight = gewicht,
            fontFamily = LocalZauberSchrift.current,
        ),
    )
}
