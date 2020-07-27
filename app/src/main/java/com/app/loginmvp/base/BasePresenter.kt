package com.app.loginmvp.base

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer
import io.reactivex.internal.util.NotificationLite.getError
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import org.json.JSONException
import java.net.ConnectException
import java.net.SocketTimeoutException

/**

 * Create by wy on 2019/10/22 09:49

 */
abstract class BasePresenter<I : IView, T : TDao> {
    var iView: I? = null
    var tDao: T? = null
    protected val composite = CompositeDisposable()

    protected val error = BehaviorSubject.create<String>()

    constructor(iView: I) {
        this.iView = iView
        this.tDao = initDao()
    }

    abstract fun initDao(): T

    fun <T> submitRequestThrowError(observable: Observable<T>, onNext: Consumer<in T>) {
        composite.add(
            observable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(onNext,
                    Consumer<Throwable> { t -> error.onNext(getError(t)) })
        )

    }


    fun getError(throwable: Throwable?): String {
//        if (throwable is HttpErrorException) {
//            return RestErrorInfo((throwable as HttpErrorException).getResponseJson())
//        }

        if (throwable is ConnectException) return "网络错误，请检查网络重试！"
        if (throwable is SocketTimeoutException) return "网络错误，请检查网络重试！"
        if (throwable is JSONException) return "网络错误，请检查网络重试！"

        return if (throwable != null) throwable.message!! else ""
    }

    fun onDestroy() {
        composite.clear()
    }

    fun detach() {
        onDetach()


        onDestroy()

        onDetached()
    }

    /**
     * 绑定视图之前
     */
    fun onAttach() {}

    /**
     * 绑定视图之后
     */
    fun onAttached() {}

    /**
     * 解绑视图之前
     */
    fun onDetach() {
        this.iView = null
        this.tDao = null
    }

    /**
     * 解绑视图之后
     */
    fun onDetached() {}

}