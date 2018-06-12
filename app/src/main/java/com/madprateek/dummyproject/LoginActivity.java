package com.madprateek.dummyproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.madprateek.dummyproject.HelperClasses.MySingleton;
import com.madprateek.dummyproject.HelperClasses.SessionManager;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private EditText mUsername, mLoginPass;
    private Button mLoginBtn;
    private TextView mAccText;
    SessionManager session;
    String loginUrl = "http://portal.jaipurrugsco.com/jrapi/public/soochana/validate";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mUsername = (EditText) findViewById(R.id.loginEmail);
        mLoginPass = (EditText) findViewById(R.id.loginPass);
        mLoginBtn = (Button) findViewById(R.id.loginBtn);
        mAccText = (TextView) findViewById(R.id.switchtext);
        session = new SessionManager(getApplicationContext());

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String username = mUsername.getText().toString();
                String pass = mLoginPass.getText().toString();
                loginUser(username,pass);
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

    private void loginUser(final String username, final String pass) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, loginUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.equals("success")){
                    Log.v("TAG","Response from server is : " + response);
                    session.createLoginSession(username);
                    Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                else {
                    Log.v("TAG","Response from server is : " + response);
                    Toast.makeText(LoginActivity.this, "login failed due to " + response, Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> details = new HashMap<>();
                details.put("username",username);
                details.put("password",pass);
                return details;
            }
        };

        MySingleton.getInstance(LoginActivity.this).addTorequestque(stringRequest);
    }

}
