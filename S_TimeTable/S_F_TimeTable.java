package com.example.kyh.real.S_TimeTable;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.TextView;

import com.example.kyh.real.Database.DbAdapter;
import com.example.kyh.real.Library.SSLTask;
import com.example.kyh.real.Library.TaskListener;
import com.example.kyh.real.R;
import com.example.kyh.real.S_F.S_Total_MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.ExecutionException;


public class S_F_TimeTable extends S_Total_MainActivity.PlaceholderFragment implements View.OnClickListener, TaskListener {
    private static DbAdapter mHelper;
    private String uid;
    private int db_ver;
    private String id;
    private String mac_addr;
    private String level;

    String toDayofweek;
    private static ArrayList<S_F_TimeTabledata> monclassList = new ArrayList<>();
    private static ArrayList<S_F_TimeTabledata> tueclassList = new ArrayList<>();
    private static ArrayList<S_F_TimeTabledata> wedclassList = new ArrayList<>();
    private static ArrayList<S_F_TimeTabledata> thuclassList = new ArrayList<>();
    private static ArrayList<S_F_TimeTabledata> friclassList = new ArrayList<>();

    TextView timeMon;
    TextView timeTue;
    TextView timeWed;
    TextView timeThu;
    TextView timeFri;
    GridLayout rootView;
    JSONObject responce;

    //<!>해야할것
    //2.현재시간 받아와서 디비랑 비교해서 그전 시꺼라 이후 시간꺼 다르
    //추가기능
    //3.일정추가된거 확인해주고 표시
    //4.일정은 시간지나면 제거

