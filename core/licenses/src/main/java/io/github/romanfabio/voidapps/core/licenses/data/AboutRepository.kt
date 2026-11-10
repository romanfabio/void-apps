package io.github.romanfabio.voidapps.core.licenses.data

import androidx.annotation.RawRes
import io.github.romanfabio.voidapps.core.common.result.VoidResult

interface AboutRepository {
    /** Loads app identity, the MIT license text and the dependency-license JSON [librariesRes]. */
    suspend fun load(@RawRes librariesRes: Int): VoidResult<AboutInfo>
}
