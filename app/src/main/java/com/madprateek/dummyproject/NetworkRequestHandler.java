package com.madprateek.dummyproject;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

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
    String dbPhotoPath = " ", dbVideoPath = " ", dbAudioPath = " ", serverId, updatedId;
    static ArrayList<AttachmentModel> globalAttach;


    public NetworkRequestHandler(Context ctx, ArrayList<AttachmentModel> attachmentModels, ArrayList<BaselineModel> baselineModels) {
        this.context = ctx;
        ftpclient = new MyFTPClientFunctions();
        db = new DatabaseHelper(ctx);
        this.attachmentModels = attachmentModels;
        this.baselineModels = baselineModels;
    }

    public void uploadAllData() {

        for (BaselineModel base : baselineModels) {
            globalAttach = (ArrayList<AttachmentModel>) db.getSpecificAttachment(base.getId());
            uploadBaseline(base,globalAttach);
        }

        globalAttach = (ArrayList<AttachmentModel>) db.getAllAttachmentsServer();
        int start = 1;
        for (AttachmentModel attach : globalAttach){
            //updateIdServer(attach);
            Log.v("TAG","Check for attachment called");
            //Log.v("TAG","Server id in check attachment method is :" + attach.getServerId());
                // Log.v("TAG","Baseline id in local database is    :" + attach.getBaselineId());
                checkForAttachments(attach);
        }

    }



    private void uploadBaseline(final BaselineModel base, final ArrayList<AttachmentModel> attachmentModels) {

        String name = base.getName();
        String message = base.getMessage();
        String villageName = base.getVillageName();
        String deviceId = base.getDeviceId();
        String location = base.getLocation();
        //String location = "India";
        String photoSubject = base.getPhotoTitleText();
        String videoSubject = base.getVideoTitleText();
        String audioSubject = base.getAudioTitleText();
        String photoPath = base.getPhotoPath();
        String videoPath = base.getVideoPath();
        String audioPath = base.getAudioPath();
        HashMap<String, String> details = new HashMap<>();
        details.put("username", name);
        details.put("village",villageName);
        details.put("deviceId",deviceId);
        details.put("location",location);
        details.put("message", message);
        if (base.getPhotoTitleText() != null){
            details.put("photoSubject", photoSubject);
            details.put("photoPath", photoPath);
        }
        if (base.getVideoTitleText() != null){
            details.put("videoSubject", videoSubject);
            details.put("videoPath", videoPath);
        }
        if (base.getAudioTitleText() != null){
            details.put("audioSubject", audioSubject);
            details.put("audioPath", audioPath);
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(server_url_attachments, new JSONObject(details), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                   // Toast.makeText(context, "Response :" + response, Toast.LENGTH_SHORT).show();
                    serverId = response.getString("baselineId");
                    updatedId = serverId;
                   // Log.v("TAG","SERVER ID IS : " + serverId);
                    db.updateServerId(serverId,base,attachmentModels);
                    for (AttachmentModel attach : attachmentModels){
                        attach.setServerId(serverId);
                        //Log.v("TAg","Set new server id " + attach.getServerId());
                    }
                    db.deleteBaseline(base);
                    Log.v("TAG","Baseline Data Deleted");
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

        if (attach.getType().equals("photo")){
            new UploadPicture(attach.getPath(),attach).execute();
        }

        if (attach.getType().equals("video")){
            new UploadVideo(attach.getPath(),attach).execute();
        }

        if (attach.getType().equals("audio")){
            new UploadAudio(attach.getPath(),attach).execute();
        }

    }

    
    private void sendNotification(String mimeType) {
         AtomicInteger c = new AtomicInteger(0);
         int m = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);
         int id = c.incrementAndGet();
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.favicon)
                .setContentTitle("Data Upload")
                .setContentText(mimeType + " uploaded Successfully")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

// notificationId is a unique int for each notification that you must define
        notificationManager.notify(m, mBuilder.build());
    }

    private void updateServerDetails(final String id, final String path, final String mimeType, final AttachmentModel attach){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, server_update_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.v("TAG","response after updating server : " + response.toString());
               if (response.equals("success")){
                   db.deleteAttachment(attach);
                   Toast.makeText(context, "successful work done", Toast.LENGTH_SHORT).show();
                   Log.v("TAG","Row deleted from attachment");
                   sendNotification(mimeType);
               }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }

        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String > details = new HashMap<>();
                details.put("baselineId",id);
                Log.v("TAg","Value of server id in update server detail is :" + id);
                details.put("path",path);
                details.put("type",mimeType);
                Log.v("TAG","Server details updated");
                return details;
            }
        };
        MySingleton.getInstance(context).addTorequestque(stringRequest);
    }


    public class UploadPicture extends AsyncTask<Void,Void,String>{

        String photoPath;
        AttachmentModel attach;
        public UploadPicture(String photoPath, AttachmentModel attach) {
            this.photoPath = photoPath;
            this.attach = attach;
        }

        @Override
        protected String doInBackground(Void... voids) {
            boolean ftpConnectionStatus = ftpclient.ftpConnect(host, username, password, 21);
            Log.d("FTP Connection Status", String.valueOf(ftpConnectionStatus));
            if (ftpConnectionStatus) {
                String uploadTimeStamp = String.valueOf(Calendar.getInstance().getTimeInMillis());
                dbPhotoPath = "_Images_" + uploadTimeStamp + ".jpg";
                boolean uploadImageStatus = ftpclient.ftpUpload(photoPath, "/soochana/" + attach.getServerId() + dbPhotoPath , "soochana", context);
                Log.d("Image Status", String.valueOf(uploadImageStatus));

                if (uploadImageStatus) {
                    Log.v("TAG", "Uploading image successful");
                    //db.updateAttachmentPhotoStatus(attach);

                    //tempPhotoStatus = "1";
                    //attach.setPhotoStatus(tempPhotoStatus);
                    //db.updatePhotoPath(dbPhotoPath,attach);
                    String id = attach.getServerId();
                    Log.v("TAg","Value of server id is :" + id);
                    String mimeType = "photo";
                    updateServerDetails(id,dbPhotoPath,mimeType,attach);
                    ftpclient.ftpDisconnect();

                }
            }
            return null;
        }
    }

    public class UploadVideo extends AsyncTask<Void,Void,String>{

        String path;
        AttachmentModel attach;

        public UploadVideo(String videoPath, AttachmentModel attach) {
            this.path = videoPath;
            this.attach = attach;
        }

        @Override
        protected String doInBackground(Void... voids) {

            boolean ftpConnectionStatus = ftpclient.ftpConnect(host, username, password, 21);
            Log.d("FTP Connection Status", String.valueOf(ftpConnectionStatus));
            if (ftpConnectionStatus) {
                String uploadTimeStamp = String.valueOf(Calendar.getInstance().getTimeInMillis());
                dbVideoPath = "_Videos_" + uploadTimeStamp + ".mp4";
                boolean uploadvideoStatus = ftpclient.ftpUpload(path, "/soochana/" + attach.getServerId() + dbVideoPath, "soochana", context);
                if (uploadvideoStatus) {
                    Log.v("TAG", "Uploading video successful");
                    //db.updateAttachmentVideoStatus(attach);
                    //tempVideoStatus = "1";
                    //attach.setVideoStatus(tempVideoStatus);
                    // db.updateVideoPath(dbVideoPath,attach);
                    String id = attach.getServerId();
                    Log.v("TAg","Value of server id is :" + id);
                    String mimeType = "video";
                    updateServerDetails(id,dbVideoPath,mimeType,attach);
                    ftpclient.ftpDisconnect();

                }
            }
            return null;
        }
    }

    public class UploadAudio extends AsyncTask<Void,Void,String>{

        String path;
        AttachmentModel attach;

        public UploadAudio(String audioPath, AttachmentModel attach) {
            this.path = audioPath ;
            this.attach = attach;
        }

        @Override
        protected String doInBackground(Void... voids) {
            boolean ftpConnectionStatus = ftpclient.ftpConnect(host, username, password, 21);
            Log.d("FTP Connection Status", String.valueOf(ftpConnectionStatus));
            if (ftpConnectionStatus){
                String uploadTimeStamp = String.valueOf(Calendar.getInstance().getTimeInMillis());
                dbAudioPath = "_Audio_" + uploadTimeStamp + ".mp3";
                boolean uploadAudioStatus = ftpclient.ftpUpload(path, "/soochana/" + attach.getServerId() + dbAudioPath, "soochana", context);
                if (uploadAudioStatus){
                    Log.v("TAG", "Uploading audio successful");
                    // db.updateAttachmentAudioStatus(attach);
                    // attach.setAudioStatus("1");
                    //db.updateAudioPath(dbAudioPath,attach);
                    String id = attach.getServerId();
                    Log.v("TAg","Value of server id is :" + id);
                    String mimeType = "audio";
                    updateServerDetails(id,dbAudioPath,mimeType,attach);
                    ftpclient.ftpDisconnect();
                }
            }

            return null;
        }
    }
}