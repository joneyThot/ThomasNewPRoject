package com.android.handyman.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.handyman.R;
import com.android.handyman.logger.Logger;
import com.android.handyman.model.StateModel;
import com.android.handyman.service.Utils;

public class StateDemoAdapter extends BaseAdapter {

	private Context mContext;
	private LayoutInflater infalter;
	private ArrayList<StateModel> stateModels;

	public StateDemoAdapter(Context mContext, ArrayList<StateModel> mStateModel) {
		super();
		this.mContext = mContext;
		this.stateModels = mStateModel;

	}

	@Override
	public int getCount() {
		return stateModels.size();
	}

	@Override
	public Object getItem(int position) {
		return stateModels.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	View vi;

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		vi = convertView;
		if (convertView == null) {
			infalter = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		vi = infalter.inflate(R.layout.row_dialog_spinner, null);

		
		if (Utils.validateString(stateModels.get(position).name)) {
			((TextView) vi.findViewById(R.id.row_items_textview)).setText(stateModels.get(position).name);
			Logger.e("TAG", "STATE ::" + stateModels.get(position).name);
        }

		return vi;
	}

	public int getPosition(int position) {
		// TODO Auto-generated method stub
		return position;
	}
}
