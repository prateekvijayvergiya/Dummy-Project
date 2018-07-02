package com.madprateek.dummyproject.HelperClasses;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.madprateek.dummyproject.Connection;
import com.madprateek.dummyproject.ModelClasses.AttachmentModel;
import com.madprateek.dummyproject.ModelClasses.BaselineModel;
import com.madprateek.dummyproject.NetworkRequestHandler;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import androidx.work.Worker;

public class CompressWorker extends Worker {

    DatabaseHelper db;
    ArrayList<AttachmentModel> allAttachments ;
    ArrayList<BaselineModel> allBaselines ;
    NetworkRequestHandler nrh ;
    String uploadTimeStamp;

    public CompressWorker(){
        super();
        db = new DatabaseHelper(getApplicationContext());
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        uploadTimeStamp = timeStamp;
    }
    @SuppressLint("WrongThread")
    @NonNull
    @Override
    public Result doWork() {
        if(Connection.isConnectingToInternet(getApplicationContext())){
            Log.d("service status :","device online");
            nrh= new NetworkRequestHandler(getApplicationContext(),allAttachments,allBaselines);
            allAttachments=(ArrayList)db.getAllAttachments();
            allBaselines=(ArrayList)db.getAllBaseline();
            new NetworkTask(allAttachments,allBaselines).execute();
            return Result.SUCCESS;
        }else{
            Log.d("service status :","device offline");
            return null;
        }

    }

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
