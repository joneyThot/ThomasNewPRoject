package com.android.handyman.model;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class SubCategoryModel implements Serializable {

	public static final long serialVersionUID = 1L;

	@SerializedName("sub_category_id")
	public String sub_category_id = "";

	@SerializedName("sub_category_name")
	public String sub_category_name = "";

	public String getSub_category_id() {
		return sub_category_id;
	}

	public void setSub_category_id(String sub_category_id) {
		this.sub_category_id = sub_category_id;
	}

	public String getSub_category_name() {
		return sub_category_name;
	}

	public void setSub_category_name(String sub_category_name) {
		this.sub_category_name = sub_category_name;
	}
	
	
}
