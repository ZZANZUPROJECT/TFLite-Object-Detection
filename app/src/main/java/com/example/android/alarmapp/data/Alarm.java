package com.example.android.alarmapp.data;

import android.net.Uri;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

/**
 * Created by user on 2017-01-24.
 */

public class Alarm extends RealmObject{

    public final static long DEFAULT_ALARM_TIME=100000;
    public final static boolean DEFAULT_USED=true;

    public final static String CONTENT_AUTHORITY = "com.example.android.alarmapp";
    public final static Uri BASE_CONTENT_URI = Uri.parse("content://"+CONTENT_AUTHORITY);
    /*
    *           content://com.example.android.alarmapp/alarms
    * */
    public static final String PATH_ALARM = "alarms";


    public final static String ID="id";
    public final static String ISUSED="isUsed";

    public final static String[] WEEKS = {"", "sun", "mon","tue","wed","thr","fri","sat"};
    public final static int INDEX_SUN = 1;
    public final static int INDEX_MON = 2;
    public final static int INDEX_TUE = 3;
    public final static int INDEX_WED = 4;
    public final static int INDEX_THR = 5;
    public final static int INDEX_FRI = 6;
    public final static int INDEX_SAT = 7;

    @PrimaryKey
    private long id;

    private int hour;
    private int minute;

    private boolean sun=false;
    private boolean mon=false;
    private boolean tue=false;
    private boolean wed=false;
    private boolean thr=false;
    private boolean fri=false;
    private boolean sat=false;

    private long alarmTime;
    private String memo;
    private double lat;
    private double lon;
    private boolean isVibrated;
    private boolean isSounded;
    private boolean isRepeated;

    @Index
    private boolean isUsed;

    public Alarm() {
    }

    public Alarm(long id, int hour, int minute,
                 boolean sun, boolean mon, boolean tue, boolean wed, boolean thr, boolean fri, boolean sat,
                 long alarmTime, String memo, double lat, double lon,
                 boolean isVibrated, boolean isSounded, boolean isRepeated, boolean isUsed) {
        this.id = id;
        this.hour = hour;
        this.minute = minute;
        this.sun = sun;
        this.mon = mon;
        this.tue = tue;
        this.wed = wed;
        this.thr = thr;
        this.fri = fri;
        this.sat = sat;
        this.alarmTime = alarmTime;
        this.memo = memo;
        this.lat = lat;
        this.lon = lon;
        this.isVibrated = isVibrated;
        this.isSounded = isSounded;
        this.isRepeated = isRepeated;
        this.isUsed = isUsed;
    }

    public static long getNextPK(Realm realm){
        Number lastId = realm.where(Alarm.class).max("id");
        long id;
        if(lastId==null){
            id=0;
        }else {
            id = lastId.longValue()+1;
        }
        return id;
    }

    public boolean isWeekSetting(){
        return sun||mon||tue||wed||thr||fri||sat;
    }

    public boolean isToday(int dayOfWeek){
        switch (dayOfWeek){
            case INDEX_SUN:
                return sun;
            case INDEX_MON:
                return mon;
            case INDEX_TUE:
                return tue;
            case INDEX_WED:
                return wed;
            case INDEX_THR:
                return thr;
            case INDEX_FRI:
                return fri;
            case INDEX_SAT:
                return sat;
            default:
                return false;
        }
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public boolean isSun() {
        return sun;
    }

    public void setSun(boolean sun) {
        this.sun = sun;
    }

    public boolean isMon() {
        return mon;
    }

    public void setMon(boolean mon) {
        this.mon = mon;
    }

    public boolean isTue() {
        return tue;
    }

    public void setTue(boolean tue) {
        this.tue = tue;
    }

    public boolean isWed() {
        return wed;
    }

    public void setWed(boolean wed) {
        this.wed = wed;
    }

    public boolean isThr() {
        return thr;
    }

    public void setThr(boolean thr) {
        this.thr = thr;
    }

    public boolean isFri() {
        return fri;
    }

    public void setFri(boolean fri) {
        this.fri = fri;
    }

    public boolean isSat() {
        return sat;
    }

    public void setSat(boolean sat) {
        this.sat = sat;
    }

    public long getAlarmTime() {
        return alarmTime;
    }

    public void setAlarmTime(long alarmTime) {
        this.alarmTime = alarmTime;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public boolean isVibrated() {
        return isVibrated;
    }

    public void setVibrated(boolean vibrated) {
        isVibrated = vibrated;
    }

    public boolean isSounded() {
        return isSounded;
    }

    public void setSounded(boolean sounded) {
        isSounded = sounded;
    }

    public boolean isRepeated() {
        return isRepeated;
    }

    public void setRepeated(boolean repeated) {
        isRepeated = repeated;
    }

    public boolean isUsed() {
        return isUsed;
    }

    public void setUsed(boolean used) {
        isUsed = used;
    }
}
