package com.cccomba.board.ui.custom

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.cccomba.board.R
import com.cccomba.board.databinding.SubTypeChangerViewBinding
import com.cccomba.board.ime.core.Subtype
import com.cccomba.board.util.enums.Language


class SubtypeChangerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr), ViewPager.OnPageChangeListener {

    var startPosition: Float? = null
    var currentSubtype: Subtype? = null
    var currentPage: Int = 0
    var subtypes = mutableListOf<Subtype>()
    var onPageSelected: ((Subtype) -> Unit)? = null

    val binding: SubTypeChangerViewBinding = DataBindingUtil.inflate(
        LayoutInflater.from(context),
        R.layout.sub_type_changer_view,
        this,
        true
    )!!

    init {
        binding.viewPager.addOnPageChangeListener(this)
        binding.viewPager
        binding.viewPager.beginFakeDrag()
    }

    fun showViewPager(languages: List<Subtype>, activeSubtype: Subtype) {
        if (visibility == View.VISIBLE) return
        subtypes.clear()
        startPosition = null
        currentSubtype = null
        subtypes = createLanguageList(languages, activeSubtype)
        binding.viewPager.offscreenPageLimit = subtypes.size
        binding.viewPager.adapter = LanguageAdapter(subtypes)
        binding.viewPager.currentItem = subtypes.size / 2
        visibility = View.VISIBLE
        currentPage = binding.viewPager.currentItem
    }

    private fun createLanguageList(languages: List<Subtype>, activeSubtype: Subtype): MutableList<Subtype> {
        val listWithOutCurrentSubtype = languages.toMutableList().apply { remove(activeSubtype) }
        return mutableListOf<Subtype>().apply {
            addAll(listWithOutCurrentSubtype)
            add(activeSubtype)
            addAll(listWithOutCurrentSubtype.reversed())
        }
    }

    class LanguageAdapter(private val language: List<Subtype>) :
        PagerAdapter() {

        override fun getCount() = language.size

        override fun isViewFromObject(view: View, `object`: Any) =
            view == `object`

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            (container as ViewPager).removeView(`object` as View)
        }

        override fun instantiateItem(container: View, position: Int): Any {
            val languageView = AppCompatTextView(container.context).apply {
                text = language[position].locale.language.let(Language::from).languageName
                setTextColor(Color.WHITE)
                gravity = Gravity.CENTER
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }
            (container as ViewPager).addView(languageView)
            return languageView
        }
    }

    fun hide() {
        if (visibility != View.VISIBLE) return
        visibility = View.GONE
        if (binding.viewPager.isFakeDragging)
            binding.viewPager.adapter = null
        onPageSelected?.invoke(currentSubtype ?: return)
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        if (positionOffset == 0.0f) currentPage = position
        val currentPosition = if (position == currentPage)
            if (positionOffset > 0.7) position + 1
            else position
        else
            if (positionOffset < 0.3) position
            else position + 1

        currentSubtype = subtypes[currentPosition]
    }

    override fun onPageSelected(position: Int) {
        currentPage = position
    }

    override fun onPageScrollStateChanged(state: Int) {

    }

    fun onEvent(event: MotionEvent) {
        if (visibility != View.VISIBLE && !binding.viewPager.isFakeDragging)
            return
        if (startPosition != null && binding.viewPager.adapter != null) when (event.action) {
            MotionEvent.ACTION_MOVE ->
                binding.viewPager
                    .fakeDragBy(-1 * (startPosition!! - event.x))
        }
        startPosition = event.x
    }
}


