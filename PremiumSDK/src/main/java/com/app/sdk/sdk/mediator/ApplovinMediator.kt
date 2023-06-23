package com.app.sdk.sdk.mediator

import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import com.app.sdk.sdk.config.SdkConfig
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdListener
import com.applovin.mediation.MaxError
import com.applovin.mediation.ads.MaxInterstitialAd
import com.applovin.sdk.AppLovinPrivacySettings
import com.applovin.sdk.AppLovinSdk

class ApplovinMediator constructor(mediatorCallBack: MediatorCallBack) :
    Mediator(mediatorCallBack), MaxAdListener {

    private var reward: MaxInterstitialAd? = null

    override fun initMediator(activity: ComponentActivity) {

        AppLovinPrivacySettings.setHasUserConsent(true, activity)
        AppLovinPrivacySettings.setIsAgeRestrictedUser(false, activity)
        AppLovinPrivacySettings.setDoNotSell(false, activity)

        with(AppLovinSdk.getInstance(activity as AppCompatActivity)) {
            mediationProvider = "max"
            if (isInitialized) mediatorCallBack.startLoadAd(this@ApplovinMediator)
            else initializeSdk { mediatorCallBack.startLoadAd(this@ApplovinMediator) }
        }
    }

    override fun loadAd(activity: ComponentActivity) {
        reward = MaxInterstitialAd(SdkConfig.adUnitId, activity as AppCompatActivity)
            .apply { setListener(this@ApplovinMediator); this.loadAd() }
    }

    override fun closeAd() {
        isActual = false
        reward?.setListener(null)
        reward?.destroy()
    }

    override fun onAdLoaded(ad: MaxAd) {
        if (isActual) reward?.showAd()
    }

    override fun onAdDisplayed(ad: MaxAd) {
        mediatorCallBack.onDisplay()
    }

    override fun onAdHidden(ad: MaxAd?) {
        mediatorCallBack.onHide()
    }

    override fun onAdClicked(ad: MaxAd?) {
        mediatorCallBack.onClicked()
    }

    override fun onAdLoadFailed(adUnitId: String?, error: MaxError?) {
        mediatorCallBack.onError()
    }

    override fun onAdDisplayFailed(ad: MaxAd?, error: MaxError?) {
        mediatorCallBack.onError()
    }

}
