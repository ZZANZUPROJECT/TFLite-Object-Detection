package com.example.android.alarmapp.utils;

import com.example.android.alarmapp.data.Alarm;

/**
 * Created by user on 2017-01-24.
 */

public class AlarmTimeUtils {

    public static String getAMPM(int hour){
        String ampm="";
        if(hour > 12){
            ampm="오후";
        }else {
            ampm="오전";
        }
        return ampm;

    }

}
