package com.ioskey.iosboard.common

import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

inline fun <T> resultOk(value: () -> T): Result<T> {
    contract {
        callsInPlace(value, InvocationKind.EXACTLY_ONCE)
    }
    return Result.success(value())
}

inline fun <T> resultErr(error: () -> Throwable): Result<T> {
    contract {
        callsInPlace(error, InvocationKind.EXACTLY_ONCE)
    }
    return Result.failure(error())
}

inline fun <T> resultErrStr(error: () -> String): Result<T> {
    contract {
        callsInPlace(error, InvocationKind.EXACTLY_ONCE)
    }
    return Result.failure(Exception(error()))
}
