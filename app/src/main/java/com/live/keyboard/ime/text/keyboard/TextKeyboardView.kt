package com.live.keyboard.ime.text.keyboard

import android.animation.ValueAnimator
import android.content.Context
import android.content.res.Configuration
import android.graphics.*
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.animation.AccelerateInterpolator
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.ColorUtils
import androidx.core.view.children
import com.live.keyboard.R
import com.live.keyboard.background.view.keyboard.repository.BottomRightCharacterRepository
import com.live.keyboard.common.Pointer
import com.live.keyboard.common.PointerMap
import com.live.keyboard.common.ViewUtils
import com.live.keyboard.data.KeyboardTheme
import com.live.keyboard.ime.core.InputKeyEvent
import com.live.keyboard.ime.core.Subtype
import com.live.keyboard.ime.keyboard.ComputingEvaluator
import com.live.keyboard.ime.keyboard.DefaultComputingEvaluator
import com.live.keyboard.ime.keyboard.ImeOptions
import com.live.keyboard.ime.keyboard.KeyData
import com.live.keyboard.ime.keyboard.Keyboard
import com.live.keyboard.ime.keyboard.KeyboardState
import com.live.keyboard.ime.keyboard.KeyboardView
import com.live.keyboard.ime.popup.MutablePopupSet
import com.live.keyboard.ime.popup.PopupExtension
import com.live.keyboard.ime.popup.PopupManager
import com.live.keyboard.ime.text.gestures.GlideTypingGesture
import com.live.keyboard.ime.text.gestures.GlideTypingManager
import com.live.keyboard.ime.text.gestures.SwipeAction
import com.live.keyboard.ime.text.gestures.SwipeGesture
import com.live.keyboard.ime.text.key.KeyCode
import com.live.keyboard.ime.text.key.KeyHintConfiguration
import com.live.keyboard.ime.text.key.KeyType
import com.live.keyboard.ime.text.key.KeyVariation
import com.live.keyboard.ime.theme.Theme
import com.live.keyboard.repository.PrefsReporitory
import com.live.keyboard.res.AssetManager
import com.live.keyboard.res.FlorisRef
import com.live.keyboard.util.enums.Language
import com.live.keyboard.util.enums.LanguageChange
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.math.sqrt


@Suppress("UNUSED_PARAMETER")
class TextKeyboardView : KeyboardView, SwipeGesture.Listener, GlideTypingGesture.Listener, CoroutineScope {
    override val coroutineContext: CoroutineContext = MainScope().coroutineContext

    private var computedKeyboard: TextKeyboard? = null
    private var iconSet: TextKeyboardIconSet? = null

    private var keyboardTheme: KeyboardTheme = KeyboardTheme()
    private var cachedState: KeyboardState = KeyboardState.new(maskOfInterest = KeyboardState.INTEREST_TEXT)

    val mode: KeyboardMode? get() = computedKeyboard?.mode

    private var externalComputingEvaluator: ComputingEvaluator? = null
    private val internalComputingEvaluator = object : ComputingEvaluator {


        override fun evaluateCaps(): Boolean {
            return externalComputingEvaluator?.evaluateCaps()
                ?: DefaultComputingEvaluator.evaluateCaps()
        }

        override fun evaluateKanaKata(): Boolean {
            return externalComputingEvaluator?.evaluateKanaKata()
                ?: DefaultComputingEvaluator.evaluateKanaKata()
        }

        override fun evaluateKanaSmall(): Boolean {
            return externalComputingEvaluator?.evaluateKanaSmall()
                ?: DefaultComputingEvaluator.evaluateKanaSmall()
        }

        override fun evaluateCharHalfWidth(): Boolean {
            return externalComputingEvaluator?.evaluateCharHalfWidth()
                ?: DefaultComputingEvaluator.evaluateCharHalfWidth()
        }

        override fun evaluateCaps(data: KeyData): Boolean {
            return externalComputingEvaluator?.evaluateCaps(data)
                ?: DefaultComputingEvaluator.evaluateCaps(data)
        }

        override fun evaluateEnabled(data: KeyData): Boolean {
            return externalComputingEvaluator?.evaluateEnabled(data)
                ?: DefaultComputingEvaluator.evaluateEnabled(data)
        }

        override fun evaluateVisible(data: KeyData): Boolean {
            return externalComputingEvaluator?.evaluateVisible(data)
                ?: DefaultComputingEvaluator.evaluateVisible(data)
        }

        override fun getActiveSubtype(): Subtype {
            return externalComputingEvaluator?.getActiveSubtype()
                ?: DefaultComputingEvaluator.getActiveSubtype()
        }

        override fun getKeyVariation(): KeyVariation {
            return externalComputingEvaluator?.getKeyVariation()
                ?: DefaultComputingEvaluator.getKeyVariation()
        }

        override fun getKeyboard(): Keyboard {
            return computedKeyboard // Purposely not calling the external evaluator!
                ?: DefaultComputingEvaluator.getKeyboard()
        }

        override fun isSlot(data: KeyData): Boolean {
            return externalComputingEvaluator?.isSlot(data)
                ?: DefaultComputingEvaluator.isSlot(data)
        }

        override fun getSlotData(data: KeyData): KeyData? {
            return externalComputingEvaluator?.getSlotData(data)
                ?: DefaultComputingEvaluator.getSlotData(data)
        }
    }

    internal var isSmartbarKeyboardView: Boolean = false
    internal var isPreviewMode: Boolean = false
    internal var isLoadingPlaceholderKeyboard: Boolean = false

    private var keyHintConfiguration: KeyHintConfiguration = KeyHintConfiguration.HINTS_DISABLED
    private val pointerMap: PointerMap<TouchPointer> = PointerMap { TouchPointer() }
    private val popupManager: PopupManager<TextKeyboardView>

    private var initSelectionStart: Int = 0
    private var initSelectionEnd: Int = 0
    private var isGliding: Boolean = false
    private val swipeGestureDetector = SwipeGesture.Detector(context, this)

    private val glideTypingDetector = GlideTypingGesture.Detector(context)
    private val glideTypingManager: GlideTypingManager
        get() = GlideTypingManager.getInstance()
    private val glideDataForDrawing: MutableList<Pair<GlideTypingGesture.Detector.Position, Long>> = mutableListOf()
    private val fadingGlide: MutableList<Pair<GlideTypingGesture.Detector.Position, Long>> = mutableListOf()
    private var fadingGlideRadius: Float = 0.0f
    private var glideRefreshJob: Job? = null

    val desiredKey: TextKey = TextKey(data = TextKeyData.UNSPECIFIED)
    private val desiredKeyView: TextKeyView = TextKeyView(context).also {
        it.key = desiredKey
    }
    private var keyMarginH: Int = 0
    private var keyMarginV: Int = 0

    private val baselineTextSize = resources.getDimension(R.dimen.key_textSize)
    var fontSizeMultiplier: Double = 1.0
        private set
    private val glideTrailPaint: Paint = Paint()
    private var labelPaintTextSize: Float = resources.getDimension(R.dimen.key_textSize)
    private var labelPaintSpaceTextSize: Float = resources.getDimension(R.dimen.key_textSize)
    private var hintedLabelPaintTextSize: Float = resources.getDimension(R.dimen.key_textHintSize)
    private val tempRect: Rect = Rect()

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        context.obtainStyledAttributes(attrs, R.styleable.TextKeyboardView).apply {
            isPreviewMode = getBoolean(R.styleable.TextKeyboardView_isPreviewKeyboard, false)
            isTouchable = !isPreviewMode
            isSmartbarKeyboardView = getBoolean(R.styleable.TextKeyboardView_isSmartbarKeyboard, false)
            isLoadingPlaceholderKeyboard = getBoolean(R.styleable.TextKeyboardView_isLoadingPlaceholderKeyboard, false)
            recycle()
        }

