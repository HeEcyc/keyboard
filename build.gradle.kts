plugins {
    base // adds clean task to root project
}

subprojects {
    repositories {
        mavenCentral()
        google()
        maven("https://jitpack.io")
        maven("https://maven.google.com")
    }
}

buildscript {
    dependencies {
        classpath("com.android.tools.build:gradle:7.0.2")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.10")
        classpath("org.jetbrains.kotlin:kotlin-serialization:1.6.20-RC2")
        classpath("com.google.gms:google-services:4.3.10")
        classpath("com.google.firebase:firebase-crashlytics-gradle:2.7.1")
    }
}
