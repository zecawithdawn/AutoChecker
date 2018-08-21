package com.example.kyh.real.AlarmAlert;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.kyh.real.AlarmService.AlarmServiceBroadcastReceiver;
import com.example.kyh.real.Library.Alarm;


/**
 * Created by park on 2015. 4. 5..
 */
public class AlarmAlertBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent alarmServiceIntent = new Intent(context, AlarmServiceBroadcastReceiver.class);
        context.sendBroadcast(alarmServiceIntent, null);

        StaticWakeLock.lockOn(context);

        Bundle bundle = intent.getExtras();
        final Alarm alarm = (Alarm) bundle.getSerializable("alarm");

        Intent alarmAlertActivityIntent;
        alarmAlertActivityIntent = new Intent(context, AlarmAlertActivity.class);
        alarmAlertActivityIntent.putExtra("alarm", alarm);
        alarmAlertActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        context.startActivity(alarmAlertActivityIntent);
    }
}
