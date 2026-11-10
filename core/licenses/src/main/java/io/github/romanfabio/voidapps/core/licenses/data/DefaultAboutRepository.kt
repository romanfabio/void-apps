package io.github.romanfabio.voidapps.core.licenses.data

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.pm.PackageInfoCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.romanfabio.voidapps.core.common.dispatcher.Dispatcher
import io.github.romanfabio.voidapps.core.common.dispatcher.VoidDispatchers
import io.github.romanfabio.voidapps.core.common.result.VoidResult
import io.github.romanfabio.voidapps.core.common.result.runSuspendCatching
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class DefaultAboutRepository @Inject constructor(
    @param:ApplicationContext private val context: Context,
    @param:Dispatcher(VoidDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
) : AboutRepository {

    override suspend fun load(librariesRes: Int): VoidResult<AboutInfo> = runSuspendCatching {
        withContext(ioDispatcher) {
            val packageInfo = context.packageManager.packageInfo(context.packageName)
            AboutInfo(
                appName = context.applicationInfo.loadLabel(context.packageManager).toString(),
                versionName = packageInfo.versionName.orEmpty(),
                versionCode = PackageInfoCompat.getLongVersionCode(packageInfo),
                licenseText = context.assets.open(LICENSE_ASSET).bufferedReader().use {
                    it.readText()
                }.reflowParagraphs(),
                librariesJson = context.resources.openRawResource(librariesRes).bufferedReader().use { it.readText() },
            )
        }
    }

    private fun PackageManager.packageInfo(packageName: String): PackageInfo =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0))
        } else {
            @Suppress("DEPRECATION")
            getPackageInfo(packageName, 0)
        }

    private companion object {
        /** Copied from the repository LICENSE at build time (see build.gradle.kts). */
        const val LICENSE_ASSET = "void_license.txt"
    }
}
