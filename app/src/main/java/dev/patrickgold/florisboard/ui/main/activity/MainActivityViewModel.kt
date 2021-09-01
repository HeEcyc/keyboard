package dev.patrickgold.florisboard.ui.main.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dev.patrickgold.florisboard.R
import dev.patrickgold.florisboard.adapters.VPAdapter
import dev.patrickgold.florisboard.databinding.ItemKeyboardThemeBinding
import dev.patrickgold.florisboard.ui.base.BaseViewModel
import dev.patrickgold.florisboard.ui.base.createAdapter
import dev.patrickgold.florisboard.ui.custom.ThemesItemDecoration
import dev.patrickgold.florisboard.util.SingleLiveData

class MainActivityViewModel(val adapter: VPAdapter) : BaseViewModel() {

    val onThemeClick = SingleLiveData<String>()
    val keyboardItemDecoration = ThemesItemDecoration(2, 30)
    val assetsThemeAdapter = createAdapter<String, ItemKeyboardThemeBinding>(R.layout.item_keyboard_theme) {
        initItems = arrayListOf("", "", "", "", "", "", "", "", "", "", "", "")
        onItemClick = { onThemeClick.postValue("") }
    }

    fun loadAssets() {

    }


    class Factory(private val adapter: VPAdapter) : ViewModelProvider.Factory {

        override fun <T : ViewModel?> create(modelClass: Class<T>) = MainActivityViewModel(adapter) as T

    }

}
