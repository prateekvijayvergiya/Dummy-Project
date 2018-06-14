package com.madprateek.dummyproject.ModelClasses;

public class BaselineModel {

    private String message;
    private String name;
    private String villageName;
    private String location;
    private String deviceId;
    private String photoTitleText;
    private String videoTitleText;
    private String audioTitleText;
    private String photoPath;
    private String videoPath;
    private String audioPath;

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

    public String getAudioPath() {
        return audioPath;
    }

    public void setAudioPath(String audioPath) {
        this.audioPath = audioPath;
    }

    public String getPhotoTitleText() {
        return photoTitleText;
    }

    public void setPhotoTitleText(String photoTitleText) {
        this.photoTitleText = photoTitleText;
    }

    public String getVideoTitleText() {
        return videoTitleText;
    }

    public void setVideoTitleText(String videoTitleText) {
        this.videoTitleText = videoTitleText;
    }

    public String getAudioTitleText() {
        return audioTitleText;
    }

    public void setAudioTitleText(String audioTitleText) {
        this.audioTitleText = audioTitleText;
    }

    public String getVillageName() {
        return villageName;
    }

    public void setVillageName(String villageName) {
        this.villageName = villageName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String id;

    public BaselineModel() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public BaselineModel( String name, String villageName, String location, String message, String deviceId, String photoTitleText,
                          String videoTitleText, String audioTitleText, String photoPath, String videoPath, String audioPath) {
        this.villageName = villageName;
        this.location = location;
        this.deviceId = deviceId;
        this.message = message;
        this.name = name;
        this.photoTitleText = photoTitleText;
        this.videoTitleText = videoTitleText;
        this.audioTitleText = audioTitleText;
        this.photoPath = photoPath;
        this.videoPath = videoPath;
        this.audioPath = audioPath;

    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
