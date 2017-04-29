package com.example.android.alarmapp.data;

import io.realm.DynamicRealm;
import io.realm.RealmMigration;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;

/**
 * Created by user on 2017-01-25.
 */

public class Migration implements RealmMigration {
    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {

        RealmSchema schema = realm.getSchema();
        /************************************************
         version 0

         Alarm Schema

         @PrimaryKey
         private long id;

         private int hour;
         private int minute;
         private RealmList<Week> weeks;
         private long alarmTime;
         private String memo;
         private double lat;
         private double lon;
         private boolean isVibrated;
         private boolean isSounded;
         private boolean isRepeated;
         private boolean isUsed;

         Week Schema
         private String week;

         version 1

         @PrimaryKey
         private long id;

         private long alarmId;  // add column

         private boolean sun;   // add weeks
         private boolean mon;
         private boolean tue;
         private boolean wed;
         private boolean thr;
         private boolean fri;
         private boolean sat;

         private int hour;
         private int minute;

         // remove weeks

         private long alarmTime;
         private String memo;
         private double lat;
         private double lon;
         private boolean isVibrated;
         private boolean isSounded;
         private boolean isRepeated;

         @index
         private boolean isUsed; // add index

         //remove weeks schema

         version 2

         @PrimaryKey
         private long id;

         //remove alarmId

         private boolean sun;
         private boolean mon;
         private boolean tue;
         private boolean wed;
         private boolean thr;
         private boolean fri;
         private boolean sat;

         private int hour;
         private int minute;

         private long alarmTime;
         private String memo;
         private double lat;
         private double lon;
         private boolean isVibrated;
         private boolean isSounded;
         private boolean isRepeated;

         @index
         private boolean isUsed;

         ************************************************/
        if(oldVersion==0){
            RealmObjectSchema alarmSchema = schema.get("Alarm");
            alarmSchema
                    .removeField("weeks")
                    .addField("alarmId",long.class)
                    .addField("sun",boolean.class)
                    .addField("mon",boolean.class)
                    .addField("tue",boolean.class)
                    .addField("wed",boolean.class)
                    .addField("thr",boolean.class)
                    .addField("fri",boolean.class)
                    .addField("sat",boolean.class)
                    .addIndex("alarmId")
                    .addIndex("isUsed");

            schema.remove("week");
            oldVersion++;
        }

        if (oldVersion==1){
            RealmObjectSchema alarmSchema = schema.get("Alarm");
            alarmSchema
                    .removeField("alarmId");

            oldVersion++;
        }

    }
}
