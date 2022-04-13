rootProject.name = "NeonKeyboard"

pluginManagement {
    repositories {
        jcenter()

        mavenCentral()
        google()
        maven{url("https://jitpack.io")}
        maven{url("https://maven.google.com")}
        maven("https://android-sdk.is.com/")
    }

    // allows the plugins syntax to be used with the android gradle plugin
    resolutionStrategy.eachPlugin {
        if (requested.id.id == "com.android.application") {
            useModule("com.android.tools.build:gradle:${requested.version}")
        }
    }
}
include(":app")
include(":particles")
