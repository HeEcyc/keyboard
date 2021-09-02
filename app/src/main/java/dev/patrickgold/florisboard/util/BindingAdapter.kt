package dev.patrickgold.florisboard.util

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.makeramen.roundedimageview.RoundedImageView
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
fun AppCompatImageView.setColorItem(textColor: ThemeEditorViewModel.TextColor) {
    val stockeColor = if (textColor.isSelected) textColor.getBorderColor()
    else textColor.stockeColor ?: textColor.textColor
    val gradientDrawable = background as GradientDrawable
    gradientDrawable.setColor(Color.parseColor(textColor.textColor))
    gradientDrawable.setStroke(context.dpToPx(2), Color.parseColor(stockeColor))
}
