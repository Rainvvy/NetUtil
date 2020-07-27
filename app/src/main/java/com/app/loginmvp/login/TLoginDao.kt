package com.app.loginmvp.login

import com.app.loginmvp.base.TDao

/**

 * Create by wy on 2019/10/22 11:40

 */
interface TLoginDao : TDao {

    //login
    fun login(user: Int, pas: Int, onCompleteListener: TDao.OnCompleteListener<List<String>>)

    //register
    fun register()
}