package com.handy.model;

import java.io.Serializable;
import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;

public class MyCollectionModelList implements Serializable {

	public static final long serialVersionUID = 1L;

	@SerializedName("success")
	public String success = "";
	
	@SerializedName("message")
	public String message = "";
	
	@SerializedName("data")
	public ArrayList<MyCollectionModel>  collectionModelsList = new ArrayList<MyCollectionModel>() ;
	
	@SerializedName("totalamount")
	public String totalamount = "";
	
	public String date = "";
	
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

	public ArrayList<MyCollectionModel> getCollectionModelsList() {
		return collectionModelsList;
	}

	public void setCollectionModelsList(
			ArrayList<MyCollectionModel> collectionModelsList) {
		this.collectionModelsList = collectionModelsList;
	}

	public String getTotalamount() {
		return totalamount;
	}

	public void setTotalamount(String totalamount) {
		this.totalamount = totalamount;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}
	
}
