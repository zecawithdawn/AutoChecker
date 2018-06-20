package com.example.kyh.real.S_F;

import android.app.ActivityManager;
import android.app.ApplicationErrorReport;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kyh.real.Beacon.RECOBackgroundMonitoringService;
import com.example.kyh.real.Beacon.RECOBackgroundRangingService;
import com.example.kyh.real.Database.DbAdapter;
import com.example.kyh.real.Library.CircularProgressBar;
import com.example.kyh.real.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;



public class S_F_Home extends S_Total_MainActivity.PlaceholderFragment implements View.OnClickListener {
    private static DbAdapter mHelper;

    private String uid;
    private int db_ver;
    private String id;
    private String mac_addr;
    private String level;

    private CircularProgressBar c1;
    private static CircularProgressBar c2;
    private CircularProgressBar c3;

    private String c1_title, c1_subTitle;
    private String c2_title, c2_subTitle;
    private String c3_title, c3_subTitle;

    private S_F_Home_CourseList courseList;

    private TextView empty;

    private Intent MonitoringIntent;

    static float progress = 0;

    private S_F_Home_CourseInfo currentClass;

    public static Handler handler = new MessageHandler();

    public static class MessageHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            if(c2 == null) Log.v("progress bar 2","c2 null");
            else{
                Log.v("Msg arg1",String.valueOf(msg.arg1));
                Log.v("Msg arg2",String.valueOf(msg.arg2));
                progress += (msg.arg1 + ((float)(msg.arg2))/1000);
                progress = progress%100;
                Log.v("Progress",String.valueOf(progress));
                c2.setProgress((int)progress);
                c2.setSubTitle((int)progress + "%");
            }

