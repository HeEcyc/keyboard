package com.neonkeyboard.cool.ime.keyboard

import com.neonkeyboard.cool.ime.core.Subtype
import com.neonkeyboard.cool.ime.text.key.*

interface ComputingEvaluator {
    fun evaluateCaps(): Boolean

    fun evaluateCaps(data: KeyData): Boolean

    fun evaluateCharHalfWidth(): Boolean = false

    fun evaluateKanaKata(): Boolean = false

    fun evaluateKanaSmall(): Boolean = false

    fun evaluateEnabled(data: KeyData): Boolean

    fun evaluateVisible(data: KeyData): Boolean

    fun getActiveSubtype(): Subtype

    fun getKeyVariation(): KeyVariation

    fun getKeyboard(): Keyboard

    fun isSlot(data: KeyData): Boolean

    fun getSlotData(data: KeyData): KeyData?
}

object DefaultComputingEvaluator : ComputingEvaluator {
    override fun evaluateCaps(): Boolean = false

    override fun evaluateCaps(data: KeyData): Boolean = false

    override fun evaluateCharHalfWidth(): Boolean = false

    override fun evaluateKanaKata(): Boolean = false

    override fun evaluateKanaSmall(): Boolean = false

    override fun evaluateEnabled(data: KeyData): Boolean = true

    override fun evaluateVisible(data: KeyData): Boolean = true

    override fun getActiveSubtype(): Subtype = Subtype.DEFAULT

    override fun getKeyVariation(): KeyVariation = KeyVariation.NORMAL

    override fun getKeyboard(): Keyboard = throw NotImplementedError()

    override fun isSlot(data: KeyData): Boolean = false

    override fun getSlotData(data: KeyData): KeyData? = null
}
