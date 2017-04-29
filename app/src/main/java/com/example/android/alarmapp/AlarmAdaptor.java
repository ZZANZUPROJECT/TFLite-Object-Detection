package com.example.android.alarmapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.android.alarmapp.alarm.AlarmManagerTask;
import com.example.android.alarmapp.data.Alarm;
import com.example.android.alarmapp.utils.AlarmTimeUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmRecyclerViewAdapter;

/**
 * Created by user on 2017-01-24.
 */

public class AlarmAdaptor extends RealmRecyclerViewAdapter<Alarm, AlarmAdaptor.AlarmAdaptorViewHolder> {

    private static final String TAG ="AlarmAdaptor";

    private final Context mContext;
    private Realm mRealm;
    private AlarmAdaptorOnClickHandler mOnClickHandler;
    private OrderedRealmCollection<Alarm> mData;
    public interface AlarmAdaptorOnClickHandler{
        void onClick(long id);
    }

    public AlarmAdaptor(@NonNull Context context, @Nullable OrderedRealmCollection<Alarm> data,
                        boolean autoUpdate, Realm realm, AlarmAdaptorOnClickHandler handler){
        super(context, data, autoUpdate);
        mData=data;
        mContext=context;
        mRealm=realm;
        mOnClickHandler=handler;
    }


