package com.madprateek.dummyproject.ModelClasses;

public class AttachmentModel {

    private int baselineId;
    private String id2;
    private int photoStatus;
    private int videoStatus;
    private String photoPath;
    private String videoPath;
    private String mimeType;

    public AttachmentModel(String id2,int baselineId, int photoStatus, int videoStatus, String photoPath, String videoPath, String mimeType) {
        this.id2 = id2;
        this.baselineId = baselineId;
        this.photoStatus = photoStatus;
        this.videoStatus = videoStatus;
        this.photoPath = photoPath;
        this.videoPath = videoPath;
        this.mimeType = mimeType;
    }

    public AttachmentModel() {

    }

    public int getBaselineId() {

        return baselineId;
    }

    public void setBaselineId(Integer baselineId) {
        this.baselineId = baselineId;
    }

    public int getPhotoStatus() {
        return photoStatus;
    }

    public void setPhotoStatus(Integer photoStatus) {
        this.photoStatus = photoStatus;
    }

    public int getVideoStatus() {
        return videoStatus;
    }

    public void setVideoStatus(Integer videoStatus) {
        this.videoStatus = videoStatus;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public String getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    public String getId2() {
        return id2;
    }

    public void setId2(String id2) {
        this.id2 = id2;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }
}
