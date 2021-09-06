package dev.patrickgold.florisboard.ui.main.activity

import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import dev.patrickgold.florisboard.R
import dev.patrickgold.florisboard.adapters.VPAdapter
import dev.patrickgold.florisboard.databinding.MainActivityBinding
import dev.patrickgold.florisboard.ui.base.BaseActivity
import dev.patrickgold.florisboard.ui.dialogs.DialogPermissions
import dev.patrickgold.florisboard.ui.main.activity.assets.FragmentAssets
import dev.patrickgold.florisboard.ui.main.activity.custom.FragmentCustomTheme
import dev.patrickgold.florisboard.ui.main.activity.settings.FragmentSettings
import dev.patrickgold.florisboard.ui.preview.theme.activity.ThemePreviewActivity

class MainActivity : BaseActivity<MainActivityViewModel, MainActivityBinding>(R.layout.main_activity),
    DialogPermissions.OnPermissionAction {

    private val viewModel: MainActivityViewModel by viewModels {
        MainActivityViewModel.Factory(VPAdapter(this, getFragmets()))
    }

    private val dialogPermissions = DialogPermissions(this)

    private val inputManager by lazy { getSystemService(InputMethodManager::class.java) }
    private val settingsLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { _ ->
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
            viewModel.nextActivity.observe(this@MainActivity, { showNexActivity(it) })
        }
        binding.bottomBar.onPageChange = viewModel.currentPage::set
        binding.mainScreens.isUserInputEnabled = false
        binding.mainScreens.offscreenPageLimit = 3
    }

    private fun onThemeClick(it: String?) {
        Intent(this@MainActivity, ThemePreviewActivity::class.java)
            .let(::startActivity)
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
        if (!isKeyboardEnable() || !isKeyboardActive()) dialogPermissions.show(supportFragmentManager)
    }

    override fun askPermissions() {
        if (!isKeyboardActive()) settingsLauncher
            .launch(Intent("android.settings.INPUT_METHOD_SETTINGS"))
        else showEnableKeyboardDialog()
    }

    override fun hasAllPermissions() = isKeyboardActive() && isKeyboardEnable()

}
