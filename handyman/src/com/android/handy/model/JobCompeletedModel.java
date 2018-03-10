package com.android.handy.model;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class JobCompeletedModel implements Serializable {

	public static final long serialVersionUID = 1L;

	@SerializedName("success")
	public String success = "";

	@SerializedName("message")
	public String message = "";
	
	@SerializedName("job_description")
	public String job_description = "";

	@SerializedName("receiver_name")
	public String receiver_name = "";

	@SerializedName("amount")
	public String amount = "";

	@SerializedName("id")
	public String id = "";

	@SerializedName("img")
	public String img = "";

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

	public String getJob_description() {
		return job_description;
	}

	public void setJob_description(String job_description) {
		this.job_description = job_description;
	}

	public String getReceiver_name() {
		return receiver_name;
	}

	public void setReceiver_name(String receiver_name) {
		this.receiver_name = receiver_name;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}
	

}
