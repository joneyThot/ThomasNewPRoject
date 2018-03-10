package com.android.handy.model;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class RegisterModel implements Serializable {

	public static final long serialVersionUID = 1L;

	@SerializedName("success")
	public String success = "";
	
	@SerializedName("message")
	public String message = "";
	
	@SerializedName("id")
	public String id = "";
	
	@SerializedName("mobile")
	public String mobile = "";
	
	@SerializedName("phone")
	public String phone = "";

	@SerializedName("firstname")
	public String firstname = "";

	@SerializedName("lastname")
	public String lastname = "";

	@SerializedName("gender")
	public String gender = "";
	
	@SerializedName("dob")
	public String dob = "";

	@SerializedName("email")
	public String email = "";
	
	@SerializedName("whatsapp_id")
	public String whatsapp_id = "";

	@SerializedName("password")
	public String password = "";

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

	@SerializedName("img")
	public String img = "";
	
	@SerializedName("img_path")
	public String img_path = "";

	@SerializedName("data")
	public User user = new User();

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

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
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

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
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

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	
}