    //fragment에서 DB생성
    // oncreateview보다 oncreate가먼저 실행
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //fragment에서 상속받아올시 bundle.으로 해결
        Bundle bundle = this.getArguments();
        uid = bundle.getString("uid", "Default UID");
        db_ver = Integer.parseInt(bundle.getString("db_ver"));
        id = bundle.getString("id");
        mac_addr = bundle.getString("mac_addr");
        level = bundle.getString("level");


    }

    //fragment안에선 얘가 보통 oncreate 역할
    //inflater 때문인가
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = (GridLayout) inflater.inflate(R.layout.fragment_s_timetable, container, false);

        //초기화
        initialize();

        //디비에서 데이터를 받아와 각각 요일의 리스트에추가
        addlist();

        //오늘의 요일에 따라 텍스트 크기변경
        dayofweek();

        //월요일의 수업및 일정셋팅
        settingtable(monclassList);

        //더미값
        // <!>이거 없으면 시간표가 이상하게 나옴
        //가장 마지막행이 바닥에 붙어버림
        dummy();

        //나머지요일의 수업 및 일정셋팅
        settingtable(tueclassList);
        settingtable(wedclassList);
        settingtable(thuclassList);
        settingtable(friclassList);

        return rootView;
    }

    //초기화 함수
    private void initialize() {
        mHelper = new DbAdapter(rootView.getContext(), uid, db_ver, level);

        monclassList.clear();
        tueclassList.clear();
        wedclassList.clear();
        thuclassList.clear();
        friclassList.clear();

        timeMon = (TextView) rootView.findViewById(R.id.s_timetablemon);
        timeTue = (TextView) rootView.findViewById(R.id.s_timetabletue);
        timeWed = (TextView) rootView.findViewById(R.id.s_timetablewed);
        timeThu = (TextView) rootView.findViewById(R.id.s_timetablethu);
        timeFri = (TextView) rootView.findViewById(R.id.s_timetablefri);

        timeMon.setOnClickListener(this);
        timeTue.setOnClickListener(this);
        timeWed.setOnClickListener(this);
        timeThu.setOnClickListener(this);
        timeFri.setOnClickListener(this);

        //현재날짜 받아오는 함수
        SimpleDateFormat dayFormat = new SimpleDateFormat("E", Locale.ENGLISH);
        Calendar calendar = Calendar.getInstance();
        toDayofweek = dayFormat.format(calendar.getTime());

        Log.d("today", toDayofweek);

        JSONObject request_json = new JSONObject();
        try {
            request_json.put("uid", uid);

            SSLTask sslTask = new SSLTask("https://166.104.245.43/get_timetable.php", request_json, this);
            responce = sslTask.execute().get();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (NullPointerException e){

        }

    }

    //전체 리스트를 받아오는 함수
    private void addlist() {

        //디비
        SQLiteDatabase db = mHelper.getReadableDatabase();
        Cursor cursor;
        cursor = db.rawQuery("select course_name,start_time " +
                "from class natural join timeslot order by start_time asc", null);

        //<!>이 커서넥스트 혹시 유효한값 가르키고 있는지 알아봐야함
        //왜 한번 넥스트 해주는지 알수없음
        cursor.moveToNext();

        //각각 요일따라서 분배
        while (cursor.moveToNext()) {
            S_F_TimeTabledata data = new S_F_TimeTabledata(cursor.getString(0), "", cursor.getInt(1), 0, "class");
            if ((data.class_stime / 10000) == 2) {
                monclassList.add(data);
            } else if ((data.class_stime / 10000) == 3) {
                tueclassList.add(data);
            } else if ((data.class_stime / 10000) == 4) {
                wedclassList.add(data);
            } else if ((data.class_stime / 10000) == 5) {
                thuclassList.add(data);
            } else if ((data.class_stime / 10000) == 6) {
                friclassList.add(data);
            }
        }
        db.close();

        //<!>여기에 일정 갱신하는 함수 만들기
        /*

         */
    }

    //오늘 날짜에 따라 요일글짜 크게 변경 시켜주는거
    private void dayofweek() {

        //<!>이거 핸드폰에 따라 width와 height값이 다름 해상도 문제 나중에 수정
        if (toDayofweek.equals("Mon")) {
            GridLayout.LayoutParams lparam =
                    (GridLayout.LayoutParams) timeMon.getLayoutParams();
            lparam.width = 200;
            lparam.height = 200;
            timeMon.setTextSize(30);
            timeMon.setLayoutParams(lparam);
        } else if (toDayofweek.equals("Tue")) {
            GridLayout.LayoutParams lparam =
                    (GridLayout.LayoutParams) timeTue.getLayoutParams();
            lparam.width = 200;
            lparam.height = 200;
            timeTue.setTextSize(30);
            timeTue.setLayoutParams(lparam);
        } else if (toDayofweek.equals("Wed")) {
            GridLayout.LayoutParams lparam =
                    (GridLayout.LayoutParams) timeWed.getLayoutParams();
            lparam.width = 200;
            lparam.height = 200;
            timeWed.setTextSize(30);
            timeWed.setLayoutParams(lparam);
        } else if (toDayofweek.equals("Thu")) {
            GridLayout.LayoutParams lparam =
                    (GridLayout.LayoutParams) timeThu.getLayoutParams();
            lparam.width = 200;
            lparam.height = 200;
            lparam.setGravity(Gravity.CENTER);
            timeThu.setTextSize(30);
            timeThu.setLayoutParams(lparam);
        } else {
            GridLayout.LayoutParams lparam =
                    (GridLayout.LayoutParams) timeFri.getLayoutParams();
            lparam.width = 200;
            lparam.height = 200;
            timeFri.setTextSize(30);
            timeFri.setLayoutParams(lparam);
        }
        rootView.setPadding(0, rootView.getPaddingTop(), rootView.getPaddingRight(), rootView.getPaddingBottom());
    }

    //시간표를 올바르게 만들기위한 더미값
    private void dummy() {
        for (int i = monclassList.size(); i < 9; i++) {
            TextView tv = new TextView(rootView.getContext());
            tv.setWidth(120);
            tv.setHeight(120);
            GridLayout.Spec rowspec = GridLayout.spec(i + 2, 1, GridLayout.CENTER);
            GridLayout.Spec colspec = GridLayout.spec(0, 1, GridLayout.CENTER);

            GridLayout.LayoutParams Location = new GridLayout.LayoutParams(rowspec, colspec);
            rootView.addView(tv, Location);
        }
    }

    //해당 요일의 수업 및 일정을 테이블에 추가해줌
    private void settingtable(ArrayList<S_F_TimeTabledata> dayofweek) {

        //각각 요일에 따라 길이가 다르기 때문에 요일마다 따로 실행하여 붙여줌
        for (int i = 0; i < dayofweek.size(); i++) {
            TextView tv = new TextView(rootView.getContext());

            //<!>해상도 문제때문에 이상하게 나올꺼임
            tv.setWidth(200);
            tv.setHeight(200);
            tv.setTextSize(12);
            tv.setGravity(Gravity.CENTER);
            tv.setText(Classtext(dayofweek.get(i).class_name));

            try {
                String attendence = responce.getString(String.valueOf(dayofweek.get(i).class_stime));

                if (attendence.equals("2")) {
                    //<!>밑에 두개가 출석 결석 지각 등 상태에 따라 바뀔 변수들
                    tv.setBackgroundDrawable(getResources().getDrawable(R.drawable.timetable2));
                    tv.setTextColor(Color.GREEN);
                } else if (attendence.equals("3")) {
                    tv.setBackgroundDrawable(getResources().getDrawable(R.drawable.timetable3));
                    tv.setTextColor(Color.YELLOW);
                } else if (attendence.equals("4")) {
                    tv.setBackgroundDrawable(getResources().getDrawable(R.drawable.timetable5));
                    tv.setTextColor(Color.RED);
                } else {
                    tv.setBackgroundDrawable(getResources().getDrawable(R.drawable.timetable4));
                    tv.setTextColor(Color.GRAY);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            //함수가 들어갈 위치
            GridLayout.Spec rowspec = GridLayout.spec(i + 2, 1, GridLayout.CENTER);
            GridLayout.Spec colspec;

            if ((dayofweek.get(i).class_stime / 10000) == 2) {
                colspec = GridLayout.spec(4, 1, GridLayout.CENTER);
            } else if ((dayofweek.get(i).class_stime / 10000) == 3) {
                colspec = GridLayout.spec(3, 1, GridLayout.CENTER);
            } else if ((dayofweek.get(i).class_stime / 10000) == 4) {
                colspec = GridLayout.spec(2, 1, GridLayout.CENTER);
            } else if ((dayofweek.get(i).class_stime / 10000) == 5) {
                colspec = GridLayout.spec(1, 1, GridLayout.CENTER);
            } else if ((dayofweek.get(i).class_stime / 10000) == 6) {
                colspec = GridLayout.spec(0, 1, GridLayout.CENTER);
            } else {
                colspec = GridLayout.spec(5, 1, GridLayout.CENTER);
            }

            GridLayout.LayoutParams Location = new GridLayout.LayoutParams(rowspec, colspec);
            rootView.addView(tv, Location);
        }
    }

    //수업이름의 길이의 따라 형태를 바꿔줌
    //<!>이것도 해상도 따라 혹은 글자수 따라 다르게 변경해줘야 할수도잇음
    private String Classtext(String class_name) {
        String returnString;
        if (class_name.length() < 4) {
            returnString = " \n" + class_name;
        } else if (class_name.length() == 5) {
            returnString = class_name.substring(0, 2) + "\n" + class_name.substring(2);
        } else if (class_name.length() == 6) {
            returnString = class_name.substring(0, 3) + "\n" + class_name.substring(3);
        } else if (class_name.length() == 7) {
            returnString = class_name.substring(0, 3) + "\n" + class_name.substring(3);
        } else {
            returnString = class_name.substring(0, 4) + "\n"+ "...";
        }
        return returnString;
    }

    //클릭 이벤트시 각각 해당하는 요일의 수업정보를 위해 이동
    //<!>혹시 화면을 1/5 씩 분할할수 잇다면 각각 요일로 이동 할 수 있게 함
    @Override
    public void onClick(View v) {
        Intent i = new Intent(getActivity(), S_TimeTable2.class);

        if (v.getId() == R.id.s_timetablemon) {
            i.putExtra("day", "Mon");
        } else if (v.getId() == R.id.s_timetabletue) {
            i.putExtra("day", "Tue");
        } else if (v.getId() == R.id.s_timetablewed) {
            i.putExtra("day", "Wed");
        } else if (v.getId() == R.id.s_timetablethu) {
            i.putExtra("day", "Thu");
        } else {
            i.putExtra("day", "Fri");
        }

        i.putExtra("uid", uid);
        i.putExtra("db_ver", String.valueOf(db_ver));
        i.putExtra("id", id);
        i.putExtra("mac_addr", mac_addr);
        i.putExtra("level", level);

        getActivity().startActivity(i);

    }

    @Override
    public void onReceived(JSONObject jsonData) {

    }

    @Override
    public void onCanceled() {

    }
}