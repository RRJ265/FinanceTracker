package com.sample.financetracker.core.common

import android.database.SQLException
import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException

sealed class AppError(message: String, cause: Throwable? = null) : Exception(message, cause) {
    class Validation(message: String) : AppError(message)
    class Storage(cause: Throwable) : AppError("We couldn't access your data. Please try again.", cause)
    class Unknown(cause: Throwable) : AppError("Something went wrong. Please try again.", cause)
}

fun Throwable.toAppError(): AppError = when (this) {
    is AppError -> this
    is SQLException, is IOException -> AppError.Storage(this)
    else -> AppError.Unknown(this)
}

inline fun <T> suspendRunCatching(block: () -> T): Result<T> = try {
    Result.success(block())
} catch (e: CancellationException) {
    throw e
} catch (e: Throwable) {
    Result.failure(e.toAppError())
}
