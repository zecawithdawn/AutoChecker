package com.example.kyh.real.AlarmService;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.util.Log;

import com.example.kyh.real.AlarmAlert.AlarmAlertBroadcastReceiver;
import com.example.kyh.real.Database.DbAdapter;
import com.example.kyh.real.Library.Alarm;

/**
 * Created by park on 2015. 4. 5..
 */
public class AlarmService extends Service {
    private DbAdapter mHelper;
    private String uid;
    private String type;
    private int db_ver;
    private boolean set_alarm;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {

        SharedPreferences setting_alarm = getSharedPreferences("alarm", MODE_PRIVATE);

        uid = setting_alarm.getString("uid"," ");
        db_ver = setting_alarm.getInt("db_ver",0);
        set_alarm = setting_alarm.getBoolean("set_alarm",false);
        type = setting_alarm.getString("type","");

        mHelper = new DbAdapter(getApplicationContext(),uid,db_ver,type);
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("AlarmService:onStartCommand", "ENTER");
        SQLiteDatabase db = mHelper.getReadableDatabase();
        Alarm alarm = mHelper.getNext(db);
        if (alarm != null) {
            alarm.schedule(getApplicationContext());
        }
        else {
            Intent myIntent = new Intent(getApplicationContext(), AlarmAlertBroadcastReceiver.class);
            myIntent.putExtra("alarm", new Alarm());

            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, myIntent,PendingIntent.FLAG_CANCEL_CURRENT);
            AlarmManager alarmManager = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);

            alarmManager.cancel(pendingIntent);
        }
        return START_NOT_STICKY;
    }
}
