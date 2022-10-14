package com.smard.boart.ui.language

import com.smard.boart.R
import com.smard.boart.databinding.ItemLanguageBinding
import com.smard.boart.ui.base.BaseViewModel
import com.smard.boart.ui.base.createAdapter
import com.smard.boart.util.enums.Language

class LanguageViewModel : BaseViewModel() {
    val adapter = createAdapter<Language, ItemLanguageBinding>(R.layout.item_language) {
        initItems = Language.values().toList()
        onItemClick = ::handleLanguagePress
    }

    private fun handleLanguagePress(language: Language) {
        val canChangeSelection = !language.isSelected || Language.values().count { it.isSelected } > 1
        if (!canChangeSelection) return
        language.isSelected = !language.isSelected
        adapter.updateItem(language)
    }
}
