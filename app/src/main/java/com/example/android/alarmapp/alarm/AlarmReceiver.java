package com.example.android.alarmapp.alarm;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.example.android.alarmapp.data.Alarm;

/**
 * Created by user on 2017-01-25.
 */

public class AlarmReceiver extends WakefulBroadcastReceiver {

    private static final String TAG = "AlarmReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        if(action.equals(AlarmService.ACTION_ALARM)){
            long id=-1;
            if(intent.hasExtra(Alarm.ID)){
                id = intent.getLongExtra(Alarm.ID, -1);
            }

            Log.d(TAG, "알람 실행, id : " + String.valueOf(id));
            if(id >= 0){
                Intent startAlarmServiceIntent = new Intent(context, AlarmService.class);
                startAlarmServiceIntent.setAction(action);
                startAlarmServiceIntent.putExtra(Alarm.ID, id);
                startWakefulService(context,startAlarmServiceIntent);
            }

        }
    }
}
