import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

val localProperties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) file.inputStream().use(::load)
}

fun localValue(name: String, fallback: String): String =
    localProperties.getProperty(name, fallback)

android {
    namespace = "org.iesalixar.pps.radiografia"
    compileSdk = 35

    defaultConfig {
        applicationId = "org.iesalixar.pps.radiografia"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        buildConfigField("String", "APP_ENV", "\"${localValue("APP_ENV", "development")}\"")
        buildConfigField("String", "API_BASE_URL", "\"${localValue("API_BASE_URL", "https://api.example.test")}\"")
        buildConfigField("String", "DEMO_API_KEY", "\"${localValue("DEMO_API_KEY", "not-configured")}\"")
    }

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    testImplementation("junit:junit:4.13.2")
}
