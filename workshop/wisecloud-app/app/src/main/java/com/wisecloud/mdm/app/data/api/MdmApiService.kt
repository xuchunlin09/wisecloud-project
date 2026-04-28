package com.wisecloud.mdm.app.data.api

import com.wisecloud.mdm.app.data.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

/**
 * Retrofit interface defining all backend API endpoints.
 */
interface MdmApiService {

    // ── Auth ──

    @POST("/api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<ApiResponse<Void>>

    @POST("/api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<ApiResponse<LoginResponse>>

    // ── Devices ──

    @POST("/api/devices/import")
    suspend fun importDevices(@Body request: DeviceImportRequest): Response<ApiResponse<DeviceImportResponse>>

    @GET("/api/devices/overview")
    suspend fun getOverview(): Response<ApiResponse<DeviceOverviewResponse>>

    @GET("/api/devices/search")
    suspend fun searchDevices(@Query("keyword") keyword: String): Response<ApiResponse<List<DeviceSummary>>>

    @GET("/api/devices/{sn}/detail")
    suspend fun getDeviceDetail(@Path("sn") sn: String): Response<ApiResponse<DeviceDetailResponse>>

    // ── Applications ──

    @Multipart
    @POST("/api/applications/upload")
    suspend fun uploadApk(
        @Part file: MultipartBody.Part,
        @Part("appAlias") appAlias: RequestBody,
        @Part("appDescription") appDescription: RequestBody?
    ): Response<ApiResponse<AppUploadResponse>>

    @GET("/api/applications")
    suspend fun listApplications(): Response<ApiResponse<List<ApplicationInfo>>>

    // ── Tasks ──

    @POST("/api/tasks/install")
    suspend fun createInstallTask(@Body request: InstallTaskRequest): Response<ApiResponse<TaskCreateResponse>>

    @POST("/api/tasks/uninstall")
    suspend fun createUninstallTask(@Body request: UninstallTaskRequest): Response<ApiResponse<TaskCreateResponse>>

    @GET("/api/tasks")
    suspend fun listTasks(): Response<ApiResponse<List<TaskSummary>>>

    @GET("/api/tasks/{traceId}/details")
    suspend fun getTaskDetails(@Path("traceId") traceId: String): Response<ApiResponse<TaskDetailResponse>>
}
