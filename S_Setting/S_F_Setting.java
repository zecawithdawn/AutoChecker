package com.example.kyh.real.S_Setting;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kyh.real.R;
import com.example.kyh.real.S_F.S_Total_MainActivity;

/**
 * Created by kyh on 2015. 1. 24..
 */

//<!>1.쉐어드 프리퍼런스로 바뀐 정보 받아오기
//   2.버튼 및 텍스트 수정
public class S_F_Setting extends S_Total_MainActivity.PlaceholderFragment implements View.OnClickListener {

    TextView s_setting_version_text;

    ImageView s_setting_id_button;
    CheckBox s_setting_autologin_checkbox;
    ImageView s_setting_alarm_button;
    CheckBox s_setting_push_nortification_checkbox;
    ImageView s_setting_version_button;


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_s_setting, container, false);

        setting_event(rootView);

        return rootView;
    }

    private void setting_event(View rootview) {
        s_setting_version_text = (TextView) rootview.findViewById(R.id.s_setting_version_text);

        s_setting_id_button = (ImageView) rootview.findViewById(R.id.s_setting_id_button);
        s_setting_alarm_button = (ImageView) rootview.findViewById(R.id.s_setting_alarm_button);
        s_setting_version_button = (ImageView) rootview.findViewById(R.id.s_setting_version_button);

        s_setting_autologin_checkbox = (CheckBox) rootview.findViewById(R.id.s_setting_autoLogin_checkbox);
        s_setting_push_nortification_checkbox = (CheckBox) rootview.findViewById(R.id.s_setting_push_notification_checkbox);


        s_setting_id_button.setOnClickListener(this);
        s_setting_alarm_button.setOnClickListener(this);
        s_setting_version_button.setOnClickListener(this);


    }


    @Override
    public void onClick(View v) {
        Intent i = null;
        if (v.getId() == R.id.s_setting_id_button) {
            i = new Intent(getActivity(), S_Setting_Id.class);
            startActivity(i);
        } else if (  v.getId() == R.id.s_setting_alarm_button ) {
            i = new Intent(getActivity(), S_Setting_Alarm.class);
            startActivity(i);
        } else if (v.getId() == R.id.s_setting_version_button) {
            i = new Intent(getActivity(), S_Setting_Version.class);
            startActivity(i);
        }
    }
}

