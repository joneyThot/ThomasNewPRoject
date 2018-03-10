package com.handy.model;

import java.io.Serializable;
import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;

public class ApplyDiscountCreditsModel implements Serializable {

	public static final long serialVersionUID = 1L;

	@SerializedName("discount")
    public ArrayList<DisocuntCreditsModel> mDiscountModel = new ArrayList<DisocuntCreditsModel>();
	
	@SerializedName("credit")
    public ArrayList<DisocuntCreditsModel> mCreditModel = new ArrayList<DisocuntCreditsModel>();

	public ArrayList<DisocuntCreditsModel> getmDiscountModel() {
		return mDiscountModel;
	}

	public void setmDiscountModel(ArrayList<DisocuntCreditsModel> mDiscountModel) {
		this.mDiscountModel = mDiscountModel;
	}

	public ArrayList<DisocuntCreditsModel> getmCreditModel() {
		return mCreditModel;
	}

	public void setmCreditModel(ArrayList<DisocuntCreditsModel> mCreditModel) {
		this.mCreditModel = mCreditModel;
	}
	
}
