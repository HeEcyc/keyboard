package com.live.keyboard.ui.crop.activity

import androidx.activity.viewModels
import com.live.keyboard.R
import com.live.keyboard.databinding.CropActivityBinding
import com.live.keyboard.ui.base.BaseActivity
import com.live.keyboard.util.BUNDLE_CROPPED_IMAGE_KEY

class CropActivity : BaseActivity<CropViewModel, CropActivityBinding>(R.layout.crop_activity) {

    private val viewModel: CropViewModel by viewModels()

    override fun provideViewModel() = viewModel

    override fun setupUI() {
        viewModel.imageUri.set(intent.getStringExtra(BUNDLE_CROPPED_IMAGE_KEY))
        binding.cropView.setCustomRatio(100,45)

        viewModel.cropeedImageUri.observe(this, {
            setResult(RESULT_OK, intent.putExtra(BUNDLE_CROPPED_IMAGE_KEY, it))
            finish()
        })
    }
}
