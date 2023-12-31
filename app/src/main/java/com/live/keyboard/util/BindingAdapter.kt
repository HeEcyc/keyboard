package com.live.keyboard.util

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.view.View
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
import com.isseiaoki.simplecropview.CropImageView
import com.live.keyboard.data.KeyboardTheme
import com.live.keyboard.ui.theme.editor.activity.ThemeEditorViewModel
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

@BindingAdapter("colorItem")
fun AppCompatImageView.setColorItem(textColor: ThemeEditorViewModel.Color) {
    val stockeColor = if (textColor.isSelected) textColor.getBorderColor()
    else textColor.stockeColor ?: textColor.textColor
    val gradientDrawable = background as GradientDrawable
    gradientDrawable.setColor(Color.parseColor(textColor.textColor))
    gradientDrawable.setStroke(context.dpToPx(2), Color.parseColor(stockeColor))
}

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

@BindingAdapter("previewTheme")
fun AppCompatImageView.setColor(keyboardTheme: KeyboardTheme?) {
    with(Glide.with(this)) {
        if (keyboardTheme?.previewImage != null)
            load(Uri.parse("file:///android_asset/ime/preview/${keyboardTheme.previewImage}"))
        else load(File(context.filesDir, "${keyboardTheme?.id}.png"))
            .signature(ObjectKey(File(context.filesDir, "${keyboardTheme?.id}.png").lastModified()))
    }.into(this)
}

object Converter {

    @JvmStatic
    fun previewUri(backgroundView: ThemeEditorViewModel.BackgroundAsset.BackgroundTheme?) = backgroundView?.uri
}
