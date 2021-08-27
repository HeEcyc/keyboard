package dev.patrickgold.florisboard.background.view.keyboard.repository

import androidx.annotation.FontRes
import androidx.lifecycle.MutableLiveData

object KeysRepository {
    private val prefsRepository = PrefsReporitory

    val fontFamilyRes = MutableLiveData(prefsRepository.fontFamilyRes)

    fun setFont(@FontRes fontRes: Int) {
        prefsRepository.fontFamilyRes = fontRes
        fontFamilyRes.postValue(fontRes)
    }
}
