package com.app.loginmvp.login

import android.os.Bundle
import com.app.loginmvp.R
import com.app.loginmvp.base.BaseActivity
import com.app.loginmvp.log
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Create by wy on 2019/10/22 11:48
 *
 *
 */
class LoginActivity : BaseActivity<LoginPresenter>(), ILoginView {
    private var TAG = "LoginActivity"
    val netInfo = "RequestInfo : ------ \n"
    override fun loginFail(msg: String) {
        //login fail
        log.logd(TAG, "------  login faile  errmsg==" + msg)
        tv_content.text = netInfo + msg
    }

    override fun initLayoutRes(): Int {
        return R.layout.activity_main
    }

    override fun initPresenter(): LoginPresenter {
        return LoginPresenter(this)

    }

    override fun loginSuccessful(res: String) {
        //login successful to do
        log.logd(TAG, " ---- " + res)
        tv_content.text = netInfo + res

    }


    override fun init() {
        super.init()
        login.setOnClickListener {
            //click  login
            // if login successful return (user + pas)
//            mPresenter?.user = user.text.toString().toInt()
//            mPresenter?.pas = pas.text.toString().toInt()
            mPresenter?.login()
        }
    }
}
