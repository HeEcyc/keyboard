package com.cccomba.board.ui.home

import android.content.Intent
import android.provider.Settings
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.view.postDelayed
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import com.app.sdk.sdk.PremiumUserSdk
import com.cccomba.board.R
import com.cccomba.board.background.view.keyboard.repository.BottomRightCharacterRepository
import com.cccomba.board.data.KeyboardTheme
import com.cccomba.board.databinding.HomeActivityBinding
import com.cccomba.board.ime.core.Subtype
import com.cccomba.board.ime.keyboard.ComputingEvaluator
import com.cccomba.board.ime.keyboard.DefaultComputingEvaluator
import com.cccomba.board.ime.keyboard.KeyData
import com.cccomba.board.ime.text.key.CurrencySet
import com.cccomba.board.ime.text.key.KeyCode
import com.cccomba.board.ime.text.keyboard.KeyboardMode
import com.cccomba.board.ime.text.keyboard.TextKeyData
import com.cccomba.board.ime.text.keyboard.TextKeyboardIconSet
import com.cccomba.board.ime.text.layout.LayoutManager
import com.cccomba.board.repository.PrefsReporitory
import com.cccomba.board.ui.base.BaseActivity
import com.cccomba.board.ui.custom.ItemDecorationWithEnds
import com.cccomba.board.ui.edit.EditFragment
import com.cccomba.board.ui.language.LanguageActivity
import com.cccomba.board.ui.options.OptionsActivity
import com.cccomba.board.ui.permission.PermissionDialog
import com.cccomba.board.util.EXTRA_LAUNCH_SETTINGS
import com.cccomba.board.util.enums.KeyboardHeight
import com.cccomba.board.util.enums.LanguageChange
import com.cccomba.board.util.enums.OneHandedMode
import kotlinx.coroutines.launch

class HomeActivity : BaseActivity<HomeViewModel, HomeActivityBinding>(R.layout.home_activity) {

    val viewModel: HomeViewModel by viewModels { HomeViewModel.Factory() }

    private val inputManager by lazy { getSystemService(InputMethodManager::class.java) }

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

    private val keyboardHeightLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        OptionsActivity.getResult(it.data)?.let { it as? KeyboardHeight }?.let(viewModel::onKeyboardHeightSelected)
    }
    private val languageButtonLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        OptionsActivity.getResult(it.data)?.let { it as? LanguageChange }?.let(viewModel::onLanguageChangeSelected)
    }
    private val oneHandedModeLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        OptionsActivity.getResult(it.data)?.let { it as? OneHandedMode }?.let(viewModel::onOneHandedModeSelected)
    }
    private val specialSymbolLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        OptionsActivity.getResult(it.data)?.let { it as? BottomRightCharacterRepository.SelectableCharacter }
            ?.let(viewModel::onSpecialSymbolSelected)
    }

    override fun setupUI() {

        if (!Settings.canDrawOverlays(this) || !hasKeyboardPermission())
            PermissionDialog().show(supportFragmentManager)

        binding.rvPreset.post {
            val spaceVertical = binding.root.width / 360 * 16
            val spaceHorizontal = binding.root.width / 360 * 8
            val itemDecoration = ItemDecorationWithEnds(
                topFirst = spaceVertical,
                bottomFirst = spaceVertical,
                bottom = spaceVertical,
                leftFirst = spaceHorizontal,
                left = spaceHorizontal,
                rightFirst = spaceHorizontal,
                right = spaceHorizontal,
                firstPredicate = { i -> i in 0..1 }
            )
            binding.rvPreset.addItemDecoration(itemDecoration)
            binding.rvCustom.addItemDecoration(itemDecoration)
        }

        viewModel.onThemeClick.observe(this) {
            binding.preview.visibility = View.VISIBLE
        }
        viewModel.onAddClick.observe(this) {
            supportFragmentManager.commit {
                add(R.id.fragmentContainer, EditFragment.newInstance(KeyboardTheme(-1)))
            }
        }
        if (intent?.hasExtra(EXTRA_LAUNCH_SETTINGS) == true)
            viewModel.onSettingsClick()
        binding.preview.setOnClickListener {}
        viewModel.onApplied.observe(this) {
            PremiumUserSdk.showInAppAd(this) {
                binding.preview.visibility = View.GONE
                binding.applied.visibility = View.VISIBLE
                binding.applied.postDelayed(1500) {
                    binding.applied.visibility = View.GONE
                }
            }
        }
        binding.buttonEdit.setOnClickListener {
            supportFragmentManager.commit {
                add(R.id.fragmentContainer, EditFragment.newInstance(viewModel.theme.get()!!))
            }
            binding.preview.visibility = View.GONE
        }
        binding.buttonClose.setOnClickListener {
            binding.preview.visibility = View.GONE
        }

        textKeyboardIconSet = TextKeyboardIconSet.new(this)
        binding.keyboardPreview.setIconSet(textKeyboardIconSet)
        binding.keyboardPreview.setComputingEvaluator(textComputingEvaluator)
        binding.keyboardPreview.sync()
        lifecycleScope.launch {
            binding.keyboardPreview.setComputedKeyboard(
                LayoutManager().computeKeyboardAsync(
                    KeyboardMode.CHARACTERS,
                    Subtype.DEFAULT
                ).await(), viewModel.theme.get()?.copy()?.apply { id = viewModel.theme.get()?.id }
            )
        }
        binding.buttonLanguage.setOnClickListener {
            startActivity(Intent(this, LanguageActivity::class.java))
        }
        binding.buttonKeyboardHeight.setOnClickListener {
            keyboardHeightLauncher.launch(
                OptionsActivity.getIntent(
                    getString(R.string.keyboard_height),
                    KeyboardHeight.values().map { it to getString(it.displayName) },
                    PrefsReporitory.Settings.keyboardHeight,
                    this
                )
            )
        }
        binding.buttonLanguageChange.setOnClickListener {
            languageButtonLauncher.launch(
                OptionsActivity.getIntent(
                    getString(R.string.language_change),
                    LanguageChange.values().map { it to getString(it.displayName) },
                    PrefsReporitory.Settings.languageChange,
                    this
                )
            )
        }
        binding.buttonOneHandedMode.setOnClickListener {
            oneHandedModeLauncher.launch(
                OptionsActivity.getIntent(
                    getString(R.string.one_handed_mode),
                    OneHandedMode.values().map { it to getString(it.displayName) },
                    PrefsReporitory.Settings.oneHandedMode,
                    this
                )
            )
        }
        binding.buttonSpecialSymbol.setOnClickListener {
            specialSymbolLauncher.launch(
                OptionsActivity.getIntent(
                    getString(R.string.special_symbols_editor),
                    BottomRightCharacterRepository.SelectableCharacter.values().map { it to getString(it.displayName) },
                    BottomRightCharacterRepository.SelectableCharacter.from(BottomRightCharacterRepository.selectedBottomRightCharacterCode),
                    this
                )
            )
        }
    }

    private fun hasKeyboardPermission() = isKeyboardActive() && isKeyboardEnable()

    private fun isKeyboardActive() = inputManager
        .enabledInputMethodList
        .firstOrNull { it.packageName == packageName } != null

    private fun isKeyboardEnable() = inputManager
        .enabledInputMethodList
        .firstOrNull { it.packageName == packageName }
        ?.id == Settings.Secure.getString(contentResolver, "default_input_method")

    override fun provideViewModel(): HomeViewModel = viewModel

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        PremiumUserSdk.onResult(this)
    }
}
