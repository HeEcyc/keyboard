package dev.patrickgold.florisboard.ui

import android.content.Intent
import android.provider.Settings
import android.view.View
import android.view.inputmethod.InputMethodInfo
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import dev.patrickgold.florisboard.R
import dev.patrickgold.florisboard.background.view.keyboard.repository.BackgroundViewRepository
import dev.patrickgold.florisboard.databinding.TestActivityBinding
import dev.patrickgold.florisboard.ui.base.BaseActivity

class TestActivity : BaseActivity<TestViewModel, TestActivityBinding>(R.layout.test_activity), View.OnClickListener {
    private val viewModel: TestViewModel by viewModels()

    override fun setupUI() {
        binding.fluid.setOnClickListener(this)
        binding.lines.setOnClickListener(this)
        binding.image.setOnClickListener(this)
        binding.particlse.setOnClickListener(this)
    }

    override fun provideViewModel() = viewModel

    fun checkSettings(): Boolean {
        getSystemService(InputMethodManager::class.java).let {
            if (!checkIsEnaleSettings(it.enabledInputMethodList)) {
                startActivity(Intent("android.settings.INPUT_METHOD_SETTINGS"))
                return false
            } else if (!checkIsEnableKeyboard(it.enabledInputMethodList)) {
                it.showInputMethodPicker()
                return false
            }
            return true
        }
    }

    fun checkIsEnaleSettings(enabledInputMethodList: List<InputMethodInfo>) =
        enabledInputMethodList.firstOrNull { it.packageName == packageName } != null

    fun checkIsEnableKeyboard(enabledInputMethodList: List<InputMethodInfo>) =
        enabledInputMethodList.firstOrNull { it.packageName == packageName }?.id ==
            Settings.Secure.getString(contentResolver, "default_input_method")

    override fun onClick(v: View) {
        if (v.id in listOf(R.id.lines, R.id.image, R.id.fluid, R.id.particlse)) {
            if (checkSettings()) when (v.id) {
                R.id.lines -> BackgroundViewRepository.BackgroundView.ParticleView
                R.id.fluid -> BackgroundViewRepository.BackgroundView.FluidView
                else -> BackgroundViewRepository.BackgroundView.ParticleFlowView
            }.let(viewModel::attachThemeType)
        }
    }
}
