package com.madprateek.dummyproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity {

    private EditText mLoginEmail, mLoginPass;
    private Button mLoginBtn;
    private TextView mAccText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mLoginEmail = (EditText) findViewById(R.id.loginEmail);
        mLoginPass = (EditText) findViewById(R.id.loginPass);
        mLoginBtn = (Button) findViewById(R.id.loginBtn);
        mAccText = (TextView) findViewById(R.id.switchtext);

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mAccText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(LoginActivity.this,SignUpActivity.class));
                finish();
            }
        });
    }
}
