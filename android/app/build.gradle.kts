plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.hilt.android)
}

android {
    namespace = "com.gdpt.huantu"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.gdpt.huantu"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // API 基础地址（电脑局域网IP，真机/模拟器通用）
        buildConfigField("String", "API_BASE_URL", "\"http://192.168.2.106:8080/\"")
        // 腾讯地图 Key
        buildConfigField("String", "TENCENT_MAP_KEY", "\"JZBBZ-V42LB-CEGUY-JSOBW-KJJO5-KGFN4\"")

    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isMinifyEnabled = false
            isDebuggable = true
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {
    // ==================== AndroidX 核心 ====================
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.fragment)
    implementation(libs.constraintlayout)
    implementation(libs.swiperefreshlayout)
    implementation(libs.viewpager2)
    implementation(libs.recyclerview)
    implementation(libs.cardview)

    // ==================== Jetpack ====================
    implementation(libs.lifecycle.viewmodel)
    implementation(libs.lifecycle.livedata)
    implementation(libs.lifecycle.runtime)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.room.runtime)
    annotationProcessor(libs.room.compiler)

    // ==================== Hilt 依赖注入 ====================
    implementation(libs.hilt.android)
    annotationProcessor(libs.hilt.compiler)

    // ==================== 网络 ====================
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)
    implementation(libs.gson)

    // ==================== 图片加载 ====================
    implementation(libs.glide)
    annotationProcessor(libs.glide.compiler)

    // ==================== UI 组件 ====================
    implementation(libs.lottie)
    implementation(libs.circleimageview)

    // ==================== 阿里云 OSS ====================
    implementation(libs.oss)

    // ==================== 定位 ====================
    implementation(libs.play.services.location)

    // ==================== 测试 ====================
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
