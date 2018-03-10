package com.handy.model;

import java.io.Serializable;
import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;

public class ApplyDiscountModel implements Serializable {

	public static final long serialVersionUID = 1L;

	@SerializedName("success")
	public String success = "";

	@SerializedName("message")
	public String message = "";
	
	@SerializedName("data")
    public ArrayList<ApplyDiscountCreditsModel> applyDiscountCreditsModels = new ArrayList<ApplyDiscountCreditsModel>();
	

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

	public ArrayList<ApplyDiscountCreditsModel> getApplyDiscountCreditsModels() {
		return applyDiscountCreditsModels;
	}

	public void setApplyDiscountCreditsModels(
			ArrayList<ApplyDiscountCreditsModel> applyDiscountCreditsModels) {
		this.applyDiscountCreditsModels = applyDiscountCreditsModels;
	}
	
	


}
