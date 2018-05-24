package com.android.minlib.smarthttp.strategy;

import java.io.File;
import java.util.HashMap;

public interface ICryptionStrategy {
    String encrypt(String request);
    HashMap<String,String> encrypt(HashMap<String,String> requestParams);
    HashMap<String,String> encrypt(HashMap<String,String> requestParams, HashMap<String,File> fileParams);

    String decrypt(String responseJson);
}
