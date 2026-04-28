package com.wisecloud.mdm.app.ui.device

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.wisecloud.mdm.app.MdmApplication
import com.wisecloud.mdm.app.data.api.RetrofitClient
import com.wisecloud.mdm.app.data.model.DeviceDetailResponse
import kotlinx.coroutines.launch

/**
 * ViewModel for DeviceDetailFragment.
 * Loads device detail by SN from the backend API.
 */
class DeviceDetailViewModel(application: Application) : AndroidViewModel(application) {

    private val tokenManager = (application as MdmApplication).tokenManager
    private val apiService = RetrofitClient.getInstance(tokenManager)

    private val _deviceDetail = MutableLiveData<DeviceDetailResponse?>()
    val deviceDetail: LiveData<DeviceDetailResponse?> = _deviceDetail

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun loadDeviceDetail(sn: String) {
        _loading.value = true
        _error.value = null
        viewModelScope.launch {
            try {
                val response = apiService.getDeviceDetail(sn)
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null && body.isSuccess) {
                        _deviceDetail.value = body.data
                    } else {
                        _error.value = body?.message ?: "加载设备详情失败"
                    }
                } else {
                    _error.value = "加载设备详情失败"
                }
            } catch (e: Exception) {
                _error.value = "网络连接失败，请检查网络设置"
            } finally {
                _loading.value = false
            }
        }
    }
}
