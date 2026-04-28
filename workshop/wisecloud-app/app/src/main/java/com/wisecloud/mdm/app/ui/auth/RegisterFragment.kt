package com.wisecloud.mdm.app.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.wisecloud.mdm.app.R
import com.wisecloud.mdm.app.databinding.FragmentRegisterBinding

/**
 * Registration screen with username, email, password, and confirm password fields.
 * Validates password match locally before submitting to the server.
 */
class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnRegister.setOnClickListener {
            val username = binding.etUsername.text?.toString().orEmpty().trim()
            val email = binding.etEmail.text?.toString().orEmpty().trim()
            val password = binding.etPassword.text?.toString().orEmpty()
            val confirmPassword = binding.etConfirmPassword.text?.toString().orEmpty()
            viewModel.register(username, email, password, confirmPassword)
        }

        binding.tvGoToLogin.setOnClickListener {
            findNavController().navigate(R.id.action_register_to_login)
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.registerResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is AuthResult.Success -> {
                    binding.tvError.visibility = View.GONE
                    Toast.makeText(requireContext(), R.string.register_success, Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_register_to_login)
                }
                is AuthResult.PasswordMismatch -> {
                    binding.tvError.text = getString(R.string.error_passwords_not_match)
                    binding.tvError.visibility = View.VISIBLE
                }
                is AuthResult.Error -> {
                    binding.tvError.text = result.message
                    binding.tvError.visibility = View.VISIBLE
                }
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
            binding.btnRegister.isEnabled = !loading
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
