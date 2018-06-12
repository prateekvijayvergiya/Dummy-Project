package com.madprateek.dummyproject;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.madprateek.dummyproject.HelperClasses.DatabaseHelper;
import com.madprateek.dummyproject.HelperClasses.MyFTPClientFunctions;

import java.text.SimpleDateFormat;
import java.util.Date;
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
          //  uploadData();
            stopSelf(startId);
        }else{
            Log.d("service status :","device online");
        }
        return START_REDELIVER_INTENT;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}
