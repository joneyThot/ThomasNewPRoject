package com.android.handy.model;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class CheckHandymanRatingModel implements Serializable {

	public static final long serialVersionUID = 1L;

	@SerializedName("success")
	public String success = "";

	@SerializedName("message")
	public String message = "";
	
	@SerializedName("data")
	public CheckHandymanRatingInnerModel user = new CheckHandymanRatingInnerModel();

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

}
