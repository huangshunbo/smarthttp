package com.android.minlib.samplesimplewidget;

import android.app.Application;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.minlib.samplesimplewidget.smart.Smart;
import com.android.minlib.samplesimplewidget.smart.SmartBean;
import com.android.minlib.samplesimplewidget.smart.SmartCallback;
import com.android.minlib.smarthttp.callback.FileCallback;
import com.android.minlib.smarthttp.callback.StringCallback;
import com.android.minlib.smarthttp.exception.ApiException;
import com.android.minlib.smarthttp.manager.IRequestManager;
import com.android.minlib.smarthttp.okhttp.RequestCall;

import java.io.File;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class MainActivity extends AppCompatActivity implements IRequestManager<RequestCall>,AdapterView.OnItemClickListener{

    ListView mListView;
    private static final String[] strs =
            {
                    "Wheather GET",
            };

    private BlockingQueue<RequestCall> mCallBlockingQueue = new LinkedBlockingDeque<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mListView = new ListView(this);
        mListView.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, strs));
        mListView.setOnItemClickListener(this);
        setContentView(mListView);

    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch(position){
            case 0:
                wheatherGet();
                break;
        }
    }

    private void wheatherGet(){
        HashMap<String,String> params = new HashMap<>();
        params.put("city","深圳");
        Smart.wheatherGet(params).buildRequest().execute(new SmartCallback<DataBean>() {

            @Override
            public void onSuccess(DataBean bean) {
                Log.d("hsb","onSuccess \n" + bean.toString());
            }

            @Override
            public void onFailure(ApiException e) {
                Log.d("hsb","onFailure \n" + e.getDetailMessage());
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        removeAllCalls();
    }

    @Override
    public void addCalls(RequestCall call) {
        //call.getCall() == null 同样加入，重新加载会重新生成call
        if (call != null && mCallBlockingQueue != null && !mCallBlockingQueue.contains(call)) {
            mCallBlockingQueue.add(call);
        }
    }

    @Override
    public void removeCall(RequestCall call) {
        if (mCallBlockingQueue != null) {
            synchronized (mCallBlockingQueue) {
                if (mCallBlockingQueue != null) {
                    mCallBlockingQueue.remove(call);
                }
            }
        }
    }

    @Override
    public void removeAllCalls() {
        if (mCallBlockingQueue != null) {
            synchronized (mCallBlockingQueue) {
                if (mCallBlockingQueue != null) {
                    for (RequestCall requestCall : mCallBlockingQueue) {
                        if (requestCall != null && requestCall.getCall() != null) {
                            requestCall.getCall().cancel();
                        }
                    }
                    mCallBlockingQueue.clear();
                    mCallBlockingQueue = null;
                }
            }
        }
    }
}
