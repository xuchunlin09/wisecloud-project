package com.wisecloud.mdm.app.ui.device

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.wisecloud.mdm.app.data.model.InstalledApp
import com.wisecloud.mdm.app.databinding.ItemInstalledAppBinding

/**
 * RecyclerView adapter for the installed apps list on the device detail page.
 * Displays app name, package name, and version.
 */
class InstalledAppAdapter :
    ListAdapter<InstalledApp, InstalledAppAdapter.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemInstalledAppBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(
        private val binding: ItemInstalledAppBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(app: InstalledApp) {
            binding.tvAppName.text = app.appName ?: ""
            binding.tvAppPackage.text = app.appPackage ?: ""
            binding.tvAppVersion.text = app.versionName ?: ""
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<InstalledApp>() {
        override fun areItemsTheSame(oldItem: InstalledApp, newItem: InstalledApp): Boolean {
            return oldItem.appPackage == newItem.appPackage
        }

        override fun areContentsTheSame(oldItem: InstalledApp, newItem: InstalledApp): Boolean {
            return oldItem == newItem
        }
    }
}
