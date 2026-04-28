package com.wisecloud.mdm.app.util

import android.content.Context
import android.content.SharedPreferences

/**
 * Manages JWT token storage using SharedPreferences.
 * Provides methods to save, retrieve, and clear the token.
 */
class TokenManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun saveToken(token: String) {
        prefs.edit().putString(KEY_TOKEN, token).apply()
    }

    fun getToken(): String? {
        return prefs.getString(KEY_TOKEN, null)
    }

    fun clearToken() {
        prefs.edit().remove(KEY_TOKEN).apply()
    }

    fun isLoggedIn(): Boolean {
        return getToken() != null
    }

    companion object {
        private const val PREFS_NAME = "mdm_prefs"
        private const val KEY_TOKEN = "jwt_token"
    }
}
