package com.cccomba.board.util

import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.View
import android.view.View.MeasureSpec
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.bumptech.glide.signature.ObjectKey
import com.cccomba.board.data.KeyboardTheme
import com.cccomba.board.ui.custom.AppGradientDrawable
import com.cccomba.board.ui.custom.AppGradientShader
import com.cccomba.board.ui.edit.EditViewModel
import com.isseiaoki.simplecropview.CropImageView
import com.makeramen.roundedimageview.RoundedImageView
import java.io.File

@BindingAdapter("itemDecoration")
fun RecyclerView.itemDecoration(itemDecoration: RecyclerView.ItemDecoration) {
    addItemDecoration(itemDecoration)
}

@BindingAdapter("imageUri")
fun RoundedImageView.itemDecoration(uri: String?) {
    Glide.with(this)
        .load(Uri.parse(uri))
        .into(this)
}

@BindingAdapter("visibleId")
fun View.changeVisibilityById(visibleId: Int) {
    if (visibleId == id && visibility != View.VISIBLE) visibility = View.VISIBLE
    else if (visibleId != id && visibility == View.VISIBLE) visibility = View.GONE
}

@BindingAdapter("font")
fun AppCompatTextView.setFont(fontRes: Int) {
    typeface = ResourcesCompat.getFont(context, fontRes)
}

//@BindingAdapter("colorItem")
//fun AppCompatImageView.setColorItem(textColor: ThemeEditorViewModel.Color) {
//    val stockeColor = if (textColor.isSelected) textColor.getBorderColor()
//    else textColor.stockeColor ?: textColor.textColor
//    val gradientDrawable = background as GradientDrawable
//    gradientDrawable.setColor(Color.parseColor(textColor.textColor))
//    gradientDrawable.setStroke(context.dpToPx(2), Color.parseColor(stockeColor))
//}

@BindingAdapter("drawable")
fun AppCompatImageView.setDrawableRes(drawableRes: Int) {
    setImageResource(drawableRes)
}

@BindingAdapter("noAminatedCurretPage")
fun ViewPager2.setPage(page: Int) {
    setCurrentItem(page, true)
}

@BindingAdapter("textRes")
fun TextView.setTextFromResource(res: Int) = setText(res)

@BindingAdapter("image")
fun CropImageView.setImage(fileUri: String?) {
    fileUri ?: return
    Glide.with(context)
        .asBitmap()
        .load(Uri.parse(fileUri))
        .into(object : CustomTarget<Bitmap>() {
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                imageBitmap = resource
            }

            override fun onLoadCleared(placeholder: Drawable?) {

            }
        })
}

@BindingAdapter("image")
fun AppCompatImageView.setImage(uri: String?) {
    uri ?: return
    Glide.with(this)
        .load(Uri.parse(uri))
        .apply(RequestOptions().transform(CenterCrop()))
        .into(this)
}

@BindingAdapter("color")
fun AppCompatImageView.setColor(color: String?) {
    color ?: return
    setImageResource(0)
    setBackgroundColor(Color.parseColor(color))
}

@BindingAdapter("bg")
fun View.setBG(color: String?) {
    color ?: return
    setBackgroundColor(Color.parseColor(color))
}

@BindingAdapter("previewTheme")
fun AppCompatImageView.setColor(keyboardTheme: KeyboardTheme?) {
    keyboardTheme ?: return
    with(Glide.with(this)) {
        if (keyboardTheme.previewImage != null)
            load(Uri.parse("file:///android_asset/ime/preview/${keyboardTheme.previewImage}"))
        else load(File(context.filesDir, "${keyboardTheme.id}.png"))
            .signature(ObjectKey(File(context.filesDir, "${keyboardTheme.id}.png").lastModified()))
    }.into(this)
}

object Converter {

    @JvmStatic
    fun previewUri(backgroundView: EditViewModel.BackgroundAsset.BackgroundTheme?) = backgroundView?.uri
}

@BindingAdapter("textColorInt")
fun setTextColorInt(tv: TextView, c: Int) {
    tv.setTextColor(c)
}

@BindingAdapter("isBold")
fun setIsBold(tv: TextView, b: Boolean) {
    tv.setTypeface(tv.typeface, if (b) Typeface.BOLD else Typeface.NORMAL)
}

@BindingAdapter("adapter")
fun setRecyclerViewAdapter(rv: RecyclerView, rva: RecyclerView.Adapter<*>) {
    rv.adapter = rva
}

@BindingAdapter("textId")
fun setTextId(tv: TextView, t: Int) {
    tv.setText(t)
}

@BindingAdapter("tint")
fun setTint(iv: AppCompatImageView, c: Int) {
    iv.imageTintList = ColorStateList.valueOf(c)
}

@BindingAdapter("setAppGradientBackground")
fun setAppGradientBackground(v: View, b: Boolean) {
    v.post { v.background = if (b) AppGradientDrawable() else null }
}

@BindingAdapter("setAppGradientText")
fun setAppGradientText(tv: TextView, b: Boolean) {
    tv.post {
        tv.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED)
        tv.paint.shader = if (b) AppGradientShader(tv.measuredWidth.toFloat(), tv.measuredHeight.toFloat()) else null
        tv.invalidate()
    }
}

@BindingAdapter("fontRes")
fun setFontRes(tv: TextView, f: Int) {
    tv.typeface = ResourcesCompat.getFont(tv.context, f)
}