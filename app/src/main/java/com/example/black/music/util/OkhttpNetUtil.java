package com.example.black.music.util;

import okhttp3.OkHttpClient;

public class OkhttpNetUtil {

    private OkHttpClient okHttpClient;
    private OkhttpNetUtil(){
        okHttpClient = new OkHttpClient();
    }

    public static OkhttpNetUtil getIns(){
        return OkhttpNetHolder.instance;
    }

    private static class OkhttpNetHolder{
        private static final OkhttpNetUtil instance = new OkhttpNetUtil();
    }

    public OkHttpClient getOkHttpClient() {
        return okHttpClient;
    }
}
