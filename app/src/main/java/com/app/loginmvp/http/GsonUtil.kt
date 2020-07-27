package com.app.loginmvp.http

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.lang.reflect.Type

/**

 * Create by wy on 2019/10/25 09:57

 */
class GsonUtil {
    companion object{
        fun <T> fromJson(json: String, classOfT: Type): T {
            val gson = Gson()
            return gson.fromJson<T>(json, classOfT)
        }

        fun toJson(`object`: Any): String {
            val gson = GsonBuilder().setPrettyPrinting().create()
            return gson.toJson(`object`)
        }
    }
}