package com.example.android.alarmapp;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.android.alarmapp.alarm.DeviceWakeUp;
import com.example.android.alarmapp.data.Alarm;
import com.example.android.alarmapp.utils.AlarmActionUtils;
import com.example.android.alarmapp.utils.AlarmTimeUtils;
import com.example.android.alarmapp.utils.LocationUtils;

import org.w3c.dom.Text;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import io.realm.Realm;


/*
*
*  알람시 표시되는 화면
*
*  시간 + 메모 + 위치정보 + 알람 중지 버튼
*
* */
public class DetailAlarmActivity extends AppCompatActivity {

    private static final String TAG ="DetailAlarmActivity";
    private Realm mRealm;

    TextView mAmPmTextView;
    TextView mTimeTextView;

    TextView mLocationTextView;

    TextView mMemoTextView;


    Button mAlarmStopButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_alarm);
        mRealm=Realm.getDefaultInstance();

        long id=-1;
        if(getIntent().hasExtra(Alarm.ID)){
            id=getIntent().getLongExtra(Alarm.ID,-1);
        }

        Alarm alarm = mRealm.where(Alarm.class)
                .equalTo(Alarm.ID,id)
                .findFirst();

        initView(alarm);



    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AlarmActionUtils.stopVibrate();
        AlarmActionUtils.stopMusic();
        mRealm.close();
    }

    private void initView(Alarm alarm){

        mAmPmTextView = (TextView) findViewById(R.id.tv_am_pm);
        mTimeTextView = (TextView) findViewById(R.id.tv_time);

        mMemoTextView = (TextView) findViewById(R.id.tv_memo);

        mLocationTextView = (TextView) findViewById(R.id.tv_location);

        mAlarmStopButton = (Button) findViewById(R.id.btn_alarm_stop);


        /* Location Fetch by lat, lon */
        Alarm alarmNotRealm = mRealm.copyFromRealm(alarm);
        new LocationFetchTask().execute(alarmNotRealm);

        /* AM or PM*/
        final String ampm = AlarmTimeUtils.getAMPM(alarm.getHour());
        mAmPmTextView.setText(ampm);

        /* Time */
        Calendar c = Calendar.getInstance();
        c.set(c.YEAR,c.MONTH,c.DATE,alarm.getHour(),alarm.getMinute());
        SimpleDateFormat formatter = new SimpleDateFormat(getString(R.string.format_time), Locale.getDefault());
        String time = formatter.format(c.getTime());
        mTimeTextView.setText(time);

        /* Memo */
        mMemoTextView.setText(alarm.getMemo());


        /* Alarm Stop */
        mAlarmStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlarmActionUtils.stopVibrate();
                AlarmActionUtils.stopMusic();
            }
        });
    }

    class LocationFetchTask extends AsyncTask<Alarm, Void, String>{

        @Override
        protected String doInBackground(Alarm... params) {
            Alarm alarm = params[0];
            double lat=alarm.getLat();
            double lon=alarm.getLon();
            while (lat==0.0&&lon==0.0){
                lat=LocationUtils.getInstance(getApplicationContext()).getLat();
                lon=LocationUtils.getInstance(getApplicationContext()).getLon();
                Log.d(TAG, "lat : "+lat+", lon : "+lon);
            }
            return LocationUtils.getAddressFromCoordinate(
                    DetailAlarmActivity.this, lat, lon);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            mLocationTextView.setText(s);
        }
    }

}
