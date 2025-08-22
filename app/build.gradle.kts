plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.devtools.ksp") version "2.0.21-1.0.27"
    id("com.google.dagger.hilt.android") version "2.56.2"
    id("androidx.navigation.safeargs.kotlin")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.baek.untitledproject"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.baek.untitledproject"
        minSdk = 26
        targetSdk = 35
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
        viewBinding = true
    }

}

hilt {
    enableAggregatingTask = false
}


dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    //viewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.9.0")

    //coroutine
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.9.0")

    //fragment
    val fragment_version = "1.8.8"
    implementation("androidx.fragment:fragment-ktx:$fragment_version")

    //material
    val material_version = "1.12.0"
    implementation("com.google.android.material:material:$material_version")

    //navigation
    val nav_version = "2.9.0"
    implementation("androidx.navigation:navigation-fragment:$nav_version")
    implementation("androidx.navigation:navigation-ui:$nav_version")

    //Room
    val room_version = "2.7.2"
    implementation("androidx.room:room-runtime:$room_version")
    ksp("androidx.room:room-compiler:$room_version")

    //hilt
    implementation("com.google.dagger:hilt-android:2.56.2")
    ksp("com.google.dagger:hilt-android-compiler:2.56.2")
    implementation("androidx.hilt:hilt-navigation-fragment:1.2.0")

    //ViewPager2
    implementation("androidx.viewpager2:viewpager2:1.1.0")

    //Glide
    implementation("com.github.bumptech.glide:glide:4.16.0")

    //firebase
    implementation(platform("com.google.firebase:firebase-bom:34.0.0"))
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-storage")
    implementation("com.google.firebase:firebase-auth")

    //Kizitonwose CalendarView
    implementation("com.kizitonwose.calendar:view:2.6.1")

    implementation("androidx.activity:activity-ktx:1.10.1")

    //PhotoView
    implementation("com.github.chrisbanes:PhotoView:2.3.0")

    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

    //datastore
    implementation("androidx.datastore:datastore-preferences:1.1.7")
    implementation("androidx.datastore:datastore-preferences-core:1.1.7")
}