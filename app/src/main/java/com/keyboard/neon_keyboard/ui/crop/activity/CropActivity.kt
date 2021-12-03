package com.keyboard.neon_keyboard.ui.crop.activity

import androidx.activity.viewModels
import com.keyboard.neon_keyboard.R
import com.keyboard.neon_keyboard.databinding.CropActivityBinding
import com.keyboard.neon_keyboard.ui.base.BaseActivity
import com.keyboard.neon_keyboard.util.BUNDLE_CROPPED_IMAGE_KEY

class CropActivity : BaseActivity<CropViewModel, CropActivityBinding>(R.layout.crop_activity) {

    private val viewModel: CropViewModel by viewModels()

    override fun provideViewModel() = viewModel

    override fun setupUI() {
        viewModel.imageUri.set(intent.getStringExtra(BUNDLE_CROPPED_IMAGE_KEY))
        binding.cropView.setCustomRatio(100, 45)

        viewModel.cropeedImageUri.observe(this, {
            setResult(RESULT_OK, intent.putExtra(BUNDLE_CROPPED_IMAGE_KEY, it))
            finish()
        })
    }
}
