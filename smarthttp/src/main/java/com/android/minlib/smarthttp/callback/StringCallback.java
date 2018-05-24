package com.android.minlib.smarthttp.callback;

import com.android.minlib.smarthttp.log.SmartLog;
import com.android.minlib.smarthttp.okhttp.SmartHttp;
import com.google.gson.Gson;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;

import okhttp3.Response;

public abstract class StringCallback<T> extends AbstractCallBack<T> {

    Gson gson = new Gson();

    @Override
    public T parseResponse(Response response) throws IOException {
        String responseString = SmartHttp.getCryptionStrategy().decrypt(response.body().string());
        SmartLog.log(url,request,responseString,null);
        Class clz = (Class<T>)(((ParameterizedType)this.getClass().getGenericSuperclass()).getActualTypeArguments()[0]);
        return (T) gson.fromJson(responseString,clz);
    }
}
