package com.example.kyh.real.LoginRegister;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.example.kyh.real.Library.SSLTask;
import com.example.kyh.real.Library.TaskListener;
import com.example.kyh.real.P_F.P_Total_MainActivity;
import com.example.kyh.real.R;
import com.example.kyh.real.S_F.S_Total_MainActivity;

import org.json.JSONException;
import org.json.JSONObject;


public class LoadingActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        // Thread will sleep for 2 seconds
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                SharedPreferences auto_login = getSharedPreferences("auto_login", MODE_PRIVATE);

                // 개발용) 쉐어드프리퍼런스 초기화
                //sharedclear();

                // 개발용) 쉐어드 프리퍼런스 변수 확인
                //sharedvalue();

                WifiManager mng = (WifiManager) getSystemService(WIFI_SERVICE);
                WifiInfo info = mng.getConnectionInfo();
                String MAC = info.getMacAddress();

                Intent i = null;
                JSONObject loginreq = new JSONObject();

                try {
                    loginreq.put("mac_addr", MAC);
                    loginreq.put("type", auto_login.getString("user_type", ""));
                    loginreq.put("id", auto_login.getString("user_id", "id"));
                    loginreq.put("pwd", auto_login.getString("user_pwd", "pwd"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                TaskListener tl = new TaskListener() {
                    @Override
                    public void onReceived(JSONObject jsonData) {
                        SharedPreferences auto_login = getSharedPreferences("auto_login", MODE_PRIVATE);
                        Intent i = null;

                        WifiManager mng = (WifiManager) getSystemService(WIFI_SERVICE);
                        WifiInfo info = mng.getConnectionInfo();
                        String MAC = info.getMacAddress();

                        if (jsonData == null) {
                            i = new Intent(LoadingActivity.this, LoginActivity.class);
                            i.putExtra("error", "서버와의 통신이 원활하지 않습니다.");
                        } else {
                            //성공 했을때
                            try {
                                if (jsonData.getString("login").equals("S")) {
                                    if (auto_login.getString("user_type", "").equals("P")) {
                                        i = new Intent(LoadingActivity.this, P_Total_MainActivity.class);
                                        i.putExtra("db_ver", jsonData.getString("db_ver"));
                                        i.putExtra("id", auto_login.getString("user_id", "id"));
                                        i.putExtra("mac_addr", MAC);
                                        i.putExtra("level", auto_login.getString("user_type", ""));
                                        i.putExtra("uid", auto_login.getString("user_uid", ""));

                                    } else if (auto_login.getString("user_type", "").equals("S")) {
                                        i = new Intent(LoadingActivity.this, S_Total_MainActivity.class);
                                        i.putExtra("db_ver", jsonData.getString("db_ver"));
                                        i.putExtra("id", auto_login.getString("user_id", "id"));
                                        i.putExtra("mac_addr", MAC);
                                        i.putExtra("level", auto_login.getString("user_type", ""));
                                        i.putExtra("uid", auto_login.getString("user_uid", ""));
                                    }

                                    //실패 했을때
                                } else {
                                    i = new Intent(LoadingActivity.this, LoginActivity.class);
                                    i.putExtra("error", "자동로그인에 실패하였습니다");

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        startActivity(i);
                        //Remove activity
                        finish();
                    }

                    @Override
                    public void onCanceled() {

                    }
                };

                if (auto_login.getBoolean("auto", false)) {
                    SSLTask sslTask = new SSLTask("https://166.104.245.43/login.php", loginreq, tl);
                    sslTask.execute();
                } else {
                    //이건 자동로그인 상태 아닐때
                    i = new Intent(LoadingActivity.this, LoginActivity.class);

                    startActivity(i);
                    //Remove activity
                    finish();
                }

            }
        }, 2000);//이 숫자가 딜레이 얼마나 줄껀지

    }

    //개발용)쉐어드 변수확인
    private void sharedvalue() {
        //쉐어드 프리퍼런스 변수 확인
        SharedPreferences auto_login = getSharedPreferences("auto_login", MODE_PRIVATE);
        Log.d("sfa", String.valueOf(auto_login.getBoolean("auto", false)));
        Log.d("sft", auto_login.getString("user_type", ""));
        Log.d("sfi", auto_login.getString("user_id", ""));
        Log.d("sfp", auto_login.getString("user_pwd", ""));
        Log.d("sfd", auto_login.getString("user_db", ""));
    }

    //개발용)쉐어드 초기화
    private void sharedclear() {
        SharedPreferences auto_login = getSharedPreferences("auto_login", MODE_PRIVATE);
        SharedPreferences.Editor edit = auto_login.edit();
        edit.clear();
        edit.apply();
    }

}
