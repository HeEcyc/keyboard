package com.puddy.board.ui.crop

import androidx.activity.viewModels
import com.puddy.board.R
import com.puddy.board.databinding.CropActivityBinding
import com.puddy.board.ui.base.BaseActivity
import com.puddy.board.util.BUNDLE_CROPPED_IMAGE_KEY

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
