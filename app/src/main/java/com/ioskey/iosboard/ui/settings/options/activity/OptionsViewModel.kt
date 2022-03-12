package com.ioskey.iosboard.ui.settings.options.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ioskey.iosboard.ui.base.BaseViewModel

class OptionsViewModel : BaseViewModel() {

    class Factory : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>) = OptionsViewModel() as T
    }

}
