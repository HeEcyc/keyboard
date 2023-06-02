package com.cccomba.board.ui.base

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.cccomba.board.BR


abstract class AppBaseAdapter<T, V : ViewDataBinding> private constructor(initItems: List<T>? = null) :
    RecyclerView.Adapter<AppBaseAdapter.BaseItem<T, out ViewDataBinding>>() {
    protected var items: MutableList<T> = mutableListOf()

    init {
        initItems?.let { items.addAll(it) }
    }

    override fun onCreateViewHolder(@NonNull viewGroup: ViewGroup, i: Int): BaseItem<T, V> =
        createHolder(getViewBinding(LayoutInflater.from(viewGroup.context), viewGroup, i))

    abstract fun getViewBinding(inflater: LayoutInflater, viewGroup: ViewGroup, viewType: Int): V

    fun clearItems() {
        if (items.isEmpty()) return
        val size = items.size
        items.clear()
        notifyItemRangeRemoved(0, size)
    }

    open fun addItems(newItems: List<T>) {
        val oldItemSize = itemCount
        items.addAll(newItems)
        notifyItemRangeInserted(oldItemSize, itemCount)
    }

    open fun addItem(newItem: T) {
        items.add(newItem)
        notifyItemInserted(itemCount)
    }

    open fun addItem(pos: Int, newItem: T) {
        items.add(pos, newItem)
        notifyItemInserted(pos)
    }

    open fun reloadData(items: List<T>) {
        onNewItems(items)
        notifyDataSetChanged()
    }

    fun onNewItems(items: List<T>) {
        this.items.clear()
        this.items.addAll(items)
    }

    fun getData() = items

    open fun updateItem(pos: Int) {
        if (pos == -1) return
        notifyItemChanged(pos, Unit)
    }

    open fun updateItem(t: T) {
        updateItem(items.indexOf(t))
    }

    fun removeItem(pos: Int) {
        if (pos == -1) return
        items.removeAt(pos)
        notifyItemRemoved(pos)
    }

    open fun createHolder(binding: V) = object : BaseItem<T, V>(binding) {}

    abstract class BaseItem<T, V : ViewDataBinding>(val binding: V) :
        RecyclerView.ViewHolder(binding.root) {

        open fun setVariable(t: T) {
            binding.setVariable(BR.item, t)
            binding.executePendingBindings()
        }

        open fun bind(t: T) {

        }

    }

    class Builder<T, V : ViewDataBinding> constructor(private val layoutId: Int? = null) {
        var onItemClick: ((item: T) -> Unit)? = null
        var onIndexedItemClick: ((postition: Int) -> Unit)? = null
        var initItems: List<T>? = null
        var itemViewTypeProvider: ((T) -> Int)? = null
        var viewBinding: ((inflater: LayoutInflater, viewGroup: ViewGroup, viewType: Int) -> V)? =
            null
        var onBind: ((item: T, binding: V) -> Unit)? = null
        var viewsClick = HashMap<Int, ((item: T) -> Unit)>()
        private var itemsSize: Int? = null
        private var itemProvider: ((Int) -> T)? = null

        fun withItemProvider(itemsSize: Int, itemProvider: (Int) -> T) {
            this.itemProvider = itemProvider
            this.itemsSize = itemsSize
        }

        private fun createViewBinding(inf: LayoutInflater, vg: ViewGroup): V =
            DataBindingUtil.bind(getView(inf, vg))!!

        private fun getView(inf: LayoutInflater, vg: ViewGroup) = inf
            .inflate(layoutId!!, vg, false)

        fun build() = object : AppBaseAdapter<T, ViewDataBinding>(initItems) {

            override fun getViewBinding(
                inflater: LayoutInflater,
                viewGroup: ViewGroup,
                viewType: Int
            ): V = viewBinding
                ?.invoke(inflater, viewGroup, viewType)
                ?: createViewBinding(inflater, viewGroup)

            override fun getItemCount() = itemsSize
                ?: super.items.size

            fun provideItem(position: Int) = itemProvider
                ?.invoke(position)
                ?: items[position]

            override fun onBindViewHolder(holder: BaseItem<T, out ViewDataBinding>, position: Int) {
                provideItem(position).let { item ->
                    holder.setVariable(item)
                    holder.binding.root.setOnClickListener {
                        onItemClick?.invoke(item)
                        onIndexedItemClick?.invoke(position)
                    }
                    holder.bind(item)
                    setViewsClick(holder, item)
                    onBind?.invoke(item, holder.binding as V)
                }
            }

            private fun setViewsClick(holder: BaseItem<T, out ViewDataBinding>, item: T) {
                viewsClick.forEach { (key, value) ->
                    holder.binding.root
                        .findViewById<View?>(key)
                        ?.setOnClickListener { value.invoke(item) }
                }
            }

            override fun getItemViewType(position: Int) = itemViewTypeProvider
                ?.invoke(getData()[position])
                ?: super.getItemViewType(position)
        }
    }
}

inline fun <T, V : ViewDataBinding> createAdapter(
    layout: Int? = null,
    body: AppBaseAdapter.Builder<T, V>.() -> Unit
): AppBaseAdapter<T, ViewDataBinding> {
    val builder = AppBaseAdapter.Builder<T, V>(layout)
    builder.body()
    return builder.build()
}

inline fun <T> test(
    layout: Int? = null,
    body: AppBaseAdapter.Builder<T, ViewDataBinding>.() -> Unit
): AppBaseAdapter<T, ViewDataBinding> {
    val builder = AppBaseAdapter.Builder<T, ViewDataBinding>(layout)
    builder.body()
    return builder.build()
}
