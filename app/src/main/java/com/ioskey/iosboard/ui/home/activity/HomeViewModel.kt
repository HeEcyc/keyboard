package com.ioskey.iosboard.ui.home.activity

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ioskey.iosboard.R
import com.ioskey.iosboard.data.KeyboardTheme
import com.ioskey.iosboard.data.NewTheme
import com.ioskey.iosboard.data.Theme
import com.ioskey.iosboard.data.db.ThemeDataBase
import com.ioskey.iosboard.databinding.ItemThemeHomeBinding
import com.ioskey.iosboard.repository.PrefsReporitory
import com.ioskey.iosboard.ui.base.AppBaseAdapter
import com.ioskey.iosboard.ui.base.BaseViewModel
import com.ioskey.iosboard.util.themesOther
import com.ioskey.iosboard.util.themesPopular
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel : BaseViewModel() {

    val needRequestLayout = MutableLiveData<Unit>()
    val addNew = MutableLiveData<Unit>()

    val selectedTheme = ObservableField<KeyboardTheme>(themesPopular.first())
    val isSelectedThemePreset = ObservableBoolean(true)

    val adapterCustom = AppBaseAdapter.Builder<Theme, ItemThemeHomeBinding>(R.layout.item_theme_home).apply {
        onItemClick = ::onThemeClick
        initItems = listOf(NewTheme)
    }.build()
    val adapterPopular = AppBaseAdapter.Builder<Theme, ItemThemeHomeBinding>(R.layout.item_theme_home).apply {
        onItemClick = ::onThemeClick
        initItems = themesPopular
    }.build()
    val adapterOther = AppBaseAdapter.Builder<Theme, ItemThemeHomeBinding>(R.layout.item_theme_home).apply {
        onItemClick = ::onThemeClick
        initItems = themesOther
    }.build()

    init {
        viewModelScope.launch(Dispatchers.Main) {
            val themes = withContext(Dispatchers.IO) { ThemeDataBase.dataBase.getThemesDao().getTheme().reversed() }
            themes.forEach { it.isSelected = it.id == PrefsReporitory.keyboardTheme?.id }
            adapterCustom.addItems(themes)
        }
        observe(selectedTheme) { _, _ -> needRequestLayout.postValue(Unit) }
    }

    fun onThemeApply(keyboardTheme: KeyboardTheme) {
        viewModelScope.launch(Dispatchers.IO) {
            delay(1000)
            withContext(Dispatchers.Main) {
                keyboardTheme.isSelected = true
                adapterCustom
                    .getData()
                    .filterIsInstance(KeyboardTheme::class.java)
                    .firstOrNull { it.id == keyboardTheme.id }
                    ?.let { currentTheme ->
//                        currentTheme.isSelected = true
                        currentTheme.copyTheme(keyboardTheme)
                        adapterCustom.updateItem(currentTheme)
                    } ?: adapterCustom.addItem(1, keyboardTheme)
            }
        }
    }

    private fun onThemeClick(theme: Theme) {
        if (theme is NewTheme) {
            addNew.postValue(Unit)
        } else if (theme is KeyboardTheme) {
            selectedTheme.set(theme)
            listOf(
                *adapterCustom.getData().toTypedArray(),
                *adapterPopular.getData().toTypedArray(),
                *adapterOther.getData().toTypedArray()
            ).forEach { (it as? KeyboardTheme)?.isSelectedObservable?.set(it == theme) }
            isSelectedThemePreset.set(false)
        }
    }

    fun apply() {
        PrefsReporitory.keyboardTheme = selectedTheme.get()
    }

    class Factory : ViewModelProvider.Factory {

        override fun <T : ViewModel?> create(modelClass: Class<T>) = HomeViewModel() as T

    }

}
