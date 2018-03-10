package com.handy.model;

import java.io.Serializable;
import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;

public class DataModel implements Serializable {

	public static final long serialVersionUID = 1L;

	@SerializedName("success")
	public String success = "";

	@SerializedName("message")
	public String message = "";
	
//	@SerializedName("data")
//    public ArrayList<ProfileHandymanModel> profileHandymanModels = new ArrayList<ProfileHandymanModel>();
	
	@SerializedName("data")
    public ArrayList<MyProfileModel> myProfileModels = new ArrayList<MyProfileModel>();
	
//	@SerializedName("data")
//    public ArrayList<MyHiringsModel> myHiringsModels = new ArrayList<MyHiringsModel>();
	
	@SerializedName("data")
    public ArrayList<MyHiringsModel> myOrderList = new ArrayList<MyHiringsModel>();
	
	@SerializedName("data")
    public ArrayList<ReviewRateModel> reviewRateModels = new ArrayList<ReviewRateModel>();

	@SerializedName("data")
	public ArrayList<HandymanCreditsModel> handymanCreditsModels = new ArrayList<HandymanCreditsModel>();
	
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
	
	public ArrayList<MyProfileModel> getMyProfileModels() {
		return myProfileModels;
	}

	public void setMyProfileModels(ArrayList<MyProfileModel> myProfileModels) {
		this.myProfileModels = myProfileModels;
	}

//	public ArrayList<MyHiringsModel> getMyHiringsModels() {
//		return myHiringsModels;
//	}
//
//	public void setMyHiringsModels(ArrayList<MyHiringsModel> myHiringsModels) {
//		this.myHiringsModels = myHiringsModels;
//	}

	public ArrayList<MyHiringsModel> getMyOrderList() {
		return myOrderList;
	}

	public void setMyOrderList(ArrayList<MyHiringsModel> myOrderList) {
		this.myOrderList = myOrderList;
	}

	public ArrayList<ReviewRateModel> getReviewRateModels() {
		return reviewRateModels;
	}

	public void setReviewRateModels(ArrayList<ReviewRateModel> reviewRateModels) {
		this.reviewRateModels = reviewRateModels;
	}

	public ArrayList<HandymanCreditsModel> getHandymanCreditsModels() {
		return handymanCreditsModels;
	}

	public void setHandymanCreditsModels(ArrayList<HandymanCreditsModel> handymanCreditsModels) {
		this.handymanCreditsModels = handymanCreditsModels;
	}
}
