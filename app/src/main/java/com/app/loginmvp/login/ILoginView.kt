package com.app.loginmvp.login

import com.app.loginmvp.base.IView

/**

 * Create by wy on 2019/10/22 11:37

 */
interface ILoginView : IView {

    fun loginSuccessful(result : String)

    fun loginFail(msg : String)
}