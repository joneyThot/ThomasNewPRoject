package com.android.handyman.adapter;
import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.handyman.R;
import com.android.handyman.model.CategoryListModel;
import com.android.handyman.model.StateModel;
import com.android.handyman.service.Utils;

public class SubCategoryAdapter extends BaseAdapter{

	private Context mContext;
	private LayoutInflater infalter;
	private ArrayList<CategoryListModel> mSubCategoryList;
	private OnClickListener mListClickListener;

	public SubCategoryAdapter(Context mContext, ArrayList<CategoryListModel> mSubCategoryList, OnClickListener mListClickListener) {
		super();
		this.mContext = mContext;
		this.mSubCategoryList = mSubCategoryList;
		this.mListClickListener = mListClickListener;

	}

	@Override
	public int getCount() {
		return mSubCategoryList.size();
	}
	
	@Override
	public Object getItem(int position) {
		return mSubCategoryList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	View vi ;
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		vi = convertView;
		if(convertView == null){
			infalter = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		vi = infalter.inflate(R.layout.row_subcategory, null);

		if(Utils.validateString(mSubCategoryList.get(position).name)){
			((TextView) vi.findViewById(R.id.row_sub_category_textview)).setText(mSubCategoryList.get(position).name);
		} 
		
		vi.setTag(position);
		vi.setOnClickListener(mListClickListener);
		return vi; 
	}
}
