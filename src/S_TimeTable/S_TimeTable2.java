package com.example.kyh.real.S_TimeTable;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.util.Size;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.kyh.real.Database.DbAdapter;
import com.example.kyh.real.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class S_TimeTable2 extends ActionBarActivity implements View.OnClickListener {
    private static DbAdapter mHelper;
    private String uid;
    private int db_ver;
    private String id;
    private String mac_addr;
    private String level;
    private String day;

    LinearLayout Timetablelist;
    LayoutInflater vi;
    ImageView createbutton;

    private static ArrayList<S_F_TimeTabledata> todaylist = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s_time_table2);

        //초기화
        initialize();

        //선택된 날짜의 정보를 todayList에 담는다.
        settingtodaytable();

        //화면에 보여질 리스트들을 셋팅
        settingList();
    }

    //초기화함수
    private void initialize() {
        todaylist.clear();

        Timetablelist = (LinearLayout) findViewById(R.id.s_timetableList);

        day= getIntent().getStringExtra("day");
        uid = getIntent().getStringExtra("uid");
        db_ver = Integer.parseInt(getIntent().getStringExtra("db_ver"));
        id = getIntent().getStringExtra("id");
        mac_addr = getIntent().getStringExtra("mac_addr");
        level = getIntent().getStringExtra("level");

        mHelper = new DbAdapter(getBaseContext(), uid, db_ver, level);

        vi = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        createbutton = (ImageView) findViewById(R.id.s_timetable2_create);

        //<!>3번째 일정추가 페이지로 이동하는거
        /*
        createbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(S_TimeTable2.this,S_TimeTable3.class);
                startActivity(i);
            }
        });
        */
    }

    //디비에서 선택된 요일의 정보를 todaylist에 넣는다
    private void settingtodaytable() {
        SQLiteDatabase db = mHelper.getReadableDatabase();
        Cursor cursor;

        cursor = db.rawQuery(makeQuery(day), null);

        //요일에 따라 어레이리스트에 추가해서
        while (cursor.moveToNext()) {
            S_F_TimeTabledata data = new S_F_TimeTabledata(cursor.getString(0), cursor.getString(1), cursor.getInt(2), cursor.getInt(3), "class");
            todaylist.add(data);
        }
        db.close();

    }

    //화면에 보여질 리스트를 셋팅
    private void settingList() {
        int thistime = Integer.parseInt(makethistime());

        //<!>nowclass가 0이면 첫수업이 표시 안될수도 잇음 나중에 확인
        int nowclass = 100;

        for (int i = 0; i < todaylist.size(); i++) {

            if ((thistime < todaylist.get(i).class_etime)
                    && (thistime >= todaylist.get(i).class_stime)) {
                nowclass = i;
            }
            if(i == 0 ){
                //오늘 날짜에 제일 첫수업 전이거나
                if (thistime/10000 == todaylist.get(i).class_stime/10000){
                    if (thistime < todaylist.get(i).class_stime){
                        nowclass=0;
                    }
                }
            }else if((i <= todaylist.size()-1)){
                //그전 시간 종료후 이거나
                if((thistime >= todaylist.get(i-1).class_etime)&&(thistime <= todaylist.get(i).class_stime)){
                    nowclass=i;
                }
            }
        }

        Log.d("nowclass",String.valueOf(nowclass));

        if ((0 <= nowclass) && (todaylist.size() > nowclass)) {
            //지금시간이 수업중 인 경우
            timeincludeclass(nowclass);
        } else {
            //지금시간이 수업중이 아닌 경우
            timeunincludeclass(thistime);
        }
    }

    //<!>각각 핸드폰에 맞는 해상도로 높이와 글씨 크기를 변경해줘야함
    //매번 새로 생성해서 붙여야 할듯 싶어서 중복화 작업 안해줌
    //각각 객체에 이벤트 리스너 붙여줄꺼임

    //수업중이 아닌경우
    private void timeunincludeclass (int thistime) {
        //다 끝난경우
        if ((todaylist.get(todaylist.size() - 1).class_etime) < thistime) {

            for (int i = 0; i < todaylist.size(); i++) {
                View custom = vi.inflate(R.layout.s_timetable_normalclass, null);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        300);
                custom.setBackgroundColor(Color.argb((todaylist.size()-i)*10,79,79,79));

                TextView normalclasstext = (TextView) custom.findViewById(R.id.s_timetable2_normaltext);
                normalclasstext.setText(todaylist.get(i).class_name + "\n" + todaylist.get(i).class_room);

                TextView normalclasstime = (TextView) custom.findViewById(R.id.s_timetable2_normaltime);
                final SpannableStringBuilder sps = new SpannableStringBuilder(maketimeformat(todaylist.get(i).class_stime));
                sps.setSpan(new AbsoluteSizeSpan(100), 0, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                normalclasstime.append(sps);

                Timetablelist.addView(custom, params);

            }//아직 시작안한 경우
        } else for (int i = 0; i < todaylist.size(); i++) {
            View custom = vi.inflate(R.layout.s_timetable_normalclass, null);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    300);
            custom.setBackgroundColor(Color.argb((i + 1) * 10, 79, 79, 79));

            TextView normalclasstext = (TextView) custom.findViewById(R.id.s_timetable2_normaltext);
            normalclasstext.setText(todaylist.get(i).class_name + "\n" + todaylist.get(i).class_room);

            TextView normalclasstime = (TextView) custom.findViewById(R.id.s_timetable2_normaltime);
            final SpannableStringBuilder sps = new SpannableStringBuilder(maketimeformat(todaylist.get(i).class_stime));
            sps.setSpan(new AbsoluteSizeSpan(100), 0, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            normalclasstime.append(sps);

            Timetablelist.addView(custom, params);
        }
    }

    //현재시간이 수업중인경우
    private void timeincludeclass(int nowclass) {
        for (int i = 0; i < todaylist.size(); i++) {

            if(nowclass == i){
                View custom = vi.inflate(R.layout.s_timetable_thisclass, null);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        450);

                TextView thisclasstext = (TextView) custom.findViewById(R.id.s_timetable2_thistext);
                thisclasstext.setText(todaylist.get(i).class_name + "\n" + todaylist.get(i).class_room);

                TextView thisclasstime = (TextView) custom.findViewById(R.id.s_timetable2_thistime);
                final SpannableStringBuilder sps = new SpannableStringBuilder(maketimeformat(todaylist.get(i).class_stime));
                sps.setSpan(new AbsoluteSizeSpan(100), 0, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                thisclasstime.append(sps);

                Timetablelist.addView(custom, params);
            }else if(nowclass > i){
                View custom = vi.inflate(R.layout.s_timetable_normalclass, null);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        300);
                custom.setBackgroundColor(Color.argb((nowclass-i)*10,79,79,79));

                TextView normalclasstext = (TextView) custom.findViewById(R.id.s_timetable2_normaltext);
                normalclasstext.setText(todaylist.get(i).class_name + "\n" + todaylist.get(i).class_room);

                TextView normalclasstime = (TextView) custom.findViewById(R.id.s_timetable2_normaltime);
                final SpannableStringBuilder sps = new SpannableStringBuilder(maketimeformat(todaylist.get(i).class_stime));
                sps.setSpan(new AbsoluteSizeSpan(100), 0, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                normalclasstime.append(sps);

                Timetablelist.addView(custom, params);
            }else{
                View custom = vi.inflate(R.layout.s_timetable_normalclass, null);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        300);
                custom.setBackgroundColor(Color.argb((i-nowclass)*10,79,79,79));

                TextView normalclasstext = (TextView) custom.findViewById(R.id.s_timetable2_normaltext);
                normalclasstext.setText(todaylist.get(i).class_name + "\n" + todaylist.get(i).class_room);

                TextView normalclasstime = (TextView) custom.findViewById(R.id.s_timetable2_normaltime);
                final SpannableStringBuilder sps = new SpannableStringBuilder(maketimeformat(todaylist.get(i).class_stime));
                sps.setSpan(new AbsoluteSizeSpan(100), 0, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                normalclasstime.append(sps);

                Timetablelist.addView(custom, params);
            }

        }
    }

    //수업시작 시간를 디자인요구 포맷으로 변경
    private String maketimeformat(int stime){
        String formattime;
        if ((stime%10000)/100 < 10){
            formattime="0"+String.valueOf((stime%10000)/100);
        }else{
            formattime=String.valueOf((stime%10000)/100);
        }

        if (stime%100 == 0){
            formattime=formattime+":00";
        }else{
            formattime=formattime+":"+String.valueOf(stime%100);
        }

        if(stime%10000 >= 1200){
            formattime=formattime+"PM";
        }else{
            formattime=formattime+"AM";
        }
        return formattime;
    }

    //현재의 날짜를 디비형식에 맞게 만들어주는 함수
    private String makethistime() {
        String thistime;

        //요일에 따른 첫자리 숫자
        SimpleDateFormat dayFormat2 = new SimpleDateFormat("E", Locale.ENGLISH);
        Calendar calendar2 = Calendar.getInstance();

        if (dayFormat2.format(calendar2.getTime()).equals("Mon")) {
            thistime = "2";
        } else if (dayFormat2.format(calendar2.getTime()).equals("Tue")) {
            thistime = "3";
        } else if (dayFormat2.format(calendar2.getTime()).equals("Wed")) {
            thistime = "4";
        } else if (dayFormat2.format(calendar2.getTime()).equals("Thu")) {
            thistime = "5";
        } else if (dayFormat2.format(calendar2.getTime()).equals("Fri")) {
            thistime = "6";
        } else if (dayFormat2.format(calendar2.getTime()).equals("Sat")) {
            thistime = "7";
        } else {
            thistime = "1";
        }

        //현재시간에 따른 나머지 4자리
        SimpleDateFormat dayFormat = new SimpleDateFormat("HHmm");
        Calendar calendar = Calendar.getInstance();

        thistime = thistime + dayFormat.format(calendar.getTime());

        //5자리의 현재시간 형식 숫자 리턴
        return thistime;

    }

    //오늘 날짜의 해당 데이터를 받아오는 쿼리를 만든다
    private String makeQuery(String day) {
        String query = "select course_name,class_room,start_time,end_time " +
                "from class natural join timeslot " +
                "where (start_time/10000) =";

        switch (day) {
            case "Mon":
                query = query + " 2 ";
                break;
            case "Tue":
                query = query + " 3 ";
                break;
            case "Wed":
                query = query + " 4 ";
                break;
            case "Thu":
                query = query + " 5 ";
                break;
            default:
                query = query + " 6 ";
                break;
        }

        return query+"order by start_time asc";
    }

    //필요없음
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_s_time_table2, menu);
        return true;
    }

    //필요없음
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {

    }
}
