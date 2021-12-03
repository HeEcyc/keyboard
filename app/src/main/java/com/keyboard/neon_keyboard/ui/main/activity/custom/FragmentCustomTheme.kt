package com.keyboard.neon_keyboard.ui.main.activity.custom

import androidx.fragment.app.activityViewModels
import com.keyboard.neon_keyboard.R
import com.keyboard.neon_keyboard.databinding.MainFragmentCustomThemeBinding
import com.keyboard.neon_keyboard.ui.base.BaseFragment
import com.keyboard.neon_keyboard.ui.main.activity.MainActivityViewModel

class FragmentCustomTheme :
    BaseFragment<MainActivityViewModel, MainFragmentCustomThemeBinding>(R.layout.main_fragment_custom_theme) {

    private val viewModel: MainActivityViewModel by activityViewModels()

    override fun provideViewModel() = viewModel

    override fun setupUI() {
        viewModel.loadSavedThemes()
    }

}
