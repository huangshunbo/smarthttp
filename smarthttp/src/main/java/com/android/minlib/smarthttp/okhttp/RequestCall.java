package com.android.minlib.smarthttp.okhttp;

import com.android.minlib.smarthttp.interceptor.LogInterceptor;
import com.android.minlib.smarthttp.log.SmartLog;
import com.android.minlib.smarthttp.exception.ApiException;
import com.android.minlib.smarthttp.callback.AbstractCallback;
import com.android.minlib.smarthttp.manager.IRequestManager;

import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public abstract class RequestCall {


    protected Call mCall;
    protected String mUrl;
    private String mTaskId;

    private Reference<IRequestManager> mIRequestManager;
    private volatile boolean isCancel = false;

    //OkHttp
    private OkHttpClient.Builder mBuilder = new OkHttpClient.Builder();
    protected Request.Builder mRequestBuild = new Request.Builder();
    //请求头
    protected Headers mHeaders = new Headers.Builder().build();

    //------------  默认的一套参数配置  ------------
    //默认缓存时间 一小时
    private static final int DEFAULT_CACHE_MILLISECONDS = 1000 * 60 * 60;
    //缓存方式
    private CacheControl FORCE_CACHE = new CacheControl.Builder().onlyIfCached().maxStale(DEFAULT_CACHE_MILLISECONDS, TimeUnit.MILLISECONDS).build();
    private CacheControl NO_CACHE = new CacheControl.Builder().noCache().build();
    //超时设置
    private long mReadTimeOut = 60000;
    private long mConnectTimeOut = 60000;
    private long mWriteTimeOut = 60000;

    private SSLSocketFactory mSSLSocketFactory = null;

    public RequestCall(String url) {
        this.mUrl = url;
        this.mTaskId = url;
        mBuilder.retryOnConnectionFailure(false);
        mBuilder.sslSocketFactory(createSSLSocketFactory());
        mBuilder.cookieJar(createCookieJar());
        mBuilder.readTimeout(mReadTimeOut, TimeUnit.MILLISECONDS);
        mBuilder.writeTimeout(mWriteTimeOut, TimeUnit.MILLISECONDS);
        mBuilder.connectTimeout(mConnectTimeOut, TimeUnit.MILLISECONDS);
    }

    public RequestCall setCacheTime(int time) {
        FORCE_CACHE = new CacheControl.Builder().onlyIfCached().maxStale(time, TimeUnit.MILLISECONDS).build();
        return this;
    }

    public RequestCall setHeaders(HashMap<String, String> params) {
        Headers.Builder builder = new Headers.Builder();
        if (params != null) {
            Iterator<String> it = params.keySet().iterator();
            while (it.hasNext()) {
                String key = it.next();
                String value = params.get(key);
                builder.add(key, value);
            }
        }
        mHeaders = builder.build();
        return this;
    }

    public RequestCall setReadTimeOut(int mReadTimeOut) {
        mBuilder.readTimeout(mReadTimeOut, TimeUnit.MILLISECONDS);
        return this;
    }

    public RequestCall setConnectTimeOut(int mConnectTimeOut) {
        mBuilder.connectTimeout(mConnectTimeOut, TimeUnit.MILLISECONDS);
        return this;
    }

    public RequestCall setWriteTimeOut(int mWriteTimeOut) {
        mBuilder.writeTimeout(mWriteTimeOut, TimeUnit.MILLISECONDS);
        return this;
    }

    public RequestCall setCertificates(InputStream... certificates) {
        try {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null);
            int index = 0;
            for (InputStream certificate : certificates) {
                String certificateAlias = Integer.toString(index++);
                keyStore.setCertificateEntry(certificateAlias, certificateFactory.generateCertificate(certificate));
                try {
                    if (certificate != null) {
                        certificate.close();
                    }
                } catch (IOException e) {
                }
            }
            SSLContext sslContext = SSLContext.getInstance("TLS");
            TrustManagerFactory trustManagerFactory =
                    TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());

            trustManagerFactory.init(keyStore);
            sslContext.init
                    (
                            null,
                            trustManagerFactory.getTrustManagers(),
                            new SecureRandom()
                    );
            mSSLSocketFactory = sslContext.getSocketFactory();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }



    public void setBuilder(OkHttpClient.Builder builder) {
        this.mBuilder = builder;
    }

    public OkHttpClient.Builder getBuilder() {
        return mBuilder;
    }

    public Call getCall() {
        return mCall;
    }

    public String getUrl() {
        return mUrl;
    }

    public String getTaskId() {
        return mTaskId;
    }

    public RequestCall buildRequest() {
        mRequestBuild
                .headers(mHeaders)
                .cacheControl(NO_CACHE)
                .url(mUrl);
        buildReuestContent(mRequestBuild);
        return this;
    }

    public RequestCall buildRequestWithCache() {
        mRequestBuild
                .headers(mHeaders)
                .cacheControl(FORCE_CACHE)
                .url(mUrl);
        buildReuestContent(mRequestBuild);
        return this;
    }

    public RequestCall buildRequestWithCache(int time) {
        mRequestBuild
                .headers(mHeaders)
                .cacheControl(new CacheControl.Builder().onlyIfCached().maxStale(time, TimeUnit.MILLISECONDS).build())
                .url(mUrl);
        buildReuestContent(mRequestBuild);
        return this;
    }


    public void execute(final AbstractCallback abstractCallBack) {
        addToRequestManager(this);
        if (!SmartHttp.isNetWorkAvailable()) {
            removeFromReqManager(this);
            abstractCallBack.onFailure(new ApiException(ApiException.ERROR_NO_NETWORD, ApiException.ERROR_NO_NETWORD_TXT));
        } else {
            mBuilder.addInterceptor(new LogInterceptor());
            mCall = mBuilder.build().newCall(mRequestBuild.build());
            if(isCancel){
                removeFromReqManager(this);
                return;
            }
            mCall.enqueue(new Callback() {
                @Override
                public void onFailure(final Call call, final IOException e) {
                    SmartLog.LOG(mUrl,getRequestBoey(),null,e);
                    removeFromReqManager(RequestCall.this);
                    SmartHttp.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            if (e instanceof SocketTimeoutException) {
                                abstractCallBack.onFailure(new ApiException(ApiException.ERROR_SOCKET_TIMEOUT, ApiException.ERROR_SOCKET_TIMEOUT_TXT, mUrl, e));
                            } else if (e instanceof SocketException) {
                                abstractCallBack.onFailure(new ApiException(ApiException.ERROR_SOCKET, ApiException.ERROR_SOCKET_TXT, mUrl, e));
                            } else if (e instanceof SSLException) {
                                abstractCallBack.onFailure(new ApiException(ApiException.ERROR_SSL, ApiException.ERROR_SSL_TXT, mUrl, e));
                            } else {
                                abstractCallBack.onFailure(new ApiException(ApiException.ERROR_UNDIFINE, ApiException.ERROR_UNDIFINE_TXT, mUrl, e));
                            }
                        }
                    });
                    SmartLog.LOG(mUrl, getRequestBoey(), null, e);
                }

                @Override
                public void onResponse(Call call, final Response response) throws IOException {
                    try {
                        removeFromReqManager(RequestCall.this);
                        if (response.isSuccessful()) {
                            try {

                                final Object obj = abstractCallBack.parseResponse(response);
//                                SmartLog.LOG(mUrl,getRequestBoey(),response.body().string(),null);
                                SmartHttp.runOnUIThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        abstractCallBack.onSuccess(obj);
                                    }
                                });
                            } catch (Exception e) {
                                if (e instanceof JSONException) {
                                    throw new ApiException(ApiException.WARING_JSON_ANALYSIS_ERROR, ApiException.WARING_JSON_ANALYSIS_ERROR_TXT, e);
                                } else {
                                    throw new ApiException(ApiException.WARING_UNDEFINE, ApiException.WARING_UNDEFINE_TXT, e);
                                }
                            }
                        } else {
                            throw new ApiException(ApiException.WARING_UNDEFINE, ApiException.WARING_UNDEFINE_TXT);
                        }
                    } catch (final Exception var7) {
                        SmartHttp.runOnUIThread(new Runnable() {
                            @Override
                            public void run() {
                                if (var7 instanceof ApiException) {
                                    abstractCallBack.onFailure((ApiException) var7);
                                } else {
                                    abstractCallBack.onFailure(new ApiException(ApiException.WARING_UNDEFINE, ApiException.WARING_UNDEFINE_TXT, var7));
                                }
                            }
                        });
                        SmartLog.LOG(mUrl, getRequestBoey(), null, var7);
                    } finally {
                        if (response.body() != null) {
                            response.body().close();
                        }
                    }

                }
            });
        }

    }

    public void cancel() {
        isCancel = true;
        if (this.mCall != null) {
            this.mCall.cancel();
        }
    }

    public boolean isCanceled() {
        if (isCancel) {
            return true;
        }
        synchronized (this) {
            return this.mCall != null && this.mCall.isCanceled();
        }
    }

    protected abstract String getRequestBoey();

    protected abstract void buildReuestContent(Request.Builder builder);


    //----------------------   CookieJar SSL   ----------------------
    private SSLSocketFactory createSSLSocketFactory() {
        if (mSSLSocketFactory != null) {
            return mSSLSocketFactory;
        }
        SSLSocketFactory sSLSocketFactory = null;
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init((KeyManager[]) null, new TrustManager[]{new TrustAllManager()}, new SecureRandom());
            sSLSocketFactory = sc.getSocketFactory();
        } catch (Exception var2) {
            var2.printStackTrace();
        }
        return sSLSocketFactory;
    }

    private CookieJar createCookieJar() {
        return new CookieJar() {
            private final HashMap<String, List<Cookie>> cookieStore = new HashMap<String, List<Cookie>>();

            @Override
            public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                cookieStore.put(url.host(), cookies);
            }

            @Override
            public List<Cookie> loadForRequest(HttpUrl url) {
                List<Cookie> cookies = cookieStore.get(url.host());
                return cookies != null ? cookies : new ArrayList<Cookie>();
            }

        };
    }

    private class TrustAllManager implements X509TrustManager {
        private TrustAllManager() {
        }

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

    //----------------------   RequestManager   ----------------------
    public RequestCall setRequestManager(IRequestManager iRequestManager) {
        if (iRequestManager != null) {
            this.mIRequestManager = new WeakReference<IRequestManager>(iRequestManager);
        }
        return this;
    }

    private void addToRequestManager(RequestCall requestCall) {
        if (this.mIRequestManager != null && this.mIRequestManager.get() != null && requestCall != null) {
            this.mIRequestManager.get().addCalls(requestCall);
        }
    }

    private void removeFromReqManager(RequestCall requestCall) {
        if (this.mIRequestManager != null && this.mIRequestManager.get() != null && requestCall != null) {
            this.mIRequestManager.get().removeCall(requestCall);
        }
    }
}
