package com.wisecloud.mdm.app.data.model

/** POST /api/applications/upload response data */
data class AppUploadResponse(
    val versionMD5: String?,
    val versionNumber: String?,
    val appName: String?,
    val packageName: String?
)

/** GET /api/applications response item */
data class ApplicationInfo(
    val id: Long?,
    val appName: String?,
    val appPackage: String?,
    val versionNumber: String?,
    val versionName: String?,
    val versionMd5: String?,
    val appAlias: String?,
    val appDescription: String?
)
