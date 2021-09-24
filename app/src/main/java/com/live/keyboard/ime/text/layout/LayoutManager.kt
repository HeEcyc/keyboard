package com.live.keyboard.ime.text.layout

import com.live.keyboard.background.view.keyboard.repository.BottomRightCharacterRepository
import com.live.keyboard.ime.core.Preferences
import com.live.keyboard.ime.core.Subtype
import com.live.keyboard.ime.keyboard.AbstractKeyData
import com.live.keyboard.ime.keyboard.DefaultComputingEvaluator
import com.live.keyboard.res.AssetManager
import com.live.keyboard.ime.popup.PopupExtension
import com.live.keyboard.ime.popup.PopupManager
import com.live.keyboard.ime.popup.PopupSet
import com.live.keyboard.ime.text.key.*
import com.live.keyboard.ime.text.keyboard.*
import com.live.keyboard.res.FlorisRef
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

private data class LTN(
    val type: LayoutType,
    val name: String
)

/**
 * Class which manages layout loading and caching.
 */
class LayoutManager {
    private val assetManager get() = AssetManager.default()
    private val prefs get() = Preferences.default()

    private val layoutCache: HashMap<String, Deferred<Result<Layout>>> = hashMapOf()
    private val layoutCacheGuard: Mutex = Mutex(locked = false)
    private val extendedPopupsCache: HashMap<String, Deferred<Result<PopupExtension>>> = hashMapOf()
    private val extendedPopupsCacheGuard: Mutex = Mutex(locked = false)
    private val ioScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val indexedLayoutRefs: EnumMap<LayoutType, MutableList<Pair<FlorisRef, LayoutMetaOnly>>> = EnumMap(LayoutType::class.java)

    init {
        for (type in LayoutType.values()) {
            indexedLayoutRefs[type] = mutableListOf()
        }
        indexLayoutRefs()
    }

    /**
     * Loads the layout for the specified type and name.
     *
     * @return the [Layout] or null.
     */
    private fun loadLayoutAsync(ltn: LTN?): Deferred<Result<Layout>> = ioScope.async {
        if (ltn == null) {
            return@async Result.failure(NullPointerException("Invalid argument value for 'ltn': null"))
        }
        val ref = FlorisRef.assets("ime/text/${ltn.type}/${ltn.name}.json")
        layoutCacheGuard.lock()
        val cached = layoutCache[ref.relativePath]
        if (cached != null) {
            layoutCacheGuard.unlock()
            return@async cached.await()
        } else {
            val layout = async { assetManager.loadJsonAsset<Layout>(ref) }
            layoutCache[ref.relativePath] = layout
            layoutCacheGuard.unlock()
            return@async layout.await()
        }
    }

    private fun loadExtendedPopupsAsync(subtype: Subtype? = null): Deferred<Result<PopupExtension>> = ioScope.async {
        val ref: FlorisRef = if (subtype == null) {
            FlorisRef.assets("${PopupManager.POPUP_EXTENSION_PATH_REL}/\$default.json")
        } else {
            val tempRef = FlorisRef.assets("${PopupManager.POPUP_EXTENSION_PATH_REL}/${subtype.locale.languageTag()}.json")
            if (assetManager.hasAsset(tempRef)) {
                tempRef
            } else {
                FlorisRef.assets("${PopupManager.POPUP_EXTENSION_PATH_REL}/${subtype.locale.language}.json")
            }
        }
        extendedPopupsCacheGuard.lock()
        val cached = extendedPopupsCache[ref.relativePath]
        if (cached != null) {
            extendedPopupsCacheGuard.unlock()
            return@async filterExtendedPopupsOnlyForSpecialKeys(cached.await())
        } else {
            val extendedPopups = async { assetManager.loadJsonAsset<PopupExtension>(ref) }
            extendedPopupsCache[ref.relativePath] = extendedPopups
            extendedPopupsCacheGuard.unlock()
            return@async filterExtendedPopupsOnlyForSpecialKeys(extendedPopups.await())
        }
    }

