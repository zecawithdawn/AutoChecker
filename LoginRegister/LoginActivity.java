package com.example.kyh.real.LoginRegister;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.kyh.real.Database.DbAdapter;
import com.example.kyh.real.Library.SSLTask;
import com.example.kyh.real.Library.TaskListener;
import com.example.kyh.real.P_F.P_Total_MainActivity;
import com.example.kyh.real.R;
import com.example.kyh.real.S_F.S_Total_MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

//
public class LoginActivity extends Activity implements View.OnClickListener, TaskListener {

    //xml 변수들 생성
    ProgressBar log_pgb;
    EditText log_idtf;
    EditText log_pwtf;
    TextView log_letf;
    Button log_loginbt;

    TextView log_regbt;
    TextView log_findidbt;
    TextView log_findpwbt;

    CheckBox log_iscb;
    CheckBox log_autocb;

    RadioButton log_prb;
    RadioButton log_srb;
    RadioGroup log_rbg;
    RelativeLayout log_rl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //여기에 액티비티에 만들어 질때 실행되는 함수
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //view id를 셋팅해줌
        set_viewid();

        //아이디찾기 및 회원가입 글자처리
        set_designtext();

        //자동로그인에 실패해서 들어온 경우 에러메세지 셋팅
        set_autologinerror();

        //아이디 저장이 체크되있던 경우 아이디 및 라디오버튼 체크
        id_save();

