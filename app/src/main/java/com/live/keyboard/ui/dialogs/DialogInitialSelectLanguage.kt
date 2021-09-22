package com.live.keyboard.ui.dialogs

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ObservableBoolean
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.storage.FirebaseStorage
import com.live.keyboard.R
import com.live.keyboard.databinding.ItemLanguageInittialDialogBinding
import com.live.keyboard.ui.base.createAdapter
import com.live.keyboard.ui.custom.ItemDecorationWithEnds
import com.live.keyboard.util.enums.Language
import com.live.keyboard.util.getFile
import com.live.keyboard.util.toPx
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
                    if (it.isSelected.get()) downloadLanguage(it.language)
                    else it.language.isSelected = false
                }
                onClosed()
                dismiss()
            }
        }
    }

    private fun downloadLanguage(language: Language) {
        if (language == Language.EN) {
            language.isSelected = true
            return
        }
        language.isDownloading.set(true)
        FirebaseStorage.getInstance().reference.child(language.dictionaryJSONFile).stream.addOnSuccessListener {
            GlobalScope.launch(Dispatchers.IO) {
                language.dictionaryJSONFile.getFile().apply { createNewFile() }.writeBytes(it.stream.readBytes())
                it.stream.close()
                withContext(Dispatchers.Main) {
                    language.isDownloadedObservable.set(true)
                }
                language.isDownloading.set(false)
                language.isSelected = true
            }
        }.addOnFailureListener {
            language.isDownloading.set(false)
            language.isSelected = false
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
