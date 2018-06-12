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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.madprateek.dummyproject.HelperClasses.DatabaseHelper;
import com.madprateek.dummyproject.HelperClasses.MyFTPClientFunctions;
import com.madprateek.dummyproject.HelperClasses.MySingleton;
import com.madprateek.dummyproject.ModelClasses.AttachmentModel;
import com.madprateek.dummyproject.ModelClasses.BaselineModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NetworkRequestHandler {

    private ArrayList<AttachmentModel> attachmentModels;
    private ArrayList<BaselineModel> baselineModels;
    private Context context;
    private final String server_update_url = "http://portal.jaipurrugsco.com/jrapi/public/soochana/update";
    private final String server_url_attachments = "http://portal.jaipurrugsco.com/jrapi/public/soochana/message";
    private static final String host = "ftp.pixxel-fs2001.fingerprinti.com";
    private static final String username = "ftpfs2001";
    private static final String password = "u701aC/}9S";
    private MyFTPClientFunctions ftpclient = null;
    private DatabaseHelper db;
    String serverBaselineId;
    String tempPhotoStatus, tempVideoStatus;
    String dbPhotoPath = " ", dbVideoPath = " ", dbAudioPath = " ", serverId;


    public NetworkRequestHandler(Context ctx, ArrayList<AttachmentModel> attachmentModels, ArrayList<BaselineModel> baselineModels) {
        this.context = ctx;
        ftpclient = new MyFTPClientFunctions();
        db = new DatabaseHelper(ctx);
        this.attachmentModels = attachmentModels;
        this.baselineModels = baselineModels;
    }

    public void uploadAllData() {
        /*for (AttachmentModel attach : attachmentModels) {
            if (!TextUtils.isEmpty(attach.getPhotoPath()) && !TextUtils.isEmpty(attach.getVideoPath()) && !TextUtils.isEmpty(attach.getAudioPath())) {
                uploadImage(attach.getPhotoPath(), attach);
                uploadVideo(attach.getVideoPath(), attach);
                uploadAudio(attach.getAudioPath(),attach);
            }
            if (!TextUtils.isEmpty(attach.getPhotoPath()) && !TextUtils.isEmpty(attach.getVideoPath()) && TextUtils.isEmpty(attach.getAudioPath())) {
                uploadImage(attach.getPhotoPath(), attach);
                uploadVideo(attach.getVideoPath(), attach);

            }
            if (!TextUtils.isEmpty(attach.getPhotoPath()) && !TextUtils.isEmpty(attach.getAudioPath()) && TextUtils.isEmpty(attach.getVideoPath())) {
                uploadImage(attach.getPhotoPath(), attach);
                uploadAudio(attach.getAudioPath(),attach);

            }
            if (TextUtils.isEmpty(attach.getPhotoPath()) && !TextUtils.isEmpty(attach.getAudioPath()) && !TextUtils.isEmpty(attach.getVideoPath())) {
                uploadVideo(attach.getVideoPath(), attach);
                uploadAudio(attach.getAudioPath(),attach);

            }
            if (!TextUtils.isEmpty(attach.getPhotoPath()) && TextUtils.isEmpty(attach.getAudioPath()) && TextUtils.isEmpty(attach.getVideoPath())) {
                uploadImage(attach.getPhotoPath(), attach);

            }
            if (TextUtils.isEmpty(attach.getPhotoPath()) && TextUtils.isEmpty(attach.getAudioPath()) && !TextUtils.isEmpty(attach.getVideoPath())) {
                uploadVideo(attach.getVideoPath(), attach);

            }
            if (TextUtils.isEmpty(attach.getPhotoPath()) && !TextUtils.isEmpty(attach.getAudioPath()) && TextUtils.isEmpty(attach.getVideoPath())) {
                uploadAudio(attach.getAudioPath(),attach);
            }
            uploadAttachment(attach);
            db.deleteAttachment(attach);
            Log.d("TAG", "Record deleted from attachment");
        }

        for (BaselineModel base : baselineModels) {
            uploadBaseline(base);
        }*/




        List<AttachmentModel> putAttach = db.getAllAttachments();
        List<BaselineModel> putBase = db.getAllBaseline();
        if (putBase != null){
            AttachmentModel attach;
            BaselineModel base;
            for (int i = 0; i < putAttach.size() ;i++){
                attach = putAttach.get(i);
                base = putBase.get(i);
                uploadBaseline(base,attach);
                //uploadAttachment(attach);

               /* if (!TextUtils.isEmpty(attach.getPhotoPath()) && !TextUtils.isEmpty(attach.getVideoPath()) && !TextUtils.isEmpty(attach.getAudioPath())) {
                    uploadImage(attach.getPhotoPath(), attach);
                    uploadVideo(attach.getVideoPath(), attach);
                    uploadAudio(attach.getAudioPath(),attach);
                }
                if (!TextUtils.isEmpty(attach.getPhotoPath()) && !TextUtils.isEmpty(attach.getVideoPath()) && TextUtils.isEmpty(attach.getAudioPath())) {
                    uploadImage(attach.getPhotoPath(), attach);
                    uploadVideo(attach.getVideoPath(), attach);

                }
                if (!TextUtils.isEmpty(attach.getPhotoPath()) && !TextUtils.isEmpty(attach.getAudioPath()) && TextUtils.isEmpty(attach.getVideoPath())) {
                    uploadImage(attach.getPhotoPath(), attach);
                    uploadAudio(attach.getAudioPath(),attach);

                }
                if (TextUtils.isEmpty(attach.getPhotoPath()) && !TextUtils.isEmpty(attach.getAudioPath()) && !TextUtils.isEmpty(attach.getVideoPath())) {
                    uploadVideo(attach.getVideoPath(), attach);
                    uploadAudio(attach.getAudioPath(),attach);

                }
                if (!TextUtils.isEmpty(attach.getPhotoPath()) && TextUtils.isEmpty(attach.getAudioPath()) && TextUtils.isEmpty(attach.getVideoPath())) {
                    uploadImage(attach.getPhotoPath(), attach);

                }
                if (TextUtils.isEmpty(attach.getPhotoPath()) && TextUtils.isEmpty(attach.getAudioPath()) && !TextUtils.isEmpty(attach.getVideoPath())) {
                    uploadVideo(attach.getVideoPath(), attach);

                }
                if (TextUtils.isEmpty(attach.getPhotoPath()) && !TextUtils.isEmpty(attach.getAudioPath()) && TextUtils.isEmpty(attach.getVideoPath())) {
                    uploadAudio(attach.getAudioPath(),attach);
                }
               */
                //db.deleteAttachment(attach);
               // Log.d("TAG", "Record deleted from attachment");
            }
        }
    }


    private void uploadBaseline(final BaselineModel base, final AttachmentModel attach) {

        String name = base.getName();
        String message = base.getMessage();
        String villageName = base.getVillageName();
        String deviceId = base.getDeviceId();
        String location = "India";
        String subject = attach.getPhotoTitle();
        String photoPath = attach.getPhotoPath();
        HashMap<String, String> details = new HashMap<>();
        details.put("username", name);
        details.put("village",villageName);
        details.put("deviceId",deviceId);
        details.put("location",location);
        details.put("message", message);
        details.put("photoSubject", subject);
        details.put("photoPath", photoPath);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(server_url_attachments, new JSONObject(details), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Toast.makeText(context, "Response :" + response, Toast.LENGTH_LONG).show();
                    serverId = response.getString("baselineId");
                    Log.v("TAG","SERVER ID IS : " + serverId);
                    db.updateServerId(serverId,attach);
                    db.deleteBaseline(base);
                    Log.v("TAG","Baseline Data Deleted");
                    checkForAttachments(attach);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        MySingleton.getInstance(context).addTorequestque(jsonObjectRequest);


    }

    private void checkForAttachments(AttachmentModel attach) {
        if (!TextUtils.isEmpty(attach.getPhotoPath()) && !TextUtils.isEmpty(attach.getVideoPath()) && !TextUtils.isEmpty(attach.getAudioPath())) {
            uploadImage(attach.getPhotoPath(), attach);
            uploadVideo(attach.getVideoPath(), attach);
            uploadAudio(attach.getAudioPath(),attach);
        }
        if (!TextUtils.isEmpty(attach.getPhotoPath()) && !TextUtils.isEmpty(attach.getVideoPath()) && TextUtils.isEmpty(attach.getAudioPath())) {
            uploadImage(attach.getPhotoPath(), attach);
            uploadVideo(attach.getVideoPath(), attach);

        }
        if (!TextUtils.isEmpty(attach.getPhotoPath()) && !TextUtils.isEmpty(attach.getAudioPath()) && TextUtils.isEmpty(attach.getVideoPath())) {
            uploadImage(attach.getPhotoPath(), attach);
            uploadAudio(attach.getAudioPath(),attach);

        }
        if (TextUtils.isEmpty(attach.getPhotoPath()) && !TextUtils.isEmpty(attach.getAudioPath()) && !TextUtils.isEmpty(attach.getVideoPath())) {
            uploadVideo(attach.getVideoPath(), attach);
            uploadAudio(attach.getAudioPath(),attach);

        }
        if (!TextUtils.isEmpty(attach.getPhotoPath()) && TextUtils.isEmpty(attach.getAudioPath()) && TextUtils.isEmpty(attach.getVideoPath())) {
            uploadImage(attach.getPhotoPath(), attach);

        }
        if (TextUtils.isEmpty(attach.getPhotoPath()) && TextUtils.isEmpty(attach.getAudioPath()) && !TextUtils.isEmpty(attach.getVideoPath())) {
            uploadVideo(attach.getVideoPath(), attach);

        }
        if (TextUtils.isEmpty(attach.getPhotoPath()) && !TextUtils.isEmpty(attach.getAudioPath()) && TextUtils.isEmpty(attach.getVideoPath())) {
            uploadAudio(attach.getAudioPath(),attach);
        }
    }

  /*  private void uploadBaseline(final BaselineModel model, final AttachmentModel attach) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, server_url_baseline, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Toast.makeText(context, "Response :" + response, Toast.LENGTH_LONG).show();
                Log.d("TAG", "upload on xampp of baseline");
                Log.d("TAG", response);
                //serverBaselineId = response.toString();
                //db.deleteBaseline(model);
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
                String message = model.getMessage();
                String villageName = model.getVillageName();
                String deviceId = model.getDeviceId();
                String location = "India";
                String subject = attach.getPhotoTitle();
                String photoPath = attach.getPhotoPath();
                Map<String, String> details = new HashMap<>();
                details.put("username", name);
                details.put("village",villageName);
                details.put("deviceId",deviceId);
                details.put("location",location);
                details.put("message", message);
                details.put("photoSubject", subject);
                details.put("photoPath", photoPath);
                return details;
            }
        };

        MySingleton.getInstance(context).addTorequestque(stringRequest);
    }*/



    private void uploadAttachment(final AttachmentModel attach) {

        if (!TextUtils.isEmpty(attach.getPhotoPath())) {
            uploadImageAttachment(attach);
        }
        if (!TextUtils.isEmpty(attach.getVideoPath())) {
            uploadVideoAttachment(attach);
        }
        if (!TextUtils.isEmpty(attach.getAudioPath())) {
            uploadAudioAttachment(attach);
        }


    }

    private void uploadAudioAttachment(final AttachmentModel attach) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, server_url_attachments, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(context, "Response :" + response, Toast.LENGTH_LONG).show();
                Log.d("TAG", "upload of audio attachment to remote db successful");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(context, "Some error occurred..... Please Try Again Later .", Toast.LENGTH_SHORT).show();
                error.printStackTrace();
                Log.d("TAG", "upload of audio attachment to remote db unsuccessful");
                Log.d("TAG", error.toString());
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                String baselineId = serverBaselineId;
                String subject = attach.getAudioTitle();
                String audioPath = attach.getAudioPath();
                String audioStatus = attach.getAudioStatus();
                Map<String, String> details = new HashMap<>();
                details.put("baseline_id", baselineId);
                details.put("subject", subject);
                details.put("audioPath", audioPath);
                details.put("audioStatus", audioStatus);
                return details;
            }
        };

        MySingleton.getInstance(context).addTorequestque(stringRequest);
    }

    private void uploadVideoAttachment(final AttachmentModel attach) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, server_url_attachments, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(context, "Response :" + response, Toast.LENGTH_LONG).show();
                Log.d("TAG", "upload of video attachment to remote db successful");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(context, "Some error occurred..... Please Try Again Later .", Toast.LENGTH_SHORT).show();
                error.printStackTrace();
                Log.d("TAG", "upload of video attachment to remote db unsuccessful");
                Log.d("TAG", error.toString());
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                String baselineId = serverBaselineId;
                String subject = attach.getVideoTitle();
                String videoPath = attach.getVideoPath();
                String videoStatus = attach.getVideoStatus();
                Map<String, String> details = new HashMap<>();
                details.put("baseline_id", baselineId);
                details.put("subject", subject);
                details.put("videoPath", videoPath);
                details.put("videoStatus", videoStatus);
                return details;
            }
        };

        MySingleton.getInstance(context).addTorequestque(stringRequest);

    }

    private void uploadImageAttachment(final AttachmentModel attach) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, server_url_attachments, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(context, "Response :" + response, Toast.LENGTH_LONG).show();
                Log.d("TAG", "upload of photo attachment to remote db successful");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(context, "Some error occurred..... Please Try Again Later .", Toast.LENGTH_SHORT).show();
                error.printStackTrace();
                Log.d("TAG", "upload of photo attachment to remote db unsuccessful");
                Log.d("TAG", error.toString());
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

               // String baselineId = "1";
                String subject = attach.getPhotoTitle();
                String photoPath = attach.getPhotoPath();
               // String photoStatus = attach.getPhotoStatus();
                Map<String, String> details = new HashMap<>();
              //  details.put("baseline_id", baselineId);
                details.put("photoSubject", subject);
                details.put("photoPath", photoPath);
              //  details.put("photo_status", photoStatus);
                return details;
            }
        };

        MySingleton.getInstance(context).addTorequestque(stringRequest);

    }


       /* StringRequest stringRequest = new StringRequest(Request.Method.POST, server_url_attachments, new Response.Listener<String>() {
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
                Map<String, String> details = new HashMap<>();
                details.put("baseline_id", baselineId);
                details.put("photo_path", photoPath);
                details.put("video_path", videoPath);
                details.put("photo_status", photoStatus);
                details.put("video_status", videoStatus);
                return details;
            }
        };

        MySingleton.getInstance(context).addTorequestque(stringRequest);*/



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
            boolean uploadImageStatus = ftpclient.ftpUpload(path, "/soochana/" + attach.getServerId() + dbPhotoPath , "soochana", context);
            Log.d("Image Status", String.valueOf(uploadImageStatus));

            if (uploadImageStatus) {
                Log.v("TAG", "Uploading image successful");
                //db.updateAttachmentPhotoStatus(attach);

                //tempPhotoStatus = "1";
                //attach.setPhotoStatus(tempPhotoStatus);
                //db.updatePhotoPath(dbPhotoPath,attach);
                String id = attach.getServerId();
                String mimeType = "photo";
                updateServerDetails(id,dbPhotoPath,mimeType);
                //ftpclient.ftpDisconnect();

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
                //db.updateAttachmentVideoStatus(attach);
                //tempVideoStatus = "1";
                //attach.setVideoStatus(tempVideoStatus);
               // db.updateVideoPath(dbVideoPath,attach);
                String id = attach.getServerId();
                String mimeType = "video";
                updateServerDetails(id,dbVideoPath,mimeType);
                ftpclient.ftpDisconnect();

            }
        }
    }

    private void uploadAudio(String path, AttachmentModel attach){

        boolean ftpConnectionStatus = ftpclient.ftpConnect(host, username, password, 21);
        Log.d("FTP Connection Status", String.valueOf(ftpConnectionStatus));
        if (ftpConnectionStatus){
            String uploadTimeStamp = String.valueOf(Calendar.getInstance().getTimeInMillis());
            dbAudioPath = "Audio_" + uploadTimeStamp + ".mp3";
            boolean uploadAudioStatus = ftpclient.ftpUpload(path, "/soochana/" + dbAudioPath, "soochana", context);
            if (uploadAudioStatus){
                Log.v("TAG", "Uploading audio successful");
               // db.updateAttachmentAudioStatus(attach);
               // attach.setAudioStatus("1");
                //db.updateAudioPath(dbAudioPath,attach);
                String id = attach.getServerId();
                String mimeType = "audio";
                updateServerDetails(id,dbAudioPath,mimeType);
            }
        }

    }

    private void updateServerDetails(final String serverId, final String path, final String mimeType){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, server_update_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.v("TAG","response after updating server : " + response.toString());

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }

        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String > details = new HashMap<>();
                details.put("baselineId",serverId);
                details.put("path",path);
                details.put("type",mimeType);
                Log.v("TAG","Server details updated");
                return super.getParams();
            }
        };
        MySingleton.getInstance(context).addTorequestque(stringRequest);
    }
}