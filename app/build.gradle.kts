import java.io.FileInputStream
import java.util.Properties

plugins {
    id(Plugin.ANDROID_APPLICATION)
    id(Plugin.JETBRAINS_KOTLIN_ANDROID)
    id(Plugin.ANDROIDX_NAVIGATION_SAFEARGS)
    id(Plugin.GMS_GOOGLE_SERVICES)
    id(Plugin.FIREBASE_CRASHLYTICS)
    id(Plugin.JETBRAINS_KOTLIN_KAPT)
    id(Plugin.HILT_ANDROID)
}

val properties = Properties().apply {
    load(FileInputStream(rootProject.file("secret.properties")))
}

android {
    namespace = "com.glion.skinscanner_and"
    compileSdk = AppConfig.COMPILE_SDK

    defaultConfig {
        applicationId = "com.glion.skinscanner_and"
        minSdk = AppConfig.MIN_SDK
        targetSdk = AppConfig.TARGET_SDK
        versionCode = AppConfig.APP_VERSION_CODE
        versionName = AppConfig.APP_VERSION

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "KAKAO_NATIVE_KEY", properties.getProperty("KAKAO_NATIVE_KEY"))
        buildConfigField("String", "KAKAO_MAP_KEY", properties.getProperty("KAKAO_MAP_KEY"))
        buildConfigField("String", "KAKAO_REST_KEY", properties.getProperty("KAKAO_REST_KEY"))
        buildConfigField("String", "KAKAO_BASE_URL", properties.getProperty("KAKAO_BASE_URL"))
        // 테스트 데이터셋 URL 저장
        buildConfigField("String", "DATA_SET_URL", properties.getProperty("DATA_SET_URL"))
        // 테스트 서버 URL 저장
        buildConfigField("String", "OUTER_SERVER", properties.getProperty("OUTER_SERVER"))
        buildConfigField("String", "INNER_SERVER", properties.getProperty("INNER_SERVER"))
    }

//    buildTypes {
//        debug {
//            // 테스트용 보상형 광고 App Id manifestPlaceholders 로 저장
//            manifestPlaceholders["AD_APP_ID"] = properties.getProperty("TEST_AD_APP_ID") as String
//            // 보상형 광고 ID buldConfigField 에 저장
//            buildConfigField("String", "AD_ID", properties.getProperty("TEST_AD_ID"))
//        }
//        release {
//            // 보상형 광고 App Id manifestPlaceholders 로 저장
//            manifestPlaceholders["AD_APP_ID"] = properties.getProperty("REWARD_AD_APP_ID") as String
//            // 보상형 광고 ID buldConfigField 에 저장
//            buildConfigField("String", "AD_ID", properties.getProperty("REWARD_AD_ID"))
//            isMinifyEnabled = false
//            proguardFiles(
//                getDefaultProguardFile("proguard-android-optimize.txt"),
//                "proguard-rules.pro"
//            )
//        }
//    }
    buildFeatures {
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    dataBinding {
        enable = true
    }

    androidResources {
        ignoreAssetsPattern = "tflite" // tflite 압축하지 않은 상태로 저장할 자산으로 지정
    }
}

dependencies {
    implementation(Library.ANDROIDX_CORE_KTX)
    implementation(Library.ANDROIDX_APPCOMPAT)
    implementation(Library.MATERIAL)
    implementation(Library.ANDROIDX_ACTIVITY)
    implementation(Library.ANDROIDX_CONSTRAINT_LAYOUT)

    // For SplashScreen
    implementation(Library.ANDROIDX_CORE_SPLASHSCREEN)

    // cameraX
    implementation(Library.ANDROIDX_CAMERA_CORE)
    implementation(Library.ANDROIDX_CAMERA_CAMERA2)
    implementation(Library.ANDROIDX_CAMERA_LIFECYCLE)
    implementation(Library.ANDROIDX_CAMERA_VIDEO)
    implementation(Library.ANDROIDX_CAMERA_VIEW)
    implementation(Library.ANDROIDX_CAMERA_EXTENSIONS)

    // tensorflow lite
    implementation(Library.TENSORFLOW_LITE)
    implementation(Library.TENSORFLOW_LITE_SELECT_TF_OPS)
    // tensorflow lite support library
    implementation(Library.TENSORFLOW_LITE_V000_NIGHT_SNAPSHOT)
    // The GPU delegate library is optional. Depend on it as needed.
    implementation(Library.TENSORFLOW_LITE_GPU)
    implementation(Library.TENSORFLOW_LITE_SUPPORT_V044)

    // Glide
    implementation(Library.GLIDE)

    // Image Cropper
    implementation(Library.VANNIKTECH_ANDROID_IMAGE_CROPPER)

    // KakaoSdk
    implementation(Library.V2_ALL) // 전체 모듈 설치, 2.11.0 버전부터 지원
    // KakaoMap Sdk
    implementation(Library.KAKAO_MAP_ANDROID)

    // Retrofit2
    implementation(Library.RETROFIT)

    // Google play service location
    // implementation(Library.PLAY_SERVICES_LOCATION)
    // AdMob
    // implementation(Library.PLAY_SERVICES_ADS)

    // firebase bom
    // implementation(platform(Library.FIREBASE_BOM))
    // firebase crashlytics
    // implementation(Library.FIREBASE_CRASHLYTICS)
    // firebase analytics
    // implementation(Library.FIREBASE_ANALYTICS)
    // firebase realtime database
    // implementation(Library.FIREBASE_DATABASE)

    // android fragment navigation
    implementation(Library.ANDROIDX_NAVIGATION_FRAGMENT)
    implementation(Library.ANDROIDX_NAVIGATION_UI)

    // hilt
    implementation(Library.HILT_ANDROID)
    kapt(Library.HILT_ANDROID_COMPILER)

    // SwipeRefreshLayout
    implementation(Library.ANDROIDX_SWIPE_REFRESH_LAYOUT)

    // lottie
    implementation(Library.LOTTIE)

    // datastore
    implementation(Library.ANDROIDX_DATASTORE_PREFERENCES)

    // splash API
    implementation(Library.ANDROIDX_CORE_SPLASHSCREEN)

    testImplementation(Library.JUNIT)
    testImplementation(Library.ANDROIDX_JUNIT)
    testImplementation(Library.ANDROIDX_ESPRESSO_CORE)
}