package com.live.keyboard.ui.theme.editor.activity

import android.app.Activity
import android.content.Intent
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.FontRes
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.forEach
import androidx.lifecycle.lifecycleScope
import com.flask.colorpicker.ColorPickerView
import com.flask.colorpicker.builder.ColorPickerDialogBuilder
import com.google.android.material.tabs.TabLayout
import com.nguyenhoanglam.imagepicker.model.Config
import com.nguyenhoanglam.imagepicker.ui.imagepicker.ImagePicker
import com.live.keyboard.R
import com.live.keyboard.data.KeyboardTheme
import com.live.keyboard.databinding.ThemeEditorActivityAppBinding
import com.live.keyboard.ime.core.Subtype
import com.live.keyboard.ime.keyboard.ComputingEvaluator
import com.live.keyboard.ime.keyboard.DefaultComputingEvaluator
import com.live.keyboard.ime.keyboard.KeyData
import com.live.keyboard.ime.text.key.CurrencySet
import com.live.keyboard.ime.text.key.KeyCode
import com.live.keyboard.ime.text.keyboard.KeyboardMode
import com.live.keyboard.ime.text.keyboard.TextKeyData
import com.live.keyboard.ime.text.keyboard.TextKeyboardIconSet
import com.live.keyboard.ime.text.layout.LayoutManager
import com.live.keyboard.ui.base.BaseActivity
import com.live.keyboard.ui.crop.activity.CropActivity
import com.live.keyboard.ui.main.activity.MainActivity
import com.live.keyboard.util.BUNDLE_CROPPED_IMAGE_KEY
import com.live.keyboard.util.BUNDLE_IS_EDITING_THEME_KEY
import com.live.keyboard.util.BUNDLE_THEME_KEY
import kotlinx.coroutines.launch

class ThemeEditorActivity :
    BaseActivity<ThemeEditorViewModel, ThemeEditorActivityAppBinding>(R.layout.theme_editor_activity_app),
    TabLayout.OnTabSelectedListener {

    private val currentTheme: KeyboardTheme by lazy {
        intent.getSerializableExtra(BUNDLE_THEME_KEY) as? KeyboardTheme
            ?: KeyboardTheme()
    }

    private val cropImageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) viewModel
                .setCustomBackground(result.data?.getStringExtra(BUNDLE_CROPPED_IMAGE_KEY))
        }

    private val pickerImageActivityLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val imageUri = ImagePicker.getImages(result.data).first().uri.toString()
                cropImageLauncher.launch(
                    Intent(this, CropActivity::class.java)
                        .putExtra(BUNDLE_CROPPED_IMAGE_KEY, imageUri)
                )
            }
        }

    private lateinit var textKeyboardIconSet: TextKeyboardIconSet
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

    private val viewModel: ThemeEditorViewModel by viewModels()

    override fun setupUI() {

        binding.backButton.setOnClickListener { onBackPressed() }
        binding.saveButton.setOnClickListener { setCurrentKeyboard() }

        if (!currentTheme.backgroundImagePath.isNullOrEmpty()) {
            viewModel.currentKeyboardBackgorund.set(
                ThemeEditorViewModel.BackgroundAsset
                    .BackgroundTheme(currentTheme.backgroundImagePath!!, currentTheme.backgoundType)
            )
        }

        viewModel.currentBackgroundColor.set(currentTheme.backgroundColor)

        textKeyboardIconSet = TextKeyboardIconSet.new(this)

        viewModel.colorPicker.observe(this) { showColorPicker(it) }
        viewModel.imagePicker.observe(this, { showImagePicker() })

        binding.progressLayout.setProgress(currentTheme.opacity)
        binding.progressLayout.onProgress = { viewModel.keyBGOpacity.set(it) }

        binding.editCategoryTabs.addOnTabSelectedListener(this)
        binding.editCategoryTabs.getTabAt(2)?.select()

        binding.keyboardPreview.setIconSet(textKeyboardIconSet)
        binding.keyboardPreview.setComputingEvaluator(textComputingEvaluator)
        binding.keyboardPreview.sync()

        lifecycleScope.launch {
            binding.keyboardPreview.setComputedKeyboard(
                LayoutManager().computeKeyboardAsync(
                    KeyboardMode.CHARACTERS,
                    Subtype.DEFAULT
                ).await(), currentTheme.copy()
            )
        }
    }

    private fun showImagePicker() {
        ImagePicker.with(this)
            .setFolderMode(false)
            .setRootDirectoryName(Config.ROOT_DIR_DCIM)
            .setMultipleMode(false)
            .setShowNumberIndicator(false)
            .setMaxSize(1)
            .setRequestCode(100)
            .intent.let(pickerImageActivityLauncher::launch)
    }

    private fun showColorPicker(colorType: ThemeEditorViewModel.ColorType) {
        ColorPickerDialogBuilder
            .with(this)
            .setTitle("Choose color")
            .showAlphaSlider(false)
            .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
            .density(12)
            .setPositiveButton(R.string.ok) { _, p1, _ -> viewModel.onColorSelected(p1, colorType) }
            .setNegativeButton(R.string.cancel) { dialog, _ -> dialog?.dismiss() }
            .build()
            .show()
    }

    override fun provideViewModel() = viewModel

    override fun onTabSelected(tab: TabLayout.Tab) {
        chageTabFont(tab, R.font.roboto_500)
        changeVisibleLayout(tab)
    }

    private fun changeVisibleLayout(tab: TabLayout.Tab) {
        when (tab.position) {
            0 -> R.id.layoutFonts
            1 -> R.id.layoutButtons
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

    fun onAttachTheme(keyboardTheme: KeyboardTheme?, isThemeHasModifications: Boolean) {
        Intent(this, MainActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            .putExtra(BUNDLE_IS_EDITING_THEME_KEY, isThemeHasModifications)
            .putExtra(BUNDLE_THEME_KEY, keyboardTheme)
            .let(::startActivity)
        finishAffinity()
    }

    private fun isKeyboardHasModifications(keyboardTheme: KeyboardTheme) = currentTheme != keyboardTheme

    private fun setCurrentKeyboard() {
        val keyboardTheme = binding.keyboardPreview.getKeyboardTheme()
        keyboardTheme.apply {
            backgroundImagePath = viewModel.currentKeyboardBackgorund.get()?.uri
            backgoundType = viewModel.currentKeyboardBackgorund.get()?.backgroundTypeName
            backgroundColor = viewModel.currentBackgroundColor.get()
            previewImage = null
        }

        val isKeyboardHasModifications = isKeyboardHasModifications(keyboardTheme)

        if (isKeyboardHasModifications) {
            viewModel.setCurrentKeyboard(keyboardTheme)
            viewModel.saveKeyboardImage(binding.keyboardPreview, keyboardTheme.id)
        }

        viewModel.attachKeyboard(keyboardTheme)

        onAttachTheme(keyboardTheme, isKeyboardHasModifications)

    }
}
