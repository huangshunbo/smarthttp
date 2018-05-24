package com.android.minlib.smarthttp.okhttp;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;

public class SmartOkhttp {
    private OkHttpClient.Builder okHttpClientBuild = new OkHttpClient.Builder();
    private OkHttpClient okHttpClient;

    private int readTimeOut = 60000;
    private int connectTimeOut = 60000;
    private int writeTimeOut = 60000;

    private SSLSocketFactory sslSocketFactory = null;

    public SmartOkhttp() {
    }

    protected OkHttpClient generateClient(){
        if(okHttpClient == null){
            okHttpClientBuild.retryOnConnectionFailure(false);
            okHttpClientBuild.sslSocketFactory(createSSLSocketFactory());
            okHttpClientBuild.cookieJar(createCookieJar());
            okHttpClientBuild.readTimeout(readTimeOut,TimeUnit.MILLISECONDS);
            okHttpClientBuild.writeTimeout(writeTimeOut,TimeUnit.MILLISECONDS);
            okHttpClientBuild.connectTimeout(connectTimeOut,TimeUnit.MILLISECONDS);

            okHttpClient = okHttpClientBuild.build();
        }
        return okHttpClient;
    }

    protected void setOkHttpClient(OkHttpClient okHttpClient) {
        this.okHttpClient = okHttpClient;
    }

    protected void setReadTimeOut(int readTimeOut) {
        this.readTimeOut = readTimeOut;
        okHttpClientBuild.readTimeout(readTimeOut, TimeUnit.MILLISECONDS);
    }

    protected void setConnectTimeOut(int connectTimeOut) {
        this.connectTimeOut = connectTimeOut;
        okHttpClientBuild.connectTimeout(connectTimeOut,TimeUnit.MILLISECONDS);
    }

    protected void setWriteTimeOut(int writeTimeOut) {
        this.writeTimeOut = writeTimeOut;
        okHttpClientBuild.writeTimeout(writeTimeOut,TimeUnit.MILLISECONDS);
    }

    protected void setCertificates(InputStream... certificates)
    {
        try
        {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null);
            int index = 0;
            for (InputStream certificate : certificates)
            {
                String certificateAlias = Integer.toString(index++);
                keyStore.setCertificateEntry(certificateAlias, certificateFactory.generateCertificate(certificate));
                try
                {
                    if (certificate != null)
                        certificate.close();
                } catch (IOException e)
                {
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
            sslSocketFactory = sslContext.getSocketFactory();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
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
    private SSLSocketFactory createSSLSocketFactory() {
        if(sslSocketFactory != null){
            return sslSocketFactory;
        }
        SSLSocketFactory sSLSocketFactory = null;
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init((KeyManager[])null, new TrustManager[]{new TrustAllManager()}, new SecureRandom());
            sSLSocketFactory = sc.getSocketFactory();
        } catch (Exception var2) {
            var2.printStackTrace();
        }
        return sSLSocketFactory;
    }
    private class TrustAllManager implements X509TrustManager {
        private TrustAllManager() {
        }
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }



}
