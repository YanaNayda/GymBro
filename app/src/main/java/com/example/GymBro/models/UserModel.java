package com.example.GymBro.models;

public class UserModel {
    String email;
    String password;
    String phone;


    public UserModel(String email, String password, String phone) {
        this.email = email;
        this.phone = phone;
        this.password = password;

    }
    public UserModel( ){}

    public String getPhone( ) {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }


    public String getEmail( ) {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword( ) {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}