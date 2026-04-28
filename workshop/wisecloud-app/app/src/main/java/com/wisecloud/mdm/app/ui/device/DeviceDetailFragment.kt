package com.wisecloud.mdm.app.ui.device

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.wisecloud.mdm.app.R
import com.wisecloud.mdm.app.data.model.DeviceDetailResponse
import com.wisecloud.mdm.app.databinding.FragmentDeviceDetailBinding

/**
 * Displays detailed information for a single device identified by SN.
 * Shows device model, ROM version, battery, online status (green/gray tag),
 * WiFi status, mobile network status, location, and installed apps list.
 */
class DeviceDetailFragment : Fragment() {

    private var _binding: FragmentDeviceDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DeviceDetailViewModel by viewModels()
    private lateinit var installedAppAdapter: InstalledAppAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDeviceDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeViewModel()

        val sn = arguments?.getString("sn") ?: return
        viewModel.loadDeviceDetail(sn)
    }

    private fun setupRecyclerView() {
        installedAppAdapter = InstalledAppAdapter()
        binding.rvInstalledApps.adapter = installedAppAdapter
    }

    private fun observeViewModel() {
        viewModel.loading.observe(viewLifecycleOwner) { loading ->
            binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
            binding.scrollView.visibility = if (loading) View.GONE else View.VISIBLE
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            if (error != null) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.deviceDetail.observe(viewLifecycleOwner) { detail ->
            if (detail != null) {
                bindDeviceDetail(detail)
            }
        }
    }

    private fun bindDeviceDetail(detail: DeviceDetailResponse) {
        binding.tvDeviceName.text = detail.deviceName ?: getString(R.string.detail_unknown)
        binding.tvSn.text = detail.sn
        binding.tvRomVersion.text = detail.otaVersionName ?: getString(R.string.detail_unknown)
        binding.tvBattery.text = if (detail.electricRate != null) "${detail.electricRate}%" else getString(R.string.detail_unknown)

        // Online status tag: 1 = online (green), else offline (gray)
        val isOnline = detail.onlineStatus == 1
        val statusText = if (isOnline) getString(R.string.status_online) else getString(R.string.status_offline)
        val statusColor = if (isOnline) Color.parseColor("#4CAF50") else Color.GRAY
        binding.tvOnlineStatus.text = statusText
        val bg = GradientDrawable().apply {
            setColor(statusColor)
            cornerRadius = 12f
        }
        binding.tvOnlineStatus.background = bg
        binding.tvOnlineStatus.setTextColor(Color.WHITE)

        // WiFi status
        binding.tvWifiStatus.text = formatConnectionStatus(detail.wifiStatus)

        // Mobile network (GPRS) status
        binding.tvGprsStatus.text = formatConnectionStatus(detail.gprsStatus)

        // Location
        val lon = detail.longitude
        val lat = detail.latitude
        binding.tvLocation.text = if (!lon.isNullOrBlank() && !lat.isNullOrBlank()) {
            "$lon, $lat"
        } else {
            getString(R.string.detail_unknown)
        }

        // Installed apps
        val apps = detail.installApps
        if (apps.isNullOrEmpty()) {
            binding.tvNoApps.visibility = View.VISIBLE
            binding.rvInstalledApps.visibility = View.GONE
        } else {
            binding.tvNoApps.visibility = View.GONE
            binding.rvInstalledApps.visibility = View.VISIBLE
            installedAppAdapter.submitList(apps)
        }
    }

    private fun formatConnectionStatus(status: Int?): String {
        return when (status) {
            1 -> getString(R.string.detail_connected)
            else -> getString(R.string.detail_disconnected)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
