package com.madprateek.dummyproject;

import android.Manifest;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.VideoView;

import com.madprateek.dummyproject.HelperClasses.AudioFunctions;
import com.madprateek.dummyproject.HelperClasses.DatabaseHelper;
import com.madprateek.dummyproject.HelperClasses.DeviceLocation;
import com.madprateek.dummyproject.HelperClasses.MyFTPClientFunctions;
import com.madprateek.dummyproject.HelperClasses.SessionManager;
import com.madprateek.dummyproject.ModelClasses.AttachmentModel;
import com.madprateek.dummyproject.ModelClasses.BaselineModel;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    String TAG = MainActivity.class.getSimpleName();
    private Spinner mNameSpinner;
    private Toolbar mToolbar;
    private Button mPhotoBtn, mVideoBtn, mSubmitBtn;
    private int REQUEST_CAMERA = 100;
    private int REQUEST_STORAGE = 200;
    private int REQUEST_AUDIO_RECORD = 300;
    private int REQUEST_LOCATION = 400;
    private int REQUEST_PHONE_STATE = 500;
    private static Boolean REQUEST_STATUS = false;
    private String CAPTURE_CODE;
    private int REQUEST_IMAGE_PICK = 10;
    private int REQUEST_VIDEO_PICK = 20;
    private int REQUEST_IMAGE_CAPTURE = 15;
    private int REQUEST_VIDEO_CAPTURE = 25;
    private int jobID = 1;
    private static final String host = "ftp.pixxel-fs2001.fingerprinti.com";
    private static final String username = "ftpfs2001";
    private static final String password = "u701aC/}9S";
    String serverId = "";
    MyFTPClientFunctions ftpclient = null;
    String server_url_baseline = "http://192.168.12.160/Baseline.php";
    String server_url_attachments = "http://192.168.12.160/attachments.php";

    private ImageView mImageShow;
    private VideoView mVideoShow;
    private EditText mPhotoTitleText, mVideoTitleText, mAudioTitleText, mMessageText;
    private String mCurrentPhotoPath, mCurrentVideoPath, uploadTimeStamp, spinnerContent;
    private File image, video;
    String mPhotoPath, mVideoPath;
    private DatabaseHelper db;
    String baseId, tempStatus = "0", mLocation = " ", mDeviceId, mOutputFile = " ";
    SessionManager session;
    DeviceLocation deviceLocation;
    AudioFunctions audioFunctions;
    Button mStartBtn, mStopBtn, mPlayBtn, mRecStopBtn;
    int mFlag = 0;
    //File image;
    Boolean imageStatus, videoStatus, uploadvideoStatus, uploadImageStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.mainAppBar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Soochana");


        mNameSpinner = (Spinner) findViewById(R.id.nameSpiner);
        mNameSpinner = initSpinner(mNameSpinner, R.array.nameArray);
        mPhotoTitleText = (EditText) findViewById(R.id.photoTitle);
        mVideoTitleText = (EditText) findViewById(R.id.videoTitle);
        mAudioTitleText = (EditText) findViewById(R.id.audioTitle);
        mMessageText = (EditText) findViewById(R.id.messageEditText);
        mPhotoBtn = (Button) findViewById(R.id.photoClickBtn);
        mVideoBtn = (Button) findViewById(R.id.videoClickBtn);
        mSubmitBtn = (Button) findViewById(R.id.submitBtn);
        db = new DatabaseHelper(this);
        ftpclient = new MyFTPClientFunctions();
        session = new SessionManager(getApplicationContext());
        audioFunctions = new AudioFunctions(getApplicationContext());
        deviceLocation = new DeviceLocation();

        session.checkLogin();
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{"android.permission.ACCESS_FINE_LOCATION"}, REQUEST_LOCATION);
        }else  mLocation = deviceLocation.getmLocation();

        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{"android.permission.READ_PHONE_STATE"}, REQUEST_PHONE_STATE);
        }else  mDeviceId = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);


        //Audio Buttons
        mStartBtn = (Button) findViewById(R.id.StartBtn);
        mStopBtn = (Button) findViewById(R.id.StopBtn);
        mPlayBtn = (Button) findViewById(R.id.playBtn);
        mRecStopBtn = (Button) findViewById(R.id.stopBtn);


        //For getting the TimeStamp
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        uploadTimeStamp = timeStamp;
        final JobInfo jobInfo = new JobInfo.Builder(jobID, new ComponentName(getApplicationContext(), MyJobService.class))
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY).build();
        final JobScheduler jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);

        //For selecting content of Spinner
        mNameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinnerContent = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                spinnerContent = parent.getSelectedItem().toString();
            }
        });


        //for clicking of Photo button
        mPhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CAPTURE_CODE = "PHOTO";
                CharSequence options[] = new CharSequence[]{"Choose from Gallery (गैलरी से चयन करो)", "Camera (कैमरा)"};
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Select Option (विकल्प चुनें)");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int position) {

                        if (position == 0) {

                            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                    != PackageManager.PERMISSION_GRANTED) {
                                // Permission is not granted
                                ActivityCompat.requestPermissions(MainActivity.this, new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, REQUEST_STORAGE);
                            } else {

                                chooseImage();
                            }

                        } else if (position == 1) {

                            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)
                                    != PackageManager.PERMISSION_GRANTED) {
                                // Permission is not granted
                                ActivityCompat.requestPermissions(MainActivity.this, new String[]{"android.permission.CAMERA"}, REQUEST_CAMERA);

                            } else {

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
                CharSequence options[] = new CharSequence[]{"Choose from Gallery (गैलरी से चयन करो)", "Camera (कैमरा)"};
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Select Option (विकल्प चुनें)");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int position) {

                        if (position == 0) {

                            if (!REQUEST_STATUS) {
                                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                        != PackageManager.PERMISSION_GRANTED) {
                                    // Permission is not granted
                                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, REQUEST_STORAGE);
                                } else {
                                    chooseVideo();
                                }
                            } else {
                                chooseVideo();
                            }


                        } else if (position == 1) {

                            if (!REQUEST_STATUS) {
                                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)
                                        != PackageManager.PERMISSION_GRANTED) {
                                    // Permission is not granted
                                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{"android.permission.CAMERA"}, REQUEST_CAMERA);

                                } else {
                                    captureVideo();
                                }

                            } else {

                                captureVideo();
                            }
                        }
                    }
                });
                builder.show();

            }
        });


        mStartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO)
                        != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{"android.permission.RECORD_AUDIO"}, REQUEST_AUDIO_RECORD);
                }else audioFunctions.startRec();
            }
        });

        mStopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                audioFunctions.stopRec();
            }
        });

        mPlayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                audioFunctions.playRec();
            }
        });

        mRecStopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                audioFunctions.stopPlay();
            }
        });

        //For submitting the form
        mSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                HashMap<String, String> user = session.getUserDetails();
                String name = user.get(SessionManager.KEY_NAME);
                String village = spinnerContent;
                String photoTitleText = mPhotoTitleText.getText().toString();
                String videoTitleText = mVideoTitleText.getText().toString();
                String audioTitleText = mAudioTitleText.getText().toString();
                String messageText = mMessageText.getText().toString();
                mOutputFile = audioFunctions.getOutputFile();
                Log.v("TAG","Audio path is : " + mOutputFile);


                if (!TextUtils.isEmpty(messageText)) {
                    messageText = messageText;
                } else {
                    messageText = "-";
                }

                storeBaseline(name,village, mLocation, messageText, mDeviceId);

                //for getting the empty text
                if (!TextUtils.isEmpty(photoTitleText)) {
                    String subject = photoTitleText;
                    String path = mPhotoPath;
                    String type = "photo";
                    String status = "0";
                    storeAttachment(baseId, serverId, subject, path, type, status);
                }
                if (!TextUtils.isEmpty(videoTitleText)) {
                    String subject = videoTitleText;
                    String path = mVideoPath;
                    String type = "video";
                    String status = "0";
                    storeAttachment(baseId, serverId, subject, path, type, status);
                }
                if (!TextUtils.isEmpty(audioTitleText)){
                    String subject = audioTitleText;
                    String path = mOutputFile;
                    String type = "audio";
                    String status = "0";
                    storeAttachment(baseId, serverId, subject, path, type, status);
                }

                //for setting video and photo path
               /* if (!TextUtils.isEmpty(mPhotoPath)) {
                    mPhotoPath = mPhotoPath;
                } else mPhotoPath = "";
                if (!TextUtils.isEmpty(mVideoPath)) {
                    mVideoPath = mVideoPath;
                } else mVideoPath = "";*/
