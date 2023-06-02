package com.cccomba.board.ui.language

import com.cccomba.board.R
import com.cccomba.board.databinding.ItemLanguageBinding
import com.cccomba.board.ui.base.BaseViewModel
import com.cccomba.board.ui.base.createAdapter
import com.cccomba.board.util.enums.Language

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
