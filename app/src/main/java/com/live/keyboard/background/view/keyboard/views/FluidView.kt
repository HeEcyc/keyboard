package com.live.keyboard.background.view.keyboard.views

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.webkit.WebChromeClient
import android.webkit.WebView

@SuppressLint("SetJavaScriptEnabled")
class FluidView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : WebView(context, attrs, defStyleAttr, defStyleRes) {

    init {
        settings.javaScriptEnabled = true
        webChromeClient = WebChromeClient()
        loadUrl("file:///android_asset/html/fluid/index.html")
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        loadUrl("file:///android_asset/html/fluid/index.html")
    }

}
