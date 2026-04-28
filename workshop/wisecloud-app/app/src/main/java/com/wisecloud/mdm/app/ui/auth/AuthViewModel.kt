package com.wisecloud.mdm.app.ui.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.wisecloud.mdm.app.MdmApplication
import com.wisecloud.mdm.app.data.api.RetrofitClient
import com.wisecloud.mdm.app.data.model.LoginRequest
import com.wisecloud.mdm.app.data.model.RegisterRequest
import kotlinx.coroutines.launch

/**
 * ViewModel handling login and register logic.
 * Calls MdmApiService via RetrofitClient and manages UI state.
 */
class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val tokenManager = (application as MdmApplication).tokenManager
    private val apiService = RetrofitClient.getInstance(tokenManager)

    // ── Login state ──

    private val _loginResult = MutableLiveData<AuthResult>()
    val loginResult: LiveData<AuthResult> = _loginResult

    // ── Register state ──

    private val _registerResult = MutableLiveData<AuthResult>()
    val registerResult: LiveData<AuthResult> = _registerResult

    // ── Loading state ──

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    /**
     * Perform login. On success, saves JWT token via TokenManager.
     */
    fun login(username: String, password: String) {
        if (username.isBlank() || password.isBlank()) {
            _loginResult.value = AuthResult.Error("请填写所有字段")
            return
        }

        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = apiService.login(LoginRequest(username, password))
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null && body.isSuccess && body.data != null) {
                        tokenManager.saveToken(body.data.token)
                        _loginResult.value = AuthResult.Success
                    } else {
                        _loginResult.value = AuthResult.Error(
                            body?.message ?: "登录失败"
                        )
                    }
                } else {
                    val errorMsg = parseErrorMessage(response.errorBody()?.string())
                    _loginResult.value = AuthResult.Error(errorMsg)
                }
            } catch (e: Exception) {
                _loginResult.value = AuthResult.Error("网络连接失败，请检查网络设置")
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Validate password match locally, then call register API.
     * Returns false immediately if passwords don't match (no network request).
     */
    fun register(username: String, email: String, password: String, confirmPassword: String): Boolean {
        if (username.isBlank() || email.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
            _registerResult.value = AuthResult.Error("请填写所有字段")
            return false
        }

        if (password != confirmPassword) {
            _registerResult.value = AuthResult.PasswordMismatch
            return false
        }

        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = apiService.register(RegisterRequest(username, email, password))
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null && body.isSuccess) {
                        _registerResult.value = AuthResult.Success
                    } else {
                        _registerResult.value = AuthResult.Error(
                            body?.message ?: "注册失败"
                        )
                    }
                } else {
                    val errorMsg = parseErrorMessage(response.errorBody()?.string())
                    _registerResult.value = AuthResult.Error(errorMsg)
                }
            } catch (e: Exception) {
                _registerResult.value = AuthResult.Error("网络连接失败，请检查网络设置")
            } finally {
                _isLoading.value = false
            }
        }
        return true
    }

    /**
     * Try to parse error message from JSON error body, fallback to generic message.
     */
    private fun parseErrorMessage(errorBody: String?): String {
        if (errorBody.isNullOrBlank()) return "请求失败"
        return try {
            val gson = com.google.gson.Gson()
            val errorResponse = gson.fromJson(errorBody, com.wisecloud.mdm.app.data.model.ApiResponse::class.java)
            errorResponse?.message ?: "请求失败"
        } catch (e: Exception) {
            "请求失败"
        }
    }
}

/** Sealed class representing authentication operation results. */
sealed class AuthResult {
    object Success : AuthResult()
    object PasswordMismatch : AuthResult()
    data class Error(val message: String) : AuthResult()
}
