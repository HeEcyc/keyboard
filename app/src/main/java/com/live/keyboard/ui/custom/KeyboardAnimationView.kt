package com.live.keyboard.ui.custom

import com.live.keyboard.data.KeyboardTheme
import com.live.keyboard.ime.core.Subtype
import com.live.keyboard.ime.keyboard.ComputingEvaluator
import com.live.keyboard.ime.keyboard.DefaultComputingEvaluator
import com.live.keyboard.ime.keyboard.KeyData
import com.live.keyboard.ime.text.key.CurrencySet
import com.live.keyboard.ime.text.key.KeyCode
import com.live.keyboard.ime.text.keyboard.KeyboardMode
import com.live.keyboard.ime.text.keyboard.TextKeyData
import com.live.keyboard.ime.text.keyboard.TextKeyboardIconSet
import com.live.keyboard.ime.text.keyboard.TextKeyboardView
import com.live.keyboard.ime.text.layout.LayoutManager

interface KeyboardAnimationView {

    val textKeyboardView: TextKeyboardView

    suspend fun initKeyboard() {

        textKeyboardView.setIconSet(TextKeyboardIconSet.new(textKeyboardView.context))
        textKeyboardView.setComputingEvaluator(getEvalutor())
        textKeyboardView.sync()
        textKeyboardView.setComputedKeyboard(
            LayoutManager().computeKeyboardAsync(
                KeyboardMode.CHARACTERS,
                Subtype.DEFAULT
            ).await(), KeyboardTheme()
        )
    }

    fun getEvalutor() = object : ComputingEvaluator by DefaultComputingEvaluator {
        override fun evaluateVisible(data: KeyData): Boolean {
            return data.code != KeyCode.SWITCH_TO_MEDIA_CONTEXT
        }

        override fun isSlot(data: KeyData): Boolean {
            return CurrencySet.isCurrencySlot(data.code)
        }

        override fun getSlotData(data: KeyData): KeyData {
            return TextKeyData(label = ".")
        }
    }

    fun animateKeyboard() {
        prepareKeyboardAnimation()
        startKeyboardAnimation()
    }

    fun prepareKeyboardAnimation()

    fun startKeyboardAnimation()
}
