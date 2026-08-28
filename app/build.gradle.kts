plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "de.korte_daniel.zaubernina"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "de.korte_daniel.zaubernina"
        minSdk = 29
        targetSdk = 36
        versionCode = 1
        versionName = "0.1"
    }

    buildTypes {
        // Eigene App-ID wie bei CurruBike: die Entwicklungsfassung soll neben einer
        // späteren Store-Version installierbar bleiben.
        debug {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
        }
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
        // Der Build, der aufs Handy geht. Übernimmt die komplette Release-Konfiguration
        // (dieselben R8-Regeln), signiert aber mit dem Debug-Schlüssel und trägt dieselbe
        // App-ID wie debug — er ersetzt die Entwicklungsfassung also als Update.
        //
        // Ohne ihn wären es 31 MB, davon 31 MB unverkleinerte Compose-Bibliothek. Das ist
        // zugleich der einzige Weg, die Verkleinerung ohne Release-Schlüssel auf einem
        // Gerät auszuprobieren: R8-Fehler zeigen sich erst zur Laufzeit, nicht beim Bauen.
        create("minified") {
            initWith(getByName("release"))
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-schlank"
            signingConfig = signingConfigs.getByName("debug")
            proguardFile("proguard-rules-lesbar.pro")
            matchingFallbacks += listOf("release")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

dependencies {
    implementation(libs.core.ktx)
    implementation(libs.activity.compose)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.lifecycle.runtime.compose)
    implementation(libs.lifecycle.viewmodel.compose)

    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.foundation)
    implementation(libs.compose.ui.tooling.preview)
    debugImplementation(libs.compose.ui.tooling)

    implementation(libs.datastore.preferences)
    implementation(libs.coroutines.android)

    testImplementation(libs.junit)
    testImplementation(libs.coroutines.test)
}

// Veröffentlicht den verkleinerten Build unter der festen Adresse
// http://192.168.178.10/zaubernina.apk (nginx-Webroot auf nexus). Bewusst NICHT der
// Debug-Build: der ist rund siebenmal so groß und nur für den Emulator gedacht.
// Auf fremden Rechnern tut die Aufgabe nichts.
val nexusWebroot = file("/opt/stacks/web/html")
val veroeffentlicheApk = tasks.register<Copy>("veroeffentlicheApk") {
    onlyIf { nexusWebroot.isDirectory }
    from(layout.buildDirectory.file("outputs/apk/minified/app-minified.apk"))
    into(nexusWebroot)
    rename { "zaubernina.apk" }
}
tasks.matching { it.name == "assembleMinified" }.configureEach {
    finalizedBy(veroeffentlicheApk)
}
