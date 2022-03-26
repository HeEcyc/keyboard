package com.ioskey.iosboard.ui.guid.activity.third

import android.content.Intent
import android.view.View
import androidx.activity.viewModels
import com.ioskey.iosboard.R
import com.ioskey.iosboard.databinding.GuidThemesActivityBinding
import com.ioskey.iosboard.ui.base.BaseActivity
import com.ioskey.iosboard.ui.custom.ItemDecorationWithEnds
import com.ioskey.iosboard.ui.theme.editor.activity.ThemeEditorActivity

class GuidThemeActivity : BaseActivity<GuidThemeViewModel, GuidThemesActivityBinding>(R.layout.guid_themes_activity) {

    val viewModel: GuidThemeViewModel by viewModels()

    override fun setupUI() {
        binding.root.post {
            val verticalOffset = binding.root.width / 411 * 6
            val horizontalOffsetInner = binding.root.width / 411 * 6
            val horizontalOffsetOuter = binding.root.width / 411 * 17
            binding.recyclerView.addItemDecoration(ItemDecorationWithEnds(
                topFirst = verticalOffset,
                topLast = verticalOffset,
                bottomFirst = verticalOffset,
                bottomLast = verticalOffset,
                leftFirst = horizontalOffsetOuter,
                leftLast = horizontalOffsetInner,
                rightFirst = horizontalOffsetInner,
                rightLast = horizontalOffsetOuter,
                firstPredicate = ::isFirst,
                lastPredicate = ::isLast
            ))
        }
        binding.buttonExit.setOnClickListener { onBackPressed() }
        binding.buttonNew.setOnClickListener {
            startActivity(Intent(this, ThemeEditorActivity::class.java))
            finish()
        }
        viewModel.finishActivity.observe(this) { onBackPressed() }
    }

    private fun isFirst(position: Int) = if (binding.root.layoutDirection == View.LAYOUT_DIRECTION_LTR)
        position % 2 == 0 else position % 2 == 1

    private fun isLast(position: Int, count: Int) = if (binding.root.layoutDirection == View.LAYOUT_DIRECTION_LTR)
        position % 2 == 1 else position % 2 == 0

    override fun provideViewModel() = viewModel

}
