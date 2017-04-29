package com.example.android.alarmapp.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.example.android.alarmapp.data.Alarm;

import java.util.GregorianCalendar;

/**
 * Created by user on 2017-01-25.
 */

public class AlarmManagerTask {

    private static final String TAG = "AlarmManagerTask";

    public static final int FLAG_INIT_REGISTER = 0;
    public static final int FLAG_RE_REGISTER = 1;

    /* 알람 등록 */
    public static void makeAlarm(Context context, Alarm alarm, int flag){

        AlarmManager am = (AlarmManager) context.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        int requestCode = (int) alarm.getId();
        int hour = alarm.getHour();
        int minute = alarm.getMinute();

        Intent intent = new Intent(context.getApplicationContext(), AlarmReceiver.class);
        intent.setAction(AlarmService.ACTION_ALARM);
        intent.putExtra(Alarm.ID,alarm.getId());

        PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(
                context.getApplicationContext(), requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        setAlarm(am, hour,minute,alarmPendingIntent,flag);
    }

    /* 알람 취소 */
    public static void cancelAlarm(Context context, Alarm alarm){
        AlarmManager am = (AlarmManager) context.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        int requestCode = (int) alarm.getId();

        Intent intent = new Intent(context.getApplicationContext(), AlarmReceiver.class);
        intent.setAction(AlarmService.ACTION_ALARM);
        intent.putExtra(Alarm.ID,alarm.getId());

        PendingIntent alarmCancelPendingIntent = PendingIntent.getBroadcast(
                context.getApplicationContext(), requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        if(alarmCancelPendingIntent!=null){
            am.cancel(alarmCancelPendingIntent);
            alarmCancelPendingIntent.cancel();
            Log.d(TAG, "알람 취소 완료");
        }
    }

    private static void setAlarm(AlarmManager am, int hour, int minute, PendingIntent alarmPendingIntent,int flag){
        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.M )
            /* Doze 모드 대응 */
            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, getMillis(hour, minute, flag), alarmPendingIntent);
        else if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT )
            am.setExact(AlarmManager.RTC_WAKEUP, getMillis(hour, minute, flag), alarmPendingIntent);
        else
            am.set(AlarmManager.RTC_WAKEUP, getMillis(hour, minute,flag), alarmPendingIntent);
    }

    private static long getMillis(int hour, int minute, int flag){
        GregorianCalendar currentCalendar = (GregorianCalendar) GregorianCalendar.getInstance();
        int currentHour = currentCalendar.get(GregorianCalendar.HOUR_OF_DAY);
        int currentMinute = currentCalendar.get(GregorianCalendar.MINUTE);
        boolean tomorrow;


        if(flag==FLAG_INIT_REGISTER){
            if(currentHour < hour || (currentHour==hour && currentMinute < minute ))
                tomorrow=true;
            else
                tomorrow=false;
            if (!tomorrow)
                currentCalendar.add(GregorianCalendar.DAY_OF_YEAR, 1);
        }else if(flag==FLAG_RE_REGISTER){
            currentCalendar.add(GregorianCalendar.DAY_OF_YEAR, 1);
        }

        currentCalendar.set(GregorianCalendar.HOUR_OF_DAY, hour);
        currentCalendar.set(GregorianCalendar.MINUTE, minute);
        currentCalendar.set(GregorianCalendar.SECOND,0);
        currentCalendar.set(GregorianCalendar.MILLISECOND,0);

        return currentCalendar.getTimeInMillis();
    }

}
