package com.gg.osto.ui.home

import android.content.Intent
import android.provider.Settings
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.core.view.postDelayed
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import com.gg.osto.R
import com.gg.osto.data.KeyboardTheme
import com.gg.osto.databinding.HomeActivityBinding
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
import com.gg.osto.ui.base.BaseActivity
import com.gg.osto.ui.custom.ItemDecorationWithEnds
import com.gg.osto.ui.edit.EditFragment
import com.gg.osto.ui.settings.SettingsActivity
import com.gg.osto.ui.splash.SplashActivity
import com.gg.osto.util.EXTRA_LAUNCH_SETTINGS
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

    override fun setupUI() {
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
