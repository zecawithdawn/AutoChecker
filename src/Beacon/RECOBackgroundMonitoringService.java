package com.example.kyh.real.Beacon;

/**
 * Created by kyh on 2015. 1. 19..
 */

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.kyh.real.Library.SSLTask;
import com.example.kyh.real.Library.TaskListener;
import com.example.kyh.real.R;
import com.example.kyh.real.S_F.S_Total_MainActivity;
import com.perples.recosdk.RECOBeaconManager;
import com.perples.recosdk.RECOBeaconRegion;
import com.perples.recosdk.RECOBeaconRegionState;
import com.perples.recosdk.RECOMonitoringListener;
import com.perples.recosdk.RECOServiceConnectListener;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * RECOBackgroundMonitoringService is to monitor regions in the background.
 * <p/>
 * RECOBackgroundMonitoringService는 백그라운드에서 monitoring을 수행합니다.
 */
public class RECOBackgroundMonitoringService extends Service implements RECOMonitoringListener, RECOServiceConnectListener, TaskListener {
    private long mScanDuration = 1 * 1000L;
    private long mSleepDuration = 1 * 1000L;
    private long mRegionExpirationTime = 10L;
    private int mNotificationID = 9999;

    private RECOBeaconManager mRecoManager;
    private ArrayList<RECOBeaconRegion> mRegions;

    private String uid;
    private String course_id;
    private String group_id;
    private int db_ver;
    private int timeslot_id;
    private int end_time;
    private String beacon_id;
    private int start_time;
    private float token = 0;
    private float duration = 1;


    //new
    private Messenger messageHandler;

    @Override
    public void onCreate() {
        Log.i("RECOBackgroundMonitoringService", "onCreate()");
        super.onCreate();

        /**
         * Create an instance of RECOBeaconManager (to set ranging timeout in the background.)
         * If you do not want to set ranging timeout in the backgournd, create an instance:
         * 		mRecoManager = RECOBeaconManager.getInstance(getApplicationContext(), false);
         * WARNING: It will affect the battery consumption.
         *
         * RECOBeaconManager 인스턴스틀 생성합니다. (백그라운드 ranging timeout 설정)
         * 백그라운드 ranging timeout을 설정하고 싶지 않으시다면, 다음과 같이 생성하시기 바랍니다.
         * 		mRecoManager = RECOBeaconManager.getInstance(getApplicationContext(), false);
         * 주의: false로 설정 시, 배터리 소모량이 증가합니다.
         */
        mRecoManager = RECOBeaconManager.getInstance(getApplicationContext(), true);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("RECOBackgroundMonitoringService", "onStartCommand()");
        if (intent.getStringExtra("uid") != null) {
            uid = intent.getStringExtra("uid");
            course_id = intent.getStringExtra("course_id");
            group_id = intent.getStringExtra("group_id");
            db_ver = intent.getIntExtra("db_ver", 0);
            timeslot_id = intent.getIntExtra("timeslot_id", 0);
            end_time = intent.getIntExtra("end_time", 0);
            beacon_id = intent.getStringExtra("beacon_id");
            start_time = intent.getIntExtra("start_time", 0);

            //new
            messageHandler = (Messenger) intent.getExtras().get("Mes");
        }
//        Log.d ("Student uid: ", uid);
//        Log.d ("course_id: ", course_id);
//        Log.d ("group_id", group_id);
//        Log.d ("db_ver", ""+db_ver);
//        Log.d ("timeslot_id", ""+timeslot_id);
//        Log.d ("end_time", ""+end_time);
//        Log.d ("beacon_id", beacon_id);
        setDuration();
        this.bindRECOService();

        //this should be set to run in the background.
        //background에서 동작하기 위해서는 반드시 실행되어야 합니다.
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.i("RECOBackgroundMonitoringService", "onDestroy()");
        this.tearDown();
        super.onDestroy();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.i("RECOBackgroundMonitoringService", "onTaskRemoved()");
        super.onTaskRemoved(rootIntent);
    }

    private void bindRECOService() {
        Log.i("RECOBackgroundMonitoringService", "bindRECOService()");

        mRegions = new ArrayList<RECOBeaconRegion>();
        this.generateBeaconRegion();

        mRecoManager.setMonitoringListener(this);
        mRecoManager.bind(this);
    }

    private void generateBeaconRegion() {
        Log.i("RECOBackgroundMonitoringService", "generateBeaconRegion()");

        RECOBeaconRegion recoRegion;

        recoRegion = new RECOBeaconRegion(S_Total_MainActivity.RECO_UUID, "RECO Sample Region");
        recoRegion.setRegionExpirationTimeMillis(mRegionExpirationTime);
        mRegions.add(recoRegion);
    }

