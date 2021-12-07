package com.neonkeyboard.cool.ui.language.selector.activity

import com.neonkeyboard.cool.R
import com.neonkeyboard.cool.databinding.ItemLanguageBinding
import com.neonkeyboard.cool.ui.base.BaseViewModel
import com.neonkeyboard.cool.ui.base.createAdapter
import com.neonkeyboard.cool.ui.custom.ItemDecorationWithEnds
import com.neonkeyboard.cool.util.enums.Language
import com.neonkeyboard.cool.util.toPx

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
