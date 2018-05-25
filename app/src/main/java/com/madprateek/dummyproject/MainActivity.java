package com.madprateek.dummyproject;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class MainActivity extends AppCompatActivity {

    private Spinner mNameSpinner;
    private Toolbar mToolbar;
    private Button mPhotoBtn,mVideoBtn,mSubmitBtn;
    private int REQUEST_CAMERA = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.mainAppBar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Soochana");

        mNameSpinner = (Spinner) findViewById(R.id.nameSpiner);
        mNameSpinner = initSpinner(mNameSpinner, R.array.nameArray);
        mPhotoBtn = (Button) findViewById(R.id.photoClickBtn);
        mVideoBtn = (Button) findViewById(R.id.videoClickBtn);
        mSubmitBtn = (Button) findViewById(R.id.submitBtn);


        //for clicking of Photo button
        mPhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CharSequence options[] = new CharSequence[]{"Choose from Gallery","Camera"};
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Select Option");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int position) {

                        if (position == 0){


                        }else if (position == 1){

                            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)
                                    != PackageManager.PERMISSION_GRANTED) {
                                // Permission is not granted
                                if (ContextCompat.checkSelfPermission(MainActivity.this,
                                        Manifest.permission.READ_CONTACTS)
                                        != PackageManager.PERMISSION_GRANTED) {

                                    // Permission is not granted
                                    // Should we show an explanation?
                                    if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                                            Manifest.permission.READ_CONTACTS)) {
                                        // Show an explanation to the user *asynchronously* -- don't block
                                        // this thread waiting for the user's response! After the user
                                        // sees the explanation, try again to request the permission.
                                    } else {
                                        // No explanation needed; request the permission
                                        ActivityCompat.requestPermissions(MainActivity.this,
                                                new String[]{Manifest.permission.READ_CONTACTS},
                                                REQUEST_CAMERA);

                                        // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                                        // app-defined int constant. The callback method gets the
                                        // result of the request.
                                    }
                                } else {
                                    // Permission has already been granted
                                }
                            }

                        }
                    }
                });
                builder.show();

            }
        });


        //for clicking of Video Button
        mVideoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CharSequence options[] = new CharSequence[]{"Choose from Gallery","Camera"};
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Select Option");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int position) {

                        if (position == 0){


                        }else if (position == 1){


                        }
                    }
                });
                builder.show();

            }
        });
    }

    public Spinner initSpinner(Spinner s, int content_array) {

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,content_array,R.layout.spinner_style);
        adapter.setDropDownViewResource(R.layout.spinner_style);
        s.setAdapter(adapter);
        return s;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 100: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }
}
