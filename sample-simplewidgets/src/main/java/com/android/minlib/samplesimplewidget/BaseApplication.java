package com.android.minlib.samplesimplewidget;

import android.app.Application;

import com.android.minlib.smarthttp.okhttp.SmartHttp;

public class BaseApplication extends Application {

    public static BaseApplication application;
    @Override
    public void onCreate() {
        super.onCreate();
        application = this;

    }
}
