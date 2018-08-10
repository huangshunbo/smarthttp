package com.android.minlib.samplesimplewidget.smart;

import com.android.minlib.samplesimplewidget.BaseApplication;
import com.android.minlib.smarthttp.okhttp.GetRequestCall;
import com.android.minlib.smarthttp.okhttp.PostRequestCall;
import com.android.minlib.smarthttp.okhttp.RequestCall;
import com.android.minlib.smarthttp.okhttp.SmartHttp;

import java.util.HashMap;
import java.util.List;

public class Smart {

    static {
        SmartHttp.init(BaseApplication.application);
    }

    public static RequestCall orderPost(String method, HashMap<String,String> params){
        return SmartHttp.post(URLConstants.generateOrderUrl(method))
                .setRequestParams(params)
                .buildRequestWithCache();
    }

    public static RequestCall wheatherGet(HashMap<String,String> params){
        return SmartHttp.get(URLConstants.generateWheatherUrl(""))//在【URLConstants】中做URL处理
                .setRequestParams(params)//可以在这里加入加密的环节,将参数进行加密
                .buildRequestWithCache(100);//支持设置是否缓存及缓存时间
    }
}
