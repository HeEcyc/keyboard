package com.live.keyboard.ui.base

import android.content.Context
import androidx.databinding.Observable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.live.keyboard.ui.dialogs.DialogChooser
import com.live.keyboard.util.addOnPropertyChangedCallback


abstract class BaseViewModel : ViewModel() {

    val newChooserDialogs = MutableLiveData<DialogChooser<*>>()

    private val observablesAndObservers = mutableListOf<Pair<Observable, Observable.OnPropertyChangedCallback>>()

    protected fun <T> showChooserWithCallback(
        titleRes: Int,
        items: Array<T>,
        selectedItem: T,
        toStringWithContext: (T, Context) -> String = { item, _ -> item.toString() },
        onItemSelected: (T) -> Unit
    ) = newChooserDialogs.postValue(DialogChooser(titleRes, items, selectedItem, toStringWithContext, onItemSelected))

    protected fun observe(observable: Observable, observer: (Observable, Int) -> Unit) =
        observablesAndObservers.add(observable to observable.addOnPropertyChangedCallback(observer))

    override fun onCleared() {
        observablesAndObservers.forEach { (observable, observer) ->
            observable.removeOnPropertyChangedCallback(observer)
        }
        observablesAndObservers.clear()
        super.onCleared()
    }

}
