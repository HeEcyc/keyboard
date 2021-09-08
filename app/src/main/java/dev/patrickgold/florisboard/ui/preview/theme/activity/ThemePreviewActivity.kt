package dev.patrickgold.florisboard.ui.preview.theme.activity

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import dev.patrickgold.florisboard.R
import dev.patrickgold.florisboard.data.KeyboardTheme
import dev.patrickgold.florisboard.databinding.ThemePreviewActivityBinding
import dev.patrickgold.florisboard.ime.core.Subtype
import dev.patrickgold.florisboard.ime.keyboard.ComputingEvaluator
import dev.patrickgold.florisboard.ime.keyboard.DefaultComputingEvaluator
import dev.patrickgold.florisboard.ime.keyboard.KeyData
import dev.patrickgold.florisboard.ime.text.key.CurrencySet
import dev.patrickgold.florisboard.ime.text.key.KeyCode
import dev.patrickgold.florisboard.ime.text.keyboard.KeyboardMode
import dev.patrickgold.florisboard.ime.text.keyboard.TextKeyData
import dev.patrickgold.florisboard.ime.text.keyboard.TextKeyboardIconSet
import dev.patrickgold.florisboard.ime.text.layout.LayoutManager
import dev.patrickgold.florisboard.ui.base.BaseActivity
import dev.patrickgold.florisboard.ui.main.activity.MainActivity
import dev.patrickgold.florisboard.ui.theme.editor.activity.ThemeEditorActivity
import dev.patrickgold.florisboard.util.BUNDLE_THEME_KEY
import kotlinx.coroutines.launch

class ThemePreviewActivity :
    BaseActivity<ThemePreviewViewModel, ThemePreviewActivityBinding>(R.layout.theme_preview_activity),
    View.OnClickListener {

    private val viewModel: ThemePreviewViewModel by viewModels()
    private val textComputingEvaluator = object : ComputingEvaluator by DefaultComputingEvaluator {
        override fun evaluateVisible(data: KeyData): Boolean {
            return data.code != KeyCode.SWITCH_TO_MEDIA_CONTEXT
        }

        override fun isSlot(data: KeyData): Boolean {
            return CurrencySet.isCurrencySlot(data.code)
        }

        override fun getSlotData(data: KeyData): KeyData {
            return TextKeyData(label = ".")
        }
    }

    override fun setupUI() {
        val currentTheme = Gson().fromJson(intent.getStringExtra(BUNDLE_THEME_KEY), KeyboardTheme::class.java)

        viewModel.backgroundImage.set(currentTheme.backgroundImagePath)
        viewModel.currentBackgroundColor.set(currentTheme.backgroundColor)

        binding.backButton.setOnClickListener(this)
        binding.saveButton.setOnClickListener(this)
        binding.editButton.setOnClickListener(this)

        binding.keyboardPreview.setIconSet(TextKeyboardIconSet.new(this))
        binding.keyboardPreview.setComputingEvaluator(textComputingEvaluator)
        binding.keyboardPreview.sync()

        lifecycleScope.launch {
            binding.keyboardPreview.setComputedKeyboard(
                LayoutManager().computeKeyboardAsync(KeyboardMode.CHARACTERS, Subtype.DEFAULT).await(),
                currentTheme
            )
        }
    }

    override fun provideViewModel() = viewModel

    override fun onClick(v: View) {
        when (v.id) {
            R.id.backButton -> onBackPressed()
            R.id.saveButton -> onAttachTheme()
            R.id.editButton -> editKeyboardTheme()
        }
    }

    private fun editKeyboardTheme() {
        Intent(this, ThemeEditorActivity::class.java)
            .putExtra(BUNDLE_THEME_KEY, intent.getStringExtra(BUNDLE_THEME_KEY))
            .let(::startActivity)
    }

    fun onAttachTheme() {
        Intent(this, MainActivity::class.java)
            .addFlags(FLAG_ACTIVITY_REORDER_TO_FRONT)
            .let(::startActivity)
        finishAffinity()
    }

}
