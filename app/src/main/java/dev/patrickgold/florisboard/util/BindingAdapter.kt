package dev.patrickgold.florisboard.util

import android.net.Uri
import android.util.Log
import android.view.View
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.makeramen.roundedimageview.RoundedImageView

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
