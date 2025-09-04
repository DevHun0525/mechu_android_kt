//only first time
import java.io.FileInputStream
import java.util.Properties

var KAKAO_SDK_VERSION = "2.21.6"

//var KAKAO_MAP_VERSION = " 2.12.8"
var KAKAO_MAP_VERSION = "2.9.5"

val properties = Properties().apply {
    load(FileInputStream(rootProject.file("local.properties")))
}

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.gms.google-services")
    id("org.jetbrains.kotlin.plugin.compose") version libs.versions.kotlin.get()
    id("com.google.devtools.ksp")
}

android {
    namespace = "io.github.devhun0525.mechu"
    compileSdk = 36

    defaultConfig {
        applicationId = "io.github.devhun0525.mechu"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        android.buildFeatures.buildConfig = true
        android.buildFeatures.viewBinding = true
        buildFeatures {
            compose = true
        }
        /*buildConfigField(
            "String",
            "NAVER_MAPS_CLIENT_ID",
            "\"${properties.getProperty("NAVER_MAPS_CLIENT_ID")}\""
        )*/

        buildConfigField(
            "String",
            "KAKAO_MAPS_NATIVE_KEY",
            "\"${properties.getProperty("KAKAO_MAPS_NATIVE_KEY")}\""
        )

        buildConfigField(
            "String",
            "KAKAO_MAPS_REST_API_KEY",
            "\"${properties.getProperty("KAKAO_MAPS_REST_API_KEY")}\""
        )

        buildConfigField(
            "String",
            "NAVER_CLIENT_ID",
            "\"${properties.getProperty("NAVER_CLIENT_ID")}\""
        )

        buildConfigField(
            "String",
            "NAVER_CLIENT_SECRET",
            "\"${properties.getProperty("NAVER_CLIENT_SECRET")}\""
        )

        ndk {
            abiFilters.add("arm64-v8a")
            abiFilters.add("armeabi-v7a")
            abiFilters.add("x86")
            abiFilters.add("x86_64")
        }

        manifestPlaceholders["MY_KAKAO_KEY"] = properties.getProperty("KAKAO_MAPS_REST_API_KEY")
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
}


dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.material3)
    implementation(libs.firebase.crashlytics.buildtools)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    //naver map
//    implementation("com.naver.maps:map-sdk:3.22.1")
    //naver login
    implementation("com.navercorp.nid:oauth:5.10.0") // jdk 11

    //kakao map
    implementation("com.kakao.maps.open:android:${KAKAO_MAP_VERSION}")
    implementation("com.google.android.gms:play-services-location:21.0.1") // 최신 버전으로 확인해주세요.

    // 카카오 로그인 API 모듈
//    implementation("com.kakao.sdk:v2-all:${KAKAO_SDK_VERSION}")
    implementation("com.kakao.sdk:v2-user:${KAKAO_SDK_VERSION}")

    // navigation
    implementation("androidx.navigation:navigation-fragment-ktx:2.5.3")
    implementation("androidx.navigation:navigation-ui-ktx:2.5.3")


    // Retrofit (Making an HTTP request)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    // Gson Converter (JSON <-> Data Class)
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    // OkHttp Logging Interceptor (네트워크 로그 확인용 - 선택)
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")


    // firebase
    implementation(platform("com.google.firebase:firebase-bom:34.1.0"))
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")

    // goggle
    implementation("com.google.gms:google-services:4.4.3")


    // camerax
    val camerax_version = "1.5.0-rc01"
    implementation("androidx.camera:camera-core:${camerax_version}")
    implementation("androidx.camera:camera-camera2:${camerax_version}")
    implementation("androidx.camera:camera-lifecycle:${camerax_version}")
    implementation("androidx.camera:camera-video:${camerax_version}")
    implementation("androidx.camera:camera-view:${camerax_version}")
    implementation("androidx.camera:camera-mlkit-vision:${camerax_version}")
    implementation("androidx.camera:camera-extensions:${camerax_version}")


    // Jetpack Compose
    val composeBom = platform("androidx.compose:compose-bom:2024.06.00") // BOM 버전 확인
    implementation(composeBom)
    androidTestImplementation(composeBom)

    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3") // Material 3
    // implementation("androidx.compose.material:material") // Material 2 (M3 권장)
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    implementation("androidx.activity:activity-compose:1.9.0") // ComponentActivity에서 setContent 사용
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.3") // ViewModel과 Compose 통합
    val nav_version = "2.9.3"
    implementation("androidx.navigation:navigation-compose:$nav_version")

    val room_version = "2.7.2"

    implementation("androidx.room:room-runtime:$room_version")

    // If this project uses any Kotlin source, use Kotlin Symbol Processing (KSP)
    // See Add the KSP plugin to your project
    ksp("androidx.room:room-compiler:$room_version")

    // If this project only uses Java source, use the Java annotationProcessor
    // No additional plugins are necessary
    annotationProcessor("androidx.room:room-compiler:$room_version")

    // optional - Kotlin Extensions and Coroutines support for Room
    implementation("androidx.room:room-ktx:$room_version")

    // optional - RxJava2 support for Room
    implementation("androidx.room:room-rxjava2:$room_version")

    // optional - RxJava3 support for Room
    implementation("androidx.room:room-rxjava3:$room_version")

    // optional - Guava support for Room, including Optional and ListenableFuture
    implementation("androidx.room:room-guava:$room_version")

    // optional - Test helpers
    testImplementation("androidx.room:room-testing:$room_version")

    // optional - Paging 3 Integration
    implementation("androidx.room:room-paging:$room_version")



    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.9")



}
