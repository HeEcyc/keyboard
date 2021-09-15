package com.live.keyboard.ui.main.activity.custom

import androidx.fragment.app.activityViewModels
import com.live.keyboard.R
import com.live.keyboard.databinding.MainFragmentCustomThemeBinding
import com.live.keyboard.ui.base.BaseFragment
import com.live.keyboard.ui.main.activity.MainActivityViewModel

class FragmentCustomTheme :
    BaseFragment<MainActivityViewModel, MainFragmentCustomThemeBinding>(R.layout.main_fragment_custom_theme) {

    private val viewModel: MainActivityViewModel by activityViewModels()

    override fun provideViewModel() = viewModel

    override fun setupUI() {
        viewModel.loadSavedThemes()
    }

}
