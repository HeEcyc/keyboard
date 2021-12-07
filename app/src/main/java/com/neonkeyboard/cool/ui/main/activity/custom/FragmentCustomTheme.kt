package com.neonkeyboard.cool.ui.main.activity.custom

import androidx.fragment.app.activityViewModels
import com.neonkeyboard.cool.R
import com.neonkeyboard.cool.databinding.MainFragmentCustomThemeBinding
import com.neonkeyboard.cool.ui.base.BaseFragment
import com.neonkeyboard.cool.ui.main.activity.MainActivityViewModel

class FragmentCustomTheme :
    BaseFragment<MainActivityViewModel, MainFragmentCustomThemeBinding>(R.layout.main_fragment_custom_theme) {

    private val viewModel: MainActivityViewModel by activityViewModels()

    override fun provideViewModel() = viewModel

    override fun setupUI() {
        viewModel.loadSavedThemes()
    }

}
