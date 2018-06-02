package com.madprateek.dummyproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SignUpActivity extends AppCompatActivity {

    private EditText mSignUpEmail, mSignUpPass;
    private TextView mAcctext;
    private Button mSignUpBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mSignUpEmail = (EditText) findViewById(R.id.signupEmail);
        mSignUpPass = (EditText) findViewById(R.id.signUpPass);
        mAcctext = (TextView) findViewById(R.id.accText);
        mSignUpBtn = (Button) findViewById(R.id.signUpBtn);


        mSignUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        mAcctext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(SignUpActivity.this,LoginActivity.class));
                finish();
            }
        });
    }
}
