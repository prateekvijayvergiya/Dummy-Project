package com.madprateek.dummyproject.ModelClasses;

public class BaselineModel {

    private String message;
    private String name;
    private String villageName;
    private String location;
    private String deviceId;

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

    public BaselineModel( String name, String villageName, String location, String message, String deviceId) {
        this.villageName = villageName;
        this.location = location;
        this.deviceId = deviceId;
        this.message = message;
        this.name = name;

    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
