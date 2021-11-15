package com.live.keyboard.ui.main.activity.settings

import android.annotation.SuppressLint
import android.app.Dialog
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatSeekBar
import androidx.fragment.app.activityViewModels
import com.live.keyboard.R
import com.live.keyboard.databinding.MainFragmentSettingsBinding
import com.live.keyboard.repository.PrefsReporitory
import com.live.keyboard.ui.base.BaseFragment
import com.live.keyboard.ui.custom.SwipeAnimationView
import com.live.keyboard.ui.dialogs.DialogSwipeAnimationView
import com.live.keyboard.ui.main.activity.MainActivityViewModel

class FragmentSettings :
    BaseFragment<MainActivityViewModel, MainFragmentSettingsBinding>(R.layout.main_fragment_settings) {

    private val viewModel: MainActivityViewModel by activityViewModels()

    private var swipeSpeedDialog: Dialog? = null

    override fun provideViewModel() = viewModel

    @SuppressLint("CutPasteId")
    override fun setupUI() {
        viewModel.selectSwipeSpeedEvents.observe(this) {
            swipeSpeedDialog = AlertDialog
                .Builder(requireContext())
                .setTitle(R.string.minimum_swipe_speed)
                .setView(R.layout.swipe_speed_seekbar_layout)
                .setNegativeButton(R.string.cancel) { dialog, _ ->
                    dialog.dismiss()
                    swipeSpeedDialog = null
                }
                .setPositiveButton(R.string.ok) { dialog, _ ->
                    PrefsReporitory.Settings.minimumSwipeSpeed =
                        (swipeSpeedDialog?.findViewById<AppCompatSeekBar>(R.id.seekbar)?.progress ?: 0) + 1000
                    dialog.dismiss()
                    swipeSpeedDialog = null
                }.create()
            swipeSpeedDialog?.setOnShowListener {
                swipeSpeedDialog?.findViewById<AppCompatSeekBar>(R.id.seekbar)?.progress =
                    PrefsReporitory.Settings.minimumSwipeSpeed - 1000
            }
            swipeSpeedDialog?.show()
        }
        binding.buttonKeyboardSwipeHelp.setOnClickListener {
            DialogSwipeAnimationView().apply { animationType = SwipeAnimationView.AnimationType.SWIPE }.show(parentFragmentManager)
        }
    }

}
