package com.android.minlib.smarthttp.okhttp;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;

public class SmartHttp {

    private static Context mContext;

    private static Handler mainHandler = new Handler(Looper.getMainLooper());
    private static OkHttpClient.Builder mBuilder;

    private static HashMap<String,String> mHeaders = new HashMap<>();

    private static List<Interceptor> interceptors = new ArrayList<>();
    private static List<Interceptor> networkInterceptors = new ArrayList<>();

    public static void init(Application application) {
        mContext = application;
    }

    public static void initBuilder(OkHttpClient.Builder builder) {
        mBuilder = builder;
    }

    public static void setHeaders(HashMap<String, String> headers) {
        mHeaders = headers;
    }

    public static void addInterceptor(Interceptor interceptor){
        interceptors.add(interceptor);
    }

    public static void addNetworkInterceptor(Interceptor interceptor){
        networkInterceptors.add(interceptor);
    }


    protected static OkHttpClient.Builder getBuilder() {
        return mBuilder;
    }


    public static PostRequestCall post(String url){
        PostRequestCall postRequest = new PostRequestCall(url);
        postRequest.setHeaders(mHeaders);
        if(interceptors.size() > 0){
            for(Interceptor interceptor : interceptors){
                postRequest.getBuilder().addInterceptor(interceptor);
            }
        }
        if(networkInterceptors.size() > 0){
            for (Interceptor interceptor : networkInterceptors){
                postRequest.getBuilder().addNetworkInterceptor(interceptor);
            }
        }

        return postRequest;
    }

    public static GetRequestCall get(String url){
        GetRequestCall getRequest = new GetRequestCall(url);
        getRequest.setHeaders(mHeaders);
        if(interceptors.size() > 0){
            for(Interceptor interceptor : interceptors){
                getRequest.getBuilder().addInterceptor(interceptor);
            }
        }
        if(networkInterceptors.size() > 0){
            for (Interceptor interceptor : networkInterceptors){
                getRequest.getBuilder().addNetworkInterceptor(interceptor);
            }
        }
        return getRequest;
    }

    protected static boolean isNetWorkAvailable() {
        if( mContext == null) {
            throw new IllegalArgumentException("Context is null !!!");
        } else {
            ConnectivityManager mgr = (ConnectivityManager)mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            if(mgr != null) {
                NetworkInfo info = mgr.getActiveNetworkInfo();
                if(info != null && info.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }

            return false;
        }
    }

    public static void runOnUIThread(Runnable runnable){
        if(runnable != null) {
            mainHandler.post(runnable);
        }
    }
}
