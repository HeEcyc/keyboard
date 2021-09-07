package dev.patrickgold.florisboard.util

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.isseiaoki.simplecropview.CropImageView
import com.makeramen.roundedimageview.RoundedImageView
import dev.patrickgold.florisboard.background.view.keyboard.repository.BackgroundViewRepository
import dev.patrickgold.florisboard.ui.theme.editor.activity.ThemeEditorViewModel

@BindingAdapter("itemDecoration")
fun RecyclerView.itemDecoration(itemDecoration: RecyclerView.ItemDecoration) {
    addItemDecoration(itemDecoration)
}

@BindingAdapter("imageUri")
fun RoundedImageView.itemDecoration(uri: Uri?) {
    Glide.with(this)
        .load(uri)
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

@BindingAdapter("colorBackground")
fun FrameLayout.setBgColor(color: String?) {
    if (childCount > 0) removeAllViews()
    setBackgroundColor(color?.let { Color.parseColor(color) } ?: Color.TRANSPARENT)
}

@BindingAdapter("imageBackground")
fun FrameLayout.backgroundView(backgroundView: BackgroundViewRepository.BackgroundView?) {
    if (childCount > 0) removeViewAt(0)
    backgroundView ?: return
    addView(backgroundView.getViewFactory().createView(context))
}

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
