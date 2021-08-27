package dev.patrickgold.florisboard.background.view.keyboard.repository

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup.LayoutParams
import androidx.lifecycle.MutableLiveData
import com.doctoror.particlesdrawable.ParticlesView
import com.nfaralli.particleflow.ParticlesSurfaceView
import dev.patrickgold.florisboard.background.view.keyboard.views.FluidView

object BackgroundViewRepository {

    private var targetViewType: Class<out View>?
        get() = ParticlesSurfaceView::class.java
        set(value) {}
    private var targetViewAttrs: AttributeSet?
        get() = null
        set(value) {}
    private var targetViewDefStyleAttr: Int?
        get() = null
        set(value) {}
    private var targetViewDefStyleRes: Int?
        get() = null
        set(value) {}

    val newBackgroundViews = MutableLiveData<ViewFactory?>()

    fun dispatchBackgroundView(context: Context): View? {
        val attrs = targetViewAttrs
        val defStyleAttr = targetViewDefStyleAttr
        val defStyleRes = targetViewDefStyleRes
        val targetViewConstructorParameterTypes = mutableListOf<Class<*>>(Context::class.java)
        val targetViewConstructorArguments = mutableListOf<Any?>(context)
        if (true) {
            targetViewConstructorParameterTypes.add(AttributeSet::class.java)
            targetViewConstructorArguments.add(attrs)
            if (defStyleAttr !== null) {
                targetViewConstructorParameterTypes.add(Int::class.java)
                targetViewConstructorArguments.add(defStyleAttr)
                if (defStyleRes !== null) {
                    targetViewConstructorParameterTypes.add(Int::class.java)
                    targetViewConstructorArguments.add(defStyleRes)
                }
            }
        }
        return targetViewType
            ?.getConstructor(*targetViewConstructorParameterTypes.toTypedArray())
            ?.newInstance(*targetViewConstructorArguments.toTypedArray())
            ?.apply {
                layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
            }
    }

    fun setTargetViewParams(attrs: AttributeSet?) =
        setTargetViewParams(attrs, null, null)

    fun setTargetViewParams(attrs: AttributeSet?, defStyleAttr: Int) =
        setTargetViewParams(attrs, defStyleAttr, null)

    fun setTargetViewParams(attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) =
        setTargetViewParams(attrs, defStyleAttr as Int?, defStyleRes as Int?)

    private fun setTargetViewParams(attrs: AttributeSet?, defStyleAttr: Int?, defStyleRes: Int?) {
        targetViewAttrs = attrs
        targetViewDefStyleAttr = defStyleAttr
        targetViewDefStyleRes = defStyleRes
    }

    abstract class ViewFactory {
        abstract fun createView(context: Context): View
    }

}
