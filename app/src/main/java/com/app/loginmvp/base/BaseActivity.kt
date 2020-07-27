package com.app.loginmvp.base

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.app.loginmvp.log

/**

 * Create by wy on 2019/10/22 09:47
 *     按照java 的写法 应该是
 *     class LoginActivity : BaseActivity<T extends LoginPresenter>
 *     奶奶的 kotlin  下面这个样子 emmm
 */
abstract class BaseActivity<T : BasePresenter<*, *>> : AppCompatActivity(), IView {
   private val TAG = "BaseActivity"
    var mPresenter: T? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(initLayoutRes())
        init()
    }

    open fun init() {
        mPresenter = initPresenter()

    }

    abstract fun initPresenter(): T

    abstract fun initLayoutRes() : Int

    override fun showDialog() {
        //加载中
        log.logd(TAG, "---- Loading。。。。。")
    }

    override fun hideDialog() {
        log.logd(TAG, "---- Load finish。。。。。")
    }
}