// ==========================================
// 📱 অ্যাপ লেভেল বিল্ড সেটিংস ও প্রিমিয়াম লাইব্রেরি
// ==========================================
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.global.livetv"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.global.livetv"
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")

    // গুগলের শক্তিশালী ExoPlayer (Media3 Core Engine)
    implementation("androidx.media3:media3-exoplayer:1.0.2")
    implementation("androidx.media3:media3-exoplayer-hls:1.0.2")
    implementation("androidx.media3:media3-ui:1.0.2")

    // নেটওয়ার্কিং, গুগল অ্যাডমব ও ইমেজ প্রসেসর
    implementation("com.google.android.gms:play-services-ads:22.1.0")
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.github.bumptech.glide:glide:4.15.1")
}
