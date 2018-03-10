package com.handyman.model;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class CheckHandymanRatingInnerModel implements Serializable {

	public static final long serialVersionUID = 1L;

	@SerializedName("id")
	public String id = "";

	@SerializedName("handyman_id")
	public String handyman_id = "";

	@SerializedName("client_id")
	public String client_id = "";

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


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getHandyman_id() {
		return handyman_id;
	}

	public void setHandyman_id(String handyman_id) {
		this.handyman_id = handyman_id;
	}

	public String getClient_id() {
		return client_id;
	}

	public void setClient_id(String client_id) {
		this.client_id = client_id;
	}

	public String getRate() {
		return rate;
	}

	public void setRate(String rate) {
		this.rate = rate;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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
	
	

}
