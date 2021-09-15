package com.live.keyboard.ui.language.selector.activity

import com.live.keyboard.R
import com.live.keyboard.databinding.ItemLanguageBinding
import com.live.keyboard.ui.base.BaseViewModel
import com.live.keyboard.ui.base.createAdapter
import com.live.keyboard.ui.custom.ItemDecorationWithEnds
import com.live.keyboard.util.enums.Language
import com.live.keyboard.util.toPx

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
        if (canChangeSelection) {
            language.isSelected = !language.isSelected
            adapter.updateItem(language)
        }
    }

}
