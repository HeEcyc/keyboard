package com.ioskey.iosboard.ui.dialogs

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ObservableBoolean
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.RecyclerView
import com.ioskey.iosboard.R
import com.ioskey.iosboard.databinding.ItemLanguageInittialDialogBinding
import com.ioskey.iosboard.ui.base.createAdapter
import com.ioskey.iosboard.ui.custom.ItemDecorationWithEnds
import com.ioskey.iosboard.util.enums.Language
import com.ioskey.iosboard.util.toPx

class DialogInitialSelectLanguage : DialogFragment(R.layout.initial_select_language_dialog) {

    private val languages = Language.values().map { LanguageViewModel(it) }

    var onClosed: () -> Unit = {}

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isCancelable = false

        view.apply {
            findViewById<RecyclerView>(R.id.recyclerView).apply {
                adapter = createAdapter<LanguageViewModel, ItemLanguageInittialDialogBinding>(R.layout.item_language_inittial_dialog) {
                    initItems = languages
                    onItemClick = lambda@ {
                        if (it.isSelected.get() && languages.count { it.isSelected.get() } < 2) return@lambda
                        it.isSelected.set(!it.isSelected.get())
                    }
                }
                addItemDecoration(ItemDecorationWithEnds(
                    topFirst = 9.toPx(),
                    bottomFirst = 9.toPx(),
                    bottom = 9.toPx(),
                    bottomLast = 9.toPx()
                ))
            }
            findViewById<View>(R.id.closeButton).setOnClickListener { dismiss(); onClosed() }
            findViewById<View>(R.id.saveButton).setOnClickListener {
                languages.forEach {
                    it.language .isSelected = it.isSelected.get()
                }
                onClosed()
                dismiss()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        dialog?.window?.apply {
            setGravity(Gravity.CENTER)
            setBackgroundDrawableResource(android.R.color.transparent)
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        }
    }

    class LanguageViewModel(val language: Language) {
        val isSelected = ObservableBoolean(language == Language.EN)
    }

}
