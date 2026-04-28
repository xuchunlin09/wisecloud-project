package com.wisecloud.mdm.app.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.wisecloud.mdm.app.MdmApplication
import com.wisecloud.mdm.app.data.api.RetrofitClient
import com.wisecloud.mdm.app.data.model.DeviceOverviewResponse
import com.wisecloud.mdm.app.data.model.DeviceSummary
import kotlinx.coroutines.launch

/**
 * ViewModel for HomeFragment.
 * Loads device overview on init and handles SN search.
 */
class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val tokenManager = (application as MdmApplication).tokenManager
    private val apiService = RetrofitClient.getInstance(tokenManager)

    // ── Overview state ──

    private val _overview = MutableLiveData<DeviceOverviewResponse?>()
    val overview: LiveData<DeviceOverviewResponse?> = _overview

    private val _overviewLoading = MutableLiveData(false)
    val overviewLoading: LiveData<Boolean> = _overviewLoading

    private val _overviewError = MutableLiveData<String?>()
    val overviewError: LiveData<String?> = _overviewError

    // ── Search state ──

    private val _searchResults = MutableLiveData<List<DeviceSummary>>()
    val searchResults: LiveData<List<DeviceSummary>> = _searchResults

    private val _searchLoading = MutableLiveData(false)
    val searchLoading: LiveData<Boolean> = _searchLoading

    private val _searchError = MutableLiveData<String?>()
    val searchError: LiveData<String?> = _searchError

    init {
        loadOverview()
    }

    fun loadOverview() {
        _overviewLoading.value = true
        _overviewError.value = null
        viewModelScope.launch {
            try {
                val response = apiService.getOverview()
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null && body.isSuccess) {
                        _overview.value = body.data
                    } else {
                        _overviewError.value = body?.message ?: "加载概览失败"
                    }
                } else {
                    _overviewError.value = "加载概览失败"
                }
            } catch (e: Exception) {
                _overviewError.value = "网络连接失败，请检查网络设置"
            } finally {
                _overviewLoading.value = false
            }
        }
    }

    fun searchDevices(keyword: String) {
        if (keyword.isBlank()) {
            _searchResults.value = emptyList()
            return
        }
        _searchLoading.value = true
        _searchError.value = null
        viewModelScope.launch {
            try {
                val response = apiService.searchDevices(keyword)
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null && body.isSuccess) {
                        _searchResults.value = body.data ?: emptyList()
                    } else {
                        _searchError.value = body?.message ?: "搜索失败"
                        _searchResults.value = emptyList()
                    }
                } else {
                    _searchError.value = "搜索失败"
                    _searchResults.value = emptyList()
                }
            } catch (e: Exception) {
                _searchError.value = "网络连接失败，请检查网络设置"
                _searchResults.value = emptyList()
            } finally {
                _searchLoading.value = false
            }
        }
    }
}
