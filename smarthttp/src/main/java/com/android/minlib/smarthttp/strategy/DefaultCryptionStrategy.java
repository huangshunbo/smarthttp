package com.android.minlib.smarthttp.strategy;

import java.io.File;
import java.util.HashMap;

public class DefaultCryptionStrategy implements ICryptionStrategy {
    @Override
    public String encrypt(String request) {
        return request;
    }

    @Override
    public HashMap<String, String> encrypt(HashMap<String, String> requestParams) {
        return requestParams;
    }

    @Override
    public HashMap<String, String> encrypt(HashMap<String, String> requestParams, HashMap<String, File> fileParams) {
        return requestParams;
    }

    @Override
    public String decrypt(String responseJson) {
        return responseJson;
    }
}
