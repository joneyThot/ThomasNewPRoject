package com.handyman.model;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class ReviewRateModel implements Serializable {

	public static final long serialVersionUID = 1L;

	@SerializedName("id")
	public String id = "";

	@SerializedName("handyman_id")
	public String handyman_id = "";

	@SerializedName("client_id")
	public String client_id = "";

	@SerializedName("hire_id")
	public String hire_id = "";

	@SerializedName("rate")
	public String rate = "";

	@SerializedName("description")
	public String description = "";

	@SerializedName("is_deleted")
	public String is_deleted = "";

	@SerializedName("status")
	public String status = "";

	@SerializedName("created_date")
	public String created_date = "";

	@SerializedName("modified_date")
	public String modified_date = "";

}
