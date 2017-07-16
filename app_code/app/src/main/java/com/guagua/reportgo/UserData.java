package com.guagua.reportgo;

import com.google.gson.annotations.SerializedName;

/**
 * Created by a63098233 on 2017/7/14.
 */

public class UserData {

    @SerializedName("avatar")
    String avatar;

    @SerializedName("name")
    String name;

    @SerializedName("email")
    String email;

    @SerializedName("plate_num")
    String plateNum = "";

    @SerializedName("token")
    String token;

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
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

    public String getPlateNum() {
        return plateNum;
    }

    public void setPlateNum(String plateNum) {
        this.plateNum = plateNum;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
