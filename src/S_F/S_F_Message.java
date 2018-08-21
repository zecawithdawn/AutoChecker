package com.example.kyh.real.S_F;

import android.content.Intent;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.kyh.real.Database.DbAdapter;
import com.example.kyh.real.Library.SSLTask;
import com.example.kyh.real.Library.TaskListener;
import com.example.kyh.real.R;
import com.example.kyh.real.S_Message.S_Message_Adapter;
import com.example.kyh.real.S_Message.S_Message_Info;
import com.example.kyh.real.S_Message.S_Message_List_data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.zip.Inflater;

/**
 * Created by kyh on 2015. 1. 24..
 */
public class S_F_Message extends S_Total_MainActivity.PlaceholderFragment implements TaskListener{
    private static DbAdapter mHelper;

    Cursor cursor_resume;
    private ArrayList<S_Message_List_data> Array_Data;
    private S_Message_List_data data;
    private S_Message_Adapter adapter;

    private String uid;
    private int db_ver;
    private String id;
    private String mac_addr;
    private String level;

    ImageView imageView;
    TextView listEmpty;
    ListView listView;
    TextView text;
    ListView s_message_list;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        uid = bundle.getString("uid", "Default UID");
        db_ver = Integer.parseInt(bundle.getString("db_ver"));
        id = bundle.getString("id");
        mac_addr = bundle.getString("mac_addr");
        level = bundle.getString("level");
    }

    @Override
    public void onResume() {

        SQLiteDatabase db = mHelper.getReadableDatabase();

        for (int index = 0; index < Array_Data.size(); index++) {
            try {
                cursor_resume = db.rawQuery("SELECT count(checked) FROM message JOIN class ON message.course_id = class.course_id WHERE course_name = '" + Array_Data.get(index).getReal() + "' group by checked having checked = 'N' ;", null);
                cursor_resume.moveToNext();

                Array_Data.get(index).setMessage_Num(cursor_resume.getString(0));
            } catch (CursorIndexOutOfBoundsException exception) {
                Array_Data.get(index).setMessage_Num("0");
            }


            adapter.notifyDataSetChanged();
        }

        super.onResume();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_s_message, container, false);
        mHelper = new DbAdapter(rootView.getContext(), uid, db_ver, level);

        imageView = (ImageView) rootView.findViewById(R.id.s_message_nimage);
        listEmpty = (TextView) rootView.findViewById(R.id.no_data1);
        listView = (ListView) rootView.findViewById(R.id.s_message_listv);

        Array_Data = new ArrayList<S_Message_List_data>();

//
//        data = new S_Message_List_data("인문학"+"\n"+"특강","홍길동 교수님","오늘 수업 하기 싫습니다 오지마세요 ...","1");
//        Array_Data.add(data);
//

        s_message_list = (ListView) rootView.findViewById(R.id.s_message_listv);
        s_message_list.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);


        if (getMessage()) {
            listEmpty.setVisibility(View.VISIBLE);
            listView.setVisibility(View.INVISIBLE);
            imageView.setVisibility(View.INVISIBLE);
        }
        listEmpty.setVisibility(View.INVISIBLE);
        listView.setVisibility(View.VISIBLE);
        imageView.setVisibility(View.VISIBLE);

        int lastest_message = 1;
        lastest_message = getLastMessage();


