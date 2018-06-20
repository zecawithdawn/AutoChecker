package com.example.kyh.real.LoginRegister;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.kyh.real.R;

public class Mailactivity extends Activity implements View.OnClickListener {

    TextView mail_text;
    Button mail_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mail);

        mail_text = (TextView) findViewById(R.id.mail_text);
        mail_button = (Button) findViewById(R.id.mail_button);

        mail_text.setText(getIntent().getStringExtra("e-mail"));
        mail_button.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.mail_button) {
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
            finish();
        }
    }
}
