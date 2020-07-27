package com.app.loginmvp

import android.util.Log

/**

 * Create by wy on 2019/10/22 15:41

 */
class log {
    companion object {
        fun logd(tag: String, msg: String) {
            Log.d(tag, "LoginMvp" + msg)
        }
    }
}