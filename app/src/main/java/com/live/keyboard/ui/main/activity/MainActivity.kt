package com.live.keyboard.ui.main.activity

import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.transition.ChangeBounds
import android.transition.Transition
import android.transition.TransitionManager
import android.view.animation.LinearInterpolator
import android.view.animation.OvershootInterpolator
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import com.live.keyboard.R
import com.live.keyboard.adapters.VPAdapter
import com.live.keyboard.data.KeyboardTheme
import com.live.keyboard.databinding.MainActivityBinding
import com.live.keyboard.repository.PrefsReporitory
import com.live.keyboard.ui.base.BaseActivity
import com.live.keyboard.ui.dialogs.DialogDone
import com.live.keyboard.ui.dialogs.DialogPermissions
import com.live.keyboard.ui.main.activity.assets.FragmentAssets
import com.live.keyboard.ui.main.activity.custom.FragmentCustomTheme
import com.live.keyboard.ui.main.activity.settings.FragmentSettings
import com.live.keyboard.ui.preview.theme.activity.ThemePreviewActivity
import com.live.keyboard.ui.theme.editor.activity.ThemeEditorActivity
import com.live.keyboard.util.BUNDLE_IS_EDITING_THEME_KEY
import com.live.keyboard.util.BUNDLE_THEME_KEY


class MainActivity : BaseActivity<MainActivityViewModel, MainActivityBinding>(R.layout.main_activity),
    DialogPermissions.OnPermissionAction {

    private val viewModel: MainActivityViewModel by viewModels {
        MainActivityViewModel.Factory(VPAdapter(this, getFragmets()))
    }

    private val dialogPermissions = DialogPermissions(this)
    private var isTryKeyboardMessageShowing = false
    private val inputManager by lazy { getSystemService(InputMethodManager::class.java) }
    private val settingsLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (isKeyboardActive() && !isKeyboardEnable()) Handler(Looper.getMainLooper())
            .postDelayed({ showEnableKeyboardDialog() }, 200)
    }

    private fun showEnableKeyboardDialog() {
        inputManager.showInputMethodPicker()
    }

    private fun getFragmets() = arrayOf<Fragment>(FragmentAssets(), FragmentCustomTheme(), FragmentSettings())

    override fun provideViewModel() = viewModel

    override fun setupUI() {
        with(viewModel) {
            onThemeClick.observe(this@MainActivity) { onThemeClick(it) }
            nextActivity.observe(this@MainActivity, { showNexActivity(it) })
        }
        binding.tryMessage.setOnClickListener { showPreviewTheme() }
        binding.bottomBar.onPageChange = { page ->
            if (page == 2 && isTryKeyboardMessageShowing) hideTryKeyboardMessage()
            else if (isTryKeyboardMessageShowing) showTryKeyboardMessage()
            viewModel.currentPage.set(page)
        }
        binding.mainScreens.isUserInputEnabled = false
        binding.mainScreens.offscreenPageLimit = 3
    }

    private fun onThemeClick(keyboardTheme: KeyboardTheme) {
        if (keyboardTheme.isSelected) Intent(this@MainActivity, ThemeEditorActivity::class.java)
            .putExtra(BUNDLE_THEME_KEY, keyboardTheme)
            .let(::startActivity)
        else {
            viewModel.setupKeyboard(keyboardTheme)
            viewModel.setSelectedItem(keyboardTheme)
            showTryKeyboardMessage()
        }
    }

    private fun showTryKeyboardMessage() {
        isTryKeyboardMessageShowing = true
        ConstraintSet().apply {
            clone(binding.root as ConstraintLayout)
            clear(R.id.tryMessage, ConstraintSet.TOP)
            connect(R.id.tryMessage, ConstraintSet.BOTTOM, R.id.bottomBar, ConstraintSet.TOP)
        }.applyTo(binding.root as ConstraintLayout)

        val transition: Transition = ChangeBounds()
        transition.interpolator = OvershootInterpolator(1.5f)
        transition.duration = 600

        TransitionManager.beginDelayedTransition(binding.root as ConstraintLayout, transition)
    }

    private fun hideTryKeyboardMessage() {
        isTryKeyboardMessageShowing = true
        ConstraintSet().apply {
            clone(binding.root as ConstraintLayout)
            clear(R.id.tryMessage, ConstraintSet.BOTTOM)
            connect(R.id.tryMessage, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)
        }.applyTo(binding.root as ConstraintLayout)

        val transition: Transition = ChangeBounds()
        transition.interpolator = LinearInterpolator()
        transition.duration = 100

        TransitionManager.beginDelayedTransition(binding.root as ConstraintLayout, transition)
    }

    private fun showNexActivity(activityClass: Class<out BaseActivity<*, *>>?) {
        startActivity(Intent(this, activityClass))
    }

    fun isKeyboardActive() = inputManager
        .enabledInputMethodList
        .firstOrNull { it.packageName == packageName } != null

    fun isKeyboardEnable() = inputManager
        .enabledInputMethodList
        .firstOrNull { it.packageName == packageName }
        ?.id == Settings.Secure.getString(contentResolver, "default_input_method")

    override fun onResume() {
        super.onResume()
        viewModel.checkEnableKeyboardSwipe()
        if (!isKeyboardEnable() || !isKeyboardActive()) dialogPermissions.show(supportFragmentManager)
    }

    override fun askPermissions() {
        if (!isKeyboardActive()) settingsLauncher
            .launch(Intent("android.settings.INPUT_METHOD_SETTINGS"))
        else showEnableKeyboardDialog()
    }

    override fun hasAllPermissions() = isKeyboardActive() && isKeyboardEnable()

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (intent.getBooleanExtra(BUNDLE_IS_EDITING_THEME_KEY, false))
            viewModel.onThemeApply(intent.getSerializableExtra(BUNDLE_THEME_KEY) as? KeyboardTheme ?: return)
        showTryKeyboardMessage()
        DialogDone().show(supportFragmentManager)
    }

    private fun showPreviewTheme() {
        Intent(this, ThemePreviewActivity::class.java)
            .putExtra(BUNDLE_THEME_KEY, PrefsReporitory.keyboardTheme ?: return)
            .let(::startActivity)
    }
}
