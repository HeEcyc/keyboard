package com.live.keyboard.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.live.keyboard.BR

abstract class BaseFragment<VM : BaseViewModel, B : ViewDataBinding>(@LayoutRes open val layoutId: Int) :
    Fragment() {
    open val isEnableTransition = false
    protected var currentOpenViewId: Int? = null
    protected lateinit var binding: B

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        binding.setVariable(BR.viewModel, provideViewModel())
        binding.lifecycleOwner = this
        binding.root.isClickable = true
        binding.root.isFocusableInTouchMode = true
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    abstract fun setupUI()

    open fun provideViewModel(): VM? = null

}
