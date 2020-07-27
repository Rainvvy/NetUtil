package com.app.loginmvp.http

import java.io.Serializable

/**

 * Create by wy on 2019/10/25 09:54

 */
class ApiResponse<T> : Serializable {
    /**
     * status : false
     * errorCode : 20002
     * msg :
     * data : null
     */

    var status: Boolean = false
    var errorCode: Int = 0
    var msg: String = ""
    var data: T? = null

    private var listmsg: String? = null //列表数据为空

    constructor() {

    }

    constructor (code: Int, message: String) {
        this.errorCode = code
        this.msg = message
    }


    fun toJsonString(): String {
        return GsonUtil.toJson(this)
    }
    fun isOk() : Boolean{
        //if status == ok && errorCode == 200
        if (status == true && errorCode == 0){
            return true
        }
        return false
    }
    override fun toString(): String {
        return "ApiResponse{" +
                "errorCode=" + errorCode +
                ", msg='" + msg + '\''.toString() +
                '}'.toString()
    }
}