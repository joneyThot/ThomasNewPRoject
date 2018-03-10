package com.android.handyman.model;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class HandymanModel implements Serializable {

	public static final long serialVersionUID = 1L;

	@SerializedName("success")
	public String success = "";
	
	@SerializedName("message")
	public String message = "";
	
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

	@SerializedName("dob")
	public String dob = "";

	@SerializedName("email")
	public String email = "";
	
	@SerializedName("whatsapp_id")
	public String whatsapp_id = "";

	@SerializedName("lat")
	public String lat = "";

	@SerializedName("lng")
	public String lng = "";

	@SerializedName("img")
	public String img = "";
	
	@SerializedName("img_path")
	public String img_path = "";

	@SerializedName("distance")
	public String distance = "";

	@SerializedName("rating")
	public String rating = "";
	
	@SerializedName("busy")
	public String busy = "";
	
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

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getDob() {
		return dob;
	}

	public void setDob(String dob) {
		this.dob = dob;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getWhatsapp_id() {
		return whatsapp_id;
	}

	public void setWhatsapp_id(String whatsapp_id) {
		this.whatsapp_id = whatsapp_id;
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

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}

	public String getImg_path() {
		return img_path;
	}

	public void setImg_path(String img_path) {
		this.img_path = img_path;
	}

	public String getDistance() {
		return distance;
	}

	public void setDistance(String distance) {
		this.distance = distance;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUser_type() {
		return user_type;
	}

	public void setUser_type(String user_type) {
		this.user_type = user_type;
	}

	public String getRating() {
		return rating;
	}

	public void setRating(String rating) {
		this.rating = rating;
	}

	public String getBusy() {
		return busy;
	}

	public void setBusy(String busy) {
		this.busy = busy;
	}
	
	

}
