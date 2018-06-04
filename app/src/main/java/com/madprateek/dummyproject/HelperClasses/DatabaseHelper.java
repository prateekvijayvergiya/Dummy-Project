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
    private static final String KEY_NAME = "name";
    private static final String KEY_PHOTO_TITLE = "photoTitle";
    private static final String KEY_VIDEO_TITLE = "videoTitle";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_BASELINE_ID = "baselineId";
    private static final String KEY_PHOTO_STATUS = "photoStatus";
    private static final String KEY_VIDEO_STATUS = "videoStatus";
    private static final String KEY_PHOTO_PATH = "photoPath";
    private static final String KEY_VIDEO_PATH = "videoPath";
    private static final String KEY_MIMETYPE = "mimeType";

    SQLiteDatabase db;
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATION_BASELINE = "CREATE TABLE "+BASELINE+" (" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_NAME + " TEXT,"
                + KEY_PHOTO_TITLE + " TEXT," + KEY_VIDEO_TITLE + " TEXT," + KEY_MESSAGE + " TEXT)";
        db.execSQL(CREATION_BASELINE);
        Log.v("TAG-Table creation","Baseline table created");

        String CREATION_ATTACHMENTS = "CREATE TABLE "+ATTACHMENTS+" (" + KEY_ID_2 + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_BASELINE_ID + " INTEGER ," + KEY_PHOTO_STATUS + " INTEGER,"
                + KEY_VIDEO_STATUS + " INTEGER," + KEY_PHOTO_PATH + " TEXT," + KEY_VIDEO_PATH + " TEXT," + KEY_MIMETYPE + " TEXT)";
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
        values.put(KEY_PHOTO_TITLE, base.getPhotoTitle());
        values.put(KEY_VIDEO_TITLE, base.getVideoTitle());
        values.put(KEY_MESSAGE, base.getMessage());
        long mId = db.insert(BASELINE,null,values);
        Log.v("TAG","Baseline Row Added");
        db.close();

        return mId;
    }
    

    public void addAttachment(AttachmentModel attach){

        db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_BASELINE_ID, attach.getBaselineId());
        values.put(KEY_PHOTO_STATUS, attach.getPhotoStatus());
        values.put(KEY_VIDEO_STATUS, attach.getVideoStatus());
        values.put(KEY_PHOTO_PATH, attach.getPhotoPath());
        values.put(KEY_VIDEO_PATH, attach.getVideoPath());
        values.put(KEY_MIMETYPE, attach.getMimeType());
        db.insert(ATTACHMENTS, null,values);
        Log.v("TAG","Attachment Row Added");
        db.close();
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
                base.setPhotoTitle(cursor.getString(2));
                base.setVideoTitle(cursor.getString(3));
                base.setMessage(cursor.getString(4));
                fetchBaseline.add(base);
                Log.v("TAG - Baseline","Data Fetching");
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
                attach.setPhotoStatus(cursor.getString(2));
                attach.setVideoStatus(cursor.getString(3));
                attach.setPhotoPath(cursor.getString(4));
                attach.setVideoPath(cursor.getString(5));
                attach.setMimeType(cursor.getString(6));
                fetchAttachments.add(attach);
                Log.v("TAG - Attachment","Data Fetching");
            }while (cursor.moveToNext());
        }
        return  fetchAttachments;
    }

    public void deleteBaseline(BaselineModel base){
        db = this.getWritableDatabase();
        db.delete(BASELINE, KEY_ID + " = ?", new String[]{String.valueOf(base.getId())});
        db.close();
    }

    public void deleteAttachment(AttachmentModel attach){
        db = this.getWritableDatabase();
        db.delete(ATTACHMENTS, KEY_ID_2 + " = ?",new String[]{attach.getId2()});
        db.close();
    }

    public void updateAttachmentPhotoStatus(AttachmentModel attach){
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
}
