package com.live.keyboard.ui.language.selector.activity

import androidx.lifecycle.MutableLiveData
import com.google.firebase.storage.FirebaseStorage
import com.live.keyboard.R
import com.live.keyboard.databinding.ItemLanguageBinding
import com.live.keyboard.ui.base.BaseViewModel
import com.live.keyboard.ui.base.createAdapter
import com.live.keyboard.ui.custom.ItemDecorationWithEnds
import com.live.keyboard.util.enums.Language
import com.live.keyboard.util.getFile
import com.live.keyboard.util.toPx
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LanguageSelectorViewModel : BaseViewModel() {

    val askDownloadLanguageEvents = MutableLiveData<Language>()
    val downloadingFinishedEvents = MutableLiveData<Unit>()

    val itemDecoration = ItemDecorationWithEnds(
        24.toPx(),
        5.toPx(),
        5.toPx(),
        5.toPx(),
        5.toPx(),
        24.toPx(),
        16.toPx(),
        16.toPx()
    )
    val adapter = createAdapter<Language, ItemLanguageBinding>(R.layout.item_language) {
        initItems = Language.values().toList()
        onItemClick = ::handleLanguagePress
    }

    private fun handleLanguagePress(language: Language) {
        val canChangeSelection = !language.isSelected || Language.values().count { it.isSelected } > 1
        if (!canChangeSelection) return
        if (!language.isSelected && !language.isDownloaded) {
            askDownloadLanguageEvents.postValue(language)
            return
        }
        language.isSelected = !language.isSelected
        adapter.updateItem(language)
    }

    fun downloadLanguage(language: Language) {
        language.isDownloading.set(true)
        FirebaseStorage.getInstance().reference.child(language.dictionaryJSONFile).stream.addOnSuccessListener {
            GlobalScope.launch(Dispatchers.IO) {
                language.dictionaryJSONFile.getFile().apply { createNewFile() }.writeBytes(it.stream.readBytes())
                it.stream.close()
                withContext(Dispatchers.Main) {
                    handleLanguagePress(language)
                    language.isDownloadedObservable.set(true)
                }
                downloadingFinishedEvents.postValue(Unit)
                language.isDownloading.set(false)
            }
        }.addOnFailureListener {
            downloadingFinishedEvents.postValue(Unit)
            language.isDownloading.set(false)
        }
    }

}
