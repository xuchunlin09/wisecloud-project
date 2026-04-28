package com.wisecloud.mdm.app.data.model

/** POST /api/devices/import request body */
data class DeviceImportRequest(
    val snList: List<String>
)

/** POST /api/devices/import response data */
data class DeviceImportResponse(
    val successCount: Int,
    val failCount: Int,
    val successList: List<String>,
    val failList: List<SnFailInfo>
)

data class SnFailInfo(
    val sn: String,
    val reason: String
)

/** GET /api/devices/overview response data */
data class DeviceOverviewResponse(
    val totalCount: Int,
    val onlineCount: Int,
    val onlineRate: String
)

/** GET /api/devices/search response item */
data class DeviceSummary(
    val sn: String,
    val deviceName: String?,
    val deviceType: String?,
    val status: Int?
)

/** GET /api/devices/{sn}/detail response data */
data class DeviceDetailResponse(
    val sn: String,
    val deviceName: String?,
    val otaVersionName: String?,
    val electricRate: Int?,
    val onlineStatus: Int?,
    val wifiStatus: Int?,
    val gprsStatus: Int?,
    val longitude: String?,
    val latitude: String?,
    val installApps: List<InstalledApp>?
)

data class InstalledApp(
    val appName: String?,
    val appPackage: String?,
    val versionName: String?
)
