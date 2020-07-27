package com.app.loginmvp

import android.app.Application
import android.content.pm.ApplicationInfo

/**

 * Create by wy on 2019/10/25 09:13

 */
class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()
        initApp()
    }

    private fun initApp() {
        myApp = this

    }

    companion object {
        var myApp: MyApp? = null

    }
}