plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp") version "1.9.20-1.0.14"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.0"
}

android {
    namespace = "com.sather.todo"
    compileSdk = 34
    
    defaultConfig {
        applicationId = "com.sather.todo"
        minSdk = 34
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }
    
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
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
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    
    // DataStore
    implementation ("androidx.datastore:datastore-preferences:1.1.0")
    
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
//    微件
    // For AppWidgets support
    implementation("androidx.glance:glance-appwidget:1.1.0")
    implementation("androidx.glance:glance-preview:1.1.0")
    implementation("androidx.glance:glance-appwidget-preview:1.1.0")
    // For interop APIs with Material 3
    implementation( "androidx.glance:glance-material3:1.1.0")
    debugImplementation("androidx.glance:glance-preview:1.1.0")
    debugImplementation("androidx.glance:glance-appwidget-preview:1.1.0")
    
//    动画效果
    implementation("androidx.compose.animation:animation:1.5.0")
    implementation("androidx.compose.foundation:foundation:1.7.5")
    implementation("androidx.navigation:navigation-compose:2.8.3")
    implementation( "androidx.lifecycle:lifecycle-runtime-compose:2.8.6")
    implementation( "androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.6")
    implementation( "androidx.lifecycle:lifecycle-viewmodel-savedstate:2.8.6")
    implementation( "androidx.lifecycle:lifecycle-livedata-ktx:2.8.6")
    implementation( "androidx.lifecycle:lifecycle-viewmodel-compose:2.8.6")
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.6")
    implementation("androidx.activity:activity-compose:1.9.2")
    implementation(platform("androidx.compose:compose-bom:2023.08.00"))
    implementation("androidx.compose.ui:ui:1.7.4")
    implementation("androidx.compose.ui:ui-graphics:1.7.4")
    implementation("androidx.compose.ui:ui-tooling-preview:1.7.4")
    implementation( "androidx.compose.material:material-icons-extended:1.7.4")
    implementation("androidx.compose.material3:material3:1.3.0")
    implementation("androidx.compose.material3:material3-adaptive-navigation-suite-android:1.3.0")
    
//    Room
    implementation("androidx.work:work-runtime-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    implementation("androidx.room:room-common:2.6.1")
    implementation("androidx.room:room-runtime:2.6.1")
    // SQLite Testing
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    
    
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.08.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.7.4")
    debugImplementation("androidx.compose.ui:ui-tooling:1.7.4")
    debugImplementation("androidx.compose.ui:ui-test-manifest:1.7.6")
}