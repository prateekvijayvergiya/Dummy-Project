package com.madprateek.dummyproject;

import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class NetworkRequestHandler {

    private ArrayList<AttachmentModel> attachmentModels;
    private ArrayList<BaselineModel> baselineModels;
    private Context context;
    private final String server_url_baseline = "http://192.168.12.160/Baseline.php";
    private final String server_url_attachments = "http://192.168.12.160/attachments.php";
    private static final String host = "ftp.pixxel-fs2001.fingerprinti.com";
    private static final String username = "ftpfs2001";
    private static final String password = "u701aC/}9S";
    private MyFTPClientFunctions ftpclient;
    private DatabaseHelper db;
    String tempPhotoStatus, tempVideoStatus;
    String dbPhotoPath = " ", dbVideoPath = " ";


    public NetworkRequestHandler(Context ctx, ArrayList<AttachmentModel> attachmentModels, ArrayList<BaselineModel> baselineModels) {
        this.context = ctx;
        ftpclient = new MyFTPClientFunctions();
        db = new DatabaseHelper(ctx);
        this.attachmentModels = attachmentModels;
        this.baselineModels = baselineModels;
    }

    public void uploadAllData() {
        for (AttachmentModel attach : attachmentModels) {
            if (!TextUtils.isEmpty(attach.getPhotoPath()) && !TextUtils.isEmpty(attach.getVideoPath())) {
                uploadImage(attach.getPhotoPath(), attach);
                uploadVideo(attach.getVideoPath(), attach);
            }
            if (!TextUtils.isEmpty(attach.getPhotoPath()) && TextUtils.isEmpty(attach.getVideoPath())) {
                uploadImage(attach.getPhotoPath(), attach);

            }
            if (!TextUtils.isEmpty(attach.getVideoPath()) && TextUtils.isEmpty(attach.getPhotoPath())) {
                uploadVideo(attach.getVideoPath(), attach);
            }
            uploadAttachment(attach);
            db.deleteAttachment(attach);
            Log.d("TAG", "Record deleted from attachment");
        }

        for (BaselineModel base : baselineModels) {
            uploadBaseline(base);
        }
    }

    private void uploadBaseline(final BaselineModel model) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, server_url_baseline, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Toast.makeText(context, "Response :" + response, Toast.LENGTH_LONG).show();
                Log.d("TAG", "upload on xampp of baseline");
                Log.d("TAG", response);
                db.deleteBaseline(model);
                Log.d("TAG", "Record deleted from Baseline");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(context, "some error occurred ..... Please Try Again Later", Toast.LENGTH_SHORT).show();
                error.printStackTrace();
                Log.d("TAG", "upload on remote db unsuccessful");
                Log.d("TAG", error.toString());

            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                String name = model.getName();
                String photoTitle = model.getPhotoTitle();
                String videoTitle = model.getVideoTitle();
                String message = model.getMessage();
                Map<String, String> details = new HashMap<>();
                details.put("name", name);
                details.put("photo_title", photoTitle);
                details.put("video_title", videoTitle);
                details.put("message", message);
                return details;
            }
        };

        MySingleton.getInstance(context).addTorequestque(stringRequest);
    }

    private void uploadAttachment(final AttachmentModel attach) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, server_url_attachments, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Toast.makeText(context, "Response :" + response, Toast.LENGTH_LONG).show();
                Log.d("TAG", "upload of attachment to remote db successful");
                Log.d("TAG", response);
                sendNotification();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(context, "Some error occurred..... Please Try Again Later .", Toast.LENGTH_SHORT).show();
                error.printStackTrace();
                Log.d("TAG", "upload of attachment to remote db unsuccessful");
                Log.d("TAG", error.toString());
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

        MySingleton.getInstance(context).addTorequestque(stringRequest);

    }

    private void sendNotification() {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.favicon)
                .setContentTitle("Data Upload")
                .setContentText("Form Submitted Successfully")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

// notificationId is a unique int for each notification that you must define
        notificationManager.notify(0, mBuilder.build());
    }

    private void uploadImage(String path, AttachmentModel attach) {

        boolean ftpConnectionStatus = ftpclient.ftpConnect(host, username, password, 21);
        Log.d("FTP Connection Status", String.valueOf(ftpConnectionStatus));
        if (ftpConnectionStatus) {
            String uploadTimeStamp = String.valueOf(Calendar.getInstance().getTimeInMillis());
            dbPhotoPath = "Images_" + uploadTimeStamp + ".jpg";
            boolean uploadImageStatus = ftpclient.ftpUpload(path, "/soochana/" + dbPhotoPath , "soochana", context);
            Log.d("Image Status", String.valueOf(uploadImageStatus));

            if (uploadImageStatus) {
                db.updateAttachmentPhotoStatus(attach);

                tempPhotoStatus = "1";
                attach.setPhotoStatus(tempPhotoStatus);
                db.updatePhotoPath(dbPhotoPath,attach);
                Log.d("Local Db Status :", attach.getPhotoStatus());
                ftpclient.ftpDisconnect();

            }
        }

    }

    private void uploadVideo(String path, AttachmentModel attach) {
        boolean ftpConnectionStatus = ftpclient.ftpConnect(host, username, password, 21);
        Log.d("FTP Connection Status", String.valueOf(ftpConnectionStatus));
        if (ftpConnectionStatus) {
            String uploadTimeStamp = String.valueOf(Calendar.getInstance().getTimeInMillis());
            dbVideoPath = "Videos_" + uploadTimeStamp + ".mp4";
            boolean uploadvideoStatus = ftpclient.ftpUpload(path, "/soochana/" + dbVideoPath, "soochana", context);
            if (uploadvideoStatus) {
                Log.v("TAG", "Uploading video successful");
                db.updateAttachmentVideoStatus(attach);
                tempVideoStatus = "1";
                attach.setVideoStatus(tempVideoStatus);
                db.updateVideoPath(dbVideoPath,attach);
                ftpclient.ftpDisconnect();

            }
        }
    }
}