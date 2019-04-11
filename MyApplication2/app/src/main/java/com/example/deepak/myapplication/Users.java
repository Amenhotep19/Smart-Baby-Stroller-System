package com.example.deepak.myapplication;


public class Users {
public String imageUrl;
    public String imageName;
public String uid;
    public String name;
    public String email;
    public String password;
    public String phone;

    public Users(){}

    public Users(String id, String name, String phone, String email){}



    public Users(String uid, String name, String email, String password, String phone) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

}