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
        classpath("com.google.gms:google-services:4.3.15")
    }
}
