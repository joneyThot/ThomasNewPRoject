package com.handyman.model;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class HireHandymanModel implements Serializable {

    public static final long serialVersionUID = 1L;

    @SerializedName("success")
    public String success = "";

    @SerializedName("message")
    public String message = "";

    @SerializedName("handyman_id")
    public String handyman_id = "";

    @SerializedName("client_id")
    public String client_id = "";

    @SerializedName("job_description")
    public String job_description = "";

    @SerializedName("appointment_date")
    public String appointment_date = "";

    @SerializedName("appointment_time")
    public String appointment_time = "";

    @SerializedName("contact_person")
    public String contact_person = "";

    @SerializedName("contact_no")
    public String contact_no = "";

    @SerializedName("comment")
    public String comment = "";

    @SerializedName("hire_status")
    public String hire_status = "";

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

    @SerializedName("lat")
    public String lat = "";

    @SerializedName("lng")
    public String lng = "";

    @SerializedName("category")
    public String category = "";

    @SerializedName("sub_category")
    public String sub_category = "";

    @SerializedName("debit")
    public String debit = "";


    public String getHandyman_id() {
        return handyman_id;
    }

    public void setHandyman_id(String handyman_id) {
        this.handyman_id = handyman_id;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getJob_description() {
        return job_description;
    }

    public void setJob_description(String job_description) {
        this.job_description = job_description;
    }

    public String getAppointment_date() {
        return appointment_date;
    }

    public void setAppointment_date(String appointment_date) {
        this.appointment_date = appointment_date;
    }

    public String getAppointment_time() {
        return appointment_time;
    }

    public void setAppointment_time(String appointment_time) {
        this.appointment_time = appointment_time;
    }

    public String getContact_person() {
        return contact_person;
    }

    public void setContact_person(String contact_person) {
        this.contact_person = contact_person;
    }

    public String getContact_no() {
        return contact_no;
    }

    public void setContact_no(String contact_no) {
        this.contact_no = contact_no;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getHire_status() {
        return hire_status;
    }

    public void setHire_status(String hire_status) {
        this.hire_status = hire_status;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getLandmark() {
        return landmark;
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSub_category() {
        return sub_category;
    }

    public void setSub_category(String sub_category) {
        this.sub_category = sub_category;
    }

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDebit() {
        return debit;
    }

    public void setDebit(String debit) {
        this.debit = debit;
    }
}
