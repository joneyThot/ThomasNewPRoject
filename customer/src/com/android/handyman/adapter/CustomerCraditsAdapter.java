package com.android.handyman.adapter;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.handyman.R;
import com.android.handyman.model.CreditsModel;
import com.android.handyman.model.StateModel;
import com.android.handyman.service.Utils;

public class CustomerCraditsAdapter extends BaseAdapter {

	private Context mContext;
	private LayoutInflater infalter;
	private ArrayList<CreditsModel> mCreditsModelList;
	private OnClickListener mListClickListener;

	public CustomerCraditsAdapter(Context mContext, ArrayList<CreditsModel> mCreditsModelList, OnClickListener mListClickListener) {
		super();
		this.mContext = mContext;
		this.mCreditsModelList = mCreditsModelList;
		this.mListClickListener = mListClickListener;

	}

	@Override
	public int getCount() {
		return mCreditsModelList.size();
	}
	
	@Override
	public Object getItem(int position) {
		return mCreditsModelList.get(position);
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
		vi = infalter.inflate(R.layout.row_credits, null);
		
		String year = "",month = "", day = "";
		if(Utils.validateString(mCreditsModelList.get(position).getCreated_date())){
			String created_date = mCreditsModelList.get(position).getCreated_date();
			year = created_date.substring(0, 4);
			month = created_date.substring(5,7);
			day = created_date.substring(8,10);
		}
		
		if(Utils.validateString(mCreditsModelList.get(position).getCredit())){
			String credit = mCreditsModelList.get(position).getCredit();
			if(!credit.equalsIgnoreCase("0")){
				((TextView) vi.findViewById(R.id.row_cradits_textview)).setText("Earned" + " " + credit + " " + "Credits");
				((TextView) vi.findViewById(R.id.row_cradits_date_textview)).setText(day + "/" + month + "/" + year);
				vi.findViewById(R.id.row_cradits_date_textview).setBackgroundResource(R.drawable.credit_green_bg);
			}
		}
		
		if(Utils.validateString(mCreditsModelList.get(position).getDebit())){
			String debit = mCreditsModelList.get(position).getDebit();
			if(!debit.equalsIgnoreCase("0")){
				((TextView) vi.findViewById(R.id.row_cradits_textview)).setText("Spent" + " " + debit + " " + "Credits");
				((TextView) vi.findViewById(R.id.row_cradits_date_textview)).setText(day + "/" + month + "/" + year);
				vi.findViewById(R.id.row_cradits_date_textview).setBackgroundResource(R.drawable.credit_blue_bg);
			}
		}
		
		vi.findViewById(R.id.row_cradits_remove_img).setTag(position);
		vi.findViewById(R.id.row_cradits_remove_img).setOnClickListener(mListClickListener);
		
		return vi; 
	}

}
