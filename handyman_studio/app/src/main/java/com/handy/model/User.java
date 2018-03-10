package com.handy.model;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class User implements Serializable {

	private static final long serialVersionUID = 1L;

	@SerializedName("id")
	public String id = "";
	
	@SerializedName("user_id")
	public String user_id = "";
	
	@SerializedName("otp")
	public String otp = "";
	
	@SerializedName("mobile")
	public String mobile = "";
	
	@SerializedName("email")
	public String email = "";
	
	@SerializedName("user_type")
	public String user_type = "";
	
	@SerializedName("firstname")
	public String firstname = "";

	@SerializedName("lastname")
	public String lastname = "";
	
	@SerializedName("img_path")
	public String img_path = "";
	
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
	
	
	
}
