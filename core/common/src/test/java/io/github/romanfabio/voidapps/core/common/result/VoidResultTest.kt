package io.github.romanfabio.voidapps.core.common.result

import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.coroutines.cancellation.CancellationException
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertNull

class VoidResultTest {

    @Test
    fun `runSuspendCatching wraps a returned value in success`() = runTest {
        val result = runSuspendCatching { 42 }

        assertEquals(VoidResult.Success(42), result)
    }

    @Test
    fun `runSuspendCatching wraps a thrown exception in failure`() = runTest {
        val error = IllegalStateException("boom")

        val result = runSuspendCatching { throw error }

        assertEquals(VoidResult.Failure(error), result)
    }

    @Test
    fun `runSuspendCatching rethrows cancellation`() = runTest {
        assertFailsWith<CancellationException> {
            runSuspendCatching { throw CancellationException("cancelled") }
        }
    }

    @Test
    fun `map transforms success and keeps failure`() {
        val failure = VoidResult.Failure(IllegalStateException())

        assertEquals(VoidResult.Success("2"), VoidResult.Success(2).map { it.toString() })
        assertIs<VoidResult.Failure>(failure.map { it.toString() })
    }

    @Test
    fun `getOrNull returns value for success and null for failure`() {
        assertEquals(1, VoidResult.Success(1).getOrNull())
        assertNull(VoidResult.Failure(IllegalStateException()).getOrNull())
    }
}
