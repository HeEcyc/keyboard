package com.ioskey.iosboard.ui.crop.activity

import androidx.activity.viewModels
import com.ioskey.iosboard.R
import com.ioskey.iosboard.databinding.CropActivityBinding
import com.ioskey.iosboard.ui.base.BaseActivity
import com.ioskey.iosboard.util.BUNDLE_CROPPED_IMAGE_KEY

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
        binding.backButton.setOnClickListener { onBackPressed() }
    }
}
