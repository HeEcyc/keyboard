package dev.patrickgold.florisboard.ui.main.activity.custom

import androidx.fragment.app.activityViewModels
import dev.patrickgold.florisboard.R
import dev.patrickgold.florisboard.databinding.MainFragmentCustomThemeBinding
import dev.patrickgold.florisboard.ui.base.BaseFragment
import dev.patrickgold.florisboard.ui.main.activity.MainActivityViewModel

class FragmentCustomTheme :
    BaseFragment<MainActivityViewModel, MainFragmentCustomThemeBinding>(R.layout.main_fragment_custom_theme) {

    private val viewModel: MainActivityViewModel by activityViewModels()

    override fun provideViewModel() = viewModel

    override fun setupUI() {
        viewModel.loadSavedThemes()
    }

}
