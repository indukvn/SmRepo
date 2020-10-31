package com.example.smile.Models;

public class Notification {
    private String userUid;
    private String text;
    private String postUid;
    private boolean ispost;

    public Notification(String userUid, String text, String postUid, boolean ispost) {
        this.userUid = userUid;
        this.text = text;
        this.postUid = postUid;
        this.ispost = ispost;
    }

    public Notification() {
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getPostUid() {
        return postUid;
    }

    public void setPostUid(String postUid) {
        this.postUid = postUid;
    }

    public boolean isIspost() {
        return ispost;
    }

    public void setIspost(boolean ispost) {
        this.ispost = ispost;
    }
}