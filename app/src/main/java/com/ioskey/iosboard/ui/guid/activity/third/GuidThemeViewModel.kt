package com.ioskey.iosboard.ui.guid.activity.third

import androidx.lifecycle.MutableLiveData
import com.ioskey.iosboard.R
import com.ioskey.iosboard.data.KeyboardTheme
import com.ioskey.iosboard.databinding.ItemThemeGuidBinding
import com.ioskey.iosboard.repository.PrefsReporitory
import com.ioskey.iosboard.ui.base.AppBaseAdapter
import com.ioskey.iosboard.ui.base.BaseViewModel
import com.ioskey.iosboard.util.themesPreset

class GuidThemeViewModel : BaseViewModel() {

    val finishActivity = MutableLiveData<Unit>()

    val adapter = AppBaseAdapter.Builder<KeyboardTheme, ItemThemeGuidBinding>(R.layout.item_theme_guid).apply {
        onItemClick = ::onThemeClick
        initItems = themesPreset
    }.build()

    private fun onThemeClick(theme: KeyboardTheme) {
        PrefsReporitory.keyboardTheme = theme
        finishActivity.postValue(Unit)
    }

}
