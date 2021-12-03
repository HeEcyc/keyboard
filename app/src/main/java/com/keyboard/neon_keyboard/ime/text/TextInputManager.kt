package com.keyboard.neon_keyboard.ime.text

import android.util.Log
import android.view.KeyEvent
import android.widget.Toast
import androidx.core.text.isDigitsOnly
import com.keyboard.neon_keyboard.BuildConfig
import com.keyboard.neon_keyboard.R
import com.keyboard.neon_keyboard.databinding.FlorisboardBinding
import com.keyboard.neon_keyboard.ime.clip.provider.ClipboardItem
import com.keyboard.neon_keyboard.ime.core.EditorInstance
import com.keyboard.neon_keyboard.ime.core.FlorisBoard
import com.keyboard.neon_keyboard.ime.core.InputEventDispatcher
import com.keyboard.neon_keyboard.ime.core.InputKeyEvent
import com.keyboard.neon_keyboard.ime.core.InputKeyEventReceiver
import com.keyboard.neon_keyboard.ime.core.Preferences
import com.keyboard.neon_keyboard.ime.core.Subtype
import com.keyboard.neon_keyboard.ime.core.TextProcessor
import com.keyboard.neon_keyboard.ime.dictionary.DictionaryManager
import com.keyboard.neon_keyboard.ime.keyboard.ComputingEvaluator
import com.keyboard.neon_keyboard.ime.keyboard.ImeOptions
import com.keyboard.neon_keyboard.ime.keyboard.InputAttributes
import com.keyboard.neon_keyboard.ime.keyboard.KeyData
import com.keyboard.neon_keyboard.ime.keyboard.Keyboard
import com.keyboard.neon_keyboard.ime.keyboard.KeyboardState
import com.keyboard.neon_keyboard.ime.keyboard.updateKeyboardState
import com.keyboard.neon_keyboard.ime.text.gestures.GlideTypingManager
import com.keyboard.neon_keyboard.ime.text.gestures.SwipeAction
import com.keyboard.neon_keyboard.ime.text.key.CurrencySet
import com.keyboard.neon_keyboard.ime.text.key.KeyCode
import com.keyboard.neon_keyboard.ime.text.key.KeyType
import com.keyboard.neon_keyboard.ime.text.key.KeyVariation
import com.keyboard.neon_keyboard.ime.text.key.UtilityKeyAction
import com.keyboard.neon_keyboard.ime.text.keyboard.KeyboardMode
import com.keyboard.neon_keyboard.ime.text.keyboard.TextKeyData
import com.keyboard.neon_keyboard.ime.text.keyboard.TextKeyboardCache
import com.keyboard.neon_keyboard.ime.text.keyboard.TextKeyboardIconSet
import com.keyboard.neon_keyboard.ime.text.keyboard.TextKeyboardView
import com.keyboard.neon_keyboard.ime.text.layout.LayoutManager
import com.keyboard.neon_keyboard.ime.text.smartbar.SmartbarView
import com.keyboard.neon_keyboard.repository.PrefsReporitory
import com.keyboard.neon_keyboard.res.AssetManager
import com.keyboard.neon_keyboard.res.FlorisRef
import com.keyboard.neon_keyboard.util.enums.Language
import com.keyboard.neon_keyboard.util.enums.LanguageChange
import com.keyboard.neon_keyboard.util.isPasswordInputType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray

/**
 * TextInputManager is responsible for managing everything which is related to text input. All of
 * the following count as text input: character, numeric (+advanced), phone and symbol layouts.
 *
 * All of the UI for the different keyboard layouts are kept under the same container element and
 * are separated from media-related UI. The core [FlorisBoard] will pass any event defined in
 * [FlorisBoard.EventListener] through to this class.
 *
 * TextInputManager is also the hub in the communication between the system, the active editor
 * instance and the Smartbar.
 */
