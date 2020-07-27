package com.app.loginmvp.login;

import android.app.Activity;
import com.app.loginmvp.base.BasePresenter;
import com.app.loginmvp.base.IView;

/**
 * Create by wy on 2019/10/22 15:18
 *
 * 用kotlin 写这里是真的坑  看mainactivity
 */

public class TestActivity<T extends  BasePresenter> extends Activity implements IView {


    @Override
    public void showDialog() {

    }

    @Override
    public void hideDialog() {

    }
}
