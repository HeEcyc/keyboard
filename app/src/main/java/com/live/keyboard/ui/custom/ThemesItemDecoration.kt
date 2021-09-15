package com.live.keyboard.ui.custom

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class ThemesItemDecoration constructor(private val spanCount: Int, private val spacing: Int) :
    RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {

        val position = parent.getChildAdapterPosition(view)
        val column = position % spanCount

        outRect.left = if (column == 0) spacing else spacing / 2
        outRect.right = if (column == spanCount - 1) spacing else spacing / 2

        outRect.top = spacing

    }
}
