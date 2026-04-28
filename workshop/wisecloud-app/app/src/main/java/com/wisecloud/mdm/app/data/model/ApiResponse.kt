package com.wisecloud.mdm.app.data.model

/**
 * Unified API response wrapper matching backend ApiResponse<T>.
 */
data class ApiResponse<T>(
    val code: Int,
    val message: String,
    val data: T?
) {
    val isSuccess: Boolean get() = code == 200
}
