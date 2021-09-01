package dev.patrickgold.florisboard.ui.theme.editor.activity

import android.widget.TextView
import androidx.activity.viewModels
import androidx.annotation.FontRes
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.forEach
import com.google.android.material.tabs.TabLayout
import dev.patrickgold.florisboard.R
import dev.patrickgold.florisboard.databinding.ThemeEditorActivityAppBinding
import dev.patrickgold.florisboard.ui.base.BaseActivity

class ThemeEditorActivity :
    BaseActivity<ThemeEditorViewModel, ThemeEditorActivityAppBinding>(R.layout.theme_editor_activity_app),
    TabLayout.OnTabSelectedListener {

    private val viewModel: ThemeEditorViewModel by viewModels()

    override fun setupUI() {
        binding.editCategoryTabs.addOnTabSelectedListener(this)
        binding.editCategoryTabs.getTabAt(2)?.select()
    }

    override fun provideViewModel() = viewModel

    override fun onTabSelected(tab: TabLayout.Tab) {
        chageTabFont(tab, R.font.roboto_500)
        changeVisibleLayout(tab)
    }

    private fun changeVisibleLayout(tab: TabLayout.Tab) {
        when (tab.position) {
            0 -> R.id.layoutFonts
            1 -> R.id.layoutColors
            2 -> R.id.layoutBackgrounds
            3 -> R.id.layoutOpacity
            4 -> R.id.layoutStockes
            else -> null
        }?.let(viewModel.visibleLayoutId::set)
    }

    override fun onTabUnselected(tab: TabLayout.Tab) {
        chageTabFont(tab, R.font.roboto_300)
    }

    override fun onTabReselected(tab: TabLayout.Tab) {

    }

    private fun chageTabFont(tab: TabLayout.Tab, @FontRes font: Int) {
        tab.view.forEach {
            if (it is TextView) it.typeface = ResourcesCompat.getFont(this, font)
            return@forEach
        }
    }
}
