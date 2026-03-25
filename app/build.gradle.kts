plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.project_orion"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.project_orion"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    aaptOptions {
        // Diz para o Android NÃO tentar espremer arquivos .bin
        noCompress += "bin"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation("com.google.mediapipe:tasks-genai:0.10.29")
    implementation ("androidx.biometric:biometric:1.1.0")// Biometria e Senha
}
