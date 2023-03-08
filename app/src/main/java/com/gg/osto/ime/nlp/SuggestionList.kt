package com.gg.osto.ime.nlp

import com.gg.osto.common.NativeInstanceWrapper
import com.gg.osto.common.NativePtr

@JvmInline
value class SuggestionList private constructor(
    private val _nativePtr: NativePtr
) : Collection<String>, NativeInstanceWrapper {

    companion object {
        fun new(maxSize: Int): SuggestionList {
            val nativePtr = nativeInitialize(maxSize)
            return SuggestionList(nativePtr)
        }

        external fun nativeInitialize(maxSize: Int): NativePtr
        external fun nativeDispose(nativePtr: NativePtr)

        external fun nativeAdd(nativePtr: NativePtr, word: Word, freq: Freq): Boolean
        external fun nativeClear(nativePtr: NativePtr)
        external fun nativeContains(nativePtr: NativePtr, element: Word): Boolean
        external fun nativeGetOrNull(nativePtr: NativePtr, index: Int): Word?
        external fun nativeGetIsPrimaryTokenAutoInsert(nativePtr: NativePtr): Boolean
        external fun nativeSetIsPrimaryTokenAutoInsert(nativePtr: NativePtr, v: Boolean)
        external fun nativeSize(nativePtr: NativePtr): Int
    }

    override val size: Int
        get() = nativeSize(_nativePtr)

    fun add(word: Word, freq: Freq): Boolean {
        return nativeAdd(_nativePtr, word, freq)
    }

    fun clear() {
        nativeClear(_nativePtr)
    }

    override fun contains(element: Word): Boolean {
        return nativeContains(_nativePtr, element)
    }

    override fun containsAll(elements: Collection<Word>): Boolean {
        elements.forEach { if (!contains(it)) return false }
        return true
    }

    @Throws(IndexOutOfBoundsException::class)
    operator fun get(index: Int): Word {
        val element = getOrNull(index)
        if (element == null) {
            throw IndexOutOfBoundsException("The specified index $index is not within the bounds of this list!")
        } else {
            return element
        }
    }

    fun getOrNull(index: Int): Word? {
        return nativeGetOrNull(_nativePtr, index)
    }

    override fun isEmpty(): Boolean = size <= 0

    val isPrimaryTokenAutoInsert: Boolean
        get() = nativeGetIsPrimaryTokenAutoInsert(_nativePtr)

    override fun iterator(): Iterator<Word> {
        return SuggestionListIterator(this)
    }

    override fun nativePtr(): NativePtr {
        return _nativePtr
    }

    override fun dispose() {
        nativeDispose(_nativePtr)
    }

    class SuggestionListIterator internal constructor (
        private val suggestionList: SuggestionList
    ) : Iterator<Word> {
        var index = 0

        override fun next(): Word = suggestionList[index++]

        override fun hasNext(): Boolean = suggestionList.getOrNull(index) != null
    }
}
