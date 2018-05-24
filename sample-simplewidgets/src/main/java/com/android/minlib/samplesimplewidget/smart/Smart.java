package com.android.minlib.samplesimplewidget.smart;

import com.android.minlib.samplesimplewidget.MainActivity;
import com.android.minlib.smarthttp.okhttp.SmartHttp;
import com.android.minlib.smarthttp.callback.AbstractCallBack;
import com.android.minlib.smarthttp.strategy.IURLStrategy;
import com.google.gson.Gson;

import java.util.HashMap;

//加解密
public class Smart {

    private static final SmartHttp smartHttp = new SmartHttp(MainActivity.application);
    static  {
        smartHttp.setCacheTime(1000 * 60 * 60);
        smartHttp.setHeaders(new HashMap());
        smartHttp.setURLStrategy(new IURLStrategy() {
            @Override
            public boolean isHttps() {
                return false;
            }

            @Override
            public String getDomains() {
                return "result.eolinker.com/w1dt8wI1fbd6f47eb994a74afd0434841df9136b52d1586?uri=/";
//                return "imtt.dd.qq.com/16891/5F46E5DD09F7478B7C1B96EEA48CA221.apk";
//                return "imtt.dd.qq.com/16891/77FCEF50B7D08DC3322DDC2F1DF54BAF.apk";
                }

        });

    }

    public static void post(String methodName, HashMap<String, String> params, AbstractCallBack abstractCallBack) {
        String paramStr = params != null ? new Gson().toJson(params) : "";
        smartHttp.post(methodName)
                .setRequestJson(paramStr)
                .buildRequest()
                .execute(abstractCallBack);

    }
}
