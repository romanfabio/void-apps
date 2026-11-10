package io.github.romanfabio.voidapps.core.licenses.data

/** Everything the About & licenses screen shows, loaded from the installed app. */
data class AboutInfo(
    val appName: String,
    val versionName: String,
    val versionCode: Long,
    val licenseText: String,
    val librariesJson: String,
)
