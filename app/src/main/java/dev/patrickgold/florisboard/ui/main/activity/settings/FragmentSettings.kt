package dev.patrickgold.florisboard.ui.main.activity.settings

import androidx.fragment.app.activityViewModels
import dev.patrickgold.florisboard.R
import dev.patrickgold.florisboard.databinding.MainFragmentSettingsBinding
import dev.patrickgold.florisboard.ui.base.BaseFragment
import dev.patrickgold.florisboard.ui.main.activity.MainActivityViewModel

class FragmentSettings :
    BaseFragment<MainActivityViewModel, MainFragmentSettingsBinding>(R.layout.main_fragment_settings) {

    private val viewModel: MainActivityViewModel by activityViewModels()

    override fun provideViewModel() = viewModel

    override fun setupUI() {

    }

}
