package com.gg.osto.ui.edit

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.nguyenhoanglam.imagepicker.model.Config
import com.nguyenhoanglam.imagepicker.ui.imagepicker.ImagePicker
import com.gg.osto.R
import com.gg.osto.data.KeyboardTheme
import com.gg.osto.databinding.EditFragmentBinding
import com.gg.osto.ime.core.Subtype
import com.gg.osto.ime.keyboard.ComputingEvaluator
import com.gg.osto.ime.keyboard.DefaultComputingEvaluator
import com.gg.osto.ime.keyboard.KeyData
import com.gg.osto.ime.text.key.CurrencySet
import com.gg.osto.ime.text.key.KeyCode
import com.gg.osto.ime.text.keyboard.KeyboardMode
import com.gg.osto.ime.text.keyboard.TextKeyData
import com.gg.osto.ime.text.keyboard.TextKeyboardIconSet
import com.gg.osto.ime.text.layout.LayoutManager
import com.gg.osto.ui.base.BaseFragment
import com.gg.osto.ui.crop.CropActivity
import com.gg.osto.ui.custom.ItemDecorationWithEnds
import com.gg.osto.ui.home.HomeViewModel
import com.gg.osto.util.BUNDLE_CROPPED_IMAGE_KEY
import kotlinx.coroutines.launch

class EditFragment : BaseFragment<EditViewModel, EditFragmentBinding>(R.layout.edit_fragment) {

    private val activityViewModel: HomeViewModel by activityViewModels()
    val viewModel: EditViewModel by viewModels { EditViewModel.Factory(theme, activityViewModel) }

    private val theme: KeyboardTheme by lazy { requireArguments().getSerializable(ARGUMENT_THEME) as KeyboardTheme }

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
                    Intent(requireContext(), CropActivity::class.java)
                        .putExtra(BUNDLE_CROPPED_IMAGE_KEY, imageUri)
                )
            }
        }

    companion object {
        private const val ARGUMENT_THEME = "argument_theme"

        fun newInstance(theme: KeyboardTheme) = EditFragment().apply {
            arguments = bundleOf(ARGUMENT_THEME to theme)
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

    override fun setupUI() {
        binding.layoutEditor.colorPicker.setOnClickListener {}
        binding.layoutEditor.fontPicker.setOnClickListener {}
        binding.layoutEditor.imagePicker.setOnClickListener {}
        binding.layoutEditor.buttonCancel.setOnClickListener { closeFragment() }
        viewModel.closeFragment.observe(this) { closeFragment() }
        binding.layoutEditor.colorPickerView.colorChangeListener = {
            binding.layoutEditor.colorDemo.setBackgroundColor(it)
        }
        viewModel.pickColor.observe(this) {
            binding.layoutEditor.colorPickerView.rgb = Color.parseColor(viewModel.currentPickerColor)
            binding.layoutEditor.colorPicker.visibility = View.VISIBLE
        }
        binding.layoutEditor.buttonCancelColor.setOnClickListener {
            binding.layoutEditor.colorPicker.visibility = View.GONE
        }
        binding.layoutEditor.buttonOkColor.setOnClickListener {
            viewModel.pickColor.value?.invoke(
                String.format("#%06X", 0xFFFFFF and binding.layoutEditor.colorPickerView.rgb)
            )
            binding.layoutEditor.colorPicker.visibility = View.GONE
        }
        binding.layoutEditor.root.post {
            val baseWidth = binding.layoutEditor.root.width
            val space = baseWidth / 360 * 10
            val spaceBottom = baseWidth / 360 * 190
            binding.layoutEditor.bgRecycler.addItemDecoration(ItemDecorationWithEnds(
                top = space,
                topLast = space,
                bottom = space,
                bottomLast = spaceBottom,
                left = space,
                leftLast = space,
                right = space,
                rightLast = space,
                lastPredicate = { i, c -> if (c % 2 == 0) i == c - 1 || i == c - 2 else i == c - 1 }
            ))
        }
        viewModel.pickImage.observe(this) {
            ImagePicker.with(this)
                .setFolderMode(false)
                .setRootDirectoryName(Config.ROOT_DIR_DCIM)
                .setMultipleMode(false)
                .setShowNumberIndicator(false)
                .setMaxSize(1)
                .setRequestCode(100)
                .intent.let(pickerImageActivityLauncher::launch)
        }
        binding.layoutEditor.buttonApply.setOnClickListener { setCurrentKeyboard() }

        textKeyboardIconSet = TextKeyboardIconSet.new(requireContext())
        binding.layoutEditor.keyboardPreview.setIconSet(textKeyboardIconSet)
        binding.layoutEditor.keyboardPreview.setComputingEvaluator(textComputingEvaluator)
        binding.layoutEditor.keyboardPreview.sync()
        lifecycleScope.launch {
            binding.layoutEditor.keyboardPreview.setComputedKeyboard(
                LayoutManager().computeKeyboardAsync(
                    KeyboardMode.CHARACTERS,
                    Subtype.DEFAULT
                ).await(), viewModel.theme.copy().apply { id = viewModel.theme.id }
            )
        }
    }

    private fun isKeyboardHasModifications(keyboardTheme: KeyboardTheme) = theme != keyboardTheme  || theme.id == -1L

    private fun setCurrentKeyboard() {
        val keyboardTheme = binding.layoutEditor.keyboardPreview.getKeyboardTheme()
        keyboardTheme.apply {
            backgroundImagePath = viewModel.currentKeyboardBackground.get()?.uri
            backgoundType = viewModel.currentKeyboardBackground.get()?.backgroundTypeName
            backgroundColor = viewModel.currentBackgroundColor.get()
            previewImage = null
        }

        val isKeyboardHasModifications = isKeyboardHasModifications(keyboardTheme)

        if (isKeyboardHasModifications) {
            viewModel.setCurrentKeyboard(keyboardTheme)
            viewModel.saveKeyboardImage(binding.layoutEditor.keyboardPreview, keyboardTheme.id)
        }

        viewModel.attachKeyboard(keyboardTheme)

        closeFragment()
    }

    private fun closeFragment() = parentFragmentManager.commit { remove(this@EditFragment) }

    override fun provideViewModel() = viewModel

}
