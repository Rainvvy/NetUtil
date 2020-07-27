package com.app.loginmvp.http

import android.text.TextUtils
import java.util.regex.Pattern

/**

 * Create by wy on 2019/10/25 10:02

 */
class StringUtil {
    companion object{
        /**
         * 字符串是否为空
         * @param string
         * @return
         */

        fun isStringValid(string: String?): Boolean {
            return string != null && !string.isEmpty() && string.length > 0
        }

        /**
         * 字符串是否为空
         * @param string
         * @return
         */

        fun isIdValid(string: String): Boolean {
            return string != "0"
        }

        /**
         * 手机号码是否有效
         * @param phoneNumber
         * @return
         */
        fun phoneNumberValid(phoneNumber: String): Boolean {
            val isValid = false
            if (!TextUtils.isEmpty(phoneNumber) && phoneNumber.length == 11) {
                try {

                    val phone = java.lang.Long.valueOf(phoneNumber)
                    if (phone >= 0) {
                        return true
                    }
                } catch (e: Exception) {
                    return false
                }

            }
            return isValid
        }

        /**
         * 通用正则表达式调用方法
         *
         * @param Content    需要检查的字符串
         * @param PatternStr 正则表达式
         * @return
         */
        fun compile(Content: String, PatternStr: String): Boolean {
            val p = Pattern.compile(PatternStr)
            val m = p.matcher(Content)
            return m.matches()
        }

        fun isPasswordValid(password: String): Boolean {
            var isValid = false

            if (isStringValid(password)) {
                if (password.length >= 6) {
                    isValid = true
                }
            }

            return isValid
        }

        fun emptyString(): String {
            return ""
        }

        fun blankString(): String {
            return "    "
        }
    }
}