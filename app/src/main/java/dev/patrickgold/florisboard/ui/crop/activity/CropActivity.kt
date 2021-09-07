package dev.patrickgold.florisboard.ui.crop.activity

import androidx.activity.viewModels
import dev.patrickgold.florisboard.R
import dev.patrickgold.florisboard.databinding.CropActivityBinding
import dev.patrickgold.florisboard.ui.base.BaseActivity
import dev.patrickgold.florisboard.util.BUNDLE_CROPPED_IMAGE_KEY

class CropActivity : BaseActivity<CropViewModel, CropActivityBinding>(R.layout.crop_activity) {

    private val viewModel: CropViewModel by viewModels()

    override fun provideViewModel() = viewModel

    override fun setupUI() {
        viewModel.imageUri.set(intent.getStringExtra(BUNDLE_CROPPED_IMAGE_KEY))
        viewModel.cropeedImageUri.observe(this, {
            setResult(RESULT_OK, intent.putExtra(BUNDLE_CROPPED_IMAGE_KEY, it))
            finish()
        })
    }
}
