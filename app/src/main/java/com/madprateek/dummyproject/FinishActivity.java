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
        builder.setMessage("Your Data is saved in local Databse and uploaded Soon");
        builder.show();

        countDownTimer = new CountDownTimer(2000, 1000) {

            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
                Intent intent = new Intent(FinishActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }

        }.start();
    }
}
