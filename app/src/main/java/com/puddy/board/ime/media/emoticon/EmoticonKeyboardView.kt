package com.puddy.board.ime.media.emoticon

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import com.puddy.board.ime.core.FlorisBoard
import kotlinx.coroutines.*

/**
 * Manages the layout creation and touch events for the emoticon section of the media context. Parts
 * of the layout of this view will be generated in coroutines and will therefore not instantly be
 * visible.
 */
class EmoticonKeyboardView : LinearLayout {
    private val florisboard: FlorisBoard = FlorisBoard.getInstance()
    private var layout: Deferred<EmoticonLayoutData?>
    private val mainScope = MainScope()

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        layout = mainScope.async(Dispatchers.IO) {
            EmoticonLayoutData.fromJsonFile("ime/media/emoticon/emoticons.json")
        }
        orientation = VERTICAL
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        mainScope.launch {
            layout.await()
            buildLayout()
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mainScope.cancel()
    }

    /**
     * Builds the layout by dynamically adding rows filled with [EmoticonKeyView]s.
     * This method runs in the [Dispatchers.Default] context and will block the main thread only
     * when it attaches a built row to the view hierarchy.
     */
    private suspend fun buildLayout() = withContext(Dispatchers.Default) {
        val layout = layout.await() ?: return@withContext
        for (row in layout.arrangement) {
            val rowView = LinearLayout(context).apply {
                layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, 0).apply {
                    weight = 1.0f
                }
                orientation = HORIZONTAL
            }
            for (emoticonKeyData in row) {
                val emoticonKeyView = EmoticonKeyView(context).apply {
                    data = emoticonKeyData
                }
                rowView.addView(emoticonKeyView)
            }
            withContext(Dispatchers.Main) {
                addView(rowView)
            }
        }
    }
}
