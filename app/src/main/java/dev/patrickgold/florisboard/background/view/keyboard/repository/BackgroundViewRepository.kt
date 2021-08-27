package dev.patrickgold.florisboard.background.view.keyboard.repository

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup.LayoutParams
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import androidx.lifecycle.MutableLiveData
import com.doctoror.particlesdrawable.ParticlesView
import com.nfaralli.particleflow.ParticlesSurfaceView

object BackgroundViewRepository {

    private var targetViewType: Class<out View>?
        get() = ParticlesSurfaceView::class.java
        set(value) {}

    val newBackgroundViews = MutableLiveData<ViewFactory?>()

    fun setNewBackgroundView(view: BackgroundView) = newBackgroundViews.postValue(view.getViewFactory())

    fun dispatchBackgroundView(context: Context): View? = targetViewType?.let {
        dispatchBackgroundView(it, context)
    }

    fun dispatchBackgroundView(viewType: Class<out View>, context: Context): View = viewType
        .getConstructor(Context::class.java, AttributeSet::class.java)
        .newInstance(context, null)
        .apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        }

    interface ViewFactory {
        fun createView(context: Context): View

        companion object {
            fun from(lambda: (Context) -> View) = object : ViewFactory {
                override fun createView(context: Context) = lambda(context)
            }
        }
    }

    sealed interface BackgroundView {
        fun getViewFactory(): ViewFactory

        object FluidView : BackgroundView {
            override fun getViewFactory() = ViewFactory.from {
                dispatchBackgroundView(dev.patrickgold.florisboard.background.view.keyboard.views.FluidView::class.java, it)
            }
        }

        object ParticleView : BackgroundView {
            override fun getViewFactory() = ViewFactory.from {
                dispatchBackgroundView(ParticlesView::class.java, it)
            }
        }

        object ParticleFlowView : BackgroundView {
            override fun getViewFactory() = ViewFactory.from {
                dispatchBackgroundView(ParticlesSurfaceView::class.java, it)
            }
        }

        class CustomView(private val getViewFrom: (Context) -> View) : BackgroundView {
            override fun getViewFactory() = ViewFactory.from(getViewFrom)
        }

        class ImageView(private val image: Bitmap) : BackgroundView {
            override fun getViewFactory() = ViewFactory.from {
                val view = dispatchBackgroundView(AppCompatImageView::class.java, it) as AppCompatImageView
                view.setImageBitmap(image)
                view.scaleType = android.widget.ImageView.ScaleType.CENTER_CROP
                view
            }
        }

    }

}
