package com.ioskey.iosboard.ui.settings.activity

import android.content.Intent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.ioskey.iosboard.R
import com.ioskey.iosboard.background.view.keyboard.repository.BottomRightCharacterRepository
import com.ioskey.iosboard.databinding.SettingsActivityBinding
import com.ioskey.iosboard.repository.PrefsReporitory
import com.ioskey.iosboard.ui.base.BaseActivity
import com.ioskey.iosboard.ui.language.selector.activity.LanguageSelectorActivity
import com.ioskey.iosboard.ui.settings.options.activity.OptionsActivity
import com.ioskey.iosboard.ui.settings.swipe.speed.dialog.SwipeSpeedDialog
import com.ioskey.iosboard.util.enums.KeyboardHeight
import com.ioskey.iosboard.util.enums.LanguageChange
import com.ioskey.iosboard.util.enums.OneHandedMode

class SettingsActivity : BaseActivity<SettingsViewModel, SettingsActivityBinding>(R.layout.settings_activity) {

    val viewModel: SettingsViewModel by viewModels { SettingsViewModel.Factory() }

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
        OptionsActivity.getResult(it.data)?.let { it as? BottomRightCharacterRepository.SelectableCharacter }?.let(viewModel::onSpecialSymbolSelected)
    }

    override fun setupUI() {
        binding.buttonBack.setOnClickListener { onBackPressed() }
        binding.buttonLanguage.setOnClickListener {
            startActivity(Intent(this, LanguageSelectorActivity::class.java))
        }
        binding.buttonSwipeSpeed.setOnClickListener {
            SwipeSpeedDialog().show(supportFragmentManager)
        }
        binding.buttonKeyboardHeight.setOnClickListener {
            keyboardHeightLauncher.launch(OptionsActivity.getIntent(
                getString(R.string.keyboard_height),
                KeyboardHeight.values().map { it to getString(it.displayName) },
                PrefsReporitory.Settings.keyboardHeight,
                this
            ))
        }
        binding.buttonLanguageButton.setOnClickListener {
            languageButtonLauncher.launch(OptionsActivity.getIntent(
                getString(R.string.language_change),
                LanguageChange.values().map { it to getString(it.displayName) },
                PrefsReporitory.Settings.languageChange,
                this
            ))
        }
        binding.buttonOneHandedMode.setOnClickListener {
            oneHandedModeLauncher.launch(OptionsActivity.getIntent(
                getString(R.string.one_handed_mode),
                OneHandedMode.values().map { it to getString(it.displayName) },
                PrefsReporitory.Settings.oneHandedMode,
                this
            ))
        }
        binding.buttonSpecialSymbol.setOnClickListener {
            specialSymbolLauncher.launch(OptionsActivity.getIntent(
                getString(R.string.special_symbols_editor),
                BottomRightCharacterRepository.SelectableCharacter.values().map { it to getString(it.displayName) },
                BottomRightCharacterRepository.SelectableCharacter.from(BottomRightCharacterRepository.selectedBottomRightCharacterCode),
                this
            ))
        }
    }

    override fun provideViewModel() = viewModel

}