        val popupLayerView = florisboard?.popupLayerView
        popupManager = PopupManager(this, popupLayerView)
        swipeGestureDetector.isEnabled = !isSmartbarKeyboardView
        setWillNotDraw(false)
    }

    fun setComputingEvaluator(evaluator: ComputingEvaluator?) {
        externalComputingEvaluator = evaluator
    }

    fun isShowingNubmerRow() = !isPreviewMode && PrefsReporitory.Settings.showNumberRow

    fun setComputedKeyboard(keyboard: TextKeyboard, keyboardTheme: KeyboardTheme? = null) {
        this.keyboardTheme = keyboardTheme ?: KeyboardTheme()
        val isShowingNumberRow = isShowingNubmerRow() && keyboard.mode == KeyboardMode.CHARACTERS
        val arraySize = if (isShowingNumberRow) 5 else keyboard.arrangement.size

        computedKeyboard = TextKeyboard(
            Array(arraySize) {
                when (it) {
                    0 -> handleFirstLine(isShowingNumberRow, keyboard)
                    else -> keyboard.arrangement[it - if (isShowingNumberRow) 1 else 0]
                }
            },
            keyboard.mode,
            keyboard.extendedPopupMapping,
            keyboard.extendedPopupMappingDefault
        )
        val renderViewDiff = computedKeyboard!!.keyCount - childCount
        if (renderViewDiff > 0) {
            // We have more keys than render views, add abs(diff) views
            for (n in 0 until abs(renderViewDiff)) {
                addView(TextKeyView(context))
            }
        } else if (renderViewDiff < 0) {
            // We have more render views than keys, remove abs(diff) views
            val n = abs(renderViewDiff)
            removeViews(childCount - n, n)
        }

        initGlideClassifier(computedKeyboard!!)
        reDrawKeyboard()
    }

    private fun handleFirstLine(isShowingNumberRow: Boolean, keyboard: TextKeyboard) =
        if (!isShowingNumberRow) keyboard.arrangement[0]
        else Array(10) { position ->
            val code = if (position == 9) 48
            else 49 + position
            TextKey(TextKeyData(code = code, label = code.toChar().toString()))
        }

    fun setIconSet(textKeyboardIconSet: TextKeyboardIconSet) {
        iconSet = textKeyboardIconSet
    }

    override fun onUpdateKeyboardState(newState: KeyboardState) {
        if (isMeasured) {
            if (cachedState.isDifferentTo(newState)) {
                // Something within the defined interest has changed
                cachedState.reset(newState)
                computeKeyboard()
                invalidate()
            }
        }
    }

    override fun sync() {
        swipeGestureDetector.apply {
            distanceThreshold = prefs.gestures.swipeDistanceThreshold
            velocityThreshold = prefs.gestures.swipeVelocityThreshold
        }
        if (isSmartbarKeyboardView) {
            keyMarginH = resources.getDimension(R.dimen.key_marginH).toInt()
            keyMarginV = resources.getDimension(R.dimen.key_marginV).toInt()
        } else {
            keyMarginH = ViewUtils.dp2px(prefs.keyboard.keySpacingHorizontal).toInt()
            keyMarginV = ViewUtils.dp2px(prefs.keyboard.keySpacingVertical).toInt()
        }
        fontSizeMultiplier = when (resources.configuration.orientation) {
            Configuration.ORIENTATION_PORTRAIT -> {
                prefs.keyboard.fontSizeMultiplierPortrait.toFloat() / 100.0
            }
            Configuration.ORIENTATION_LANDSCAPE -> {
                prefs.keyboard.fontSizeMultiplierLandscape.toFloat() / 100.0
            }
            else -> 1.0
        }
        keyHintConfiguration = prefs.keyboard.keyHintConfiguration()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        setBackgroundColor(Color.TRANSPARENT)
        glideTypingDetector.let {
            it.registerListener(this)
            it.registerListener(glideTypingManager)
            it.velocityThreshold = prefs.gestures.swipeVelocityThreshold
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        glideTypingDetector.let {
            it.unregisterListener(this)
            it.unregisterListener(glideTypingManager)
        }
    }

    override fun onTouchEventInternal(event: MotionEvent) {
        florisboard?.onEvent(event)

        val dispatcher = florisboard?.textInputManager?.inputEventDispatcher ?: return
        swipeGestureDetector.onTouchEvent(event)

        if (prefs.glide.enabled && computedKeyboard?.mode == KeyboardMode.CHARACTERS) {
            val glidePointer = pointerMap.findById(0)
            if (glideTypingDetector.onTouchEvent(event, glidePointer?.initialKey)) {
                for (pointer in pointerMap) {
                    if (pointer.activeKey != null) {
                        onTouchCancelInternal(event, pointer)
                    }
                }
                if (event.actionMasked == MotionEvent.ACTION_UP || event.actionMasked == MotionEvent.ACTION_CANCEL) {
                    pointerMap.clear()
                }
                isGliding = true
                return
            }
        }

        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                dispatcher.send(InputKeyEvent.down(TextKeyData.INTERNAL_BATCH_EDIT))
                val pointerIndex = event.actionIndex
                val pointerId = event.getPointerId(pointerIndex)
                val pointer = pointerMap.add(pointerId, pointerIndex)
                if (pointer != null) {
                    swipeGestureDetector.onTouchDown(event, pointer)
                    onTouchDownInternal(event, pointer)
                }
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                val pointerIndex = event.actionIndex
                val pointerId = event.getPointerId(pointerIndex)
                val oldPointer = pointerMap.findById(pointerId)
                if (oldPointer != null) {
                    swipeGestureDetector.onTouchCancel(event, oldPointer)
                    onTouchCancelInternal(event, oldPointer)
                    pointerMap.removeById(oldPointer.id)
                }
                // Search for active character keys and cancel them
                for (pointer in pointerMap) {
                    val activeKey = pointer.activeKey
                    if (activeKey != null && popupManager.isSuitableForPopups(activeKey)) {
                        swipeGestureDetector.onTouchCancel(event, pointer)
                        onTouchUpInternal(event, pointer)
                    }
                }
                val pointer = pointerMap.add(pointerId, pointerIndex)
                if (pointer != null) {
                    swipeGestureDetector.onTouchDown(event, pointer)
                    onTouchDownInternal(event, pointer)
                }
            }
            MotionEvent.ACTION_MOVE -> {


                for (pointerIndex in 0 until event.pointerCount) {
                    val pointerId = event.getPointerId(pointerIndex)
                    val pointer = pointerMap.findById(pointerId)
                    if (pointer != null) {
                        pointer.index = pointerIndex
                        val alwaysTriggerOnMove = (pointer.hasTriggeredGestureMove
                            && (pointer.initialKey?.computedData?.code == KeyCode.DELETE
                            && prefs.gestures.deleteKeySwipeLeft == SwipeAction.DELETE_CHARACTERS_PRECISELY
                            || pointer.initialKey?.computedData?.code == KeyCode.SPACE
                            || pointer.initialKey?.computedData?.code == KeyCode.CJK_SPACE))
                        if (swipeGestureDetector.onTouchMove(
                                event,
                                pointer,
                                alwaysTriggerOnMove
                            ) || pointer.hasTriggeredGestureMove
                        ) {


                            pointer.longPressJob?.cancel()
                            pointer.longPressJob = null
                            pointer.hasTriggeredGestureMove = true
                            pointer.activeKey?.let { activeKey ->
                                activeKey.setPressed(false) { invalidate(activeKey) }
                                florisboard!!.textInputManager.inputEventDispatcher.let { dispatcher ->
                                    if (dispatcher.isPressed(activeKey.computedData.code)) {
                                        dispatcher.send(InputKeyEvent.cancel(activeKey.computedData))
                                    }
                                }
                            }
                        } else {
                            if (pointer.activeKey?.computedData?.code == KeyCode.SPACE
                                && pointer.initialKey?.computedData?.code == KeyCode.SPACE
                            ) {
                                florisboard?.showSubTypeChangerView()
                            }
                            onTouchMoveInternal(event, pointer)
                        }
                    }
                }
            }
            MotionEvent.ACTION_POINTER_UP -> {
                val pointerIndex = event.actionIndex
                val pointerId = event.getPointerId(pointerIndex)
                val pointer = pointerMap.findById(pointerId)
                if (pointer != null) {
                    pointer.index = pointerIndex
                    if (swipeGestureDetector.onTouchUp(
                            event,
                            pointer
                        ) || pointer.hasTriggeredGestureMove || pointer.shouldBlockNextUp
                    ) {
                        if (pointer.hasTriggeredGestureMove && pointer.initialKey?.computedData?.code == KeyCode.DELETE) {
                            florisboard!!.textInputManager.isGlidePostEffect = false
                            florisboard!!.activeEditorInstance.apply {
                                if (selection.isSelectionMode) {
                                    deleteBackwards()
                                }
                            }
                        }
                        onTouchCancelInternal(event, pointer)
                    } else {
                        onTouchUpInternal(event, pointer)
                    }
                    pointerMap.removeById(pointer.id)
                }
            }
            MotionEvent.ACTION_UP -> {
                florisboard?.hideSubtypeChangerView()
                dispatcher.send(InputKeyEvent.up(TextKeyData.INTERNAL_BATCH_EDIT))
                val pointerIndex = event.actionIndex
                val pointerId = event.getPointerId(pointerIndex)
                for (pointer in pointerMap) {
                    if (pointer.id == pointerId) {
                        pointer.index = pointerIndex
                        if (swipeGestureDetector.onTouchUp(
                                event,
                                pointer
                            ) || pointer.hasTriggeredGestureMove || pointer.shouldBlockNextUp
                        ) {
                            if (pointer.hasTriggeredGestureMove && pointer.initialKey?.computedData?.code == KeyCode.DELETE) {
                                florisboard!!.textInputManager.isGlidePostEffect = false
                                florisboard!!.activeEditorInstance.apply {
                                    if (selection.isSelectionMode) {
                                        deleteBackwards()
                                    }
                                }
                            }
                            onTouchCancelInternal(event, pointer)
                        } else {
                            onTouchUpInternal(event, pointer)
                        }
                    } else {
                        swipeGestureDetector.onTouchCancel(event, pointer)
                        onTouchCancelInternal(event, pointer)
                    }
                }
                pointerMap.clear()
            }
            MotionEvent.ACTION_CANCEL -> {
                for (pointer in pointerMap) {
                    swipeGestureDetector.onTouchCancel(event, pointer)
                    onTouchCancelInternal(event, pointer)
                }
                pointerMap.clear()
                dispatcher.send(InputKeyEvent.up(TextKeyData.INTERNAL_BATCH_EDIT))
            }
        }
    }

    private fun onTouchDownInternal(event: MotionEvent, pointer: TouchPointer) {
        val key = computedKeyboard?.getKeyForPos(
            event.getX(pointer.index).roundToInt(), event.getY(pointer.index).roundToInt()
        )
        if (key != null && key.isEnabled) {
            florisboard!!.textInputManager.inputEventDispatcher.send(InputKeyEvent.down(key.computedData))
            if (prefs.keyboard.popupEnabled && popupManager.isSuitableForPopups(key)) {
                popupManager.show(key, keyHintConfiguration)
            }
            florisboard!!.inputFeedbackManager.keyPress(key.computedData)
            key.setPressed(true) { invalidate(key) }
            if (pointer.initialKey == null) {
                pointer.initialKey = key
            }
            pointer.activeKey = key
            pointer.longPressJob = mainScope.launch {
                val delayMillis = prefs.keyboard.longPressDelay.toLong()
                when (key.computedData.code) {
                    KeyCode.SPACE, KeyCode.CJK_SPACE -> {
                        initSelectionStart = florisboard!!.activeEditorInstance.selection.start
                        initSelectionEnd = florisboard!!.activeEditorInstance.selection.end
                        delay((delayMillis * 2.5f).toLong())
                        when (prefs.gestures.spaceBarLongPress) {
                            SwipeAction.NO_ACTION,
                            SwipeAction.INSERT_SPACE -> {
                            }
                            else -> {
                                florisboard!!.executeSwipeAction(prefs.gestures.spaceBarLongPress)
                                pointer.shouldBlockNextUp = true
                            }
                        }
                    }
                    KeyCode.SHIFT -> {
                        delay((delayMillis * 2.5f).toLong())
                        florisboard!!.textInputManager.inputEventDispatcher.send(
                            InputKeyEvent.downUp(
                                TextKeyData.SHIFT_LOCK
                            )
                        )
                        florisboard!!.inputFeedbackManager.keyLongPress(key.computedData)
                    }
                    KeyCode.LANGUAGE_SWITCH -> {
                        delay((delayMillis * 2.0f).toLong())
                        pointer.shouldBlockNextUp = true
                        florisboard!!.textInputManager.inputEventDispatcher.let { dispatcher ->
                            dispatcher.send(InputKeyEvent.downUp(TextKeyData.SHOW_INPUT_METHOD_PICKER))
                        }
                    }
                    else -> {
                        delay(delayMillis)
                        if (popupManager.isSuitableForPopups(key) && key.computedPopups.getPopupKeys(
                                keyHintConfiguration
                            ).isNotEmpty()
                        ) {
                            popupManager.extend(key, keyHintConfiguration)
                            florisboard!!.inputFeedbackManager.keyLongPress(key.computedData)
                        }
                    }
                }
            }
        } else {
            pointer.activeKey = null
        }
    }

    private fun onTouchMoveInternal(event: MotionEvent, pointer: TouchPointer) {
        val initialKey = pointer.initialKey
        val activeKey = pointer.activeKey
        if (initialKey != null && activeKey != null) {
            if (popupManager.isShowingExtendedPopup) {
                if (!popupManager.propagateMotionEvent(activeKey, event, pointer.index)) {
                    onTouchCancelInternal(event, pointer)
                    onTouchDownInternal(event, pointer)
                }
            } else {
                if ((event.getX(pointer.index) < activeKey.visibleBounds.left - 0.1f * activeKey.visibleBounds.width())
                    || (event.getX(pointer.index) > activeKey.visibleBounds.right + 0.1f * activeKey.visibleBounds.width())
                    || (event.getY(pointer.index) < activeKey.visibleBounds.top - 0.35f * activeKey.visibleBounds.height())
                    || (event.getY(pointer.index) > activeKey.visibleBounds.bottom + 0.35f * activeKey.visibleBounds.height())
                ) {
                    onTouchCancelInternal(event, pointer)
                    onTouchDownInternal(event, pointer)
                }
            }
        }
    }

    private fun onTouchUpInternal(event: MotionEvent, pointer: TouchPointer) {
        pointer.longPressJob?.cancel()
        pointer.longPressJob = null

        val initialKey = pointer.initialKey
        val activeKey = pointer.activeKey
        if (initialKey != null && activeKey != null) {
            activeKey.setPressed(false) { invalidate(activeKey) }
            florisboard!!.textInputManager.inputEventDispatcher.let { dispatcher ->
                if (popupManager.isSuitableForPopups(activeKey)) {
                    val retData = popupManager.getActiveKeyData(activeKey, keyHintConfiguration)
                    if (retData != null && !pointer.hasTriggeredGestureMove) {
                        if (retData == activeKey.computedData) {
                            dispatcher.send(InputKeyEvent.up(activeKey.computedData))
                        } else {
                            if (dispatcher.isPressed(activeKey.computedData.code)) {
                                dispatcher.send(InputKeyEvent.cancel(activeKey.computedData))
                            }
                            dispatcher.send(InputKeyEvent.downUp(retData))
                        }
                    } else {
                        if (dispatcher.isPressed(activeKey.computedData.code)) {
                            dispatcher.send(InputKeyEvent.cancel(activeKey.computedData))
                        }
                    }
                    popupManager.hide()
                } else {
                    if (pointer.hasTriggeredGestureMove) {
                        dispatcher.send(InputKeyEvent.cancel(activeKey.computedData))
                    } else {
                        dispatcher.send(InputKeyEvent.up(activeKey.computedData))
                    }
                }
            }
            pointer.activeKey = null
        }
        pointer.hasTriggeredGestureMove = false
        pointer.shouldBlockNextUp = false
    }

    private fun onTouchCancelInternal(event: MotionEvent, pointer: TouchPointer) {
        val florisboard = florisboard ?: return
        pointer.longPressJob?.cancel()
        pointer.longPressJob = null

        val activeKey = pointer.activeKey
        if (activeKey != null) {
            activeKey.setPressed(false) { invalidate(activeKey) }
            florisboard.textInputManager.inputEventDispatcher
                .send(InputKeyEvent.cancel(activeKey.computedData))
            if (popupManager.isSuitableForPopups(activeKey)) {
                popupManager.hide()
            }
            pointer.activeKey = null
        }
        pointer.hasTriggeredGestureMove = false
        pointer.shouldBlockNextUp = false
    }

    override fun onSwipe(event: SwipeGesture.Event): Boolean {
        val florisboard = florisboard ?: return false
        val pointer = pointerMap.findById(event.pointerId) ?: return false
        val initialKey = pointer.initialKey ?: return false

        val activeKey = pointer.activeKey

        return when (initialKey.computedData.code) {
            KeyCode.DELETE -> handleDeleteSwipe(event)
            KeyCode.SPACE, KeyCode.CJK_SPACE -> handleSpaceSwipe(event)
            else -> when {
                (initialKey.computedData.code == KeyCode.SHIFT && activeKey?.computedData?.code == KeyCode.SPACE ||
                    initialKey.computedData.code == KeyCode.SHIFT && activeKey?.computedData?.code == KeyCode.CJK_SPACE) &&
                    event.type == SwipeGesture.Type.TOUCH_MOVE -> handleSpaceSwipe(event)
                initialKey.computedData.code == KeyCode.SHIFT && activeKey?.computedData?.code != KeyCode.SHIFT &&
                    event.type == SwipeGesture.Type.TOUCH_UP -> {
                    activeKey?.let {
                        florisboard.textInputManager.inputEventDispatcher.send(
                            InputKeyEvent.up(
                                popupManager.getActiveKeyData(it, keyHintConfiguration) ?: it.computedData
                            )
                        )
                    }
                    florisboard.textInputManager.inputEventDispatcher.send(InputKeyEvent.cancel(TextKeyData.SHIFT))
                    true
                }
                initialKey.computedData.code > KeyCode.SPACE && !popupManager.isShowingExtendedPopup -> when {
                    !prefs.glide.enabled && !pointer.hasTriggeredGestureMove -> when (event.type) {
                        SwipeGesture.Type.TOUCH_UP -> {
                            val swipeAction = when (event.direction) {
                                SwipeGesture.Direction.UP -> prefs.gestures.swipeUp
                                SwipeGesture.Direction.DOWN -> prefs.gestures.swipeDown
                                SwipeGesture.Direction.LEFT -> prefs.gestures.swipeLeft
                                SwipeGesture.Direction.RIGHT -> prefs.gestures.swipeRight
                                else -> SwipeAction.NO_ACTION
                            }
                            if (swipeAction != SwipeAction.NO_ACTION) {
                                florisboard.executeSwipeAction(swipeAction)
                                true
                            } else {
                                false
                            }
                        }
                        else -> false
                    }
                    else -> false
                }
                else -> false
            }
        }
    }

    private fun handleDeleteSwipe(event: SwipeGesture.Event): Boolean {
        val florisboard = florisboard ?: return false
        if (cachedState.isRawInputEditor) return false
        val pointer = pointerMap.findById(event.pointerId) ?: return false
        return when (event.type) {
            SwipeGesture.Type.TOUCH_MOVE -> when (prefs.gestures.deleteKeySwipeLeft) {
                SwipeAction.DELETE_CHARACTERS_PRECISELY -> {
                    florisboard.activeEditorInstance.apply {
                        if (abs(event.relUnitCountX) > 0) {
                            florisboard.inputFeedbackManager.gestureMovingSwipe(TextKeyData.DELETE)
                        }
                        markComposingRegion(null)
                        if (selection.isValid) {
                            selection.updateAndNotify(
                                (selection.end + event.absUnitCountX + 1).coerceIn(0, selection.end),
                                selection.end
                            )
                        }
                    }
                    pointer.shouldBlockNextUp = true
                    true
                }
                SwipeAction.DELETE_WORDS_PRECISELY -> {
                    florisboard.activeEditorInstance.apply {
                        if (abs(event.relUnitCountX) > 0) {
                            florisboard.inputFeedbackManager.gestureMovingSwipe(TextKeyData.DELETE)
                        }
                        markComposingRegion(null)
                        if (selection.isValid) {
                            selectionSetNWordsLeft(abs(event.absUnitCountX / 2) - 1)
                        }
                    }
                    pointer.shouldBlockNextUp = true
                    true
                }
                else -> false
            }
            SwipeGesture.Type.TOUCH_UP -> {
                if (event.direction == SwipeGesture.Direction.LEFT &&
                    prefs.gestures.deleteKeySwipeLeft == SwipeAction.DELETE_WORD
                ) {
                    florisboard.executeSwipeAction(prefs.gestures.deleteKeySwipeLeft)
                    true
                } else {
                    false
                }
            }
        }
    }

    private fun handleSpaceSwipe(event: SwipeGesture.Event): Boolean {
        val florisboard = florisboard ?: return false
        val pointer = pointerMap.findById(event.pointerId) ?: return false

        return when (event.type) {
            SwipeGesture.Type.TOUCH_MOVE -> when (event.direction) {
                SwipeGesture.Direction.LEFT -> {
                    if (prefs.gestures.spaceBarSwipeLeft == SwipeAction.MOVE_CURSOR_LEFT) {
                        abs(event.relUnitCountX).let {
                            val count = if (!pointer.hasTriggeredGestureMove) {
                                it - 1
                            } else {
                                it
                            }
                            if (count > 0) {
                                florisboard.inputFeedbackManager.gestureMovingSwipe(TextKeyData.SPACE)
                                florisboard.textInputManager.inputEventDispatcher
                                    .send(InputKeyEvent.downUp(TextKeyData.ARROW_LEFT, count))
                            }
                        }
                    }
                    true
                }
                SwipeGesture.Direction.RIGHT -> {
                    if (prefs.gestures.spaceBarSwipeRight == SwipeAction.MOVE_CURSOR_RIGHT) {
                        abs(event.relUnitCountX).let {
                            val count = if (!pointer.hasTriggeredGestureMove) {
                                it - 1
                            } else {
                                it
                            }
                            if (count > 0) {
                                florisboard.inputFeedbackManager.gestureMovingSwipe(TextKeyData.SPACE)
                                florisboard.textInputManager.inputEventDispatcher.send(
                                    InputKeyEvent.downUp(
                                        TextKeyData.ARROW_RIGHT, count
                                    )
                                )
                            }
                        }
                    }
                    true
                }
                else -> true // To prevent the popup display of nearby keys
            }
            SwipeGesture.Type.TOUCH_UP -> when (event.direction) {
                SwipeGesture.Direction.LEFT -> {
                    prefs.gestures.spaceBarSwipeLeft.let {
                        if (it != SwipeAction.MOVE_CURSOR_LEFT) {
                            florisboard.executeSwipeAction(it)
                            true
                        } else {
                            false
                        }
                    }
                }
                SwipeGesture.Direction.RIGHT -> {
                    prefs.gestures.spaceBarSwipeRight.let {
                        if (it != SwipeAction.MOVE_CURSOR_RIGHT) {
                            florisboard.executeSwipeAction(it)
                            true
                        } else {
                            false
                        }
                    }
                }
                else -> {
                    if (event.absUnitCountY < -6) {
                        florisboard.executeSwipeAction(prefs.gestures.spaceBarSwipeUp)
                        true
                    } else {
                        false
                    }
                }
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredWidth = MeasureSpec.getSize(widthMeasureSpec).toFloat()
        val desiredHeight = if (isSmartbarKeyboardView || isPreviewMode) {
            MeasureSpec.getSize(heightMeasureSpec).toFloat()
        } else {
            (florisboard?.uiBinding?.inputView?.desiredTextKeyboardViewHeight ?: MeasureSpec.getSize(
                heightMeasureSpec
            )
                .toFloat())
        } * if (isPreviewMode) 0.90f
        else if (isShowingNubmerRow() && computedKeyboard?.mode == KeyboardMode.CHARACTERS) 1.20f
        else {
            1f
        }

        super.onMeasure(
            MeasureSpec.makeMeasureSpec(desiredWidth.roundToInt(), MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(desiredHeight.roundToInt(), MeasureSpec.EXACTLY)
        )
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        computeDesiredDimensions()
        computeKeyboard()
    }

    private fun computeDesiredDimensions() {
        val keyboard = computedKeyboard ?: return
        desiredKey.touchBounds.let { bounds ->
            bounds.right = if (isSmartbarKeyboardView) {
                measuredWidth / 6
            } else {
                measuredWidth / 10
            }
            if (id == R.id.keyboard) bounds.left -= 1
            bounds.bottom = when {
                isSmartbarKeyboardView -> {
                    measuredHeight
                }
                florisboard?.uiBinding?.inputView?.shouldGiveAdditionalSpace == true && keyboard.mode != KeyboardMode.CHARACTERS -> {
                    (measuredHeight / (keyboard.rowCount + 0.5f).coerceAtMost(5.0f)).toInt()
                }
                else -> {
                    measuredHeight / keyboard.rowCount.coerceAtLeast(1)
                }
            }.times(if (id == R.id.keyboard) 0.95f else 1f).toInt()
        }
        desiredKey.visibleBounds.apply {
            left = keyMarginH
            right = desiredKey.touchBounds.width() - keyMarginH
            when {
                isSmartbarKeyboardView -> {
                    top = (0.75 * keyMarginV).toInt()
                    bottom = (desiredKey.touchBounds.height() - 0.75 * keyMarginV).toInt()
                }
                else -> {
                    top = keyMarginV
                    bottom = desiredKey.touchBounds.height() - keyMarginV
                }
            }
        }
        TextKeyboard.layoutDrawableBounds(desiredKey, 1.0)
        TextKeyboard.layoutLabelBounds(desiredKey)

        setTextSizeFor(
            desiredKeyView.labelPaint,
            desiredKey.visibleLabelBounds.width().toFloat(),
            desiredKey.visibleLabelBounds.height().toFloat(),
            "X",
            fontSizeMultiplier
        )
        labelPaintTextSize = desiredKeyView.labelPaint.textSize
        labelPaintSpaceTextSize =
            desiredKeyView.labelPaint.textSize.coerceAtMost(resources.getDimension(R.dimen.key_space_textSize))

        setTextSizeFor(
            desiredKeyView.hintedLabelPaint,
            desiredKey.visibleBounds.width() * 1.0f / 5.0f,
            desiredKey.visibleBounds.height() * 1.0f / 5.0f,
            "X",
            fontSizeMultiplier
        )
        hintedLabelPaintTextSize = desiredKeyView.hintedLabelPaint.textSize
    }

    private fun computeKeyboard() {

        val keyboard = computedKeyboard ?: return
        for (key in keyboard.keys()) {
            key.compute(internalComputingEvaluator)
            computeLabelsAndDrawables(key, keyHintConfiguration)
        }

        keyboard.layout(this)
        val isBorderless = true

        var numberCode = 49
        var isNeedAddNumberPopup = isShowingNubmerRow()

        keyboard.keys().withIndex().forEach { (n, key) ->
            getChildAt(n)?.let { rv ->
                if (rv is TextKeyView) {
                    rv.key = when {
                        isPreviewMode -> key
                        isSpecialKey(key) -> getNewSpecialKey(key)
                        mustChangeLeftComaKey(key) -> key.apply {
                            computedData = TextKeyData(
                                key.computedData.type,
                                46,
                                ".",
                                key.computedData.groupId,
                                key.computedData.popup
                            )
                            label = "."
                        }
                        else -> key
                    }

                    if (isShowingNubmerRow()) key.computedNumberHint = null
                    else if (isNeedAddNumberPopup) key.computedNumberHint = TextKeyData(
                        label = if (numberCode == 58) {
                            isNeedAddNumberPopup = false
                            48.toChar().toString()
                        } else numberCode.toChar().toString(),
                        code = if (numberCode == 58) 48 else numberCode++,
                    )
                    layoutRenderView(rv, key, isBorderless)
                    prepareKey(key, keyboardTheme, rv)
                    rv.invalidate()
                }
            }
        }
        handleTheme(keyboardTheme)
    }

    private fun mustChangeLeftComaKey(key: TextKey) = key.computedData.code == 44
        && key.computedData.groupId != 2
        && BottomRightCharacterRepository.selectedBottomRightCharacterCode == 44

    private fun getNewSpecialKey(key: TextKey): TextKey {
        val popupKeyData = listOf(
            TextKeyData(code = 38, label = "&"),
            TextKeyData(code = 37, label = "%"),
            TextKeyData(code = 44, label = ","),
            TextKeyData(code = 34, label = "\\"),
            TextKeyData(code = 45, label = "-"),
            TextKeyData(code = 58, label = ":"),
            TextKeyData(code = 39, label = "'"),
            TextKeyData(code = 64, label = "@"),
            TextKeyData(code = 59, label = ";"),
            TextKeyData(code = 47, label = "/"),
            TextKeyData(code = 40, label = "("),
            TextKeyData(code = 41, label = ")"),
            TextKeyData(code = 35, label = "#"),
            TextKeyData(code = 33, label = "!"),
            TextKeyData(code = 63, label = "?"),
        )

        val currentSymbol = BottomRightCharacterRepository.SelectableCharacter.values()
            .firstOrNull { it.code == PrefsReporitory.Settings.specialSymbol } ?: return key

        key.computedData.let {
            if (it is TextKeyData) {
                it.code = currentSymbol.code
                it.label = currentSymbol.label
            }
        }

        val newKeyList = popupKeyData.map {
            if (it.code == currentSymbol.code) TextKeyData(code = 46, label = ".")
            else it
        }

        key.computedPopups = MutablePopupSet(
            key.computedData,
            relevant = ArrayList(newKeyList)
        )

        return key
    }

    fun readJsonFile() {

        val ref = FlorisRef.assets("${PopupManager.POPUP_EXTENSION_PATH_REL}/en.json")

        AssetManager.init(context).loadJsonAsset<PopupExtension>(ref)
    }

    private fun layoutRenderView(rv: TextKeyView, key: TextKey, isBorderless: Boolean) {
        val shouldReduceSize = false
        rv.layout(
            key.visibleBounds.left,
            if (shouldReduceSize) {
                (key.visibleBounds.top + key.visibleBounds.height() * 0.12).toInt()
            } else {
                key.visibleBounds.top
            },
            key.visibleBounds.right,
            if (shouldReduceSize) {
                (key.visibleBounds.bottom - key.visibleBounds.height() * 0.12).toInt()
            } else {
                key.visibleBounds.bottom
            }
        )
    }

    private fun prepareKey(key: TextKey, theme: KeyboardTheme, renderView: TextKeyView) {
        renderView.let { rv ->
            rv.labelPaint.let {
                (rv.background as? GradientDrawable)?.alpha = (255 * (theme.opacity / 100f)).toInt()
                it.color = Color.parseColor(theme.keyTextColor)
                if (computedKeyboard?.mode == KeyboardMode.CHARACTERS && (key.computedData.code == KeyCode.SPACE || key.computedData.code == KeyCode.CJK_SPACE)) {
                    it.alpha = 120
                }
                it.textSize = when (key.computedData.code) {
                    KeyCode.SPACE, KeyCode.CJK_SPACE -> labelPaintSpaceTextSize
                    KeyCode.VIEW_CHARACTERS,
                    KeyCode.VIEW_SYMBOLS,
                    KeyCode.VIEW_SYMBOLS2 -> labelPaintTextSize * 0.80f
                    KeyCode.VIEW_NUMERIC,
                    KeyCode.VIEW_NUMERIC_ADVANCED -> labelPaintTextSize * 0.55f
                    else -> labelPaintTextSize
                }
            }
            rv.hintedLabelPaint.let {
                it.color = Color.parseColor(theme.buttonColor)
                it.alpha = 170
                it.textSize = hintedLabelPaintTextSize
            }
        }
    }

    /**
     * Automatically sets the text size of [boxPaint] for given [text] so it fits within the given
     * bounds.
     *
     * Implementation based on this blog post by Lucas (SketchingDev), written on Aug 20, 2015
     *  https://sketchingdev.co.uk/blog/resizing-text-to-fit-into-a-container-on-android.html
     *
     * @param boxPaint The [Paint] object which the text size should be applied to.
     * @param boxWidth The max width for the surrounding box of [text].
     * @param boxHeight The max height for the surrounding box of [text].
     * @param text The text for which the size should be calculated.
     * @param multiplier The factor by which the resulting text size should be multiplied with.
     */
    private fun setTextSizeFor(
        boxPaint: Paint,
        boxWidth: Float,
        boxHeight: Float,
        text: String,
        multiplier: Double = 1.0
    ): Float {
        var size = baselineTextSize
        boxPaint.textSize = size
        boxPaint.getTextBounds(text, 0, text.length, tempRect)
        val w = tempRect.width().toFloat()
        val h = tempRect.height().toFloat()
        val diffW = abs(boxWidth - w)
        val diffH = abs(boxHeight - h)
        if (w < boxWidth && h < boxHeight) {
            // Text fits, scale up on axis which has less room
            size *= if (diffW < diffH) {
                1.0f + diffW / w
            } else {
                1.0f + diffH / h
            }
        } else if (w >= boxWidth && h < boxHeight) {
            // Text does not fit on x-axis
            size *= (1.0f - diffW / w)
        } else if (w < boxWidth && h >= boxHeight) {
            // Text does not fit on y-axis
            size *= (1.0f - diffH / h)
        } else {
            // Text does not fit at all, scale down on axis which has most overshoot
            size *= if (diffW < diffH) {
                1.0f - diffH / h
            } else {
                1.0f - diffW / w
            }
        }
        size *= multiplier.toFloat()
        boxPaint.textSize = size
        return size
    }

    override fun onThemeUpdated(theme: Theme) {
        if (theme.getAttr(Theme.Attr.GLIDE_TRAIL_COLOR).toSolidColor().color == 0) {
            glideTrailPaint.color = theme.getAttr(Theme.Attr.WINDOW_COLOR_PRIMARY).toSolidColor().color
            glideTrailPaint.alpha = 32
        } else {
            glideTrailPaint.color = theme.getAttr(Theme.Attr.GLIDE_TRAIL_COLOR).toSolidColor().color
        }
        val isBorderless = !theme.getAttr(Theme.Attr.KEY_SHOW_BORDER).toOnOff().state
        for (n in 0 until childCount) {
            val rv = getChildAt(n) as? TextKeyView
            if (rv?.key != null) {
                layoutRenderView(rv, rv.key!!, isBorderless)
                prepareKey(rv.key!!, keyboardTheme, rv)
                rv.invalidate()
            }
        }
    }

    private fun invalidate(key: TextKey) {
        for (n in 0 until childCount) {
            val rv = getChildAt(n) as? TextKeyView
            if (rv != null && rv.key == key) {
                prepareKey(key, keyboardTheme, rv)
                rv.invalidate()
                break
            }
        }
    }

    fun onDrawComputedKey(canvas: Canvas, key: TextKey, renderView: TextKeyView) {
        if (!key.isVisible) return
        val label = key.label
        if (label != null) {
            if (key.computedData.code == KeyCode.SPACE && needToShowLanguageChangeArrows()) {
                ResourcesCompat.getDrawable(resources, R.drawable.ic_language_change_arrow_left, null)?.apply {
                    bounds = Rect(
                        key.visibleBounds.left,
                        with(key.visibleBounds) { top + (bottom - top) / 4 },
                        with(key.visibleBounds) { left + (bottom - top) / 2 },
                        with(key.visibleBounds) { bottom - (bottom - top) / 4 },
                    )
                    setTint(Color.parseColor(keyboardTheme.keyTextColor))
                    draw(canvas)
                }
                ResourcesCompat.getDrawable(resources, R.drawable.ic_language_change_arrow_right, null)?.apply {
                    bounds = Rect(
                        with(key.visibleBounds) { right - (bottom - top) / 2 },
                        with(key.visibleBounds) { top + (bottom - top) / 4 },
                        key.visibleBounds.right,
                        with(key.visibleBounds) { bottom - (bottom - top) / 4 },
                    )
                    setTint(Color.parseColor(keyboardTheme.keyTextColor))
                    draw(canvas)
                }
            }
            renderView.labelPaint.let {
                it.typeface = ResourcesCompat.getFont(context, keyboardTheme.keyFont ?: R.font.roboto_400)
                it.color = Color.parseColor(keyboardTheme.keyTextColor)
                val centerX = key.visibleLabelBounds.exactCenterX()
                val centerY = key.visibleLabelBounds.exactCenterY() + (it.textSize - it.descent()) / 2
                if (label.contains("\n")) {
                    // Even if more lines may be existing only the first 2 are shown
                    val labelLines = label.split("\n")
                    val verticalAdjustment = key.visibleBounds.height() * 0.18f
                    canvas.drawText(labelLines[0], centerX, centerY - verticalAdjustment, it)
                    canvas.drawText(labelLines[1], centerX, centerY + verticalAdjustment, it)
                } else {
                    if (isPreviewMode && isSpecialKey(key)) canvas.drawText(".", centerX, centerY, it)
                    else canvas.drawText(label, centerX, centerY, it)
                }
            }
        }


        val hintedLabel = key.hintedLabel
        if (hintedLabel != null) {
            renderView.hintedLabelPaint.let {
                it.typeface = ResourcesCompat.getFont(context, keyboardTheme.keyFont ?: R.font.roboto_400)
                it.color = Color.parseColor(keyboardTheme.keyTextColor)
                val centerX = key.visibleBounds.left + key.visibleBounds.width() * 5.0f / 6.0f
                val centerY =
                    key.visibleBounds.top + key.visibleBounds.height() * 1.0f / 6.0f + (it.textSize - it.descent()) / 2
                canvas.drawText(hintedLabel, centerX, centerY, it)
            }
        }

        val foregroundDrawableId = key.foregroundDrawableId
        if (foregroundDrawableId != null) {
            iconSet?.withDrawable(foregroundDrawableId) {
                bounds = key.visibleDrawableBounds
                setTint(renderView.labelPaint.color)
                draw(canvas)
            }
        }
    }

    private fun needToShowLanguageChangeArrows() =
        PrefsReporitory.Settings.languageChange == LanguageChange.SWIPE_THROUGH_SPACE
            && Language.values().count { it.isSelected } > 1


    /**
     * Computes the labels and drawables needed to draw the key.
     */
    private fun computeLabelsAndDrawables(key: TextKey, keyHintConfiguration: KeyHintConfiguration) {
        // Reset attributes first to avoid invalid states if not updated
        //  key.label = null
        key.hintedLabel = null
        key.foregroundDrawableId = null

        val data = key.computedData
        if (data.type == KeyType.CHARACTER && data.code != KeyCode.SPACE && data.code != KeyCode.CJK_SPACE
            && data.code != KeyCode.HALF_SPACE && data.code != KeyCode.KESHIDA || data.type == KeyType.NUMERIC
        ) {
            key.label = data.asString(isForDisplay = true)
            key.computedPopups.getPopupKeys(keyHintConfiguration).hint?.asString(isForDisplay = true).let {
                key.hintedLabel = it
            }
        } else {
            when (data.code) {
                KeyCode.ARROW_LEFT -> {
                    key.foregroundDrawableId = R.drawable.ic_keyboard_arrow_left
                }
                KeyCode.ARROW_RIGHT -> {
                    key.foregroundDrawableId = R.drawable.ic_keyboard_arrow_right
                }
                KeyCode.CLIPBOARD_COPY -> {
                    key.foregroundDrawableId = R.drawable.ic_content_copy
                }
                KeyCode.CLIPBOARD_CUT -> {
                    key.foregroundDrawableId = R.drawable.ic_content_cut
                }
                KeyCode.CLIPBOARD_PASTE -> {
                    key.foregroundDrawableId = R.drawable.ic_content_paste
                }
                KeyCode.CLIPBOARD_SELECT_ALL -> {
                    key.foregroundDrawableId = R.drawable.ic_select_all
                }
                KeyCode.DELETE -> {
                    key.foregroundDrawableId = R.drawable.ic_backspace
                }
                KeyCode.ENTER -> {
                    val imeOptions = cachedState.imeOptions
                    key.foregroundDrawableId = when (imeOptions.enterAction) {
                        ImeOptions.EnterAction.DONE -> R.drawable.ic_done
                        ImeOptions.EnterAction.GO -> R.drawable.ic_arrow_right_alt
                        ImeOptions.EnterAction.NEXT -> R.drawable.ic_arrow_right_alt
                        ImeOptions.EnterAction.NONE -> R.drawable.ic_keyboard_return
                        ImeOptions.EnterAction.PREVIOUS -> R.drawable.ic_arrow_right_alt
                        ImeOptions.EnterAction.SEARCH -> R.drawable.ic_search
                        ImeOptions.EnterAction.SEND -> R.drawable.ic_send
                        ImeOptions.EnterAction.UNSPECIFIED -> R.drawable.ic_keyboard_return
                    }
                    if (imeOptions.flagNoEnterAction) {
                        key.foregroundDrawableId = R.drawable.ic_keyboard_return
                    }
                }
                KeyCode.LANGUAGE_SWITCH -> {
                    key.foregroundDrawableId = R.drawable.ic_language
                }
                KeyCode.PHONE_PAUSE -> key.label = resources.getString(R.string.key__phone_pause)
                KeyCode.PHONE_WAIT -> key.label = resources.getString(R.string.key__phone_wait)
                KeyCode.SHIFT -> {
                    key.foregroundDrawableId = when (cachedState.caps) {
                        true -> R.drawable.ic_keyboard_capslock
                        else -> R.drawable.ic_keyboard_arrow_up
                    }
                }
                KeyCode.SPACE, KeyCode.CJK_SPACE -> {
                    when (computedKeyboard?.mode) {
                        KeyboardMode.NUMERIC,
                        KeyboardMode.NUMERIC_ADVANCED,
                        KeyboardMode.PHONE,
                        KeyboardMode.PHONE2 -> {
                            key.foregroundDrawableId = R.drawable.ic_space_bar
                        }
                        KeyboardMode.CHARACTERS -> {
                            key.label = florisboard?.activeSubtype?.locale?.iso3Language?.uppercase()?.take(2)
                        }
                        else -> {
                        }
                    }
                }
                KeyCode.SWITCH_TO_MEDIA_CONTEXT -> {
                    key.foregroundDrawableId = R.drawable.ic_sentiment_satisfied
                }
                KeyCode.SWITCH_TO_CLIPBOARD_CONTEXT -> {
                    key.foregroundDrawableId = R.drawable.ic_assignment
                }
                KeyCode.KANA_SWITCHER -> {
                    key.foregroundDrawableId = if (cachedState.isKanaKata) {
                        R.drawable.ic_keyboard_kana_switcher_kata
                    } else {
                        R.drawable.ic_keyboard_kana_switcher_hira
                    }
                }
                KeyCode.CHAR_WIDTH_SWITCHER -> {
                    key.foregroundDrawableId = if (cachedState.isCharHalfWidth) {
                        R.drawable.ic_keyboard_char_width_switcher_full
                    } else {
                        R.drawable.ic_keyboard_char_width_switcher_half
                    }
                }
                KeyCode.CHAR_WIDTH_FULL -> {
                    key.foregroundDrawableId = R.drawable.ic_keyboard_char_width_switcher_full
                }
                KeyCode.CHAR_WIDTH_HALF -> {
                    key.foregroundDrawableId = R.drawable.ic_keyboard_char_width_switcher_half
                }
                KeyCode.SWITCH_TO_TEXT_CONTEXT,
                KeyCode.VIEW_CHARACTERS -> {
                    key.label = resources.getString(R.string.key__view_characters)
                }
                KeyCode.VIEW_NUMERIC,
                KeyCode.VIEW_NUMERIC_ADVANCED -> {
                    key.label = resources.getString(R.string.key__view_numeric)
                }
                KeyCode.VIEW_PHONE -> {
                    key.label = resources.getString(R.string.key__view_phone)
                }
                KeyCode.VIEW_PHONE2 -> {
                    key.label = resources.getString(R.string.key__view_phone2)
                }
                KeyCode.VIEW_SYMBOLS -> {
                    key.label = resources.getString(R.string.key__view_symbols)
                }
                KeyCode.VIEW_SYMBOLS2 -> {
                    key.label = resources.getString(R.string.key__view_symbols2)
                }
                KeyCode.HALF_SPACE -> {
                    key.label = resources.getString(R.string.key__view_half_space)
                }
                KeyCode.KESHIDA -> {
                    key.label = resources.getString(R.string.key__view_keshida)
                }
            }
        }
    }

    override fun dispatchDraw(canvas: Canvas?) {
        super.dispatchDraw(canvas)
        if ((prefs.glide.enabled && prefs.glide.showTrail && !isSmartbarKeyboardView) || isPreviewMode) {
            val targetDist = 3.0f
            val radius = 20.0f
            val radiusReductionFactor = 0.99f
            if (fadingGlideRadius > 0) {
                drawGlideTrail(fadingGlide, targetDist, fadingGlideRadius, canvas, radiusReductionFactor)
            }

            if (isGliding && glideDataForDrawing.isNotEmpty()) {
                drawGlideTrail(glideDataForDrawing, targetDist, radius, canvas, radiusReductionFactor)
            }
        }
    }

    private fun drawGlideTrail(
        gestureData: MutableList<Pair<GlideTypingGesture.Detector.Position, Long>>,
        targetDist: Float,
        initialRadius: Float,
        canvas: Canvas?,
        radiusReductionFactor: Float
    ) {
        var radius = initialRadius
        var drawnPoints = 0
        var prevX = gestureData.lastOrNull()?.first?.x ?: 0.0f
        var prevY = gestureData.lastOrNull()?.first?.y ?: 0.0f
        val time = System.currentTimeMillis()
        val trailDuration = prefs.glide.trailDuration + if (isPreviewMode) 500 else 0
        outer@ for (i in gestureData.size - 1 downTo 1) {
            if (time - gestureData[i - 1].second > trailDuration) break

            val dx = prevX - gestureData[i - 1].first.x
            val dy = prevY - gestureData[i - 1].first.y
            val dist = sqrt(dx * dx + dy * dy)

            val numPoints = (dist / targetDist).toInt()
            for (j in 0 until numPoints) {
                radius *= radiusReductionFactor
                val intermediateX =
                    gestureData[i].first.x * (1 - j.toFloat() / numPoints) + gestureData[i - 1].first.x * (j.toFloat() / numPoints)
                val intermediateY =
                    gestureData[i].first.y * (1 - j.toFloat() / numPoints) + gestureData[i - 1].first.y * (j.toFloat() / numPoints)
                canvas?.drawCircle(intermediateX, intermediateY, radius, glideTrailPaint)
                drawnPoints += 1
                prevX = intermediateX
                prevY = intermediateY
            }
        }
    }

    private fun initGlideClassifier(keyboard: TextKeyboard) {
        if (isSmartbarKeyboardView || isPreviewMode || keyboard.mode != KeyboardMode.CHARACTERS) {
            return
        }
        post {
            val keys = keyboard.keys().asSequence().toList()
            GlideTypingManager.getInstance().setLayout(keys)
        }
    }

    override fun onGlideAddPoint(point: GlideTypingGesture.Detector.Position) {
        if (prefs.glide.enabled || isPreviewMode) {
            glideDataForDrawing.add(Pair(point, System.currentTimeMillis()))
            if (glideRefreshJob == null) {
                glideRefreshJob = launch(Dispatchers.Main) {
                    while (true) {
                        invalidate()
                        delay(10)
                    }
                }
            }
        }
    }

    override fun onGlideComplete(data: GlideTypingGesture.Detector.PointerData) {
        onGlideCancelled()
    }

    override fun onGlideCancelled() {
        if (prefs.glide.showTrail) {
            fadingGlide.clear()
            fadingGlide.addAll(glideDataForDrawing)

            val animator = ValueAnimator.ofFloat(20.0f, 0.0f)
            animator.interpolator = AccelerateInterpolator()
            animator.duration = prefs.glide.trailDuration.toLong()
            animator.addUpdateListener {
                fadingGlideRadius = it.animatedValue as Float
                invalidate()
            }
            animator.start()

            glideDataForDrawing.clear()
            isGliding = false
            invalidate()
            glideRefreshJob?.cancel()
            glideRefreshJob = null
        }
    }

    private class TouchPointer : Pointer() {
        var initialKey: TextKey? = null
        var activeKey: TextKey? = null
        var longPressJob: Job? = null
        var hasTriggeredGestureMove: Boolean = false
        var shouldBlockNextUp: Boolean = false

        override fun reset() {
            super.reset()
            initialKey = null
            activeKey = null
            longPressJob?.cancel()
            longPressJob = null
            hasTriggeredGestureMove = false
            shouldBlockNextUp = false
        }

        override fun toString(): String {
            return "${TouchPointer::class.simpleName} { id=$id, index=$index, initialKey=$initialKey, activeKey=$activeKey }"
        }
    }

    fun setFont(font: Int?) {
        font ?: return
        keyboardTheme.keyFont = font
        handleKey { invalidate(it.key ?: return@handleKey) }
    }

    fun setKeyColor(color: String?) {
        color ?: return
        keyboardTheme.keyTextColor = color
        handleKey { invalidate(it.key ?: return@handleKey) }
    }

    fun setOpacity(percent: Int?) {
        percent ?: return
        keyboardTheme.opacity = percent
        handleKey {
            invalidate(it.key ?: return@handleKey)
        }
    }

    fun setStrokeCornerRadius(radius: Int?) {
        radius ?: return
        keyboardTheme.strokeRadius = radius
        handleKey {
            (it.background as? GradientDrawable)
                ?.cornerRadius = ViewUtils.dp2px(radius.toFloat())
            it.invalidate()
        }
    }

    fun setStrokeColor(strokeColor: String?) {
        if (strokeColor == keyboardTheme.strokeColor) return
        keyboardTheme.strokeColor = strokeColor
        handleKey { it.invalidate() }
    }

    fun setButtonColor(buttonColor: String?) {
        buttonColor ?: return
        keyboardTheme.buttonColor = buttonColor
        keyboardTheme.imeButtonColor =
            String.format("#%06X", 0xFFFFFF and getDarkerShade(buttonColor, 0.4f))
        keyboardTheme.buttonSecondaryColor =
            String.format("#%06X", 0xFFFFFF and getDarkerShade(buttonColor, 0.2f))
        setButtonColor(
            keyboardTheme.buttonColor,
            keyboardTheme.imeButtonColor,
            keyboardTheme.buttonSecondaryColor
        )
    }

    private fun setButtonColor(buttonColor: String, imeColor: String, secondaryKeyColor: String) {
        handleKey {
            val keyBackground = when {
                isEnterKey(it) -> imeColor
                isAddictionKey(it.key) -> secondaryKeyColor
                else -> buttonColor
            }
            (it.background as? GradientDrawable)?.setColor(Color.parseColor(keyBackground))
        }
    }

    fun isEnterKey(textKeyView: TextKeyView) = textKeyView.key?.computedData?.code == KeyCode.ENTER

    fun isAddictionKey(key: TextKey?): Boolean {

        if (key?.computedData?.code in listOf(
                KeyCode.DELETE,
                KeyCode.SHIFT,
                KeyCode.VIEW_NUMERIC_ADVANCED,
                KeyCode.VIEW_CHARACTERS,
                KeyCode.VIEW_SYMBOLS2,
                KeyCode.SHIFT_LOCK,
                KeyCode.PHONE_PAUSE,
                KeyCode.VIEW_SYMBOLS
            )
        ) return true
        if (isSpecialKey(key)) return true

        if (computedKeyboard?.mode != KeyboardMode.CHARACTERS)
            return key?.computedData?.code in listOf(46, 60, 62)

        return false
    }

    private fun isSpecialKey(key: TextKey?) = key?.computedData?.code in
        BottomRightCharacterRepository.SelectableCharacter
            .values()
            .map { it.code } && computedKeyboard?.mode == KeyboardMode.CHARACTERS && key?.computedData?.groupId == 2

    private fun getDarkerShade(color: String, factor: Float) =
        ColorUtils.blendARGB(Color.parseColor(color), Color.BLACK, factor)

    private fun handleKey(action: (TextKeyView) -> Unit) {
        computedKeyboard?.keys()?.withIndex()?.forEach { (n, _) ->
            getChildAt(n)?.let { rv -> if (rv is TextKeyView) action.invoke(rv) }
        }
    }

    fun activeGliding() {
        isGliding = true
    }

    fun deactiveGlideTyping() {
        isGliding = false
        glideRefreshJob?.cancel()
        glideRefreshJob = null
        invalidate()
    }

    private fun reDrawKeyboard() {
        if (isMeasured) {
            computeDesiredDimensions()
            computeKeyboard()
            invalidate()
        }
    }

    fun handleTheme(keyboardTheme: KeyboardTheme) {
        val buttonColor = keyboardTheme.buttonColor
        setButtonColor(buttonColor, keyboardTheme.imeButtonColor, keyboardTheme.buttonSecondaryColor)
        setKeyColor(keyboardTheme.keyTextColor)
        setStrokeCornerRadius(keyboardTheme.strokeRadius)
        handleKey { it.invalidate() }
    }

    fun getKeyboardTheme() = keyboardTheme

    fun onDrawStroke(canvas: Canvas, key: TextKey) {
        keyboardTheme.strokeColor ?: return
        val gradientDrawable = GradientDrawable()
        gradientDrawable.cornerRadius = ViewUtils.dp2px(keyboardTheme.strokeRadius.toFloat())
        gradientDrawable.setStroke(3, Color.parseColor(keyboardTheme.strokeColor))
        gradientDrawable.bounds.set(key.visibleBounds)
        gradientDrawable.draw(canvas)
    }

    fun findKeyView(label: String) = children
        .filterIsInstance<TextKeyView>()
        .firstOrNull { it.key?.label == label }

    fun findKeyViewByCode(code: Int) = children
        .filterIsInstance<TextKeyView>()
        .firstOrNull { it.key?.computedData?.code == code }

}
