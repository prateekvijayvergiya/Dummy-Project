package com.madprateek.dummyproject;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private Spinner mNameSpinner;
    private Toolbar mToolbar;
    private Button mPhotoBtn,mVideoBtn,mSubmitBtn;
    private int REQUEST_CAMERA = 100;
    private int REQUEST_STORAGE = 200;
    private static Boolean REQUEST_STATUS = false;
    private String CAPTURE_CODE;
    private int REQUEST_IMAGE_PICK = 10;
    private int REQUEST_VIDEO_PICK = 20;
    private int REQUEST_IMAGE_CAPTURE = 15;
    private int REQUEST_VIDEO_CAPTURE = 25;


    private ImageView mImageShow;
    private VideoView mVideoShow;
    private String mCurrentPhotoPath,mCurrentVideoPath,uploadTimeStamp;
    private File image,video;
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

                CAPTURE_CODE = "PHOTO";
                CharSequence options[] = new CharSequence[]{"Choose from Gallery","Camera"};
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Select Option");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int position) {

                        if (position == 0){

                            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                    != PackageManager.PERMISSION_GRANTED) {
                                // Permission is not granted
                                ActivityCompat.requestPermissions(MainActivity.this, new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, REQUEST_STORAGE);
                            }else {

                                chooseImage();
                            }

                        }else if (position == 1){

                            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)
                                    != PackageManager.PERMISSION_GRANTED) {
                                // Permission is not granted
                                ActivityCompat.requestPermissions(MainActivity.this, new String[]{"android.permission.CAMERA"}, REQUEST_CAMERA);

                            }else{

                                capturePhoto();
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

                CAPTURE_CODE = "VIDEO";
                CharSequence options[] = new CharSequence[]{"Choose from Gallery","Camera"};
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Select Option");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int position) {

                        if (position == 0){

                            if (!REQUEST_STATUS){
                                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                        != PackageManager.PERMISSION_GRANTED) {
                                    // Permission is not granted
                                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, REQUEST_STORAGE);
                                }else {
                                    chooseVideo();
                                }
                            }else {
                                chooseVideo();
                            }


                        }else if (position == 1){

                            if (!REQUEST_STATUS){
                                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)
                                        != PackageManager.PERMISSION_GRANTED) {
                                    // Permission is not granted
                                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{"android.permission.CAMERA"}, REQUEST_CAMERA);

                                }else {
                                    captureVideo();
                                }

                            }
                            else {

                                captureVideo();
                            }
                        }
                    }
                });
                builder.show();

            }
        });
    }

    //For initialise Spinner
    public Spinner initSpinner(Spinner s, int content_array) {

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,content_array,R.layout.spinner_style);
        adapter.setDropDownViewResource(R.layout.spinner_style);
        s.setAdapter(adapter);
        return s;
    }


    //Request runtime permission to users
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode){

            //Gallery
            case 200:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){


                    REQUEST_STATUS = true;//for setting camera feature true for permission
                    if (CAPTURE_CODE == "PHOTO"){
                        chooseImage();

                    }else {

                        chooseVideo();
                    }
                }
                break;
            //Camera
            case 100:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                    REQUEST_STATUS = true;//for setting camera feature true for permission
                    if (CAPTURE_CODE == "PHOTO"){
                        capturePhoto();

                    }else {

                        captureVideo();
                    }
                }

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED){

                    Toast.makeText(this, "You will not be able to take pictures from Camera", Toast.LENGTH_LONG).show();
                }
                break;

        }
    }

    //for capturing photo from camera
    private void capturePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(MainActivity.this,
                        "com.madprateek.dummyproject.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    //for capturing video from camera
    private void captureVideo() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createVideoFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(MainActivity.this,
                        "com.madprateek.dummyproject.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_VIDEO_CAPTURE);
            }
        }
    }

    //for choosing image from gallery
    private void chooseImage() {

        if (CAPTURE_CODE == "PHOTO"){
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent,
                    "Select Picture"), REQUEST_IMAGE_PICK);
        }
    }

    //for choosing video from gallery
    private void chooseVideo() {

        if (CAPTURE_CODE == "VIDEO"){
            Intent intent = new Intent();
            intent.setType("video/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent,
                    "Select Picture"), REQUEST_VIDEO_PICK);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

       switch (requestCode){

           case 10:
               if(resultCode == RESULT_OK)
               {
                   Uri uri = data.getData();
                   mImageShow = (ImageView)findViewById(R.id.photoView);
                   mImageShow.setVisibility(View.VISIBLE);
                   mImageShow.setImageURI(uri);
               }
               break;

           case 15:
               if (resultCode == RESULT_OK){
                   galleryAddPic();
                   Log.v("TAG","Gallery saved");
                   image = new File(mCurrentPhotoPath);
                   Uri uri = Uri.fromFile(image);
                   mImageShow = (ImageView)findViewById(R.id.photoView);
                   mImageShow.setVisibility(View.VISIBLE);
                   mImageShow.setImageURI(uri);
                   //filepath = imageUri.toString();
                   Log.v("Tag","Image Done");
               }
               break;

           case 20:

              if (resultCode == RESULT_OK){
                  Uri uri = data.getData();
                  mVideoShow = (VideoView) findViewById(R.id.videoView);
                  mVideoShow.setVisibility(View.VISIBLE);
                  mVideoShow.setVideoURI(uri);
              }

           break;

           case 25:

               if (resultCode == RESULT_OK){
                   galleryAddVideo();
                   Log.v("TAG","Gallery saved");
                   video = new File(mCurrentVideoPath);
                   Uri videoUri = Uri.fromFile(video);
                   mVideoShow = (VideoView) findViewById(R.id.videoView);
                   mVideoShow.setVisibility(View.VISIBLE);
                   mVideoShow.setVideoURI(videoUri);
                   //filepath = imageUri.toString();
                   Log.v("Tag","Image Done");
               }


       }
    }


    //for adding pic in gallery
    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }


    //For adding video in gallery
    private void galleryAddVideo() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentVideoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    //for creating image file in gallery
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        uploadTimeStamp = timeStamp;
        String imageFileName = "JPG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }


    //For creating Video file in gallery
    private File createVideoFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        uploadTimeStamp = timeStamp;
        String VideoFileName = "mp4" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File video = File.createTempFile(
                VideoFileName,  /* prefix */
                ".mp4",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentVideoPath = video.getAbsolutePath();
        Log.v("TAG","From createFile" + mCurrentVideoPath);
        return video;
    }
}
