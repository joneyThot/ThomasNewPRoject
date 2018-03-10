package com.android.handy.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.handy.R;
import com.android.handy.model.JobdescriptionModel;
import com.android.handy.service.Utils;

public class JobDescriptionAdapter extends BaseAdapter {

	private Context mContext;
	private LayoutInflater infalter;
	private ArrayList<JobdescriptionModel> jobdescriptionModels;

	public JobDescriptionAdapter(Context mContext, ArrayList<JobdescriptionModel> jobdescriptionModels) {
		super();
		this.mContext = mContext;
		this.jobdescriptionModels = jobdescriptionModels;

	}

	@Override
	public int getCount() {
		return jobdescriptionModels.size();
	}

	@Override
	public Object getItem(int position) {
		return jobdescriptionModels.get(position);
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

		
		if (Utils.validateString(jobdescriptionModels.get(position).category) && Utils.validateString(jobdescriptionModels.get(position).subcategory)) {
			((TextView) vi.findViewById(R.id.row_items_textview)).setText(jobdescriptionModels.get(position).category + " - " + jobdescriptionModels.get(position).subcategory);
        }

		return vi;
	}

	public int getPosition(int position) {
		// TODO Auto-generated method stub
		return position;
	}
}
