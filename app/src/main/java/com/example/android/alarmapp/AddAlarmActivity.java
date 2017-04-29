package com.example.android.alarmapp;

import android.content.Context;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.ToggleButton;

import com.example.android.alarmapp.alarm.AlarmManagerTask;
import com.example.android.alarmapp.data.Alarm;
import com.example.android.alarmapp.utils.AlarmActionUtils;
import com.example.android.alarmapp.utils.LocationUtils;

import java.util.List;

import io.realm.Realm;

public class AddAlarmActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener{
    private final static String TAG = "AddAlarmActivity";

    private Realm mRealm;

    private LocationUtils mLocation;

    private TimePicker mTimePicker;
    private ToggleButton mSunToggleButton;
    private ToggleButton mMonToggleButton;
    private ToggleButton mTueToggleButton;
    private ToggleButton mWedToggleButton;
    private ToggleButton mThrToggleButton;
    private ToggleButton mFriToggleButton;
    private ToggleButton mSatToggleButton;

    private Switch mVibrateSwitch;
    private Switch mSoundSwitch;
    private Switch mRepeatSwitch;

    private EditText mMemoEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_alarm);

        ActionBar actionBar = this.getSupportActionBar();
        // Set the action bar back button to look like an up button
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        initView();
        setListener();

        mRealm=Realm.getDefaultInstance();

        mLocation=LocationUtils.getInstance(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRealm.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_save:
                addAlarmInDB();
                NavUtils.navigateUpFromSameTask(this);
                return true;

            case R.id.action_cancel:
                NavUtils.navigateUpFromSameTask(this);
                return true;

            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /* View id 등록 */
    private void initView(){
        mTimePicker = (TimePicker) findViewById(R.id.tp_time_picker);
        mSunToggleButton = (ToggleButton) findViewById(R.id.tgbtn_sun_repeat);
        mMonToggleButton = (ToggleButton) findViewById(R.id.tgbtn_mon_repeat);
        mTueToggleButton = (ToggleButton) findViewById(R.id.tgbtn_tue_repeat);
        mWedToggleButton = (ToggleButton) findViewById(R.id.tgbtn_wed_repeat);
        mThrToggleButton = (ToggleButton) findViewById(R.id.tgbtn_thr_repeat);
        mFriToggleButton = (ToggleButton) findViewById(R.id.tgbtn_fri_repeat);
        mSatToggleButton = (ToggleButton) findViewById(R.id.tgbtn_sat_repeat);
        mVibrateSwitch = (Switch) findViewById(R.id.swc_vibrate);
        mSoundSwitch = (Switch) findViewById(R.id.swc_sound);
        mRepeatSwitch = (Switch) findViewById(R.id.swc_repeat);
        mMemoEditText = (EditText) findViewById(R.id.et_memo);
    }

    private void setListener(){
        mSunToggleButton.setOnCheckedChangeListener(this);
        mMonToggleButton.setOnCheckedChangeListener(this);
        mTueToggleButton.setOnCheckedChangeListener(this);
        mWedToggleButton.setOnCheckedChangeListener(this);
        mThrToggleButton.setOnCheckedChangeListener(this);
        mFriToggleButton.setOnCheckedChangeListener(this);
        mSatToggleButton.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(mSunToggleButton.isChecked()||mMonToggleButton.isChecked()||
                mTueToggleButton.isChecked()||mWedToggleButton.isChecked()||
                mThrToggleButton.isChecked()||mFriToggleButton.isChecked()||
                mSatToggleButton.isChecked()){
            mRepeatSwitch.setEnabled(true);
        }else {
            mRepeatSwitch.setEnabled(false);
        }
    }

    private void addAlarmInDB(){

        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                int hour;
                int minute;
                if (Build.VERSION.SDK_INT >= 23) {
                    hour=mTimePicker.getHour();
                    minute=mTimePicker.getMinute();
                }else{
                    hour = mTimePicker.getCurrentHour();
                    minute=mTimePicker.getCurrentMinute();
                }
                long alarmtime=Alarm.DEFAULT_ALARM_TIME;
                String memo = mMemoEditText.getText().toString();
                boolean isUsed= Alarm.DEFAULT_USED;

                long pk = Alarm.getNextPK(realm);

                Alarm alarmRealm = realm.createObject(Alarm.class, pk);
                alarmRealm.setHour(hour);
                alarmRealm.setMinute(minute);
                alarmRealm.setAlarmTime(alarmtime);
                alarmRealm.setMemo(memo);
                alarmRealm.setUsed(isUsed);
                alarmRealm.setVibrated(mVibrateSwitch.isChecked());
                alarmRealm.setSounded(mSoundSwitch.isChecked());
                alarmRealm.setRepeated(mRepeatSwitch.isChecked());

                alarmRealm.setSun(mSunToggleButton.isChecked());
                alarmRealm.setMon(mMonToggleButton.isChecked());
                alarmRealm.setTue(mTueToggleButton.isChecked());
                alarmRealm.setWed(mWedToggleButton.isChecked());
                alarmRealm.setThr(mThrToggleButton.isChecked());
                alarmRealm.setFri(mFriToggleButton.isChecked());
                alarmRealm.setSat(mSatToggleButton.isChecked());

                Log.d(TAG, "lat: "+mLocation.getLat() +", lon : "+mLocation.getLon());
                alarmRealm.setLat(mLocation.getLat());
                alarmRealm.setLon(mLocation.getLon());

                AlarmManagerTask.makeAlarm(getApplicationContext(), alarmRealm,
                        AlarmManagerTask.FLAG_INIT_REGISTER);
            }
        });

    }

}
