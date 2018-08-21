package com.example.kyh.real.P_F;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kyh.real.Database.DbAdapter;
import com.example.kyh.real.Library.SoundSearcher;
import com.example.kyh.real.R;
import com.example.kyh.real.StudentList.P_Class;
import com.example.kyh.real.StudentList.P_Home_Student;
import com.example.kyh.real.StudentList.P_Home_StudentAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by kyh on 2015. 1. 24..
 */
public class P_F_Home extends P_Total_MainActivity.PlaceholderFragment implements View.OnClickListener{
    private static DbAdapter mHelper;
    /* allStudentList: 원본 데이터 리스트 (모든 학생 리스트)
     * sortedStudentList: 결석생 우선보기 기준으로 정렬된 데이터 리스트
     * searchedStudentList: sortedStudentList에서 정렬된 데이터로 초성검색한 결과 리스트
     */
    private static ArrayList<P_Home_Student> allStudentList = new ArrayList<>();
    private static ArrayList<P_Home_Student> sortedStudentList = new ArrayList<>();
    private static ArrayList<P_Home_Student> searchedStudentList = new ArrayList<>();

    /* adapter: searchedStudentList의 데이터를 listView에 띄워줄 어댑터
     * checkBox: 결석생 우선보기 체크박스
     * searchKeyword: 학생검색란에 쓰여진 검색키워드
     * 이상 6개 데이터는 StudentAdapter에서 현재 프레그먼트의 resume() 메소드를 호출 시 사용되는
     * 변수들 이므로 static 으로 선언, 관리한다.
     */
    static P_Home_StudentAdapter adapter;
    static CheckBox checkBox;
    static TextView classInfo;
    static TextView beaconNumber;
    static TextView listEmpty;
    static EditText searchBox;
    static Button clearButton;
    static Button syncButton;
    static ListView listView;

    private static String searchKeyword = "";

    private String uid;
    private int db_ver;
    private String id;
    private String mac_addr;
    private String level;
    // RelativeLayout.
    RelativeLayout layout;

    static P_Class nextClass;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        uid = bundle.getString("uid", "Default UID");
        db_ver = Integer.parseInt(bundle.getString("db_ver"));
        id = bundle.getString("id");
        mac_addr = bundle.getString("mac_addr");
        level = bundle.getString("level");
        //여기 있던  setContentView(R.layout.homepage 를 지우고 아래 onCreateView를 생성 해야함.
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d ("RESUME!!!!", "");
        updateState();
        fillViews();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        layout = (RelativeLayout) inflater.inflate(R.layout.fragment_p_home, container, false);
        mHelper = new DbAdapter(layout.getContext(), uid, db_ver, level);
        mHelper.getReadableDatabase();
        super.onCreate(savedInstanceState);

        drawViews();            /* 전체 화면 생성  */
        fillViews();            /* 화면에 내용 채우기 */

