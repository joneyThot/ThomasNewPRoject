package com.android.handyman.model;

import java.io.Serializable;
import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;

public class DataModel implements Serializable {

	public static final long serialVersionUID = 1L;

	@SerializedName("success")
	public String success = "";

	@SerializedName("message")
	public String message = "";
	
	@SerializedName("page")
	public String page = "";
	
	@SerializedName("data")
    public ArrayList<AdvertiseListModel> advertiseListModels = new ArrayList<AdvertiseListModel>();
	
	@SerializedName("data")
    public ArrayList<CategoryListModel> categoryListModels = new ArrayList<CategoryListModel>();

	@SerializedName("data")
    public ArrayList<HandymanModel> handymanModels = new ArrayList<HandymanModel>();
	
	@SerializedName("data")
    public ArrayList<HandymanModel> searchHandymanModels = new ArrayList<HandymanModel>();
	
	@SerializedName("data")
    public ArrayList<ProfileHandymanModel> profileHandymanModels = new ArrayList<ProfileHandymanModel>();
	
	@SerializedName("data")
    public ArrayList<MyProfileModel> myProfileModels = new ArrayList<MyProfileModel>();
	
	@SerializedName("data")
	public ArrayList<CreditsModel> creditsModelList = new ArrayList<CreditsModel>();
	
	@SerializedName("data")
	public ArrayList<MyHiringsModel> myHiringsModels = new ArrayList<MyHiringsModel>();
	
	@SerializedName("data")
	public ArrayList<FavouriteHandymanModel> favouriteHandymanModel = new ArrayList<FavouriteHandymanModel>();
	
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
	
	public String getPage() {
		return page;
	}

	public void setPage(String page) {
		this.page = page;
	}

	public ArrayList<AdvertiseListModel> getAdvertiseListModels() {
		return advertiseListModels;
	}

	public void setAdvertiseListModels(
			ArrayList<AdvertiseListModel> advertiseListModels) {
		this.advertiseListModels = advertiseListModels;
	}
	
	public ArrayList<CategoryListModel> getCategoryListModels() {
		return categoryListModels;
	}

	public void setCategoryListModels(
			ArrayList<CategoryListModel> categoryListModels) {
		this.categoryListModels = categoryListModels;
	}

	public ArrayList<HandymanModel> getHandymanModels() {
		return handymanModels;
	}

	public void setHandymanModels(ArrayList<HandymanModel> handymanModels) {
		this.handymanModels = handymanModels;
	}

	public ArrayList<ProfileHandymanModel> getProfileHandymanModels() {
		return profileHandymanModels;
	}

	public void setProfileHandymanModels(
			ArrayList<ProfileHandymanModel> profileHandymanModels) {
		this.profileHandymanModels = profileHandymanModels;
	}

	public ArrayList<MyProfileModel> getMyProfileModels() {
		return myProfileModels;
	}

	public void setMyProfileModels(ArrayList<MyProfileModel> myProfileModels) {
		this.myProfileModels = myProfileModels;
	}

	public ArrayList<CreditsModel> getCreditsModelList() {
		return creditsModelList;
	}

	public void setCreditsModelList(ArrayList<CreditsModel> creditsModelList) {
		this.creditsModelList = creditsModelList;
	}

	public ArrayList<FavouriteHandymanModel> getFavouriteHandymanModel() {
		return favouriteHandymanModel;
	}

	public void setFavouriteHandymanModel(
			ArrayList<FavouriteHandymanModel> favouriteHandymanModel) {
		this.favouriteHandymanModel = favouriteHandymanModel;
	}

	public ArrayList<HandymanModel> getSearchHandymanModels() {
		return searchHandymanModels;
	}

	public void setSearchHandymanModels(
			ArrayList<HandymanModel> searchHandymanModels) {
		this.searchHandymanModels = searchHandymanModels;
	}

	public ArrayList<MyHiringsModel> getMyHiringsModels() {
		return myHiringsModels;
	}

	public void setMyHiringsModels(ArrayList<MyHiringsModel> myHiringsModels) {
		this.myHiringsModels = myHiringsModels;
	}
	
	
}
