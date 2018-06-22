package com.madprateek.dummyproject;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.madprateek.dummyproject.HelperClasses.DatabaseHelper;
import com.madprateek.dummyproject.HelperClasses.MySingleton;
import com.madprateek.dummyproject.ModelClasses.BaselineModel;

import java.util.ArrayList;

public class SubmissionActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    Context context;
    private TextView mSubmitted,mPending;
    DatabaseHelper db = new DatabaseHelper(this);

    public SubmissionActivity(){

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submission);

        mToolbar = (Toolbar) findViewById(R.id.submissionAppBar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Soochana");

        mPending = (TextView) findViewById(R.id.pendingText);
        mSubmitted = (TextView) findViewById(R.id.submittedText);
        Bundle bundle = getIntent().getExtras();
        String id = bundle.getString("deviceId");

        ArrayList<BaselineModel> allAttachments = (ArrayList) db.getAllBaseline();
        Log.v("TAG","Value is : " + allAttachments.size());
        int count = allAttachments.size();
        mPending.setText(String.valueOf(count));

        String url = "http://portal.jaipurrugsco.com/jrapi/public/soochana/" + id + "/submitted";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                mSubmitted.setText(response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        MySingleton.getInstance(getApplicationContext()).addTorequestque(stringRequest);
    }
}
