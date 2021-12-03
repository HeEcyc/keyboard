package com.keyboard.neon_keyboard.ui.language.selector.activity

import com.keyboard.neon_keyboard.R
import com.keyboard.neon_keyboard.databinding.ItemLanguageBinding
import com.keyboard.neon_keyboard.ui.base.BaseViewModel
import com.keyboard.neon_keyboard.ui.base.createAdapter
import com.keyboard.neon_keyboard.ui.custom.ItemDecorationWithEnds
import com.keyboard.neon_keyboard.util.enums.Language
import com.keyboard.neon_keyboard.util.toPx

class LanguageSelectorViewModel : BaseViewModel() {
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
        language.isSelected = !language.isSelected
        adapter.updateItem(language)
    }
}
