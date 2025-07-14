plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.kotlin.android)
  alias(libs.plugins.kotlin.compose)
  id("kotlin-kapt")
  id("com.google.dagger.hilt.android")
}

android {
  namespace = "com.example.myapplication"
  compileSdk = 36
  defaultConfig {
    applicationId = "com.example.myapplication"
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
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }
  kotlinOptions {
    jvmTarget = "17"
  }
  buildFeatures {
    compose = true
  }
}

dependencies {
  implementation(platform(libs.androidx.compose.bom))
  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.lifecycle.runtime.ktx)
  implementation(libs.androidx.activity.compose)
  implementation(libs.androidx.ui)
  implementation(libs.androidx.ui.graphics)
  implementation(libs.androidx.ui.tooling.preview)
  implementation(libs.androidx.material3)
  implementation(libs.androidx.navigation.compose)
  implementation(libs.androidx.lifecycle.viewmodel.compose)

  implementation(libs.gson)

  implementation(libs.hilt.android)
  kapt(libs.hilt.compiler)
  kapt(libs.androidx.hilt.compiler)
  implementation(libs.androidx.hilt.navigation.compose)

  implementation(libs.okhttp)
  implementation(libs.logging.interceptor)

  implementation(libs.retrofit)
  implementation(libs.converter.gson)

  implementation(libs.androidx.compose.ui.ui)
  implementation(libs.ui.graphics)
  implementation(libs.androidx.compose.ui.ui.tooling.preview)
  debugImplementation(libs.ui.tooling)
  implementation(libs.coil.compose)
  implementation(libs.coil.network.okhttp)
  implementation("com.github.franmontiel:PersistentCookieJar:v1.0.1")

  // Coil for image loading
//  implementation(libs.coil.compose)
//  implementation(libs.androidx.datastore.preferences)


  testImplementation(libs.junit)
  androidTestImplementation(libs.androidx.junit)
  androidTestImplementation(libs.androidx.espresso.core)
  androidTestImplementation(platform(libs.androidx.compose.bom))
  androidTestImplementation(libs.androidx.ui.test.junit4)
  debugImplementation(libs.androidx.ui.tooling)
  debugImplementation(libs.androidx.ui.test.manifest)
}
