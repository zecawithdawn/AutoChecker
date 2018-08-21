package com.example.kyh.real.Database;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.kyh.real.Library.Alarm;
import com.example.kyh.real.Library.SSLTask;
import com.example.kyh.real.Library.TaskListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.StringTokenizer;

/**
 * Created by park on 2015. 1. 16..
 */

public class DbAdapter extends SQLiteOpenHelper implements TaskListener{
    private String position;
        /* 첫 번째 인수는 DB를 생성하는 컨텍스트이되 보통 메인 액티비티를 전달한다. name, version 인수로 전달되는
         * DB파일의 이름과 버전은 이후 DB를 생성 및 업데이트할 때 사용된다. 세 번째 인수는 커스텀 커서를 사용할 때
         * 지정하는데 표준 커서를 사용할 경우는 null로 지정한다. 도우미 객체를 생성해 놓으면 DB가 필요한 시점에 다음
         * 세 매소드를 호출한다.
         */
    public DbAdapter(Context context, String name, int version, String position) {
        super (context, name+".db", null, version);
        this.position = position;
    }

        /* DB가 처음 만들어질 때 호출된다.
         * 여기서 테이블을 만들고 초기 레코드를 삽입한다.
         */
        @Override
        public void onCreate(SQLiteDatabase db) {
            if (makeDatabase(db))
                Log.d ("Get Schema Success", "");
            else {
                Log.d("Get Schema Failed", "");
                dropAll(db);
                if (makeDatabase(db))
                    Log.d ("Get Schema Success", "");
                else {
                    Log.d("Get Schema Failed", "");
                    dropAll(db);
                }
            }
        }

