package com.app.loginmvp.http

import retrofit2.Call
import okhttp3.RequestBody
import okhttp3.ResponseBody


import retrofit2.http.*


/**

 * Create by wy on 2019/10/25 09:33

 */
interface RequestInterface {
    @POST
     fun post(
        @Url url: String,
        @Query("timestamp") time: String,
        @Query("sign") sign: String,
        @Body requestBody: RequestBody
    ): Call<ResponseBody>


    @Headers("Cache-Control: public, max-age=3600")
    @POST
     fun postHaveCache(
        @Url url: String,
        @Query("timestamp") time: String,
        @Query("sign") sign: String,
        @Body requestBody: RequestBody
    ): Call<ResponseBody>
}