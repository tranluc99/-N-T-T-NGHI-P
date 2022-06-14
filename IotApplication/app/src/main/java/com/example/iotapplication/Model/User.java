package com.example.iotapplication.Model;

public class User {
    private String displayName ;
    private String email ;
    private String uID ;
    private int image ;
    private String pawssword ;

    public User(String displayName, String email, String uID, int image) {
        this.displayName = displayName;
        this.email = email;
        this.uID = uID;
        this.image = image;
    }

    public String getPawssword() {
        return pawssword;
    }

    public void setPawssword(String pawssword) {
        this.pawssword = pawssword;
    }

    public User(String displayName, String email, String uID) {
        this.displayName = displayName;
        this.email = email;
        this.uID = uID;
    }

    public User() {
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getuID() {
        return uID;
    }

    public void setuID(String uID) {
        this.uID = uID;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}
