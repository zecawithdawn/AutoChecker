package com.example.kyh.real.Library;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.kyh.real.AlarmAlert.AlarmAlertBroadcastReceiver;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by park on 2015. 4. 5..
 */
public class Alarm implements Serializable {
    private String id;
    private int time;
    private boolean vibrate = true;
    private String ringtonPath;

    public Alarm () {}

    public void schedule (Context context) {
        Intent myIntent = new Intent(context, AlarmAlertBroadcastReceiver.class);
        myIntent.putExtra("alarm", this);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, myIntent,PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, getAlarmTime().getTimeInMillis(), pendingIntent);
    }

    public Calendar getAlarmTime() {
        Calendar calendar = Calendar.getInstance();
        Log.d ("MINUTE:", "" + time %100);
        Log.d ("HOUR:" , "" + (time / 100) % 100 );
        Log.d("DAY:", "" + time / 10000);
        calendar.set(Calendar.MINUTE, time % 100);
        calendar.set(Calendar.HOUR_OF_DAY, (time / 100) % 100);
        calendar.set(Calendar.DAY_OF_WEEK, time / 10000);

        if (getCurrentTime() >= time)
            calendar.add(Calendar.DATE, 7);

        return calendar;
    }

    public int getCurrentTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("HHmm", Locale.KOREA);
        Calendar calendar = Calendar.getInstance();
        Date today = new Date();

        return Integer.parseInt(calendar.get(Calendar.DAY_OF_WEEK) + formatter.format(today));
    }

    public void setId (String id) {
        this.id = id;
    }

    public String getId () {
        return id;
    }

    public void setTime (int time) {
        this.time = time;
    }

    public int getTime () {
        return time;
    }

    public void setVibrate (boolean vibrate) { this.vibrate = vibrate; }

    public boolean getVibrate () { return vibrate; }

    public void setRingtonPath (String path) { this.ringtonPath = path; }

    public String getRingtonPath () { return ringtonPath; }
}
