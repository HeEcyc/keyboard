package dev.patrickgold.florisboard.util

enum class Language {
    EN, RU, FR, DE, IT, ES, PT;

    companion object {
        fun from(code: String) = valueOf(code.uppercase())
    }

    val dictionaryJSONAsset by lazy {
        when (this) {
            EN -> "ime/dict/data.json"
            RU -> "ime/dict/ru_wordlist.json"
            ES -> "ime/dict/de_wordlist.json"
            FR -> "ime/dict/es_wordlist.json"
            IT -> "ime/dict/fr_wordlist.json"
            PT -> "ime/dict/it_wordlist.json"
            DE -> "ime/dict/pt_PT_wordlist.json"
        }
    }

}
