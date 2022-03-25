package com.ioskey.iosboard.ui.settings.swipe.speed.dialog

import android.widget.SeekBar
import com.ioskey.iosboard.R
import com.ioskey.iosboard.databinding.SwipeSpeedDialogBinding
import com.ioskey.iosboard.repository.PrefsReporitory
import com.ioskey.iosboard.ui.base.BaseDialog

class SwipeSpeedDialog : BaseDialog<SwipeSpeedDialogBinding>(R.layout.swipe_speed_dialog) {

    override fun setupUI() {
        binding.seekBar.progress = PrefsReporitory.Settings.minimumSwipeSpeed - 1000
        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                if (p2)
                    PrefsReporitory.Settings.minimumSwipeSpeed = p1 + 1000
            }
            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {}
        })
    }

}
