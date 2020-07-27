package com.app.loginmvp.http

import io.reactivex.Observable
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call

import java.io.File


/**

 * Create by wy on 2019/10/25 09:53

 */
class RxRequest {
    companion object {

        val netError = "网络不给力，请稍后重试！"


        fun <T> getRxHttp(rxRequest: RequestUtil<T>): Observable<String> {
            return Observable.create { observableEmitter ->
                val body = rxRequest.getBodyParameter()
                val imageBody = rxRequest.getImageFileParameter()
                val videoBody = rxRequest.getVideoFileParameter()

                val builder = MultipartBody.Builder().setType(MultipartBody.FORM)

                for (key in body!!.keys) {
                    builder.addFormDataPart(key, getStringValue(body.get(key)))
                }

                for (key in imageBody.keys) {

                    if (!StringUtil.isStringValid(imageBody.get(key))) {
                        break
                    }
                    val imgF = File(imageBody.get(key))
                    builder.addPart(
                        MultipartBody.Part.createFormData(
                            key, imgF.getName(),
                            RequestBody.create(MediaType.parse("image/*"), imgF)
                        )
                    )
                }

                for (key in videoBody.keys) {

                    if (!StringUtil.isStringValid(videoBody.get(key))) {
                        break
                    }

                    val videoF = File(videoBody.get(key))
                    builder.addPart(
                        MultipartBody.Part.createFormData(
                            "video", videoF.getName(),
                            RequestBody.create(MediaType.parse("video/*"), videoF)
                        )
                    )
                }

                if (body.isEmpty()) {
                    builder.addFormDataPart("timestamp", "")
                }

                val call: Call<ResponseBody>

                if (!rxRequest.isCache) {
                    call = rxRequest.getRequestInterface()!!.post(
                        rxRequest.getUrl()!!,
                        (System.currentTimeMillis() / 1000).toString(),
                        rxRequest.getSign(),
                        builder.build()
                    )

                } else {
                    call = rxRequest.getRequestInterface()!!.postHaveCache(
                        rxRequest.getUrl()!!,
                        (System.currentTimeMillis() / 1000).toString(),
                        rxRequest.getSign(),
                        builder.build()
                    )
                }


                try {
                    val response = call.execute()

                    val responseStr = response.body()!!.string()
                    LogUtil.print("请求接口返回: $responseStr")

                    if (response.code() == 200) {
                        observableEmitter.onNext(responseStr)
                    } else {
                        val apiResponse = ApiResponse<T>()
                        apiResponse.errorCode = -1
                        apiResponse.msg = netError
                        observableEmitter.onNext(apiResponse.toJsonString())
                    }
                } catch (e: Exception) {
                    val apiResponse = ApiResponse<T>()
                    apiResponse.errorCode = -1
                    apiResponse.msg = netError
                    observableEmitter.onNext(apiResponse.toJsonString())
                    e.printStackTrace()
                }


            }
        }


        fun getStringValue(s: String?): String {
            return s ?: ""
        }
    }
}