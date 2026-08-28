package de.korte_daniel.zaubernina.ui

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import de.korte_daniel.zaubernina.data.grundschrift.WOERTER
import de.korte_daniel.zaubernina.logic.Genauigkeit
import de.korte_daniel.zaubernina.ui.theme.Thema
import de.korte_daniel.zaubernina.ui.theme.ZauberTheme
import de.korte_daniel.zaubernina.ui.tracing.UebungsBildschirm

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Beim Üben soll der Bildschirm nicht ausgehen — ein Kind fährt langsam nach und
        // berührt dabei minutenlang dieselbe Stelle.
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContent { Zaubernina() }
    }
}

@Composable
private fun Zaubernina() {
    // Noch im Speicher statt in DataStore: die Themenwahl wandert mit dem Elternbereich
    // in die dauerhafte Ablage.
    var thema by remember { mutableStateOf(Thema.NACHTHIMMEL) }
    val genauigkeit = Genauigkeit.NORMAL

    var wortIndex by remember { mutableIntStateOf(0) }
    var buchstabeIndex by remember { mutableIntStateOf(0) }
    val wort = WOERTER[wortIndex]

    ZauberTheme(thema) {
        val farben = ZauberTheme.farben
        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawBehind { drawRect(farben.hintergrundPinsel(size)) },
        ) {
            Box(modifier = Modifier.fillMaxSize().systemBarsPadding()) {
                UebungsBildschirm(
                    wort = wort,
                    buchstabeIndex = buchstabeIndex,
                    thema = thema,
                    genauigkeit = genauigkeit,
                    onThemaWechsel = { thema = it },
                    onBuchstabeFertig = {
                        buchstabeIndex = (buchstabeIndex + 1) % wort.length
                    },
                    onWortWechsel = {
                        wortIndex = (wortIndex + 1) % WOERTER.size
                        buchstabeIndex = 0
                    },
                )
            }
        }
    }
}
