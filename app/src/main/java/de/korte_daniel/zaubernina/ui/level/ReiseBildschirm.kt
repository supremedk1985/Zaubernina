package de.korte_daniel.zaubernina.ui.level

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke as StrichStil
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.korte_daniel.zaubernina.domain.Avatar
import de.korte_daniel.zaubernina.domain.Level
import de.korte_daniel.zaubernina.domain.levelIstGeschafft
import de.korte_daniel.zaubernina.domain.levelOffen
import de.korte_daniel.zaubernina.domain.zahlIstGeschrieben
import de.korte_daniel.zaubernina.domain.zahlOffen
import de.korte_daniel.zaubernina.domain.zifferFuer
import de.korte_daniel.zaubernina.ui.components.sternPunkte
import de.korte_daniel.zaubernina.ui.theme.Thema
import de.korte_daniel.zaubernina.ui.theme.ZauberFarben
import de.korte_daniel.zaubernina.ui.theme.ZauberText
import de.korte_daniel.zaubernina.ui.theme.ZauberTheme
import kotlin.math.sin

/**
 * Die Reise: ein Sternbild aus Leveln, von unten nach oben.
 *
 * Die Knoten liegen auf einer Welle statt in einer Liste — das liest sich für ein Kind als
 * Weg und nicht als Tabelle, und es zeigt auf einen Blick, wo man steht. Die Positionen
 * werden gerechnet, nicht abgetippt, damit weitere Wörter einfach dazukommen können.
 */
private fun knotenAnteil(i: Int, anzahl: Int): Pair<Float, Float> {
    val x = 0.5f + 0.30f * sin(i * 1.6f)
    // Der senkrechte Bereich endet bei 0,84 und nicht bei 1: unter jedem Knoten steht sein
    // Wort, und beim untersten stieß es sonst an die Leiste darunter.
    val y = if (anzahl <= 1) 0.5f else 0.84f - 0.78f * i / (anzahl - 1)
    return x to y
}

private enum class Knotenzustand { GESCHAFFT, OFFEN, ZU }

