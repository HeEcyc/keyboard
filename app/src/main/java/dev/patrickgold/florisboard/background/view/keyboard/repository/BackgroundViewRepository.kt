package dev.patrickgold.florisboard.background.view.keyboard.repository

import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup.LayoutParams
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.Glide
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
        .apply { layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT) }

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

        companion object {

            fun fromName(name: String?): BackgroundView? = when (name) {
                FluidView::class.java.simpleName -> FluidView
                ParticleView::class.java.simpleName -> ParticleView
                ParticleFlowView::class.java.simpleName -> ParticleFlowView
                else -> null
            }

        }

        fun name(): String = this::class.java.simpleName

        object FluidView : BackgroundView {
            override fun getViewFactory() = ViewFactory.from {
                dispatchBackgroundView(
                    dev.patrickgold.florisboard.background.view.keyboard.views.FluidView::class.java,
                    it
                )
            }
        }

        object ParticleView : BackgroundView {
            override fun getViewFactory() = ViewFactory.from {
                val view = dispatchBackgroundView(ParticlesView::class.java, it) as ParticlesView
                view.setBackgroundColor(Color.BLACK)
                view.dotColor = Color.WHITE
                view
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

        class ImageView(private val uri: Uri) : BackgroundView {
            override fun getViewFactory() = ViewFactory.from {
                val view = dispatchBackgroundView(AppCompatImageView::class.java, it) as AppCompatImageView
                view.scaleType = android.widget.ImageView.ScaleType.CENTER_CROP
                Glide.with(view)
                    .load(uri)
                    .into(view)
                view
            }
        }
    }

}
