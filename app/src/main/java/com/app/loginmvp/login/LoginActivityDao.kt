package com.app.loginmvp.login

import com.app.loginmvp.base.TDao

/**

 * Create by wy on 2019/10/22 11:43

 */
class LoginActivityDao : TLoginDao {
    override fun login(user: Int, pas: Int, listener: TDao.OnCompleteListener<List<String>>) {
        //login  一般这里请求服务器 我们模拟
        if (user > 0 && pas > 0) {
            val data = ArrayList<String>()
            data.add((user + pas).toString())

            listener.onSuccess(data)
        }else listener.onFail("login fail ---  user and pas < 0")

//



    }

    override fun register() {

    }

}