package com.handyman.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class ContactsSupportModel implements Serializable {

	public static final long serialVersionUID = 1L;

	@SerializedName("success")
	public String success = "";
	
	@SerializedName("message")
	public String message = "";

	@SerializedName("email")
	public ArrayList<String> email = new ArrayList<>();

	@SerializedName("phone")
	public ArrayList<String> phone = new ArrayList<>();

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

	public ArrayList<String> getEmail() {
		return email;
	}

	public void setEmail(ArrayList<String> email) {
		this.email = email;
	}

	public ArrayList<String> getPhone() {
		return phone;
	}

	public void setPhone(ArrayList<String> phone) {
		this.phone = phone;
	}
}
