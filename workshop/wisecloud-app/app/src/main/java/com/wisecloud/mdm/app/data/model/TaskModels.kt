package com.wisecloud.mdm.app.data.model

/** POST /api/tasks/install request body */
data class InstallTaskRequest(
    val taskName: String,
    val versionMD5: String,
    val versionNumber: String,
    val versionName: String,
    val appName: String,
    val snList: List<String>
)

/** POST /api/tasks/uninstall request body */
data class UninstallTaskRequest(
    val pkgName: String,
    val snList: List<String>
)

/** POST /api/tasks/install and /api/tasks/uninstall response data */
data class TaskCreateResponse(
    val traceId: String?,
    val taskName: String?
)

/** GET /api/tasks response item */
data class TaskSummary(
    val traceId: String,
    val taskName: String?,
    val taskType: String?,
    val targetApp: String?,
    val deviceCount: Int?,
    val completedCount: Int?,
    val failedCount: Int?,
    val progress: String?,
    val createdAt: String?
)

/** GET /api/tasks/{traceId}/details response data */
data class TaskDetailResponse(
    val completed: List<TaskDeviceStatus>,
    val failed: List<TaskDeviceStatus>,
    val executing: List<TaskDeviceStatus>,
    val preparing: List<TaskDeviceStatus>
) {
    fun isAllTerminal(): Boolean = executing.isEmpty() && preparing.isEmpty()
}

data class TaskDeviceStatus(
    val sn: String,
    val instructionStatus: Int,
    val executeCode: String?,
    val executeMessage: String?
)
