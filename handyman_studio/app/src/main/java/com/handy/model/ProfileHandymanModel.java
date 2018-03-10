package com.handy.model;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class ProfileHandymanModel implements Serializable {

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

	@SerializedName("img")
	public String img = "";

	@SerializedName("img_path")
	public String img_path = "";

	@SerializedName("otp")
	public String otp = "";

	@SerializedName("otp_date")
	public String otp_date = "";

	@SerializedName("is_deleted")
	public String is_deleted = "";

	@SerializedName("status")
	public String status = "";

	@SerializedName("created_date")
	public String created_date = "";

	@SerializedName("modified_date")
	public String modified_date = "";
	
	@SerializedName("city_name")
	public String city_name = "";
	
	@SerializedName("state_name")
	public String state_name = "";
	
	@SerializedName("rating")
	public String rating = "";
	
	@SerializedName("like")
	public String like = "";

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

	public String getUser_type() {
		return user_type;
	}

	public void setUser_type(String user_type) {
		this.user_type = user_type;
	}

	public String getJob_list() {
		return job_list;
	}

	public void setJob_list(String job_list) {
		this.job_list = job_list;
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

	public String getQualification() {
		return qualification;
	}

	public void setQualification(String qualification) {
		this.qualification = qualification;
	}

	public String getExperience() {
		return experience;
	}

	public void setExperience(String experience) {
		this.experience = experience;
	}

	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
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

	public String getOtp() {
		return otp;
	}

	public void setOtp(String otp) {
		this.otp = otp;
	}

	public String getOtp_date() {
		return otp_date;
	}

	public void setOtp_date(String otp_date) {
		this.otp_date = otp_date;
	}

	public String getIs_deleted() {
		return is_deleted;
	}

	public void setIs_deleted(String is_deleted) {
		this.is_deleted = is_deleted;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCreated_date() {
		return created_date;
	}

	public void setCreated_date(String created_date) {
		this.created_date = created_date;
	}

	public String getModified_date() {
		return modified_date;
	}

	public void setModified_date(String modified_date) {
		this.modified_date = modified_date;
	}

	public String getCity_name() {
		return city_name;
	}

	public void setCity_name(String city_name) {
		this.city_name = city_name;
	}

	public String getState_name() {
		return state_name;
	}

	public void setState_name(String state_name) {
		this.state_name = state_name;
	}

	public String getLike() {
		return like;
	}

	public void setLike(String like) {
		this.like = like;
	}

	public String getRating() {
		return rating;
	}

	public void setRating(String rating) {
		this.rating = rating;
	}
	
	
	
}
