package com.example.smile.Models;

import android.util.EventLogTags;

public class User {
    private String username;
    private String status;
    private String imageurl;
    private String descriptionTv;
    private String Uid;


    public User() {
    }

    public User(String username, String status, String image,String description, String id) {
        this.username = username;
        this.status = status;
        this.imageurl = image;
        this.descriptionTv = description;
        this.Uid = id;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public String getImageurl() {
        return imageurl;
    }
    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }
    public String getDescription() {
        return descriptionTv;
    }
    public void setDescription(String description) {
        descriptionTv = description;
    }
    public String getUid() {
        return Uid;
    }
    public void setUid(String uid) {
        Uid = uid;
    }
}








