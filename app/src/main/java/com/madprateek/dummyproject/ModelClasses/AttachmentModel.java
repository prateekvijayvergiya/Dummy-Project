package com.madprateek.dummyproject.ModelClasses;

public class AttachmentModel {

    private String baselineId;
    private String id2;
    private String photoStatus;
    private String videoStatus;
    private String audioStatus;
    private String photoPath;
    private String videoPath;
    private String audioPath;
    private String photoTitle;
    private String videoTitle;
    private String audioTitle;
    private String serverId;

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public String getAudioStatus() {
        return audioStatus;
    }

    public void setAudioStatus(String audioStatus) {
        this.audioStatus = audioStatus;
    }

    public String getAudioPath() {
        return audioPath;
    }

    public void setAudioPath(String audioPath) {
        this.audioPath = audioPath;
    }

    public String getPhotoTitle() {
        return photoTitle;
    }

    public void setPhotoTitle(String photoTitle) {
        this.photoTitle = photoTitle;
    }

    public String getVideoTitle() {
        return videoTitle;
    }

    public void setVideoTitle(String videoTitle) {
        this.videoTitle = videoTitle;
    }

    public String getAudioTitle() {
        return audioTitle;
    }

    public void setAudioTitle(String audioTitle) {
        this.audioTitle = audioTitle;
    }


    public AttachmentModel(String baselineId, String serverId, String photoTitle,String videoTitle, String audioTitle,
                           String photoStatus, String videoStatus, String audioStatus, String photoPath, String videoPath, String audioPath) {
        this.baselineId = baselineId;
        this.photoStatus = photoStatus;
        this.videoStatus = videoStatus;
        this.photoPath = photoPath;
        this.videoPath = videoPath;
        this.photoTitle = photoTitle;
        this.videoTitle = videoTitle;
        this.audioTitle = audioTitle;
        this.audioPath = audioPath;
        this.audioStatus = audioStatus;
        this.serverId = serverId;

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

}
