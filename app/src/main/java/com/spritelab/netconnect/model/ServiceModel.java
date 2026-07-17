package com.spritelab.netconnect.model;

public class ServiceModel {
    private String url;
    private boolean isSuccessfully;

    public ServiceModel(String url, boolean isSuccessfully) {
        this.url = url;
        this.isSuccessfully = isSuccessfully;
    }

    public boolean isSuccessfully() {
        return isSuccessfully;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setSuccessfully(boolean successfully) {
        isSuccessfully = successfully;
    }
}