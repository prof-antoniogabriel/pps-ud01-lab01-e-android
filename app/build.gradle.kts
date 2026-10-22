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

        // Variables incorporadas al BuildConfig de la aplicación.
        buildConfigField(
            "String",
            "APP_ENV",
            "\"${localValue("APP_ENV", "development")}\""
        )

        buildConfigField(
            "String",
            "API_BASE_URL",
            "\"${localValue("API_BASE_URL", "https://api.example.test")}\""
        )

        buildConfigField(
            "String",
            "DEMO_API_KEY",
            "\"${localValue("DEMO_API_KEY", "not-configured")}\""
        )
    }

    // Genera la clase BuildConfig para poder acceder
    // a los valores definidos mediante buildConfigField.
    buildFeatures {
        buildConfig = true
    }

    // Java y Kotlin deben generar bytecode compatible.
    // Se utiliza Java 17 para este proyecto Android.
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    // Kotlin debe utilizar el mismo target que Java.
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {

    // Framework de pruebas unitarias.
    testImplementation("junit:junit:4.13.2")
}