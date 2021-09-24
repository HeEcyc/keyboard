package com.live.keyboard.ui.dialogs

import android.app.AlertDialog
import android.content.Context
import android.view.ViewGroup.LayoutParams
import com.live.keyboard.R
import com.live.keyboard.ui.custom.SwipeAnimationView
import kotlinx.coroutines.runBlocking

class DialogSwipeAnimationView(context: Context, animationType: SwipeAnimationView.AnimationType) {

    private val swipeAnimationView = SwipeAnimationView(context).apply {
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
    }
    private val dialog = AlertDialog
        .Builder(context, R.style.AlterDialog)
        .setCancelable(true)
        .setTitle(animationType.descriptionRes)
        .setView(swipeAnimationView)
        .create()
        .apply {
            setOnShowListener {
                runBlocking {
                    swipeAnimationView.initKeyboard()
                    swipeAnimationView.showKeyboardAnimation(animationType)
                }
            }
        }

    fun show() = dialog.show()

}
