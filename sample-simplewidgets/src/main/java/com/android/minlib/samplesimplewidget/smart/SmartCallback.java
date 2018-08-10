package com.android.minlib.samplesimplewidget.smart;

import com.android.minlib.smarthttp.callback.AbstractCallback;
import com.google.gson.Gson;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;

import okhttp3.Response;


public abstract class SmartCallback<T> extends AbstractCallback<T> {

    Gson gson = new Gson();

    @Override
    public T parseResponse(Response response) throws IOException {
        String json = "{\"date\":\"20180810\",\"message\":\"Success !\",\"status\":200,\"city\":\"深圳\",\"count\":52,\"data\":{\"shidu\":\"80%\",\"pm25\":17.0,\"pm10\":26.0,\"quality\":\"优\",\"wendu\":\"28\",\"ganmao\":\"各类人群可自由活动\",\"yesterday\":{\"date\":\"09日星期四\",\"sunrise\":\"05:58\",\"high\":\"高温 33.0℃\",\"low\":\"低温 27.0℃\",\"sunset\":\"19:00\",\"aqi\":29.0,\"fx\":\"无持续风向\",\"fl\":\"<3级\",\"type\":\"多云\",\"notice\":\"阴晴之间，谨防紫外线侵扰\"},\"forecast\":[{\"date\":\"10日星期五\",\"sunrise\":\"05:58\",\"high\":\"高温 31.0℃\",\"low\":\"低温 26.0℃\",\"sunset\":\"19:00\",\"aqi\":17.0,\"fx\":\"东南风\",\"fl\":\"3-4级\",\"type\":\"阵雨\",\"notice\":\"阵雨来袭，出门记得带伞\"},{\"date\":\"11日星期六\",\"sunrise\":\"05:58\",\"high\":\"高温 30.0℃\",\"low\":\"低温 26.0℃\",\"sunset\":\"18:59\",\"aqi\":17.0,\"fx\":\"东南风\",\"fl\":\"<3级\",\"type\":\"暴雨\",\"notice\":\"关好门窗，外出避开低洼地\"},{\"date\":\"12日星期日\",\"sunrise\":\"05:59\",\"high\":\"高温 30.0℃\",\"low\":\"低温 26.0℃\",\"sunset\":\"18:58\",\"aqi\":17.0,\"fx\":\"东南风\",\"fl\":\"3-4级\",\"type\":\"暴雨\",\"notice\":\"关好门窗，外出避开低洼地\"},{\"date\":\"13日星期一\",\"sunrise\":\"05:59\",\"high\":\"高温 31.0℃\",\"low\":\"低温 26.0℃\",\"sunset\":\"18:58\",\"aqi\":20.0,\"fx\":\"无持续风向\",\"fl\":\"<3级\",\"type\":\"阵雨\",\"notice\":\"阵雨来袭，出门记得带伞\"},{\"date\":\"14日星期二\",\"sunrise\":\"05:59\",\"high\":\"高温 32.0℃\",\"low\":\"低温 27.0℃\",\"sunset\":\"18:57\",\"aqi\":26.0,\"fx\":\"无持续风向\",\"fl\":\"<3级\",\"type\":\"阵雨\",\"notice\":\"阵雨来袭，出门记得带伞\"}]}}";
        SmartBean bean = gson.fromJson(response.body().string(),SmartBean.class);
        Class clz = (Class<T>)(((ParameterizedType)this.getClass().getGenericSuperclass()).getActualTypeArguments()[0]);
        return (T) gson.fromJson(gson.toJson(bean.getData()),clz);
    }
}
