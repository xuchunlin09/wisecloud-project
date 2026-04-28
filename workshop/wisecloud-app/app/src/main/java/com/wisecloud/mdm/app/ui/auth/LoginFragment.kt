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
import com.wisecloud.mdm.app.databinding.FragmentLoginBinding

/**
 * Login screen with username/password fields and a login button.
 * On successful login, navigates to HomeFragment.
 */
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnLogin.setOnClickListener {
            val username = binding.etUsername.text?.toString().orEmpty().trim()
            val password = binding.etPassword.text?.toString().orEmpty().trim()
            viewModel.login(username, password)
        }

        binding.tvGoToRegister.setOnClickListener {
            findNavController().navigate(R.id.action_login_to_register)
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.loginResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is AuthResult.Success -> {
                    binding.tvError.visibility = View.GONE
                    findNavController().navigate(R.id.action_login_to_home)
                }
                is AuthResult.Error -> {
                    binding.tvError.text = result.message
                    binding.tvError.visibility = View.VISIBLE
                }
                is AuthResult.PasswordMismatch -> { /* Not applicable for login */ }
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
            binding.btnLogin.isEnabled = !loading
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
