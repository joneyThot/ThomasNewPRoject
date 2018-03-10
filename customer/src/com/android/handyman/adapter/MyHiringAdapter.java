package com.android.handyman.adapter;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.handyman.R;
import com.android.handyman.model.MyHiringsModel;
import com.android.handyman.model.StateModel;
import com.android.handyman.service.Utils;

public class MyHiringAdapter extends BaseAdapter{

	private Context mContext;
	private LayoutInflater infalter;
	private ArrayList<MyHiringsModel> mMyHiringsList;
	private OnClickListener mListClickListener;

	public MyHiringAdapter(Context mContext, ArrayList<MyHiringsModel> mMyHiringsList, OnClickListener mListClickListener) {
		super();
		this.mContext = mContext;
		this.mMyHiringsList = mMyHiringsList;
		this.mListClickListener = mListClickListener;

	}

	@Override
	public int getCount() {
		return mMyHiringsList.size();
	}
	
	@Override
	public Object getItem(int position) {
		return mMyHiringsList.get(position);
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
		vi = infalter.inflate(R.layout.row_my_hirings, null);
		
		if(Utils.validateString(mMyHiringsList.get(position).hire_status)){
			String status = mMyHiringsList.get(position).hire_status;
			if (status.equalsIgnoreCase("pending")) {
				((LinearLayout) vi.findViewById(R.id.color_layout)).setBackgroundColor(mContext.getResources().getColor(R.color.hire_pending_color));
//				vi.findViewById(R.id.order_image).setVisibility(View.GONE);
			} else if (status.equalsIgnoreCase("active")) {
				((LinearLayout) vi.findViewById(R.id.color_layout)).setBackgroundColor(mContext.getResources().getColor(R.color.hire_active_color));
//				vi.findViewById(R.id.order_image).setVisibility(View.VISIBLE);
//				((ImageView) vi.findViewById(R.id.order_image)).setBackgroundResource(R.drawable.icon_accepted);
			} else if (status.equalsIgnoreCase("start")) {
				((LinearLayout) vi.findViewById(R.id.color_layout)).setBackgroundColor(mContext.getResources().getColor(R.color.hire_start_color));
//				vi.findViewById(R.id.order_image).setVisibility(View.VISIBLE);
//				((ImageView) vi.findViewById(R.id.order_image)).setBackgroundResource(R.drawable.icon_start);
			} else if (status.equalsIgnoreCase("cancelled")) {
				((LinearLayout) vi.findViewById(R.id.color_layout)).setBackgroundColor(mContext.getResources().getColor(R.color.hire_cancelled_color));
//				vi.findViewById(R.id.order_image).setVisibility(View.VISIBLE);
//				((ImageView) vi.findViewById(R.id.order_image)).setBackgroundResource(R.drawable.icon_cancelled);
			} else if (status.equalsIgnoreCase("completed")) {
				((LinearLayout) vi.findViewById(R.id.color_layout)).setBackgroundColor(mContext.getResources().getColor(R.color.hire_completed_color));
//				vi.findViewById(R.id.order_image).setVisibility(View.VISIBLE);
//				((ImageView) vi.findViewById(R.id.order_image)).setBackgroundResource(R.drawable.icon_completed);
			} else if (status.equalsIgnoreCase("rejected")) {
				((LinearLayout) vi.findViewById(R.id.color_layout)).setBackgroundColor(mContext.getResources().getColor(R.color.hire_rejected_color));
//				vi.findViewById(R.id.order_image).setVisibility(View.GONE);
			}
		}

		if(Utils.validateString(mMyHiringsList.get(position).comment)){
			((TextView) vi.findViewById(R.id.hire_for_txt)).setText(mMyHiringsList.get(position).comment);
		} 
		
		if(Utils.validateString(mMyHiringsList.get(position).order_id)){
			((TextView) vi.findViewById(R.id.order_id_for_txt)).setText(mMyHiringsList.get(position).order_id);
		} 
		
		if(Utils.validateString(mMyHiringsList.get(position).created_date)){
			((TextView) vi.findViewById(R.id.order_date_text)).setText(mMyHiringsList.get(position).created_date);
		}
		
		if(Utils.validateString(mMyHiringsList.get(position).category_name)){
			((TextView) vi.findViewById(R.id.category_txt)).setText(mMyHiringsList.get(position).category_name);
		} 
		
		if(Utils.validateString(mMyHiringsList.get(position).sub_category_name)){
			((TextView) vi.findViewById(R.id.sub_category_txt)).setText(mMyHiringsList.get(position).sub_category_name);
		} 
		
		if(Utils.validateString(mMyHiringsList.get(position).appointment_date)){
			String birth_date = mMyHiringsList.get(position).appointment_date.toString();
			((TextView) vi.findViewById(R.id.date_text)).setText(birth_date);
		}
		
		if(Utils.validateString(mMyHiringsList.get(position).appointment_time)){
			String time = mMyHiringsList.get(position).appointment_time.toString();
			((TextView) vi.findViewById(R.id.time_text)).setText(time.substring(0, 2)+ ":"+time.substring(3, 5)+"" + time.substring(8,11));
//			int hours = Integer.parseInt(time.substring(0, 2));
//			 if(hours > 12)
//	           {
//				 ((TextView) vi.findViewById(R.id.time_text)).setText(time.substring(0, 2)+ ":"+time.substring(3, 5)+" P.M.");
//	           }
//	           if(hours == 12)
//	           {
//	        	   ((TextView) vi.findViewById(R.id.time_text)).setText(time.substring(0, 2)+ ":"+time.substring(3, 5)+" P.M.");
//	           }
//	           if(hours < 12)
//	           {
//	               if(hours!=0) 
//	            	   ((TextView) vi.findViewById(R.id.time_text)).setText(time.substring(0, 2)+ ":"+time.substring(3, 5)+" A.M.");
//	               else
//	            	   ((TextView) vi.findViewById(R.id.time_text)).setText(time.substring(0, 2)+ ":"+time.substring(3, 5)+" A.M.");
//	           }
//	         
		} 
		
		if(Utils.validateString(mMyHiringsList.get(position).debit)){
			((TextView) vi.findViewById(R.id.credits_text)).setText(mMyHiringsList.get(position).debit + " Debits");
		} 
		
		
		vi.setTag(position);
		vi.setOnClickListener(mListClickListener);
		
		/* View vi = convertView;
	     ViewHolder holder = null;

	        try {

	            if (vi == null) {
	            	infalter = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	                vi = infalter.inflate(R.layout.row_my_hirings, null);
	                holder = new ViewHolder();

	                holder.hire_for_txt = (TextView) vi.findViewById(R.id.hire_for_txt);
	                holder.category_txt = (TextView) vi.findViewById(R.id.category_txt);
	                holder.sub_category_txt = (TextView) vi.findViewById(R.id.sub_category_txt);
	                holder.date_text = (TextView) vi.findViewById(R.id.date_text);
	                holder.time_text = (TextView) vi.findViewById(R.id.time_text);
	                holder.credits_text = (TextView) vi.findViewById(R.id.credits_text);
	                holder.color_layout = (LinearLayout) vi.findViewById(R.id.color_layout);
	                vi.setTag(holder);
	                vi.setTag(position);
		    		vi.setOnClickListener(mListClickListener);

	            } else {
	                holder = (ViewHolder) vi.getTag();
	            }
	            
	            
	            if(Utils.validateString(mMyHiringsList.get(position).hire_status)){
//	        		Toast.makeText(mContext, mMyHiringsList.get(position).hire_status, Toast.LENGTH_SHORT).show();
	    			String status = mMyHiringsList.get(position).hire_status;
	    			if (status.equalsIgnoreCase("pending")) {
	    				 holder.color_layout.setBackgroundColor(mContext.getResources().getColor(R.color.hire_pending_color));
	    			} else if (status.equalsIgnoreCase("active")) {
	    				holder.color_layout.setBackgroundColor(mContext.getResources().getColor(R.color.hire_active_color));
	    			} else if (status.equalsIgnoreCase("cancelled")) {
	    				holder.color_layout.setBackgroundColor(mContext.getResources().getColor(R.color.hire_cancelled_color));
	    			} else if (status.equalsIgnoreCase("completed")) {
	    				holder.color_layout.setBackgroundColor(mContext.getResources().getColor(R.color.hire_completed_color));
	    			} else if (status.equalsIgnoreCase("rejected")) {
	    				holder.color_layout.setBackgroundColor(mContext.getResources().getColor(R.color.hire_rejected_color));
	    			}
	    		}
	            
	            if(Utils.validateString(mMyHiringsList.get(position).job_description)){
	            	holder.hire_for_txt.setText(mMyHiringsList.get(position).job_description);
	    		} 
	    		
	    		if(Utils.validateString(mMyHiringsList.get(position).category_name)){
	    			holder.category_txt.setText(mMyHiringsList.get(position).category_name);
	    		} 
	    		
	    		if(Utils.validateString(mMyHiringsList.get(position).subCategory_name)){
	    			holder.sub_category_txt.setText(mMyHiringsList.get(position).subCategory_name);
	    		} 
	    		
	    		if(Utils.validateString(mMyHiringsList.get(position).appointment_date)){
	    			String birth_date = mMyHiringsList.get(position).appointment_date.toString();
	    			holder.date_text.setText(birth_date);
	    		}
	    		
	    		if(Utils.validateString(mMyHiringsList.get(position).appointment_time)){
	    			String time = mMyHiringsList.get(position).appointment_time.toString();
	    			int hours = Integer.parseInt(time.substring(0, 2));
	    			 if(hours > 12)
	    	           {
	    				 holder.time_text.setText(time.substring(0, 2)+ ":"+time.substring(3, 5)+" P.M.");
	    	           }
	    	           if(hours == 12)
	    	           {
	    	        	   holder.time_text.setText(time.substring(0, 2)+ ":"+time.substring(3, 5)+" P.M.");
	    	           }
	    	           if(hours < 12)
	    	           {
	    	               if(hours!=0) 
	    	            	   holder.time_text.setText(time.substring(0, 2)+ ":"+time.substring(3, 5)+" A.M.");
	    	               else
	    	            	   holder.time_text.setText(time.substring(0, 2)+ ":"+time.substring(3, 5)+" A.M.");
	    	           }
	    	         
	    		} 
	    		
	    		if(Utils.validateString(mMyHiringsList.get(position).debit)){
	    			((TextView) vi.findViewById(R.id.credits_text)).setText(mMyHiringsList.get(position).debit + " Debits");
	    		} 
	    		
	    		
	        } catch (Exception e) {
	            e.printStackTrace();
	        }*/

		return vi; 
	}
	
	/* static class ViewHolder{
         TextView hire_for_txt;
         TextView category_txt;
         TextView sub_category_txt;
         TextView date_text;
         TextView time_text;
         TextView credits_text;
         LinearLayout color_layout;
     }*/

	public void setList(ArrayList<MyHiringsModel> mTempCustomerList) {
		
		
	}

}
