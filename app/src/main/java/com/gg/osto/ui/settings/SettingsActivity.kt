package com.gg.osto.ui.settings

import android.content.Intent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.gg.osto.R
import com.gg.osto.background.view.keyboard.repository.BottomRightCharacterRepository
import com.gg.osto.databinding.SettingsActivityBinding
import com.gg.osto.repository.PrefsReporitory
import com.gg.osto.ui.base.BaseActivity
import com.gg.osto.ui.language.LanguageActivity
import com.gg.osto.ui.options.OptionsActivity
import com.gg.osto.util.enums.KeyboardHeight
import com.gg.osto.util.enums.LanguageChange
import com.gg.osto.util.enums.OneHandedMode

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
        binding.buttonBack.setOnClickListener { finish() }
        binding.buttonLanguage.setOnClickListener {
            startActivity(Intent(this, LanguageActivity::class.java))
        }
        binding.buttonKeyboardHeight.setOnClickListener {
            keyboardHeightLauncher.launch(OptionsActivity.getIntent(
                getString(R.string.keyboard_height),
                KeyboardHeight.values().map { it to getString(it.displayName) },
                PrefsReporitory.Settings.keyboardHeight,
                this
            ))
        }
        binding.buttonLanguageChange.setOnClickListener {
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
