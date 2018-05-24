package com.android.minlib.smarthttp.exception;

import okhttp3.Response;

public class ApiException extends Exception{

    public static final int ERROR_UNDIFINE = -10000;
    public static final String ERROR_UNDIFINE_TXT = "未知错误";
    public static final int ERROR_NO_NETWORD = -10001;
    public static final String ERROR_NO_NETWORD_TXT = "无网络连接";
    public static final int ERROR_SOCKET_TIMEOUT = -10002;
    public static final String ERROR_SOCKET_TIMEOUT_TXT = "Socket连接超时";
    public static final int ERROR_SOCKET = -10003;
    public static final String ERROR_SOCKET_TXT = "Socket出错";
    public static final int ERROR_SSL = -10004;
    public static final String ERROR_SSL_TXT = "SSL出错";

    public static final int WARING_UNDEFINE = 10000;
    public static final String WARING_UNDEFINE_TXT = "未知警告错误";
    public static final int WARING_DATA_EMPTY = 10001;
    public static final String WARING_DATA_EMPTY_TXT = "返回数据为空";
    public static final int WARING_JSON_ANALYSIS_ERROR = 10002;
    public static final String WARING_JSON_ANALYSIS_ERROR_TXT = "返回Json数据解析异常";

    private int code;
    private String message;
    private String url;

    public ApiException(int code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }
    public ApiException(int code, String message,String url) {
        super(message);
        this.code = code;
        this.message = message;
        this.url = url;
    }
    public ApiException(int code,Throwable throwable){
        super(throwable);
        this.code = code;
        this.message = throwable.getMessage();
    }

    public ApiException(int code,String message,Throwable throwable){
        super(throwable);
        this.code = code;
        this.message = message;
    }

    public ApiException(int code,String message,String url,Throwable throwable){
        super(throwable);
        this.code = code;
        this.message = message;
        this.url = url;
    }

    public ApiException(Response response) {
        super(response.message());
        this.code = response.code();
        this.message = response.message();
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String mUrl) {
        this.url = mUrl;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
