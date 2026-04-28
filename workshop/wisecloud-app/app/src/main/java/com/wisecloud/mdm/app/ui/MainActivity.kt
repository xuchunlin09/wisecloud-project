package com.wisecloud.mdm.app.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.wisecloud.mdm.app.MdmApplication
import com.wisecloud.mdm.app.R
import com.wisecloud.mdm.app.databinding.ActivityMainBinding
import com.wisecloud.mdm.app.util.TokenExpiredEvent

/**
 * Single-activity host for the navigation graph.
 * Listens for TokenExpiredEvent to auto-redirect to login on 401.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val tokenExpiredListener: () -> Unit = {
        // Run on main thread since TokenExpiredEvent may fire from OkHttp thread
        runOnUiThread {
            navigateToLogin()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val tokenManager = (application as MdmApplication).tokenManager

        // If already logged in, skip login and go to home
        if (tokenManager.isLoggedIn()) {
            val navHostFragment = supportFragmentManager
                .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
            val navController = navHostFragment.navController
            val graph = navController.navInflater.inflate(R.navigation.nav_graph)
            graph.setStartDestination(R.id.homeFragment)
            navController.graph = graph
        }

        TokenExpiredEvent.register(tokenExpiredListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        TokenExpiredEvent.unregister(tokenExpiredListener)
    }

    /**
     * Navigate to login fragment, clearing the entire back stack.
     */
    private fun navigateToLogin() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as? NavHostFragment ?: return
        val navController = navHostFragment.navController
        val graph = navController.navInflater.inflate(R.navigation.nav_graph)
        graph.setStartDestination(R.id.loginFragment)
        navController.graph = graph
    }
}
