package com.example.android.alarmapp.utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;

import com.example.android.alarmapp.DetailAlarmActivity;
import com.example.android.alarmapp.R;
import com.example.android.alarmapp.alarm.AlertStopService;
import com.example.android.alarmapp.data.Alarm;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

/**
 * Created by user on 2017-01-26.
 */

public class NotificationUtils {

    // 정보 : icon, 시간, 위치정보, 날씨
    // 버튼 : detail 화면, 알람 중지

    private static final int ALARM_NOTIFICATION_ID = 123;

    private static final int ACTION_ALARM_CANCEL_ID = 1001;


    public static void notifyAlarm(Context context, final Alarm alarm, String location){

        String notificationTitle = context.getString(R.string.noti_title);

        GregorianCalendar currentTime = (GregorianCalendar) GregorianCalendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("EEE, HH:mm a");
        String timeStr = format.format(currentTime.getTime());

        StringBuffer notificationText = new StringBuffer();
        notificationText.append("메모 : ");
        notificationText.append(alarm.getMemo());
        notificationText.append(timeStr);
        notificationText.append("\n");
        notificationText.append("location : ");
        notificationText.append(location);

        /* notification 빌드 */
        NotificationCompat.Builder notiBuilder = new NotificationCompat.Builder(context)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setSmallIcon(R.drawable.ic_alarm_on_white_24dp)
                .setLargeIcon(largeIcon(context))
                .setContentTitle(notificationTitle)
                .setContentText(notificationText.toString())
                .setStyle(new NotificationCompat.BigTextStyle().bigText(notificationText.toString()))
               // .setDefaults(Notification.DEFAULT_VIBRATE)
                .setContentIntent(contentIntent(context, alarm))
                .addAction(stopAlertAction(context))
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setAutoCancel(true);

        /* notification 실행 */
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(ALARM_NOTIFICATION_ID, notiBuilder.build());

    }

    public static NotificationCompat.Action stopAlertAction(Context context){
        Intent intent = new Intent(context, AlertStopService.class);
        intent.setAction(AlertStopService.ACTION_ALERT_STOP);

        PendingIntent pendingIntent = PendingIntent.getService(
                context,
                ACTION_ALARM_CANCEL_ID,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        NotificationCompat.Action stopAlertAction = new NotificationCompat.Action(
                R.drawable.ic_sound_off,
                "알람 중지",
                pendingIntent
        );
        return stopAlertAction;
    }

    private static PendingIntent contentIntent(Context context, final Alarm alarm){
        /* intent */
        Intent startDetailActivityIntent = new Intent(context, DetailAlarmActivity.class);
        startDetailActivityIntent.putExtra(Alarm.ID, alarm.getId());

        /* pending intent */
        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context);
        taskStackBuilder.addNextIntentWithParentStack(startDetailActivityIntent);
        PendingIntent resultPendingIntent = taskStackBuilder.
                getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        return resultPendingIntent;
    }

    private static Bitmap largeIcon(Context context){
        Resources res = context.getResources();
        Bitmap largeIcon = BitmapFactory.decodeResource(res,R.drawable.ic_alarm_on_black_24dp);
        return largeIcon;
    }



}
