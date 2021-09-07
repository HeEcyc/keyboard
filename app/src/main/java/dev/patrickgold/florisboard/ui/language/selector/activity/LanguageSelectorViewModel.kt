package dev.patrickgold.florisboard.ui.language.selector.activity

import dev.patrickgold.florisboard.R
import dev.patrickgold.florisboard.databinding.ItemLanguageBinding
import dev.patrickgold.florisboard.ui.base.BaseViewModel
import dev.patrickgold.florisboard.ui.base.createAdapter
import dev.patrickgold.florisboard.ui.custom.ItemDecorationWithEnds
import dev.patrickgold.florisboard.util.enums.Language
import dev.patrickgold.florisboard.util.toPx

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
