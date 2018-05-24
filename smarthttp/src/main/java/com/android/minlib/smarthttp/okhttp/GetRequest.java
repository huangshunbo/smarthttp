package com.android.minlib.smarthttp.okhttp;

import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

import okhttp3.Request;

public class GetRequest extends SmartRequest {

    private LinkedHashMap<String, String> requestParams = new LinkedHashMap();

    public GetRequest(String url, @NonNull SmartOkhttp smartOkhttp) {
        super(url, smartOkhttp);
    }

    @Override
    protected void buildReuestContent(Request.Builder builder) {
        url = handleGetUrl(url,requestParams);
    }

    @Override
    protected String getRequestBoey() {
        return null;
    }

    public SmartRequest setRequestParams(HashMap<String, String> requestParams) {
        this.requestParams.clear();
        this.requestParams.putAll(requestParams);
        return this;
    }


    public static String handleGetUrl(String url, HashMap<String, String> params) {
        // 添加url参数
        if (params != null && params.size() > 0) {
            Iterator<String> it = params.keySet().iterator();
            StringBuffer sb = null;
            while (it.hasNext()) {
                String key = it.next();
                String value = params.get(key);
                if (sb == null) {
                    sb = new StringBuffer();
                    sb.append("?");
                } else {
                    sb.append("&");
                }
                sb.append(key);
                sb.append("=");
                sb.append(value);
            }
            url += sb.toString();
        }
        return url;
    }
}
