package com.android.handyman.model;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class AdvertiseListModel implements Serializable {

	public static final long serialVersionUID = 1L;

	@SerializedName("id")
	public String id = "";
	
	@SerializedName("title")
	public String title = "";
	
	@SerializedName("banner_name")
	public String banner_name = "";
	
	@SerializedName("banner_path")
	public String banner_path = "";
	
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
	
	@SerializedName("count")
	public String count = "";

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getBanner_name() {
		return banner_name;
	}

	public void setBanner_name(String banner_name) {
		this.banner_name = banner_name;
	}

	public String getBanner_path() {
		return banner_path;
	}

	public void setBanner_path(String banner_path) {
		this.banner_path = banner_path;
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

	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
}
