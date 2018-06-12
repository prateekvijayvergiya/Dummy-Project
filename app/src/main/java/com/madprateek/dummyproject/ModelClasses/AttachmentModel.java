package com.madprateek.dummyproject.ModelClasses;

public class AttachmentModel {

    private String baselineId;
    private String id2;
    private String subject;
    private String path;
    private String type;
    private String status;

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    private String serverId;

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }



    public AttachmentModel(String baselineId, String serverId, String subject, String path, String type, String status) {
        this.baselineId = baselineId;
        this.serverId = serverId;
        this.subject = subject;
        this.path = path;
        this.type = type;
        this.status = status;

    }

    public AttachmentModel() {

    }

    public String getBaselineId() {

        return baselineId;
    }

    public void setBaselineId(String baselineId) {
        this.baselineId = baselineId;
    }


    public String getId2() {
        return id2;
    }

    public void setId2(String id2) {
        this.id2 = id2;
    }

}
