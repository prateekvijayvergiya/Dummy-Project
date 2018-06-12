package com.madprateek.dummyproject.HelperClasses;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class AudioFunctions extends AppCompatActivity {

    private MediaRecorder myAudioRecorder;
    private String outputFile;
    private Context ctx;

    public AudioFunctions(Context ctx) {
        this.ctx = ctx;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public void startRec(){

        myAudioRecorder = new MediaRecorder();
        myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        myAudioRecorder.setOutputFile(getFilename());
        try {
            myAudioRecorder.prepare();
            myAudioRecorder.start();
        } catch (IllegalStateException ise) {
            // make something ...
        } catch (IOException ioe) {
            // make something
        }
        Toast.makeText(ctx, "Recording started", Toast.LENGTH_LONG).show();
    }

    public void stopRec() {

        //String uploadTimeStamp = String.valueOf(Calendar.getInstance().getTimeInMillis());
       // outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + uploadTimeStamp + "/recording.3gp";
        //outputFile = getExternalCacheDir().getAbsolutePath() + uploadTimeStamp + "/recording.3gp";
       // Log.v("TAG","Audio storage path is : " + outputFile);
        myAudioRecorder = new MediaRecorder();
        myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        myAudioRecorder.setOutputFile(getFilename());
       // myAudioRecorder.stop();
        myAudioRecorder.release();
        myAudioRecorder = null;
        Toast.makeText(ctx, "Audio Recorded successfully", Toast.LENGTH_LONG).show();
    }

    public void playRec(){

        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            Toast.makeText(ctx, "Function call", Toast.LENGTH_LONG).show();
            mediaPlayer.setDataSource(outputFile);
            mediaPlayer.prepare();
            mediaPlayer.start();
            Toast.makeText(ctx, "Playing Audio", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            // make something
            Log.v("TAG","There is some error : " + e.getMessage());
        }
    }

    public void stopPlay(){

        MediaPlayer mediaPlayer = new MediaPlayer();
        mediaPlayer.release();
        mediaPlayer = null;

    }

    public String getOutputFile() {
        return outputFile;
    }

    private String getFilename()
    {
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File file = new File(getExternalFilesDir(Environment.DIRECTORY_MUSIC), filepath);

        if(!file.exists()){
            file.mkdirs();
        }

        outputFile = file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".mp3";
        Log.v("TAG","Storage Path is : " + outputFile);
        return (outputFile);
    }
}
