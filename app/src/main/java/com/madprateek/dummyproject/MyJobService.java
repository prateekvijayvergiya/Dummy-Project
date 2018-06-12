package com.madprateek.dummyproject;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.AsyncTask;
import android.util.Log;

import com.madprateek.dummyproject.HelperClasses.DatabaseHelper;
import com.madprateek.dummyproject.HelperClasses.MyFTPClientFunctions;
import com.madprateek.dummyproject.ModelClasses.AttachmentModel;
import com.madprateek.dummyproject.ModelClasses.BaselineModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class MyJobService extends JobService {
    DatabaseHelper db;
    private static final String host = "ftp.pixxel-fs2001.fingerprinti.com";
    private static final String username = "ftpfs2001";
    private static final String password = "u701aC/}9S";
    private MyFTPClientFunctions ftpclient = null;
    Boolean uploadvideoStatus,uploadImageStatus;
    String tempPhotoStatus = "0",tempVideoStatus = "0",uploadTimeStamp;
    int mFlag = 0, rand = 0;
    static  int count =0;
    Random random;
    String server_url_baseline = "http://192.168.12.160/Baseline.php";
    String server_url_attachments = "http://192.168.12.160/attachments.php";
    ArrayList<AttachmentModel> allAttachments ;
    ArrayList<BaselineModel> allBaselines ;
    NetworkRequestHandler nrh ;

    public MyJobService() {
        super();
        db = new DatabaseHelper(this);
        ftpclient = new MyFTPClientFunctions();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        uploadTimeStamp = timeStamp;

    }

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        if(Connection.isConnectingToInternet(getApplicationContext())){
            Log.d("service status :","device online");
//            uploadData();
//            jobFinished(jobParameters,false);
            nrh= new NetworkRequestHandler(getApplicationContext(),allAttachments,allBaselines);
            allAttachments=(ArrayList)db.getAllAttachments();
            allBaselines=(ArrayList)db.getAllBaseline();
            new NetworkTask(allAttachments,allBaselines).execute();
            return false;
        }else{
            Log.d("service status :","device offline");
            return true;
        }

    }



    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }

//    @Override
//    public boolean onStopJob(JobParameters jobParameters) {
//        return !(Connection.isConnectingToInternet(getApplicationContext()));
//    }

    class NetworkTask extends AsyncTask<Void,Void,String> {
        private ArrayList<AttachmentModel> attachmentModels;
        private ArrayList<BaselineModel> baselineModels;

        public NetworkTask(ArrayList<AttachmentModel> attachmentModels,ArrayList<BaselineModel> baselineModels) {
            super();
            this.attachmentModels=attachmentModels;
            this.baselineModels=baselineModels;
        }

        @Override
        protected String doInBackground(Void... voids) {
            NetworkRequestHandler nrh = new NetworkRequestHandler(getApplicationContext(),attachmentModels,baselineModels);
            nrh.uploadAllData();
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d("Job Service","Starting Data Upload");
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("Job Service","Data Upload Finished");
        }
    }
}
