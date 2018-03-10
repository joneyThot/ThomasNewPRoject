package com.handyman.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @SerializedName("id")
    public String id = "";

    @SerializedName("mobile")
    public String mobile = "";

    @SerializedName("firstname")
    public String firstname = "";

    @SerializedName("lastname")
    public String lastname = "";

    @SerializedName("gender")
    public String gender = "";

    @SerializedName("user_type")
    public String user_type = "";

    @SerializedName("job_list")
    public String job_list = "";

    @SerializedName("dob")
    public String dob = "";

    @SerializedName("email")
    public String email = "";

    @SerializedName("whatsapp_id")
    public String whatsapp_id = "";

    @SerializedName("password")
    public String password = "";

    @SerializedName("qualification")
    public String qualification = "";

    @SerializedName("experience")
    public String experience = "";

    @SerializedName("provider")
    public String provider = "";

    @SerializedName("address")
    public String address = "";

    @SerializedName("street")
    public String street = "";

    @SerializedName("landmark")
    public String landmark = "";

    @SerializedName("city")
    public String city = "";

    @SerializedName("pincode")
    public String pincode = "";

    @SerializedName("state")
    public String state = "";

    @SerializedName("website")
    public String website = "";

    @SerializedName("lat")
    public String lat = "";

    @SerializedName("lng")
    public String lng = "";

    @SerializedName("device_id")
    public String device_id = "";

    @SerializedName("device_token")
    public String device_token = "";

    @SerializedName("img")
    public String img = "";

    @SerializedName("img_path")
    public String img_path = "";

    @SerializedName("otp")
    public String otp = "";

    @SerializedName("otp_date")
    public String otp_date = "";

    @SerializedName("is_confirm")
    public String is_confirm = "";

    @SerializedName("is_login")
    public String is_login = "";

    @SerializedName("is_deleted")
    public String is_deleted = "";

    @SerializedName("status")
    public String status = "";

    @SerializedName("created_date")
    public String created_date = "";

    @SerializedName("modified_date")
    public String modified_date = "";

    @SerializedName("user_id")
    public String user_id = "";

    @SerializedName("order_id")
    public String order_id = "";


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }


    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getImg_path() {
        return img_path;
    }

    public void setImg_path(String img_path) {
        this.img_path = img_path;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_type() {
        return user_type;
    }

    public void setUser_type(String user_type) {
        this.user_type = user_type;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }
}
