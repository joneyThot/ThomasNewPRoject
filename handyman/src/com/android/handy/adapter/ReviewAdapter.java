package com.android.handy.adapter;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.handy.R;
import com.android.handy.model.ReviewRateModel;
import com.android.handy.service.Utils;

public class ReviewAdapter extends BaseAdapter{

	private Context mContext;
	private LayoutInflater infalter;
	private ArrayList<ReviewRateModel> mReviewRateModels;
	private OnClickListener onClickListener;

	public ReviewAdapter(Context mContext, ArrayList<ReviewRateModel> mReviewRateModels,OnClickListener onClickListener) {
		super();
		this.mContext = mContext;
		this.mReviewRateModels = mReviewRateModels;
		this.onClickListener = onClickListener;

	}

	@Override
	public int getCount() {
		return mReviewRateModels.size();
	}
	
	@Override
	public Object getItem(int position) {
		return mReviewRateModels.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	View vi ;
	@SuppressLint("ViewHolder") @Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		vi = convertView;
		if(convertView == null){
			infalter = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		vi = infalter.inflate(R.layout.row_review, null);

		if(Utils.validateString(mReviewRateModels.get(position).description)){
			((TextView) vi.findViewById(R.id.review_description)).setText(mReviewRateModels.get(position).description);
		} 
		
		if(Utils.validateString(mReviewRateModels.get(position).created_date)){
			((TextView) vi.findViewById(R.id.review_date)).setText(mReviewRateModels.get(position).created_date);
		} 
		
		if(Utils.validateString(mReviewRateModels.get(position).rate)){
			float rate = Float.parseFloat(mReviewRateModels.get(position).rate);
			((RatingBar) vi.findViewById(R.id.review_rating)).setRating(rate);
		}
		
		vi.setTag(position);
		vi.setOnClickListener(onClickListener);
		
		return vi; 
	}
	
	public void setList(ArrayList<ReviewRateModel> mTempCustomerList) {
		
		
	}

}
