package com.example.android.alarmapp.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Vibrator;

import com.example.android.alarmapp.R;

/**
 * Created by user on 2017-01-25.
 */

public class AlarmActionUtils {
    /* 진동, 벨소리 처리*/

    private static MediaPlayer mMediaPlayer;
    private static AudioManager mAudioManager;
    private static Vibrator vibe;

    public static void startVibrate(Context context, long millis){
        vibe = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        vibe.vibrate(millis);
    }

    public static void stopVibrate(){
        if(vibe!=null && vibe.hasVibrator()) vibe.cancel();
    }

    public static void prepareMusic(Context c){
        stopMusic();
        mMediaPlayer = MediaPlayer.create(c, R.raw.song);
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }

    public static void startMusic(Context c){
        if(mMediaPlayer!=null){
            mMediaPlayer.start();
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
            {
                @Override
                public void onCompletion(MediaPlayer mp)
                {
                    mp.stop();
                    mp.release();
                }
            });
        }

    }

    public static void stopMusic(){
        if(mMediaPlayer != null ){
            if( mMediaPlayer.isPlaying()) mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }


}
