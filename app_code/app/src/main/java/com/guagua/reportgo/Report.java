package com.guagua.reportgo;

import com.google.gson.annotations.SerializedName;

/**
 * Created by a63098233 on 2017/7/14.
 */

public class Report {

    @SerializedName("pic")
    String pic;

    @SerializedName("address")
    String address;

    @SerializedName("date")
    String date;

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
