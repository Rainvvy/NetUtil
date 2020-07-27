package com.app.loginmvp.base

/**

 * Create by wy on 2019/10/22 10:10

 */
interface TDao {
    interface OnCompleteListener<T> {
        fun onSuccess(data: T)

        fun onFail(msg: String)
    }
}