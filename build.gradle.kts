plugins {
    base // adds clean task to root project
}

subprojects {
    repositories {
        mavenCentral()
        google()
    }
}
buildscript {
    dependencies {
        classpath("com.google.gms:google-services:4.3.10")
    }
}
