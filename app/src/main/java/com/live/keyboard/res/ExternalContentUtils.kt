package com.live.keyboard.res

import android.content.Context
import android.net.Uri
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.BufferedReader
import java.io.BufferedWriter
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

class ExternalContentUtils private constructor() {
    companion object {
        inline fun <R> readFromUri(context: Context, uri: Uri, maxSize: Int, block: (it: BufferedInputStream) -> R): Result<R> {
            contract {
                callsInPlace(block, InvocationKind.AT_MOST_ONCE)
            }
            return runCatching {
                val contentResolver = context.contentResolver
                    ?: throw NullPointerException("System content resolver not available")
                val inputStream = contentResolver.openInputStream(uri)
                    ?: throw NullPointerException("Cannot open input stream for given uri '$uri'")
                val assetFileDescriptor = contentResolver.openAssetFileDescriptor(uri, "r")
                    ?: throw NullPointerException("Cannot open asset file descriptor for given uri '$uri'")
                if (assetFileDescriptor.length > maxSize) {
                    throw Exception("Contents of given uri '$uri' exceeds maximum size of $maxSize bytes!")
                }
                inputStream.buffered().use { block(it) }
            }
        }

        inline fun <R> readTextFromUri(context: Context, uri: Uri, maxSize: Int, block: (it: BufferedReader) -> R): Result<R> {
            contract {
                callsInPlace(block, InvocationKind.AT_MOST_ONCE)
            }
            return runCatching {
                val contentResolver = context.contentResolver
                    ?: throw NullPointerException("System content resolver not available")
                val inputStream = contentResolver.openInputStream(uri)
                    ?: throw NullPointerException("Cannot open input stream for given uri '$uri'")
                val assetFileDescriptor = contentResolver.openAssetFileDescriptor(uri, "r")
                    ?: throw NullPointerException("Cannot open asset file descriptor for given uri '$uri'")
                if (assetFileDescriptor.length > maxSize) {
                    throw Exception("Contents of given uri '$uri' exceeds maximum size of $maxSize bytes!")
                }
                inputStream.bufferedReader(Charsets.UTF_8).use { block(it) }
            }
        }

        fun readAllTextFromUri(context: Context, uri: Uri, maxSize: Int): Result<String> {
            return readTextFromUri(context, uri, maxSize) { it.readText() }
        }

        inline fun writeToUri(context: Context, uri: Uri, block: (it: BufferedOutputStream) -> Unit): Result<Unit> {
            contract {
                callsInPlace(block, InvocationKind.AT_MOST_ONCE)
            }
            return runCatching {
                val contentResolver = context.contentResolver
                    ?: throw NullPointerException("System content resolver not available")
                // Must use "rwt" mode to ensure destination file length is truncated after writing.
                val outputStream = contentResolver.openOutputStream(uri, "rwt")
                    ?: throw NullPointerException("Cannot open output stream for given uri '$uri'")
                outputStream.buffered().use { block(it) }
            }
        }

        inline fun writeTextToUri(context: Context, uri: Uri, block: (it: BufferedWriter) -> Unit): Result<Unit> {
            contract {
                callsInPlace(block, InvocationKind.AT_MOST_ONCE)
            }
            return runCatching {
                val contentResolver = context.contentResolver
                    ?: throw NullPointerException("System content resolver not available")
                // Must use "rwt" mode to ensure destination file length is truncated after writing.
                val outputStream = contentResolver.openOutputStream(uri, "rwt")
                    ?: throw NullPointerException("Cannot open output stream for given uri '$uri'")
                outputStream.bufferedWriter(Charsets.UTF_8).use { block(it) }
            }
        }

        fun writeAllTextToUri(context: Context, uri: Uri, text: String): Result<Unit> {
            return writeTextToUri(context, uri) { it.write(text) }
        }
    }
}
