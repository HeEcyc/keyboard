package dev.patrickgold.florisboard.ui.custom

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import dev.patrickgold.florisboard.R
import dev.patrickgold.florisboard.databinding.CustomNavBarBinding

class CustomNavBar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    val binding: CustomNavBarBinding = DataBindingUtil.inflate(
        LayoutInflater.from(context),
        R.layout.custom_nav_bar,
        this,
        true
    )!!


    init {

    }


}
