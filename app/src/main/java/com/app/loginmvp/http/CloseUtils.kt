package com.app.loginmvp.http

import java.io.Closeable
import java.io.IOException

/**

 * Create by wy on 2019/10/25 09:26

 */
class CloseUtils {
    private fun CloseUtils() {
        throw UnsupportedOperationException("u can't instantiate me...")
    }

    companion object {

        /**
         * Close the io stream.
         *
         * @param closeables closeables
         */
        fun closeIO(vararg closeables: Closeable) {
            if (closeables == null) return
            for (closeable in closeables) {
                if (closeable != null) {
                    try {
                        closeable.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                }
            }
        }

        /**
         * Close the io stream quietly.
         *
         * @param closeables closeables
         */
        fun closeIOQuietly(vararg closeables: Closeable) {
            if (closeables == null) return
            for (closeable in closeables) {
                if (closeable != null) {
                    try {
                        closeable.close()
                    } catch (ignored: IOException) {
                    }

                }
            }
        }
    }
}