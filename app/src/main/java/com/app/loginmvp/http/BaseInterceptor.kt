package com.app.loginmvp.http

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**

 * Create by wy on 2019/10/25 09:38

 */
class BaseInterceptor : Interceptor {
    private var headers: Map<String, String>? = null

    constructor(headers: Map<String, String>) {
        this.headers = headers
    }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {

        val builder = chain.request()
            .newBuilder()
        if (headers != null && headers?.size!! > 0) {
            val keys = headers?.keys
            for (headerKey in keys!!) {
                builder.addHeader(headerKey, headers!![headerKey]).build()
            }
        }
        return chain.proceed(builder.build())

    }
}