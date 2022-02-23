package com.ioskey.iosboard.ui.theme.editor.activity

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
import com.google.gson.Gson
import com.ioskey.iosboard.R
import com.ioskey.iosboard.data.KeyboardTheme
import com.ioskey.iosboard.databinding.ThemeEditorActivityAppBinding
import com.ioskey.iosboard.ime.core.Subtype
import com.ioskey.iosboard.ime.keyboard.ComputingEvaluator
import com.ioskey.iosboard.ime.keyboard.DefaultComputingEvaluator
import com.ioskey.iosboard.ime.keyboard.KeyData
import com.ioskey.iosboard.ime.text.key.CurrencySet
import com.ioskey.iosboard.ime.text.key.KeyCode
import com.ioskey.iosboard.ime.text.keyboard.KeyboardMode
import com.ioskey.iosboard.ime.text.keyboard.TextKeyData
import com.ioskey.iosboard.ime.text.keyboard.TextKeyboardIconSet
import com.ioskey.iosboard.ime.text.layout.LayoutManager
import com.ioskey.iosboard.ui.base.BaseActivity
import com.ioskey.iosboard.ui.crop.activity.CropActivity
import com.ioskey.iosboard.ui.main.activity.MainActivity
import com.ioskey.iosboard.util.BUNDLE_CROPPED_IMAGE_KEY
import com.ioskey.iosboard.util.BUNDLE_IS_EDITING_THEME_KEY
import com.ioskey.iosboard.util.BUNDLE_THEME_KEY
import com.nguyenhoanglam.imagepicker.model.Config
import com.nguyenhoanglam.imagepicker.ui.imagepicker.ImagePicker
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

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
                ).await(), currentTheme.copy().apply { id = currentTheme.id }
            )
        }
        viewModel.initFontAdapter(currentTheme.keyFont)
    }

    private fun generateKeyBards() {
        lifecycleScope.launch {
            val gsom = Gson()
            assets.list("ime/theme")?.forEach {
                val i: InputStream = assets.open("ime/theme/$it")
                val br = BufferedReader(InputStreamReader(i))
                val todoItem: KeyboardTheme = gsom.fromJson(br, KeyboardTheme::class.java)

                binding.keyboardPreview.setComputedKeyboard(
                    LayoutManager().computeKeyboardAsync(
                        KeyboardMode.CHARACTERS,
                        Subtype.DEFAULT
                    ).await(), todoItem
                )

                delay(2000)
            }
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

    private fun onAttachTheme(keyboardTheme: KeyboardTheme?, isThemeHasModifications: Boolean) {
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
