package com.madprateek.dummyproject.ModelClasses;

public class AttachmentModel {

    private String baselineId;
    private String id2;
    private String photoStatus;
    private String videoStatus;
    private String photoPath;
    private String videoPath;
    private String mimeType;

    public AttachmentModel(String baselineId, String photoStatus, String videoStatus, String photoPath, String videoPath, String mimeType) {
        this.baselineId = baselineId;
        this.photoStatus = photoStatus;
        this.videoStatus = videoStatus;
        this.photoPath = photoPath;
        this.videoPath = videoPath;
        this.mimeType = mimeType;
    }

    public AttachmentModel() {

    }

    public String getBaselineId() {

        return baselineId;
    }

    public void setBaselineId(String baselineId) {
        this.baselineId = baselineId;
    }

    public String getPhotoStatus() {
        return photoStatus;
    }

    public void setPhotoStatus(String photoStatus) {
        this.photoStatus = photoStatus;
    }

    public String getVideoStatus() {
        return videoStatus;
    }

    public void setVideoStatus(String videoStatus) {
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
