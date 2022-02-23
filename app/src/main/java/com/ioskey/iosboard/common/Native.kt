package com.ioskey.iosboard.common

import java.nio.ByteBuffer

/**
 * Type alias for a native pointer.
 */
typealias NativePtr = Long

/**
 * Constant value for a native null pointer.
 */
const val NATIVE_NULLPTR: NativePtr = 0L

/**
 * Type alias for a native string in standard UTF-8 encoding.
 */
typealias NativeStr = ByteBuffer

/**
 * Converts a native string to a Java string.
 */
fun NativeStr.toJavaString(): String {
    val bytes: ByteArray
    if (this.hasArray()) {
        bytes = this.array()
    } else {
        bytes = ByteArray(this.remaining())
        this.get(bytes)
    }
    return String(bytes, Charsets.UTF_8)
}

/**
 * Converts a Java string to a native string.
 */
fun String.toNativeStr(): NativeStr {
    val bytes = this.toByteArray(Charsets.UTF_8)
    val buffer = ByteBuffer.allocateDirect(bytes.size)
    buffer.put(bytes)
    buffer.rewind()
    return buffer
}

/**
 * Generic interface for a native instance object. Defines the basic
 * methods which each native instance wrapper should define and be able
 * to handle to.
 */
interface NativeInstanceWrapper {
    /**
     * Returns the native pointer of this instance. The returned pointer
     * is only valid if [dispose] has not been previously called.
     *
     * @return The native null pointer for this instance.
     */
    fun nativePtr(): NativePtr

    /**
     * Deletes the native object and frees allocated resources. After
     * invoking this method one MUST NOT touch this instance ever again.
     */
    fun dispose()
}
