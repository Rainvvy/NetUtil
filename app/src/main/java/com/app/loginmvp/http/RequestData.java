package com.app.loginmvp.http;

import com.app.loginmvp.MyApp;
import com.app.loginmvp.R;

/**
 * Created by Administrator on 2018/8/2.
 */

public class RequestData extends RequestUtil {

    public static <T> RequestUtil<T> build() {
        RequestUtil<T> request = RequestUtil.Companion.builder();
        request.addHead("auth", getRequestHead());
        request.setBaseUrl(MyApp.Companion.getMyApp().getString(R.string.baseUr_j));
//        request.setBaseUrl(MyApp.getAppContext().getString(R.string.testUr_j));
        request.headUrl(MyApp.Companion.getMyApp().getString(R.string.api_head));

//        if (StringUtil.isStringValid(UserModel.getInstance().getUserId())) {
//            request.addBody("uid", UserModel.getInstance().getUserId());
//        }

//        request.setSignString(Utils.getString(R.string.keySign));
        return request;
    }

    private static String getRequestHead() {
//        return UserModel.getInstance().getUserId() +
//                "|" +
//                UserModel.getInstance().getUserToken() +
//                "|" +
//                PhoneUtils.getCombinedDeviceID(Utils.getApp()) +
//                "|" +
//                System.currentTimeMillis() / 1000;
        return StringUtil.Companion.emptyString();
    }
}

