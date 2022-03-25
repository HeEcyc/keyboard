plugins {
    base // adds clean task to root project
}

subprojects {
    repositories {
        jcenter()
        mavenCentral()
        google()
        maven("https://jitpack.io")
        maven("https://maven.google.com")
    }
}

buildscript {
    repositories {
        jcenter()
        mavenCentral()
        google()
        maven("https://jitpack.io")
        maven("https://maven.google.com")
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.0.2")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.10")
        classpath("com.jfrog.bintray.gradle:gradle-bintray-plugin:1.8.5")
        classpath("org.jetbrains.kotlin:kotlin-serialization:1.6.20-RC2")
        classpath("com.google.gms:google-services:4.3.10")
        classpath("com.google.firebase:firebase-crashlytics-gradle:2.7.1")
    }
}

allprojects {
    repositories {
        jcenter()
        mavenCentral()
        google()
        maven("https://jitpack.io")
        maven("https://maven.google.com")
    }
}
