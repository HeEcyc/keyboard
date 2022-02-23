package com.ioskey.iosboard.ui.main.activity.assets

import androidx.fragment.app.activityViewModels
import com.ioskey.iosboard.R
import com.ioskey.iosboard.databinding.MainFragmentAssetsBinding
import com.ioskey.iosboard.ui.base.BaseFragment
import com.ioskey.iosboard.ui.main.activity.MainActivityViewModel

class FragmentAssets :
    BaseFragment<MainActivityViewModel, MainFragmentAssetsBinding>(R.layout.main_fragment_assets) {

    private val viewModel: MainActivityViewModel by activityViewModels()

    override fun provideViewModel() = viewModel

    override fun setupUI() {
        viewModel.loadAssetsThemes()
    }
}
