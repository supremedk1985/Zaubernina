// Wurzel-Buildfile. Bewusst schmal: die App braucht weder Hilt noch KSP noch Serialization.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
}
