plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")

    id("kotlin-parcelize")
    id ("androidx.navigation.safeargs.kotlin")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.datn"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.datn"
        minSdk = 24
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures{
        viewBinding = true
    }
}

dependencies {
    implementation ("com.google.android.gms:play-services-auth:21.1.0")
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.annotation:annotation:1.7.1")
    implementation("com.google.firebase:firebase-database-ktx:21.0.0")
    implementation("com.google.firebase:firebase-auth:23.0.0")
    implementation("com.google.firebase:firebase-storage:21.0.0")
    implementation("org.jetbrains:annotations:15.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation ("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.15.1")

    implementation("androidx.datastore:datastore-preferences:1.0.0-alpha01")

    // LiveData
    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")

    implementation ("androidx.activity:activity-ktx:1.8.2")
    implementation ("androidx.fragment:fragment:1.6.2")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    // Room components
//    implementation ("androidx.room:room-runtime:2.6.1")
//    kapt ("androidx.room:room-compiler:2.6.1")
//    implementation ("androidx.room:room-ktx:2.6.1")
//    androidTestImplementation ("androidx.room:room-testing:2.6.1")
    // Kotlin
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")

    debugImplementation ("com.squareup.leakcanary:leakcanary-android:2.8.1")
    //Retrofit
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.squareup.okhttp3:logging-interceptor:4.9.0")
    implementation ("com.squareup.picasso:picasso:2.71828")

    implementation ("com.github.denzcoskun:ImageSlideshow:0.1.2")

    implementation("com.facebook.shimmer:shimmer:0.5.0")

    implementation ("de.hdodenhof:circleimageview:3.1.0")

    implementation ("io.github.yahiaangelo.markdownedittext:markdownedittext:1.1.3")

    implementation ("com.google.android.gms:play-services-auth:21.1.0")

    implementation("androidx.palette:palette-ktx:1.0.0")

    implementation ("com.google.android.material:material:1.13.0-alpha01")

    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("at.blogc:expandabletextview:1.0.5")

    implementation ("androidx.paging:paging-runtime-ktx:3.2.1")

    implementation ("com.squareup.retrofit2:converter-scalars:2.6.4")

    //map
    implementation ("com.google.android.gms:play-services-location:21.2.0")

}