package com.live.keyboard.ime.text.keyboard

import androidx.collection.SparseArrayCompat
import androidx.collection.set
import com.live.keyboard.ime.core.Subtype
import kotlinx.coroutines.*
import java.util.*
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Helper class for caching computed text keyboards. Done to reduce the CPU strain and to reuse already allocated
 * objects. The cache stores the deferred results of the keyboard, meaning that it can contain keyboards which haven't
 * even finished computing. When getting a keyboard, the caller must always await the result thus. Additionally several
 * clear methods are provided to clear the cache either partially or completely.
 *
 * @param ioDispatcher The dispatcher for the IO channel. Defaults to [Dispatchers.IO].
 */
@Suppress("MemberVisibilityCanBePrivate")
class TextKeyboardCache(ioDispatcher: CoroutineDispatcher = Dispatchers.IO) {
    private val cache: EnumMap<KeyboardMode, SparseArrayCompat<Deferred<TextKeyboard>>> = EnumMap(KeyboardMode::class.java)
    private val scope: CoroutineScope = CoroutineScope(ioDispatcher + SupervisorJob())

    init {
        // Initialize all odes with an empty array. As we won't remove the arrays anymore (only clear them), any
        // get operations on `cache` will be automatically assumed to be not null.
        for (mode in KeyboardMode.values()) {
            cache[mode] = SparseArrayCompat()
        }
    }

    /**
     * Clears all computed keyboards for all modes and all subtypes from this cache.
     */
    fun clear() {
        for (mode in KeyboardMode.values()) {
            cache[mode]!!.clear()
        }
    }

    /**
     * Clears all computed keyboards associated with given [mode].
     *
     * @param mode The keyboard mode for which the computed keyboards should be cleared from the cache.
     */
    fun clear(mode: KeyboardMode) {
        cache[mode]!!.clear()
    }

    /**
     * Clears all computed keyboards associated with given [subtype].
     *
     * @param subtype The subtype for which the computed keyboards should be cleared from the cache.
     */
    fun clear(subtype: Subtype) {
        for (mode in KeyboardMode.values()) {
            cache[mode]!!.remove(subtype.hashCode())
        }
    }

    /**
     * Clears all computed keyboards associated with given [mode] _and_ [subtype].
     *
     * @param mode The keyboard mode for which the computed keyboards should be cleared from the cache.*
     * @param subtype The subtype for which the computed keyboards should be cleared from the cache.
     */
    fun clear(mode: KeyboardMode, subtype: Subtype) {
        cache[mode]!!.remove(subtype.hashCode())
    }

    /**
     * Performs a get operation on the cache to retrieve the computed keyboard for given [mode] _and_ [subtype].
     *
     * @param mode The mode of the computed keyboard to get.
     * @param subtype The subtype of the computed keyboard to get.
     *
     * @return The deferred computed keyboard or null if the cache does not have an entry associated with the given
     *  params.
     */
    fun getAsync(mode: KeyboardMode, subtype: Subtype): Deferred<TextKeyboard>? {
        return cache[mode]!![subtype.hashCode()].also {
        }
    }

    /**
     * Performs a get operation on the cache to retrieve the computed keyboard for given [mode] _and_ [subtype]. If no
     * entry for the given params can be found, [block] will be called and the deferred result of this block will
     * instantly be put into the cache and returned.
     *
     * @param mode The mode of the computed keyboard to get.
     * @param subtype The subtype of the computed keyboard to get.
     * @param block The lambda expression which is invoked to provide a fallback computed keyboard.
     *
     * @return The deferred computed keyboard either from the cache or from [block].
     */
    fun getOrElseAsync(mode: KeyboardMode, subtype: Subtype, block: suspend () -> TextKeyboard): Deferred<TextKeyboard> {
        contract {
            callsInPlace(block, InvocationKind.AT_MOST_ONCE)
        }
        val cachedKeyboard = getAsync(mode, subtype)
        return if (cachedKeyboard != null) {
            cachedKeyboard
        } else {
            val keyboard = scope.async { block() }
            set(mode, subtype, keyboard)
            keyboard
        }
    }

    /**
     * Performs a set operation on the cache and assigns [keyboard] to the given params [mode] and [subtype].
     *
     * @param mode The mode of the computed keyboard to set.
     * @param subtype The subtype of the computed keyboard to set.
     * @param keyboard The deferred computed keyboard to set for the given params.
     */
    fun set(mode: KeyboardMode, subtype: Subtype, keyboard: Deferred<TextKeyboard>) {
        cache[mode]!![subtype.hashCode()] = keyboard
    }
}