//             uploadData();
              /*  Connection connection = new Connection();
                if (connection.isConnectingToInternet(getApplicationContext())) {
                    ArrayList<AttachmentModel> allAttachments = (ArrayList) db.getAllAttachments();
                    ArrayList<BaselineModel> allBaselines = (ArrayList) db.getAllBaseline();
                    new NetworkTask(allAttachments, allBaselines).execute();

                } else {

                    jobScheduler.schedule(jobInfo);
                    jobID++;
                    Toast.makeText(getApplicationContext(), "Please Check your Internet Connectivity", Toast.LENGTH_LONG).show();

                }

                Intent intent = new Intent(MainActivity.this,FinishActivity.class);
                startActivity(intent);*/


            }
        });

    }

    class NetworkTask extends AsyncTask<Void, Void, String> {
        private ArrayList<AttachmentModel> attachmentModels;
        private ArrayList<BaselineModel> baselineModels;

        public NetworkTask(ArrayList<AttachmentModel> attachmentModels, ArrayList<BaselineModel> baselineModels) {
            super();
            this.attachmentModels = attachmentModels;
            this.baselineModels = baselineModels;
        }

        @Override
        protected String doInBackground(Void... voids) {
            NetworkRequestHandler nrh = new NetworkRequestHandler(getApplicationContext(), attachmentModels, baselineModels);
            nrh.uploadAllData();
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d(TAG, "Starting Data Upload");
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d(TAG, "Data Upload Finished");
        }
    }


    private void storeAttachment(String id, String serverId, String subject, String path, String type, String status) {
        AttachmentModel attach = new AttachmentModel(id, serverId, subject, path, type, status);
        db.addAttachment(attach);
        Log.v("TAG - attachment", "Data inserted row created in attachment");
    }

    private void storeBaseline(String username, String spinnerContent, String location, String message, String deviceId) {
        BaselineModel base = new BaselineModel(username,spinnerContent,location,message,deviceId);
        long Id = db.addBaseline(base);
        //baseId = Long.toString(Id);
        baseId = String.valueOf(Id);
        Log.v("TAG - Baseline", "Data inserted row created in Baseline");
    }

    //For initialise Spinner
    public Spinner initSpinner(Spinner s, int content_array) {

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, content_array, R.layout.spinner_style);
        adapter.setDropDownViewResource(R.layout.spinner_style);
        s.setAdapter(adapter);
        return s;
    }


    //Request runtime permission to users
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {

            //Gallery
            case 200:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                    REQUEST_STATUS = true;//for setting camera feature true for permission
                    if (CAPTURE_CODE == "PHOTO") {
                        chooseImage();

                    } else {

                        chooseVideo();
                    }
                }
                break;
            //Camera
            case 100:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    REQUEST_STATUS = true;//for setting camera feature true for permission
                    if (CAPTURE_CODE == "PHOTO") {
                        capturePhoto();

                    } else {

                        captureVideo();
                    }
                }

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {

                    Toast.makeText(this, "You will not be able to take pictures from Camera", Toast.LENGTH_LONG).show();
                }
                break;

            //AUDIO
            case 300:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    audioFunctions.startRec();
                    mOutputFile = audioFunctions.getOutputFile();
                }else {
                    mOutputFile = audioFunctions.getOutputFile();
                    //Toast.makeText(this, "You require permission for capturing audio", Toast.LENGTH_SHORT).show();
                }
                break;

            //FOR LOCATION
            case 400:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    mLocation = deviceLocation.getmLocation();
                }else {
                    mLocation = deviceLocation.getmLocation();
                    //Toast.makeText(this, "You require permission for capturing location", Toast.LENGTH_SHORT).show();
                }break;

            //FOR device id
            case 500:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    mDeviceId = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
                }else{
                    mDeviceId = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
                    // Toast.makeText(this, "This app require device Id", Toast.LENGTH_SHORT).show();
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

        if (CAPTURE_CODE == "PHOTO") {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent,
                    "Select Picture"), REQUEST_IMAGE_PICK);
        }
    }

    //for choosing video from gallery
    private void chooseVideo() {

        if (CAPTURE_CODE == "VIDEO") {
            Intent intent = new Intent();
            intent.setType("video/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent,
                    "Select Picture"), REQUEST_VIDEO_PICK);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {

            case 10:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    mImageShow = (ImageView) findViewById(R.id.photoView);
                    mImageShow.setVisibility(View.VISIBLE);
                    mImageShow.setImageURI(uri);
                    mPhotoPath = uri.getPath();
                    Log.v("TAG", "path of image : " + mPhotoPath);
                }
                break;

            //Image capture
            case 15:
                if (resultCode == RESULT_OK) {
                    galleryAddPic();
                    Log.v("TAG", "Gallery saved");
                    image = new File(mCurrentPhotoPath);
                    Uri uri = Uri.fromFile(image);
                    mImageShow = (ImageView) findViewById(R.id.photoView);
                    mImageShow.setVisibility(View.VISIBLE);
                    mImageShow.setImageURI(uri);
                    //filepath = imageUri.toString();
                    Log.v("Tag", "Image Done");
                    mPhotoPath = uri.getPath();
                }
                break;

            case 20:

                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    mVideoShow = (VideoView) findViewById(R.id.videoView);
                    mVideoShow.setVisibility(View.VISIBLE);
                    mVideoShow.setVideoURI(uri);
                    MediaController mediaController = new MediaController(this);
                    mediaController.setAnchorView(mVideoShow);
                    mVideoShow.setMediaController(mediaController);
                    mVideoPath = uri.getPath();
                }

                break;

            //video capture
            case 25:

                if (resultCode == RESULT_OK) {
                    galleryAddVideo();
                    video = new File(mCurrentVideoPath);
                    Uri videoUri = Uri.fromFile(video);
                    mVideoShow = (VideoView) findViewById(R.id.videoView);
                    mVideoShow.setVisibility(View.VISIBLE);
                    mVideoShow.setVideoURI(videoUri);
                    MediaController mediaController = new MediaController(this);
                    mediaController.setAnchorView(mVideoShow);
                    mVideoShow.setMediaController(mediaController);
                    mVideoPath = videoUri.getPath();
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
        image = File.createTempFile(
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
        Log.v("TAG", "From createFile" + mCurrentVideoPath);
        return video;
    }


    /*  //For uploading final Data to server
      public void uploadData() {

          Connection connection = new Connection();
          if (connection.isConnectingToInternet(getApplicationContext())) {

              new Thread(new Runnable() {
                  @Override
                  public void run() {

                      List<AttachmentModel> putAttach = db.getAllAttachments();
                      Log.v("TAG", "display putAttach  " + putAttach.size());
                      List<BaselineModel> putBase = db.getAllBaseline();

                      if (putAttach != null) {
                          AttachmentModel attach;
                          BaselineModel base;
                          for (int i = 0; i < putAttach.size(); i++) {
                              attach = putAttach.get(i);
                              base = putBase.get(i);
                              Log.v("TAG", "value of base is" + base.toString());

                              if (!TextUtils.isEmpty(attach.getPhotoPath()) && !TextUtils.isEmpty(attach.getVideoPath())) {

                                  //imageStatus = uploadImage(attach.getPhotoPath());
                                  //videoStatus = uploadVideo(attach.getVideoPath());
                                  uploadBoth(attach.getPhotoPath(), attach.getVideoPath(), attach, base);
                              }
                              if (!TextUtils.isEmpty(attach.getPhotoPath()) && TextUtils.isEmpty(attach.getVideoPath())) {

                                  uploadImage(attach.getPhotoPath(), attach, base);
                                  //db.deleteAttachment(attach);
  //                               if (flag == 1){
  //                                    db.updateAttachmentPhotoStatus(attach);
  //                                    uploadDataBaseline();
  //                                    Log.v("TAG","UploadDataBaseline called");
  //                                    //uploadDataAttachment();
  //                                }

                              }
                              if (!TextUtils.isEmpty(attach.getVideoPath()) && TextUtils.isEmpty(attach.getPhotoPath())) {

                                  uploadVideo(attach.getVideoPath(), attach, base);
                              }

                          }
                      }
                  }
              }).start();
          } else {
              JobInfo jobInfo = new JobInfo.Builder(jobID++, new ComponentName(this, MyJobService.class))
                      .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY).build();
              JobScheduler jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
              jobScheduler.schedule(jobInfo);
              Toast.makeText(this, "Please Check your Internet Connectivity", Toast.LENGTH_LONG).show();
          }
      }


      //For uploading data on given server of attachments
      private void uploadDataAttachment(final AttachmentModel attach) {

          new Thread(new Runnable() {
              @Override
              public void run() {

                  //if (put != null){

                  //for (final AttachmentModel attach : put){

                  StringRequest stringRequest = new StringRequest(Request.Method.POST, server_url_attachments, new Response.Listener<String>() {
                      @Override
                      public void onResponse(String response) {

                          Toast.makeText(MainActivity.this, "Response :" + response, Toast.LENGTH_LONG).show();
                          Log.v("TAG", "upload on xampp of attachment");
                          Log.v("TAG", response);
                          db.deleteAttachment(attach);
                          Log.v("TAG", "Record deleted from attachment");
                      }
                  }, new Response.ErrorListener() {
                      @Override
                      public void onErrorResponse(VolleyError error) {

                          Toast.makeText(MainActivity.this, "some error found .....", Toast.LENGTH_SHORT).show();
                          error.printStackTrace();
                          Log.v("TAG", "upload on xampp unsuccessful");
                          Log.v("TAG", error.toString());

                      }
                  }) {

                      @Override
                      protected Map<String, String> getParams() throws AuthFailureError {

                          String baselineId = attach.getBaselineId();
                          String photoPath = attach.getPhotoPath();
                          String videoPath = attach.getVideoPath();
                          String photoStatus = attach.getPhotoStatus();
                          String videoStatus = attach.getVideoStatus();
                          String mimeType = attach.getMimeType();
                          Map<String, String> details = new HashMap<>();
                          details.put("baseline_id", baselineId);
                          details.put("photo_path", photoPath);
                          details.put("video_path", videoPath);
                          details.put("photo_status", photoStatus);
                          details.put("video_status", videoStatus);
                          details.put("mime_type", mimeType);
                          return details;
                      }
                  };

                  MySingleton.getInstance(MainActivity.this).addTorequestque(stringRequest);
                  //}
                  //}
              }
          }).start();
      }


      //For uploading data on given server of Baseline
      private void uploadDataBaseline(final BaselineModel base) {

          new Thread(new Runnable() {
              @Override
              public void run() {

                  // List<BaselineModel> put = db.getAllBaseline();
                  //if (put != null){

                  // for (final BaselineModel base : put){

                  StringRequest stringRequest = new StringRequest(Request.Method.POST, server_url_baseline, new Response.Listener<String>() {
                      @Override
                      public void onResponse(String response) {

                          Toast.makeText(MainActivity.this, "Response :" + response, Toast.LENGTH_LONG).show();
                          Log.v("TAG", "upload on xampp of baseline");
                          Log.v("TAG", response);
                          db.deleteBaseline(base);
                          Log.v("TAG", "Record deleted from Baseline");
                      }
                  }, new Response.ErrorListener() {
                      @Override
                      public void onErrorResponse(VolleyError error) {

                          Toast.makeText(MainActivity.this, "some error found .....", Toast.LENGTH_SHORT).show();
                          error.printStackTrace();
                          Log.v("TAG", "upload on xampp unsuccessful");
                          Log.v("TAG", error.toString());

                      }
                  }) {

                      @Override
                      protected Map<String, String> getParams() throws AuthFailureError {

                          String name = base.getName();
                          String photoTitle = base.getPhotoTitle();
                          String videoTitle = base.getVideoTitle();
                          String message = base.getMessage();
                          Map<String, String> details = new HashMap<>();
                          details.put("name", name);
                          details.put("photo_title", photoTitle);
                          details.put("video_title", videoTitle);
                          details.put("message", message);
                          return details;
                      }
                  };

                  MySingleton.getInstance(MainActivity.this).addTorequestque(stringRequest);
                  //}
                  // }
              }
          }).start();
      }

      private void uploadBoth(final String photoPath, final String videoPath, final AttachmentModel attach, final BaselineModel base) {
          new Thread(new Runnable() {
              @Override
              public void run() {

                  boolean status = false;
                  // host – your FTP address
                  // username & password – for your secured login
                  // 21 default gateway for FTP
                  status = ftpclient.ftpConnect(host, username, password, 21);
                  if (status) {
                      Log.d("TAG", "Connection Success");
                      uploadImageStatus = ftpclient.ftpUpload(photoPath, "/soochana/Images_" + uploadTimeStamp + ".jpg", "soochana", getApplicationContext());
                      if (uploadImageStatus) {
                          Log.v("TAG", "Uploading image successful");
                          // disconnect();
                      }
                  } else {
                      Log.d("TAG", "Connection failed from image");
                  }

                  boolean status2 = false;
                  // host – your FTP address
                  // username & password – for your secured login
                  // 21 default gateway for FTP
                  status2 = ftpclient.ftpConnect(host, username, password, 21);
                  if (status2) {
                      Log.d("TAG", "Connection Success");
                      uploadvideoStatus = ftpclient.ftpUpload(videoPath, "/soochana/Videos_" + uploadTimeStamp + ".mp4", "soochana", getApplicationContext());
                      if (uploadvideoStatus) {
                          Log.v("TAG", "Uploading video successful");
                          disconnect();
                      }
                  } else {
                      Log.d("TAG", "Connection failed from image");
                  }

                  if (uploadImageStatus && uploadvideoStatus) {
                      db.updateAttachmentPhotoVideoStatus(attach);
                      tempPhotoStatus = "1";
                      tempVideoStatus = "1";
                      attach.setPhotoStatus(tempPhotoStatus);
                      attach.setVideoStatus(tempVideoStatus);
                      uploadDataBaseline(base);
                      uploadDataAttachment(attach);
                  }

              }
          }).start();
      }


      //For uploading image on FTP Server
      public void uploadImage(final String photoPath, final AttachmentModel attach, final BaselineModel base) {

          new Thread(new Runnable() {
              public void run() {
                  boolean status = false;
                  // host – your FTP address
                  // username & password – for your secured login
                  // 21 default gateway for FTP
                  status = ftpclient.ftpConnect(host, username, password, 21);
                  try {
                      Thread.sleep(2000);
                  } catch (InterruptedException e) {
                      e.printStackTrace();
                  }
                  Log.v("TAg", "Value of status is  " + status);
                  if (status) {
                      Log.d("TAG", "Connection Success");
                      uploadImageStatus = ftpclient.ftpUpload(photoPath, "/soochana/Images_" + uploadTimeStamp + ".jpg", "soochana", getApplicationContext());
                      if (uploadImageStatus) {
                          Log.v("TAG", uploadImageStatus.toString());
                          mFlag = 1;
                          Log.v("TAG", "Uploading image successful");
                          db.updateAttachmentPhotoStatus(attach);

                          tempPhotoStatus = "1";
                          attach.setPhotoStatus(tempPhotoStatus);
                          Log.v("TAG", "Status updated : " + attach.getPhotoStatus());
                          uploadDataBaseline(base);
                          uploadDataAttachment(attach);
                          Log.v("TAG", "UploadDataBaseline called");
                          disconnect();
                      }
                  } else {
                      Log.d("TAG", "Connection failed from image");
                  }
              }
          }).start();

      }


      //For uploading video on FTP server
      public void uploadVideo(final String videoPath, final AttachmentModel attach, final BaselineModel base) {

          new Thread(new Runnable() {
              public void run() {
                  boolean status = false;
                  // host – your FTP address
                  // username & password – for your secured login
                  // 21 default gateway for FTP
                  status = ftpclient.ftpConnect(host, username, password, 21);
                  if (status) {
                      Log.d("TAG", "Connection Success");
                      uploadvideoStatus = ftpclient.ftpUpload(videoPath, "/soochana/Videos_" + uploadTimeStamp + ".mp4", "soochana", getApplicationContext());
                      if (uploadvideoStatus) {
                          Log.v("TAG", "Uploading video successful");
                          db.updateAttachmentVideoStatus(attach);
                          tempVideoStatus = "1";
                          attach.setVideoStatus(tempVideoStatus);
                          uploadDataBaseline(base);
                          uploadDataAttachment(attach);
                          Log.v("TAG", "UploadDataBaseline called");
                          disconnect();
                      }
                  } else {
                      Log.d("TAG", "Connection failed from video");
                  }
              }
          }).start();

      }


      //For disconnecting from FTP server
      private void disconnect() {

          new Thread(new Runnable() {
              public void run() {
                  ftpclient.ftpDisconnect();
              }
          }).start();
      }
  */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.logoutText) {

            session.logoutUser();
        }

        if (item.getItemId() == R.id.submissionText){

            Intent intent = new Intent(MainActivity.this,SubmissionActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

}
