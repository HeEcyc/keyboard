package com.ioskey.iosboard.ui.language.selector.activity

import com.ioskey.iosboard.R
import com.ioskey.iosboard.databinding.ItemLanguageBinding
import com.ioskey.iosboard.ui.base.BaseViewModel
import com.ioskey.iosboard.ui.base.createAdapter
import com.ioskey.iosboard.ui.custom.ItemDecorationWithEnds
import com.ioskey.iosboard.util.enums.Language
import com.ioskey.iosboard.util.toPx

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
