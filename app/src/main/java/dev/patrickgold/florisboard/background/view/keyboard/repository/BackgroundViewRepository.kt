package dev.patrickgold.florisboard.background.view.keyboard.repository

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup.LayoutParams
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
    }

    sealed interface BackgroundView {
        fun getViewFactory(): ViewFactory

        object FluidView : BackgroundView {
            override fun getViewFactory() = object : ViewFactory {
                override fun createView(context: Context) =
                    dispatchBackgroundView(dev.patrickgold.florisboard.background.view.keyboard.views.FluidView::class.java, context)
            }
        }

        object ParticleView : BackgroundView {
            override fun getViewFactory() = object : ViewFactory {
                override fun createView(context: Context) =
                    dispatchBackgroundView(ParticlesView::class.java, context)
            }
        }

        object ParticleFlowView : BackgroundView {
            override fun getViewFactory() = object : ViewFactory {
                override fun createView(context: Context) =
                    dispatchBackgroundView(ParticlesSurfaceView::class.java, context)
            }
        }

        class CustomView(private val getViewFrom: (Context) -> View) : BackgroundView {
            override fun getViewFactory() = object : ViewFactory {
                override fun createView(context: Context) = getViewFrom(context)
            }
        }

    }

}
