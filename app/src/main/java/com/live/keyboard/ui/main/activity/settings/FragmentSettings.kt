package com.live.keyboard.ui.main.activity.settings

import androidx.fragment.app.activityViewModels
import com.live.keyboard.R
import com.live.keyboard.databinding.MainFragmentSettingsBinding
import com.live.keyboard.ui.base.BaseFragment
import com.live.keyboard.ui.main.activity.MainActivityViewModel

class FragmentSettings :
    BaseFragment<MainActivityViewModel, MainFragmentSettingsBinding>(R.layout.main_fragment_settings) {

    private val viewModel: MainActivityViewModel by activityViewModels()

    override fun provideViewModel() = viewModel

    override fun setupUI() {

    }

}