    private fun filterExtendedPopupsOnlyForSpecialKeys(popups: Result<PopupExtension>): Result<PopupExtension> =
        if (popups.isFailure)
            popups
        else {
            val popupsValue = popups.getOrNull()!!
            val filteredMapping = mutableMapOf<KeyVariation, Map<String, PopupSet<AbstractKeyData>>>()
            popupsValue.mapping.entries.forEach { entry ->
                filteredMapping[entry.key] = entry.value.filterKeys { it.startsWith('~') }.mapValues {
                    val needToChangePopupSet = !BottomRightCharacterRepository.isDefaultBottomRightCharacter && it.key == "~right"
                    if (needToChangePopupSet)
                        BottomRightCharacterRepository.getCorrectPopupSet()
                    else
                        it.value
                }
            }
            Result.success(PopupExtension(
                popupsValue.name,
                popupsValue.label,
                popupsValue.authors,
                filteredMapping.toMap()
            ))
        }

    /**
     * Merges the specified layouts (LTNs) and returns the computed layout.
     * The computed layout may looks like this:
     *   e e e e e e e e e e      e = extension
     *   c c c c c c c c c c      c = main
     *    c c c c c c c c c       m = mod
     *   m c c c c c c c c m
     *   m m m m m m m m m m
     *
     * @param keyboardMode The keyboard mode for the returning [TextKeyboard].
     * @param subtype The subtype used for populating the extended popups.
     * @param main The main layout type and name.
     * @param modifier The modifier (mod) layout type and name.
     * @param extension The extension layout type and name.
     * @return a [TextKeyboard] object, regardless of the specified LTNs or errors.
     */
    private suspend fun mergeLayoutsAsync(
        keyboardMode: KeyboardMode,
        subtype: Subtype,
        main: LTN? = null,
        modifier: LTN? = null,
        extension: LTN? = null
    ): TextKeyboard {
        val extendedPopupsDefault = loadExtendedPopupsAsync()
        val extendedPopups = loadExtendedPopupsAsync(subtype)

        val mainLayout = loadLayoutAsync(main).await().getOrNull()
        val modifierToLoad = if (mainLayout?.modifier != null) {
            val layoutType = when (mainLayout.type) {
                LayoutType.SYMBOLS -> {
                    LayoutType.SYMBOLS_MOD
                }
                LayoutType.SYMBOLS2 -> {
                    LayoutType.SYMBOLS2_MOD
                }
                else -> {
                    LayoutType.CHARACTERS_MOD
                }
            }
            LTN(layoutType, mainLayout.modifier)
        } else {
            modifier
        }
        val modifierLayout = loadLayoutAsync(modifierToLoad).await().getOrNull()?.let { layout ->
            val needToChangeCharacters = !BottomRightCharacterRepository.isDefaultBottomRightCharacter && layout.type == LayoutType.CHARACTERS_MOD
            layout.takeIf { !needToChangeCharacters } ?: Layout(
                layout.type,
                layout.name,
                layout.label,
                layout.authors,
                layout.direction,
                layout.modifier,
                listOf(
                    layout.arrangement[0],
                    layout.arrangement[1].map { abstractKeyData ->
                        if (abstractKeyData is TextKeyData && abstractKeyData.code == 46) {
                            TextKeyData(
                                abstractKeyData.type,
                                BottomRightCharacterRepository.selectedBottomRightCharacterCode,
                                BottomRightCharacterRepository.selectedBottomRightCharacterLabel,
                                abstractKeyData.groupId,
                                abstractKeyData.popup
                            )
                        } else abstractKeyData
                    }
                )
            )
        }
        val extensionLayout = loadLayoutAsync(extension).await().getOrNull()

        val computedArrangement: ArrayList<Array<TextKey>> = arrayListOf()

        if (extensionLayout != null) {
            for (row in extensionLayout.arrangement) {
                val rowArray = Array(row.size) { TextKey(row[it]) }
                computedArrangement.add(rowArray)
            }
        }

        if (mainLayout != null && modifierLayout != null) {
            for (mainRowI in mainLayout.arrangement.indices) {
                val mainRow = mainLayout.arrangement[mainRowI]
                if (mainRowI + 1 < mainLayout.arrangement.size) {
                    val rowArray = Array(mainRow.size) { TextKey(mainRow[it]) }
                    computedArrangement.add(rowArray)
                } else {
                    // merge main and mod here
                    val rowArray = arrayListOf<TextKey>()
                    val firstModRow = modifierLayout.arrangement.firstOrNull()
                    for (modKey in (firstModRow ?: listOf())) {
                        if (modKey is TextKeyData && modKey.code == 0) {
                            rowArray.addAll(mainRow.map { TextKey(it) })
                        } else {
                            rowArray.add(TextKey(modKey))
                        }
                    }
                    val temp = Array(rowArray.size) { rowArray[it] }
                    computedArrangement.add(temp)
                }
            }
            for (modRowI in 1 until modifierLayout.arrangement.size) {
                val modRow = modifierLayout.arrangement[modRowI]
                val rowArray = Array(modRow.size) { TextKey(modRow[it]) }
                computedArrangement.add(rowArray)
            }
        } else if (mainLayout != null && modifierLayout == null) {
            for (mainRow in mainLayout.arrangement) {
                val rowArray = Array(mainRow.size) { TextKey(mainRow[it]) }
                computedArrangement.add(rowArray)
            }
        } else if (mainLayout == null && modifierLayout != null) {
            for (modRow in modifierLayout.arrangement) {
                val rowArray = Array(modRow.size) { TextKey(modRow[it]) }
                computedArrangement.add(rowArray)
            }
        }

        // Add hints to keys
        if (keyboardMode == KeyboardMode.CHARACTERS) {
            val symbolsComputedArrangement = computeKeyboardAsync(KeyboardMode.SYMBOLS, subtype).await().arrangement
            // number row hint always happens on first row
            if (prefs.keyboard.hintedNumberRowMode != KeyHintMode.DISABLED) {
                val row = computedArrangement[0]
                val symbolRow = symbolsComputedArrangement[0]
                addRowHints(row, symbolRow, KeyType.NUMERIC)
            }
            // all other symbols are added bottom-aligned
            val rOffset = computedArrangement.size - symbolsComputedArrangement.size
            for ((r, row) in computedArrangement.withIndex()) {
                if (r < rOffset) {
                    continue
                }
                val symbolRow = symbolsComputedArrangement.getOrNull(r - rOffset)
                if (symbolRow != null) {
                    addRowHints(row, symbolRow, KeyType.CHARACTER)
                }
            }
        }

        val array = Array(computedArrangement.size) { computedArrangement[it] }
        return TextKeyboard(
            arrangement = array,
            mode = keyboardMode,
            extendedPopupMapping = extendedPopups.await().onFailure {
            }.getOrNull()?.mapping,
            extendedPopupMappingDefault = extendedPopupsDefault.await().onFailure {
            }.getOrNull()?.mapping
        )
    }