    private void startMonitoring() {
        Log.i("RECOBackgroundMonitoringService", "startMonitoring()");

        mRecoManager.setScanPeriod(mScanDuration);
        mRecoManager.setSleepPeriod(mSleepDuration);

        for (RECOBeaconRegion region : mRegions) {
            try {
                mRecoManager.startMonitoringForRegion(region);
            } catch (RemoteException e) {
                Log.e("RECOBackgroundMonitoringService", "RemoteException has occured while executing RECOManager.startMonitoringForRegion()");
                e.printStackTrace();
            } catch (NullPointerException e) {
                Log.e("RECOBackgroundMonitoringService", "NullPointerException has occured while executing RECOManager.startMonitoringForRegion()");
                e.printStackTrace();
            }
        }
    }

    private void stopMonitoring() {
        Log.i("RECOBackgroundMonitoringService", "stopMonitoring()");

        for (RECOBeaconRegion region : mRegions) {
            try {
                mRecoManager.stopMonitoringForRegion(region);
            } catch (RemoteException e) {
                Log.e("RECOBackgroundMonitoringService", "RemoteException has occured while executing RECOManager.stopMonitoringForRegion()");
                e.printStackTrace();
            } catch (NullPointerException e) {
                Log.e("RECOBackgroundMonitoringService", "NullPointerException has occured while executing RECOManager.stopMonitoringForRegion()");
                e.printStackTrace();
            }
        }
    }

    private void tearDown() {
        Log.i("RECOBackgroundMonitoringService", "tearDown()");
        this.stopMonitoring();

        try {
            mRecoManager.unbind();
        } catch (RemoteException e) {
            Log.e("RECOBackgroundMonitoringService", "RemoteException has occured while executing unbind()");
            e.printStackTrace();
        }
    }

    @Override
    public void onServiceConnect() {
        Log.i("RECOBackgroundMonitoringService", "onServiceConnect()");
        this.startMonitoring();
        //chkTimeAndDestroy();
        //Write the code when RECOBeaconManager is bound to RECOBeaconService

    }

    @Override
    public void didDetermineStateForRegion(RECOBeaconRegionState state, RECOBeaconRegion region) {
        Log.i("RECOBackgroundMonitoringService", "didDetermineStateForRegion()");
        //chkTimeAndDestroy();
        //Write the code when the state of the monitored region is changed
    }

    @Override
    public void didEnterRegion(RECOBeaconRegion region) {
        Log.i("RECOBackgroundMonitoringService", "didEnterRegion() - " + region.getUniqueIdentifier());
        //this.popupNotification("Inside of " + region.getUniqueIdentifier());
        //chkTimeAndDestroy();

        if (sendToServer()) {
            //new
            Message msg = Message.obtain();
            token = 0.5f;
            msg.arg1 = (int) (token / 1);
            msg.arg2 = (int) ((token % 1) * 1000);
//            try {
//                messageHandler.send(msg);
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
            token = 0;
        } else
            Log.d("Send Token Failed", "");
    }

    @Override
    public void didExitRegion(RECOBeaconRegion region) {
        Log.i("RECOBackgroundMonitoringService", "didExitRegion() - " + region.getUniqueIdentifier());
        //this.popupNotification("Outside of " + region.getUniqueIdentifier());
        //chkTimeAndDestroy();
        //Write the code when the device is exit the region
    }

    @Override
    public void didStartMonitoringForRegion(RECOBeaconRegion region) {
        Log.i("RECOBackgroundMonitoringService", "didStartMonitoringForRegion() - " + region.getUniqueIdentifier());
        //chkTimeAndDestroy();
        //Write the code when starting monitoring the region is started successfully
    }

    private void popupNotification(String msg) {
        Log.i("RECOBackgroundMonitoringService", "popupNotification()");
        String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.KOREA).format(new Date());
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this).setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(msg + " " + currentTime)
                .setContentText(msg);

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        builder.setStyle(inboxStyle);
        nm.notify(mNotificationID, builder.build());
        mNotificationID = (mNotificationID - 1) % 1000 + 9000;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // This method is not used
        return null;
    }

    private void chkTimeAndDestroy() {
        if (getCurrentTime() > (end_time - 2))
            onDestroy();
    }

    private int getCurrentTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("HHmm", Locale.KOREA);
        Calendar calendar = Calendar.getInstance();
        Date today = new Date();

        return Integer.parseInt(calendar.get(Calendar.DAY_OF_WEEK) + formatter.format(today));
    }

    private boolean sendToServer() {
        boolean ret = true;
        JSONObject reqDB = new JSONObject();
        final String DOMAIN = "https://166.104.245.43/update_attendance.php";

        token += duration;

        try {
            reqDB.put("uid", uid);
            reqDB.put("course_id", course_id);
            reqDB.put("group_id", group_id);
            reqDB.put("timeslot_id", timeslot_id);
            reqDB.put("token", token);
            SSLTask sslTask = new SSLTask(DOMAIN, reqDB, this);
            sslTask.execute();
        } catch (JSONException e) {
            e.printStackTrace();
            ret = false;
        } catch (Exception e) {
            e.printStackTrace();
            ret = false;
        }

        return ret;
    }

    private void setDuration() {
        int howLong = end_time - start_time;

        if (howLong > 0)
            duration = (float) 2 / (float) howLong;
    }

    @Override
    public void onReceived(JSONObject jsonData) {

    }

    @Override
    public void onCanceled() {

    }
}
