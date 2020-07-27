package com.app.loginmvp.login

import com.app.loginmvp.R
import com.app.loginmvp.http.ApiResponse
import com.app.loginmvp.http.RequestData
import com.app.loginmvp.http.RequestData.build
import com.google.gson.reflect.TypeToken
import io.reactivex.Observable

/**

 * Create by wy on 2019/10/25 10:58

 */
class LoginModel {
    companion object{
        internal fun ZgAppNews(): Observable<ApiResponse<String>> {
            return RequestData.build<ApiResponse<String>>()
                .setToJsonType(object : TypeToken<ApiResponse<String>>() {
                }.type)
                .url(R.string.api_zgApp_news)
                .request()
        }

    }
}