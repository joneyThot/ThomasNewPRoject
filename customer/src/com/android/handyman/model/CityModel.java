package com.android.handyman.model;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class CityModel implements Serializable {

	public static final long serialVersionUID = 1L;

	@SerializedName("id")
	public String id = "";

	@SerializedName("state_id")
	public String state_id = "";
	
	@SerializedName("name")
	public String name = "";

	@SerializedName("status")
	public String status = "";

	@SerializedName("is_deleted")
	public String is_deleted = "";

	@SerializedName("created_date")
	public String created_date = "";

	@SerializedName("modified_date")
	public String modified_date = "";
	
	@SerializedName("count")
	public String count = "";

	public CityModel(String id, String state_id, String name, String status, String is_deleted,
			String created_date, String modified_date, String count) {
		super();
		this.id = id;
		this.state_id = state_id;
		this.name = name;
		this.status = status;
		this.is_deleted = is_deleted;
		this.created_date = created_date;
		this.modified_date = modified_date;
		this.count = count;
	}

}