class TextInputManager private constructor() : CoroutineScope by MainScope(), InputKeyEventReceiver,
    FlorisBoard.EventListener, SmartbarView.EventListener, EditorInstance.WordHistoryChangedListener {

    var isGlidePostEffect: Boolean = false
    private val florisboard get() = FlorisBoard.getInstance()
    private val prefs get() = Preferences.default()
    val symbolsWithSpaceAfter: List<String>
    private val activeEditorInstance: EditorInstance
        get() = florisboard.activeEditorInstance

    lateinit var layoutManager: LayoutManager
        private set
    val keyboards = TextKeyboardCache()
    var textInputKeyboardView: TextKeyboardView? = null
    var textKeyboardIconSet: TextKeyboardIconSet = TextKeyboardIconSet.new(FlorisBoard.getInstance())
        private set
    private val dictionaryManager: DictionaryManager get() = DictionaryManager.default()
    val inputEventDispatcher: InputEventDispatcher = InputEventDispatcher.new(
        repeatableKeyCodes = intArrayOf(
            KeyCode.ARROW_DOWN,
            KeyCode.ARROW_LEFT,
            KeyCode.ARROW_RIGHT,
            KeyCode.ARROW_UP,
            KeyCode.DELETE,
            KeyCode.FORWARD_DELETE
        )
    )

    internal var smartbarView: SmartbarView? = null

    val activeState: KeyboardState get() = florisboard.activeState
    private var newCapsState: Boolean = false
    private var isNumberRowVisible: Boolean = false

    // Composing text related properties
    var isManualSelectionMode: Boolean = false
    private var isManualSelectionModeStart: Boolean = false
    private var isManualSelectionModeEnd: Boolean = false

    companion object {
        private var instance: TextInputManager? = null

        @Synchronized
        fun getInstance(): TextInputManager {
            if (instance == null) {
                instance = TextInputManager()
            }
            return instance!!
        }
    }

    init {
        florisboard.addEventListener(this)
        val data =
            AssetManager.default().loadTextAsset(FlorisRef.assets("ime/text/symbols-with-space.json")).getOrThrow()
        val json = JSONArray(data)
        this.symbolsWithSpaceAfter = List(json.length()) { json.getString(it) }
    }

    val evaluator = object : ComputingEvaluator {
        override fun evaluateCaps(): Boolean {
            return activeState.caps || activeState.capsLock
        }

        override fun evaluateCaps(data: KeyData): Boolean {
            return evaluateCaps() && data.code >= KeyCode.SPACE
        }

        override fun evaluateCharHalfWidth(): Boolean = activeState.isCharHalfWidth

        override fun evaluateKanaKata(): Boolean = activeState.isKanaKata

        override fun evaluateKanaSmall(): Boolean = activeState.isKanaSmall

        override fun evaluateEnabled(data: KeyData): Boolean {
            return when (data.code) {
                KeyCode.CLIPBOARD_COPY,
                KeyCode.CLIPBOARD_CUT -> {
                    activeState.isSelectionMode && activeState.isRichInputEditor
                }
                KeyCode.CLIPBOARD_PASTE -> {
                    florisboard.florisClipboardManager?.canBePasted(
                        florisboard.florisClipboardManager?.primaryClip
                    ) == true
                }
                KeyCode.CLIPBOARD_SELECT_ALL -> {
                    activeState.isRichInputEditor
                }
                KeyCode.SWITCH_TO_CLIPBOARD_CONTEXT -> {
                    prefs.clipboard.enableHistory
                }
                else -> true
            }
        }

        override fun evaluateVisible(data: KeyData): Boolean {
            return when (data.code) {
                KeyCode.SWITCH_TO_TEXT_CONTEXT,
                KeyCode.PHONE_PAUSE ->
                    if (data.groupId == 2) true
                    else !(PrefsReporitory.Settings.showEmoji && needToShowLanguageKey())
                KeyCode.LANGUAGE_SWITCH -> needToShowLanguageKey()
                KeyCode.SWITCH_TO_MEDIA_CONTEXT -> PrefsReporitory.Settings.showEmoji
                else -> true
            }
        }

        private fun needToShowLanguageKey() = PrefsReporitory.Settings.languageChange == LanguageChange.SPECIAL_BUTTON
            && Language.values().count { it.isSelected } > 1

        override fun getActiveSubtype(): Subtype {
            return florisboard.activeSubtype
        }

        override fun getKeyVariation(): KeyVariation {
            return activeState.keyVariation
        }

        override fun getKeyboard(): Keyboard {
            throw NotImplementedError() // Correct value must be inserted by the TextKeyboardView
        }

        override fun isSlot(data: KeyData): Boolean {
            return CurrencySet.isCurrencySlot(data.code)
        }

        override fun getSlotData(data: KeyData): KeyData? {
            return florisboard.subtypeManager.getCurrencySet(getActiveSubtype()).getSlot(data.code)
        }
    }

    /**
     * Non-UI-related setup + preloading of all required computed layouts (asynchronous in the
     * background).
     */
    override fun onCreate() {
        layoutManager = LayoutManager()
        textKeyboardIconSet = TextKeyboardIconSet.new(florisboard)
        inputEventDispatcher.keyEventReceiver = this
        isNumberRowVisible = prefs.keyboard.numberRow
        activeEditorInstance.wordHistoryChangedListener = this
        var subtypes = florisboard.subtypeManager.subtypes
        if (subtypes.isEmpty()) {
            subtypes = listOf(Subtype.DEFAULT)
        }
        for (subtype in subtypes) {
            for (mode in KeyboardMode.values()) {
                keyboards.set(mode, subtype, keyboard = layoutManager.computeKeyboardAsync(mode, subtype))
            }
        }
    }

    override fun onInitializeInputUi(uiBinding: FlorisboardBinding) {
        textInputKeyboardView = uiBinding.text.mainKeyboardView.also {
            it.setIconSet(textKeyboardIconSet)
            it.setComputingEvaluator(evaluator)
            it.sync()
        }

        smartbarView = uiBinding.text.smartbar.root.also {
            it.setEventListener(this)
            it.sync()
        }

        setActiveKeyboardMode(getActiveKeyboardMode(), updateState = false)
    }

    /**
     * Cancels all coroutines and cleans up.
     */
    override fun onDestroy() {
        smartbarView?.setEventListener(null)
        smartbarView = null

        textInputKeyboardView?.setComputingEvaluator(null)
        textInputKeyboardView = null
        keyboards.clear()

        inputEventDispatcher.keyEventReceiver = null
        inputEventDispatcher.close()

        dictionaryManager.unloadUserDictionariesIfNecessary()

        activeEditorInstance.wordHistoryChangedListener = null

        cancel()
        layoutManager.onDestroy()
        instance = null
    }

    /**
     * Evaluates the [KeyboardState.keyboardMode], [KeyboardState.keyVariation] and [KeyboardState.isComposingEnabled]
     * property values when starting to interact with a input editor. Also resets the composing
     * texts and sets the initial caps mode accordingly.
     */
    override fun onStartInputView(instance: EditorInstance, restarting: Boolean) {
        val keyboardMode = when (activeState.inputAttributes.type) {
            InputAttributes.Type.NUMBER -> {
                activeState.keyVariation = KeyVariation.NORMAL
                KeyboardMode.NUMERIC
            }
            InputAttributes.Type.PHONE -> {
                activeState.keyVariation = KeyVariation.NORMAL
                KeyboardMode.PHONE
            }
            InputAttributes.Type.TEXT -> {
                activeState.keyVariation = when (activeState.inputAttributes.variation) {
                    InputAttributes.Variation.EMAIL_ADDRESS,
                    InputAttributes.Variation.WEB_EMAIL_ADDRESS -> {
                        KeyVariation.EMAIL_ADDRESS
                    }
                    InputAttributes.Variation.PASSWORD,
                    InputAttributes.Variation.VISIBLE_PASSWORD,
                    InputAttributes.Variation.WEB_PASSWORD -> {
                        KeyVariation.PASSWORD
                    }
                    InputAttributes.Variation.URI -> {
                        KeyVariation.URI
                    }
                    else -> {
                        KeyVariation.NORMAL
                    }
                }
                KeyboardMode.CHARACTERS
            }
            else -> {
                activeState.keyVariation = KeyVariation.NORMAL
                KeyboardMode.CHARACTERS
            }
        }
        activeState.isComposingEnabled = when (keyboardMode) {
            KeyboardMode.NUMERIC,
            KeyboardMode.PHONE,
            KeyboardMode.PHONE2 -> false
            else -> activeState.keyVariation != KeyVariation.PASSWORD && prefs.suggestion.enabled
        }
        val newIsNumberRowVisible = prefs.keyboard.numberRow
        if (isNumberRowVisible != newIsNumberRowVisible) {
            keyboards.clear(KeyboardMode.CHARACTERS)
            isNumberRowVisible = newIsNumberRowVisible
        }
        setActiveKeyboardMode(keyboardMode, updateState = false)
        instance.composingEnabledChanged()
        activeState.isPrivateMode = prefs.advanced.forcePrivateMode ||
            activeState.imeOptions.flagNoPersonalizedLearning
        if (!prefs.correction.rememberCapsLockState) {
            activeState.capsLock = false
        }
        isGlidePostEffect = false
        updateCapsState()
        smartbarView?.setCandidateSuggestionWords(System.nanoTime(), null)
    }

    override fun onWindowShown() {
        launch(Dispatchers.Default) {
            dictionaryManager.loadUserDictionariesIfNecessary()
        }
        textInputKeyboardView?.sync()
        smartbarView?.sync()
    }

    /**
     * Gets the active keyboard mode.
     *
     * @return The active keyboard mode.
     */
    fun getActiveKeyboardMode(): KeyboardMode {
        return activeState.keyboardMode
    }

    /**
     * Sets the active keyboard mode and updates the [KeyboardState.isQuickActionsVisible] state.
     */
    private fun setActiveKeyboardMode(mode: KeyboardMode, updateState: Boolean = true) {
        activeState.keyboardMode = mode
        isManualSelectionMode = false
        isManualSelectionModeStart = false
        isManualSelectionModeEnd = false
        activeState.isQuickActionsVisible = false
        setActiveKeyboard(mode, florisboard.activeSubtype, updateState)
    }

    private fun setActiveKeyboard(mode: KeyboardMode, subtype: Subtype, updateState: Boolean = true) =
        launch(Dispatchers.IO) {
            val activeKeyboard = keyboards.getOrElseAsync(mode, subtype) {
                layoutManager.computeKeyboardAsync(
                    keyboardMode = mode,
                    subtype = subtype
                ).await()
            }.await()
            withContext(Dispatchers.Main) {
                textInputKeyboardView?.setComputedKeyboard(activeKeyboard, PrefsReporitory.keyboardTheme)
                if (updateState) {
                    florisboard.dispatchCurrentStateToInputUi()
                }
            }
        }

    override fun onSubtypeChanged(newSubtype: Subtype, doRefreshLayouts: Boolean) {
        launch {
            if (activeState.isComposingEnabled) {
                launch(Dispatchers.IO) {
                    dictionaryManager.prepareDictionaries(newSubtype)
                }
            }
            if (prefs.glide.enabled) {
                GlideTypingManager.getInstance().setWordData(newSubtype)
            }
            if (doRefreshLayouts) {
                setActiveKeyboard(getActiveKeyboardMode(), newSubtype)
            }
        }
        isGlidePostEffect = false
    }

    /**
     * Main logic point for processing cursor updates as well as parsing the current composing word
     * and passing this info on to the [SmartbarView] to turn it into candidate suggestions.
     */
    override fun onUpdateSelection() {
        if (!inputEventDispatcher.isPressed(KeyCode.SHIFT)) {
            updateCapsState()
        }
    }

    override fun onWordHistoryChanged(
        currentWord: EditorInstance.Region?,
        wordsBeforeCurrent: List<EditorInstance.Region>,
        wordsAfterCurrent: List<EditorInstance.Region>
    ) {
        if (currentWord == null || !currentWord.isValid) {
            smartbarView?.setCandidateSuggestionWords(System.nanoTime(), null)
            return
        }
        if (activeState.isComposingEnabled && !inputEventDispatcher.isPressed(KeyCode.DELETE) && !isGlidePostEffect) {
            launch(Dispatchers.Default) {
                val startTime = System.nanoTime()
                dictionaryManager.suggest(
                    currentWord = currentWord.text,
                    preceidingWords = listOf(),
                    subtype = florisboard.activeSubtype,
                    allowPossiblyOffensive = !prefs.suggestion.blockPossiblyOffensive,
                    maxSuggestionCount = 16
                ) { suggestions ->
                    withContext(Dispatchers.Main) {
                        smartbarView?.setCandidateSuggestionWords(startTime, suggestions)
                        smartbarView?.updateCandidateSuggestionCapsState()
                    }
                }
                if (BuildConfig.DEBUG) {
                    val elapsed = (System.nanoTime() - startTime) / 1000.0
                }
            }
        }
    }

    override fun onPrimaryClipChanged() {
        smartbarView?.onPrimaryClipChanged()
    }

    /**
     * Updates the current caps state according to the [EditorInstance.cursorCapsMode], while
     * respecting [KeyboardState.capsLock] property and the correction.autoCapitalization preference.
     */
    private fun updateCapsState() {
        if (!activeState.capsLock) {
            activeState.caps = prefs.correction.autoCapitalization &&
                activeEditorInstance.cursorCapsMode != InputAttributes.CapsMode.NONE
        }
    }

    /**
     * Executes a given [SwipeAction]. Ignores any [SwipeAction] but the ones relevant for this
     * class.
     */
    fun executeSwipeAction(swipeAction: SwipeAction) {
        val keyData = when (swipeAction) {
            SwipeAction.CYCLE_TO_PREVIOUS_KEYBOARD_MODE -> when (getActiveKeyboardMode()) {
                KeyboardMode.CHARACTERS -> TextKeyData.VIEW_NUMERIC_ADVANCED
                KeyboardMode.NUMERIC_ADVANCED -> TextKeyData.VIEW_SYMBOLS2
                KeyboardMode.SYMBOLS2 -> TextKeyData.VIEW_SYMBOLS
                else -> TextKeyData.VIEW_CHARACTERS
            }
            SwipeAction.CYCLE_TO_NEXT_KEYBOARD_MODE -> when (getActiveKeyboardMode()) {
                KeyboardMode.CHARACTERS -> TextKeyData.VIEW_SYMBOLS
                KeyboardMode.SYMBOLS -> TextKeyData.VIEW_SYMBOLS2
                KeyboardMode.SYMBOLS2 -> TextKeyData.VIEW_NUMERIC_ADVANCED
                else -> TextKeyData.VIEW_CHARACTERS
            }
            SwipeAction.DELETE_WORD -> if (PrefsReporitory.Settings.keyboardSwipe) TextKeyData.DELETE_WORD else null
            SwipeAction.INSERT_SPACE -> if (PrefsReporitory.Settings.keyboardSwipe) TextKeyData.SPACE else null
            SwipeAction.MOVE_CURSOR_DOWN -> TextKeyData.ARROW_DOWN
            SwipeAction.MOVE_CURSOR_UP -> TextKeyData.ARROW_UP
            SwipeAction.MOVE_CURSOR_LEFT -> TextKeyData.ARROW_LEFT
            SwipeAction.MOVE_CURSOR_RIGHT -> TextKeyData.ARROW_RIGHT
            SwipeAction.MOVE_CURSOR_START_OF_LINE -> TextKeyData.MOVE_START_OF_LINE
            SwipeAction.MOVE_CURSOR_END_OF_LINE -> TextKeyData.MOVE_END_OF_LINE
            SwipeAction.MOVE_CURSOR_START_OF_PAGE -> TextKeyData.MOVE_START_OF_PAGE
            SwipeAction.MOVE_CURSOR_END_OF_PAGE -> TextKeyData.MOVE_END_OF_PAGE
            SwipeAction.SHIFT -> TextKeyData.SHIFT
            SwipeAction.REDO -> TextKeyData.REDO
            SwipeAction.UNDO -> TextKeyData.UNDO
            SwipeAction.SWITCH_TO_CLIPBOARD_CONTEXT -> TextKeyData.SWITCH_TO_CLIPBOARD_CONTEXT
            SwipeAction.SHOW_INPUT_METHOD_PICKER -> TextKeyData.SHOW_INPUT_METHOD_PICKER
            else -> null
        }

        Log.d("12345", keyData?.label ?: "Emptu")

        if (keyData != null) {
            inputEventDispatcher.send(InputKeyEvent.downUp(keyData))
        }
    }

    override fun onSmartbarBackButtonPressed() {
        florisboard.inputFeedbackManager.keyPress()
        setActiveKeyboardMode(KeyboardMode.CHARACTERS)
    }

    override fun onSmartbarCandidatePressed(word: String) {
        florisboard.inputFeedbackManager.keyPress()
        isGlidePostEffect = false
        activeEditorInstance.commitCompletion(word)
    }

    override fun onSmartbarClipboardCandidatePressed(clipboardItem: ClipboardItem) {
        florisboard.inputFeedbackManager.keyPress()
        isGlidePostEffect = false
        activeEditorInstance.commitClipboardItem(clipboardItem)
    }

    override fun onSmartbarPrivateModeButtonClicked() {
        florisboard.inputFeedbackManager.keyPress()
        Toast.makeText(florisboard, R.string.private_mode_dialog__title, Toast.LENGTH_LONG).show()
    }

    override fun onSmartbarQuickActionPressed(quickActionId: Int) {
        florisboard.inputFeedbackManager.keyPress()
        when (quickActionId) {
            R.id.quick_action_switch_to_media_context -> florisboard.setActiveInput(R.id.media_input)
            R.id.quick_action_open_settings -> florisboard.launchSettings()
            R.id.quick_action_one_handed_toggle -> florisboard.toggleOneHandedMode(isRight = true)
            R.id.quick_action_undo -> {
                inputEventDispatcher.send(InputKeyEvent.downUp(TextKeyData.UNDO))
                return
            }
            R.id.quick_action_redo -> {
                inputEventDispatcher.send(InputKeyEvent.downUp(TextKeyData.REDO))
                return
            }
        }
        activeState.isQuickActionsVisible = false
        smartbarView?.updateKeyboardState(activeState)
    }

    /**
     * Handles a [KeyCode.DELETE] event.
     */
    private fun handleDelete() {
        if (isGlidePostEffect) {
            handleDeleteWord()
            isGlidePostEffect = false
            smartbarView?.setCandidateSuggestionWords(System.nanoTime(), null)
        } else {
            isManualSelectionMode = false
            isManualSelectionModeStart = false
            isManualSelectionModeEnd = false
            activeEditorInstance.deleteBackwards()
        }
    }

    /**
     * Handles a [KeyCode.DELETE_WORD] event.
     */
    private fun handleDeleteWord() {
        isManualSelectionMode = false
        isManualSelectionModeStart = false
        isManualSelectionModeEnd = false
        isGlidePostEffect = false
        activeEditorInstance.deleteWordBackwards()
    }

    /**
     * Handles a [KeyCode.ENTER] event.
     */
    private fun handleEnter() {
        if (activeState.imeOptions.flagNoEnterAction) {
            activeEditorInstance.performEnter()
        } else {
            when (activeState.imeOptions.enterAction) {
                ImeOptions.EnterAction.DONE,
                ImeOptions.EnterAction.GO,
                ImeOptions.EnterAction.NEXT,
                ImeOptions.EnterAction.PREVIOUS,
                ImeOptions.EnterAction.SEARCH,
                ImeOptions.EnterAction.SEND -> {
                    activeEditorInstance.performEnterAction(activeState.imeOptions.enterAction)
                }
                else -> activeEditorInstance.performEnter()
            }
        }
        isGlidePostEffect = false
    }

    /**
     * Handles a [KeyCode.LANGUAGE_SWITCH] event. Also handles if the language switch should cycle
     * FlorisBoard internal or system-wide.
     */
    private fun handleLanguageSwitch() {
        when (prefs.keyboard.utilityKeyAction) {
            UtilityKeyAction.DYNAMIC_SWITCH_LANGUAGE_EMOJIS,
            UtilityKeyAction.SWITCH_LANGUAGE -> florisboard.switchToNextSubtype()
            else -> florisboard.switchToNextKeyboard()
        }
    }

    /**
     * Handles a [KeyCode.SHIFT] down event.
     */
    fun handleShiftDown(ev: InputKeyEvent) {
        if (ev.isConsecutiveEventOf(inputEventDispatcher.lastKeyEventDown, prefs.keyboard.longPressDelay.toLong())) {
            newCapsState = true
            activeState.caps = true
            activeState.capsLock = true
        } else {
            newCapsState = !activeState.caps
            activeState.caps = true
            activeState.capsLock = false
        }
        smartbarView?.updateCandidateSuggestionCapsState()
        florisboard.dispatchCurrentStateToInputUi()
    }

    /**
     * Handles a [KeyCode.SHIFT] up event.
     */
    fun handleShiftUp() {
        activeState.caps = newCapsState
        smartbarView?.updateCandidateSuggestionCapsState()
        florisboard.dispatchCurrentStateToInputUi()
    }

    /**
     * Handles a [KeyCode.SHIFT] cancel event.
     */
    private fun handleShiftCancel() {
        activeState.caps = false
        activeState.capsLock = false
        smartbarView?.updateCandidateSuggestionCapsState()
        florisboard.dispatchCurrentStateToInputUi()
    }

    /**
     * Handles a [KeyCode.SHIFT] up event.
     */
    private fun handleShiftLock() {
        val lastKeyEvent = inputEventDispatcher.lastKeyEventDown ?: return
        if (lastKeyEvent.data.code == KeyCode.SHIFT && lastKeyEvent.action == InputKeyEvent.Action.DOWN) {
            newCapsState = true
            activeState.caps = true
            activeState.capsLock = true
            smartbarView?.updateCandidateSuggestionCapsState()
            florisboard.dispatchCurrentStateToInputUi()
        }
    }

    /**
     * Handles a [KeyCode.KANA_SWITCHER] event
     */
    private fun handleKanaSwitch() {
        activeState.isKanaKata = !activeState.isKanaKata
        activeState.isCharHalfWidth = false
        florisboard.dispatchCurrentStateToInputUi()
    }

    /**
     * Handles a [KeyCode.KANA_HIRA] event
     */
    private fun handleKanaHira() {
        activeState.isKanaKata = false
        activeState.isCharHalfWidth = false
        florisboard.dispatchCurrentStateToInputUi()
    }

    /**
     * Handles a [KeyCode.KANA_KATA] event
     */
    private fun handleKanaKata() {
        activeState.isKanaKata = true
        activeState.isCharHalfWidth = false
        florisboard.dispatchCurrentStateToInputUi()
    }

    /**
     * Handles a [KeyCode.KANA_HALF_KATA] event
     */
    private fun handleKanaHalfKata() {
        activeState.isKanaKata = true
        activeState.isCharHalfWidth = true
        florisboard.dispatchCurrentStateToInputUi()
    }

    /**
     * Handles a [KeyCode.CHAR_WIDTH_SWITCHER] event
     */
    private fun handleCharWidthSwitch() {
        activeState.isCharHalfWidth = !activeState.isCharHalfWidth
        florisboard.dispatchCurrentStateToInputUi()
    }

    /**
     * Handles a [KeyCode.CHAR_WIDTH_SWITCHER] event
     */
    private fun handleCharWidthFull() {
        activeState.isCharHalfWidth = false
        florisboard.dispatchCurrentStateToInputUi()
    }

    /**
     * Handles a [KeyCode.CHAR_WIDTH_SWITCHER] event
     */
    private fun handleCharWidthHalf() {
        activeState.isCharHalfWidth = true
        florisboard.dispatchCurrentStateToInputUi()
    }

    /**
     * Handles a [KeyCode.SPACE] event. Also handles the auto-correction of two space taps if
     * enabled by the user.
     */
    private fun handleSpace(ev: InputKeyEvent) {
        if (prefs.keyboard.spaceBarSwitchesToCharacters && getActiveKeyboardMode() != KeyboardMode.CHARACTERS) {
            setActiveKeyboardMode(KeyboardMode.CHARACTERS)
        }
        if (prefs.correction.doubleSpacePeriod) {
            if (ev.isConsecutiveEventOf(inputEventDispatcher.lastKeyEventUp, prefs.keyboard.longPressDelay.toLong())) {
                val text = activeEditorInstance.getTextBeforeCursor(2)
                if (text.length == 2 && !text.matches("""[.!?‽\s][\s]""".toRegex())) {
                    activeEditorInstance.deleteBackwards()
                    activeEditorInstance.commitText(".")
                }
            }
        }
        isGlidePostEffect = false
        activeEditorInstance.commitText(KeyCode.SPACE.toChar().toString())
    }

    /**
     * Handles [KeyCode] arrow and move events, behaves differently depending on text selection.
     */
    private fun handleArrow(code: Int, count: Int) = activeEditorInstance.apply {
        val isShiftPressed = isManualSelectionMode || inputEventDispatcher.isPressed(KeyCode.SHIFT)
        when (code) {
            KeyCode.ARROW_DOWN -> {
                if (!selection.isSelectionMode && isManualSelectionMode) {
                    isManualSelectionModeStart = false
                    isManualSelectionModeEnd = true
                }
                sendDownUpKeyEvent(KeyEvent.KEYCODE_DPAD_DOWN, meta(shift = isShiftPressed), count)
            }
            KeyCode.ARROW_LEFT -> {
                if (!selection.isSelectionMode && isManualSelectionMode) {
                    isManualSelectionModeStart = true
                    isManualSelectionModeEnd = false
                }
                sendDownUpKeyEvent(KeyEvent.KEYCODE_DPAD_LEFT, meta(shift = isShiftPressed), count)
            }
            KeyCode.ARROW_RIGHT -> {
                if (!selection.isSelectionMode && isManualSelectionMode) {
                    isManualSelectionModeStart = false
                    isManualSelectionModeEnd = true
                }
                sendDownUpKeyEvent(KeyEvent.KEYCODE_DPAD_RIGHT, meta(shift = isShiftPressed), count)
            }
            KeyCode.ARROW_UP -> {
                if (!selection.isSelectionMode && isManualSelectionMode) {
                    isManualSelectionModeStart = true
                    isManualSelectionModeEnd = false
                }
                sendDownUpKeyEvent(KeyEvent.KEYCODE_DPAD_UP, meta(shift = isShiftPressed), count)
            }
            KeyCode.MOVE_START_OF_PAGE -> {
                if (!selection.isSelectionMode && isManualSelectionMode) {
                    isManualSelectionModeStart = true
                    isManualSelectionModeEnd = false
                }
                sendDownUpKeyEvent(KeyEvent.KEYCODE_DPAD_UP, meta(alt = true, shift = isShiftPressed), count)
            }
            KeyCode.MOVE_END_OF_PAGE -> {
                if (!selection.isSelectionMode && isManualSelectionMode) {
                    isManualSelectionModeStart = false
                    isManualSelectionModeEnd = true
                }
                sendDownUpKeyEvent(KeyEvent.KEYCODE_DPAD_DOWN, meta(alt = true, shift = isShiftPressed), count)
            }
            KeyCode.MOVE_START_OF_LINE -> {
                if (!selection.isSelectionMode && isManualSelectionMode) {
                    isManualSelectionModeStart = true
                    isManualSelectionModeEnd = false
                }
                sendDownUpKeyEvent(KeyEvent.KEYCODE_DPAD_LEFT, meta(alt = true, shift = isShiftPressed), count)
            }
            KeyCode.MOVE_END_OF_LINE -> {
                if (!selection.isSelectionMode && isManualSelectionMode) {
                    isManualSelectionModeStart = false
                    isManualSelectionModeEnd = true
                }
                sendDownUpKeyEvent(KeyEvent.KEYCODE_DPAD_RIGHT, meta(alt = true, shift = isShiftPressed), count)
            }
        }
        isGlidePostEffect = false
    }

    /**
     * Handles a [KeyCode.CLIPBOARD_SELECT] event.
     */
    private fun handleClipboardSelect() = activeEditorInstance.apply {
        isManualSelectionMode = if (selection.isSelectionMode) {
            if (isManualSelectionMode && isManualSelectionModeStart) {
                selection.updateAndNotify(selection.start, selection.start)
            } else {
                selection.updateAndNotify(selection.end, selection.end)
            }
            false
        } else {
            !isManualSelectionMode
            // Must call to update UI properly
            //editingKeyboardView?.onUpdateSelection()
        }
        isGlidePostEffect = false
    }

    override fun onInputKeyDown(ev: InputKeyEvent) {
        val data = ev.data
        when (data.code) {
            KeyCode.INTERNAL_BATCH_EDIT -> {
                florisboard.beginInternalBatchEdit()
                return
            }
            KeyCode.SHIFT -> {
                handleShiftDown(ev)
            }
        }
    }

    override fun onInputKeyUp(ev: InputKeyEvent) {
        val data = ev.data
        when (data.code) {
            KeyCode.ARROW_DOWN,
            KeyCode.ARROW_LEFT,
            KeyCode.ARROW_RIGHT,
            KeyCode.ARROW_UP,
            KeyCode.MOVE_START_OF_PAGE,
            KeyCode.MOVE_END_OF_PAGE,
            KeyCode.MOVE_START_OF_LINE,
            KeyCode.MOVE_END_OF_LINE -> if (ev.action == InputKeyEvent.Action.DOWN_UP || ev.action == InputKeyEvent.Action.REPEAT) {
                handleArrow(data.code, ev.count)
            } else {
                handleArrow(data.code, 1)
            }
            KeyCode.CHAR_WIDTH_SWITCHER -> handleCharWidthSwitch()
            KeyCode.CHAR_WIDTH_FULL -> handleCharWidthFull()
            KeyCode.CHAR_WIDTH_HALF -> handleCharWidthHalf()
            KeyCode.CLEAR_CLIPBOARD_HISTORY -> florisboard.florisClipboardManager?.clearHistoryWithAnimation()
            KeyCode.CLIPBOARD_CUT -> activeEditorInstance.performClipboardCut()
            KeyCode.CLIPBOARD_COPY -> activeEditorInstance.performClipboardCopy()
            KeyCode.CLIPBOARD_PASTE -> activeEditorInstance.performClipboardPaste()
            KeyCode.CLIPBOARD_SELECT -> handleClipboardSelect()
            KeyCode.CLIPBOARD_SELECT_ALL -> activeEditorInstance.performClipboardSelectAll()
            KeyCode.DELETE -> handleDelete()
            KeyCode.DELETE_WORD -> handleDeleteWord()
            KeyCode.ENTER -> handleEnter()
            KeyCode.INTERNAL_BATCH_EDIT -> {
                florisboard.endInternalBatchEdit()
                return
            }
            KeyCode.LANGUAGE_SWITCH -> handleLanguageSwitch()
            KeyCode.REDO -> activeEditorInstance.performRedo()
            KeyCode.SETTINGS -> florisboard.launchSettings()
            KeyCode.SHIFT -> handleShiftUp()
            KeyCode.SHIFT_LOCK -> handleShiftLock()
            KeyCode.KANA_SWITCHER -> handleKanaSwitch()
            KeyCode.KANA_HIRA -> handleKanaHira()
            KeyCode.KANA_KATA -> handleKanaKata()
            KeyCode.KANA_HALF_KATA -> handleKanaHalfKata()
            KeyCode.SHOW_INPUT_METHOD_PICKER -> {
                if (PrefsReporitory.Settings.languageChange != LanguageChange.SWIPE_THROUGH_SPACE)
                    florisboard.imeManager?.showInputMethodPicker()
            }
            KeyCode.SPACE -> handleSpace(ev)
            KeyCode.SWITCH_TO_MEDIA_CONTEXT -> florisboard.setActiveInput(R.id.media_input)
            KeyCode.SWITCH_TO_CLIPBOARD_CONTEXT -> florisboard.setActiveInput(R.id.clip_input)
            KeyCode.SWITCH_TO_TEXT_CONTEXT -> florisboard.setActiveInput(
                R.id.text_input,
                forceSwitchToCharacters = true
            )
            KeyCode.TOGGLE_ONE_HANDED_MODE_LEFT -> florisboard.toggleOneHandedMode(isRight = false)
            KeyCode.TOGGLE_ONE_HANDED_MODE_RIGHT -> florisboard.toggleOneHandedMode(isRight = true)
            KeyCode.VIEW_CHARACTERS -> setActiveKeyboardMode(KeyboardMode.CHARACTERS)
            KeyCode.VIEW_NUMERIC -> setActiveKeyboardMode(KeyboardMode.NUMERIC)
            KeyCode.VIEW_NUMERIC_ADVANCED -> setActiveKeyboardMode(KeyboardMode.NUMERIC_ADVANCED)
            KeyCode.VIEW_PHONE -> setActiveKeyboardMode(KeyboardMode.PHONE)
            KeyCode.VIEW_PHONE2 -> setActiveKeyboardMode(KeyboardMode.PHONE2)
            KeyCode.VIEW_SYMBOLS -> setActiveKeyboardMode(KeyboardMode.SYMBOLS)
            KeyCode.VIEW_SYMBOLS2 -> setActiveKeyboardMode(KeyboardMode.SYMBOLS2)
            KeyCode.UNDO -> activeEditorInstance.performUndo()
            else -> {
                when (activeState.keyboardMode) {
                    KeyboardMode.NUMERIC,
                    KeyboardMode.NUMERIC_ADVANCED,
                    KeyboardMode.PHONE,
                    KeyboardMode.PHONE2 -> when (data.type) {
                        KeyType.CHARACTER,
                        KeyType.NUMERIC -> {
                            val text = data.asString(isForDisplay = false)
                            if (isGlidePostEffect && (TextProcessor.isWord(
                                    text,
                                    florisboard.activeSubtype.locale
                                ) || text.isDigitsOnly())
                            ) {
                                activeEditorInstance.commitText(" ")
                            }
                            activeEditorInstance.commitText(text)
                        }
                        else -> when (data.code) {
                            KeyCode.PHONE_PAUSE,
                            KeyCode.PHONE_WAIT -> {
                                val text = data.asString(isForDisplay = false)
                                if (isGlidePostEffect && (TextProcessor.isWord(
                                        text,
                                        florisboard.activeSubtype.locale
                                    ) || text.isDigitsOnly())
                                ) {
                                    activeEditorInstance.commitText(" ")
                                }
                                activeEditorInstance.commitText(text)
                            }
                        }
                    }
                    else -> when (data.type) {
                        KeyType.CHARACTER, KeyType.NUMERIC -> {
                            val text = data.asString(isForDisplay = false)
                            if (isGlidePostEffect && (TextProcessor.isWord(
                                    text,
                                    florisboard.activeSubtype.locale
                                ) || text.isDigitsOnly())
                            ) {
                                activeEditorInstance.commitText(" ")
                            }
                            activeEditorInstance.commitText(text)
                        }
                        else -> {
                        }
                    }
                }
            }
        }
        if (data.code != KeyCode.SHIFT && !activeState.capsLock && !inputEventDispatcher.isPressed(KeyCode.SHIFT)) {
            updateCapsState()
        }
        if (ev.data.code > KeyCode.SPACE) {
            isGlidePostEffect = false
        }
    }

    override fun onInputKeyRepeat(ev: InputKeyEvent) {
        florisboard.inputFeedbackManager.keyRepeatedAction(ev.data)
        onInputKeyUp(ev)
    }

    override fun onInputKeyCancel(ev: InputKeyEvent) {
        val data = ev.data
        when (data.code) {
            KeyCode.SHIFT -> handleShiftCancel()
        }
    }

    fun handleGesture(word: String) {
        activeEditorInstance.commitGesture(fixCase(word))
    }

    /**
     * Changes a word to the current case.
     * eg if [KeyboardState.capsLock] is true, abc -> ABC
     *    if [caps]     is true, abc -> Abc
     *    otherwise            , abc -> abc
     */
    private fun fixCase(word: String): String {
        return when {
            activeState.capsLock -> word.uppercase(florisboard.activeSubtype.locale.base)
            activeState.caps -> word.replaceFirstChar { if (it.isLowerCase()) it.titlecase(florisboard.activeSubtype.locale.base) else it.toString() }
            else -> word
        }
    }

    fun enableShift() {
        if (activeEditorInstance.editorInfo?.isPasswordInputType() == true) return
        inputEventDispatcher.send(InputKeyEvent.downUp(TextKeyData.SHIFT, 1))

        Log.d("12345", "enter")
    }
}