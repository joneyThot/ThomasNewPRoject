package com.handyman.model;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class AdvertiseListModel implements Serializable {

	public static final long serialVersionUID = 1L;

	@SerializedName("success")
	public String success = "";

	@SerializedName("message")
	public String message = "";

	@SerializedName("id")
	public String id = "";

	@SerializedName("users_id")
	public String users_id = "";

	@SerializedName("banner_id")
	public String banner_id = "";

	@SerializedName("is_sent")
	public String is_sent = "";
	
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

	@SerializedName("is_read")
	public String is_read = "";
	
	@SerializedName("count")
	public String count = "";

	@SerializedName("option_value")
	public String option_value = "";

	@SerializedName("data")
	public String data = "";

	@SerializedName("domain_name")
	public String domain_name = "";

	@SerializedName("image_url")
	public String image_url = "";

	public boolean selection = false;


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

	public boolean isSelection() {
		return selection;
	}

	public void setSelection(boolean selection) {
		this.selection = selection;
	}

	public String getIs_read() {
		return is_read;
	}

	public void setIs_read(String is_read) {
		this.is_read = is_read;
	}

	public String getOption_value() {
		return option_value;
	}

	public void setOption_value(String option_value) {
		this.option_value = option_value;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getUsers_id() {
		return users_id;
	}

	public void setUsers_id(String users_id) {
		this.users_id = users_id;
	}

	public String getBanner_id() {
		return banner_id;
	}

	public void setBanner_id(String banner_id) {
		this.banner_id = banner_id;
	}

	public String getIs_sent() {
		return is_sent;
	}

	public void setIs_sent(String is_sent) {
		this.is_sent = is_sent;
	}

	public String getDomain_name() {
		return domain_name;
	}

	public void setDomain_name(String domain_name) {
		this.domain_name = domain_name;
	}

	public String getImage_url() {
		return image_url;
	}

	public void setImage_url(String image_url) {
		this.image_url = image_url;
	}
}
