package com.madprateek.dummyproject.HelperClasses;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.MediaController;
import android.widget.Toast;

import com.madprateek.dummyproject.R;

import java.io.IOException;

public class AudioFunctions extends AppCompatActivity {

    private MediaRecorder myAudioRecorder;
    private String outputFile;
    private Context ctx;
    MediaController mediaController;

    public AudioFunctions(Context ctx) {
        this.ctx = ctx;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myAudioRecorder = new MediaRecorder();
        myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        try {
            myAudioRecorder.setOutputFile(getFilename());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void startRec(){

        try {
            myAudioRecorder = new MediaRecorder();
            myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
            myAudioRecorder.setOutputFile(getFilename());
            myAudioRecorder.prepare();
            myAudioRecorder.start();
        } catch (IllegalStateException ise) {
            // make something ...
        } catch (IOException ioe) {
            // make something
        }
        Toast.makeText(ctx, "Recording started", Toast.LENGTH_SHORT).show();
    }

    public void stopRec() {

        //String uploadTimeStamp = String.valueOf(Calendar.getInstance().getTimeInMillis());
       // outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + uploadTimeStamp + "/recording.3gp";
        //outputFile = getExternalCacheDir().getAbsolutePath() + uploadTimeStamp + "/recording.3gp";
       // Log.v("TAG","Audio storage path is : " + outputFile);
       // myAudioRecorder.stop();
        myAudioRecorder.release();
        myAudioRecorder = null;
        Toast.makeText(ctx, "Audio Recorded successfully", Toast.LENGTH_SHORT).show();
    }

    public void playRec(){

        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            //Toast.makeText(ctx, "Function call", Toast.LENGTH_LONG).show();
            mediaPlayer.setDataSource(outputFile);
            mediaPlayer.prepare();
            mediaPlayer.start();
            Toast.makeText(ctx, "Playing Audio", Toast.LENGTH_SHORT).show();
            mediaController = new MediaController(this);
            mediaController.setAnchorView(findViewById(R.id.playBtn));
            mediaController.requestFocus();
            mediaController.show();
        } catch (Exception e) {
            // make something
            Log.v("TAG","There is some error : " + e.getMessage());
        }
    }

    public void stopPlay(){

        MediaPlayer mediaPlayer = new MediaPlayer();
        mediaPlayer.release();
        mediaController.hide();
        mediaPlayer = null;

    }

    public String getOutputFile() {
        return outputFile;
    }

    private String getFilename() throws IOException {
        long time = System.currentTimeMillis();
        //String filepath = Environment.getExternalStorageDirectory() + "/Android/data/com.madprateek.dummyproject/files/Pictures/" + "audio_" + time + ".mp3";
       // String filepath = Environment.getExternalStorageDirectory().getPath() + "/Music/audio_" + time + ".mp3";
        //Log.v("TAG","PATH is " + filepath);

        String fileName = "/Audio_" + time + ".mp3";
        //File file = new File(Environment.DIRECTORY_MUSIC,fileName);
       // if(!file.exists()){
          //  file.mkdirs();}
       /// String path = file.getAbsolutePath();
       // File myDataPath = new File(SDCardpath
        //        + "/.My Recordings");
       // if (!myDataPath.exists())
         //   myDataPath.mkdir();
       // File audiofile = new File(myDataPath + "/" + fileName);
       /* File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), filepath);
        Log.v("TAG","file is " + file);

        if(!file.exists()){
            file.mkdirs();
        }*/

       // outputFile = filepath;     //file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".mp3";

      String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC) + fileName;


      outputFile = filePath;
        Log.w("TAG","Storage Path is : " + outputFile);
        return outputFile;
    }
}
