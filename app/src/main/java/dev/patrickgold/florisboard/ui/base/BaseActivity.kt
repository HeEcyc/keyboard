package dev.patrickgold.florisboard.ui.base

import android.content.pm.PackageManager
import android.os.Bundle
import android.os.StrictMode
import android.view.Window
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import dev.patrickgold.florisboard.BR
import dev.patrickgold.florisboard.ui.theme.editor.activity.ThemeEditorViewModel

abstract class BaseActivity<TViewModel : BaseViewModel, TBinding : ViewDataBinding>(@LayoutRes val layout: Int) :
    AppCompatActivity() {

    private var onPermission: ((Boolean) -> Unit)? = null
    lateinit var binding: TBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StrictMode.setVmPolicy(StrictMode.VmPolicy.Builder().build())
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = DataBindingUtil.setContentView(this, layout)
        binding.setVariable(BR.viewModel, provideViewModel())
        binding.lifecycleOwner = this
        initListeners()
        setupUI()
    }

    private fun initListeners() {
        provideViewModel().newChooserDialogs.observe(this) { it.show(this) }
    }

    abstract fun setupUI()

    abstract fun provideViewModel(): TViewModel

    fun askPermission(permissions: Array<String>, onPermission: ((Boolean) -> Unit)? = null) {
        if (hasPermission(permissions)) onPermission?.invoke(true)
        else {
            this.onPermission = onPermission
            ActivityCompat.requestPermissions(this, permissions, 200)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onPermission?.let { it(!grantResults.contains(PackageManager.PERMISSION_DENIED)) }
        onPermission = null
    }

    private fun hasPermission(permissions: Array<String>) = permissions
        .firstOrNull { ActivityCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_DENIED } != null
}
