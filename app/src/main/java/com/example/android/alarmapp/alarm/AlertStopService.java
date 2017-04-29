package com.example.android.alarmapp.alarm;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.example.android.alarmapp.utils.AlarmActionUtils;


public class AlertStopService extends IntentService {

    private static final String TAG = "AlertStopService";
    public static final String ACTION_ALERT_STOP = "com.example.android.alarmapp.alarm.action.alert.stop";

    public AlertStopService() {
        super("AlertStopService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_ALERT_STOP.equals(action)) {
                AlarmActionUtils.stopMusic();
                AlarmActionUtils.stopVibrate();
                Log.d(TAG, "알람 울림 중지 완료");
            }
        }
    }

}
