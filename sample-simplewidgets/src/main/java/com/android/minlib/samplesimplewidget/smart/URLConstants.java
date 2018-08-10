package com.android.minlib.samplesimplewidget.smart;

import android.os.Debug;

class URLConstants {

    private static final String HTTP = "http://";
    private static final String HTTPS = "https://";

    private static final String RELEASE_URL_WHEATHER = "www.sojson.com/open/api/weather/json.shtml";
    private static final String DEBUG_URL_WHEATHER = "www.sojson.com/open/api/weather/json.shtml";

    public static String generateOrderUrl(String method) {
        return generateDomain() + method;
    }

    public static String generateWheatherUrl(String method) {
        return generateDomain() + method;
    }

    private static String generateDomain() {
        return HTTPS + RELEASE_URL_WHEATHER;
    }
}
