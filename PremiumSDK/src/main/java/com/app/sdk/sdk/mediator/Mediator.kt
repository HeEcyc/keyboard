package com.app.sdk.sdk.mediator

import androidx.activity.ComponentActivity

abstract class Mediator(val mediatorCallBack: MediatorCallBack) {
    protected var isActual = true

    abstract fun initMediator(activity: ComponentActivity)

    abstract fun loadAd(activity: ComponentActivity)

    interface MediatorCallBack {

        fun startLoadAd(mediator: Mediator)

        fun onError()

        fun onHide()

        fun onClicked()

        fun onDisplay()

        fun displayMsg(msg: String)

    }

    abstract fun closeAd()
}