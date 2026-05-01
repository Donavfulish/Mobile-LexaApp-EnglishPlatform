import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.androidx.navigation.safeargs)
    id("com.google.dagger.hilt.android")
    kotlin("kapt")
}

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(localPropertiesFile.inputStream())
}
val geminiApiKey = localProperties.getProperty("GEMINI_API_KEY") ?: ""
val baseUrl = localProperties.getProperty("SERVER_BASE_URL") ?: "http://10.0.2.2:8081/"

android {
    namespace = "com.home.lexa"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.home.lexa"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "GEMINI_KEY", "\"$geminiApiKey\"")
        buildConfigField("String", "SERVER_BASE_URL", "\"$baseUrl\"")

        vectorDrawables {
            useSupportLibrary = true
        }

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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
        viewBinding = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.gridlayout)
    implementation(libs.firebase.messaging)
    implementation(libs.androidx.navigation.runtime.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    val koinVersion = "3.5.3"
    val retrofitVersion = "2.9.0"
    val roomVersion = "2.6.1"
    val coroutinesVersion = "1.7.3"

    // 1. Dependency Injection: Koin
    implementation("io.insert-koin:koin-android:${koinVersion}")

    // 2. Networking: Retrofit & OkHttp & Kotlinx Serialization
    implementation("com.squareup.retrofit2:retrofit:$retrofitVersion")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
    implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:1.0.0")

    // 3. Local Caching: Room Database
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    // Lưu ý: Cần thêm plugin id("com.google.devtools.ksp") ở đầu file để Room hoạt động tốt với Kotlin
    // ksp("androidx.room:room-compiler:$roomVersion")

    // 4. Asynchronous: Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutinesVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")

    // 5. Image Loading: Coil
    implementation("io.coil-kt:coil:2.5.0")

    // 6. Firebase Auth (Hỗ trợ UC001 Login)
    implementation(platform("com.google.firebase:firebase-bom:32.7.1"))
    implementation("com.google.firebase:firebase-auth")

    // 7. UI Support: Material 3, Lottie (Animations), Vico (Biểu đồ)
    implementation("com.google.android.material:material:1.11.0")
    implementation("com.airbnb.android:lottie:6.3.0")
    implementation("com.patrykandpatrick.vico:core:1.13.1")
    implementation("com.patrykandpatrick.vico:views:1.13.1")

    // 8. OTP bar
    implementation ("io.github.chaosleung:pinview:1.4.4")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // 9. Auto-sizing text (one in many functions)
    implementation("com.intuit.sdp:sdp-android:1.1.1")
    implementation("com.intuit.ssp:ssp-android:1.1.1")

    //10. Hỗ trợ làm hiệu ứng skeleton khi loading
    implementation("com.facebook.shimmer:shimmer:0.5.0")

    //11. Hilt - dùng ViewModel tại MainActivity
    implementation("com.google.dagger:hilt-android:2.51.1")
    kapt("com.google.dagger:hilt-android-compiler:2.51.1")

    implementation("androidx.fragment:fragment-ktx:1.8.2")
    implementation("androidx.activity:activity-ktx:1.9.1")

    //12. Upload & Hiển thị ảnh
    implementation("io.coil-kt:coil:2.5.0")

    //13. Splash Screen
    implementation("androidx.core:core-splashscreen:1.0.1")
    implementation("androidx.recyclerview:recyclerview:1.3.2")

    //13. Display Image for ShapeableImageView
    implementation ("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.16.0")
    implementation("com.github.stfalcon-studio:StfalconImageViewer:1.0.1")

    implementation("com.google.ai.client.generativeai:generativeai:0.9.0")

}
