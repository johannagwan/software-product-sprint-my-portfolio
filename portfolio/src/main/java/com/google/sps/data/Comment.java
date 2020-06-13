package com.google.sps.data;

public class Comment {
    private String username;
    private String commentBody;
    private String timestamp;

    public Comment(String username, String commentBody, String timestamp) {
        this.username = username;
        this.commentBody = commentBody;
        this.timestamp = timestamp;
    }
}