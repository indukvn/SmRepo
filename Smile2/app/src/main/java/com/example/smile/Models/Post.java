package com.example.smile.Models;

public class Post {
    private String postUid;
    private String postimage;
    private String description;
    private String publisher;

    public Post(String postUid, String postimage, String description, String publisher) {
        this.postUid = postUid;
        this.postimage = postimage;
        this.description = description;
        this.publisher = publisher;
    }

    public Post() {
    }

    public String getPostUid() {
        return postUid;
    }

    public void setPostUid(String postUid) {
        this.postUid = postUid;
    }

    public String getPostimage() {
        return postimage;
    }

    public void setPostimage(String postimage) {
        this.postimage = postimage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }
}
