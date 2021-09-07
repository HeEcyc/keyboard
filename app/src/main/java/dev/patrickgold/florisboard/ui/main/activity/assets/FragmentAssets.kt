package dev.patrickgold.florisboard.ui.main.activity.assets

import androidx.fragment.app.activityViewModels
import dev.patrickgold.florisboard.R
import dev.patrickgold.florisboard.databinding.MainFragmentAssetsBinding
import dev.patrickgold.florisboard.ui.base.BaseFragment
import dev.patrickgold.florisboard.ui.main.activity.MainActivityViewModel

class FragmentAssets :
    BaseFragment<MainActivityViewModel, MainFragmentAssetsBinding>(R.layout.main_fragment_assets) {

    private val viewModel: MainActivityViewModel by activityViewModels()

    override fun provideViewModel() = viewModel

    override fun setupUI() {
        viewModel.loadAssetsThemes()
    }
}
