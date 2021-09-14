package dev.patrickgold.florisboard.ui.crop.activity

import android.graphics.Bitmap
import android.net.Uri
import androidx.databinding.ObservableField
import androidx.lifecycle.viewModelScope
import com.isseiaoki.simplecropview.CropImageView
import com.isseiaoki.simplecropview.callback.CropCallback
import dev.patrickgold.florisboard.FlorisApplication
import dev.patrickgold.florisboard.ui.base.BaseViewModel
import dev.patrickgold.florisboard.util.SingleLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import kotlin.coroutines.suspendCoroutine

class CropViewModel : BaseViewModel() {

    val cropeedImageUri = SingleLiveData<String>()
    val imageUri = ObservableField<String>()

    fun cropImage(cropView: CropImageView) {
        viewModelScope.launch(Dispatchers.IO) {
            getCroppedBitmap(cropView)
                .let(::saveBitmapToCacheFile)
                .let(cropeedImageUri::postValue)
        }
    }

    private fun saveBitmapToCacheFile(bitmap: Bitmap): String {
        val tempImageFile = File(FlorisApplication.instance.filesDir, getTempFileName())
        BufferedOutputStream(FileOutputStream(tempImageFile))
            .use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
        return Uri.fromFile(tempImageFile).toString()
    }

    private fun getTempFileName() = "IMG_${System.currentTimeMillis()}.png"

    private suspend fun getCroppedBitmap(cropView: CropImageView): Bitmap {
        return suspendCoroutine {
            cropView.cropAsync(object : CropCallback {
                override fun onError(e: Throwable) {
                    it.resumeWith(Result.failure(e))
                }

                override fun onSuccess(cropped: Bitmap) {
                    it.resumeWith(Result.success(cropped))
                }
            })
        }
    }
}
