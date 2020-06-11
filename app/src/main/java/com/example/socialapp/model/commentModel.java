package com.example.socialapp.model;

public class commentModel
{

    private String commentId ;
    private String name ;
    private String userImage ;
    private String comment ;
    private int type ;
    private long time ;

    public commentModel() {
    }

    public commentModel(String commentId, String name, String userImage, String comment, int type, long time) {
        this.commentId = commentId;
        this.name = name;
        this.userImage = userImage;
        this.comment = comment;
        this.type = type;
        this.time = time;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
