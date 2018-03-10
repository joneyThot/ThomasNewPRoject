package com.android.handy.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.handy.R;
import com.android.handy.logger.Logger;
import com.android.handy.model.CityModel;
import com.android.handy.service.Utils;

public class CityAdapter extends BaseAdapter {

	private Context mContext;
	private LayoutInflater infalter;
	private ArrayList<CityModel> mCityModel;

	public CityAdapter(Context mContext, ArrayList<CityModel> mCityModel) {
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

		if (Utils.validateString(mCityModel.get(position).name)) {
			((TextView) vi.findViewById(R.id.row_items_textview)).setText(mCityModel.get(position).name);
			Logger.e("TAG", "CITY ::" + mCityModel.get(position).name);
        }

		return vi;
	}

	public int getPosition(int position) {
		// TODO Auto-generated method stub
		return position;
	}
}