    private fun addRowHints(main: Array<TextKey>, hint: Array<TextKey>, hintType: KeyType) {
        for ((k,key) in main.withIndex()) {
            val hintKey = hint.getOrNull(k)?.data?.compute(DefaultComputingEvaluator)
            if (hintKey?.type != hintType) {
                continue
            }

            when (hintType) {
                KeyType.CHARACTER -> {
                    key.computedSymbolHint = hintKey
                }
                KeyType.NUMERIC -> {
                    key.computedNumberHint = hintKey
                }
                else -> {
                    // do nothing
                }
            }
        }
    }

    /**
     * Computes a layout for [keyboardMode] based on the given [subtype] and returns it.
     *
     * @param keyboardMode The keyboard mode for which the layout should be computed.
     * @param subtype The subtype which localizes the computed layout.
     */
    fun computeKeyboardAsync(
        keyboardMode: KeyboardMode,
        subtype: Subtype
    ): Deferred<TextKeyboard> = ioScope.async {
        var main: LTN? = null
        var modifier: LTN? = null
        var extension: LTN? = null

        when (keyboardMode) {
            KeyboardMode.CHARACTERS -> {
                if (prefs.keyboard.numberRow) {
                    extension = LTN(LayoutType.NUMERIC_ROW, subtype.layoutMap.numericRow)
                }
                main = LTN(LayoutType.CHARACTERS, subtype.layoutMap.characters)
                modifier = LTN(LayoutType.CHARACTERS_MOD, "\$default")
            }
            KeyboardMode.EDITING -> {
                // Layout for this mode is defined in custom layout xml file.
                return@async TextKeyboard(arrayOf(), keyboardMode, null, null)
            }
            KeyboardMode.NUMERIC -> {
                main = LTN(LayoutType.NUMERIC, subtype.layoutMap.numeric)
            }
            KeyboardMode.NUMERIC_ADVANCED -> {
                main = LTN(LayoutType.NUMERIC_ADVANCED, subtype.layoutMap.numericAdvanced)
            }
            KeyboardMode.PHONE -> {
                main = LTN(LayoutType.PHONE, subtype.layoutMap.phone)
            }
            KeyboardMode.PHONE2 -> {
                main = LTN(LayoutType.PHONE2, subtype.layoutMap.phone2)
            }
            KeyboardMode.SYMBOLS -> {
                extension = LTN(LayoutType.NUMERIC_ROW, subtype.layoutMap.numericRow)
                main = LTN(LayoutType.SYMBOLS, subtype.layoutMap.symbols)
                modifier = LTN(LayoutType.SYMBOLS_MOD, "\$default")
            }
            KeyboardMode.SYMBOLS2 -> {
                main = LTN(LayoutType.SYMBOLS2, subtype.layoutMap.symbols2)
                modifier = LTN(LayoutType.SYMBOLS2_MOD, "\$default")
            }
            KeyboardMode.SMARTBAR_CLIPBOARD_CURSOR_ROW -> {
                extension = LTN(LayoutType.EXTENSION, "clipboard_cursor_row")
            }
            KeyboardMode.SMARTBAR_NUMBER_ROW -> {
                extension = LTN(LayoutType.NUMERIC_ROW, subtype.layoutMap.numericRow)
            }
        }

        return@async mergeLayoutsAsync(keyboardMode, subtype, main, modifier, extension)
    }

