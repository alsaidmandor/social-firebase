package com.example.socialapp.model;

public class postModel
{

    private String name ;
    private String userImage ;
    private String PostText ;
    private String postImage ;
    private long time ;
    private int type ;
    private String postId ;


    public postModel() {
    }

    public postModel(String name, String userImage, String postText, String postImage, long time, int type, String postId)
    {
        this.name = name;
        this.userImage = userImage;
        PostText = postText;
        this.postImage = postImage;
        this.time = time;
        this.type = type;
        this.postId = postId;
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

    public String getPostText() {
        return PostText;
    }

    public void setPostText(String postText) {
        PostText = postText;
    }

    public String getPostImage() {
        return postImage;
    }

    public void setPostImage(String postImage) {
        this.postImage = postImage;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }
}
