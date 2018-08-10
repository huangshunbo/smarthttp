package com.android.minlib.smarthttp.callback;

import com.android.minlib.smarthttp.exception.ApiException;

import java.io.IOException;

import okhttp3.Response;

public abstract class AbstractCallback<T> {

    public abstract void onSuccess(T t);

    public abstract void onFailure(ApiException e);

    public abstract T parseResponse(Response response) throws IOException;
}
