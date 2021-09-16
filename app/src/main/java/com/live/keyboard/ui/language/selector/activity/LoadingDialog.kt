package com.live.keyboard.ui.language.selector.activity

import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.live.keyboard.R

class LoadingDialog : DialogFragment(R.layout.language_loading_dialog) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = false
    }

    override fun onResume() {
        super.onResume()
        dialog?.window?.apply {
            setGravity(Gravity.CENTER)
            setBackgroundDrawableResource(android.R.color.transparent)
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        }
    }

}
