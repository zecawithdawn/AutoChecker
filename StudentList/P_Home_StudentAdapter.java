package com.example.kyh.real.StudentList;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.kyh.real.Database.DbAdapter;
import com.example.kyh.real.P_F.P_F_Home;
import com.example.kyh.real.R;

import java.util.ArrayList;

/**
 * Created by park on 2015. 1. 9..
 */
public class P_Home_StudentAdapter extends BaseAdapter implements View.OnClickListener {
    DbAdapter mHelper;
    LayoutInflater layoutInflater;
    /* oldState - 학생의 기존 출석상태를 저장한다.
     * Spinner 값이 변할 때마다 즉각적으로 listView를 갱신하기 위해서는 개별 Spinner 리스너가
     * 동작할 때마다 부모 프레그먼트의 메소드를 호출하여 데이터를 갱신해줘야 한다. 하지만 Spinner
     * 리스너는 학생들의 초기 출석상태를 세팅해주는 과정에서도 동작하기 때문에 이렇게 별도의 oldState
     * 를 관리해주는 배열을 선언하여 이전값과 비교, 값이 다른 경우에만 동작하게 하였다.
     */
    String[] oldState;
    // 어댑터가 listView에 전개해야할 데이터 리스트이다.
    private ArrayList<P_Home_Student> items;
    private int layoutResourceId;
    private Context context;

    public P_Home_StudentAdapter(Context context, int layoutResourceId, ArrayList<P_Home_Student> items, int studentSize, DbAdapter mHelper) {
        super();
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.items = items;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // StudentAdapter가 호출되는 가장 첫 시점, 모든 학생들의 수 만큼 배열을 잡아준다.
        oldState = new String[studentSize];
        this.mHelper = mHelper;
    }

    // 사용안함.
    @Override
    public int getCount() {
        return items.size();
    }

    // 이름 또는 학번이 선택된 경우 화면전환.
    @Override
    public void onClick(View v) {
//        Log.d ("Name: ", items.get(Integer.parseInt(v.getTag().toString())).getName());
        Intent i = new Intent(context, P_AttendanceInfo.class);
        i.putExtra("name",items.get(Integer.parseInt(v.getTag().toString())).getName());
        context.startActivity(i);
    }

    @Override
    /*  getView
     *  position: 행의 index
     *  convertView: 행 전체를 나타내는 뷰.
     *  parent: 어댑터를 갖고있는 부모 뷰를 나타낸다. adapter를 set한 ListView가 된다.
     */
    public View getView(final int position, View convertView, ViewGroup parent) {
        // findViewById 메소드 호출을 줄이기 위해 Holder를 만든다.
        // Holder의 사용은 리스트뷰 속도 개선에 큰 효과를 줄 수 있다.
        final viewHolder mHolder;
        // convertView 에 널이 넘어오면 inflater로 layout을 전개한다.
        // 홀더를 생성 후 태그를 달아준다.
        if (convertView == null) {
            convertView = layoutInflater.inflate(layoutResourceId, null);

            // mHolder 생성 후 각 view를 매칭시켜준다.
            mHolder = new viewHolder();
            mHolder.txt_name = (TextView) convertView.findViewById(R.id.column1);
            mHolder.txt_id = (TextView) convertView.findViewById(R.id.column2);
            mHolder.spinner_state = (Spinner) convertView.findViewById(R.id.column3);
            mHolder.left_dotted_line = (ImageView) convertView.findViewById(R.id.leftDottedLine);

            // mHolder에 태그를 등록한다.
            convertView.setTag(mHolder);
            // 스피너 어댑터 생성 및 등록.
            ArrayAdapter spinAdapter = ArrayAdapter.createFromResource(context, R.array.state, R.layout.p_home_spinner_layout);
            spinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mHolder.spinner_state.setAdapter(spinAdapter);
        } else {
            // 리스트 뷰는 convertView를 recycle 하기 때문에, convertView가 null이 아니라면
            // 저장된 태그값으로 mHolder를 불러와서 보여준다.
            mHolder = (viewHolder) convertView.getTag();
        }
        /* 스피너 리스너 객체를 생성해주는 과정이다.
         * 객체를 생성해주는 과정이기 때문에 if (convertView == null) 안에서 동작하는게 효율적일 것 같지만
         * 실제 동작은 효율적일지 몰라도 큰 문제가 발생한다. listView의 child는 어댑터에서 설정한 리스트 전체를
         * 갖고 있지 않다. 화면에 보이는 리스트 만큼의 child를 갖고 있는데, 화면에 보이는 리스트 개수를 넘어가는
         * 리스트들에 대해서 recycle된 convertView를 넘겨주기 때문에 전혀 엉뚱한 row의 스피너 값이 변경되는
         * 현상을 목격할 수 있다. 따라서 이 곳에 리스너를 위치시켜야 정상 작동을 할 수 있다.
         */
        mHolder.spinner_state.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                String selected = parent.getItemAtPosition(pos).toString();
                if (!oldState[position].equals(selected)) {
                    SQLiteDatabase db;
                    db = mHelper.getWritableDatabase();
                    P_Home_Student D = (P_Home_Student) getItem(position);
                    D.setState(selected);
                    // course_id , group_id, timeslot_id where로 확인해야한다.
//                    db.execSQL("UPDATE attendance SET state = '"+selected+"' WHERE name is '"+D.getName()+"' AND student_id is "+D.getId()+";");
                    db.close();
                    Log.d("name: ", "" + ((P_Home_Student) getItem(position)).getName());
                    P_F_Home.refresh(D.getId(), D.getState(), "set_attendance");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // 리스트에서 position을 index로 데이터를 가져온다.
        P_Home_Student data = (P_Home_Student) getItem(position);
        // 학생이름 저장 후 리스너 등록.
        mHolder.txt_name.setText(data.name);
        mHolder.txt_name.setOnClickListener(this);
        mHolder.txt_name.setTag(position);
        // 학번 저장 후 리스너 등록.
        mHolder.txt_id.setText(data.id);
        mHolder.txt_id.setOnClickListener(this);
        mHolder.txt_id.setTag(position);

        mHolder.left_dotted_line.setOnClickListener(this);
        mHolder.left_dotted_line.setTag(position);

        // 스피너 초기 데이터 설정 및 이전 값 저장.
        oldState[position] = data.getState();
        mHolder.spinner_state.setSelection(data.state.equals("결석") ? 2 : data.state.equals("지각") ? 1 : 0);

        //  return
        return convertView;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        // 아주 드물게, 초성 검색에 따른 데이터 refresh 과정에서 BoundOfIndexException이 발생할 수 있다.
        if (position < items.size())
            return items.get(position);
        return null;
    }

    // ViewHolder
    public class viewHolder {
        TextView txt_name;
        TextView txt_id;
        Spinner spinner_state;
        ImageView left_dotted_line;
    }
}