//        try {

            JSONObject requestjson = new JSONObject();
        try {
            requestjson.put("uid", uid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            requestjson.put("id", id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            requestjson.put("recent", lastest_message + "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //로그인을위해 데이터를 서버로 보냄
            SSLTask sslTask = new SSLTask("https://166.104.245.43/get_message.php", requestjson, this);
             sslTask.execute();
            //response를 받아온다
//            JSONObject responsejson = sslTask.execute().get();
//
//            if (responsejson.get("getmsg").equals("S")) {
//                if (responsejson.get("nodata").equals("N")) {
//                    JSONArray messageArray = responsejson.getJSONArray("message");
//                    SQLiteDatabase db = mHelper.getWritableDatabase();
//
//                    for (int k = 0; k < messageArray.length(); k++) {
//                        db.execSQL("INSERT INTO message VALUES (" + new String(messageArray.get(k).toString().getBytes("ISO-8859-1"), "UTF-8") + ");");
//                        Log.d("QUERY: ", messageArray.get(k).toString());
//                    }
//                } else {
//
//                }
//            } else {
//
//            }
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        } catch (JSONException e) {
//            e.printStackTrace();
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }

        adapter = new S_Message_Adapter(rootView.getContext(), android.R.layout.simple_list_item_1, Array_Data);
        s_message_list.setAdapter(adapter);
//
//        s_message_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//                S_Message_List_data datata = adapter.getItem(position);
//                Intent info_activity = new Intent(getActivity(), S_Message_Info.class);
//                info_activity.putExtra("course_name", Array_Data.get(position).getReal());
//                info_activity.putExtra("prof_name", Array_Data.get(position).getMain_Title());
//                info_activity.putExtra("msg", Array_Data.get(position).getSub_Title());
//                info_activity.putExtra("checked", Array_Data.get(position).getMessage_Num());
//
//                info_activity.putExtra("uid", uid);
//                info_activity.putExtra("db_ver", db_ver);
//                info_activity.putExtra("level", level);
//                startActivity(info_activity);
//
//            }
//        });

        return rootView;


    }

    public boolean getMessage() {
        SQLiteDatabase db = mHelper.getReadableDatabase();
        Cursor cursor1, cursor2, cursor3;
        cursor1 = db.rawQuery("SELECT course_name, prof_name, course_id FROM class ;", null);

        String token_class_name;
        String token_class_name3;
        String token_class_name2;

        String class_id;

        while (cursor1.moveToNext()) {


            class_id = "'" + cursor1.getString(2) + "'";
            cursor2 = db.rawQuery("SELECT msg_id, msg FROM message WHERE course_id = " + class_id + "order by msg_id desc" + ";", null);
            token_class_name = cursor1.getString(0).trim().replaceAll(" ","");
            token_class_name3=token_class_name.substring(0,3);
            token_class_name2=token_class_name.substring(3,5);
//            token_class_name3+"\n"+token_class_name2;
//            Log.d("전체",token_class_name);
//            Log.d("3",token_class_name3);
//            Log.d("2",token_class_name2);

            cursor2.moveToNext();
            try {
                cursor3 = db.rawQuery("SELECT count(checked) FROM message WHERE course_id = " + class_id + "group by checked having checked = 'N' ;", null);
                cursor3.moveToNext();
                Log.d("갯",cursor3.getString(0));
                data = new S_Message_List_data(token_class_name3+"\n"+token_class_name2, cursor1.getString(1), cursor2.getString(1), cursor3.getString(0));
                data.setReal(cursor1.getString(0));
            } catch (CursorIndexOutOfBoundsException e) {
                data = new S_Message_List_data(token_class_name3+"\n"+token_class_name2, cursor1.getString(1), cursor2.getString(1), "0");
                data.setReal(cursor1.getString(0));
            }

            Array_Data.add(data);

        }


        db.close();
        return true;
    }

    public int getLastMessage() {
        SQLiteDatabase db = mHelper.getReadableDatabase();
        Cursor cursor;
        int msg_id = 1;
        cursor = db.rawQuery("SELECT msg_id FROM message order by msg_id desc ;", null);
        cursor.moveToNext();
        msg_id = cursor.getInt(0);

        db.close();
        return msg_id;
    }

    @Override
    public void onReceived(JSONObject jsonData) {
//        JSONObject responsejson = sslTask.execute().get();

        try {
            if (jsonData.get("getmsg").equals("S")) {
                if (jsonData.get("nodata").equals("N")) {
                    JSONArray messageArray = jsonData.getJSONArray("message");
                    SQLiteDatabase db = mHelper.getWritableDatabase();

                    for (int k = 0; k < messageArray.length(); k++) {
                        db.execSQL("INSERT INTO message VALUES (" + new String(messageArray.get(k).toString().getBytes("ISO-8859-1"), "UTF-8") + ");");
                        Log.d("QUERY: ", messageArray.get(k).toString());
                    }
                } else {

                }
            } else {

            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

//        adapter = new S_Message_Adapter(rootView.getContext(), android.R.layout.simple_list_item_1, Array_Data);
//        s_message_list.setAdapter(adapter);

        s_message_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                S_Message_List_data datata = adapter.getItem(position);
                Intent info_activity = new Intent(getActivity(), S_Message_Info.class);
                info_activity.putExtra("course_name", Array_Data.get(position).getReal());
                info_activity.putExtra("prof_name", Array_Data.get(position).getMain_Title());
                info_activity.putExtra("msg", Array_Data.get(position).getSub_Title());
                info_activity.putExtra("checked", Array_Data.get(position).getMessage_Num());

                info_activity.putExtra("uid", uid);
                info_activity.putExtra("db_ver", db_ver);
                info_activity.putExtra("level", level);
                startActivity(info_activity);

            }
        });

    }




    @Override
    public void onCanceled() {

    }
}

