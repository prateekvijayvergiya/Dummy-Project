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
import android.text.TextUtils;
import android.util.Log;
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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.madprateek.dummyproject.HelperClasses.DatabaseHelper;
import com.madprateek.dummyproject.HelperClasses.MyFTPClientFunctions;
import com.madprateek.dummyproject.HelperClasses.MySingleton;
import com.madprateek.dummyproject.ModelClasses.AttachmentModel;
import com.madprateek.dummyproject.ModelClasses.BaselineModel;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private static final String host = "ftp.pixxel-fs2001.fingerprinti.com";
    private static final String username = "ftpfs2001";
    private static final String password = "u701aC/}9S";
    private MyFTPClientFunctions ftpclient = null;
    String server_url_baseline = "http://192.168.12.160/Baseline.php";
    String server_url_attachments = "http://192.168.12.160/attachments.php";

    private ImageView mImageShow;
    private VideoView mVideoShow;
    private EditText mPhotoTitleText,mVideoTitleText,mMessageText;
    private String mCurrentPhotoPath,mCurrentVideoPath,uploadTimeStamp,spinnerContent,mMimeType;
    private File image,video;
    String mPhotoPath,mVideoPath;
    private DatabaseHelper db;
    String baseId,tempPhotoStatus = "0",tempVideoStatus = "0";
    int mFlag = 0;
    //File image;
    Boolean imageStatus,videoStatus,uploadvideoStatus,uploadImageStatus;
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
        mMessageText = (EditText) findViewById(R.id.messageEditText);
        mPhotoBtn = (Button) findViewById(R.id.photoClickBtn);
        mVideoBtn = (Button) findViewById(R.id.videoClickBtn);
        mSubmitBtn = (Button) findViewById(R.id.submitBtn);
        db = new DatabaseHelper(this);
        ftpclient = new MyFTPClientFunctions();


        //For getting the TimeStamp
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        uploadTimeStamp = timeStamp;


       //finalUpload();
        uploadData();


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
                CharSequence options[] = new CharSequence[]{"Choose from Gallery (गैलरी से चयन करो)","Camera (कैमरा)"};
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Select Option (विकल्प चुनें)");
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
                CharSequence options[] = new CharSequence[]{"Choose from Gallery (गैलरी से चयन करो)","Camera (कैमरा)"};
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Select Option (विकल्प चुनें)");
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


        //For submitting the form
        mSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = spinnerContent;
                String photoTitleText = mPhotoTitleText.getText().toString();
                String videoTitleText = mVideoTitleText.getText().toString();
                String messageText = mMessageText.getText().toString();

                //for getting the empty text
                if (!TextUtils.isEmpty(photoTitleText)){
                    photoTitleText = photoTitleText;
                }else {
                    photoTitleText = "-";
                }
                if (!TextUtils.isEmpty(videoTitleText)){
                    videoTitleText = videoTitleText;
                }else {
                    videoTitleText = "-";
                }
                if (!TextUtils.isEmpty(messageText)){
                    messageText = messageText;
                }else {
                    messageText = "-";
                }

                //for getting the mime type
                if (!TextUtils.isEmpty(mPhotoPath) && !TextUtils.isEmpty(mVideoPath)){
                    mMimeType = "JPEG/mp4";
                }else if (!TextUtils.isEmpty(mPhotoPath)){
                    mMimeType = "JPEG";
                }else mMimeType = "mp4";


                //for setting video and photo path
                if (!TextUtils.isEmpty(mPhotoPath)){
                    mPhotoPath = mPhotoPath;
                }else mPhotoPath = "";
                if (!TextUtils.isEmpty(mVideoPath)){
                    mVideoPath = mVideoPath;
                }else mVideoPath = "";

                storeBaseline(name,photoTitleText,videoTitleText,messageText);
                storeAttachment(baseId,tempPhotoStatus,tempVideoStatus,mPhotoPath,mVideoPath,mMimeType);
                //showDataBasseline();
                //showDataAttachment();

                uploadData();
                Log.v("TAG","upload data called");

            }
        });

    }

    private void finalUpload() {

        List<AttachmentModel> put = db.getAllAttachments();
        for (int i = put.size() ; i >= 0 ; i-- ){
            Log.v("TAG","Loop called : " + i);
            uploadData();
            Log.v("TAG","from on create upload called");
        }
    }

    private void storeAttachment(String id, String s, String s1, String mPhotoPath, String mVideoPath, String mMimeType) {
        AttachmentModel attach = new AttachmentModel(id,s,s1,mPhotoPath,mVideoPath,mMimeType);
        db.addAttachment(attach);
        Log.v("TAG - attachment","Data inserted row created in attachment");
    }

    private void storeBaseline(String spinnerContent, String photoTitleText, String videoTitleText, String messageText) {
        BaselineModel base = new BaselineModel(spinnerContent, photoTitleText, videoTitleText, messageText);
        long Id = db.addBaseline(base);
        //baseId = Long.toString(Id);
        baseId = String.valueOf(Id);
        Log.v("TAG - Baseline","Data inserted row created in Baseline");
    }

    //For initialise Spinner
    public Spinner initSpinner(Spinner s, int content_array) {

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,content_array,R.layout.spinner_style);
        adapter.setDropDownViewResource(R.layout.spinner_style);
        s.setAdapter(adapter);
        return s;
    }



    //For showing data of baseline on logs
    private void showDataBasseline(){

        List<BaselineModel> put = db.getAllBaseline();
        for (BaselineModel base : put){

            String dId = base.getId();
            String name = base.getName();
            String dphoto = base.getPhotoTitle();
            String dVideo = base.getVideoTitle();
            String dmessage = base.getMessage();
            HashMap<String,String> details = new HashMap<>();
            details.put("id",dId);
            details.put("\nname",name);
            details.put("\nphoto",dphoto);
            details.put("\nvideo",dVideo);
            details.put("\nmessage",dmessage);
            Log.v("Show Details",String.valueOf(details));
        }
    }

    //For showing data of attachment on logs
    private void showDataAttachment(){

        List<AttachmentModel> put = db.getAllAttachments();
        for (AttachmentModel attach : put){

            String dId = attach.getId2();
            String fId = attach.getBaselineId();
            String pPath = attach.getPhotoPath();
            String vPath = attach.getVideoPath();
            String pStatus = attach.getPhotoStatus();
            String vStatus = attach.getVideoStatus();
            String type = attach.getMimeType();
            HashMap<String,String> details = new HashMap<>();
            details.put("dId",dId);
            details.put("\nfId",fId);
            details.put("\npPath",pPath);
            details.put("\nvpath",vPath);
            details.put("\npStatus",pStatus);
            details.put("\nvStatus",vStatus);
            details.put("\ntype",type);
            Log.v("Show Details",String.valueOf(details));
        }
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
                   mPhotoPath = uri.getPath();
                   Log.v("TAG","path of image");
               }
               break;

               //Image capture
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
                   mPhotoPath = uri.getPath();
               }
               break;

           case 20:

              if (resultCode == RESULT_OK){
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

               if (resultCode == RESULT_OK){
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
        Log.v("TAG","From createFile" + mCurrentVideoPath);
        return video;
    }


    //For uploading final Data to server
    public void uploadData(){

        Connection connection = new Connection();
        if ( connection.isConnectingToInternet(getApplicationContext())){

            new Thread(new Runnable() {
                @Override
                public void run() {

                    List<AttachmentModel> putAttach = db.getAllAttachments();
                    Log.v("TAG","display putAttach  " + putAttach.size());
                    List<BaselineModel> putBase = db.getAllBaseline();

                    if (putAttach != null){
                        AttachmentModel attach;
                        BaselineModel base;
                        for (int i = 0; i < putAttach.size() ;i++){
                            attach = putAttach.get(i);
                            base = putBase.get(i);
                            Log.v("TAG","value of base is" + base.toString());

                            if (!TextUtils.isEmpty(attach.getPhotoPath()) && !TextUtils.isEmpty(attach.getVideoPath())){

                                //imageStatus = uploadImage(attach.getPhotoPath());
                                //videoStatus = uploadVideo(attach.getVideoPath());
                                uploadBoth(attach.getPhotoPath(),attach.getVideoPath(), attach, base);
                            }
                            if (!TextUtils.isEmpty(attach.getPhotoPath()) && TextUtils.isEmpty(attach.getVideoPath())){

                                 uploadImage(attach.getPhotoPath(),attach,base);
                                //db.deleteAttachment(attach);
                               /* if (flag == 1){
                                    db.updateAttachmentPhotoStatus(attach);
                                    uploadDataBaseline();
                                    Log.v("TAG","UploadDataBaseline called");
                                    //uploadDataAttachment();
                                }*/

                            }
                            if (!TextUtils.isEmpty(attach.getVideoPath())  && TextUtils.isEmpty(attach.getPhotoPath())){

                                uploadVideo(attach.getVideoPath(),attach,base);
                            }

                        }
                    }
                }
            }).start();
        }
        else {

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

                                Toast.makeText(MainActivity.this,"Response :"+response,Toast.LENGTH_LONG).show();
                                Log.v("TAG","upload on xampp of attachment");
                                Log.v("TAG",response);
                                db.deleteAttachment(attach);
                                Log.v("TAG","Record deleted from attachment");
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                                Toast.makeText(MainActivity.this,"some error found .....",Toast.LENGTH_SHORT).show();
                                error.printStackTrace();
                                Log.v("TAG","upload on xampp unsuccessful");
                                Log.v("TAG",error.toString());

                            }
                        }){

                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {

                                String baselineId = attach.getBaselineId();
                                String photoPath = attach.getPhotoPath();
                                String videoPath = attach.getVideoPath();
                                String photoStatus = attach.getPhotoStatus();
                                String videoStatus = attach.getVideoStatus();
                                String mimeType = attach.getMimeType();
                                Map<String,String> details = new HashMap<>();
                                details.put("baseline_id",baselineId);
                                details.put("photo_path",photoPath);
                                details.put("video_path",videoPath);
                                details.put("photo_status",photoStatus);
                                details.put("video_status",videoStatus);
                                details.put("mime_type",mimeType);
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

                                Toast.makeText(MainActivity.this,"Response :"+response,Toast.LENGTH_LONG).show();
                                Log.v("TAG","upload on xampp of baseline");
                                Log.v("TAG",response);
                                db.deleteBaseline(base);
                                Log.v("TAG","Record deleted from Baseline");
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                                Toast.makeText(MainActivity.this,"some error found .....",Toast.LENGTH_SHORT).show();
                                error.printStackTrace();
                                Log.v("TAG","upload on xampp unsuccessful");
                                Log.v("TAG",error.toString());

                            }
                        }){

                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {

                                String name = base.getName();
                                String photoTitle = base.getPhotoTitle();
                                String videoTitle = base.getVideoTitle();
                                String message = base.getMessage();
                                Map<String,String> details = new HashMap<>();
                                details.put("name",name);
                                details.put("photo_title",photoTitle);
                                details.put("video_title",videoTitle);
                                details.put("message",message);
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
                    uploadImageStatus = ftpclient.ftpUpload(photoPath,"/soochana/Images_" + uploadTimeStamp + ".jpg","soochana",getApplicationContext());
                    if (uploadImageStatus){
                        Log.v("TAG","Uploading image successful");
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
                    uploadvideoStatus = ftpclient.ftpUpload(videoPath,"/soochana/Videos_" + uploadTimeStamp + ".mp4","soochana",getApplicationContext());
                    if (uploadvideoStatus){
                        Log.v("TAG","Uploading video successful");
                        disconnect();
                    }
                } else {
                    Log.d("TAG", "Connection failed from image");
                }

                if (uploadImageStatus && uploadvideoStatus){
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
    public void uploadImage(final String photoPath, final AttachmentModel attach, final BaselineModel base){

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
                Log.v("TAg","Value of status is  " + status);
                if (status) {
                    Log.d("TAG", "Connection Success");
                    uploadImageStatus = ftpclient.ftpUpload(photoPath,"/soochana/Images_" + uploadTimeStamp + ".jpg","soochana",getApplicationContext());
                    if (uploadImageStatus){
                        Log.v("TAG",uploadImageStatus.toString());
                        mFlag = 1;
                        Log.v("TAG","Uploading image successful");
                        db.updateAttachmentPhotoStatus(attach);

                        tempPhotoStatus = "1";
                        attach.setPhotoStatus(tempPhotoStatus);
                        Log.v("TAG","Status updated : " + attach.getPhotoStatus());
                        uploadDataBaseline(base);
                        uploadDataAttachment(attach);
                        Log.v("TAG","UploadDataBaseline called");
                        disconnect();
                    }
                } else {
                    Log.d("TAG", "Connection failed from image");
                }
            }
        }).start();

    }


    //For uploading video on FTP server
    public void uploadVideo(final String videoPath, final AttachmentModel attach, final BaselineModel base){

        new Thread(new Runnable() {
            public void run() {
                boolean status = false;
                // host – your FTP address
                // username & password – for your secured login
                // 21 default gateway for FTP
                status = ftpclient.ftpConnect(host, username, password, 21);
                if (status) {
                    Log.d("TAG", "Connection Success");
                    uploadvideoStatus = ftpclient.ftpUpload(videoPath,"/soochana/Videos_" + uploadTimeStamp + ".mp4","soochana",getApplicationContext());
                    if (uploadvideoStatus){
                        Log.v("TAG","Uploading video successful");
                        db.updateAttachmentVideoStatus(attach);
                        tempVideoStatus = "1";
                        attach.setVideoStatus(tempVideoStatus);
                        uploadDataBaseline(base);
                        uploadDataAttachment(attach);
                        Log.v("TAG","UploadDataBaseline called");
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

}
