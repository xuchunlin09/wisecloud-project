package com.wisecloud.mdm.app.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.wisecloud.mdm.app.R
import com.wisecloud.mdm.app.databinding.FragmentHomeBinding

/**
 * Home screen displaying device overview stats and SN search.
 * - Overview: total devices, online devices, online rate
 * - Search: input SN keyword, view results, tap to navigate to detail
 */
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()
    private lateinit var searchAdapter: DeviceSearchAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSearchAdapter()
        setupSearchInput()
        observeViewModel()
    }

    private fun setupSearchAdapter() {
        searchAdapter = DeviceSearchAdapter { sn ->
            val bundle = bundleOf("sn" to sn)
            findNavController().navigate(R.id.action_home_to_deviceDetail, bundle)
        }
        binding.rvSearchResults.adapter = searchAdapter
    }

    private fun setupSearchInput() {
        // Search on keyboard action
        binding.etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch()
                true
            } else false
        }

        // Search on end icon click
        binding.searchInputLayout.setEndIconOnClickListener {
            performSearch()
        }
    }

    private fun performSearch() {
        val keyword = binding.etSearch.text?.toString().orEmpty().trim()
        viewModel.searchDevices(keyword)
    }

    private fun observeViewModel() {
        // Overview data
        viewModel.overview.observe(viewLifecycleOwner) { overview ->
            if (overview != null) {
                binding.tvTotalCount.text = overview.totalCount.toString()
                binding.tvOnlineCount.text = overview.onlineCount.toString()
                binding.tvOnlineRate.text = overview.onlineRate
            }
        }

        // Overview loading
        viewModel.overviewLoading.observe(viewLifecycleOwner) { loading ->
            binding.overviewProgressBar.visibility = if (loading) View.VISIBLE else View.GONE
            binding.overviewCardsRow.visibility = if (loading) View.INVISIBLE else View.VISIBLE
        }

        // Overview error
        viewModel.overviewError.observe(viewLifecycleOwner) { error ->
            if (error != null) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
            }
        }

        // Search results
        viewModel.searchResults.observe(viewLifecycleOwner) { results ->
            searchAdapter.submitList(results)
            binding.tvEmptyState.visibility =
                if (results.isEmpty() && !binding.etSearch.text.isNullOrBlank()) View.VISIBLE
                else View.GONE
        }

        // Search loading
        viewModel.searchLoading.observe(viewLifecycleOwner) { loading ->
            binding.searchProgressBar.visibility = if (loading) View.VISIBLE else View.GONE
        }

        // Search error
        viewModel.searchError.observe(viewLifecycleOwner) { error ->
            if (error != null) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
