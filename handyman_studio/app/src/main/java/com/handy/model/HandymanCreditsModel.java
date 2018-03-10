package com.handy.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class HandymanCreditsModel implements Serializable {

	public static final long serialVersionUID = 1L;

	@SerializedName("success")
	public String success = "";
	
	@SerializedName("message")
	public String message = "";

	@SerializedName("id")
	public String id = "";

	@SerializedName("handyman_id")
	public String handyman_id = "";

	@SerializedName("credit_use_for_convert")
	public String credit_use_for_convert = "";

	@SerializedName("total_cash")
	public String total_cash = "";

	@SerializedName("is_requested")
	public String is_requested = "";

	@SerializedName("total_hm_credit")
	public String total_hm_credit = "";

	@SerializedName("total")
	public String total = "";

	@SerializedName("status")
	public String status = "";

	@SerializedName("is_deleted")
	public String is_deleted = "";

	@SerializedName("created_date")
	public String created_date = "";

	@SerializedName("modified_date")
	public String modified_date = "";

	@SerializedName("coins")
	public String coins = "";


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

	public String getHandyman_id() {
		return handyman_id;
	}

	public void setHandyman_id(String handyman_id) {
		this.handyman_id = handyman_id;
	}

	public String getCredit_use_for_convert() {
		return credit_use_for_convert;
	}

	public void setCredit_use_for_convert(String credit_use_for_convert) {
		this.credit_use_for_convert = credit_use_for_convert;
	}

	public String getTotal_cash() {
		return total_cash;
	}

	public void setTotal_cash(String total_cash) {
		this.total_cash = total_cash;
	}

	public String getIs_requested() {
		return is_requested;
	}

	public void setIs_requested(String is_requested) {
		this.is_requested = is_requested;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTotal_hm_credit() {
		return total_hm_credit;
	}

	public void setTotal_hm_credit(String total_hm_credit) {
		this.total_hm_credit = total_hm_credit;
	}

	public String getTotal() {
		return total;
	}

	public void setTotal(String total) {
		this.total = total;
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

	public String getCoins() {
		return coins;
	}

	public void setCoins(String coins) {
		this.coins = coins;
	}
}
