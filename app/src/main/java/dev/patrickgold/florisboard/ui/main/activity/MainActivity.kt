package dev.patrickgold.florisboard.ui.main.activity

import android.content.Intent
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import dev.patrickgold.florisboard.R
import dev.patrickgold.florisboard.adapters.VPAdapter
import dev.patrickgold.florisboard.databinding.MainActivityBinding
import dev.patrickgold.florisboard.ui.base.BaseActivity
import dev.patrickgold.florisboard.ui.main.activity.assets.FragmentAssets
import dev.patrickgold.florisboard.ui.main.activity.custom.FragmentCustomTheme
import dev.patrickgold.florisboard.ui.main.activity.settings.FragmentSettings
import dev.patrickgold.florisboard.ui.preview.theme.activity.ThemePreviewActivity

class MainActivity : BaseActivity<MainActivityViewModel, MainActivityBinding>(R.layout.main_activity) {
    private val viewModel: MainActivityViewModel by viewModels {
        MainActivityViewModel.Factory(VPAdapter(this, getFragmets()))
    }

    private fun getFragmets() = arrayOf<Fragment>(FragmentAssets(), FragmentCustomTheme(), FragmentSettings())

    override fun provideViewModel() = viewModel

    override fun setupUI() {
        with(viewModel) {
            onThemeClick.observe(this@MainActivity) { onThemeClick(it) }
            viewModel.nextActivity.observe(this@MainActivity, { showNexActivity(it) })
        }
        binding.bottomBar.onPageChange = viewModel.currentPage::set
        binding.mainScreens.isUserInputEnabled = false
        binding.mainScreens.offscreenPageLimit = 3
//        DialogChooser(R.string.ok, arrayOf("hello", "by", "die"), "by") {
//            Log.d("12345", it)
//        }.show(this)
    }

    private fun onThemeClick(it: String?) {
        Intent(this@MainActivity, ThemePreviewActivity::class.java)
            .let(::startActivity)
    }

    private fun showNexActivity(activityClass: Class<out BaseActivity<*, *>>?) {
        startActivity(Intent(this, activityClass))
    }

}