        layout.setOnClickListener(this);
        return layout;
    }

    public void classNotExist () {
        classInfo.setText("등록된 수업이 없습니다.");
        searchBox.setEnabled(false);
        clearButton.setEnabled(false);
        checkBox.setEnabled(false);
        syncButton.setEnabled(false);
        listEmpty.setText("표시할 내용이 없습니다.\n서버로부터 데이터를 받아오려면\n화면을 터치해주세요.");
        listEmpty.setOnClickListener(this);
        listView.setVisibility(View.INVISIBLE);
        listEmpty.setVisibility(View.VISIBLE);
    }

    public void classNotStart () {
        searchBox.setEnabled(false);
        clearButton.setEnabled(false);
        checkBox.setEnabled(false);
        syncButton.setEnabled(true);
        listEmpty.setText("수업 전입니다.");
        listEmpty.setOnClickListener(null);
        listView.setVisibility(View.INVISIBLE);
        listEmpty.setVisibility(View.VISIBLE);
    }

    public void classStarted () {
        searchBox.setEnabled(true);
        clearButton.setEnabled(true);
        checkBox.setEnabled(true);
        syncButton.setEnabled(true);
        listView.setVisibility(View.VISIBLE);
        listEmpty.setVisibility(View.INVISIBLE);
    }

    public void fillViews () {
        updateState();
        if (isClassExists()) {
            nextClass = getNearClassName();
            classInfo.setText(nextClass.getCourse_name());
            if (isClassStart(nextClass)) {
                syncData(nextClass);
                classStarted ();
                getStudentList(nextClass);
                makeSortedStudentList();
                searchFromSortedList();
                adapter = new P_Home_StudentAdapter(layout.getContext(), R.layout.p_home_row, searchedStudentList, allStudentList.size(), mHelper);
                listView.setAdapter(adapter);
            }
            else {
                classNotStart();
            }
        } else {
            classNotExist();
        }
    }

    public void drawViews () {
        beaconNumber = (TextView) layout.findViewById(R.id.beaconNumber);
        beaconNumber.setOnClickListener(this);

        syncButton = (Button) layout.findViewById(R.id.syncBtn);
        syncButton.setOnClickListener(this);

        classInfo = (TextView) layout.findViewById(R.id.classInfo);
        classInfo.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        classInfo.setOnClickListener(this);

        searchBox = (EditText) layout.findViewById(R.id.searchBox);
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            // 데이터가 쓰여지거나 삭제되면 동작한다.
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                /* 검색 키워드는 결석자 우선보기 메뉴에서도 사용되므로 전역변수로 관리한다.
                 * 결석생 우선보기에 따라 정렬되어있는 SortedList에서 초성검색을 한다.
                 * 검색된 데이터로 만들어진 리스트를 searchedStudentList에 저장하고
                 * adapter.notifyDataSetChanged() 메소드를 통해 listView를 갱신한다.
                 */
                searchKeyword = s.toString();
                searchFromSortedList();
                adapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        searchBox.addTextChangedListener(watcher);

        clearButton = (Button) layout.findViewById(R.id.clearButton);
        clearButton.setOnClickListener(this);

        checkBox = (CheckBox) layout.findViewById(R.id.checkBox);
        checkBox.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                makeSortedStudentList();
                searchFromSortedList();
                adapter.notifyDataSetChanged();
            }
        });

        listEmpty = (TextView) layout.findViewById(R.id.listEmpty);
        listEmpty.setVisibility(View.INVISIBLE);

        listView = (ListView) layout.findViewById(R.id.listView);
        listView.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);

        View header = (View)getActivity().getLayoutInflater().inflate(R.layout.p_home_header, null);
        listView.addHeaderView(header);
    }

    public boolean isClassStart (P_Class nextClass) {
        SimpleDateFormat formatter = new SimpleDateFormat("HHmm", Locale.KOREA);
        Calendar calendar = Calendar.getInstance();
        Date today = new Date();
        int currentTime = Integer.parseInt(calendar.get (Calendar.DAY_OF_WEEK) + formatter.format(today));

        if ((nextClass.getStart_time() <= currentTime) && (currentTime < nextClass.getEnd_time()))
            return true;
        else
            return false;
    }

    public boolean isClassExists () {
        SQLiteDatabase db;
        Cursor cursor;
        db = mHelper.getReadableDatabase();
        boolean ret = false;

        cursor = db.rawQuery("SELECT EXISTS (SELECT 1 FROM class);", null);
        cursor.moveToNext();

        if (cursor.getInt(0) != 0)
            ret = true;

        cursor.close();
        db.close();
        return ret;
    }

    public boolean isListExists (P_Class nextClass) {
        SQLiteDatabase db;
        Cursor cursor;
        boolean ret;
        db = mHelper.getReadableDatabase();
        cursor = db.rawQuery("SELECT COUNT(DISTINCT student_id) FROM student_class WHERE course_id = '" +
                nextClass.getCourse_id() + "' AND group_id = '" +
                nextClass.getGroup_id() + "';", null);
        cursor.moveToNext();

        if (cursor.getInt(0) == 0)
            ret = false;
        else
            ret = true;

        cursor.close();
        db.close();
        return ret;
    }

    public P_Class getNearClassName () {
        SimpleDateFormat formatter = new SimpleDateFormat("HHmm", Locale.KOREA);
        Calendar calendar = Calendar.getInstance();
        Date today = new Date();
        SQLiteDatabase db = mHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT course_name, MIN(start_time), end_time, course_id, group_id, timeslot_id FROM timeslot NATURAL JOIN class WHERE end_time > " +
                calendar.get(Calendar.DAY_OF_WEEK) + formatter.format(today) + ";", null);

        cursor.moveToNext();
        if (cursor.getString(0) == null) {
            cursor = db.rawQuery("SELECT course_name, MIN(start_time), end_time, course_id, group_id, timeslot_id FROM timeslot NATURAL JOIN class;", null);
            cursor.moveToNext();
        }

        P_Class nextClass = new P_Class(cursor.getString(0), Integer.parseInt(cursor.getString(1)), Integer.parseInt(cursor.getString(2)), cursor.getString(3), cursor.getString(4), cursor.getString(5));

        return nextClass;
    }

    /* studentAdapter 내부에 있는 스피너 리스너가 호출하는 함수이다.
     * 자식이 부모의 메소드를 호출하는 역참조 형태이기 때문에 관련된 함수 및 변수 모두 static 으로 관리한다.
     * 이 메소드가 호출되면 sortedStudentList를 다시 만들고 변경된 리스트에서 초성검색을 다시 실시한다.
     * 새로 만들어진 searchedStudendList로 listView를 갱신한다.
     */
    public static void refresh (String uid, String state, String url) {
        Log.d ("UID: ", uid);
        Log.d ("STATE: ", state);
        SimpleDateFormat formatter = new SimpleDateFormat("yy-MM-dd", Locale.KOREA);
        Date today = new Date();
        String currentTime = formatter.format(today);

        SQLiteDatabase db;
        db = mHelper.getWritableDatabase();

        db.execSQL("INSERT INTO ATTENDANCE_UPDATE VALUES ('" + nextClass.getCourse_id() + "', '" +
                   nextClass.getGroup_id() + "', " + nextClass.getTimeslot_id() + ", " +
                   uid + ", '" + currentTime + "', '" + state + "', null);");

        makeSortedStudentList();
        searchFromSortedList();
        adapter.notifyDataSetChanged();
    }

    // 초성검색 함수
    public static void searchFromSortedList () {
        // 호출될 때마다 초기화 작업을 수행한다. 이 작업이 없다면 리스트의 데이터는 계속 중첩되어 증가할 것이다.
        searchedStudentList.clear();
        // 학생검색란에 아무런 데이터가 없을 경우, 정렬된 데이터를 초성검색 없이 searchedStudentList에 옮긴다.
        if (searchKeyword.length() == 0) {
            for (int i = 0; i < sortedStudentList.size(); i++) {
                searchedStudentList.add(sortedStudentList.get(i));
            }
        }
        // 학생검색란에 데이터가 있을경우.
        else
        {
            P_Home_Student st;
            // sortedStudentList에 있는 모든 학생에 대해 초성검사를 실시한다.
            // 검색결과는 searchedStudentList에 저장된다.
            for (int i = 0; i < sortedStudentList.size(); i++)
            {
                st = sortedStudentList.get(i);
                if (SoundSearcher.matchString(st.getName(), searchKeyword)) {
                    searchedStudentList.add(st);
                }
            }
        }
    }

    public void insertStudentList() {
        SQLiteDatabase db;
        db = mHelper.getReadableDatabase();
        Cursor cursor;
        /*
        String [] name = {"최광규","김광규","박광규","서광규","유광규","한광규","남궁광규","팽광규","남광규","지광규",
                "최연홍","김연홍","박연홍","서연홍","유연홍","한연홍","남궁연홍","팽연홍","남연홍","지연홍",
                "최성호","김성호","박성호","서성호","유성호","한성호","남궁성호","팽성호","남성호","지성호",
                "최찬영","김찬영","박찬영","서찬영","유찬영","한찬영","남궁찬영","팽찬영","남찬영","지찬영",
                "최동환","김동환","박동환","서동환","유동환","한동환","남궁동환","팽동환","남동환","지동환",
                "최동호","김동호","박동호","서동호","유동호","한동호","남궁동호","팽동호","남동호","지동호",
                "최규열","김규열","박규열","서규열","유규열","한규열","남궁규열","팽규열","남규열","지규열",
                "최재석","김재석","박재석","서재석","유재석","한재석","남궁재석","팽재석","남재석","지재석",
                "최석진","김석진","박석진","서석진","유석진","한석진","남궁석진","팽석진","남석진","지석진",
                "최지성","김지성","박지성","서지성","유지성","한지성","남궁지성","팽지성","남지성","지지성"};
        String [] dept = {"컴퓨터공학과", "전자시스템공학과", "기계공학과"};
        String [] state = {"지각", "결석", "출석"};

        for (int i = 0; i < 100; i++) {
            db.execSQL("INSERT INTO student VALUES ('" + name[i] + "', "
                    + String.valueOf(2009036600 + i) + ", '"
                    + dept[i % 3] + "', '"
                    + "남', "
                    + String.valueOf((i % 4) + 1) + ", '"
                    + state[i%3]
                    +"');");
            Log.d("NAME: ", name[i]);
        } */

        db.close();
    }

    public void updateState () {
        SQLiteDatabase db;
        Cursor cursor;
        db = mHelper.getReadableDatabase();
        cursor = db.rawQuery("SELECT COUNT(DISTINCT student_id) FROM attendance_update;", null);
        cursor.moveToNext();

        if (cursor.getInt(0) > 0) {
            cursor = db.rawQuery("SELECT student_id, state, course_id, group_id, timeslot_id, att_date, att_index FROM attendance_update ORDER BY att_index ASC;", null);
            while (cursor.moveToNext()) {
                if (!mHelper.setState(db, cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6)))
                    if (!mHelper.setState(db, cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6)))
                        if (!mHelper.setState(db, cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6))) {
                            Log.d("UPDATE FAILED: ", cursor.getString(0) + ", " + cursor.getString(1));
                            break;
                        }
            }
        }
    }

    public void getStudentList (P_Class nextClass) {
        String s_state;
        allStudentList.clear();
        SimpleDateFormat formatter = new SimpleDateFormat("yy-MM-dd", Locale.KOREA);
        Date today = new Date();
        String currentTime = formatter.format(today);

        SQLiteDatabase db;
        Cursor cursor;
        db = mHelper.getReadableDatabase();
        cursor = db.rawQuery("SELECT DISTINCT name, student_id, state FROM student NATURAL JOIN attendance WHERE course_id ='"+nextClass.getCourse_id()+"' AND group_id = '"+nextClass.getGroup_id()+ "' AND att_date = '" + currentTime + "';", null);

        while (cursor.moveToNext()) {
            switch (cursor.getString(2)) {
                case "P":
                    s_state = "출석";
                    break;
                case "L":
                    s_state = "지각";
                    break;
                default:
                    s_state = "결석";
                    break;
            }
            allStudentList.add(new P_Home_Student(cursor.getString(0),cursor.getString(1), s_state));
        }

        cursor.close();
        db.close();
    }

    // 결석생 우선보기에 따른 정렬함수
    public static void makeSortedStudentList () {
        ArrayList<Integer> indexOfAbsentee = new ArrayList<>();
        P_Home_Student st;
        // 리스트에 있는 데이터 전부 제거.
        sortedStudentList.clear();
        // allStudentList 의 데이터를 복사하며 동시에 결석한 학생의 인덱스를 저장한다.
        for (int i = 0; i < allStudentList.size(); i++) {
            st = allStudentList.get(i);
            if (st.getState().equals("결석"))
                indexOfAbsentee.add(i);
            sortedStudentList.add(st);
        }
        // 결석생 위주로 보기 옵션이 체크되어 있는경우,
        // 마지막에 저장되어있는 결석생부터 앞으로 차례대로 제거 후 리스트의 맨 앞에 추가한다.
        if (checkBox.isChecked()) {
            int j = indexOfAbsentee.size() - 1;
            for (int i = 0; i < indexOfAbsentee.size(); i++, j--) {
                st = sortedStudentList.get(indexOfAbsentee.get(j)+i);
                sortedStudentList.remove(indexOfAbsentee.get(j)+i);
                sortedStudentList.add(0,st);
            }
        }
    }

    public void syncData (P_Class nextClass) {
        SimpleDateFormat formatter = new SimpleDateFormat("yy-MM-dd", Locale.KOREA);
        Date today = new Date();
        String currentTime = formatter.format(today);

        SQLiteDatabase db;
        db = mHelper.getWritableDatabase();
        if (!mHelper.getAttendance(db, id, uid, mac_addr, nextClass.getCourse_id(), nextClass.getGroup_id(), nextClass.getTimeslot_id(), currentTime)) {
            if (!mHelper.getAttendance(db, id, uid, mac_addr, nextClass.getCourse_id(), nextClass.getGroup_id(), nextClass.getTimeslot_id(), currentTime)) {
                if (!mHelper.getAttendance(db, id, uid, mac_addr, nextClass.getCourse_id(), nextClass.getGroup_id(), nextClass.getTimeslot_id(), currentTime)) {
                    Log.d("연결에 실패하였습니다.", "");
                    // Wifi - CHECK
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        SQLiteDatabase db;
        ContentValues row;
        int viewId = v.getId();
        // 삭제버튼에 대한 리스너. 버튼이 눌러지면 학생검색란이 초기화된다.
        if (viewId == R.id.clearButton) {
            EditText searchBox = (EditText) getView().findViewById(R.id.searchBox);
            searchBox.setText(null);
        }
        else if (viewId == R.id.listEmpty) {
            Log.d("listEmpty", "");
            db = mHelper.getWritableDatabase();
            if(mHelper.getInitData(db, id, uid, mac_addr)) {
                fillViews();
                Toast.makeText(getActivity().getApplicationContext(), "데이터를 받아왔습니다.", Toast.LENGTH_SHORT);
            }
            else {
                Toast.makeText(getActivity().getApplicationContext(), "데이터를 받아오지 못했습니다.", Toast.LENGTH_SHORT);
            }
        }
        else if (viewId == R.id.syncBtn) {
            fillViews();
        }
        else {
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(layout.getWindowToken(), 0);
        }
    }
}

