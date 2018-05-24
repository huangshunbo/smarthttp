package com.android.minlib.smarthttp.okhttp;

import android.support.annotation.NonNull;

import com.android.minlib.smarthttp.log.SmartLog;
import com.android.minlib.smarthttp.exception.ApiException;
import com.android.minlib.smarthttp.callback.AbstractCallBack;

import org.json.JSONException;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLException;

import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.Request;
import okhttp3.Response;

public abstract class SmartRequest {


    protected Request.Builder requestBuild = new Request.Builder();

    protected String url;
    //请求头
    protected Headers headers = new Headers.Builder().build();
    protected SmartOkhttp smartOkhttp = null;

    //默认缓存时间 一小时
    private static final int DEFAULT_CACHE_MILLISECONDS = 1000 * 60 * 60;
    //缓存方式
    private CacheControl FORCE_CACHE = new CacheControl.Builder().onlyIfCached().maxStale(DEFAULT_CACHE_MILLISECONDS, TimeUnit.MILLISECONDS).build();
    private CacheControl NO_CACHE = new CacheControl.Builder().noCache().build();

    public SmartRequest(String url, @NonNull SmartOkhttp smartOkhttp) {
        this.url = url;
        this.smartOkhttp = smartOkhttp;
    }

    protected void setCacheTime(int time) {
        FORCE_CACHE = new CacheControl.Builder().onlyIfCached().maxStale(time, TimeUnit.MILLISECONDS).build();
    }

    protected void setHeaders(HashMap<String, String> params) {
        Headers.Builder builder = new Headers.Builder();
        if (params != null) {
            Iterator<String> it = params.keySet().iterator();
            while (it.hasNext()) {
                String key = it.next();
                String value = params.get(key);
                builder.add(key, value);
            }
        }
        headers = builder.build();
    }



    public SmartRequest buildRequest() {
        requestBuild
                .headers(headers)
                .cacheControl(NO_CACHE)
                .url(url);
        buildReuestContent(requestBuild);
        return this;
    }

    public SmartRequest buildRequestWithCache() {
        requestBuild
                .headers(headers)
                .cacheControl(FORCE_CACHE)
                .url(url);
        buildReuestContent(requestBuild);
        return this;
    }

    public SmartRequest buildRequestWithCache(int time) {
        requestBuild
                .headers(headers)
                .cacheControl(new CacheControl.Builder().onlyIfCached().maxStale(time, TimeUnit.MILLISECONDS).build())
                .url(url);
        buildReuestContent(requestBuild);
        return this;
    }

    protected abstract void buildReuestContent(Request.Builder builder);

    public void execute(final AbstractCallBack abstractCallBack) {
        if (!SmartHttp.isNetWorkAvailable()) {
            abstractCallBack.onFailure(new ApiException(ApiException.ERROR_NO_NETWORD, ApiException.ERROR_NO_NETWORD_TXT));
        } else {
            smartOkhttp.generateClient().newCall(requestBuild.build()).enqueue(new Callback() {
                @Override
                public void onFailure(final Call call, final IOException e) {
                    SmartHttp.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            if (e instanceof SocketTimeoutException) {
                                abstractCallBack.onFailure(new ApiException(ApiException.ERROR_SOCKET_TIMEOUT, ApiException.ERROR_SOCKET_TIMEOUT_TXT, url, e));
                            } else if (e instanceof SocketException) {
                                abstractCallBack.onFailure(new ApiException(ApiException.ERROR_SOCKET, ApiException.ERROR_SOCKET_TXT, url, e));
                            } else if (e instanceof SSLException) {
                                abstractCallBack.onFailure(new ApiException(ApiException.ERROR_SSL, ApiException.ERROR_SSL_TXT, url, e));
                            } else {
                                abstractCallBack.onFailure(new ApiException(ApiException.ERROR_UNDIFINE, ApiException.ERROR_UNDIFINE_TXT, url, e));
                            }
                        }
                    });
                    SmartLog.log(url,getRequestBoey(),null,e);
                }

                @Override
                public void onResponse(Call call, final Response response) throws IOException {
                        try {
                            if (response.isSuccessful()) {
                                try {
                                    abstractCallBack.url = url;
                                    abstractCallBack.request = getRequestBoey();
                                    final Object obj = abstractCallBack.parseResponse(response);
                                    SmartHttp.runOnUIThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            abstractCallBack.onSuccess(obj);
                                        }
                                    });
                                }catch (Exception e) {
                                    if(e instanceof JSONException){
                                        throw new ApiException(ApiException.WARING_JSON_ANALYSIS_ERROR, ApiException.WARING_JSON_ANALYSIS_ERROR_TXT,e);
                                    }else{
                                        throw new ApiException(ApiException.WARING_UNDEFINE,ApiException.WARING_UNDEFINE_TXT, e);
                                    }
                                }
                            } else {
                                throw new ApiException(ApiException.WARING_UNDEFINE,ApiException.WARING_UNDEFINE_TXT);
                            }
                        } catch (final Exception var7) {
                            SmartHttp.runOnUIThread(new Runnable() {
                                public void run() {
                                    if (var7 instanceof ApiException) {
                                        abstractCallBack.onFailure((ApiException)var7);
                                    } else {
                                        abstractCallBack.onFailure(new ApiException(ApiException.WARING_UNDEFINE,ApiException.WARING_UNDEFINE_TXT,var7));
                                    }

                                }
                            });
                            SmartLog.log(url,getRequestBoey(),null,var7);
                        } finally {
                            if (response.body() != null) {
                                response.body().close();
                            }
                        }

                }
            });
        }

    }

    protected abstract String getRequestBoey();

}
