package com.android.minlib.smarthttp.callback;

import com.android.minlib.smarthttp.exception.ApiException;

import java.io.IOException;

import okhttp3.Response;

public abstract class AbstractCallBack<T> {
    public String url;
    public String request;
    public String response;

    public abstract void onSuccess(T t);

    public abstract void onFailure(ApiException e);

    public abstract T parseResponse(Response response) throws IOException;
}
