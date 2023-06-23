rootProject.name = "CombaBoard"

include(":app")
include(":PremiumSDK")

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()

        flatDir {
            dirs("libs")
        }

        maven { url = uri("https://jitpack.io") }
        maven { url = uri("https://artifactory.appodeal.com/appodeal") }
        maven { url = uri("https://android-sdk.is.com") }
        maven { url = uri("https://imobile-maio.github.io/maven") }
        maven { url = uri("https://fan-adn.github.io/nendSDK-Android-lib/library") }
        maven { url = uri("https://maven.ogury.co") }
        maven { url = uri("https://artifact.bytedance.com/repository/pangle") }
        maven { url = uri("https://s3.amazonaws.com/smaato-sdk-releases/") }
        maven { url = uri("https://sdk.tapjoy.com") }
        maven { url = uri("https://verve.jfrog.io/artifactory/verve-gradle-release") }
        maven { url = uri("https://cboost.jfrog.io/artifactory/chartboost-ads/") }
    }
}

pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }

    // allows the plugins syntax to be used with the android gradle plugin
    resolutionStrategy.eachPlugin {
        if (requested.id.id == "com.android.application") {
            useModule("com.android.tools.build:gradle:${requested.version}")
        }
    }
}
