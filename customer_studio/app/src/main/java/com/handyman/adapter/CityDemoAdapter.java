package com.handyman.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.handyman.R;
import com.handyman.logger.Logger;
import com.handyman.model.CityModel;
import com.handyman.service.Utils;

public class CityDemoAdapter extends BaseAdapter {

	private Context mContext;
	private LayoutInflater infalter;
	private ArrayList<CityModel> mCityModel;

	public CityDemoAdapter(Context mContext, ArrayList<CityModel> mCityModel) {
		super();
		this.mContext = mContext;
		this.mCityModel = mCityModel;

	}

	@Override
	public int getCount() {
		return mCityModel.size();
	}

	@Override
	public Object getItem(int position) {
		return mCityModel.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	View vi;

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		vi = convertView;
		if (convertView == null) {
			infalter = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		vi = infalter.inflate(R.layout.row_dialog_spinner, null);

//		if(position == 5){
//			position = position - 1;
//			if (Utils.validateString(mCityModel.get(position).name)) {
//				((TextView) vi.findViewById(R.id.row_items_textview)).setText(mCityModel.get(position).name);
//				Logger.e("TAG", "CITY ::" + mCityModel.get(position).name);
//	        }
//		} else {
			if (Utils.validateString(mCityModel.get(position).name)) {
				((TextView) vi.findViewById(R.id.row_items_textview)).setText(mCityModel.get(position).name);
				Logger.e("TAG", "CITY ::" + mCityModel.get(position).name);
	        }	
//		}
//		
//		int pos = Integer.parseInt(mCityModel.get(position).count);
		
		

		return vi;
	}

	public int getPosition(int position) {
		
		return position;
	}
	
//	public String getName(String name) {
//		
//		return name;
//	}
	
	
	
}
