package com.example.smile.Models;

public class Inscribe {
    private String description;
    private String publisher;
    private String comments;
    private String inscribeUid;
    private String publisherUid;
    private String posttext;

    public Inscribe(String description, String publisher, String comments, String inscribeUid, String publisherUid, String posttext) {
        this.description = description;
        this.publisher = publisher;
        this.comments = comments;
        this.inscribeUid = inscribeUid;
        this.publisherUid = publisherUid;
        this.posttext = posttext;
    }

    public Inscribe() {
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

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getInscribeUid() {
        return inscribeUid;
    }

    public void setInscribeUid(String inscribeUid) {
        this.inscribeUid = inscribeUid;
    }

    public String getPublisherUid() {
        return publisherUid;
    }

    public void setPublisherUid(String publisherUid) {
        this.publisherUid = publisherUid;
    }

    public String getPosttext() {
        return posttext;
    }

    public void setPosttext(String posttext) {
        this.posttext = posttext;
    }
}
