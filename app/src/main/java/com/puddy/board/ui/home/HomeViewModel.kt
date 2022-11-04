package com.puddy.board.ui.home

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.puddy.board.data.KeyboardTheme
import com.puddy.board.data.NewTheme
import com.puddy.board.data.Theme
import com.puddy.board.data.db.ThemeDataBase
import com.puddy.board.databinding.ItemThemeBinding
import com.puddy.board.repository.PrefsReporitory
import com.puddy.board.ui.base.BaseViewModel
import com.puddy.board.ui.base.createAdapter
import com.puddy.board.util.themesPreset
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel : BaseViewModel() {

    val onAddClick = MutableLiveData<Unit>()
    val onThemeClick = MutableLiveData<KeyboardTheme>()
    val onApplied = MutableLiveData<Unit>()

    val theme = ObservableField<KeyboardTheme?>()

    val presetSelected = ObservableBoolean(true)
    val customSelected = ObservableBoolean(false)

    val adapterPreset = createAdapter<Theme, ViewDataBinding> {
        initItems = themesPreset
        viewBinding = { inflater, viewGroup, _ -> ItemThemeBinding.inflate(inflater, viewGroup, false) }
        onBind = onBind@ { theme, _ ->
            if (theme !is KeyboardTheme) return@onBind
            theme.isSelectedObservable.set(theme.id == PrefsReporitory.keyboardTheme?.id && theme.backgroundImagePath == PrefsReporitory.keyboardTheme?.backgroundImagePath)
        }
        onItemClick = {
            onThemeClick.postValue(it as KeyboardTheme)
            theme.set(it)
        }
    }

    val adapterCustom = createAdapter<Theme, ViewDataBinding> {
        initItems = listOf(NewTheme)
        viewBinding = { inflater, viewGroup, _ -> ItemThemeBinding.inflate(inflater, viewGroup, false) }
        onItemClick = {
            if (it is NewTheme) onAddClick.postValue(Unit)
            else {
                onThemeClick.postValue(it as KeyboardTheme)
                theme.set(it)
            }
        }
    }

    init {
        viewModelScope.launch(Dispatchers.Main) {
            val themes = withContext(Dispatchers.IO) { ThemeDataBase.dataBase.getThemesDao().getTheme().reversed() }
            themes.forEach { it.isSelectedObservable.set(it.id == PrefsReporitory.keyboardTheme?.id && it.backgroundImagePath == PrefsReporitory.keyboardTheme?.backgroundImagePath) }
            adapterCustom.addItems(themes)
        }
    }

    fun apply() {
        PrefsReporitory.keyboardTheme = theme.get()
        listOf(
            *adapterPreset.getData().toTypedArray(),
            *adapterCustom.getData().toTypedArray()
        ).mapNotNull { it as? KeyboardTheme }.forEach { it.isSelectedObservable.set(it === theme.get()) }
        onApplied.postValue(Unit)
    }

    fun onPresetClick() {
        customSelected.set(false)
        presetSelected.set(true)
    }

    fun onCustomClick() {
        presetSelected.set(false)
        customSelected.set(true)
    }

    class Factory : ViewModelProvider.Factory {

        override fun <T : ViewModel?> create(modelClass: Class<T>) = HomeViewModel() as T

    }

}
