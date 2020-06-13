package com.google.sps.data;

public class Comment {
    private String username;
    private String comment;
    private String timestamp;

    public Comment(String username, String comment, String timestamp) {
        this.username = username;
        this.comment = comment;
        this.timestamp = timestamp;
    }
}