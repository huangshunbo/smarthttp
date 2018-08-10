package com.android.minlib.smarthttp.callback;

import com.google.gson.Gson;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;

import okhttp3.Response;

public abstract class StringCallback<T> extends AbstractCallback<T> {

    Gson gson = new Gson();

    @Override
    public T parseResponse(Response response) throws IOException {
        String responseString = response.body().string();
        Class clz = (Class<T>)(((ParameterizedType)this.getClass().getGenericSuperclass()).getActualTypeArguments()[0]);
        return (T) gson.fromJson(responseString,clz);
    }
}
