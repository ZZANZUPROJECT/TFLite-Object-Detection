package com.example.android.alarmapp.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

/**
 * Created by user on 2017-01-25.
 */

public class AlarmProvider extends ContentProvider{

    private final static String TAG = "AlarmProvider";

    public static final int CODE_ALARM=100;

    public static final int CODE_ALARM_WITH_ID=101;

    public static final String[] sColumns = new String[]{
            "id", "hour", "minute", "sun", "tue", "wed", "thr", "fri", "sat",
            "alarmTime", "memo", "lat", "lon", "isVibrated","isSounded", "isRepeated"
    };

    private static final UriMatcher sUriMatch = buildUriMatcher();
    private Realm mRealm;

    private static UriMatcher buildUriMatcher(){
        final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = Alarm.CONTENT_AUTHORITY;

        uriMatcher.addURI(authority, Alarm.PATH_ALARM, CODE_ALARM);
        uriMatcher.addURI(authority, Alarm.PATH_ALARM+"/#", CODE_ALARM_WITH_ID);
        return uriMatcher;
    }

    @Override
    public boolean onCreate() {

        Realm.init(getContext());
        RealmConfiguration realmConfig = new RealmConfiguration.Builder()
                .schemaVersion(2)
                .migration(new Migration())
                .build();

        //Realm.deleteRealm(realmConfig); // Delete Realm between app restarts.
        Realm.setDefaultConfiguration(realmConfig);

        mRealm=Realm.getDefaultInstance();
        Log.d(TAG, "Ream init Completed in Content Provider");
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        switch (sUriMatch.match(uri)){
            case CODE_ALARM:
                /* content://com.example.android.alarmapp/alarms */
                RealmResults<Alarm> alarms = mRealm.where(Alarm.class).findAll();

                MatrixCursor matrixCursor = new MatrixCursor(sColumns);
                for (Alarm alarm : alarms){
                    Object[] rowData=
                            new Object[]{
                                alarm.getId(), alarm.getHour(), alarm.getMinute(),
                                    alarm.isSun(),alarm.isMon(),alarm.isTue(),alarm.isWed(),
                                    alarm.isThr(), alarm.isFri(), alarm.isSat(),
                                    alarm.getAlarmTime(), alarm.getMemo(), alarm.getLat(), alarm.getLon(),
                                    alarm.isVibrated(),alarm.isSounded(), alarm.isRepeated()
                            };
                    matrixCursor.addRow(rowData);
                }

                matrixCursor.setNotificationUri(getContext().getContentResolver(), uri);
                return matrixCursor;
            case CODE_ALARM_WITH_ID:
                /* content://com.example.android.alarmapp/alarms/{id} */
                final long id = Long.parseLong(uri.getLastPathSegment());
                Alarm alarm = mRealm.where(Alarm.class).equalTo(Alarm.ID,id).findFirst();

                MatrixCursor matrixCursorWithId = new MatrixCursor(sColumns);
                Object[] rowData=
                        new Object[]{
                                alarm.getId(), alarm.getHour(), alarm.getMinute(),
                                alarm.isSun(),alarm.isMon(),alarm.isTue(),alarm.isWed(),
                                alarm.isThr(), alarm.isFri(), alarm.isSat(),
                                alarm.getAlarmTime(), alarm.getMemo(), alarm.getLat(), alarm.getLon(),
                                alarm.isVibrated(),alarm.isSounded(), alarm.isRepeated()
                        };
                matrixCursorWithId.addRow(rowData);

                matrixCursorWithId.setNotificationUri(getContext().getContentResolver(), uri);
                return matrixCursorWithId;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        switch (sUriMatch.match(uri)){
            case CODE_ALARM:
                long id = Alarm.getNextPK(mRealm);
                mRealm.beginTransaction();
                Alarm alarm = mRealm.createObject(Alarm.class);
                alarm.setId(id);
                alarm.setHour(values.getAsInteger(sColumns[1]));
                alarm.setMinute(values.getAsInteger(sColumns[2]));
                alarm.setSun(values.getAsBoolean(sColumns[3]));
                alarm.setMon(values.getAsBoolean(sColumns[4]));
                alarm.setTue(values.getAsBoolean(sColumns[5]));
                alarm.setWed(values.getAsBoolean(sColumns[6]));
                alarm.setThr(values.getAsBoolean(sColumns[7]));
                alarm.setFri(values.getAsBoolean(sColumns[8]));
                alarm.setSat(values.getAsBoolean(sColumns[9]));
                alarm.setAlarmTime(values.getAsLong(sColumns[10]));
                alarm.setMemo(values.getAsString(sColumns[11]));
                alarm.setLat(values.getAsDouble(sColumns[12]));
                alarm.setLon(values.getAsDouble(sColumns[13]));
                alarm.setVibrated(values.getAsBoolean(sColumns[14]));
                alarm.setSounded(values.getAsBoolean(sColumns[15]));
                alarm.setRepeated(values.getAsBoolean(sColumns[16]));
                mRealm.commitTransaction();
                return Uri.withAppendedPath(uri, String.valueOf(id));
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        switch (sUriMatch.match(uri)){
            case CODE_ALARM:
                long id = Alarm.getNextPK(mRealm);
                mRealm.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        Alarm alarm = mRealm.createObject(Alarm.class);
                        alarm.setId(++id);
                        alarm.setHour(value.getAsInteger(sColumns[1]));
                        alarm.setMinute(value.getAsInteger(sColumns[2]));
                        alarm.setSun(value.getAsBoolean(sColumns[3]));
                        alarm.setMon(value.getAsBoolean(sColumns[4]));
                        alarm.setTue(value.getAsBoolean(sColumns[5]));
                        alarm.setWed(value.getAsBoolean(sColumns[6]));
                        alarm.setThr(value.getAsBoolean(sColumns[7]));
                        alarm.setFri(value.getAsBoolean(sColumns[8]));
                        alarm.setSat(value.getAsBoolean(sColumns[9]));
                        alarm.setAlarmTime(value.getAsLong(sColumns[10]));
                        alarm.setMemo(value.getAsString(sColumns[11]));
                        alarm.setLat(value.getAsDouble(sColumns[12]));
                        alarm.setLon(value.getAsDouble(sColumns[13]));
                        alarm.setVibrated(value.getAsBoolean(sColumns[14]));
                        alarm.setSounded(value.getAsBoolean(sColumns[15]));
                        alarm.setRepeated(value.getAsBoolean(sColumns[16]));
                    }
                }finally {
                    mRealm.commitTransaction();
                }
                return values.length;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        switch (sUriMatch.match(uri)){

            /* delete All Alarm Object */
            /* content://com.example.android.alarmapp/alarms */
            case CODE_ALARM:
                boolean completed;
                RealmResults<Alarm> realmResults;
                mRealm.beginTransaction();
                try{
                    realmResults = mRealm.where(Alarm.class).findAll();
                    completed = realmResults.deleteAllFromRealm();
                }finally {
                    mRealm.commitTransaction();
                }

                if (completed) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return completed? realmResults.size() : 0;

            /* delete Alarm Object with ID*/
            /* content://com.example.android.alarmapp/alarms/{id} */
            case CODE_ALARM_WITH_ID:
                final long id = Long.parseLong(uri.getLastPathSegment());

                mRealm.beginTransaction();
                try{
                    completed = mRealm.where(Alarm.class)
                            .equalTo(Alarm.ID, id)
                            .findAll()
                            .deleteAllFromRealm();

                }finally {
                    mRealm.commitTransaction();
                }

                if (completed) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return completed? 1 : 0;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        switch (sUriMatch.match(uri)){
            case CODE_ALARM_WITH_ID:
                boolean completed;
                mRealm.beginTransaction();
                try {
                    Alarm alarm = mRealm.where(Alarm.class)
                            .equalTo(Alarm.ID,values.getAsLong(sColumns[0]))
                            .findFirst();
                    alarm.setHour(values.getAsInteger(sColumns[1]));
                    alarm.setMinute(values.getAsInteger(sColumns[2]));
                    alarm.setSun(values.getAsBoolean(sColumns[3]));
                    alarm.setMon(values.getAsBoolean(sColumns[4]));
                    alarm.setTue(values.getAsBoolean(sColumns[5]));
                    alarm.setWed(values.getAsBoolean(sColumns[6]));
                    alarm.setThr(values.getAsBoolean(sColumns[7]));
                    alarm.setFri(values.getAsBoolean(sColumns[8]));
                    alarm.setSat(values.getAsBoolean(sColumns[9]));
                    alarm.setAlarmTime(values.getAsLong(sColumns[10]));
                    alarm.setMemo(values.getAsString(sColumns[11]));
                    alarm.setLat(values.getAsDouble(sColumns[12]));
                    alarm.setLon(values.getAsDouble(sColumns[13]));
                    alarm.setVibrated(values.getAsBoolean(sColumns[14]));
                    alarm.setSounded(values.getAsBoolean(sColumns[15]));
                    alarm.setRepeated(values.getAsBoolean(sColumns[16]));
                    completed=true;
                }catch (Exception e){
                    completed=false;
                } finally {
                    mRealm.commitTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return completed? 1 : 0;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }
}
