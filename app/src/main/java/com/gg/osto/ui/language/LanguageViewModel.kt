package com.gg.osto.ui.language

import com.gg.osto.R
import com.gg.osto.databinding.ItemLanguageBinding
import com.gg.osto.ui.base.BaseViewModel
import com.gg.osto.ui.base.createAdapter
import com.gg.osto.util.enums.Language

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
