package com.ioskey.iosboard.ui.custom

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class ItemDecorationWithEndsLegacy(
    private val topFirst: Int = 0,
    private val top: Int = 0,
    private val topLast: Int = 0,
    private val bottomFirst: Int = 0,
    private val bottom: Int = 0,
    private val bottomLast: Int = 0,
    private val left: Int = 0,
    private val right: Int = 0
) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.left = left
        outRect.right = right
        val position = parent.getChildAdapterPosition(view)
        val adapter = parent.adapter
        outRect.top = if (position == 0)
            topFirst
        else if (adapter !== null && position == adapter.itemCount - 1)
            topLast
        else
            top
        outRect.bottom = if (position == 0)
            bottomFirst
        else if (adapter !== null && position == adapter.itemCount - 1)
            bottomLast
        else
            bottom
    }
}
