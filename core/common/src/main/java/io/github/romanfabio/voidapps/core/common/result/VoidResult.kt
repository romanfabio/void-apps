package io.github.romanfabio.voidapps.core.common.result

import kotlin.coroutines.cancellation.CancellationException

/**
 * Outcome of a one-shot data operation that can fail (docs/ARCHITECTURE.md §7).
 * Exceptions never cross the repository boundary; they are wrapped in [Failure].
 */
sealed interface VoidResult<out T> {
    data class Success<out T>(val value: T) : VoidResult<T>

    data class Failure(val error: Throwable) : VoidResult<Nothing>
}

/** Transforms the value of a [VoidResult.Success]; failures pass through unchanged. */
fun <T, R> VoidResult<T>.map(transform: (T) -> R): VoidResult<R> = when (this) {
    is VoidResult.Success -> VoidResult.Success(transform(value))
    is VoidResult.Failure -> this
}

/** Returns the value of a [VoidResult.Success] or null for a [VoidResult.Failure]. */
fun <T> VoidResult<T>.getOrNull(): T? = (this as? VoidResult.Success)?.value

/**
 * Runs [block] and wraps its outcome in a [VoidResult].
 * [CancellationException] is always rethrown so structured concurrency keeps working.
 */
@Suppress("TooGenericExceptionCaught")
suspend fun <T> runSuspendCatching(block: suspend () -> T): VoidResult<T> = try {
    VoidResult.Success(block())
} catch (e: CancellationException) {
    throw e
} catch (e: Exception) {
    VoidResult.Failure(e)
}
