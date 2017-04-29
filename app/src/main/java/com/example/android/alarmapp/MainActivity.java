package com.example.android.alarmapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.example.android.alarmapp.alarm.AlarmManagerTask;
import com.example.android.alarmapp.data.Alarm;
import com.example.android.alarmapp.utils.LocationUtils;

import io.realm.Realm;

public class MainActivity extends AppCompatActivity implements AlarmAdaptor.AlarmAdaptorOnClickHandler {

    private static final String TAG = "MainActivity";
    private Realm mRealm;
    private RecyclerView mAlarmRecylerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRealm = Realm.getDefaultInstance();
        mAlarmRecylerView = (RecyclerView) findViewById(R.id.rv_alarm_list);
        setRecyclerView();

        setFabButton();

        LocationUtils.getInstance(this); // location(lat,lon) 3번 구한 후 중지
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRealm.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_add:
                // AddAlarmActivity 실행
                Intent intent = new Intent(MainActivity.this, AddAlarmActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setRecyclerView(){
        mAlarmRecylerView.setLayoutManager(new LinearLayoutManager(this));
        mAlarmRecylerView.setHasFixedSize(true);
        mAlarmRecylerView.setAdapter(new AlarmAdaptor(
                this, /* context */
                mRealm.where(Alarm.class).findAllAsync(), /* RealmResults<Alarm>*/
                true, /* autoRefresh*/
                mRealm, /* Realm  */
                this /* AlarmAdaptorOnClickHandler */
                ));

        // swipe delete
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT){

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                final long id = (long) viewHolder.itemView.getTag();
                mRealm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        Alarm alarmRealm= realm.where(Alarm.class)
                                .equalTo(Alarm.ID, id)
                                .findFirst();
                        AlarmManagerTask.cancelAlarm(getApplicationContext(), alarmRealm);
                        alarmRealm.deleteFromRealm();
                    }
                });
            }
        }).attachToRecyclerView(mAlarmRecylerView);
    }


    /*  Alarm 추가 */
    private void setFabButton(){
        FloatingActionButton fabButton = (FloatingActionButton) findViewById(R.id.fab);
        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create a new intent to start an AddTaskActivity
                Intent addTaskIntent = new Intent(MainActivity.this, AddAlarmActivity.class);
                startActivity(addTaskIntent);
            }
        });
    }

    /* AlarmAdaptor.AlarmAdaptorOnClickHandler */
    @Override
    public void onClick(final long id) {
        mRealm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm bgRealm) {
                Alarm alarmRealm= bgRealm.where(Alarm.class)
                        .equalTo(Alarm.ID, id)
                        .findFirst();
                alarmRealm.setUsed(!alarmRealm.isUsed());
                Log.d(TAG, "current is Used : " + alarmRealm.isUsed());

            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Alarm alarmRealm= mRealm.where(Alarm.class)
                        .equalTo(Alarm.ID, id)
                        .findFirst();
                if(alarmRealm.isUsed()){
                    AlarmManagerTask.makeAlarm(getApplicationContext(), alarmRealm,
                            AlarmManagerTask.FLAG_INIT_REGISTER);
                    Log.d(TAG , "알람 재등록");
                }else {
                    AlarmManagerTask.cancelAlarm(getApplicationContext(), alarmRealm);
                    Log.d(TAG , "알람 취소");
                }
                Log.d(TAG , "success to transaction");
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                Log.d(TAG , "fail to transaction");
            }
        });

    }
}
