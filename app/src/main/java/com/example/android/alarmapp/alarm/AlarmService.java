package com.example.android.alarmapp.alarm;

import android.app.ActivityManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Intent;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.example.android.alarmapp.DetailAlarmActivity;
import com.example.android.alarmapp.data.Alarm;
import com.example.android.alarmapp.utils.AlarmActionUtils;
import com.example.android.alarmapp.utils.LocationUtils;
import com.example.android.alarmapp.utils.NotificationUtils;

import java.util.GregorianCalendar;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by user on 2017-01-25.
 */

public class AlarmService extends IntentService{

    private static final String TAG = "AlarmService";

    public static final String ACTION_ALARM = "com.example.android.alarmapp.action.alarm";
    public static final String ACTION_ALARM_REBOOT = "com.example.android.alarmapp.action.alarm.reboot";

    public AlarmService() {
        super("AlarmService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        AlarmActionUtils.prepareMusic(getApplicationContext());
    }

    /* background task 수행 */
    @Override
    protected void onHandleIntent(Intent intent) {
        Realm mRealm = null;
        try {
            mRealm = Realm.getDefaultInstance();

            final String action = intent.getAction();
            if (action.equals(ACTION_ALARM)) {

                long id = -1;
                if (intent.hasExtra(Alarm.ID)) {
                    id = intent.getLongExtra(Alarm.ID, -1);
                }
                if (id < 0) return; // id가 extra로 넘어오지 않거나, 값이 없으면 return

                Log.d(TAG, "알람 액션, id : " + String.valueOf(id));

                final Alarm alarm = mRealm.where(Alarm.class).equalTo(Alarm.ID, id).findFirst();

                if (alarm.isWeekSetting()) {
                /* 요일 알람 */

                    GregorianCalendar calendar = (GregorianCalendar) GregorianCalendar.getInstance();
                    int currentDayOfWeek = calendar.get(GregorianCalendar.DAY_OF_WEEK);
                    /* 오늘인지 체크*/
                    if (alarm.isToday(currentDayOfWeek)) {
                        /* 진동, 벨소리, Notification */
                        alertAction(alarm);

                        /* 반복 아님 */
                        if (!alarm.isRepeated()) {
                            /* 알람 미사용으로 수정 */
                            mRealm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    alarm.setUsed(false);
                                }
                            });
                            return;
                        }
                    }
                    /* 오늘x or 반복 설정*/
                    /* 알람 재등록 */
                    AlarmManagerTask.makeAlarm(this, alarm, AlarmManagerTask.FLAG_RE_REGISTER);

                } else {
                    /* 요일 등록x, 재등록 x*/

                    /* 진동, 벨소리, Notification, DetailActivity */
                    alertAction(alarm);

                    /* 알람 미사용으로 수정 */
                    mRealm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            alarm.setUsed(false);
                        }
                    });
                    return;
                }
            }else if(action.equals(ACTION_ALARM_REBOOT)){
                /* 알람 재등록 */
                RealmResults<Alarm> alarmRealmResults = mRealm.where(Alarm.class)
                        .equalTo(Alarm.ISUSED, true)
                        .findAll();
                for (Alarm alarm : alarmRealmResults){
                    Log.d(TAG, "알람 사용 여부 :" + String.valueOf(alarm.isUsed()));
                    Log.d(TAG, "알람 재등록 id : " + String.valueOf(alarm.getId()));
                    AlarmManagerTask.makeAlarm(this, alarm, AlarmManagerTask.FLAG_INIT_REGISTER);
                }
            }
        }finally {
            if(mRealm!=null){
                mRealm.close();
            }
        }
        AlarmReceiver.completeWakefulIntent(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        DeviceWakeUp.release();
    }

    /* 진동, 벨소리, Notification, DetailActivity */
    private void alertAction(final Alarm alarm){

        /* 꺼진 화면 On */
        DeviceWakeUp.acquire(this);

        if(alarm.isVibrated()){
            Log.d(TAG, "진동이 울립니다.");
            AlarmActionUtils.startVibrate(getApplicationContext(), alarm.getAlarmTime());
        }
        if(alarm.isSounded()){
            Log.d(TAG, "벨소리가 울립니다.");
            AlarmActionUtils.startMusic(getApplicationContext());
        }

        //DetailActivity 실행
        Intent startDetailActivityIntent = new Intent(this, DetailAlarmActivity.class);
        startDetailActivityIntent.putExtra(Alarm.ID, alarm.getId());

        /* pending intent */
        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(this);
        taskStackBuilder.addNextIntentWithParentStack(startDetailActivityIntent);
        PendingIntent resultPendingIntent = taskStackBuilder.
                getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        try {
            resultPendingIntent.send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }

        /*
        boolean isRunningApp=false;
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses =
                activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo processInfo : runningAppProcesses){
            if (processInfo.importance == processInfo.IMPORTANCE_FOREGROUND){
                String name = processInfo.processName;
                Log.d(TAG, "process name : " + name);
                if (name.equals("com.example.android.alarmapp")){
                    isRunningApp=true;
                    break;
                }
            }
        }

        if(isRunningApp){
            Intent startDetailActivityIntent = new Intent(this, DetailAlarmActivity.class);
            startDetailActivityIntent.putExtra(Alarm.ID, alarm.getId());

            TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(this);
            taskStackBuilder.addNextIntentWithParentStack(startDetailActivityIntent);
            PendingIntent resultPendingIntent = taskStackBuilder.
                    getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            try {
                resultPendingIntent.send();
            } catch (PendingIntent.CanceledException e) {
                e.printStackTrace();
            }
        }else {
            //Noti 실행
            String address = LocationUtils.getAddressFromCoordinate(this, alarm.getLat(), alarm.getLon());
            NotificationUtils.notifyAlarm(this, alarm, address);
        }

        */

    }

}
