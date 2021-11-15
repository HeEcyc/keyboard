package com.keyboard.neon_keyboard.ui.dialogs

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.keyboard.neon_keyboard.R

class DialogInitialSelectDesign : DialogFragment(R.layout.initial_select_design_dialog) {

    var onSelected: (Design) -> Unit = {}

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isCancelable = false

        view.apply {
            findViewById<View>(R.id.closeButton).setOnClickListener { dismiss() }
            findViewById<View>(R.id.buttonPresets).setOnClickListener { onSelected(Design.PRESET); dismiss() }
            findViewById<View>(R.id.buttonCustom).setOnClickListener { onSelected(Design.CUSTOM); dismiss() }
        }
    }

    override fun onResume() {
        super.onResume()
        dialog?.window?.apply {
            setGravity(Gravity.CENTER)
            setBackgroundDrawableResource(android.R.color.transparent)
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        }
    }

    enum class Design { PRESET, CUSTOM }

}
