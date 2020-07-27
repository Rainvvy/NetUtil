package com.app.loginmvp.http

import android.annotation.SuppressLint
import android.support.annotation.StringRes
import android.text.TextUtils
import android.util.Log
import com.alibaba.fastjson.JSON

import com.app.loginmvp.BuildConfig
import com.app.loginmvp.MyApp
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import com.orhanobut.logger.Logger
import io.reactivex.Observable
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import okio.Buffer
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.lang.reflect.Type

import java.nio.charset.Charset
import java.util.*
import java.util.concurrent.TimeUnit

import kotlin.text.Charsets.UTF_8

/**

 * Create by wy on 2019/10/25 09:00

 */
 open class RequestUtil<T> {
    private val DEFAULT_TIMEOUT = 5L

    private val bodyParameter = HashMap<String, String>()
    private val headParameter = HashMap<String, String>()
    private val imageFileParameter = HashMap<String, String>()
    private val videoFileParameter = HashMap<String, String>()
    private val normalFileParameter = HashMap<String, String>()

    private var toJsonType: Type? = null

    private var url: String? = null
    private var baseUrl: String? = null
    private var headUrl: String? = null
    private val uid: String? = null
    private var signString: String? = null

    private var retrofit: Retrofit? = null
    private var okHttpClient: OkHttpClient? = null


    private var requestInterface: RequestInterface? = null


    internal var isCache = false


    companion object {
       internal var builder: OkHttpClient.Builder? = null

         fun <T> builder(): RequestUtil<T> {
            val requestUtil = RequestUtil<T>()
            builder = OkHttpClient.Builder()
            return requestUtil
        }
    }


    /**
     * 添加网络请求表单
     *
     * @param key
     * @param value
     * @return
     */

    fun addBody(key: String, value: String): RequestUtil<T> {
        bodyParameter[key] = value
        return this
    }

    /**
     * 添加图片文件参数
     *
     * @param key
     * @param filePath
     * @return
     */

    fun addImageFileBody(key: String, filePath: String): RequestUtil<T> {
        imageFileParameter[key] = filePath
        return this
    }


    fun addImageFileBodys(body: Map<String, String>): RequestUtil<T> {
        for ((key, value) in body) {
            imageFileParameter[key] = value
        }
        return this
    }


//    /**
//     * 添加视频文件参数
//     *
//     * @param key
//     * @param filePath
//     * @return
//     */
//
//    public RequestUtil<T> addVideoFileBody(String key, String filePath) {
//        videoFileParameter.put(key, filePath);
//        return this;
//    }

//    /**
//     * 添加普通文件参数
//     *
//     * @param key
//     * @param filePath
//     * @return
//     */
//
//    public RequestUtil<T> addNormalFileBody(String key, String filePath) {
//        normalFileParameter.put(key, filePath);
//        return this;
//    }

    /**
     * 添加请求头
     *
     * @param key
     * @param value
     * @return
     */

    fun addHead(key: String, value: String): RequestUtil<T> {
        headParameter[key] = value
        return this
    }

    /**
     * 设置项目头链接 例（/CAdminAPI/V1/）
     *
     * @param headUrl
     * @return
     */

    fun headUrl(headUrl: String): RequestUtil<T> {
        this.headUrl = headUrl
        return this
    }

    /**
     * 设置接口 例（GetCircleMessageList）
     *
     * @param url~
     * @return
     */

    fun url(url: String): RequestUtil<T> {
        this.url = url
        return this
    }

    /**
     * 设置接口
     *
     * @param resId~
     * @return
     */

    fun url(@StringRes resId: Int): RequestUtil<T> {
        this.url = MyApp.myApp?.getString(resId)
        return this
    }

    /**
     * 设置返回数据中data的数据类型
     *
     * @param toJsonType~
     * @return
     */

    fun setToJsonType(toJsonType: Type): RequestUtil<T> {
        this.toJsonType = toJsonType
        return this
    }

    fun addList(body: Map<String, String>): RequestUtil<T> {
        val entries = body.entries.iterator()
        while (entries.hasNext()) {
            val entry = entries.next()
            bodyParameter[entry.key] = entry.value
        }
        return this
    }

    /**
     * 设置服务器链接
     *
     * @param baseUrl~
     * @return
     */

    fun setBaseUrl(baseUrl: String): RequestUtil<T> {
        this.baseUrl = baseUrl
        return this
    }

    fun request(): Observable<T> {
        builder?.addInterceptor(BaseInterceptor(headParameter))
            ?.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)

        if (isCache) {
            builder?.addNetworkInterceptor(CacheInterceptor())
        }
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        builder?.addInterceptor(interceptor)

        okHttpClient = builder?.addInterceptor(RetrofitLogInterceptor())?.build()

        val gson = GsonBuilder()
            .setLenient()
            .create()

        retrofit = Retrofit.Builder()
            .baseUrl(getRequestUrl())
            .client(okHttpClient)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        requestInterface = retrofit!!.create(RequestInterface::class.java)

        var observable = RxRequest.getRxHttp(this)
            .map { s ->
                try {
                    if (BuildConfig.DEBUG) {
                        Logger.d(bodyParameter)
                        Logger.json(s)
                    }
                    return@map GsonUtil.fromJson<T>(s, toJsonType!!)
                } catch (e: Exception) {
                    return@map JSON.parseObject<T>(s, toJsonType)
                }
            }

        observable = observable.map({ e -> e })
        return observable
    }

    class RetrofitLogInterceptor : Interceptor {

        @Synchronized
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
            val request = chain.request()
            val startTime = System.currentTimeMillis()
            val response = chain.proceed(chain.request())
            val endTime = System.currentTimeMillis()
            val duration = endTime - startTime
            val mediaType = response.body()!!.contentType()
            val content = response.body()!!.string()
            Log.i(TAG, "请求地址：| " + request.toString())
            printParams(request.body()!!)
            Log.i(TAG, "请求体返回：| Response:$content")
            Log.i(TAG, "----------请求耗时:" + duration + "毫秒----------")
            return response.newBuilder().body(okhttp3.ResponseBody.create(mediaType, content)).build()
        }


        private fun printParams(body: RequestBody) {
            val buffer = Buffer()
            try {
                body.writeTo(buffer)
                var charset = Charset.forName("UTF-8")
                val contentType = body.contentType()
                if (contentType != null) {
                    charset = contentType!!.charset(UTF_8)
                }
                val params = buffer.readString(charset)
                Log.i(TAG, "请求参数： | $params")
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }

        companion object {
            var TAG = "RetrofitLogInterceptor"
        }
    }

    internal fun getRequestInterface(): RequestInterface? {
        return requestInterface
    }

    internal fun getRequestUrl(): String {
        return baseUrl!! + headUrl!!
    }

    fun getUrl(): String? {
        return url
    }

    fun getBodyParameter(): Map<String, String>? {
        return bodyParameter
    }

    fun getImageFileParameter(): Map<String, String> {
        return imageFileParameter
    }

    fun getVideoFileParameter(): Map<String, String> {
        return videoFileParameter
    }

    fun getNormalFileParameter(): Map<String, String> {
        return normalFileParameter
    }

    fun setSignString(signString: String) {
        this.signString = signString
    }

    fun getSign(): String {
        val map = TreeMap<String, Any>()
        map["get_timestamp"] = System.currentTimeMillis() / 1000
        if (getBodyParameter() != null && getBodyParameter()!!.size > 0) {
            for (key in getBodyParameter()!!.keys) {
                if (!map.containsKey("post_$key") && getBodyParameter()!![key] != null && !TextUtils.isEmpty(
                        getBodyParameter()!![key].toString()
                    )
                ) {
                    map["post_$key"] = getBodyParameter()!![key].toString()
                }
            }
        }
        val stringBuilder = StringBuilder()
        for (key in map.keys) {
            stringBuilder.append(key).append("=").append(map[key]).append("&")
        }
        stringBuilder.append("key=").append(signString)
        var result = stringBuilder.toString()

//        result = EncryptionTool.MD5_32(result)
        return result
    }
}

class CacheInterceptor : Interceptor {

    @SuppressLint("MissingPermission")
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {

        // 有网络时 设置缓存超时时间10秒钟
        val maxAge = 10
        // 无网络时，设置超时为1个小时
        val maxStale = 60 * 60
        var request = chain.request()
        if (NetworkUtils.isConnected()) {
            //有网络时只从网络获取
            request = request.newBuilder()
                .cacheControl(CacheControl.FORCE_NETWORK)
                .build()
        } else {
            //无网络时只从缓存中读取
            request = request.newBuilder()
                .cacheControl(CacheControl.FORCE_CACHE)
                .build()
        }
        var response = chain.proceed(request)
        if (NetworkUtils.isConnected()) {
            response = response.newBuilder()
                .removeHeader("Pragma")
                .header("Cache-Control", "public, max-age=$maxAge")
                .build()
        } else {
            response = response.newBuilder()
                .removeHeader("Pragma")
                .header("Cache-Control", "public, only-if-cached, max-stale=$maxStale")
                .build()
        }
        return response
    }
}