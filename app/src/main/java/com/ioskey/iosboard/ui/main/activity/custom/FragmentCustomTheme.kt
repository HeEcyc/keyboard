package com.ioskey.iosboard.ui.main.activity.custom

import androidx.fragment.app.activityViewModels
import com.ioskey.iosboard.R
import com.ioskey.iosboard.databinding.MainFragmentCustomThemeBinding
import com.ioskey.iosboard.ui.base.BaseFragment
import com.ioskey.iosboard.ui.main.activity.MainActivityViewModel

class FragmentCustomTheme :
    BaseFragment<MainActivityViewModel, MainFragmentCustomThemeBinding>(R.layout.main_fragment_custom_theme) {

    private val viewModel: MainActivityViewModel by activityViewModels()

    override fun provideViewModel() = viewModel

    override fun setupUI() {
        viewModel.loadSavedThemes()
    }

}
