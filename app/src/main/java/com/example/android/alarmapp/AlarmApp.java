package com.example.android.alarmapp;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.example.android.alarmapp.data.Migration;
import com.example.android.alarmapp.utils.AlarmActionUtils;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by user on 2017-01-24.
 */


public class AlarmApp extends Application {

    private final static String TAG = "AlarmApp";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        Log.d(TAG, "attachBaseContext");
    }
}
