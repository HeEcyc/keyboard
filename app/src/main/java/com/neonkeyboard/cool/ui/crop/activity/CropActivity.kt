package com.neonkeyboard.cool.ui.crop.activity

import androidx.activity.viewModels
import com.neonkeyboard.cool.R
import com.neonkeyboard.cool.databinding.CropActivityBinding
import com.neonkeyboard.cool.ui.base.BaseActivity
import com.neonkeyboard.cool.util.BUNDLE_CROPPED_IMAGE_KEY

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
