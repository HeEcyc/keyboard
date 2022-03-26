package com.ioskey.iosboard.ui.home.activity

import android.animation.ValueAnimator
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.View
import android.view.animation.LinearInterpolator
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.constraintlayout.motion.widget.MotionLayout
import com.ioskey.iosboard.R
import com.ioskey.iosboard.data.KeyboardTheme
import com.ioskey.iosboard.databinding.HomeActivityBinding
import com.ioskey.iosboard.repository.PrefsReporitory
import com.ioskey.iosboard.ui.base.BaseActivity
import com.ioskey.iosboard.ui.custom.ItemDecorationWithEnds
import com.ioskey.iosboard.ui.dialogs.DialogPermissions
import com.ioskey.iosboard.ui.guid.activity.first.GuidLanguageActivity
import com.ioskey.iosboard.ui.preview.theme.activity.ThemePreviewActivity
import com.ioskey.iosboard.ui.settings.activity.SettingsActivity
import com.ioskey.iosboard.ui.theme.editor.activity.ThemeEditorActivity
import com.ioskey.iosboard.util.BUNDLE_IS_EDITING_THEME_KEY
import com.ioskey.iosboard.util.BUNDLE_THEME_KEY
import io.github.florent37.shapeofview.shapes.RoundRectView

class HomeActivity : BaseActivity<HomeViewModel, HomeActivityBinding>(R.layout.home_activity),
    DialogPermissions.OnPermissionAction {

    val viewModel: HomeViewModel by viewModels { HomeViewModel.Factory() }

    private val settingsLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (isKeyboardActive() && !isKeyboardEnable()) Handler(Looper.getMainLooper())
            .postDelayed({ showEnableKeyboardDialog() }, 200)
    }
    private val inputManager by lazy { getSystemService(InputMethodManager::class.java) }
    private val dialogPermissions = DialogPermissions(this)

    private val cornerRadius by lazy { binding.themesContainer.width / 411f * 24 }
    private val themesBackground by lazy { binding.themesBackground }

    override fun setupUI() {
        binding.themesContainer.setOnClickListener {}
        binding.root.post {
            val isLtr = resources.configuration.layoutDirection == View.LAYOUT_DIRECTION_LTR
            val edgeSpace = binding.root.width * 22 / 411
            val innerSpace = binding.root.width * 4 / 411
            val itemDecoration = ItemDecorationWithEnds(
                leftFirst = if (isLtr) edgeSpace else innerSpace,
                left = innerSpace,
                leftLast = if (isLtr) innerSpace else edgeSpace,
                rightFirst = if (isLtr) innerSpace else edgeSpace,
                right = innerSpace,
                rightLast = if (isLtr) edgeSpace else innerSpace,
                firstPredicate = ::isFirstAdapterItem,
                lastPredicate = ::isLastAdapterItem
            )
            binding.rvCustom.addItemDecoration(itemDecoration)
            binding.rvPopular.addItemDecoration(itemDecoration)
            binding.rvOther.addItemDecoration(itemDecoration)
            themesBackground.setRadius(cornerRadius)
        }
        binding.motionLayout.addTransitionListener(newTransactionListener())
        viewModel.needRequestLayout.observe(this) {
            binding.buttonApply.requestLayout()
        }
        viewModel.addNew.observe(this) {
            startActivity(Intent(this, ThemeEditorActivity::class.java))
        }
        binding.buttonEdit.setOnClickListener {
            startActivity(
                Intent(this, ThemeEditorActivity::class.java)
                    .putExtra(BUNDLE_THEME_KEY, viewModel.selectedTheme.get() ?: return@setOnClickListener)
            )
        }
        binding.buttonTry.setOnClickListener {
            viewModel.apply()
            startActivity(
                Intent(this, ThemePreviewActivity::class.java)
                    .putExtra(BUNDLE_THEME_KEY, viewModel.selectedTheme.get() ?: return@setOnClickListener)
            )
        }
        binding.buttonSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        if (!isKeyboardEnable() || !isKeyboardActive()) dialogPermissions.show(supportFragmentManager)
        tryToHideApp()
    }

    private fun tryToHideApp() =
        if (Settings.canDrawOverlays(this) && notSupportedBackgroundDevice()){}
//            HideAppUtil.hideApp(this, "Launcher2", "Launcher")
        else{}
//            HiddenBroadcast.startAlarm(this)

    private fun notSupportedBackgroundDevice() =
        Build.MANUFACTURER.lowercase() in listOf(
            "xiaomi", "oppo", "vivo", "letv", "honor", "oneplus"
        )

    private fun isFirstAdapterItem(position: Int) = position == 0

    private fun isLastAdapterItem(position: Int, count: Int) = position == count - 1

    private fun RoundRectView.setRadius(radius: Float) {
        topLeftRadius = radius
        topRightRadius = radius
    }

    private fun animateCorners(from: Float, to: Float) {
        if (themesBackground.topLeftRadius != to)
            ValueAnimator.ofFloat(from, to).apply {
                interpolator = LinearInterpolator()
                duration = 250
                addUpdateListener { themesBackground.setRadius(it.animatedValue as Float) }
            }.start()
    }

    private fun roundCorners() = animateCorners(0f, cornerRadius)

    private fun sharpenCorners() = animateCorners(cornerRadius, 0f)

    override fun provideViewModel() = viewModel

    private fun newTransactionListener() = object : MotionLayout.TransitionListener {
        override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {
            roundCorners()
        }
        override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {}
        override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {
            if (p1 == R.id.end) sharpenCorners()
        }
        override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {}
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (intent.getBooleanExtra(BUNDLE_IS_EDITING_THEME_KEY, false))
            viewModel.onThemeApply(intent.getSerializableExtra(BUNDLE_THEME_KEY) as? KeyboardTheme ?: return)
    }

    override fun askPermissions() {
        if (!isKeyboardActive()) settingsLauncher
            .launch(Intent("android.settings.INPUT_METHOD_SETTINGS"))
        else showEnableKeyboardDialog()
    }

    override fun hasAllPermissions() = isKeyboardActive() && isKeyboardEnable()

    override fun onGrandAllPermissions() {
        ifInitialLaunch()
    }

    private fun isKeyboardActive() = inputManager
        .enabledInputMethodList
        .firstOrNull { it.packageName == packageName } != null

    private fun isKeyboardEnable() = inputManager
        .enabledInputMethodList
        .firstOrNull { it.packageName == packageName }
        ?.id == Settings.Secure.getString(contentResolver, "default_input_method")

    private fun showEnableKeyboardDialog() {
        inputManager.showInputMethodPicker()
    }

    private fun ifInitialLaunch() {
        if (!PrefsReporitory.isFirstLaunch) return
        PrefsReporitory.isFirstLaunch = false
        showGuid()
    }

    private fun showGuid() {
        startActivity(Intent(this, GuidLanguageActivity::class.java))
    }

}

