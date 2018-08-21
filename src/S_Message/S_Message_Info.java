package com.example.kyh.real.S_Message;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.kyh.real.Database.DbAdapter;
import com.example.kyh.real.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import static android.app.PendingIntent.getActivity;

public class S_Message_Info extends ActionBarActivity {
    private static DbAdapter mHelper;

    private ArrayList<S_Message_Info_List_data> Array_Data;
    private S_Message_Info_List_data data;
    private S_Message_Info_Adapter adapter;

    private Dialog mDialog = null;

    String save_course_name;
    String save_prof_name;
    TextView listEmpty;
    ListView listView;
    TextView textView1;
    TextView textView2;
    RelativeLayout relativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendenceinfo);
//        Typeface tf = Typeface.createFromAsset(getAssets(), "KoPubDotumMedium.ttf");
//        TextView coursename = (TextView)findViewById(R.id.s_m_info_content);
//        TextView profname= (TextView)findViewById(R.id.s_m_info_profname);
//        coursename.setTypeface(tf);
//        profname.setTypeface(tf);
//        Intent intent = getIntent();
//
//        String course_name = intent.getExtras().getString("course_name");
//        String prof_name = intent.getExtras().getString("prof_name");
//
//        String uid = intent.getExtras().getString("uid");
//        Integer db_ver = intent.getExtras().getInt("db_ver");
//        String level = intent.getExtras().getString("level");
        String token_class_name;
        String token_class_name3;
        String token_class_name2;


        Intent intent = getIntent();
        String course_name = intent.getStringExtra("course_name");
        String prof_name = intent.getStringExtra("prof_name");

        save_course_name = intent.getStringExtra("course_name");

//        token_class_name = save_course_name.trim().replaceAll(" ","");
        Log.d("name",save_course_name);
        Log.d("name",course_name);
//        token_class_name3=token_class_name.substring(0, 3);
//        Log.d("3",token_class_name3);
////        token_class_name2=token_class_name.substring(3, 5);
//        Log.d("2",token_class_name2);

        save_prof_name = intent.getStringExtra("prof_name");

        String uid = intent.getStringExtra("uid");
        Integer db_ver = intent.getExtras().getInt("db_ver");
        String level = intent.getStringExtra("level");

        mHelper = new DbAdapter(getApplicationContext(), uid, db_ver, level);

//      listEmpty = (TextView) findViewById(R.id.no_data2);
        listView = (ListView) findViewById(R.id.s_m_info_listv);
        textView1 = (TextView) findViewById(R.id.s_m_info_course);
        textView2 = (TextView) findViewById(R.id.s_m_info_profname);
//        textView1.setText(token_class_name3+"\n"+token_class_name2);
        textView1.setText("fuck");
        textView2.setText(prof_name);

        Array_Data = new ArrayList<S_Message_Info_List_data>();

//        data = new S_Message_Info_List_data("야이 개새키들아 오지마라 수업 안한다. 서성호는 짱이다." + "\n" + "위대한 서성만 개츠비보다 더 좋" + "\n"
//                , "2015-2-20");
//        Array_Data.add(data);
//        data = new S_Message_Info_List_data("야이 개새키들아 오지마라 수업 안한다. 서성호는 짱이다." + "\n" + "위대한 서성만 개츠비보다 더 좋" + "\n"
//                , "2015-2-20");


        ListView s_message_list = (ListView) findViewById(R.id.s_m_info_listv);
        s_message_list.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);

        if (getMessageInfo()) {
//            listEmpty.setVisibility(View.VISIBLE);
            listView.setVisibility(View.INVISIBLE);
            textView1.setVisibility(View.INVISIBLE);
            textView2.setVisibility(View.INVISIBLE);
        }
//        listEmpty.setVisibility(View.INVISIBLE);
        listView.setVisibility(View.VISIBLE);
        textView1.setVisibility(View.VISIBLE);
        textView2.setVisibility(View.VISIBLE);


        adapter = new S_Message_Info_Adapter(this, android.R.layout.simple_list_item_1, Array_Data);
        s_message_list.setAdapter(adapter);

        s_message_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                S_Message_Info_List_data datata = adapter.getItem(position);
                final Dialog dialog = new Dialog(S_Message_Info.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
///              dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                dialog.setContentView(R.layout.s_message_dialog);
                TextView txt = (TextView) dialog.findViewById(R.id.txt);
                txt.setText(datata.getM_s_m_info_content());
                TextView d_date = (TextView) dialog.findViewById(R.id.d_date);
                d_date.setText(datata.getM_s_m_info_date());

                dialog.show();


                checkedUpdate(position);

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

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

    public boolean getMessageInfo() {

        SQLiteDatabase db = mHelper.getReadableDatabase();
        Cursor cursor;
        cursor = db.rawQuery("select msg, senddate, checked from class join message on class.course_id = message.course_id where course_name = '" + save_course_name + "';", null);

        while (cursor.moveToNext()) {
            String date_cut = cursor.getString(1);
            data = new S_Message_Info_List_data(cursor.getString(0), date_cut.substring(0, 10));
            Array_Data.add(data);
        }
        db.close();
        return true;
    }

    public boolean checkedUpdate(int position) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
//        Log.d("empty",Array_Data.get(position).getM_s_m_info_content());
        db.execSQL("update message set checked = 'Y' where msg = '" + Array_Data.get(position).getM_s_m_info_content() + "';");

//        Cursor cursor;
//        cursor=db.rawQuery("select * from message;",null);
////        while (cursor.moveToNext()){
////            Log.d("바껴라 시발아 ", cursor.getString(4));
////        }
        return true;
    }
}
