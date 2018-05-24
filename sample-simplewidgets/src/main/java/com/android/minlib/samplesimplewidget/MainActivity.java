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
import com.android.minlib.samplesimplewidget.smart.TestBean;
import com.android.minlib.smarthttp.callback.FileCallback;
import com.android.minlib.smarthttp.callback.StringCallback;
import com.android.minlib.smarthttp.exception.ApiException;

import java.io.File;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{

    public static Application application;

    ListView mListView;
    private static final String[] strs =
            {
                    "NetRequest",
            };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        application = getApplication();
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
                netPost();
                break;
        }
    }

    private void netPost(){
        HashMap<String,String> map = new HashMap<>();
        map.put("name","huangshunbo");
        Smart.post("common/test", map, new StringCallback<TestBean>() {
            @Override
            public void onSuccess(TestBean o) {
                Log.e("hsb",""+o.getName());
            }

            @Override
            public void onFailure(ApiException e) {
                Log.e("hsb",e.getUrl() + e.getMessage());
            }
        });
    }

    private void netRequest() {
        Smart.post("", null, new FileCallback(Environment.getExternalStorageDirectory().getAbsolutePath(),"testfile") {
            @Override
            public void onProgress(float progress, float total) {
                Log.d("MainActivity","progress = "+progress + " total = " + total);
            }

            @Override
            public void onSuccess(File file) {
                Log.d("MainActivity","onSuccess");
            }

            @Override
            public void onFailure(ApiException e) {
                Log.d("MainActivity","onFailure");
            }
        });
    }
}
