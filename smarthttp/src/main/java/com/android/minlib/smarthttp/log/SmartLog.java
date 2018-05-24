package com.android.minlib.smarthttp.log;

import android.util.Log;

import okhttp3.internal.platform.Platform;

public class SmartLog {

    public static void log(String url,String request,String response,Exception e){
        log("     \n");
        log("╔════════════════════════════════════════════ Request Start ════════════════════════════════════════════");
        log("║ URL :" + url);
        if(request != null || response != null) {
            log("║──────────────────────────────────────────────────────────────────────────────────────────────");
        }

        if(request != null){
            log("║ 请求参数 ");
            log("║ " + request.replace("\n","\n║"));
            if(response != null) {
                log("║──────────────────────────────────────────────────────────────────────────────────────────────");
            }
        }

        if(response != null){
            log("║ 返回数据 ");
            log("║ " + response.replace("\n","\n║"));
        }

        log("╚════════════════════════════════════════════ Request End ════════════════════════════════════════════");
        log("\n");

        if(e != null){
            log("║  ");
            log("║ 异常信息 ");
            log("║  ");
//            log("║ " + e.getStackTrace().toString());
            e.printStackTrace();
        }
    }

    private static void log(String message){
        Log.d("SmartLog",message);
    }
}
