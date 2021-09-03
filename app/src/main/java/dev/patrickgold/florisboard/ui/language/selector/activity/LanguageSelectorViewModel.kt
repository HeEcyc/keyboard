package dev.patrickgold.florisboard.ui.language.selector.activity

import dev.patrickgold.florisboard.R
import dev.patrickgold.florisboard.databinding.ItemLanguageBinding
import dev.patrickgold.florisboard.ui.base.BaseViewModel
import dev.patrickgold.florisboard.ui.base.createAdapter
import dev.patrickgold.florisboard.ui.custom.HorizontalItemDecoration

class LanguageSelectorViewModel : BaseViewModel() {

    val itemDecoration = HorizontalItemDecoration(50)
    val adapter = createAdapter<LanguageModel, ItemLanguageBinding>(R.layout.item_language) {
        initItems = getLanguageModels()
        onItemClick = ::handleLanguagePress
    }

    private fun getLanguageModels() = listOf(LanguageModel())

    private fun handleLanguagePress(languageModel: LanguageModel) {
        languageModel.isSelected = !languageModel.isSelected
        adapter.updateItem(languageModel)
    }

    data class LanguageModel(var name: String = System.currentTimeMillis().toString(), var isSelected: Boolean = false)
}
