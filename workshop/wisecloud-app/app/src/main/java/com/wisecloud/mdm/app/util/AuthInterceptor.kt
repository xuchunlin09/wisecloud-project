package com.wisecloud.mdm.app.util

import okhttp3.Interceptor
import okhttp3.Response

/**
 * OkHttp Interceptor that automatically attaches the JWT Bearer token
 * to outgoing requests and handles 401 responses by clearing the token
 * and posting a TokenExpiredEvent.
 */
class AuthInterceptor(private val tokenManager: TokenManager) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()

        tokenManager.getToken()?.let { token ->
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }

        val response = chain.proceed(requestBuilder.build())

        if (response.code == 401) {
            tokenManager.clearToken()
            TokenExpiredEvent.post()
        }

        return response
    }
}
