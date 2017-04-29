package com.example.android.alarmapp;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
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
import com.example.android.alarmapp.utils.LocationUtils;

import io.realm.Realm;

public class EditAlarmActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    private final static String TAG = "EditAlarmActivity";

    private boolean isUsed;

    private LocationUtils mLocation;

    private Realm mRealm;
    private Alarm mAlarm;

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
        setContentView(R.layout.activity_edit_alarm);
        mRealm = Realm.getDefaultInstance();

        long id = getIntent().getLongExtra(Alarm.ID,0);
        mAlarm = mRealm.where(Alarm.class)
                .equalTo(Alarm.ID,id)
                .findFirst();

        initView();
        setListener();

        bindData(mAlarm);

        // TODO gps load 시간차 문제
        mLocation=LocationUtils.getInstance(this);

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRealm.close();
        //if(mLocation!=null) mLocation.isStop(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_update:
                updateAlarmInDB(mAlarm);
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

    /* alarm data를 View에 바인딩 */
    private void bindData(Alarm alarmRealm){
        if (Build.VERSION.SDK_INT >= 23) {
            mTimePicker.setHour(alarmRealm.getHour());
            mTimePicker.setMinute(alarmRealm.getMinute());
        }else{
            mTimePicker.setCurrentHour(alarmRealm.getHour());
            mTimePicker.setCurrentMinute(alarmRealm.getMinute());
        }

        if(alarmRealm.isSun()) mSunToggleButton.setChecked(true);
        if(alarmRealm.isMon()) mMonToggleButton.setChecked(true);
        if(alarmRealm.isTue()) mTueToggleButton.setChecked(true);
        if(alarmRealm.isWed()) mWedToggleButton.setChecked(true);
        if(alarmRealm.isThr()) mThrToggleButton.setChecked(true);
        if(alarmRealm.isFri()) mFriToggleButton.setChecked(true);
        if(alarmRealm.isSat()) mSatToggleButton.setChecked(true);

        mVibrateSwitch.setChecked(alarmRealm.isVibrated());
        mSoundSwitch.setChecked(alarmRealm.isSounded());
        mRepeatSwitch.setEnabled(mSunToggleButton.isChecked()||mMonToggleButton.isChecked()||
                mTueToggleButton.isChecked()||mWedToggleButton.isChecked()||
                mThrToggleButton.isChecked()||mFriToggleButton.isChecked()||
                mSatToggleButton.isChecked());
        mRepeatSwitch.setChecked(alarmRealm.isRepeated());
        isUsed=Alarm.DEFAULT_USED;
        mMemoEditText.setText(alarmRealm.getMemo());
    }

    private void updateAlarmInDB(final Alarm alarmRealm){

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
