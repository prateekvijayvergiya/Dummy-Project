package com.madprateek.dummyproject;

import android.Manifest;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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
import com.madprateek.dummyproject.HelperClasses.CompressWorker;
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
import java.util.concurrent.TimeUnit;

import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

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
    private int REQUEST_HARDWARE_LOCATION = 600;
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
    String serverId = null;
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
    String baseId, tempStatus = "0", mLocation, mDeviceId, mOutputFile;
    SessionManager session;
    DeviceLocation deviceLocation;
    AudioFunctions audioFunctions;
    Button mStartBtn, mStopBtn, mPlayBtn, mRecStopBtn;
    int mFlag = 0;
    JobInfo jobInfo;
    JobScheduler jobScheduler;
    //File image;
    Boolean imageStatus, videoStatus, uploadvideoStatus, uploadImageStatus;
    LocationListener locationListener;
    LocationManager locationManager;
    String[] PERMISSIONS = {Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION};
    int PERMISSION_ALL = 1;
    WorkManager workManager;
    WorkRequest callDataRequest;

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
        workManager = WorkManager.getInstance();
        ftpclient = new MyFTPClientFunctions();
        session = new SessionManager(MainActivity.this);
        audioFunctions = new AudioFunctions(getApplicationContext());
        deviceLocation = new DeviceLocation(MainActivity.this);

        //Log.v("TAG", "Location of device is : " + mLocation);

        session.checkLogin();
        askPermission();

        mStartBtn = (Button) findViewById(R.id.StartBtn);
        mStopBtn = (Button) findViewById(R.id.StopBtn);
        mPlayBtn = (Button) findViewById(R.id.playBtn);
        mRecStopBtn = (Button) findViewById(R.id.stopBtn);


        //For getting the TimeStamp
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        uploadTimeStamp = timeStamp;


      /*  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            jobInfo = new JobInfo.Builder(jobID, new ComponentName(getApplicationContext(), MyJobService.class))
                    .setPeriodic(16 * 60 * 1000, 5 * 60 * 1000)
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY).build();
            jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);

        } else {
            jobInfo = new JobInfo.Builder(jobID, new ComponentName(getApplicationContext(), MyJobService.class))
                    .setPeriodic(10 * 60 * 1000)
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY).build();
            jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        }*/


        final PeriodicWorkRequest.Builder callDataRequest = new PeriodicWorkRequest.Builder(CompressWorker.class,
                16, TimeUnit.MINUTES, 5, TimeUnit.MINUTES);
        final PeriodicWorkRequest photoCheckWork = callDataRequest.build();



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
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{"android.permission.RECORD_AUDIO"}, REQUEST_AUDIO_RECORD);
                } else audioFunctions.startRec();
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

                mOutputFile = audioFunctions.getOutputFile();
                HashMap<String, String> user = session.getUserDetails();
                String name = user.get(SessionManager.KEY_NAME);
                String village = spinnerContent;
                String photoTitleText = mPhotoTitleText.getText().toString();
                String videoTitleText = mVideoTitleText.getText().toString();
                String audioTitleText = mAudioTitleText.getText().toString();
                String messageText = mMessageText.getText().toString();
                String photoPath = mPhotoPath;
                String videoPath = mVideoPath;
                String audioPath = mOutputFile;
                Log.w("TAG", "Audio path is : " + mOutputFile);

                checkForValidation();
                if (checkForValidation()){
                    if (!TextUtils.isEmpty(messageText)) {
                        messageText = messageText;
                    } else {
                        messageText = "-";
                    }

                    if (TextUtils.isEmpty(photoTitleText))
                        photoTitleText = "";
                    if (TextUtils.isEmpty(videoTitleText))
                        videoTitleText = "";
                    if (TextUtils.isEmpty(audioTitleText))
                        audioTitleText = "";


                     mLocation = getLocation();
                    Log.v("TAG","device Location during submission is : " + mLocation );
                    Toast.makeText(MainActivity.this, "Value of location in DB " + mLocation, Toast.LENGTH_SHORT).show();
                    storeBaseline(name, village, mLocation, messageText, mDeviceId, photoTitleText, videoTitleText, audioTitleText,
                            photoPath, videoPath, audioPath);

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
                    if (!TextUtils.isEmpty(audioTitleText)) {
                        String subject = audioTitleText;
                        String path = audioPath;
                        String type = "audio";
                        String status = "0";
                        storeAttachment(baseId, serverId, subject, path, type, status);
                    }

                    Connection connection = new Connection();
                    if (connection.isConnectingToInternet(getApplicationContext())) {
                        //ArrayList<AttachmentModel> allAttachments = (ArrayList) db.getAllAttachments();
                        //ArrayList<BaselineModel> allBaselines = (ArrayList) db.getAllBaseline();
                        //new NetworkTask(allAttachments, allBaselines).execute();
                       // jobScheduler.schedule(jobInfo);
                       // jobID++;
                        workManager.enqueue(photoCheckWork);

                    } else {

                        //jobScheduler.schedule(jobInfo);
                        //jobID++;
                        workManager.enqueue(photoCheckWork);
                        Toast.makeText(getApplicationContext(), "Please Check your Internet Connectivity", Toast.LENGTH_LONG).show();

                    }

                    Intent intent = new Intent(MainActivity.this, FinishActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

    }

    private void askPermission() {
        for (String permission : PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this,PERMISSIONS,PERMISSION_ALL);
                return;
            }else {
                mLocation = getLocation();
                mDeviceId = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
            }
        }
    }


    public boolean checkForValidation(){
        boolean status = false;

        String photoTitleText = mPhotoTitleText.getText().toString();
        String videoTitleText = mVideoTitleText.getText().toString();
        String audioTitleText = mAudioTitleText.getText().toString();
        String messageText = mMessageText.getText().toString();
        String photoPath = mPhotoPath;
        String videoPath = mVideoPath;
        String audioPath = mOutputFile;

        if (photoPath != null){
            if (photoTitleText.equals("")){
               // Toast.makeText(MainActivity.this, "Please fill Title", Toast.LENGTH_SHORT).show();
                mPhotoTitleText.setError("This Field Required");
                return status;
            }
        }
        if (videoPath != null){
            if (videoTitleText.equals("")){
               // Toast.makeText(MainActivity.this, "Please fill Title", Toast.LENGTH_SHORT).show();
                mVideoTitleText.setError("This Field Required");
                return status;
            }
        }
        if (audioPath != null){
            if (audioTitleText.equals("")){
                //Toast.makeText(MainActivity.this, "Please fill Title", Toast.LENGTH_SHORT).show();
                mAudioTitleText.setError("This Field Required");
                return status;
            }
        }
        if (TextUtils.isEmpty(photoTitleText) && TextUtils.isEmpty(videoTitleText) && TextUtils.isEmpty(audioTitleText) &&
                TextUtils.isEmpty(messageText)) {
            Toast.makeText(MainActivity.this, "Please fill required details", Toast.LENGTH_SHORT).show();
            mPhotoTitleText.setError("This Field Required");
            return status;
        }else {
            status = true;
            return status;
        }

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

    private void storeBaseline(String username, String spinnerContent, String location, String message, String deviceId, String photoTitleText,
                               String videoTitleText, String audioTitleText, String photoPath, String videoPath, String audioPath) {
        BaselineModel base = new BaselineModel(username,spinnerContent,location,message,deviceId,photoTitleText,videoTitleText,audioTitleText,
                photoPath,videoPath,audioPath);
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

            case 1:{
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED
                        && grantResults[2] == PackageManager.PERMISSION_GRANTED ){
                    //Do your work.
                    mLocation = getLocation();
                    mDeviceId = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
                } else {
                    Toast.makeText(this, "Until you grant the permission, we cannot proceed further", Toast.LENGTH_SHORT).show();
                }
                return;
            }
            //Gallery
            case 200:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                    REQUEST_STATUS = true;//for setting camera feature true for permission
                    if (CAPTURE_CODE == "PHOTO") {
                        chooseImage();

                    } else {

                        chooseVideo();
                    }
                }//else {
                    //ActivityCompat.requestPermissions(MainActivity.this, new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, REQUEST_STORAGE);
                //}
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
                    //mOutputFile = audioFunctions.getOutputFile();
                   // Toast.makeText(this, "You require permission for capturing audio", Toast.LENGTH_SHORT).show();
                }
                break;

            //FOR LOCATION
            case 400:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    mLocation = getLocation();
                }else {
                    //mLocation = getLocation();
                    //Toast.makeText(this, "You require permission for capturing location", Toast.LENGTH_SHORT).show();
                  //  ActivityCompat.requestPermissions(MainActivity.this, new String[]{"android.permission.ACCESS_FINE_LOCATION"}, REQUEST_LOCATION);
                }break;

            //FOR device id
            case 500:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    mDeviceId = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
                }else{
                    //mDeviceId = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
                    //Toast.makeText(this, "This app require device Id", Toast.LENGTH_SHORT).show();
                   // ActivityCompat.requestPermissions(MainActivity.this, new String[]{"android.permission.READ_PHONE_STATE"}, REQUEST_PHONE_STATE);
                }
                break;

            case 600:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                   //mLocation = getLocation();
                }else{
                   // mLocation= getLocation();
                    // Toast.makeText(this, "This app require device Id", Toast.LENGTH_SHORT).show();
                }
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
                    mPhotoPath = getImagePath(uri);
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
                    mediaController.show();
                    mVideoShow.setMediaController(mediaController);
                    mediaController.setEnabled(true);
                    mediaController.requestFocus();
                    mVideoPath = getVideoPath(uri);
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


    public String getImagePath(Uri uri){
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":")+1);
        cursor.close();

        cursor = getContentResolver().query(
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();

        return path;
    }


    public String getVideoPath(Uri uri){
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":")+1);
        cursor.close();

        cursor = getContentResolver().query(
                android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Video.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
        cursor.close();

        return path;
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.logoutText) {

            session.logoutUser();
            finish();
        }

        if (item.getItemId() == R.id.submissionText){

            Intent intent = new Intent(MainActivity.this,SubmissionActivity.class);
            intent.putExtra("deviceId",mDeviceId);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    public String getLocation() {
        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                mLocation = "Latitude : " + location.getLatitude() + " , " + "Longitude : " + location.getLongitude();
                //Log.v("TAG","Value of mLocation in main onLocationChanged :" + mLocation);
               // Toast.makeText(MainActivity.this, "Return inside onLocation" + mLocation, Toast.LENGTH_SHORT).show();
                //Log.v("TAG", "Latitude : " + location.getLatitude() + " , " + "Longitude : " + location.getLongitude());
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        };


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{"android.permission.ACCESS_FINE_LOCATION"}, REQUEST_LOCATION);

        } else {
            //Log.v("TAG","Entered in else");
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
            return mLocation;
        }

       // Log.v("TAG","returning value in Main is :" + mLocation);
       // Toast.makeText(this, "Return outside in Main onLocation" + mLocation, Toast.LENGTH_SHORT).show();
        return mLocation;
    }

}
