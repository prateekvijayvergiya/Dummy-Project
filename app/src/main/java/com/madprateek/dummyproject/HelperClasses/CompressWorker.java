package com.madprateek.dummyproject.HelperClasses;

import android.annotation.SuppressLint;
import android.arch.lifecycle.LiveData;
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

import androidx.work.Constraints;
import androidx.work.WorkManager;
import androidx.work.WorkStatus;
import androidx.work.Worker;

public class CompressWorker extends Worker {

    DatabaseHelper db;
    ArrayList<AttachmentModel> allAttachments ;
    ArrayList<BaselineModel> allBaselines ;
    String uploadTimeStamp;

    public CompressWorker(){
        super();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        uploadTimeStamp = timeStamp;
    }
    @SuppressLint("WrongThread")
    @NonNull
    @Override
    public Result doWork() {
        db = new DatabaseHelper(getApplicationContext());

        Log.d("service status :","device online");

            allAttachments=(ArrayList)db.getAllAttachments();
            allBaselines=(ArrayList)db.getAllBaseline();
            Log.d("Work Count:",allAttachments.size()+" "+allBaselines.size());
            NetworkRequestHandler nrh = new NetworkRequestHandler(getApplicationContext(),allAttachments,allBaselines);
            nrh.uploadAllData();

            return Result.SUCCESS;

    }

/*
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
*/
}
