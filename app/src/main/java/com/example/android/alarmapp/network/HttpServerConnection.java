package com.example.android.alarmapp.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HttpServerConnection {
    private static final String TAG = HttpServerConnection.class.getSimpleName();
    private static final String BASE_URL = "http://ec2-13-58-89-190.us-east-2.compute.amazonaws.com:5000/";

    private static Retrofit mRetrofit;

    public static synchronized Retrofit getInstance() {
        // If Instance is null then initialize new Instance
        if (mRetrofit == null) {

            mRetrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return mRetrofit;
    }
}
