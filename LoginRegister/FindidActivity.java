package com.example.kyh.real.LoginRegister;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.kyh.real.R;

/**
 * Created by choigwanggyu on 2015. 3. 23..
 */
public class FindidActivity extends Activity{

    TextView Fi_sitext;
    TextView Fi_sntext;

    EditText Fi_sitf;
    EditText Fi_sntf;

    TextView Fi_letf;
    ToggleButton Fi_ptb;
    ToggleButton Fi_stb;
    Button Fi_rbt;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_findid);

        //view id 선언을 위한 함수
        set_viewid();

        //watcher 및 클릭 이벤트 설정
        set_eventListener();

    }

    private void set_viewid() {
        Fi_sitf = (EditText) findViewById(R.id.FI_sitf);
        Fi_sntf = (EditText) findViewById(R.id.FI_sntf);

        Fi_sitext = (TextView) findViewById(R.id.FI_sitext);
        Fi_sntext = (TextView) findViewById(R.id.FI_sntext);

        Fi_letf = (TextView) findViewById(R.id.FI_letf);
        Fi_rbt = (Button) findViewById(R.id.FI_button);

        Fi_ptb = (ToggleButton) findViewById(R.id.FI_pro);
        Fi_stb = (ToggleButton) findViewById(R.id.FI_stu);
        ((RadioGroup) findViewById(R.id.FI_rbg)).setOnCheckedChangeListener(ToggleListener);

    }

    private void set_eventListener() {

        
    }

    //라디오 버튼 하나만 눌리게 하기
    static final RadioGroup.OnCheckedChangeListener ToggleListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(final RadioGroup radioGroup, final int i) {
            for (int j = 0; j < radioGroup.getChildCount(); j++) {
                final ToggleButton view = (ToggleButton) radioGroup.getChildAt(j);
                view.setChecked(view.getId() == i);
            }
        }
    };

    public void onToggle(View view) {
        ((RadioGroup) view.getParent()).check(view.getId());
        // app specific stuff ..
        if (Fi_ptb.isChecked()) {
            Fi_ptb.setBackgroundColor(Color.argb(80, 255, 255, 255));
            Fi_stb.setBackgroundColor(Color.argb(32, 75, 75, 75));
        } else {
            Fi_stb.setBackgroundColor(Color.argb(80, 255, 255, 255));
            Fi_ptb.setBackgroundColor(Color.argb(32, 75, 75, 75));
        }

    }
}
