package com.handyman.model;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class JobdescriptionModel implements Serializable {

	public static final long serialVersionUID = 1L;

	@SerializedName("category_id")
	public String category_id = "";

	@SerializedName("category")
	public String category = "";
	
	@SerializedName("sub_category_id")
	public String sub_category_id = "";

	@SerializedName("subcategory")
	public String subcategory = "";
	
	public JobdescriptionModel(String category_id, String category,	String sub_category_id, String subcategory) {
		super();
		this.category_id = category_id;
		this.category = category;
		this.sub_category_id = sub_category_id;
		this.subcategory = subcategory;
	}

	
	
}
