package de.korte_daniel.zaubernina.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import de.korte_daniel.zaubernina.domain.Avatar

/**
 * Die Avatare, von Hand gezeichnet statt als Foto. Bewusst: Die App hat keine
 * Berechtigungen — ein Foto-Avatar brächte Medienzugriff und gespeicherte Kinderbilder
 * mit. Jeder Avatar ist eine einfache Form in der übergebenen Farbe.
 */
@Composable
fun AvatarBild(avatar: Avatar, farbe: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val m = Offset(size.width / 2f, size.height / 2f)
        val r = size.minDimension / 2f
        when (avatar) {
            Avatar.STERN -> {
                val punkte = sternPunkte(m, r * 0.95f, 0f)
                val pfad = Path().apply {
                    moveTo(punkte[0].x, punkte[0].y)
                    for (i in 1 until punkte.size) lineTo(punkte[i].x, punkte[i].y)
                    close()
                }
                drawPath(pfad, farbe)
            }

            Avatar.MOND -> {
                // Sichel = großer Kreis minus versetzter Kreis. Die Pfad-Differenz ist
                // sauberer als zwei Bögen von Hand — die sahen zerbrochen aus.
                val voll = Path().apply {
                    addOval(Rect(m.x - r * 0.85f, m.y - r * 0.85f, m.x + r * 0.85f, m.y + r * 0.85f))
                }
                val abzug = Path().apply {
                    addOval(Rect(m.x - r * 0.55f, m.y - r * 1.05f, m.x + r * 1.05f, m.y + r * 0.55f))
                }
                drawPath(Path.combine(androidx.compose.ui.graphics.PathOperation.Difference, voll, abzug), farbe)
            }

            Avatar.HERZ -> {
                val pfad = Path().apply {
                    moveTo(m.x, m.y + r * 0.75f)
                    cubicTo(m.x - r * 1.3f, m.y - r * 0.1f, m.x - r * 0.6f, m.y - r * 1.0f, m.x, m.y - r * 0.3f)
                    cubicTo(m.x + r * 0.6f, m.y - r * 1.0f, m.x + r * 1.3f, m.y - r * 0.1f, m.x, m.y + r * 0.75f)
                    close()
                }
                drawPath(pfad, farbe)
            }

            Avatar.BLUME -> {
                for (i in 0 until 5) {
                    val w = i * 72f / 180f * Math.PI.toFloat() - Math.PI.toFloat() / 2f
                    drawCircle(
                        color = farbe,
                        radius = r * 0.34f,
                        center = Offset(m.x + kotlin.math.cos(w) * r * 0.52f, m.y + kotlin.math.sin(w) * r * 0.52f),
                    )
                }
                drawCircle(farbe.copy(alpha = 0.55f), r * 0.28f, m)
            }

            Avatar.RAKETE -> raketeZeichnen(m, r, farbe)

            Avatar.WOLKE -> {
                drawCircle(farbe, r * 0.38f, Offset(m.x - r * 0.42f, m.y + r * 0.1f))
                drawCircle(farbe, r * 0.5f, Offset(m.x + r * 0.05f, m.y - r * 0.18f))
                drawCircle(farbe, r * 0.36f, Offset(m.x + r * 0.5f, m.y + r * 0.12f))
                drawRect(
                    color = farbe,
                    topLeft = Offset(m.x - r * 0.42f, m.y + r * 0.1f),
                    size = androidx.compose.ui.geometry.Size(r * 0.92f, r * 0.38f),
                )
            }
        }
    }
}

private fun DrawScope.raketeZeichnen(m: Offset, r: Float, farbe: Color) {
    val pfad = Path().apply {
        // Rumpf mit Spitze, leicht schräg gedacht aber gerade gezeichnet — einfach lesbar.
        moveTo(m.x, m.y - r * 0.95f)
        cubicTo(m.x + r * 0.42f, m.y - r * 0.45f, m.x + r * 0.42f, m.y + r * 0.15f, m.x + r * 0.28f, m.y + r * 0.45f)
        lineTo(m.x - r * 0.28f, m.y + r * 0.45f)
        cubicTo(m.x - r * 0.42f, m.y + r * 0.15f, m.x - r * 0.42f, m.y - r * 0.45f, m.x, m.y - r * 0.95f)
        close()
    }
    drawPath(pfad, farbe)
    // Flossen
    val flosse = Path().apply {
        moveTo(m.x - r * 0.3f, m.y + r * 0.1f)
        lineTo(m.x - r * 0.7f, m.y + r * 0.62f)
        lineTo(m.x - r * 0.26f, m.y + r * 0.45f)
        close()
        moveTo(m.x + r * 0.3f, m.y + r * 0.1f)
        lineTo(m.x + r * 0.7f, m.y + r * 0.62f)
        lineTo(m.x + r * 0.26f, m.y + r * 0.45f)
        close()
    }
    drawPath(flosse, farbe.copy(alpha = 0.75f))
    // Bullauge und Flamme
    drawCircle(farbe.copy(alpha = 0.4f), r * 0.14f, Offset(m.x, m.y - r * 0.3f))
    val flamme = Path().apply {
        moveTo(m.x - r * 0.14f, m.y + r * 0.48f)
        cubicTo(m.x - r * 0.1f, m.y + r * 0.8f, m.x + r * 0.1f, m.y + r * 0.8f, m.x + r * 0.14f, m.y + r * 0.48f)
        close()
    }
    drawPath(flamme, farbe.copy(alpha = 0.6f))
}
