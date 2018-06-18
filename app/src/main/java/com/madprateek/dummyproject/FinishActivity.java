package com.madprateek.dummyproject;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

public class FinishActivity extends AppCompatActivity {

    CountDownTimer countDownTimer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish);

        showAlert();

    }

    public void showAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Uploading Status");
        builder.setMessage("Data stored Successfully, will be sent once internet available");
       // builder.show();
        final AlertDialog alertDialog = builder.show();
        alertDialog.show();


        countDownTimer = new CountDownTimer(3000, 1000) {

            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
                alertDialog.dismiss();
                Intent intent = new Intent(FinishActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }

        }.start();
    }
}
