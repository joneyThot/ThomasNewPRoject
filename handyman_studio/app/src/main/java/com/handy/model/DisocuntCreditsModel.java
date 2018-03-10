package com.handy.model;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class DisocuntCreditsModel implements Serializable {

	public static final long serialVersionUID = 1L;

	@SerializedName("success")
	public String success = "";

	@SerializedName("message")
	public String message = "";
	
	@SerializedName("total")
	public String total = "";
	
	@SerializedName("credit")
	public String credit = "";
	
	@SerializedName("discount")
	public String discount = "";
	

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

	public String getTotal() {
		return total;
	}

	public void setTotal(String total) {
		this.total = total;
	}

	public String getCredit() {
		return credit;
	}

	public void setCredit(String credit) {
		this.credit = credit;
	}

	public String getDiscount() {
		return discount;
	}

	public void setDiscount(String discount) {
		this.discount = discount;
	}
	

}
