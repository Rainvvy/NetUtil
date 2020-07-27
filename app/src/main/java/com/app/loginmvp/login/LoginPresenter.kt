package com.app.loginmvp.login

import com.app.loginmvp.base.BasePresenter
import com.app.loginmvp.base.TDao
import io.reactivex.functions.Consumer

/**

 * Create by wy on 2019/10/22 11:39

 */
class LoginPresenter : BasePresenter<ILoginView, TLoginDao> {
    var user: Int? = -1
    var pas: Int? = -1

    constructor(iLoginView: ILoginView) : super(iLoginView)


    fun login() {
        if (iView != null) {
            iView?.showDialog()

            submitRequestThrowError(LoginModel.ZgAppNews(), Consumer {
                //todo
                iView?.hideDialog()
                if (it.isOk()) //return data
                    iView?.loginSuccessful(it.toJsonString())
                else //Handle throw  it.msg it.errcode
                    iView?.loginFail(it.msg)

            })
//
//            tDao?.login(user!!, pas!!, object : TDao.OnCompleteListener<List<String>> {
//                override fun onFail(msg: String) {
//                    iView?.hideDialog()
//                    iView?.loginFail(msg)
//                }
//
//                override fun onSuccess(data: List<String>) {
//                    iView?.hideDialog()
//                    iView?.loginSuccessful(data.get(0))
//                }
//            })
        }
    }

    override fun initDao(): TLoginDao {
        return LoginActivityDao()
    }

}