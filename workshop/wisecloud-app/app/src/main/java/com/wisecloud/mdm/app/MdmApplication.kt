package com.wisecloud.mdm.app

import android.app.Application
import com.wisecloud.mdm.app.util.TokenManager

class MdmApplication : Application() {

    lateinit var tokenManager: TokenManager
        private set

    override fun onCreate() {
        super.onCreate()
        tokenManager = TokenManager(this)
    }
}
