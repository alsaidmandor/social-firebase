package com.example.socialapp.model;

public class myChats
{
    private String receiverId ;
    private  String name ;
    private  String image ;

    public myChats() {
    }

    public myChats(String receiverId, String name, String image) {
        this.receiverId = receiverId;
        this.name = name;
        this.image = image;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
