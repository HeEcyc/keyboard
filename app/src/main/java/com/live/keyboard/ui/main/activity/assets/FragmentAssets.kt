package com.live.keyboard.ui.main.activity.assets

import androidx.fragment.app.activityViewModels
import com.live.keyboard.R
import com.live.keyboard.databinding.MainFragmentAssetsBinding
import com.live.keyboard.ui.base.BaseFragment
import com.live.keyboard.ui.main.activity.MainActivityViewModel

class FragmentAssets :
    BaseFragment<MainActivityViewModel, MainFragmentAssetsBinding>(R.layout.main_fragment_assets) {

    private val viewModel: MainActivityViewModel by activityViewModels()

    override fun provideViewModel() = viewModel

    override fun setupUI() {
        viewModel.loadAssetsThemes()
    }
}
