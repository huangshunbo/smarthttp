package com.android.minlib.smarthttp.log;

import android.text.TextUtils;
import android.util.Log;

import com.apkfuns.logutils.LogUtils;

public class SmartLog {

    public static void LOG(String url, String request, String response, Exception e){
        LOG("     \n");
        LOG("╔════════════════════════════════════════════ Request Start ════════════════════════════════════════════");
        LOG("║ URL :" + url);

        if(!TextUtils.isEmpty(request)){
            LOG("║──────────────────────────────────────────── Request ────────────────────────────────────────────");
            LOG("║ " + request.replace("\n","\n║"));
        }

        if(!TextUtils.isEmpty(response)){
            LOG("║──────────────────────────────────────────── Response ────────────────────────────────────────────");
            LOG("║ " + response.replace("\n","\n║"));
        }

        if(e != null){
            LOG("║──────────────────────────────────────────── Error ────────────────────────────────────────────");
            e.printStackTrace();
        }
        LOG("╚════════════════════════════════════════════ End ════════════════════════════════════════════");
        LOG("\n");
    }

    private static void LOG(String message){
        Log.d("SmartLog",message);
    }
}
