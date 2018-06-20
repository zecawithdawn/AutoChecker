package com.example.kyh.real.LoginRegister;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.kyh.real.Library.SSLTask;
import com.example.kyh.real.Library.TaskListener;
import com.example.kyh.real.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


public class RegActivity extends Activity implements View.OnClickListener, TaskListener {

    //GUI변수 선언
    EditText reg_idtf;
    EditText reg_pwtf;
    EditText reg_prtf;
    EditText reg_sitf;
    EditText reg_sntf;
    EditText reg_setf;

    TextView reg_sitext;
    TextView reg_idef;
    TextView reg_pwef;
    TextView reg_pref;
    TextView reg_snef;
    TextView reg_sief;
    TextView reg_seef;

    ImageView reg_idcheck;
    ImageView reg_pwcheck;
    ImageView reg_prcheck;
    ImageView reg_sncheck;
    ImageView reg_sicheck;
    ImageView reg_secheck;

    TextView reg_letf;
    ToggleButton reg_ptb;
    ToggleButton reg_stb;
    RelativeLayout reg_lay1;
    RelativeLayout reg_lay2;
    Button reg_rbt;
    ImageView reg_back;

    int idfocus, pwfocus, prfocus, snfocus, sifocus, sefocus;

    //<!>ssltask 변경

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);

        //view id 선언을 위한 함수
        set_viewid();

        //watcher 및 클릭 이벤트 설정
        set_eventListener();

    }


    //view id 셋팅
    private void set_viewid() {
        reg_idtf = (EditText) findViewById(R.id.Reg_idtf);
        reg_pwtf = (EditText) findViewById(R.id.Reg_pwtf);
        reg_prtf = (EditText) findViewById(R.id.Reg_prtf);
        reg_sntf = (EditText) findViewById(R.id.Reg_sntf);
        reg_sitf = (EditText) findViewById(R.id.Reg_sitf);
        reg_setf = (EditText) findViewById(R.id.Reg_setf);

        reg_sitext = (TextView) findViewById(R.id.Reg_sitext);

        reg_idef = (TextView) findViewById(R.id.Reg_idef);
        reg_pwef = (TextView) findViewById(R.id.Reg_pwef);
        reg_pref = (TextView) findViewById(R.id.Reg_pref);
        reg_sief = (TextView) findViewById(R.id.Reg_sief);
        reg_snef = (TextView) findViewById(R.id.Reg_snef);
        reg_seef = (TextView) findViewById(R.id.Reg_seef);

        reg_idcheck = (ImageView) findViewById(R.id.Reg_idcheck);
        reg_pwcheck = (ImageView) findViewById(R.id.Reg_pwcheck);
        reg_prcheck = (ImageView) findViewById(R.id.Reg_prcheck);
        reg_sncheck = (ImageView) findViewById(R.id.Reg_sncheck);
        reg_sicheck = (ImageView) findViewById(R.id.Reg_sicheck);
        reg_secheck = (ImageView) findViewById(R.id.Reg_secheck);

        reg_letf = (TextView) findViewById(R.id.Reg_letf);
        reg_rbt = (Button) findViewById(R.id.Reg_button);
        reg_back = (ImageView) findViewById(R.id.reg_backbutton);
        reg_lay1 = (RelativeLayout) findViewById(R.id.Reglayout);
        reg_lay2 = (RelativeLayout) findViewById(R.id.Reginlayout);
        reg_ptb = (ToggleButton) findViewById(R.id.Rb_pro);
        reg_stb = (ToggleButton) findViewById(R.id.Rb_stu);
        ((RadioGroup) findViewById(R.id.Reg_rbg)).setOnCheckedChangeListener(ToggleListener);
    }

    //event 설정
    private void set_eventListener() {
        //clicklistener설정
        reg_lay1.setOnClickListener(this);
        reg_lay2.setOnClickListener(this);
        reg_rbt.setOnClickListener(this);
        reg_back.setOnClickListener(this);

        //텍스트 입력시 예외처리 확인
        idfocus = 0;
        pwfocus = 0;
        prfocus = 0;
        snfocus = 0;
        sifocus = 0;
        sefocus = 0;
        reg_idtf.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                idtext_check(reg_idtf.getText().toString());
            }
        });
        reg_pwtf.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                pwtext_check(reg_pwtf.getText().toString());
            }
        });
        reg_prtf.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                prtext_check(reg_pwtf.getText().toString(), reg_prtf.getText().toString());
            }
        });
        reg_sntf.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                sntext_check(reg_sntf.getText().toString());
            }
        });
        reg_sitf.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                sitext_check(reg_sitf.getText().toString());
            }
        });
        reg_setf.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                setext_check(reg_setf.getText().toString());
            }
        });
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
        if (reg_ptb.isChecked()) {
            reg_ptb.setBackgroundColor(Color.argb(80, 255, 255, 255));
            reg_stb.setBackgroundColor(Color.argb(32, 75, 75, 75));
        } else {
            reg_stb.setBackgroundColor(Color.argb(80, 255, 255, 255));
            reg_ptb.setBackgroundColor(Color.argb(32, 75, 75, 75));
        }

    }

    //아이디 에러처리
    private void idtext_check(String id) {
        if (id.isEmpty()) {
            if (idfocus != 0) {
                reg_idef.setTextColor(Color.RED);
                reg_idef.setText("아이디를 입력해주세요");
                reg_idcheck.setBackgroundDrawable(getResources().getDrawable(R.drawable.reg_error));
            }
            idfocus++;
        } else {
            int digit_num = 0, char_num = 0;
            if (id.length() < 6 && id.length() > 16) {
                reg_idef.setTextColor(Color.RED);
                reg_idef.setText("6자이상 16자이하의 아이디를 입력해주세요");
                reg_idcheck.setBackgroundDrawable(getResources().getDrawable(R.drawable.reg_error));
            } else {
                for (int i = 0; i < id.length(); i++) {
                    if (Character.isDigit(id.charAt(i))) digit_num++;
                    if (Character.isLetter(id.charAt(i))) char_num++;
                }
                if (digit_num == 0 || char_num == 0) {
                    reg_idef.setTextColor(Color.RED);
                    reg_idef.setText("아이디는 영문,숫자 조합만 가능합니다");
                    reg_idcheck.setBackgroundDrawable(getResources().getDrawable(R.drawable.reg_error));
                } else if (digit_num + char_num != id.length()) {
                    reg_idef.setTextColor(Color.RED);
                    reg_idef.setText("특수문자는 입력이 불가능합니다.");
                    reg_idcheck.setBackgroundDrawable(getResources().getDrawable(R.drawable.reg_error));
                } else {
                    if (check_internetstate()) {
                        JSONObject check = new JSONObject();
                        try {
                            check.put("id", id);

                            SSLTask ssltask = new SSLTask("https://166.104.245.43/precheck.php", check, this);
                            JSONObject rescheckid = ssltask.execute().get(2, TimeUnit.SECONDS);

                            if (rescheckid.get("check").equals("S")) {
                                reg_idef.setText("");
                                reg_idef.setText("사용 할 수 있는 아이디입니다.");
                                reg_idef.setTextColor(Color.BLACK);
                                reg_idcheck.setBackgroundDrawable(getResources().getDrawable(R.drawable.reg_checked));
                            } else {
                                reg_idef.setTextColor(Color.RED);
                                reg_idef.setText("중복된 아이디 입니다.다른 아이디를 입력해주세요.");
                                reg_idcheck.setBackgroundDrawable(getResources().getDrawable(R.drawable.reg_error));
                            }
                        } catch (JSONException | InterruptedException | ExecutionException e) {
                            e.printStackTrace();
                        } catch (TimeoutException e) {
                            reg_idef.setTextColor(Color.RED);
                            reg_idef.setText("서버와의 통신이 원활하지 않습니다.");
                            reg_idcheck.setBackgroundDrawable(getResources().getDrawable(R.drawable.reg_error));
                        }
                    } else {
                        reg_idef.setTextColor(Color.RED);
                        reg_idef.setText("인터넷이 연결되어있지 않습니다");
                        reg_idcheck.setBackgroundDrawable(getResources().getDrawable(R.drawable.reg_error));
                    }
                }
            }
        }
    }

    //비밀번호 에러처리
    private void pwtext_check(String pw) {
        if (pw.isEmpty()) {
            if (pwfocus != 0) {
                reg_pwef.setTextColor(Color.RED);
                reg_pwef.setText("비밀번호를 입력해주세요");
                reg_pwcheck.setBackgroundDrawable(getResources().getDrawable(R.drawable.reg_error));
            }
            pwfocus++;
        } else {
            int digit_num = 0, char_num = 0;
            if (pw.length() > 16) {
                reg_pwef.setTextColor(Color.RED);
                reg_pwef.setText("25자 이하의 비밀번호를 설정해주세요");
                reg_pwcheck.setBackgroundDrawable(getResources().getDrawable(R.drawable.reg_error));
            } else {
                for (int i = 0; i < pw.length(); i++) {
                    if (Character.isDigit(pw.charAt(i))) digit_num++;
                    if (Character.isLetter(pw.charAt(i))) char_num++;
                }
                if (digit_num + char_num != pw.length()) {
                    reg_pwef.setTextColor(Color.RED);
                    reg_pwef.setText("특수문자는 사용할수 없습니다.");
                    reg_pwcheck.setBackgroundDrawable(getResources().getDrawable(R.drawable.reg_error));
                } else {
                    reg_pwef.setText("올바른 형식입니다.");
                    reg_pwcheck.setBackgroundDrawable(getResources().getDrawable(R.drawable.reg_checked));
                    reg_pwef.setTextColor(Color.BLACK);
                    //체크되어있는지 확인하는애 필요
                }
            }
        }
    }

    //비밀번호 재확인 에러처리
    private void prtext_check(String pw, String pr) {
        if (pr.isEmpty()) {
            if (prfocus != 0) {
                reg_pref.setTextColor(Color.RED);
                reg_pref.setText("비밀번호 재확인을 입력해주세요");
                reg_prcheck.setBackgroundDrawable(getResources().getDrawable(R.drawable.reg_error));
            }
            prfocus++;
        } else {
            if (pw.equals(pr)) {
                reg_pref.setText("비밀번호가 일치합니다.");
                reg_pref.setTextColor(Color.BLACK);
                reg_prcheck.setBackgroundDrawable(getResources().getDrawable(R.drawable.reg_checked));
            } else {
                reg_pref.setTextColor(Color.RED);
                reg_pref.setText("비밀번호가 일치하지 않습니다 다시 입력해 주세요");
                reg_prcheck.setBackgroundDrawable(getResources().getDrawable(R.drawable.reg_error));
            }
        }
    }

    //이름 에러처리
    private void sntext_check(String name) {
        if (name.isEmpty()) {
            if (snfocus != 0) {
                reg_snef.setTextColor(Color.RED);
                reg_snef.setText("이름을 입력해주세요");
                reg_sncheck.setBackgroundDrawable(getResources().getDrawable(R.drawable.reg_error));
            }
            snfocus++;
        } else {
            //나중에 예외처리가 필요할경우 여기서
            reg_snef.setText("");
            reg_sncheck.setBackgroundDrawable(getResources().getDrawable(R.drawable.reg_checked));
        }
    }

    //학번 에러처리
    private void sitext_check(String uid) {
        if (uid.isEmpty()) {
            if (sifocus != 0) {
                reg_sief.setTextColor(Color.RED);
                reg_sief.setText("학번을 입력해주세요");
                reg_sicheck.setBackgroundDrawable(getResources().getDrawable(R.drawable.reg_error));
            }
            sifocus++;
        } else {
            int digit_num = 0;
            if (uid.length() > 17) {
                reg_sief.setTextColor(Color.RED);
                reg_sief.setText("16자이하의 학번을 입력해주세요");
                reg_sicheck.setBackgroundDrawable(getResources().getDrawable(R.drawable.reg_error));
            } else {
                for (int i = 0; i < uid.length(); i++) {
                    if (Character.isDigit(uid.charAt(i))) digit_num++;
                }
                if (digit_num != uid.length()) {
                    reg_sief.setTextColor(Color.RED);
                    reg_sief.setText("학번은 숫자만 입력해주세요");
                    reg_sicheck.setBackgroundDrawable(getResources().getDrawable(R.drawable.reg_error));

                } else {
                    if (check_internetstate()) {
                        JSONObject check = new JSONObject();
                        try {
                            check.put("uid", uid);

                            SSLTask ssltask = new SSLTask("https://166.104.245.43/precheck.php", check, this);
                            JSONObject rescheckid = ssltask.execute().get(2, TimeUnit.SECONDS);

                            if (rescheckid.get("check").equals("S")) {
                                reg_sief.setText("사용 할 수 있는 학번입니다.");
                                reg_sief.setTextColor(Color.BLACK);
                                reg_sicheck.setBackgroundDrawable(getResources().getDrawable(R.drawable.reg_checked));
                                //여기서 성공했다는 체크박스 보이게 해줘야함
                            } else {
                                reg_sief.setTextColor(Color.RED);
                                reg_sief.setText("중복된 학번 입니다.다시 확인 하여주세요");
                                reg_sicheck.setBackgroundDrawable(getResources().getDrawable(R.drawable.reg_error));
                            }
                        } catch (JSONException | ExecutionException | InterruptedException e) {
                            e.printStackTrace();
                        } catch (TimeoutException e) {
                            reg_sief.setTextColor(Color.RED);
                            reg_sief.setText("서버와의 연결이 원활 하지 않습니다.");
                            reg_sicheck.setBackgroundDrawable(getResources().getDrawable(R.drawable.reg_error));
                        }
                    } else {
                        reg_sief.setTextColor(Color.RED);
                        reg_sief.setText("인터넷이 연결되어있지 않습니다");
                        reg_sicheck.setBackgroundDrawable(getResources().getDrawable(R.drawable.reg_error));
                    }
                }
            }
        }
    }

    //이메일 에러처리
    private void setext_check(String email) {
        if (email.isEmpty()) {
            if (sefocus != 0) {
                reg_seef.setTextColor(Color.RED);
                reg_seef.setText("이메일을 입력해주세요");
                reg_secheck.setBackgroundDrawable(getResources().getDrawable(R.drawable.reg_error));
            }
            sefocus++;
        } else {
            int digit_num = 0, char_num = 0;
            if (email.length() > 26) {
                reg_seef.setTextColor(Color.RED);
                reg_seef.setText("25자이하의 이메일주소를 입력해주세요");
                reg_secheck.setBackgroundDrawable(getResources().getDrawable(R.drawable.reg_error));
            } else {
                for (int i = 0; i < email.length(); i++) {
                    if (Character.isDigit(email.charAt(i))) digit_num++;
                    if (Character.isLetter(email.charAt(i))) char_num++;
                }
                if (digit_num + char_num != email.length()) {
                    reg_seef.setTextColor(Color.RED);
                    reg_seef.setText("이메일은 영어와 숫자만 입력가능합니다.");
                    reg_secheck.setBackgroundDrawable(getResources().getDrawable(R.drawable.reg_error));

                } else {
                    if (check_internetstate()) {
                        JSONObject check = new JSONObject();
                        try {
                            check.put("email", email + "@hanyang.ac.kr");

                            SSLTask ssltask = new SSLTask("https://166.104.245.43/precheck.php", check, this);
                            JSONObject rescheckid = ssltask.execute().get(2, TimeUnit.SECONDS);

                            if (rescheckid.get("check").equals("S")) {
                                reg_seef.setText("사용 할 수 있는 이메일입니다.");
                                reg_seef.setTextColor(Color.BLACK);
                                reg_secheck.setBackgroundDrawable(getResources().getDrawable(R.drawable.reg_checked));

                            } else {
                                reg_seef.setTextColor(Color.RED);
                                reg_seef.setText("중복된 이메일 입니다.다른 이메일을 입력해주세요");
                                reg_secheck.setBackgroundDrawable(getResources().getDrawable(R.drawable.reg_error));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (TimeoutException e) {
                            reg_seef.setTextColor(Color.RED);
                            reg_seef.setText("서버와의 통신이 원활하지 않습니다.");
                            reg_secheck.setBackgroundDrawable(getResources().getDrawable(R.drawable.reg_error));
                        }
                    } else {
                        reg_seef.setTextColor(Color.RED);
                        reg_seef.setText("인터넷이 연결되어있지 않습니다");
                        reg_secheck.setBackgroundDrawable(getResources().getDrawable(R.drawable.reg_error));
                    }
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(reg_lay1.getWindowToken(), 0);

        if (v.getId() == R.id.Reg_button) {
            //회원가입 전처리 절차로 이동
            Before_Reg_procedure();
        }
        else if(v.getId() == R.id.reg_backbutton){
            Intent i = new Intent(this,LoginActivity.class);
            startActivity(i);
        }
    }

    //회원가입 전처리 절차
    private void Before_Reg_procedure() {
        //빈칸이 없는지 확인
        if (all_clear()) {

            WifiManager mng = (WifiManager) getSystemService(WIFI_SERVICE);
            WifiInfo info = mng.getConnectionInfo();

            //통신을 위해 필요한 데이터들을 가져온다
            String ID = reg_idtf.getText().toString();
            String PW = reg_pwtf.getText().toString();
            String SI = reg_sitf.getText().toString();
            String SN = reg_sntf.getText().toString();
            String SE = reg_setf.getText().toString();
                    //+ "@hanyang.ac.kr";
            String MAC = info.getMacAddress();

            //3G 또는 WiFi 에 연결되어 있을 경우
            if (check_internetstate()) {
                //Asynctask로 로그인
                try {
                    JSONObject jsonregister = new JSONObject();
                    jsonregister.put("id", ID);
                    jsonregister.put("pw", PW);
                    jsonregister.put("uid", SI);
                    jsonregister.put("name", SN);
                    jsonregister.put("email", SE);
                    jsonregister.put("mac_addr", MAC);

                    if (reg_ptb.isChecked()) {
                        jsonregister.put("type", "P");
                    } else {
                        jsonregister.put("type", "S");
                    }

                    SSLTask sslTask = new SSLTask("https://166.104.245.43/register.php", jsonregister,this);
                    JSONObject res = sslTask.execute().get();

                    //<!>이거 나중에 한양메일 지워야함
                    res.put("e-mail",SE + "@hanyang.ac.kr");
                    After_Reg_procedure(res);

                } catch (JSONException | ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                reg_letf.setText("인터넷이 연결되어있지 않습니다.");
                reg_rbt.setText("에러");
                reg_rbt.setBackgroundColor(Color.RED);
            }
        } else {
            reg_letf.setText("빈칸 혹은 올바르지 않은 입력란이 존재합니다");
            reg_letf.setTextColor(Color.RED);
            reg_rbt.setText("에러");
            reg_rbt.setBackgroundColor(Color.RED);
        }
    }

    //모든칸이 입력되었는지 확인하는 함수
    private boolean all_clear() {
        return (!(reg_idtf.getText().toString().isEmpty() ||
                reg_idcheck.getVisibility() == View.INVISIBLE ||
                reg_pwtf.getText().toString().isEmpty() ||
                reg_pwcheck.getVisibility() == View.INVISIBLE ||
                reg_prtf.getText().toString().isEmpty() ||
                reg_prcheck.getVisibility() == View.INVISIBLE ||
                reg_sntf.getText().toString().isEmpty() ||
                reg_sncheck.getVisibility() == View.INVISIBLE ||
                reg_sitf.getText().toString().isEmpty() ||
                reg_sicheck.getVisibility() == View.INVISIBLE ||
                reg_setf.getText().toString().isEmpty() ||
                reg_secheck.getVisibility() == View.INVISIBLE));
    }

    //인터넷 연결 상태를 확인하는 함수
    public boolean check_internetstate() {
        ConnectivityManager cManager;
        NetworkInfo mobile;
        NetworkInfo wifi;

        cManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        mobile = cManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        wifi = cManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        return mobile.isConnected() || wifi.isConnected();
    }

    private void After_Reg_procedure(JSONObject res) {
        try {
            //회원가입의 결과창
            if (res.getString("register").equals("S")) {
                reg_letf.setText("");
                reg_rbt.setText("메 일");
                reg_rbt.setBackgroundColor(Color.rgb(158, 186, 64));
                Intent i = new Intent(this, Mailactivity.class);
                i.putExtra("e_mail",res.getString("e-mail"));

                startActivity(i);
                finish();
            } else {
                //실패시
                reg_letf.setText("회원가입에 실패하였습니다");
                reg_letf.setTextColor(Color.RED);
                reg_rbt.setText("에러");
                reg_rbt.setBackgroundColor(Color.RED);
            }
        } catch (NullPointerException e) {
            Log.d("에러에러", "서버가 반응이 없졍");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onReceived(JSONObject jsonData) {

    }

    @Override
    public void onCanceled() {

    }
}