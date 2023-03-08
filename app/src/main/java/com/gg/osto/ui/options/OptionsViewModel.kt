package com.gg.osto.ui.options

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.gg.osto.ui.base.BaseViewModel

class OptionsViewModel : BaseViewModel() {

    class Factory : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>) = OptionsViewModel() as T
    }

}
