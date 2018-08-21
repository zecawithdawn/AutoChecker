package com.example.kyh.real.AlarmAlert;

import android.app.Activity;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.os.Vibrator;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.example.kyh.real.Library.Alarm;
import com.example.kyh.real.R;


/**
 * Created by park on 2015. 4. 5..
 */
public class AlarmAlertActivity extends Activity implements View.OnClickListener {
    private Alarm alarm;

    private Button OkBtn;
    private Vibrator vibrator;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        setContentView(R.layout.activity_alarm_alert);

        Bundle bundle = this.getIntent().getExtras();
        alarm = (Alarm) bundle.getSerializable("alarm");

        OkBtn = (Button) findViewById(R.id.alarmOkBtn);
        OkBtn.setOnClickListener(this);

        startAlarm();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        try {
            if (vibrator != null)
                vibrator.cancel();
        } catch (Exception e) {

        }
        try {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.release();
            }
        } catch (Exception e) {

        }
        super.onDestroy();
    }

    private void startAlarm() {
        if (alarm != null) {
            if (alarm.getVibrate()) {
                vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                long[] pattern = {1000, 200, 200, 200};
                vibrator.vibrate(pattern, 0);
            }
            /*if (!alarm.getRingtonPath().equals("null")) {
                mediaPlayer = new MediaPlayer();
               try {
                    mediaPlayer.setVolume(1.0f, 1.0f);
                    mediaPlayer.setDataSource(this,
                            Uri.parse(alarm.getRingtonPath()));
                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                    mediaPlayer.setLooping(true);
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                } catch (Exception e) {
                    mediaPlayer.release();
                }
            }*/
        }
    }

    @Override
    public void onClick(View v) {
        this.finish();
    }
}
