package com.wisecloud.mdm.app.data.api

import com.wisecloud.mdm.app.util.AuthInterceptor
import com.wisecloud.mdm.app.util.TokenManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Singleton Retrofit client configured with:
 * - Base URL (configurable)
 * - OkHttp client with AuthInterceptor for automatic JWT token attachment
 * - Gson converter for JSON serialization/deserialization
 * - HTTP logging for debug builds
 */
object RetrofitClient {

    private const val DEFAULT_BASE_URL = "http://10.0.2.2:8080"
    private const val CONNECT_TIMEOUT_SECONDS = 30L
    private const val READ_TIMEOUT_SECONDS = 30L
    private const val WRITE_TIMEOUT_SECONDS = 60L

    @Volatile
    private var apiService: MdmApiService? = null

    @Volatile
    private var baseUrl: String = DEFAULT_BASE_URL

    fun setBaseUrl(url: String) {
        baseUrl = url
        apiService = null // Force re-creation on next access
    }

    fun getInstance(tokenManager: TokenManager): MdmApiService {
        return apiService ?: synchronized(this) {
            apiService ?: createApiService(tokenManager).also { apiService = it }
        }
    }

    private fun createApiService(tokenManager: TokenManager): MdmApiService {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(tokenManager))
            .addInterceptor(loggingInterceptor)
            .connectTimeout(CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(MdmApiService::class.java)
    }
}
