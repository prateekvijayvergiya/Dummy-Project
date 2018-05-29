package com.madprateek.dummyproject.ModelClasses;

public class BaselineModel {

    private String photoTitle;
    private String videoTitle;
    private String message;
    private String name;
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

    public BaselineModel( String name, String photoTitle, String videoTitle, String message) {
        this.photoTitle = photoTitle;
        this.videoTitle = videoTitle;
        this.message = message;
        this.name = name;

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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
