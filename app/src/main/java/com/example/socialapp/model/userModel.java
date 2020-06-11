package com.example.socialapp.model;

public class userModel
{

    private String name ;
    private String email ;
    private String mobile ;
    private String address  ;
    private String image ;
    private String id ;

    public userModel() {
    }

    public userModel(String name, String email, String mobile, String address, String image, String id)
    {
        this.name = name;
        this.email = email;
        this.mobile = mobile;
        this.address = address;
        this.image = image;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
