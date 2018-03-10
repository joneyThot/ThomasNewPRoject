package com.handy.model;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class StateModel implements Serializable {

	public static final long serialVersionUID = 1L;

	@SerializedName("id")
	public String id = "";
	
	@SerializedName("parent_id")
	public String parent_id = "";

	@SerializedName("name")
	public String name = "";

	@SerializedName("status")
	public String status = "";
	
	@SerializedName("img")
	public String img = "";
	
	@SerializedName("img_path")
	public String img_path = "";

	@SerializedName("is_deleted")
	public String is_deleted = "";

	@SerializedName("created_date")
	public String created_date = "";

	@SerializedName("modified_date")
	public String modified_date = "";
	
	@SerializedName("count")
	public String count = "";

	public StateModel(String id, String name, String status, String is_deleted,
			String created_date, String modified_date,String count) {
		super();
		this.id = id;
		this.name = name;
		this.status = status;
		this.is_deleted = is_deleted;
		this.created_date = created_date;
		this.modified_date = modified_date;
		this.count = count;
	}

	public StateModel() {
		// TODO Auto-generated constructor stub
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getIs_deleted() {
		return is_deleted;
	}

	public void setIs_deleted(String is_deleted) {
		this.is_deleted = is_deleted;
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

	public String getParent_id() {
		return parent_id;
	}

	public void setParent_id(String parent_id) {
		this.parent_id = parent_id;
	}

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}

	public String getImg_path() {
		return img_path;
	}

	public void setImg_path(String img_path) {
		this.img_path = img_path;
	}

	
}
