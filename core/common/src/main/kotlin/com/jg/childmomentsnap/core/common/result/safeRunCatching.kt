package com.jg.childmomentsnap.core.common.result

import kotlin.coroutines.cancellation.CancellationException

inline fun <T> safeRunCatching(block: () -> T): Result<T> {
    return try {
        Result.success(block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Result.failure(e)
    }
}