        /* DB를 업그레이드 할 때 호출된다.
         * 기존 테이블을 삭제하고 새로 만들거나 ALTER TABLE로 스키마를 수정한다.
         */
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.d("onUpgrade DB", "oldVersion: "+oldVersion+" newVersion: "+newVersion);
            dropAll(db);
            makeDatabase(db);


        }

    public int getCurrentTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("HHmm", Locale.KOREA);
        Calendar calendar = Calendar.getInstance();
        Date today = new Date();

        return Integer.parseInt(calendar.get(Calendar.DAY_OF_WEEK) + formatter.format(today));
    }

    public Alarm getNext(SQLiteDatabase db) {
        Alarm next = new Alarm();
        Cursor cursor = db.rawQuery("SELECT course_name, MIN(start_time) FROM timeslot NATURAL JOIN class WHERE start_time > " + getCurrentTime() + ";",null);
        cursor.moveToNext();

        if (cursor.getString(0) != null) {
            next.setId(cursor.getString(0));
            next.setTime(cursor.getInt(1));

        }
        else {
            cursor = db.rawQuery("SELECT course_name, MIN(start_time) FROM timeslot NATURAL JOIN class ;",null);
            cursor.moveToNext();
            if (cursor.getString(0) != null) {
                next.setId(cursor.getString(0));
                next.setTime(cursor.getInt(1));

            }
            else
                next = null;
        }

        return next;
    }

    private boolean makeDatabase(SQLiteDatabase db) {
        boolean ret = true;
        JSONObject reqDB = new JSONObject();
        String DOMAIN;

        switch (position) {
            case "P":
                DOMAIN = "https://166.104.245.43/get_table.php";
                break;
            case "S":
                DOMAIN = "https://166.104.245.43/get_table_s.php";
                break;
            default:
                Log.d ("학생, 교수가 불분명합니다: position = ", position);
                return false;
        }

        try {
            int tableNumber;

            reqDB.put ("request", "true");

            SSLTask sslTask = new SSLTask(DOMAIN, reqDB, this);
            reqDB = sslTask.execute().get();

            if ((tableNumber = reqDB.length()) > 0) {
                for (int i = 1; i <= tableNumber; i++) {
                    db.execSQL(reqDB.getString("sql"+i));
                    Log.d ("QUERY: ", reqDB.getString("sql"+i));
                }
            }
            else {
                db.setVersion(0);
                ret = false;
            }
        } catch (Exception e) {
            db.setVersion(0);
            ret = false;
            e.printStackTrace();
        }

        return ret;
    }

    private void dropAll (SQLiteDatabase db) {
        ArrayList<String> tableNames = getTablesName(db);

        try {
            while (!tableNames.isEmpty()) {
                Log.d ("Drop Query: ", "DROP TABLE IF EXISTS '" + tableNames.get(0) + "';");
                db.execSQL("DROP TABLE IF EXISTS '" + tableNames.remove(0) + "';");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ArrayList<String> getTablesName (SQLiteDatabase db) {
        ArrayList<String> arrTblNames = new ArrayList<String>();
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table';", null);
        int i;

        if (c.moveToFirst()) {
            while ( !c.isAfterLast() ) {
                arrTblNames.add( c.getString( c.getColumnIndex("name")) );
                Log.d ("NAME: ", c.getString( c.getColumnIndex("name")));
                c.moveToNext();
            }
        }

        for (i = 0; i < arrTblNames.size(); i++) {
            if (arrTblNames.get(i).equals("sqlite_sequence") || arrTblNames.get(i).equals("android_metadata")) {
                arrTblNames.remove(i);
                break;
            }
        }
        for (i = 0; i < arrTblNames.size(); i++) {
            if (arrTblNames.get(i).equals("sqlite_sequence") || arrTblNames.get(i).equals("android_metadata")) {
                arrTblNames.remove(i);
                break;
            }
        }

        return arrTblNames;
    }

    public boolean getInitData (SQLiteDatabase db, String id, String uid, String mac_addr) {
        boolean ret = false;

        JSONObject reqDB = new JSONObject();
        final String DOMAIN;

        switch (position) {
            case "P":
                DOMAIN = "https://166.104.245.43/get_init_data.php";
                break;
            case "S":
                DOMAIN = "https://166.104.245.43/get_init_data_s.php";
                break;
            default:
                Log.d ("학생, 교수가 불분명합니다: position = ", position);
                return false;
        }

        try {
            int tableNumber, j;

            reqDB.put ("id", id);
            reqDB.put ("uid", uid);
            reqDB.put ("mac_addr", mac_addr);

            SSLTask sslTask = new SSLTask(DOMAIN, reqDB, this);
            reqDB = sslTask.execute().get();

            ArrayList<String> classNames = getTablesName(db);

            if (position.equals("P")) {
                for (j = 0; !classNames.get(j).equals("attendance") && (j < classNames.size()); j++);
                Log.d("NAME: ", classNames.remove(j));
                for (j = 0; !classNames.get(j).equals("attendance_update") && (j < classNames.size()); j++);
                Log.d("NAME: ", classNames.remove(j));
            }

            tableNumber = classNames.size();

            if (reqDB.length() > 0) {
                for (int i = 0; i < tableNumber; i++) {
                    JSONArray jsonArray = reqDB.getJSONArray(classNames.get(i));
                    for (int k = 0; k < jsonArray.length(); k++) {
                        db.execSQL("INSERT OR REPLACE INTO " + classNames.get(i) + " VALUES (" + new String(jsonArray.get(k).toString().getBytes("ISO-8859-1"),"UTF-8") + ");");
                        Log.d("QUERY: ", jsonArray.get(k).toString());
                    }
                }
                ret = true;
            }
        } catch (Exception e) {
            ret = false;
            e.printStackTrace();
        }

        return ret;
    }

    public boolean getAttendance (SQLiteDatabase db, String id, String uid, String mac_addr, String course_id, String group_id, String timeslot_id, String currentTime) {
        boolean ret = false;

        JSONObject reqDB = new JSONObject();
        final String DOMAIN = "https://166.104.245.43/get_attendance.php";

        try {
            String s_uid, s_state;

            reqDB.put ("id", id);
            reqDB.put ("uid", uid);
            reqDB.put ("mac_addr", mac_addr);
            reqDB.put ("course_id", course_id);
            reqDB.put ("group_id", group_id);
            reqDB.put ("timeslot_id", timeslot_id);

            SSLTask sslTask = new SSLTask(DOMAIN, reqDB, this);
            reqDB = sslTask.execute().get();

            if (reqDB.length() > 0) {
                JSONArray jsonArray = reqDB.getJSONArray("data");
                for (int k = 0; k < jsonArray.length(); k++) {
                    StringTokenizer s = new StringTokenizer(jsonArray.getString(k).toString());
                    s_uid = s.nextToken("-");
                    s_state = s.nextToken("-");

                    db.execSQL("INSERT OR REPLACE INTO attendance VALUES ('" +
                            course_id + "', '" +
                            group_id + "', " +
                            timeslot_id + ", " +
                            s_uid + ", '" +
                            currentTime + "', '" +
                            s_state + "');");
                }
            ret = true;
            }
        } catch (Exception e) {
            ret = false;
            e.printStackTrace();
        }
        return ret;
    }

    public boolean setState (SQLiteDatabase db, String uid, String state, String course_id, String group_id, String timeslot_id, String att_date, String index) {
        boolean ret = false;
        JSONObject reqDB = new JSONObject();
        final String DOMAIN = "https://166.104.245.43/set_attendance.php";

        switch (state) {
            case "출석":
                state = "P";
                break;
            case "지각":
                state = "L";
                break;
            default:
                state = "A";
                break;
        }

        try {
            reqDB.put ("uid", uid);
            reqDB.put ("state", state);
            reqDB.put ("course_id", course_id);
            reqDB.put ("group_id", group_id);
            reqDB.put ("timeslot_id", timeslot_id);
            reqDB.put ("att_date", att_date);

            SSLTask sslTask = new SSLTask(DOMAIN, reqDB, this);
            reqDB = sslTask.execute().get();

            if ((reqDB.length() > 0) && reqDB.getString("set_attendance").equals("S"))  {
                db.execSQL("DELETE FROM attendance_update WHERE att_index = " + index);
                Log.d ("STATE: ", state);
                db.execSQL("DELETE FROM attendance WHERE student_id = " + uid + " AND course_id = '" + course_id + "' AND group_id = '" + group_id + "' AND timeslot_id = " + timeslot_id + " AND att_date = '" + att_date + "';");
                ret = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    @Override
    public void onReceived(JSONObject jsonData) {

    }

    @Override
    public void onCanceled() {

    }
}