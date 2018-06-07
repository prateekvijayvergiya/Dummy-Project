package com.madprateek.dummyproject;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;

import com.madprateek.dummyproject.HelperClasses.DatabaseHelper;
import com.madprateek.dummyproject.ModelClasses.BaselineModel;

import java.util.ArrayList;

public class SubmissionActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private TextView mSubmitted,mPending;
    DatabaseHelper db = new DatabaseHelper(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submission);

        mToolbar = (Toolbar) findViewById(R.id.submissionAppBar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Soochana");

        mPending = (TextView) findViewById(R.id.pendingText);

        ArrayList<BaselineModel> allAttachments = (ArrayList) db.getAllBaseline();
        Log.v("TAG","Value is : " + allAttachments.size());
        int count = allAttachments.size();
        mPending.setText(String.valueOf(count));

    }
}