    /**
     * Called when the application is destroyed. Used to cancel any pending coroutines.
     */
    fun onDestroy() {
        ioScope.cancel()
    }

    fun getMetaFor(type: LayoutType, name: String): LayoutMetaOnly? {
        return indexedLayoutRefs[type]?.firstOrNull { it.second.name == name }?.second
    }

    fun getMetaNameListFor(type: LayoutType): List<String> {
        return indexedLayoutRefs[type]?.map { it.second.name } ?: listOf()
    }

    fun getMetaLabelListFor(type: LayoutType): List<String> {
        return indexedLayoutRefs[type]?.map { it.second.label } ?: listOf()
    }

    private fun indexLayoutRefs() {
        for (type in LayoutType.values()) {
            indexedLayoutRefs[type]?.clear()
            assetManager.listAssets<LayoutMetaOnly>(
                FlorisRef.assets("ime/text/$type")
            ).onSuccess { assetList ->
                indexedLayoutRefs[type]?.let { indexedList ->
                    for ((ref, layoutMeta) in assetList) {
                        indexedList.add(Pair(ref, layoutMeta))
                    }
                    indexedList.sortBy { it.second.name }
                    if (type == LayoutType.CHARACTERS) {
                        // Move selected layouts to the top of the list
                        for (layoutName in listOf("azerty", "qwertz", "qwerty")) {
                            val index: Int = indexedList.indexOfFirst { it.second.name == layoutName }
                            if (index > 0) {
                                indexedList.add(0, indexedList.removeAt(index))
                            }
                        }
                    }
                }
            }
        }
    }
}
