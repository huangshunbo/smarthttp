package com.android.minlib.smarthttp.okhttp;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;

public class PostRequest extends SmartRequest {
    //请求方式：json字符串、key-value表单、文件+key-value表单
    private String requestJson;
    private LinkedHashMap<String, String> requestParams = new LinkedHashMap();
    private LinkedHashMap<String, File> requestFiles = new LinkedHashMap();
    private String requestBody = "";

    public PostRequest(String url, @NonNull SmartOkhttp smartOkhttp) {
        super(url, smartOkhttp);
    }

    public SmartRequest setRequestJson(String requestJson) {
        this.requestJson = SmartHttp.getCryptionStrategy().encrypt(requestJson);
        return this;
    }

    public SmartRequest setRequestParams(HashMap<String, String> requestParams) {
        this.requestParams.clear();
        this.requestParams.putAll(SmartHttp.getCryptionStrategy().encrypt(requestParams));
        return this;
    }

    public SmartRequest setFile(HashMap<String, File> requestFiles, HashMap<String, String> requestParams) {
        this.requestParams.clear();
        this.requestParams.putAll(SmartHttp.getCryptionStrategy().encrypt(requestParams,requestFiles));
        this.requestFiles.clear();
        this.requestFiles.putAll(requestFiles);
        return this;
    }


    /**
     * <br> Description: 根据三种不同的内容构建对应的RequestBody
     * <br> Author:      huangshunbo
     * <br> Date:        2018/5/2 10:34
     */
    protected void buildReuestContent(Request.Builder requestBuild) {
        //构建 json字符串
        if (!TextUtils.isEmpty(requestJson)) {
            requestBuild.post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), requestJson));
            requestBody = requestJson;
            //构建 key-value表单
        } else if (requestParams.size() > 0 && requestFiles.size() <= 0) {
            FormBody.Builder formBody = new FormBody.Builder();//创建表单请求体
            Iterator iterator = this.requestParams.keySet().iterator();
            while (iterator.hasNext()) {
                String key = (String) iterator.next();
                String value = this.requestParams.get(key);
                formBody.add(key, value);
            }
            requestBuild.post(formBody.build());
            requestBody = requestParams.toString();
        } else if (requestFiles.size() > 0) {
            MultipartBody.Builder multipartBody = new MultipartBody.Builder();
            multipartBody.setType(MultipartBody.FORM);

            Iterator var1 = this.requestParams.keySet().iterator();
            while (var1.hasNext()) {
                String key = (String) var1.next();
                String value = this.requestParams.get(key);
                multipartBody.addFormDataPart(key, value);
            }

            Iterator var2 = this.requestFiles.keySet().iterator();
            while (var2.hasNext()) {
                String key = (String) var2.next();
                File file = this.requestFiles.get(key);
                multipartBody.addFormDataPart(key, file.getName(), RequestBody.create(MediaType.parse("file/*"), file));
            }
            requestBody = requestFiles.toString() + "\n" + requestParams.toString();
        }
    }

    @Override
    protected String getRequestBoey() {
        return requestBody;
    }

}
