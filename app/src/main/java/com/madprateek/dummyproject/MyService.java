package com.madprateek.dummyproject;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class MyService extends Service {
    DatabaseHelper db;
    private static final String host = "ftp.pixxel-fs2001.fingerprinti.com";
    private static final String username = "ftpfs2001";
    private static final String password = "u701aC/}9S";
    private MyFTPClientFunctions ftpclient = null;
    private String mCurrentPhotoPath,mCurrentVideoPath,uploadTimeStamp,spinnerContent,mMimeType;
    Boolean uploadvideoStatus,uploadImageStatus;
    String baseId,tempPhotoStatus = "0",tempVideoStatus = "0";
    int mFlag = 0, rand =0;
    static  int count =0;
    Random random;
    String server_url_baseline = "http://192.168.12.160/Baseline.php";
    String server_url_attachments = "http://192.168.12.160/attachments.php";


    public MyService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        db = new DatabaseHelper(this);
        ftpclient = new MyFTPClientFunctions();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        uploadTimeStamp = timeStamp;

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.d("service status :","started");
        if(Connection.isConnectingToInternet(getApplicationContext())){
            Log.d("service status :","device online");
            uploadData();
            stopSelf(startId);
        }else{
            Log.d("service status :","device online");
        }
        return START_REDELIVER_INTENT;
    }

    private void uploadData() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                List<AttachmentModel> putAttach = db.getAllAttachments();
                count = putAttach.size();
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
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
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
                Log.v("TAg","Value of status is  " + status);
                if (status) {
                    Log.d("TAG", "Connection Success");
                    random = new Random();
                    rand = random.nextInt(1000);
                    uploadImageStatus = ftpclient.ftpUpload(photoPath,"/soochana/Images_" + uploadTimeStamp + "_" + rand + ".jpg","soochana",getApplicationContext());
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
                        //disconnect();
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
                        //disconnect();
                    }
                } else {
                    Log.d("TAG", "Connection failed from video");
                }
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

                        Toast.makeText(getApplicationContext(),"Response :"+response,Toast.LENGTH_LONG).show();
                        Log.v("TAG","upload on xampp of baseline");
                        Log.v("TAG",response);
                        db.deleteBaseline(base);
                        Log.v("TAG","Record deleted from Baseline");
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(getApplicationContext(),"some error found .....",Toast.LENGTH_SHORT).show();
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

                MySingleton.getInstance(getApplicationContext()).addTorequestque(stringRequest);
                //}
                // }
            }
        }).start();
    }

    private void uploadDataAttachment(final AttachmentModel attach) {

        new Thread(new Runnable() {
            @Override
            public void run() {

                //if (put != null){

                //for (final AttachmentModel attach : put){

                StringRequest stringRequest = new StringRequest(Request.Method.POST, server_url_attachments, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Toast.makeText(getApplicationContext(),"Response :"+response,Toast.LENGTH_LONG).show();
                        Log.v("TAG","upload on xampp of attachment");
                        Log.v("TAG",response);
                        db.deleteAttachment(attach);
                        count --;
                        Log.v("TAG","Record deleted from attachment");
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(getApplicationContext(),"some error found .....",Toast.LENGTH_SHORT).show();
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

                MySingleton.getInstance(getApplicationContext()).addTorequestque(stringRequest);
                //}
                //}
            }
        }).start();

        if (count != 1)
            uploadData();
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
