package com.app.loginmvp.http

import android.text.TextUtils
import android.util.Log
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser

/**

 * Create by wy on 2019/10/25 09:59

 */
class LogUtil {
    companion object{
        fun print(error: Any) {
            jsonFormatterLog("" + error)
        }

        fun print(map: Map<String, String>) {
            val keys = map.keys
            val params = StringBuffer()
            params.append("{" + "\n")
            for (key in keys) {
                params.append("  body    " + key + ": " + map[key] + "\n")
            }
            params.append("}")
            params.toString()
            Log.e("LOG:", params.toString())
        }

        fun print(maps: List<Map<String, String>>) {
            val params = StringBuffer()
            params.append("{" + "\n")
            for (map in maps) {
                val keys = map.keys
                for (key in keys) {
                    params.append("  body    " + key + ": " + map[key] + "\n")
                }
            }
            params.append("}")
            Log.e("LOG:", params.toString())
        }

        private fun jsonFormatterLog(s: String) {
            if (TextUtils.isEmpty(s)) {
                Log.e("LOG:", "" + s)
                return
            }
            var json = s
            var tag: String? = null
            if (s.indexOf("{") > 0) {
                tag = s.substring(0, s.indexOf("{"))
                json = s.substring(s.indexOf("{"))
            }
            if (s.indexOf("{") > -1) {
                try {
                    val gson = GsonBuilder().setPrettyPrinting().create()
                    val jsonParser = JsonParser()
                    val jsonElement = jsonParser.parse(json)
                    json = gson.toJson(jsonElement)
                    if (!TextUtils.isEmpty(tag)) {
                        json = tag + "\n" + json
                    }
                    Log.e("LOG:", "" + json)
                    return
                } catch (e: Exception) {
                }

            }
            Log.e("LOG:", "" + s)
        }
    }
}