package com.ioskey.iosboard.ui.settings.options.activity

import android.content.Context
import android.content.Intent
import androidx.activity.viewModels
import androidx.databinding.ObservableBoolean
import com.ioskey.iosboard.R
import com.ioskey.iosboard.databinding.ItemOptionBinding
import com.ioskey.iosboard.databinding.OptionsActivityBinding
import com.ioskey.iosboard.ui.base.AppBaseAdapter
import com.ioskey.iosboard.ui.base.BaseActivity
import java.io.Serializable

class OptionsActivity : BaseActivity<OptionsViewModel, OptionsActivityBinding>(R.layout.options_activity) {

    private val viewModel: OptionsViewModel by viewModels {
        OptionsViewModel.Factory()
    }

    private val title: String by lazy { intent.getStringExtra(EXTRAS_TITLE)!! }
    private val selectedOption: Serializable by lazy { intent.getSerializableExtra(EXTRAS_SELECTED_OPTION)!! }
    private val options: List<Pair<Serializable, String>> by lazy {
        (0 until intent.getIntExtra(EXTRAS_OPTIONS_AMOUNT, 0)).map {
            intent.getSerializableExtra(EXTRAS_OPTION_NO_ + it) as Pair<Serializable, String>
        }
    }

    companion object {
        private const val EXTRAS_TITLE = "extras_title"
        private const val EXTRAS_OPTIONS_AMOUNT = "extras_options_amount"
        private const val EXTRAS_OPTION_NO_ = "extras_option_no_"
        private const val EXTRAS_SELECTED_OPTION = "extras_selected_option"
        private const val EXTRAS_RESULT = "extras_result"

        fun getIntent(
            title: String,
            options: List<Pair<Serializable, String>>,
            selectedOption: Serializable,
            context: Context
        ) = Intent(context, OptionsActivity::class.java).apply {
            putExtra(EXTRAS_TITLE, title)
            putExtra(EXTRAS_OPTIONS_AMOUNT, options.size)
            options.forEachIndexed { index, pair -> putExtra(EXTRAS_OPTION_NO_ + index, pair) }
            putExtra(EXTRAS_SELECTED_OPTION, selectedOption)
        }

        fun getResult(intent: Intent) = intent.getSerializableExtra(EXTRAS_RESULT)!!
    }

    private val adapter by lazy {
        AppBaseAdapter.Builder<Option, ItemOptionBinding>(R.layout.item_option).apply {
            initItems = options.map { (option, title) -> Option(option, title, option == selectedOption) }
            onItemClick = ::onOptionClick
        }.build()
    }

    override fun setupUI() {
        binding.title.text = title
        binding.recyclerView.adapter = adapter
        binding.buttonBack.setOnClickListener { onBackPressed() }
    }

    private fun onOptionClick(option: Option) {
        if (option.isSelected.get()) return
        adapter.getData().firstOrNull { it.isSelected.get() }?.isSelected?.set(false)
        option.isSelected.set(true)
    }

    override fun provideViewModel() = viewModel

    override fun onBackPressed() {
        setResult(RESULT_OK, Intent().putExtra(EXTRAS_RESULT, adapter.getData().first { it.isSelected.get() }.option))
        super.onBackPressed()
    }

    class Option(val option: Serializable, val title: String, isSelected: Boolean) {
        val isSelected = ObservableBoolean(isSelected)
    }

}
