plugins {
    id("com.android.library") version "7.3.1"
    id("org.jetbrains.kotlin.android") version "1.7.10"
    id("com.google.gms.google-services") apply false
}

android {
    compileSdk = 31
    buildToolsVersion = "30.0.3"
    ndkVersion = "22.1.7171670"

    namespace = "com.app.sdk"

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    defaultConfig {
        minSdk = 23
        targetSdk = 31
    }
}

dependencies {
    api(fileTree("src/main/libs") { include("*.aar") })

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.android.material:material:1.9.0")
    implementation("com.google.android.gms:play-services-ads-identifier")
    implementation("com.google.firebase:firebase-messaging-ktx:23.1.2")
    implementation("com.google.android.gms:play-services-base:18.2.0")

    implementation("com.applovin:applovin-sdk:11.9.0")
    implementation("com.kochava.tracker:tracker:4.1.0")

    //Mediators
    implementation("com.applovin.mediation:adcolony-adapter:+")
    implementation("com.applovin.mediation:chartboost-adapter:+")
    implementation("com.applovin.mediation:criteo-adapter:+")
    implementation("com.applovin.mediation:fyber-adapter:+")
    implementation("com.applovin.mediation:hyprmx-adapter:+")
    implementation("com.applovin.mediation:inmobi-adapter:+")
    implementation("com.squareup.picasso:picasso:2.71828")
    implementation("com.applovin.mediation:ironsource-adapter:+")
    implementation("com.applovin.mediation:line-adapter:+")
    implementation("com.applovin.mediation:maio-adapter:+")
    implementation("com.applovin.mediation:facebook-adapter:+")
    implementation("com.applovin.mediation:nend-adapter:+")
    implementation("com.applovin.mediation:ogury-presage-adapter:+")
    implementation("com.applovin.mediation:bytedance-adapter:+")
    implementation("com.applovin.mediation:smaato-adapter:+")
    implementation("com.applovin.mediation:tapjoy-adapter:+")
    implementation("com.applovin.mediation:unityads-adapter:+")
    implementation("com.applovin.mediation:verve-adapter:+")
    implementation("com.applovin.mediation:vungle-adapter:+")

    implementation("com.my.tracker:mytracker-sdk:3.0.10")
    implementation("androidx.work:work-runtime:2.8.1")
    implementation("com.google.guava:guava:31.0.1-android")

    implementation("com.android.installreferrer:installreferrer:2.2")

}
