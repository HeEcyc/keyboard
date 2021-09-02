package dev.patrickgold.florisboard.ui.theme.editor.activity

import android.util.Log
import android.widget.TextView
import androidx.activity.viewModels
import androidx.annotation.FontRes
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.forEach
import androidx.lifecycle.lifecycleScope
import com.google.android.material.tabs.TabLayout
import dev.patrickgold.florisboard.R
import dev.patrickgold.florisboard.databinding.ThemeEditorActivityAppBinding
import dev.patrickgold.florisboard.ime.core.Subtype
import dev.patrickgold.florisboard.ime.text.keyboard.KeyboardMode
import dev.patrickgold.florisboard.ime.text.layout.LayoutManager
import dev.patrickgold.florisboard.ui.base.BaseActivity
import kotlinx.coroutines.launch

class ThemeEditorActivity :
    BaseActivity<ThemeEditorViewModel, ThemeEditorActivityAppBinding>(R.layout.theme_editor_activity_app),
    TabLayout.OnTabSelectedListener {

    private val viewModel: ThemeEditorViewModel by viewModels()

    override fun setupUI() {
        binding.editCategoryTabs.addOnTabSelectedListener(this)
        binding.editCategoryTabs.getTabAt(2)?.select()

        viewModel.colorPicker.observe(this) { showColorPicker(it) }

        lifecycleScope.launch {
            binding.keyboardPreview.setComputedKeyboard(
                LayoutManager().computeKeyboardAsync(
                    KeyboardMode.CHARACTERS,
                    Subtype.DEFAULT
                ).await()
            )
        }
    }

    private fun showColorPicker(it: ThemeEditorViewModel.ColorType?) {
        Log.d("12345", "enter")
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