            super.handleMessage(msg);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        uid = bundle.getString("uid", "Default UID");
        db_ver = Integer.parseInt(bundle.getString("db_ver"));
        id = bundle.getString("id");
        mac_addr = bundle.getString("mac_addr");
        level = bundle.getString("level");
        MonitoringIntent = S_Total_MainActivity.MonitoringIntent;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_s_home, container, false);
        mHelper = new DbAdapter(rootView.getContext(), uid, db_ver, level);

        c2 = (CircularProgressBar) rootView.findViewById(R.id.circularprogressbar);
        c1 = (CircularProgressBar) rootView.findViewById(R.id.circularprogressbar_prev);
        c3 = (CircularProgressBar) rootView.findViewById(R.id.circularprogressbar_next);

        empty = (TextView) rootView.findViewById(R.id.s_home_empty);
        empty.setVisibility(View.INVISIBLE);
        empty.setOnClickListener(this);

        setCourseList();
        setClassInfo();
        fillViews();

        c1.setOnClickListener(this);
        c2.setOnClickListener(this);
        c3.setOnClickListener(this);

        return rootView;
    }

    private void fillViews() {
        c1.setProgress(100);
        c1.setTitle(c1_title);
        c1.setSubTitle(c1_subTitle);

        c2.animateProgressTo(0, (int) progress, new CircularProgressBar.ProgressAnimationListener() {
            @Override
            public void onAnimationStart() {
                c2.setSubTitle(c2_subTitle);
                c2.setTitle(c2_title);
            }

            @Override
            public void onAnimationProgress(int progress) {
                if (!c2_subTitle.equals("대기")) {
                    c2_subTitle = progress + "%";
                }
            }

            @Override
            public void onAnimationFinish() {
                c2.setSubTitle(c2_subTitle);
            }
        });

        c3.setProgress(100);
        c3.setTitle(c3_title);
        c3.setSubTitle(c3_subTitle);
    }

    private void setClassInfo() {
        if (courseList.isEmpty()) {
            setClassErr();
        }
        else {
            setClass();
        }
    }

    private void setClass() {
        int currentTime = Integer.parseInt(getCurrentTime());
        currentClass = courseList.getCurrentClass(currentTime);

        for (int i = 0; i < 20; i++) {
            if (currentClass.getEndTime() >= currentTime)
                break;
            currentClass = currentClass.next;
        }

        c1_title = courseNameTrim(currentClass.prev.getCourseName());
        c1_subTitle = timeFormat(currentClass.prev.getStartTime());

        c2_title = courseNameTrim(currentClass.getCourseName());
        S_Total_MainActivity.RECO_UUID = currentClass.getBeaconId();

        if (isClassStart(currentClass.getStartTime(),
                currentClass.getEndTime(),
                currentTime)) {
            c2_subTitle = "시작";
            startService();
        }
        else {
            c2_subTitle = "대기";
            stopService();
        }

        c3_title = courseNameTrim(currentClass.next.getCourseName());
        c3_subTitle = timeFormat(currentClass.next.getStartTime());
    }

    private void startService() {
        /**
         * 서버로 보내야하는 데이터를 넣습니다.
         */
        MonitoringIntent.putExtra("uid", uid);
        MonitoringIntent.putExtra("db_ver", db_ver);
        MonitoringIntent.putExtra("course_id", currentClass.getCourseId());
        MonitoringIntent.putExtra("group_id", currentClass.getGroupId());
        MonitoringIntent.putExtra("timeslot_id", currentClass.getTimeslotId());
        MonitoringIntent.putExtra("start_time", currentClass.getStartTime());
        MonitoringIntent.putExtra("end_time", currentClass.getEndTime());
        MonitoringIntent.putExtra("beacon_id", currentClass.getBeaconId());
        MonitoringIntent.putExtra("Mes", new Messenger(handler));

        S_Total_MainActivity.RECO_UUID = currentClass.getBeaconId();

        getActivity().startService(MonitoringIntent);
        Toast toast2 = Toast.makeText(getActivity().getApplicationContext(),
                "service monitoring", Toast.LENGTH_SHORT);
        toast2.setGravity(Gravity.CENTER, 0, 0);
        toast2.show();
    }

    private void stopService() {
        getActivity().stopService(MonitoringIntent);
    }

    private String courseNameTrim (String courseName) {
        if (courseName.length() > 7)
            return courseName.substring(0,5) + "..";
        return courseName;
    }

    private void setCourseList () {
        SQLiteDatabase db = mHelper.getReadableDatabase();

        courseList = new S_F_Home_CourseList();
        Cursor cursor = db.rawQuery("SELECT course_name, start_time, end_time, beacon_id, course_id, group_id, timeslot_id FROM timeslot NATURAL JOIN class ORDER BY start_time ASC;", null);
        cursor.moveToNext();

        while (cursor.moveToNext()) {
            courseList.list_push_back(new S_F_Home_CourseInfo(cursor.getString(0),cursor.getInt(1),cursor.getInt(2),cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getInt(6)));
        }
        cursor.close();
        db.close();
    }

    private boolean isClassStart (int start_time, int end_time, int current_time) {
        if ((start_time <= current_time) && (current_time < end_time))
            return true;
        else
            return false;
    }

    private boolean isClassEnd (int end_time, int current_time) {
        if (end_time <= current_time)
            return true;
        else
            return false;
    }

    private void setClassErr() {
        Log.d ("에러: ", "setClassErr");
        empty.setVisibility(View.VISIBLE);

        SharedPreferences set_alarm = getActivity().getSharedPreferences("auto_login", getActivity().MODE_PRIVATE);

        //알람용 디비버젼 관리
        if(set_alarm.getBoolean("set_alarm",false)){
            SharedPreferences.Editor edit = set_alarm.edit();
            edit.remove("db_ver");
            edit.putInt("db_ver",db_ver);
            edit.apply();
        }

    }

    private String getCurrentTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("HHmm", Locale.KOREA);
        Calendar calendar = Calendar.getInstance();
        Date today = new Date();

        return calendar.get(Calendar.DAY_OF_WEEK) + formatter.format(today);
    }

    private String timeFormat(int currentTime) {
        int newCurrentTime = currentTime % 10000;
        int hour = newCurrentTime / 100;
        int min = newCurrentTime % 100;
        String _hour;
        String _min;
        String AM_PM = "AM";

        if (hour == 0)
            _hour = "00:";
        else {
            if (hour > 12) {
                hour -= 12;
                AM_PM = "PM";
            }
            _hour = hour + ":";
        }

        if (min == 0)
            _min = "00";
        else
            _min = min + "";

            return _hour + _min + AM_PM;
    }

    @Override
    public void onClick(View v) {
        SQLiteDatabase db = mHelper.getReadableDatabase();
        if (v.getId() == R.id.circularprogressbar) {
            Log.d("C2_Touched", "");
        } else if (v.getId() == R.id.circularprogressbar_next) {
            Log.d("C3_Touched", "");
        } else if (v.getId() == R.id.circularprogressbar_prev) {
            Log.d("C1_Touched", "");
        } else if (v.getId() == R.id.s_home_empty) {
            Log.d("EMPTY!!", "");
            if (mHelper.getInitData(db, id, uid, mac_addr)) {
                setCourseList();
                setClassInfo();
                fillViews();
                empty.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public void onResume() {
        // 만약 현재 수업이 끝났다면.
        if (currentClass != null) {
            if (isClassEnd(currentClass.getEndTime(), Integer.parseInt(getCurrentTime()))) {
                // 비콘 서비스 종료
                stopService();
                if (false) {
                    setCourseList();
                }
                setClassInfo();
                fillViews();
            }
        }
        super.onResume();
    }
}