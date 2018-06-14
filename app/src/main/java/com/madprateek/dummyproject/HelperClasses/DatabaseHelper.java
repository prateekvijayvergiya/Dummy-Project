package com.madprateek.dummyproject.HelperClasses;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.madprateek.dummyproject.ModelClasses.AttachmentModel;
import com.madprateek.dummyproject.ModelClasses.BaselineModel;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {


    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "new_data";
    private static final String BASELINE = "baseline";
    private static final String ATTACHMENTS = "attachments";
    private static final String KEY_ID = "id";
    private static final String KEY_ID_2 = "id2";
    private static final String KEY_SERVER_ID = "serverId";
    private static final String KEY_NAME = "name";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_BASELINE_ID = "baselineId";
    private static final String KEY_VILLAGE_NAME = "villageName";
    private static final String KEY_LOCATON = "locaton";
    private static final String KEY_DEVICE = "deviceId";
    private static final String KEY_SUBJECT = "subject";
    private static final String KEY_PATH = "path";
    private static final String KEY_TYPE = "mimeType";
    private static final String KEY_STATUS = "status";
    private static final String KEY_PHOTO_TEXT = "photoText";
    private static final String KEY_VIDEO_TEXT = "videoText";
    private static final String KEY_AUDIO_TEXT = "audioText";
    private static final String KEY_PHOTO_PATH = "photoPath";
    private static final String KEY_VIDEO_PATH = "videoPath";
    private static final String KEY_AUDIO_PATH = "audioPath";



    SQLiteDatabase db;
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATION_BASELINE = "CREATE TABLE "+BASELINE+" (" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_NAME + " TEXT,"
                + KEY_VILLAGE_NAME + " TEXT," + KEY_LOCATON + " TEXT," + KEY_MESSAGE + " TEXT," + KEY_DEVICE + " TEXT," + KEY_PHOTO_TEXT + " TEXT,"
                + KEY_VIDEO_TEXT + " TEXT," + KEY_AUDIO_TEXT + " TEXT," + KEY_PHOTO_PATH + " TEXT," + KEY_VIDEO_PATH + " TEXT," + KEY_AUDIO_PATH + " TEXT)";
        db.execSQL(CREATION_BASELINE);
        Log.v("TAG-Table creation","Baseline table created");

       /* String CREATION_ATTACHMENTS = "CREATE TABLE "+ATTACHMENTS+" (" + KEY_ID_2 + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_BASELINE_ID + " INTEGER," + KEY_SERVER_ID + " INTEGER,"  + KEY_PHOTO_TITLE + " TEXT," + KEY_VIDEO_TITLE + " TEXT," + KEY_AUDIO_TITLE + " TEXT," + KEY_PHOTO_STATUS + " INTEGER,"
                + KEY_VIDEO_STATUS + " INTEGER," + KEY_AUDIO_STATUS + " INTEGER," + KEY_PHOTO_PATH + " TEXT,"
                + KEY_VIDEO_PATH + " TEXT," + KEY_AUDIO_PATH + " TEXT)";
        db.execSQL(CREATION_ATTACHMENTS);*/
        String CREATION_ATTACHMENTS = "CREATE TABLE " +ATTACHMENTS+" (" + KEY_ID_2 + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_BASELINE_ID + " INTEGER," + KEY_SERVER_ID + " INTEGER," + KEY_SUBJECT + " TEXT," + KEY_PATH + " TEXT,"
                + KEY_TYPE + " TEXT," + KEY_STATUS + " INTEGER)";
        db.execSQL(CREATION_ATTACHMENTS);
        Log.v("TAG- Table creation", "Attachments table created");
    }
    //+ "FOREIGN KEY ("+KEY_BASELINE_ID+") REFERENCES "+BASELINE+" ("+KEY_ID+"))";

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + BASELINE);
        this.onCreate(db);

        db.execSQL("DROP TABLE IF EXISTS " + ATTACHMENTS);
        this.onCreate(db);
    }

  /*  @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()){
            db.execSQL("PRAGMA foreign_keys = ON;");
        }
    }*/

    public long addBaseline(BaselineModel base){

        db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ID, base.getId());
        values.put(KEY_NAME, base.getName());
        values.put(KEY_VILLAGE_NAME,base.getVillageName());
        values.put(KEY_LOCATON,base.getLocation());
        values.put(KEY_MESSAGE, base.getMessage());
        values.put(KEY_DEVICE,base.getDeviceId());
        values.put(KEY_PHOTO_TEXT,base.getPhotoTitleText());
        values.put(KEY_VIDEO_TEXT,base.getVideoTitleText());
        values.put(KEY_AUDIO_TEXT,base.getAudioTitleText());
        values.put(KEY_PHOTO_PATH,base.getPhotoPath());
        values.put(KEY_VIDEO_PATH,base.getVideoPath());
        values.put(KEY_AUDIO_PATH,base.getAudioPath());
        long mId = db.insert(BASELINE,null,values);
        Log.v("TAG","Baseline Row Added");

        return mId;
    }


    public void addAttachment(AttachmentModel attach){

        db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_BASELINE_ID, attach.getBaselineId());
        values.put(KEY_SERVER_ID,attach.getServerId());
        values.put(KEY_SUBJECT,attach.getSubject());
        values.put(KEY_PATH,attach.getPath());
        values.put(KEY_TYPE,attach.getType());
        values.put(KEY_STATUS,attach.getStatus());
        db.insert(ATTACHMENTS, null,values);
        Log.v("TAG","Attachment Row Added");
    }

    public List<BaselineModel> getAllBaseline(){

        List<BaselineModel> fetchBaseline = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + BASELINE;
        db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        BaselineModel base;

        if (cursor.moveToFirst()){
            do{
                base = new BaselineModel();
                base.setId(cursor.getString(0));
                base.setName(cursor.getString(1));
                base.setVillageName(cursor.getString(2));
                base.setLocation(cursor.getString(3));
                base.setMessage(cursor.getString(4));
                base.setDeviceId(cursor.getString(5));
                base.setPhotoTitleText(cursor.getString(6));
                base.setVideoTitleText(cursor.getString(7));
                base.setAudioTitleText(cursor.getString(8));
                base.setPhotoPath(cursor.getString(9));
                base.setVideoPath(cursor.getString(10));
                base.setAudioPath(cursor.getString(11));
                fetchBaseline.add(base);
            }while (cursor.moveToNext());
        }
        return fetchBaseline;
    }

    public List<AttachmentModel> getAllAttachments(){

        List<AttachmentModel> fetchAttachments = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + ATTACHMENTS;
        db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        AttachmentModel attach;

        if (cursor.moveToFirst()){
            do {
                attach = new AttachmentModel();
                attach.setId2(cursor.getString(0));
                attach.setBaselineId(cursor.getString(1));
                attach.setServerId(cursor.getString(2));
                attach.setSubject(cursor.getString(3));
                attach.setPath(cursor.getString(4));
                attach.setType(cursor.getString(5));
                attach.setStatus(cursor.getString(6));
                fetchAttachments.add(attach);
            }while (cursor.moveToNext());
        }
        return  fetchAttachments;
    }

    public  List<AttachmentModel> getSpecificAttachment(String id){
        List<AttachmentModel> fetch = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + ATTACHMENTS + " WHERE " + KEY_BASELINE_ID + " = " + id;
        db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        AttachmentModel attach;

        if (cursor.moveToFirst()){
            do {
                attach = new AttachmentModel();
                attach.setId2(cursor.getString(0));
                attach.setBaselineId(cursor.getString(1));
                attach.setServerId(cursor.getString(2));
                attach.setSubject(cursor.getString(3));
                attach.setPath(cursor.getString(4));
                attach.setType(cursor.getString(5));
                attach.setStatus(cursor.getString(6));
                fetch.add(attach);
            }while (cursor.moveToNext());
        }
        return fetch;
    }

    public void deleteBaseline(BaselineModel base){
        db = this.getWritableDatabase();
        db.delete(BASELINE, KEY_ID + " = ?", new String[]{String.valueOf(base.getId())});

    }

    public void deleteAttachment(AttachmentModel attach){
        db = this.getWritableDatabase();
        db.delete(ATTACHMENTS, KEY_ID_2 + " = ?",new String[]{attach.getId2()});

    }

  /*  public void updateAttachmentPhotoStatus(AttachmentModel attach){
        db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_PHOTO_STATUS,"1");
        db.update(ATTACHMENTS, values, KEY_ID_2 + " = ?",new String[]{String.valueOf(attach.getId2())});
        db.close();

    }

    public void updateAttachmentPhotoVideoStatus(AttachmentModel attach){
        db = this.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_PHOTO_STATUS,"1");
        values.put(KEY_VIDEO_STATUS,"1");
        db.update(ATTACHMENTS, values, KEY_ID_2 + " = ?",new String[]{attach.getId2()});
        db.close();

    }

    public void updateAttachmentVideoStatus(AttachmentModel attach){
        db = this.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_VIDEO_STATUS,"1");
        db.update(ATTACHMENTS, values, KEY_ID_2 + " = ?",new String[]{attach.getId2()});
        db.close();

    }

    public void updateAttachmentAudioStatus(AttachmentModel attach){
        db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_AUDIO_STATUS,"1");
        db.update(ATTACHMENTS, values, KEY_ID_2 + " = ?", new String[]{attach.getId2()});
        db.close();

    }

    public void updatePhotoPath(String dbPhotoPath,AttachmentModel attach){
        db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_PHOTO_PATH, dbPhotoPath);
        db.update(ATTACHMENTS, values, KEY_ID_2 + " = ?", new String[]{String.valueOf(attach.getId2())});
        attach.setPhotoPath(dbPhotoPath);
        Log.v("TAG","value of photoPath in DB : " + attach.getPhotoPath());
        db.close();
    }

    public void updateVideoPath(String dbVideoPath, AttachmentModel attach){
        db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_VIDEO_PATH, dbVideoPath);
        db.update(ATTACHMENTS, values, KEY_ID_2 + " = ?", new String[]{String.valueOf(attach.getId2())});
        attach.setVideoPath(dbVideoPath);
        db.close();
    }

    public void updateAudioPath(String dbAudioPath, AttachmentModel attach){
        db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_AUDIO_PATH, dbAudioPath);
        db.update(ATTACHMENTS, values, KEY_ID_2 + " = ?", new String[]{String.valueOf(attach.getId2())});
        attach.setAudioPath(dbAudioPath);
        db.close();
    }*/

    public void updateServerId(String serverId, BaselineModel base, ArrayList<AttachmentModel> attachmentModels){
        db = this.getWritableDatabase();
        String id = base.getId();
        ContentValues values = new ContentValues();
        values.put(KEY_SERVER_ID, serverId);
        db.update(ATTACHMENTS, values, KEY_BASELINE_ID + " = ?", new String[]{String.valueOf(base.getId())});
        Log.v("TAG", "Server id updated in attachment");
        /*attachmentModels = (ArrayList<AttachmentModel>) getSpecificAttachment(id);
        Log.v("TAG","selected specific attachments" + attachmentModels.toString());
        for (AttachmentModel attach : attachmentModels){
            attach.setServerId(serverId);
            Log.v("TAg","Set new server id " + attach.getServerId());
        }*/
        showData(id);

    }

    private void showData(String id) {

        List<AttachmentModel> specificAttach = getSpecificAttachment(id);
        for (AttachmentModel attach : specificAttach){
           String server = attach.getServerId();
           Log.v("TAG","\nServer id from show data is : " + server);
           Log.v("TAG","\nServer id updated in DB from show data is : " + attach.getServerId());
        }
    }
}