    @Override
    public AlarmAdaptorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.alarm_list_item, parent, false);
        view.setFocusable(true); // ?

        return new AlarmAdaptorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AlarmAdaptorViewHolder holder, int position) {

        final Alarm alarm = getItem(position);

        /* AM or PM*/
        final String ampm = AlarmTimeUtils.getAMPM(alarm.getHour());
        holder.mAmPmTextView.setText(ampm);

        /* Time */
        Calendar c = Calendar.getInstance();
        c.set(c.YEAR,c.MONTH,c.DATE,alarm.getHour(),alarm.getMinute());
        SimpleDateFormat formatter = new SimpleDateFormat(mContext.getString(R.string.format_time), Locale.getDefault());
        String time = formatter.format(c.getTime());
        holder.mTimeTextView.setText(time);

        if(alarm.isUsed()){
            holder.mTimeTextView.setTextColor(Color.BLACK);
            holder.mAmPmTextView.setTextColor(Color.BLACK);
        }else {
            holder.mTimeTextView.setTextColor(Color.LTGRAY);
            holder.mAmPmTextView.setTextColor(Color.LTGRAY);
        }


        /* Weeks */
        if(alarm.isSun()) holder.mSunTextView.setTextColor(Color.RED);
        else holder.mSunTextView.setTextColor(Color.LTGRAY);
        if(alarm.isMon()) holder.mMonTextView.setTextColor(Color.RED);
        else holder.mMonTextView.setTextColor(Color.LTGRAY);
        if(alarm.isTue()) holder.mTueTextView.setTextColor(Color.RED);
        else holder.mTueTextView.setTextColor(Color.LTGRAY);
        if(alarm.isWed()) holder.mWedTextView.setTextColor(Color.RED);
        else holder.mWedTextView.setTextColor(Color.LTGRAY);
        if(alarm.isThr()) holder.mThrTextView.setTextColor(Color.RED);
        else holder.mThrTextView.setTextColor(Color.LTGRAY);
        if(alarm.isFri()) holder.mFriTextView.setTextColor(Color.RED);
        else holder.mFriTextView.setTextColor(Color.LTGRAY);
        if(alarm.isSat()) holder.mSatTextView.setTextColor(Color.RED);
        else holder.mSatTextView.setTextColor(Color.LTGRAY);

        /* save id */
        final long id = alarm.getId();
        holder.itemView.setTag(id);

        /* edit, delete button */
        holder.mEditAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, EditAlarmActivity.class);
                intent.putExtra(Alarm.ID, id);
                mContext.startActivity(intent);
            }
        });

        holder.mDeleteAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRealm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        Alarm alarmRealm= realm.where(Alarm.class)
                                .equalTo(Alarm.ID, id)
                                .findFirst();
                        AlarmManagerTask.cancelAlarm(mContext.getApplicationContext(), alarmRealm);
                        alarmRealm.deleteFromRealm();
                    }
                });
            }
        });


        /* vibration, sound toggle button*/
        if(alarm.isVibrated())
            holder.mVibrateToggleButton.setBackgroundDrawable(getDrawable(mContext, R.drawable.ic_vibration_on));
        else
            holder.mVibrateToggleButton.setBackgroundDrawable(getDrawable(mContext, R.drawable.ic_vibration_off));
        if (alarm.isSounded())
            holder.mSoundToggleButton.setBackgroundDrawable(getDrawable(mContext, R.drawable.ic_sound_on));
        else
            holder.mSoundToggleButton.setBackgroundDrawable(getDrawable(mContext, R.drawable.ic_sound_off));

        /* 알람 진동 여부 */
        holder.mVibrateToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mRealm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        Alarm alarmRealm = realm.where(Alarm.class)
                                .equalTo(Alarm.ID,id)
                                .findFirst();
                        alarmRealm.setVibrated(!alarmRealm.isVibrated());
                        Log.d(TAG, "alarm.isVibrated() : " + alarmRealm.isVibrated());
                    }
                });
            }
        });

        /* 알람 벨소리 여부*/
        holder.mSoundToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mRealm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        Alarm alarmRealm = realm.where(Alarm.class)
                                .equalTo(Alarm.ID,id)
                                .findFirst();
                        alarmRealm.setSounded(!alarmRealm.isSounded());
                        Log.d(TAG, "alarm.isSounded : " + alarmRealm.isSounded());
                    }
                });
            }
        });


    }
    private Drawable getDrawable(Context context, int id){
        Drawable drawable;
        if (Build.VERSION.SDK_INT >= 21) {
            drawable= context.getDrawable(id);
        }else {
            drawable=context.getResources().getDrawable(id);
        }
        return drawable;
    }

    public class AlarmAdaptorViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener{

        TextView mAmPmTextView;
        TextView mTimeTextView;

        TextView mSunTextView;
        TextView mMonTextView;
        TextView mTueTextView;
        TextView mWedTextView;
        TextView mThrTextView;
        TextView mFriTextView;
        TextView mSatTextView;

        ImageButton mEditAlarmButton;
        ImageButton mDeleteAlarmButton;

        ToggleButton mVibrateToggleButton;
        ToggleButton mSoundToggleButton;

        public AlarmAdaptorViewHolder(View itemView) {
            super(itemView);
            mAmPmTextView = (TextView) itemView.findViewById(R.id.tv_am_pm);
            mTimeTextView = (TextView) itemView.findViewById(R.id.tv_time);

            mSunTextView = (TextView) itemView.findViewById(R.id.tv_sun);
            mMonTextView = (TextView) itemView.findViewById(R.id.tv_mon);
            mTueTextView = (TextView) itemView.findViewById(R.id.tv_tue);
            mWedTextView = (TextView) itemView.findViewById(R.id.tv_wed);
            mThrTextView = (TextView) itemView.findViewById(R.id.tv_thr);
            mFriTextView = (TextView) itemView.findViewById(R.id.tv_fri);
            mSatTextView = (TextView) itemView.findViewById(R.id.tv_sat);

            mEditAlarmButton = (ImageButton) itemView.findViewById(R.id.btn_edit_alarm);
            mDeleteAlarmButton = (ImageButton) itemView.findViewById(R.id.btn_delete_alarm);

            mVibrateToggleButton = (ToggleButton) itemView.findViewById(R.id.tgbtn_vibrate);
            mSoundToggleButton = (ToggleButton) itemView.findViewById(R.id.tgbtn_sound);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            Alarm alarm = mData.get(adapterPosition);

            if(alarm.isUsed()){
                mTimeTextView.setTextColor(Color.LTGRAY);
                mAmPmTextView.setTextColor(Color.LTGRAY);
            }else {
                mTimeTextView.setTextColor(Color.BLACK);
                mAmPmTextView.setTextColor(Color.BLACK);
            }
            long id = alarm.getId();
            mOnClickHandler.onClick(id);
        }

    }
}
