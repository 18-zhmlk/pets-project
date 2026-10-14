plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    kotlin("kapt")
}

android {
    namespace = "com.example.pawpal"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.pawpal"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        viewBinding = true
    }
}

kapt {
    arguments {
        // Передаем путь к схемам Room как аргумент процессора аннотаций
        arg("room.schemaLocation", "$projectDir/schemas")
    }
}
android {
    buildFeatures {
        viewBinding = true
    }

}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation("androidx.cardview:cardview:1.0.0")
    implementation(libs.material.calendarview)
    implementation("androidx.fragment:fragment-ktx:1.7.0")
    implementation(libs.generativeai)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    val room_version = "2.6.1" // Рекомендуемая версия, если у вас не указана другая
    // Поддержка Kotlin Extensions (Flow, Coroutines)
    implementation("androidx.room:room-ktx:${room_version}")
    implementation("androidx.room:room-runtime:${room_version}")
    // Процессор аннотаций (ОБЯЗАТЕЛЕН для генерации кода Room)
    kapt("androidx.room:room-compiler:$room_version")
    implementation("com.google.android.material:material:1.12.0")
    implementation(libs.material.calendarview) // <-- Add this line
    implementation(libs.androidx.compose.material3)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}