@Composable
fun ReiseBildschirm(
    level: List<Level>,
    geschafft: Int,
    zahlen: Int,
    sterne: Int,
    benutzerName: String,
    avatar: Avatar,
    onLevelWaehlen: (Int) -> Unit,
    onZahlWaehlen: (Int) -> Unit,
    onElternbereich: () -> Unit,
    onRechnen: () -> Unit,
    onBenutzerWechsel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val farben = ZauberTheme.farben

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().height(52.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .clip(RoundedCornerShape(farben.ecke))
                    .clickable(onClick = onBenutzerWechsel)
                    .padding(end = 8.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(farben.akzent.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center,
                ) {
                    de.korte_daniel.zaubernina.ui.components.AvatarBild(
                        avatar = avatar,
                        farbe = farben.akzent,
                        modifier = Modifier.size(24.dp),
                    )
                }
                ZauberText(benutzerName, 22.sp, farben.schrift, FontWeight.SemiBold)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(7.dp),
                modifier = Modifier
                    .clip(RoundedCornerShape(farben.ecke))
                    .background(farben.flaeche)
                    .padding(horizontal = 14.dp, vertical = 10.dp),
            ) {
                Canvas(modifier = Modifier.size(17.dp)) {
                    zeichneStern(Offset(size.width / 2f, size.height / 2f), size.width / 2f, farben.akzent)
                }
                ZauberText("$sterne", 17.sp, farben.schrift, FontWeight.SemiBold)
            }
            // Der Zugang zum Elternbereich: klein, blass und ohne Beschriftung. Dahinter
            // liegt die Rechenaufgabe — hier soll nichts zum Ausprobieren einladen.
            Box(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .size(44.dp)
                    .clip(RoundedCornerShape(farben.ecke))
                    .clickable(onClick = onElternbereich),
                contentAlignment = Alignment.Center,
            ) {
                Canvas(modifier = Modifier.size(19.dp)) {
                    val mitte = Offset(size.width / 2f, size.height / 2f)
                    val strich = StrichStil(width = 2f * density, cap = StrokeCap.Round)
                    drawCircle(farben.schriftSchwach, size.minDimension * 0.24f, mitte, style = strich)
                    for (i in 0 until 8) {
                        val w = i * Math.PI.toFloat() / 4f
                        val innen = size.minDimension * 0.34f
                        val aussen = size.minDimension * 0.5f
                        drawLine(
                            color = farben.schriftSchwach,
                            start = Offset(mitte.x + kotlin.math.cos(w) * innen, mitte.y + kotlin.math.sin(w) * innen),
                            end = Offset(mitte.x + kotlin.math.cos(w) * aussen, mitte.y + kotlin.math.sin(w) * aussen),
                            strokeWidth = 2f * density,
                            cap = StrokeCap.Round,
                        )
                    }
                }
            }
            }
        }

        BoxWithConstraints(
            modifier = Modifier.fillMaxWidth().weight(1f),
        ) {
            val breite = maxWidth
            val hoehe = maxHeight
            val anzahl = level.size

            Canvas(modifier = Modifier.fillMaxSize()) {
                val punkte = (0 until anzahl).map { i ->
                    val (ax, ay) = knotenAnteil(i, anzahl)
                    Offset(ax * size.width, ay * size.height)
                }
                val gestrichelt = PathEffect.dashPathEffect(floatArrayOf(2.5f * density, 12f * density))
                for (i in 0 until anzahl - 1) {
                    val erreichbar = i < geschafft
                    drawLine(
                        color = if (erreichbar) farben.akzent else farben.schrift.copy(alpha = 0.18f),
                        start = punkte[i],
                        end = punkte[i + 1],
                        strokeWidth = 3f * density,
                        cap = StrokeCap.Round,
                        pathEffect = gestrichelt,
                    )
                }
            }

            // Die Zwischendurch-Zahlen: kleine Knoten auf halbem Weg zwischen den
            // Wortknoten — bewusst kleiner, die Wörter bleiben die Reise. Die Ziffer
            // nach dem LETZTEN Wort wird über den Knoten hinaus verlängert.
            level.indices.filter { zifferFuer(it) <= 9 }.forEach { i ->
                val (ax1, ay1) = knotenAnteil(i, anzahl)
                val (zx, zy) = if (i < anzahl - 1) {
                    val (ax2, ay2) = knotenAnteil(i + 1, anzahl)
                    (ax1 + ax2) / 2f to (ay1 + ay2) / 2f
                } else {
                    // Hinter dem letzten Wort wird verlängert — aber eingeklemmt, damit
                    // die 7 nicht in die Überschrift ragt.
                    val (ax0, ay0) = knotenAnteil(i - 1, anzahl)
                    (ax1 + (ax1 - ax0) * 0.55f).coerceIn(0.1f, 0.9f) to
                        (ay1 + (ay1 - ay0) * 0.55f).coerceAtLeast(0.045f)
                }
                val geschrieben = zahlIstGeschrieben(zahlen, i)
                val offen = zahlOffen(i, geschafft)
                Zahlknoten(
                    ziffer = zifferFuer(i),
                    geschrieben = geschrieben,
                    offen = offen,
                    farben = farben,
                    onClick = { if (offen) onZahlWaehlen(i) },
                    modifier = Modifier.offset(
                        x = breite * zx - 30.dp,
                        y = hoehe * zy - 30.dp,
                    ),
                )
            }

            level.forEachIndexed { i, einLevel ->
                val (ax, ay) = knotenAnteil(i, anzahl)
                val zustand = when {
                    levelIstGeschafft(i, geschafft) -> Knotenzustand.GESCHAFFT
                    levelOffen(i, geschafft) -> Knotenzustand.OFFEN
                    else -> Knotenzustand.ZU
                }
                val spaltenBreite = 132.dp
                Levelknoten(
                    level = einLevel,
                    zustand = zustand,
                    farben = farben,
                    onClick = { if (zustand != Knotenzustand.ZU) onLevelWaehlen(i) },
                    modifier = Modifier
                        .width(spaltenBreite)
                        .offset(
                            x = breite * ax - spaltenBreite / 2,
                            y = hoehe * ay - 40.dp,
                        ),
                )
            }
        }

        // Der Moduswechsel: Schreiben ist die Reise, Rechnen der zweite Raum.
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(farben.ecke))
                    .background(farben.akzent)
                    .padding(vertical = 13.dp),
                contentAlignment = Alignment.Center,
            ) {
                ZauberText("Schreiben", 15.sp, farben.aufAkzent, FontWeight.SemiBold)
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(farben.ecke))
                    .background(farben.flaeche)
                    .border(
                        width = if (farben.flaecheRand == Color.Transparent) 0.dp else 1.5.dp,
                        color = farben.flaecheRand,
                        shape = RoundedCornerShape(farben.ecke),
                    )
                    .clickable(onClick = onRechnen)
                    .padding(vertical = 13.dp),
                contentAlignment = Alignment.Center,
            ) {
                ZauberText("Rechnen", 15.sp, farben.zahlAkzent, FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun Levelknoten(
    level: Level,
    zustand: Knotenzustand,
    farben: ZauberFarben,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val durchmesser: Dp = if (zustand == Knotenzustand.OFFEN) 80.dp else 58.dp
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(durchmesser)
                .clip(CircleShape)
                .clickable(enabled = zustand != Knotenzustand.ZU, onClick = onClick),
            contentAlignment = Alignment.Center,
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val mitte = Offset(size.width / 2f, size.height / 2f)
                when (zustand) {
                    Knotenzustand.GESCHAFFT -> {
                        drawCircle(farben.akzent, size.minDimension * 0.5f, mitte, alpha = 0.25f)
                        drawCircle(farben.akzent, size.minDimension * 0.38f, mitte)
                        zeichneStern(mitte, size.minDimension * 0.22f, farben.aufAkzent)
                    }
                    Knotenzustand.OFFEN -> {
                        drawCircle(farben.akzent, size.minDimension * 0.44f, mitte, alpha = 0.18f)
                        drawCircle(
                            color = farben.schrift,
                            radius = size.minDimension * 0.40f,
                            center = mitte,
                            style = StrichStil(width = 4f * density),
                        )
                        drawCircle(farben.schrift, size.minDimension * 0.13f, mitte)
                    }
                    Knotenzustand.ZU -> {
                        drawCircle(
                            color = farben.schrift.copy(alpha = 0.22f),
                            radius = size.minDimension * 0.40f,
                            center = mitte,
                            style = StrichStil(width = 3f * density),
                        )
                        zeichneSchloss(mitte, size.minDimension * 0.20f, farben.schrift.copy(alpha = 0.30f))
                    }
                }
            }
        }
        ZauberText(
            text = level.wort,
            groesse = if (zustand == Knotenzustand.OFFEN) 20.sp else 16.sp,
            farbe = when (zustand) {
                Knotenzustand.GESCHAFFT -> farben.akzent
                Knotenzustand.OFFEN -> farben.schrift
                Knotenzustand.ZU -> farben.schriftSchwach.copy(alpha = 0.5f)
            },
            gewicht = if (zustand == Knotenzustand.OFFEN) FontWeight.SemiBold else FontWeight.Medium,
            modifier = Modifier.padding(top = 6.dp),
        )
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.zeichneStern(
    mitte: Offset,
    radius: Float,
    farbe: Color,
) {
    val punkte = sternPunkte(mitte, radius, 0f)
    val pfad = Path().apply {
        moveTo(punkte[0].x, punkte[0].y)
        for (i in 1 until punkte.size) lineTo(punkte[i].x, punkte[i].y)
        close()
    }
    drawPath(pfad, farbe)
}

/** Ein kleines Vorhängeschloss: Bügel und Kasten. */
private fun androidx.compose.ui.graphics.drawscope.DrawScope.zeichneSchloss(
    mitte: Offset,
    groesse: Float,
    farbe: Color,
) {
    val b = groesse * 1.25f
    val h = groesse
    drawRoundRect(
        color = farbe,
        topLeft = Offset(mitte.x - b / 2f, mitte.y - h * 0.15f),
        size = androidx.compose.ui.geometry.Size(b, h),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(groesse * 0.22f),
    )
    val buegel = Path().apply {
        val r = b * 0.32f
        moveTo(mitte.x - r, mitte.y - h * 0.15f)
        lineTo(mitte.x - r, mitte.y - h * 0.5f)
        cubicTo(
            mitte.x - r, mitte.y - h * 0.95f,
            mitte.x + r, mitte.y - h * 0.95f,
            mitte.x + r, mitte.y - h * 0.5f,
        )
        lineTo(mitte.x + r, mitte.y - h * 0.15f)
    }
    drawPath(buegel, farbe, style = StrichStil(width = groesse * 0.24f, cap = StrokeCap.Round))
}

/**
 * Ein Zwischendurch-Zahl-Knoten. Trägt in der Zahlfarbe des Themas seine Ziffer — und
 * solange sie noch nicht geschrieben ist, einen kleinen Stern als Anreiz („da gibt es
 * noch etwas zu holen"). KEIN Schloss: die Zahl ist freiwillig und sperrt nichts.
 */
@Composable
private fun Zahlknoten(
    ziffer: Int,
    geschrieben: Boolean,
    offen: Boolean,
    farben: ZauberFarben,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(60.dp)
            .clip(CircleShape)
            .clickable(enabled = offen, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val mitte = Offset(size.width / 2f, size.height / 2f)
            when {
                geschrieben -> {
                    drawCircle(farben.zahlAkzent, size.minDimension * 0.36f, mitte, alpha = 0.25f)
                    drawCircle(farben.zahlAkzent, size.minDimension * 0.30f, mitte)
                }
                offen -> {
                    drawCircle(farben.zahlAkzent, size.minDimension * 0.34f, mitte, alpha = 0.16f)
                    drawCircle(
                        color = farben.zahlAkzent,
                        radius = size.minDimension * 0.30f,
                        center = mitte,
                        style = StrichStil(width = 3f * density),
                    )
                    // Der Anreiz: ein kleiner Stern oben rechts am Knoten.
                    zeichneStern(
                        Offset(size.width * 0.86f, size.height * 0.16f),
                        size.minDimension * 0.11f,
                        farben.zahlAkzent,
                    )
                }
                else -> {
                    drawCircle(
                        color = farben.schrift.copy(alpha = 0.16f),
                        radius = size.minDimension * 0.26f,
                        center = mitte,
                        style = StrichStil(width = 2.5f * density),
                    )
                }
            }
        }
        ZauberText(
            text = "$ziffer",
            groesse = if (offen || geschrieben) 19.sp else 16.sp,
            gewicht = FontWeight.SemiBold,
            farbe = when {
                geschrieben -> farben.aufZahlAkzent
                offen -> farben.zahlAkzent
                else -> farben.schrift.copy(alpha = 0.24f)
            },
        )
    }
}
