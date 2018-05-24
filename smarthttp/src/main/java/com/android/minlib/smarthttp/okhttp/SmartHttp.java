package com.android.minlib.smarthttp.okhttp;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;

import com.android.minlib.smarthttp.strategy.DefaultCryptionStrategy;
import com.android.minlib.smarthttp.strategy.ICryptionStrategy;
import com.android.minlib.smarthttp.strategy.IURLStrategy;
import com.android.minlib.smarthttp.strategy.URLStrategy;

import java.io.InputStream;
import java.util.HashMap;

import okhttp3.OkHttpClient;

public class SmartHttp {

    private static Context mContext;
    private SmartOkhttp smartOkhttp = new SmartOkhttp();

    private static Handler mainHandler = new Handler(Looper.getMainLooper());

    private HashMap<String,String> headers = new HashMap<>();
    private int cacheTime = 1000 * 60 * 60;

    private URLStrategy urlStrategy = new URLStrategy();

    private static ICryptionStrategy iCryptionStrategy = new DefaultCryptionStrategy();

    public SmartHttp(Application application) {
        this.mContext = application;
    }

    public SmartHttp(Application application,ICryptionStrategy iCryptionStrategy) {
        this.mContext = application;
        this.iCryptionStrategy = iCryptionStrategy;
    }

    public PostRequest post(String methodName){
        PostRequest postRequest = new PostRequest(urlStrategy.generateUrl(methodName),smartOkhttp);
        postRequest.setHeaders(headers);
        postRequest.setCacheTime(cacheTime);
        return postRequest;
    }

    public GetRequest get(String methodName){
        GetRequest getRequest = new GetRequest(urlStrategy.generateUrl(methodName),smartOkhttp);
        getRequest.setHeaders(headers);
        getRequest.setCacheTime(cacheTime);
        return getRequest;
    }

    public void setHeaders(HashMap headers){
        this.headers = headers;
    }

    public void setCacheTime(int time){
        this.cacheTime = time;
    }

    public void setOkHttpClient(OkHttpClient okHttpClient) {
        smartOkhttp.setOkHttpClient(okHttpClient);
    }

    public void setReadTimeOut(int readTimeOut) {
        smartOkhttp.setReadTimeOut(readTimeOut);
    }

    protected void setConnectTimeOut(int connectTimeOut) {
        smartOkhttp.setConnectTimeOut(connectTimeOut);
    }

    protected void setWriteTimeOut(int writeTimeOut) {
        smartOkhttp.setWriteTimeOut(writeTimeOut);
    }

    protected void setCertificates(InputStream... certificates){
        smartOkhttp.setCertificates(certificates);
    }

    public void setURLStrategy(IURLStrategy iurlStrategy){
        urlStrategy.setIurlStrategy(iurlStrategy);
    }
    public static boolean isNetWorkAvailable() {
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

    public static ICryptionStrategy getCryptionStrategy(){
        return iCryptionStrategy;
    }

    public static void runOnUIThread(Runnable runnable){
        if(runnable != null) {
            mainHandler.post(runnable);
        }
    }
}
