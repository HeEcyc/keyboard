package com.gg.osto.ui.crop

import androidx.activity.viewModels
import com.gg.osto.R
import com.gg.osto.databinding.CropActivityBinding
import com.gg.osto.ui.base.BaseActivity
import com.gg.osto.util.BUNDLE_CROPPED_IMAGE_KEY

class CropActivity : BaseActivity<CropViewModel, CropActivityBinding>(R.layout.crop_activity) {

    private val viewModel: CropViewModel by viewModels()

    override fun provideViewModel() = viewModel

    override fun setupUI() {
        viewModel.imageUri.set(intent.getStringExtra(BUNDLE_CROPPED_IMAGE_KEY))
        binding.cropView.setCustomRatio(100, 45)

        viewModel.cropeedImageUri.observe(this) {
            setResult(RESULT_OK, intent.putExtra(BUNDLE_CROPPED_IMAGE_KEY, it))
            finish()
        }
        binding.buttonCancel.setOnClickListener { onBackPressed() }
    }
}
