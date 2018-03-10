package com.handy.adapter;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.handy.R;
import com.handy.model.MyCollectionModel;
import com.handy.service.Utils;

public class HandymanCollectionAdapter extends BaseAdapter{

	private Context mContext;
	private LayoutInflater infalter;
	ArrayList<MyCollectionModel> collectionModelsList ;
	private OnClickListener mListClickListener;
//	String date = "";
	
	public HandymanCollectionAdapter(Context mContext, ArrayList<MyCollectionModel> collectionModelsList , OnClickListener mListClickListener) {
		super();
		this.mContext = mContext;
		this.collectionModelsList = collectionModelsList;
		this.mListClickListener = mListClickListener;

	}

//	public HandymanCollectionAdapter(Context mContext, ArrayList<MyCollectionModel> collectionModelsList , OnClickListener mListClickListener, String date) {
//		super();
//		this.mContext = mContext;
//		this.collectionModelsList = collectionModelsList;
//		this.mListClickListener = mListClickListener;
//		this.date = date;
//
//	}
	
	@Override
	public int getCount() {
		return collectionModelsList.size();
	}
	
	@Override
	public Object getItem(int position) {
		return collectionModelsList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	View vi ;
	@SuppressLint({ "ViewHolder", "SimpleDateFormat" }) @Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		vi = convertView;
		if(convertView == null){
			infalter = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		vi = infalter.inflate(R.layout.row_handyman_collection, null);
		
		if(Utils.validateString(collectionModelsList.get(position).dateMain)){
			vi.findViewById(R.id.date_header).setVisibility(View.VISIBLE);
			vi.findViewById(R.id.lv1).setVisibility(View.GONE);
			
			((TextView) vi.findViewById(R.id.date_header)).setText(collectionModelsList.get(position).dateMain);
		} else {
			vi.findViewById(R.id.date_header).setVisibility(View.GONE);
			vi.findViewById(R.id.lv1).setVisibility(View.VISIBLE);
		}
		
		
		if(Utils.validateString(collectionModelsList.get(position).client_name)){
			((TextView) vi.findViewById(R.id.collection_customer_name_txt)).setText(collectionModelsList.get(position).client_name);
		} 
		
		if(Utils.validateString(collectionModelsList.get(position).comment)){
			((TextView) vi.findViewById(R.id.collection_hire_for_txt)).setText(collectionModelsList.get(position).comment);
		} 
		
		if(Utils.validateString(collectionModelsList.get(position).category_name)){
			((TextView) vi.findViewById(R.id.collection_category_txt)).setText(collectionModelsList.get(position).category_name);
		} 
		
		if(Utils.validateString(collectionModelsList.get(position).subcategory_name)){
			((TextView) vi.findViewById(R.id.collection_sub_category_txt)).setText(collectionModelsList.get(position).subcategory_name);
		} 
		
		
		if(Utils.validateString(collectionModelsList.get(position).appointment_time)){
			String time = collectionModelsList.get(position).appointment_time.toString();
			((TextView) vi.findViewById(R.id.collection_time_text)).setText(time.substring(0, 2)+ ":"+time.substring(3, 5)+"" + time.subSequence(8, 11));
	         
		} 
		
		if(Utils.validateString(collectionModelsList.get(position).total)){
			((TextView) vi.findViewById(R.id.collection_rupees_text)).setText(collectionModelsList.get(position).total);
		}
		
		vi.findViewById(R.id.credits_text).setVisibility(View.INVISIBLE);
		
		vi.setTag(position);
		vi.setOnClickListener(mListClickListener);
		
	        
		return vi; 
	}
	

	public void setList(ArrayList<MyCollectionModel> mTempHiringslist) {
//		this.mMyHiringsList = mTempHiringslist;
		
	}
	
}