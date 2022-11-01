package com.puddy.board.ui.language

import com.puddy.board.R
import com.puddy.board.databinding.ItemLanguageBinding
import com.puddy.board.ui.base.BaseViewModel
import com.puddy.board.ui.base.createAdapter
import com.puddy.board.util.enums.Language

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
