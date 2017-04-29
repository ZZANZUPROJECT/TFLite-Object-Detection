package com.example.android.alarmapp.alarm;

import android.content.Context;
import android.os.PowerManager;

/**
 * Created by user on 2017-01-28.
 */

public class DeviceWakeUp {
    private static PowerManager.WakeLock sCpuWakeLock;

    static void acquire(Context context) {
        if (sCpuWakeLock != null) {
            return;
        }
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        sCpuWakeLock = pm.newWakeLock(
                PowerManager.SCREEN_BRIGHT_WAKE_LOCK |
                        PowerManager.ACQUIRE_CAUSES_WAKEUP |
                        PowerManager.ON_AFTER_RELEASE, "fail");
        sCpuWakeLock.acquire();
    }

    static void release() {
        if (sCpuWakeLock != null) {
            sCpuWakeLock.release();
            sCpuWakeLock = null;
        }
    }
}

