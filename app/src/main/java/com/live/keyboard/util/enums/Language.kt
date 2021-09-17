package com.live.keyboard.util.enums

import androidx.databinding.ObservableBoolean
import com.live.keyboard.repository.PrefsReporitory
import com.live.keyboard.util.fileExists

enum class Language(val languageName: String, val weightMB: Int = 0) {
    DE("Deutsch", 11),
    EN("English"),
    ES("Español", 5),
    FR("Français", 4),
    IT("Italiano", 3),
    PT("Português", 4),
    RU("Русский", 6);

    var isSelected: Boolean by PrefsReporitory.Settings.Language
    val isDownloaded: Boolean get() = this == EN || dictionaryJSONFile.fileExists()
    val isDownloadedObservable by lazy { ObservableBoolean(isDownloaded) }
    val isDownloading = ObservableBoolean(false)

    companion object {

        init {
            values().forEach {
                if (it.isSelected && !it.isDownloaded) it.isSelected = false
            }
        }

        fun from(code: String) = valueOf(code.uppercase())
    }

    val dictionaryJSONFile by lazy {
        when (this) {
            EN -> "ime/dict/data.json"
            RU -> "ru_wordlist.json"
            ES -> "es_wordlist.json"
            FR -> "fr_wordlist.json"
            IT -> "it_wordlist.json"
            PT -> "pt_PT_wordlist.json"
            DE -> "de_wordlist.json"
        }
    }

}
