package com.wisecloud.mdm.app.ui.home

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.wisecloud.mdm.app.R
import com.wisecloud.mdm.app.data.model.DeviceSummary
import com.wisecloud.mdm.app.databinding.ItemDeviceSearchBinding

/**
 * RecyclerView adapter for device search results.
 * Displays SN, device name, and online status.
 * Clicking an item triggers [onDeviceClick] with the device SN.
 */
class DeviceSearchAdapter(
    private val onDeviceClick: (String) -> Unit
) : ListAdapter<DeviceSummary, DeviceSearchAdapter.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemDeviceSearchBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(
        private val binding: ItemDeviceSearchBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(device: DeviceSummary) {
            binding.tvDeviceSn.text = device.sn
            binding.tvDeviceName.text = device.deviceName ?: device.deviceType ?: ""

            // Status display: 1 = online (green), otherwise offline (gray)
            val isOnline = device.status == 1
            val context = binding.root.context
            if (isOnline) {
                binding.tvDeviceStatus.text = context.getString(R.string.status_online)
                binding.tvDeviceStatus.setTextColor(Color.parseColor("#4CAF50"))
            } else {
                binding.tvDeviceStatus.text = context.getString(R.string.status_offline)
                binding.tvDeviceStatus.setTextColor(Color.GRAY)
            }

            binding.root.setOnClickListener {
                onDeviceClick(device.sn)
            }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<DeviceSummary>() {
        override fun areItemsTheSame(oldItem: DeviceSummary, newItem: DeviceSummary): Boolean {
            return oldItem.sn == newItem.sn
        }

        override fun areContentsTheSame(oldItem: DeviceSummary, newItem: DeviceSummary): Boolean {
            return oldItem == newItem
        }
    }
}
