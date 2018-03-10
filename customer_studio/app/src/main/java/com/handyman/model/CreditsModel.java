package com.handyman.model;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class CreditsModel implements Serializable {

	public static final long serialVersionUID = 1L;
	
	@SerializedName("success")
	public String success = "";
	
	@SerializedName("message")
	public String message = "";

	@SerializedName("id")
	public String id = "";

	@SerializedName("client_id")
	public String client_id = "";
	
	@SerializedName("total")
	public String total = "";
	
	@SerializedName("credit")
	public String credit = "";
	
	@SerializedName("debit")
	public String debit = "";
	
	@SerializedName("status")
	public String status = "";

	@SerializedName("is_deleted")
	public String is_deleted = "";

	@SerializedName("created_date")
	public String created_date = "";

	@SerializedName("modified_date")
	public String modified_date = "";
	
	@SerializedName("total_amount")
	public String total_amount = "";

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getClient_id() {
		return client_id;
	}

	public void setClient_id(String client_id) {
		this.client_id = client_id;
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

	public String getDebit() {
		return debit;
	}

	public void setDebit(String debit) {
		this.debit = debit;
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

	public String getTotal_amount() {
		return total_amount;
	}

	public void setTotal_amount(String total_amount) {
		this.total_amount = total_amount;
	}
	
	
	

}
