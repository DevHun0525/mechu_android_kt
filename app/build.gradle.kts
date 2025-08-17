//only first time
import java.io.FileInputStream
import java.util.Properties

//var KAKAO_SDK_VERSION = "2.21.6"

//var KAKAO_MAP_VERSION = " 2.12.8"
var KAKAO_MAP_VERSION = "2.9.5"

val properties = Properties().apply {
    load(FileInputStream(rootProject.file("local.properties")))
}

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
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

        ndk {
            abiFilters.add("arm64-v8a")
            abiFilters.add("armeabi-v7a")
            abiFilters.add("x86")
            abiFilters.add("x86_64")
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

    //kakao map
    implementation("com.kakao.maps.open:android:${KAKAO_MAP_VERSION}")
    implementation("com.google.android.gms:play-services-location:21.0.1") // 최신 버전으로 확인해주세요.
//    implementation("com.kakao.sdk:v2-all:${KAKAO_SDK_VERSION}")

    // navigation
    implementation("androidx.navigation:navigation-fragment-ktx:2.5.3")
    implementation("androidx.navigation:navigation-ui-ktx:2.5.3")


    // Retrofit (Making an HTTP request)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    // Gson Converter (JSON <-> Data Class)
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    // OkHttp Logging Interceptor (네트워크 로그 확인용 - 선택)
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")
}