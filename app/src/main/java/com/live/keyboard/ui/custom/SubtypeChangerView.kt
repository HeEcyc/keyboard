package com.live.keyboard.ui.custom

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
import com.live.keyboard.R
import com.live.keyboard.databinding.SubTypeChangerViewBinding
import com.live.keyboard.ime.core.Subtype
import com.live.keyboard.util.enums.Language


class SubtypeChangerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr), ViewPager.OnPageChangeListener {

    var subtypes = listOf<Subtype>()
    var previouAbs: Int? = null
    var onPageSelected: ((Subtype) -> Unit)? = null

    val binding: SubTypeChangerViewBinding = DataBindingUtil.inflate(
        LayoutInflater.from(context),
        R.layout.sub_type_changer_view,
        this,
        true
    )!!

    init {
        binding.viewPager.addOnPageChangeListener(this)
    }

    fun showViewPager(languages: List<Subtype>, activeSubtype: Subtype) {
        if (visibility == View.VISIBLE) return
        subtypes = languages

        binding.viewPager.offscreenPageLimit = 3
        visibility = View.VISIBLE
        binding.viewPager.adapter = LanguageAdapter(languages)
        binding.viewPager.currentItem = languages.indexOf(activeSubtype)
        binding.viewPager.beginFakeDrag()
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
        previouAbs = null
        if (visibility != View.VISIBLE) return
        visibility = View.GONE
        binding.viewPager.endFakeDrag()
    }


    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

    }

    override fun onPageSelected(position: Int) {
        onPageSelected?.invoke(subtypes[position])
    }

    override fun onPageScrollStateChanged(state: Int) {

    }

    fun onEvent(event: MotionEvent, spaceCenter: Float) {
        binding.viewPager.onTouchEvent(event)
        when (event.action) {
            MotionEvent.ACTION_MOVE -> {
                binding.viewPager.fakeDragBy((spaceCenter - event.x) * 0.6f)
            }
        }
    }
}


