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
public class FindpwActivity extends Activity {

    TextView Fp_idtext;
    TextView Fp_sntext;
    TextView Fp_sitext;

    EditText Fp_idtf;
    EditText Fp_sntf;
    EditText Fp_sitf;

    TextView Fp_letf;
    ToggleButton Fp_ptb;
    ToggleButton Fp_stb;
    Button Fp_rbt;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_findpw);

        //view id 선언을 위한 함수
        set_viewid();

        //watcher 및 클릭 이벤트 설정
        set_eventListener();

    }

    private void set_viewid() {
        Fp_idtf = (EditText) findViewById(R.id.FP_idtf);
        Fp_sntf = (EditText) findViewById(R.id.FP_sntf);
        Fp_sitf = (EditText) findViewById(R.id.FP_sitf);

        Fp_idtext = (TextView) findViewById(R.id.FP_idtext);
        Fp_sntext = (TextView) findViewById(R.id.FP_sntext);
        Fp_sitext = (TextView) findViewById(R.id.FP_sitext);

        Fp_letf = (TextView) findViewById(R.id.FP_letf);
        Fp_rbt = (Button) findViewById(R.id.FP_button);

        Fp_ptb = (ToggleButton) findViewById(R.id.FP_pro);
        Fp_stb = (ToggleButton) findViewById(R.id.FP_stu);
        ((RadioGroup) findViewById(R.id.FP_rbg)).setOnCheckedChangeListener(ToggleListener);

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
        if (Fp_ptb.isChecked()) {
            Fp_ptb.setBackgroundColor(Color.argb(80, 255, 255, 255));
            Fp_stb.setBackgroundColor(Color.argb(32, 75, 75, 75));
        } else {
            Fp_stb.setBackgroundColor(Color.argb(80, 255, 255, 255));
            Fp_ptb.setBackgroundColor(Color.argb(32, 75, 75, 75));
        }

    }
}
