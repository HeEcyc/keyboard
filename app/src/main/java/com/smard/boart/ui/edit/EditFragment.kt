package com.smard.boart.ui.edit

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
import com.smard.boart.R
import com.smard.boart.data.KeyboardTheme
import com.smard.boart.databinding.EditFragmentBinding
import com.smard.boart.ime.core.Subtype
import com.smard.boart.ime.keyboard.ComputingEvaluator
import com.smard.boart.ime.keyboard.DefaultComputingEvaluator
import com.smard.boart.ime.keyboard.KeyData
import com.smard.boart.ime.text.key.CurrencySet
import com.smard.boart.ime.text.key.KeyCode
import com.smard.boart.ime.text.keyboard.KeyboardMode
import com.smard.boart.ime.text.keyboard.TextKeyData
import com.smard.boart.ime.text.keyboard.TextKeyboardIconSet
import com.smard.boart.ime.text.layout.LayoutManager
import com.smard.boart.ui.base.BaseFragment
import com.smard.boart.ui.crop.CropActivity
import com.smard.boart.ui.custom.ItemDecorationWithEnds
import com.smard.boart.ui.home.HomeViewModel
import com.smard.boart.util.BUNDLE_CROPPED_IMAGE_KEY
import kotlinx.coroutines.launch
import kotlin.math.max

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
        binding.layoutPreview.buttonCancel.setOnClickListener { closeFragment() }
        binding.layoutEditor.buttonCancel.setOnClickListener { closeFragment() }
        viewModel.closeFragment.observe(this) { closeFragment() }
        binding.layoutPreview.buttonEdit.setOnClickListener {
            binding.layoutEditor.root.visibility = View.VISIBLE
            binding.layoutPreview.root.visibility = View.GONE
        }
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
        binding.layoutPreview.content.post {
            val baseWidth = max(binding.layoutPreview.content.width, binding.layoutEditor.content.width)
            val space = baseWidth / 360 * 6
            val spaceBottom = baseWidth / 360 * 173
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
        binding.layoutPreview.keyboardPreview.setIconSet(textKeyboardIconSet)
        binding.layoutPreview.keyboardPreview.setComputingEvaluator(textComputingEvaluator)
        binding.layoutPreview.keyboardPreview.sync()
        binding.layoutEditor.keyboardPreview.setIconSet(textKeyboardIconSet)
        binding.layoutEditor.keyboardPreview.setComputingEvaluator(textComputingEvaluator)
        binding.layoutEditor.keyboardPreview.sync()
        lifecycleScope.launch {
            binding.layoutPreview.keyboardPreview.setComputedKeyboard(
                LayoutManager().computeKeyboardAsync(
                    KeyboardMode.CHARACTERS,
                    Subtype.DEFAULT
                ).await(), viewModel.theme.copy().apply { id = viewModel.theme.id }
            )
            binding.layoutEditor.keyboardPreview.setComputedKeyboard(
                LayoutManager().computeKeyboardAsync(
                    KeyboardMode.CHARACTERS,
                    Subtype.DEFAULT
                ).await(), viewModel.theme.copy().apply { id = viewModel.theme.id }
            )
        }

        if (theme.id == -1L) {
            binding.layoutPreview.buttonEdit.callOnClick()
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