        //클릭이벤트 설정
        set_clickeventListener();

    }

    //이벤트 설정
    private void set_clickeventListener() {
        log_loginbt.setOnClickListener(this);
        log_regbt.setOnClickListener(this);
        log_findidbt.setOnClickListener(this);
        log_findpwbt.setOnClickListener(this);
        log_rl.setOnClickListener(this);
    }

    //viewid를 가져온다
    private void set_viewid() {
        log_pgb = (ProgressBar) findViewById(R.id.login_pb);
        log_idtf = (EditText) findViewById(R.id.log_idtf);
        log_pwtf = (EditText) findViewById(R.id.log_pwtf);
        log_letf = (TextView) findViewById(R.id.log_letf);
        log_loginbt = (Button) findViewById(R.id.log_loginbt);
        log_regbt = (TextView) findViewById(R.id.log_registerbt);
        log_findidbt = (TextView) findViewById(R.id.login_findidbt);
        log_findpwbt = (TextView) findViewById(R.id.login_findpwbt);

        log_iscb = (CheckBox) findViewById(R.id.log_iscb);
        log_autocb = (CheckBox) findViewById(R.id.log_alcb);
        log_prb = (RadioButton) findViewById(R.id.log_prb);
        log_srb = (RadioButton) findViewById(R.id.log_srb);
        log_rl = (RelativeLayout) findViewById(R.id.log_rl);
        log_rbg = (RadioGroup) findViewById(R.id.log_rbg);
    }

    //아이디찾기 및 회원가입 텍스트설정
    private void set_designtext() {
        final SpannableStringBuilder reg = new SpannableStringBuilder("아직 회원이 아니신가요?");
        final SpannableStringBuilder fii = new SpannableStringBuilder("아이디 /");
        final SpannableStringBuilder fip = new SpannableStringBuilder("비밀번호를");

        fii.setSpan(new StyleSpan(Typeface.BOLD_ITALIC), 0, 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        fii.setSpan(new UnderlineSpan(), 0, 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        fip.setSpan(new StyleSpan(Typeface.BOLD_ITALIC), 0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        fip.setSpan(new UnderlineSpan(), 0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        reg.setSpan(new StyleSpan(Typeface.BOLD_ITALIC), 3, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        reg.setSpan(new UnderlineSpan(), 3, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        log_findidbt.append(fii);
        log_findpwbt.append(fip);
        log_regbt.append(reg);
    }

    //자동로그인이 실패했을시의 에러메세지
    private void set_autologinerror() {
        Error_message(getIntent().getStringExtra("error"));
        sharedclear();
    }

    private void sharedclear() {
        SharedPreferences auto_login = getSharedPreferences("auto_login", MODE_PRIVATE);
        SharedPreferences.Editor edit = auto_login.edit();
        edit.clear();
        edit.apply();
    }

    //아이디 저장이 체크되어있을시 아이디 및 라디오 셋팅
    private void id_save() {
        SharedPreferences id_save = getSharedPreferences("id_save", MODE_PRIVATE);
        if (id_save.getBoolean("save", false)) {
            log_iscb.setChecked(true);
            log_idtf.setText(id_save.getString("user_id", ""));
            if (id_save.getString("user_type", "").equals("S")) {
                log_srb.setChecked(true);
            } else {
                log_prb.setChecked(true);
            }
        }
    }


    @Override
    public void onClick(View v) {
        //클릭이벤트시 키보드가 내려감
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(log_rl.getWindowToken(), 0);

        if (v.getId() == R.id.log_loginbt) {
            //회원가입을 클릭했을때
            log_loginbt.setVisibility(View.INVISIBLE);
            log_letf.setText("");
            Before_Login_procedure();
        } else if (v.getId() == R.id.log_registerbt) {
            Intent i = new Intent(LoginActivity.this, RegActivity.class);
            startActivity(i);
            //아이디 비번찾기 클릭했을때
        } else if (v.getId() == R.id.login_findidbt) {
            Intent i = new Intent(LoginActivity.this, FindidActivity.class);
            startActivity(i);
        } else if (v.getId() == R.id.login_findpwbt) {
            Intent i = new Intent(LoginActivity.this, FindpwActivity.class);
            startActivity(i);
        }
    }

    //로그인 전 처리과정
    private void Before_Login_procedure() {
        log_pgb.setVisibility(View.VISIBLE);
        log_loginbt.setVisibility(View.INVISIBLE);

        //예외처리에 필요한 ID,Pwd를 Edittext에서 받아온다
        String ID = log_idtf.getText().toString();
        String Pwd = log_pwtf.getText().toString();

        //개발용 함수
        //check_wifistate();
        //ID,PW에 대한 local 예외처리 && 인터넷 연결상태 확인 && 타입확인
        if (check_internetstate() && check_type(ID, Pwd) && text_handling(ID, Pwd)) {
            //Asynctask로 로그인
            //누른 라디오 버튼에 따라 보내는곳이 달라

            JSONObject requestjson = Make_loginrequestjson(ID, Pwd);


            //로그인을위해 데이터를 서버로 보냄
            SSLTask sslTask = new SSLTask("https://166.104.245.43/login.php", requestjson, this);

            //response를 받아온다
            sslTask.execute();
            //로그인 후처리를 위해 response에 데이터를 넣어준다

        }
    }

    //교수와 학생중 하나가 선택되었는지 확인
    private boolean check_type(String ID, String pwd) {
        if (log_prb.isChecked() || log_srb.isChecked()) {
            return true;
        } else {

            Error_message("학생과 교수중 하나를 선택하여 주세요");
            log_idtf.setText(ID);
            log_pwtf.setText(pwd);
            return false;
        }
    }

    //인터넷 상태 확인
    private boolean check_internetstate() {
        ConnectivityManager cManager;
        NetworkInfo mobile;
        NetworkInfo wifi;

        cManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        mobile = cManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        wifi = cManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (mobile.isConnected() || wifi.isConnected()) {
            return true;
        } else {
            Error_message("인터넷에 연결되어있지 않습니다.");
            return false;
        }
    }

    //ID와 Password에 대한 로컬예외처리
    private boolean text_handling(String id, String pw) {
        //ID,PW에 대한 local 예외처리
        //아이디 혹은 비밀번호가 입력되지 않았을때
        if (id.isEmpty() || pw.isEmpty()) {
            this.Error_message("아이디와 비밀번호를 입력해주세요");
            return false;
            //아이디에 대한 예외처리 영문+숫자,6~16까지 길이,인젝션 방어
        } else {
            int id_digit_num = 0, id_char_num = 0;
            int pw_digit_num = 0, pw_char_num = 0;
            if (id.length() < 6 || id.length() > 16) {
                this.Error_message("ID는 6글자이상 16글자 이하로 입력해주세요");
                return false;
            } else {

                for (int i = 0; i < id.length(); i++) {
                    if (Character.isDigit(id.charAt(i))) id_digit_num++;
                    if (Character.isLetter(id.charAt(i))) id_char_num++;
                }

                for (int i = 0; i < pw.length(); i++) {
                    if (Character.isDigit(pw.charAt(i))) pw_digit_num++;
                    if (Character.isLetter(pw.charAt(i))) pw_char_num++;
                }

                //id에 문자와 숫자가 섞여있는지 확인,특수문자가 있는경우 제거
                if (id_digit_num == 0 || id_char_num == 0) {
                    Error_message("ID는 영문+숫자의 조합만 가능합니다.");
                    return false;
                } else if (id_digit_num + id_char_num != id.length()) {
                    Error_message("특수문자는 입력이 불가능합니다.");
                    return false;
                } else if (pw_char_num + pw_digit_num != pw.length()) {
                    Error_message("비밀번호를 다시 입력해주세요");
                    return false;
                } else {
                    return true;
                }
            }
        }
    }

    //예외메세지 및 초기화용 함수
    private void Error_message(String set) {
        if (!(log_iscb.isChecked())) {
            SharedPreferences save_id = getSharedPreferences("id_save", MODE_PRIVATE);
            SharedPreferences.Editor edit1 = save_id.edit();
            edit1.clear();
            edit1.apply();
            log_rbg.clearCheck();
            log_idtf.setText("");
        }

        log_pgb.setVisibility(View.INVISIBLE);
        log_loginbt.setVisibility(View.VISIBLE);
        log_pwtf.setText("");
        log_letf.setText(set);
    }

    //로그인용 제이손 오브젝트를 만든다
    private JSONObject Make_loginrequestjson(String ID, String Pwd) {

        JSONObject request_json = new JSONObject();

        try {
            WifiManager mng = (WifiManager) getSystemService(WIFI_SERVICE);
            WifiInfo info = mng.getConnectionInfo();
            String MAC = info.getMacAddress();
            request_json.put("mac_addr", MAC);
            request_json.put("id", ID);
            request_json.put("pwd", Pwd);

            if (log_prb.isChecked() && !(log_srb.isChecked())) {
                request_json.put("type", "P");
            } else {
                request_json.put("type", "S");
            }

        } catch (JSONException e) {
            //들어올 이유가 없음
            Log.d("error", "로그인용 Json만들어주는 에러");
            Error_message("??");
        }

        return request_json;
    }

    //로그인 성공후 처리과정
    private void After_Login_Procedure(JSONObject responsejson) {

        try {
            String user_type = responsejson.getString("user_type");
            String user_id = responsejson.getString("user_id");
            String user_pwd = responsejson.getString("user_pwd");
            String mac_addr = responsejson.getString("mac_addr");
            String uid = responsejson.getString("uid");
            String db_ver = responsejson.getString("db_ver");

            //서버의 맥어드레스와 나의 맥어드레스 비교
            if (Compare_macaddr(mac_addr)) {
                //아이디저장만 눌린경우 아이디저장 절차 진행
                if (log_iscb.isChecked() && !(log_autocb.isChecked())) {
                    SharedPreferences save_id = getSharedPreferences("id_save", MODE_PRIVATE);
                    SharedPreferences.Editor edit = save_id.edit();

                    edit.putBoolean("save", true);
                    edit.putString("user_type", user_type);
                    edit.putString("user_id", user_id);

                    edit.apply();

                } else if (!(log_iscb.isChecked()) && !(log_autocb.isChecked())) {
                    //둘다 눌려있지 않은경우
                    SharedPreferences auto_login = getSharedPreferences("auto_login", MODE_PRIVATE);
                    SharedPreferences.Editor edit = auto_login.edit();
                    edit.clear();
                    edit.apply();
                    SharedPreferences id_save = getSharedPreferences("id_save", MODE_PRIVATE);
                    SharedPreferences.Editor edit2 = id_save.edit();
                    edit2.clear();
                    edit2.apply();

                } else {
                    //나머진 경우는 자동로그인이 체크된 경우
                    SharedPreferences auto_login = getSharedPreferences("auto_login", MODE_PRIVATE);
                    SharedPreferences.Editor edit = auto_login.edit();

                    edit.putBoolean("auto", true);
                    edit.putString("user_type", user_type);
                    edit.putString("user_id", user_id);
                    edit.putString("user_pwd", user_pwd);
                    edit.putString("user_uid", uid);
                    edit.apply();
                }

                //화면 이동을 위한 함수
                Next_activity(user_type, uid, db_ver, user_id, mac_addr);
                finish();

            } else {
                Error_message("서버저장된 기기의 정가 다릅니다.\n자신의 기기로 로그인해주세요");
            }
        } catch (JSONException e) {
            //JSON에서 아무것도 안받아오면 여기로 들어와
            Log.d("error", "로그인 후처리 과정중 JSON에러 ");
            Error_message("????");

        }
    }

    //서버의 mac과 나의 mac이 같은지 확인
    private boolean Compare_macaddr(String mac_addr) {
        WifiManager mng = (WifiManager) getSystemService(WIFI_SERVICE);
        WifiInfo info = mng.getConnectionInfo();
        return mac_addr.equals(info.getMacAddress());
    }

    //다음 화면으로 가기위한 함수
    private void Next_activity(String type, String UID, String db_ver, String id, String mac_addr) {
        Log.d("next", type);
        if (createDatabase(UID, db_ver, type)) {
            if (type.equals("S")) {
                Intent i = new Intent(LoginActivity.this, S_Total_MainActivity.class);
                i.putExtra("uid", UID);
                i.putExtra("db_ver", db_ver);
                i.putExtra("id", id);
                i.putExtra("mac_addr", mac_addr);
                i.putExtra("level", type);
                startActivity(i);
                finish();
            } else if (type.equals("P")) {
                Intent i = new Intent(LoginActivity.this, P_Total_MainActivity.class);
                i.putExtra("uid", UID);
                i.putExtra("db_ver", db_ver);
                i.putExtra("id", id);
                i.putExtra("mac_addr", mac_addr);
                i.putExtra("level", type);
                startActivity(i);
                finish();
            }
            log_pgb.setVisibility(View.INVISIBLE);
            log_loginbt.setVisibility(View.VISIBLE);
        } else {
            Log.d("DB 생성 에러", "");
        }
    }

    //db정보 받아오기
    private boolean createDatabase(String UID, String db_ver, String level) {
        try {
            SQLiteDatabase db;
            DbAdapter mHelper;
            mHelper = new DbAdapter(this, UID, Integer.parseInt(db_ver), level);
            Log.d("Server Database Version: ", db_ver);
            db = mHelper.getWritableDatabase();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void onReceived(JSONObject jsonData) {

        if (jsonData == null) {
            Error_message("서버가 응답하지 않습니다.");
        } else {
            String ID = log_idtf.getText().toString();
            String Pwd = log_pwtf.getText().toString();
            try {
                if (jsonData.getString("login").equals("S")) {
                    if (log_prb.isChecked() && !(log_srb.isChecked())) {
                        jsonData.put("user_type", "P");
                    } else {
                        jsonData.put("user_type", "S");
                    }
                    jsonData.put("user_id", ID);
                    jsonData.put("user_pwd", Pwd);

                    After_Login_Procedure(jsonData);

                } else {
                    Error_message("로그인에 실패하였습니다 아이디와 비밀번호를 확인해 주세요 ");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onCanceled() {

    }
}