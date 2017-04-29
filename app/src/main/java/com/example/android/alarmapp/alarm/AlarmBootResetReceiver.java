package com.example.android.alarmapp.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.android.alarmapp.data.Alarm;

/**
 * Created by user on 2017-01-25.
 */

public class AlarmBootResetReceiver extends BroadcastReceiver {

    private static final String TAG = "AlarmBootResetReceiver";

    private static final String BOOT_COMPLETED_ACTION = "android.intent.action.BOOT_COMPLETED";

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(BOOT_COMPLETED_ACTION)){
            Log.d(TAG, "재부팅이 완료되었습니다.");
            Intent startAlarmServiceIntent = new Intent(context, AlarmService.class);
            startAlarmServiceIntent.setAction(AlarmService.ACTION_ALARM_REBOOT);
            context.startService(startAlarmServiceIntent);
        }
    }
}
