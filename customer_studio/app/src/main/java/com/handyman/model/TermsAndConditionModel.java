package com.handyman.model;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class TermsAndConditionModel implements Serializable {

	public static final long serialVersionUID = 1L;

	@SerializedName("success")
	public String success = "";

	@SerializedName("message")
	public String message = "";

	@SerializedName("id")
	public String id = "";

	@SerializedName("lat")
	public String lat = "";

	@SerializedName("lng")
	public String lng = "";

	@SerializedName("category")
	public String category = "";

	@SerializedName("sub_category")
	public String sub_category = "";

	@SerializedName("cityname")
	public String cityname = "";

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

	public String getCityname() {
		return cityname;
	}

	public void setCityname(String cityname) {
		this.cityname = cityname;
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
}
