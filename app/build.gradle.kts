plugins {
    id("com.android.application") version "7.0.1"
    kotlin("android") version "1.5.20"
    kotlin("kapt") version "1.5.20"
    kotlin("plugin.serialization") version "1.5.20"
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

android {
    compileSdk = 30
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
        applicationId = "com.live.keyboard"
        minSdk = 23
        targetSdk = 30
        versionCode = 53
        versionName = "0.3.13"

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

        externalNativeBuild {
            cmake {
                cFlags("-fvisibility=hidden", "-DU_STATIC_IMPLEMENTATION=1")
                cppFlags(
                    "-fvisibility=hidden",
                    "-std=c++17",
                    "-fexceptions",
                    "-ffunction-sections",
                    "-fdata-sections",
                    "-DU_DISABLE_RENAMING=1",
                    "-DU_STATIC_IMPLEMENTATION=1"
                )
                arguments("-DANDROID_STL=c++_static")
            }
        }

        sourceSets {
            maybeCreate("main").apply {
                assets {
                    srcDirs("src/main/assets", "src/main/icu4c/prebuilt/assets")
                }
                jniLibs {
                    srcDirs("src/main/icu4c/prebuilt/jniLibs")
                }
            }
        }
    }

    buildFeatures {
        viewBinding = true
        dataBinding = true
    }

    externalNativeBuild {
        cmake {
            path("src/main/cpp/CMakeLists.txt")
        }
    }

    buildTypes {
        named("debug").configure {
//            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"

            isDebuggable = true
            isJniDebuggable = true

            resValue("mipmap", "floris_app_icon", "@mipmap/ic_app_icon_debug")
            resValue("mipmap", "floris_app_icon_round", "@mipmap/ic_app_icon_debug_round")
            resValue("string", "floris_app_name", "FlorisBoard Debug")
        }

        create("beta") // Needed because by default the "beta" BuildType does not exist
        named("beta").configure {
//            applicationIdSuffix = ".beta"
            versionNameSuffix = "-beta10"
            proguardFiles.add(getDefaultProguardFile("proguard-android-optimize.txt"))

            resValue("mipmap", "floris_app_icon", "@mipmap/ic_app_icon_beta")
            resValue("mipmap", "floris_app_icon_round", "@mipmap/ic_app_icon_beta_round")
            resValue("string", "floris_app_name", "FlorisBoard Beta")
        }

        named("release").configure {
            proguardFiles.add(getDefaultProguardFile("proguard-android-optimize.txt"))

            resValue("mipmap", "floris_app_icon", "@mipmap/ic_app_icon_release")
            resValue("mipmap", "floris_app_icon_round", "@mipmap/ic_app_icon_release_round")
            resValue("string", "floris_app_name", "@string/app_name")
        }
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }

    lint {
        isAbortOnError = false
    }
}

dependencies {
    implementation("androidx.activity", "activity-ktx", "1.2.1")
    implementation("androidx.appcompat", "appcompat", "1.2.0")
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
    implementation("androidx.room", "room-runtime", "2.2.6")
    implementation("com.google.code.gson", "gson", "2.8.8")

    implementation("com.google.firebase:firebase-bom:28.4.1")
    implementation("com.google.firebase:firebase-storage")
    implementation("com.google.firebase:firebase-storage-ktx:20.0.0")
    implementation("com.google.firebase:firebase-analytics-ktx:19.0.2")
    implementation("com.google.firebase:firebase-messaging:22.0.0")
    implementation("com.google.firebase:firebase-crashlytics:18.2.3")

    implementation("com.appsflyer:af-android-sdk:6.3.2")

    implementation("com.makeramen:roundedimageview:2.3.0")
    implementation("com.github.bumptech.glide:glide:4.12.0")

    implementation(files("libs/color_picker.aar"))
    implementation(files("libs/imagepicker.aar"))
    implementation(files("libs/crop_view.aar"))

    kapt("androidx.room", "room-compiler", "2.2.6")
    implementation(project(":particles"))
    testImplementation(kotlin("test"))
    testImplementation("androidx.test", "core", "1.3.0")
    testImplementation("org.mockito", "mockito-inline", "3.7.7")
    testImplementation("org.robolectric", "robolectric", "4.5.1")



    androidTestImplementation("androidx.test.ext", "junit", "1.1.2")
    androidTestImplementation("androidx.test.espresso", "espresso-core", "3.3.0")

    // BACKGROUND VIEWS

    implementation("com.github.doctoror.particlesdrawable:library:1.0.9")
}
