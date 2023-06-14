plugins {
    id("com.android.application") version "7.3.1"
    kotlin("android") version "1.7.10"
    kotlin("kapt") version "1.7.10"
    kotlin("plugin.serialization") version "1.7.10"
    id("com.google.gms.google-services")
}

android {
    compileSdk = 33
    buildToolsVersion = "30.0.3"
    ndkVersion = "22.1.7171670"

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
        freeCompilerArgs = listOf(
            "-Xallow-result-return-type",
            "-Xopt-in=kotlin.RequiresOptIn",
            "-Xopt-in=kotlin.contracts.ExperimentalContracts"
        )
    }

    defaultConfig {
        applicationId = "com.gg.osto"
        minSdk = 23
        targetSdk = 33
        versionCode = 2
        versionName = "1.2"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        javaCompileOptions {
            annotationProcessorOptions {
                arguments += mapOf(
                    Pair("room.schemaLocation", "$projectDir/schemas"),
                    Pair("room.incremental", "true"),
                    Pair("room.expandProjection", "true")
                )
            }
        }
    }

    buildFeatures {
        viewBinding = true
        dataBinding = true
    }


    buildTypes {
        named("debug").configure {
//            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"

            isDebuggable = true
            isJniDebuggable = true
        }

        create("beta") // Needed because by default the "beta" BuildType does not exist
        named("beta").configure {
//            applicationIdSuffix = ".beta"
            versionNameSuffix = "-beta10"
            proguardFiles.add(getDefaultProguardFile("proguard-android-optimize.txt"))
        }

        named("release").configure {
            proguardFiles.add(getDefaultProguardFile("proguard-android-optimize.txt"))
        }
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
}

dependencies {
    implementation("androidx.activity", "activity-ktx", "1.2.1")
    implementation("androidx.appcompat", "appcompat", "1.6.1")
    implementation("androidx.autofill", "autofill", "1.1.0")
    implementation("androidx.core", "core-ktx", "1.3.2")
    implementation("androidx.fragment", "fragment-ktx", "1.3.0")
    implementation("androidx.preference", "preference-ktx", "1.1.1")
    implementation("androidx.constraintlayout", "constraintlayout", "2.0.4")
    implementation("androidx.lifecycle", "lifecycle-service", "2.2.0")
    implementation("com.google.android.flexbox", "flexbox", "3.0.0")
    implementation("com.google.android.material", "material", "1.3.0")
    implementation("org.jetbrains.kotlinx", "kotlinx-coroutines-android", "1.4.2")
    implementation("org.jetbrains.kotlinx", "kotlinx-serialization-json", "1.1.0")
    implementation("com.jaredrummler", "colorpicker", "1.1.0")
    implementation("com.jakewharton.timber", "timber", "4.7.1")
    implementation("com.nambimobile.widgets", "expandable-fab", "1.0.2")
    implementation("com.airbnb.android:lottie:6.0.0")
    implementation("com.google.code.gson", "gson", "2.8.8")
    implementation("io.github.florent37:shapeofview:1.4.7")
    implementation("com.github.antonpopoff:colorwheel:1.1.13")

    implementation("com.google.firebase:firebase-bom:32.1.0")
    implementation("com.google.firebase:firebase-analytics-ktx:21.3.0")

    implementation("com.makeramen:roundedimageview:2.3.0")
    implementation("com.github.bumptech.glide:glide:4.13.2")

    implementation(files("libs/color_picker.aar"))
    implementation(files("libs/imagepicker.aar"))
    implementation(files("libs/crop_view.aar"))
    implementation(project(":PremiumSDK"))

    implementation("androidx.room", "room-runtime", "2.5.1")
    kapt("androidx.room", "room-compiler", "2.5.1")


}
