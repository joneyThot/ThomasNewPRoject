package com.handy.fragment;

import java.util.Locale;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.handy.MainActivity;
import com.handy.R;
import com.handy.SplashActivity;
import com.handy.model.MyCollectionModel;
import com.handy.service.Utils;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

public class MyColleactionDetailsFragment extends BaseFragment implements OnClickListener{
	
	private static String TAG = "MyColleactionDetailsFragment";
	
	private SharedPreferences mSharedPreferences;
	
	MyCollectionModel mDayCollection = new MyCollectionModel();
	MyCollectionModel mMonthCollection = new MyCollectionModel();
	MyCollectionModel mWeekCollection = new MyCollectionModel();
	MyCollectionModel mCustomCollection = new MyCollectionModel();
	
	String job_id = "",category_id, status, status_id,latitude = "0.0",longitude = "0.0", h_name = "", h_email = "",job_Description = "", mobile_no = "", full_address = "";
	
	Fragment fr;
	View mRootView;
	ImageView mHanfymanprofileImg;
	ImageView mAccept,mStart,mCancel,mComplete;
	private int mDeviceWidth = 480;

	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2) @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,	Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.fragment_my_collection_details, container, false);
		WindowManager w = ((Activity) getActivity()).getWindowManager();

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			Point size = new Point();
			w.getDefaultDisplay().getSize(size);
			mDeviceWidth = size.x;
		} else {
			Display d = w.getDefaultDisplay();
			mDeviceWidth = d.getWidth();
		}
		
		initview();
		return mRootView;
	}

	private void initview() {
		mSharedPreferences = getActivity().getSharedPreferences(Utils.PREF_NAME, Context.MODE_PRIVATE); 
		getActivity().findViewById(R.id.title).setVisibility(View.GONE);
		
		mActivity.getSlidingMenu().setMode(SlidingMenu.LEFT);
		mActivity.getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		
		mHanfymanprofileImg = (ImageView) mRootView.findViewById(R.id.my_collection_details_profile_img);
		
		mRootView.findViewById(R.id.my_collection_details_call_img).setOnClickListener(this);
		mRootView.findViewById(R.id.my_collection_details_map_img).setOnClickListener(this);
//		mRootView.findViewById(R.id.report_img).setOnClickListener(this);
		
		if(getArguments() != null) {
			mDayCollection =  (MyCollectionModel) getArguments().get(Utils.MY_COLLECTION_DAY_DETAILS);
			mMonthCollection =  (MyCollectionModel) getArguments().get(Utils.MY_COLLECTION_MONTH_DETAILS);
			mWeekCollection =  (MyCollectionModel) getArguments().get(Utils.MY_COLLECTION_WEEK_DETAILS);
			mCustomCollection =  (MyCollectionModel) getArguments().get(Utils.MY_COLLECTION_CUSTOM_DETAILS);
			
			if(mDayCollection != null){
				((MainActivity) getActivity()).setTitleText("", mDayCollection.client_name, View.VISIBLE, View.GONE, View.GONE);
				((TextView) mRootView.findViewById(R.id.my_collection_details_name_profile_txt)).setText(mDayCollection.client_name);
				
				if(Utils.validateString(mDayCollection.user_img_path)) {
					//			Picasso.with(mActivity).load(mVenue.image).fit().centerCrop().into(mBannerImage);
					Transformation transformation = new Transformation() {

						@Override public Bitmap transform(Bitmap source) {
							int targetWidth = mDeviceWidth;

							double aspectRatio = (double) source.getHeight() / (double) source.getWidth();
							int targetHeight = (int) (targetWidth * aspectRatio);
							if(targetHeight > targetWidth) {
								targetHeight = targetWidth;
							}
							Bitmap result = Bitmap.createScaledBitmap(source, targetWidth, targetHeight, false);
							if (result != source) {
								// Same bitmap is returned if sizes are the same
								source.recycle();
							}
							return result;
						}

						@Override public String key() {
							return "transformation" + " desiredWidth";
						}
					};

					Picasso.with(mActivity)
					.load(Utils.IMAGE_URL + mDayCollection.user_img_path)
					.placeholder(R.drawable.avtar_images)
					.error(R.drawable.avtar_images)
					.transform(transformation)
					.centerCrop()
					.resize(mDeviceWidth, (int) (mDeviceWidth))
					.into(mHanfymanprofileImg);
				}
				

				if(Utils.validateString(mDayCollection.job_description)){
					job_Description = mDayCollection.job_description;
					mRootView.findViewById(R.id.my_collection_details_job_description_textview).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.my_collection_details_job_description).setVisibility(View.VISIBLE);
					((TextView) mRootView.findViewById(R.id.my_collection_details_job_description)).setText(job_Description);
				}
				

				if(Utils.validateString(mDayCollection.appointment_date)){
					mRootView.findViewById(R.id.my_collection_details_date_textview).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.my_collection_details_date).setVisibility(View.VISIBLE);
					String birth_date = mDayCollection.appointment_date;
					((TextView) mRootView.findViewById(R.id.my_collection_details_date)).setText(birth_date);
				}
				

				if(Utils.validateString(mDayCollection.appointment_time)){
					mRootView.findViewById(R.id.my_collection_details_time_textview).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.my_collection_details_time).setVisibility(View.VISIBLE);
					String time = mDayCollection.appointment_time;
			        ((TextView) mRootView.findViewById(R.id.my_collection_details_time)).setText(time.substring(0, 2)+ ":"+time.substring(3, 5)+"" + time.substring(8, 11));
			          
				}
				

				if(Utils.validateString(mDayCollection.contact_person)){
					mRootView.findViewById(R.id.my_collection_details_contact_person_textview).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.my_collection_details_contact_person).setVisibility(View.VISIBLE);
					((TextView) mRootView.findViewById(R.id.my_collection_details_contact_person)).setText(mDayCollection.contact_person);
				}
				
				if(Utils.validateString(mDayCollection.contact_no)){
					mobile_no = mDayCollection.contact_no;
					mRootView.findViewById(R.id.my_collection_details_contact_no_textview).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.my_collection_details_contact_no).setVisibility(View.VISIBLE);
					((TextView) mRootView.findViewById(R.id.my_collection_details_contact_no)).setText(mDayCollection.contact_no);
				}
				
				if(Utils.validateString(mDayCollection.getAddress())){
					mRootView.findViewById(R.id.my_collection_details_address_textview).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.my_collection_details_address).setVisibility(View.VISIBLE);
					String address = "", street = "", landmark = "", city = "", state = "", pincode = "", floor = "", apartment = "";
					address =  mDayCollection.getAddress();
//					if(Utils.validateString(mDayCollection.getStreet()))
//						street = mDayCollection.getStreet();
//					if(Utils.validateString(mDayCollection.getLandmark()))
//						landmark = mDayCollection.getLandmark();
//					if(Utils.validateString(mDayCollection.getCity_name()))
//						city = mDayCollection.getCity_name();
//					if(Utils.validateString(mDayCollection.getPincode()))
//						pincode = mDayCollection.getPincode();
					if(Utils.validateString(mDayCollection.getFloor()))
						floor = mDayCollection.getFloor() + ", ";
					if(Utils.validateString(mDayCollection.getApartment()))
						apartment = mDayCollection.getApartment() + ", ";
					
//					((TextView) mRootView.findViewById(R.id.my_collection_details_address)).setText(address + "\n" + street + "\n" + landmark + "\n" + city + " - " + pincode);
//					full_address = address + "," + street + "," + landmark + "," + city + " - " + pincode; 
					((TextView) mRootView.findViewById(R.id.my_collection_details_address)).setText(floor + apartment + address);
					full_address = address;
				}
				
				if(Utils.validateString(mDayCollection.comment)){
					mRootView.findViewById(R.id.my_collection_details_requirment_textview).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.my_collection_details_requirment).setVisibility(View.VISIBLE);
					((TextView) mRootView.findViewById(R.id.my_collection_details_requirment)).setText(mDayCollection.comment);
				}
				
				if(Utils.validateString(mDayCollection.order_id)){
					mRootView.findViewById(R.id.livOrderId).setVisibility(View.VISIBLE);
					((TextView) mRootView.findViewById(R.id.my_collection_details_order_id_for_txt)).setText(mDayCollection.order_id);
				} else {
					mRootView.findViewById(R.id.livOrderId).setVisibility(View.GONE);
				}
				if(Utils.validateString(mDayCollection.created_date)){
					mRootView.findViewById(R.id.livOrderDate).setVisibility(View.VISIBLE);
					((TextView) mRootView.findViewById(R.id.my_collection_details_order_date_text)).setText(mDayCollection.created_date);
				} else {
					mRootView.findViewById(R.id.livOrderDate).setVisibility(View.GONE);
				}
				
				if(Utils.validateString(mDayCollection.amount)){
					mRootView.findViewById(R.id.livAmount).setVisibility(View.VISIBLE);
					((TextView) mRootView.findViewById(R.id.my_collection_details_amount_txt)).setText(mDayCollection.amount + "/-");
				} else {
					mRootView.findViewById(R.id.livAmount).setVisibility(View.GONE);
				}
				
				if(Utils.validateString(mDayCollection.discount)){
					mRootView.findViewById(R.id.livDiscount).setVisibility(View.VISIBLE);
					((TextView) mRootView.findViewById(R.id.my_collection_details_discount_txt)).setText(mDayCollection.discount + "%");
				} else {
					mRootView.findViewById(R.id.livDiscount).setVisibility(View.GONE);
				}
				
				if(Utils.validateString(mDayCollection.credit)){
					mRootView.findViewById(R.id.livCredits).setVisibility(View.VISIBLE);
					((TextView) mRootView.findViewById(R.id.my_collection_details_credit_txt)).setText(mDayCollection.credit);
				} else {
					mRootView.findViewById(R.id.livCredits).setVisibility(View.GONE);
				}
				
				if(Utils.validateString(mDayCollection.lat)){
					latitude = mDayCollection.lat;
				}
				
				if(Utils.validateString(mDayCollection.lng)){
					longitude = mDayCollection.lng;
				}
				
			}
			
			if(mMonthCollection != null){
				((MainActivity) getActivity()).setTitleText("", mMonthCollection.client_name, View.VISIBLE, View.GONE, View.GONE);
				((TextView) mRootView.findViewById(R.id.my_collection_details_name_profile_txt)).setText(mMonthCollection.client_name);
				
				if(Utils.validateString(mMonthCollection.user_img_path)) {
					//			Picasso.with(mActivity).load(mVenue.image).fit().centerCrop().into(mBannerImage);
					Transformation transformation = new Transformation() {

						@Override public Bitmap transform(Bitmap source) {
							int targetWidth = mDeviceWidth;

							double aspectRatio = (double) source.getHeight() / (double) source.getWidth();
							int targetHeight = (int) (targetWidth * aspectRatio);
							if(targetHeight > targetWidth) {
								targetHeight = targetWidth;
							}
							Bitmap result = Bitmap.createScaledBitmap(source, targetWidth, targetHeight, false);
							if (result != source) {
								// Same bitmap is returned if sizes are the same
								source.recycle();
							}
							return result;
						}

						@Override public String key() {
							return "transformation" + " desiredWidth";
						}
					};

					Picasso.with(mActivity)
					.load(Utils.IMAGE_URL + mMonthCollection.user_img_path)
					.placeholder(R.drawable.avtar_images)
					.error(R.drawable.avtar_images)
					.transform(transformation)
					.centerCrop()
					.resize(mDeviceWidth, (int) (mDeviceWidth))
					.into(mHanfymanprofileImg);
				}
				

				if(Utils.validateString(mMonthCollection.job_description)){
					job_Description = mMonthCollection.job_description;
					mRootView.findViewById(R.id.my_collection_details_job_description_textview).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.my_collection_details_job_description).setVisibility(View.VISIBLE);
					((TextView) mRootView.findViewById(R.id.my_collection_details_job_description)).setText(job_Description);
				}
				

				if(Utils.validateString(mMonthCollection.appointment_date)){
					mRootView.findViewById(R.id.my_collection_details_date_textview).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.my_collection_details_date).setVisibility(View.VISIBLE);
					String birth_date = mMonthCollection.appointment_date;
					((TextView) mRootView.findViewById(R.id.my_collection_details_date)).setText(birth_date);
				}
				

				if(Utils.validateString(mMonthCollection.appointment_time)){
					mRootView.findViewById(R.id.my_collection_details_time_textview).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.my_collection_details_time).setVisibility(View.VISIBLE);
					String time = mMonthCollection.appointment_time;
			        ((TextView) mRootView.findViewById(R.id.my_collection_details_time)).setText(time.substring(0, 2)+ ":"+time.substring(3, 5)+"" + time.substring(8, 11));
			          
				}
				

				if(Utils.validateString(mMonthCollection.contact_person)){
					mRootView.findViewById(R.id.my_collection_details_contact_person_textview).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.my_collection_details_contact_person).setVisibility(View.VISIBLE);
					((TextView) mRootView.findViewById(R.id.my_collection_details_contact_person)).setText(mMonthCollection.contact_person);
				}
				
				if(Utils.validateString(mMonthCollection.contact_no)){
					mobile_no = mMonthCollection.contact_no;
					mRootView.findViewById(R.id.my_collection_details_contact_no_textview).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.my_collection_details_contact_no).setVisibility(View.VISIBLE);
					((TextView) mRootView.findViewById(R.id.my_collection_details_contact_no)).setText(mMonthCollection.contact_no);
				}
				
				if(Utils.validateString(mMonthCollection.getAddress())){
					mRootView.findViewById(R.id.my_collection_details_address_textview).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.my_collection_details_address).setVisibility(View.VISIBLE);
					String address = "", street = "", landmark = "", city = "", state = "", pincode = "", floor = "", apartment = "";
					address =  mMonthCollection.getAddress();
//					if(Utils.validateString(mMonthCollection.getStreet()))
//						street = mMonthCollection.getStreet();
//					if(Utils.validateString(mMonthCollection.getLandmark()))
//						landmark = mMonthCollection.getLandmark();
//					if(Utils.validateString(mMonthCollection.getCity_name()))
//						city = mMonthCollection.getCity_name();
//					if(Utils.validateString(mMonthCollection.getPincode()))
//						pincode = mMonthCollection.getPincode();
					if(Utils.validateString(mMonthCollection.getFloor()))
						floor = mMonthCollection.getFloor() + ", ";
					if(Utils.validateString(mMonthCollection.getApartment()))
						apartment = mMonthCollection.getApartment() + ", ";
					
//					((TextView) mRootView.findViewById(R.id.my_collection_details_address)).setText(address + "\n" + street + "\n" + landmark + "\n" + city + " - " + pincode);
//					full_address = address + "," + street + "," + landmark + "," + city + " - " + pincode;
					((TextView) mRootView.findViewById(R.id.my_collection_details_address)).setText(floor + apartment + address);
					full_address = address ;
				}
				
				if(Utils.validateString(mMonthCollection.comment)){
					mRootView.findViewById(R.id.my_collection_details_requirment_textview).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.my_collection_details_requirment).setVisibility(View.VISIBLE);
					((TextView) mRootView.findViewById(R.id.my_collection_details_requirment)).setText(mMonthCollection.comment);
				}
				
//				if(Utils.validateString(mMonthCollection.total)){
//					mRootView.findViewById(R.id.my_collection_details_amount_txt).setVisibility(View.VISIBLE);
//					((TextView) mRootView.findViewById(R.id.my_collection_details_amount_txt)).setText(mMonthCollection.total + "/-");
//				}

				if(Utils.validateString(mMonthCollection.order_id)){
					mRootView.findViewById(R.id.livOrderId).setVisibility(View.VISIBLE);
					((TextView) mRootView.findViewById(R.id.my_collection_details_order_id_for_txt)).setText(mMonthCollection.order_id);
				} else {
					mRootView.findViewById(R.id.livOrderId).setVisibility(View.GONE);
				}
				if(Utils.validateString(mMonthCollection.created_date)){
					mRootView.findViewById(R.id.livOrderDate).setVisibility(View.VISIBLE);
					((TextView) mRootView.findViewById(R.id.my_collection_details_order_date_text)).setText(mMonthCollection.created_date);
				} else {
					mRootView.findViewById(R.id.livOrderDate).setVisibility(View.GONE);
				}

				if(Utils.validateString(mMonthCollection.amount)){
					mRootView.findViewById(R.id.livAmount).setVisibility(View.VISIBLE);
					((TextView) mRootView.findViewById(R.id.my_collection_details_amount_txt)).setText(mMonthCollection.amount + "/-");
				} else {
					mRootView.findViewById(R.id.livAmount).setVisibility(View.GONE);
				}

				if(Utils.validateString(mMonthCollection.discount)){
					mRootView.findViewById(R.id.livDiscount).setVisibility(View.VISIBLE);
					((TextView) mRootView.findViewById(R.id.my_collection_details_discount_txt)).setText(mMonthCollection.discount + "%");
				} else {
					mRootView.findViewById(R.id.livDiscount).setVisibility(View.GONE);
				}

				if(Utils.validateString(mMonthCollection.credit)){
					mRootView.findViewById(R.id.livCredits).setVisibility(View.VISIBLE);
					((TextView) mRootView.findViewById(R.id.my_collection_details_credit_txt)).setText(mMonthCollection.credit);
				} else {
					mRootView.findViewById(R.id.livCredits).setVisibility(View.GONE);
				}
				
				if(Utils.validateString(mMonthCollection.lat)){
					latitude = mMonthCollection.lat;
				}
				
				if(Utils.validateString(mMonthCollection.lng)){
					longitude = mMonthCollection.lng;
				}
				
			}
			
			if(mWeekCollection != null){
				((MainActivity) getActivity()).setTitleText("", mWeekCollection.client_name, View.VISIBLE, View.GONE, View.GONE);
				((TextView) mRootView.findViewById(R.id.my_collection_details_name_profile_txt)).setText(mWeekCollection.client_name);
				
				if(Utils.validateString(mWeekCollection.user_img_path)) {
					//			Picasso.with(mActivity).load(mVenue.image).fit().centerCrop().into(mBannerImage);
					Transformation transformation = new Transformation() {

						@Override public Bitmap transform(Bitmap source) {
							int targetWidth = mDeviceWidth;

							double aspectRatio = (double) source.getHeight() / (double) source.getWidth();
							int targetHeight = (int) (targetWidth * aspectRatio);
							if(targetHeight > targetWidth) {
								targetHeight = targetWidth;
							}
							Bitmap result = Bitmap.createScaledBitmap(source, targetWidth, targetHeight, false);
							if (result != source) {
								// Same bitmap is returned if sizes are the same
								source.recycle();
							}
							return result;
						}

						@Override public String key() {
							return "transformation" + " desiredWidth";
						}
					};

					Picasso.with(mActivity)
					.load(Utils.IMAGE_URL + mWeekCollection.user_img_path)
					.placeholder(R.drawable.avtar_images)
					.error(R.drawable.avtar_images)
					.transform(transformation)
					.centerCrop()
					.resize(mDeviceWidth, (int) (mDeviceWidth))
					.into(mHanfymanprofileImg);
				}
				

				if(Utils.validateString(mWeekCollection.job_description)){
					job_Description = mWeekCollection.job_description;
					mRootView.findViewById(R.id.my_collection_details_job_description_textview).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.my_collection_details_job_description).setVisibility(View.VISIBLE);
					((TextView) mRootView.findViewById(R.id.my_collection_details_job_description)).setText(job_Description);
				}
				

				if(Utils.validateString(mWeekCollection.appointment_date)){
					mRootView.findViewById(R.id.my_collection_details_date_textview).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.my_collection_details_date).setVisibility(View.VISIBLE);
					String birth_date = mWeekCollection.appointment_date;
					((TextView) mRootView.findViewById(R.id.my_collection_details_date)).setText(birth_date);
				}
				

				if(Utils.validateString(mWeekCollection.appointment_time)){
					mRootView.findViewById(R.id.my_collection_details_time_textview).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.my_collection_details_time).setVisibility(View.VISIBLE);
					String time = mWeekCollection.appointment_time;
			        ((TextView) mRootView.findViewById(R.id.my_collection_details_time)).setText(time.substring(0, 2)+ ":"+time.substring(3, 5)+"" + time.substring(8, 11));
			          
				}
				

				if(Utils.validateString(mWeekCollection.contact_person)){
					mRootView.findViewById(R.id.my_collection_details_contact_person_textview).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.my_collection_details_contact_person).setVisibility(View.VISIBLE);
					((TextView) mRootView.findViewById(R.id.my_collection_details_contact_person)).setText(mWeekCollection.contact_person);
				}
				
				if(Utils.validateString(mWeekCollection.contact_no)){
					mobile_no = mWeekCollection.contact_no;
					mRootView.findViewById(R.id.my_collection_details_contact_no_textview).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.my_collection_details_contact_no).setVisibility(View.VISIBLE);
					((TextView) mRootView.findViewById(R.id.my_collection_details_contact_no)).setText(mWeekCollection.contact_no);
				}
				
				if(Utils.validateString(mWeekCollection.getAddress())){
					mRootView.findViewById(R.id.my_collection_details_address_textview).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.my_collection_details_address).setVisibility(View.VISIBLE);
					String address = "", street = "", landmark = "", city = "", state = "", pincode = "", floor = "", apartment = "";

					address =  mWeekCollection.getAddress();
//					if(Utils.validateString(mWeekCollection.getStreet()))
//						street = mWeekCollection.getStreet();
//					if(Utils.validateString(mWeekCollection.getLandmark()))
//						landmark = mWeekCollection.getLandmark();
//					if(Utils.validateString(mWeekCollection.getCity_name()))
//						city = mWeekCollection.getCity_name();
//					if(Utils.validateString(mWeekCollection.getPincode()))
//						pincode = mWeekCollection.getPincode();
					if(Utils.validateString(mWeekCollection.getFloor()))
						floor = mWeekCollection.getFloor() + ", ";
					if(Utils.validateString(mWeekCollection.getApartment()))
						apartment = mWeekCollection.getApartment() + ", ";
					
//					((TextView) mRootView.findViewById(R.id.my_collection_details_address)).setText(address + "\n" + street + "\n" + landmark + "\n" + city + " - " + pincode);
//					full_address = address + "," + street + "," + landmark + "," + city + " - " + pincode;
					
					((TextView) mRootView.findViewById(R.id.my_collection_details_address)).setText(floor  + apartment + address);
					full_address =  address ;
				}
				
				if(Utils.validateString(mWeekCollection.comment)){
					mRootView.findViewById(R.id.my_collection_details_requirment_textview).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.my_collection_details_requirment).setVisibility(View.VISIBLE);
					((TextView) mRootView.findViewById(R.id.my_collection_details_requirment)).setText(mWeekCollection.comment);
				}
				
//				if(Utils.validateString(mWeekCollection.total)){
//					mRootView.findViewById(R.id.my_collection_details_amount_txt).setVisibility(View.VISIBLE);
//					((TextView) mRootView.findViewById(R.id.my_collection_details_amount_txt)).setText(mWeekCollection.total + "/-");
//				}

				if(Utils.validateString(mWeekCollection.order_id)){
					mRootView.findViewById(R.id.livOrderId).setVisibility(View.VISIBLE);
					((TextView) mRootView.findViewById(R.id.my_collection_details_order_id_for_txt)).setText(mWeekCollection.order_id);
				} else {
					mRootView.findViewById(R.id.livOrderId).setVisibility(View.GONE);
				}
				if(Utils.validateString(mWeekCollection.created_date)){
					mRootView.findViewById(R.id.livOrderDate).setVisibility(View.VISIBLE);
					((TextView) mRootView.findViewById(R.id.my_collection_details_order_date_text)).setText(mWeekCollection.created_date);
				} else {
					mRootView.findViewById(R.id.livOrderDate).setVisibility(View.GONE);
				}

				if(Utils.validateString(mWeekCollection.amount)){
					mRootView.findViewById(R.id.livAmount).setVisibility(View.VISIBLE);
					((TextView) mRootView.findViewById(R.id.my_collection_details_amount_txt)).setText(mWeekCollection.amount + "/-");
				} else {
					mRootView.findViewById(R.id.livAmount).setVisibility(View.GONE);
				}

				if(Utils.validateString(mWeekCollection.discount)){
					mRootView.findViewById(R.id.livDiscount).setVisibility(View.VISIBLE);
					((TextView) mRootView.findViewById(R.id.my_collection_details_discount_txt)).setText(mWeekCollection.discount + "%");
				} else {
					mRootView.findViewById(R.id.livDiscount).setVisibility(View.GONE);
				}

				if(Utils.validateString(mWeekCollection.credit)){
					mRootView.findViewById(R.id.livCredits).setVisibility(View.VISIBLE);
					((TextView) mRootView.findViewById(R.id.my_collection_details_credit_txt)).setText(mWeekCollection.credit);
				} else {
					mRootView.findViewById(R.id.livCredits).setVisibility(View.GONE);
				}
				
				if(Utils.validateString(mWeekCollection.lat)){
					latitude = mWeekCollection.lat;
				}
				
				if(Utils.validateString(mWeekCollection.lng)){
					longitude = mWeekCollection.lng;
				}
				
			}
			
			if(mCustomCollection != null){
				((MainActivity) getActivity()).setTitleText("", mCustomCollection.client_name, View.VISIBLE, View.GONE, View.GONE);
				((TextView) mRootView.findViewById(R.id.my_collection_details_name_profile_txt)).setText(mCustomCollection.client_name);
				
				if(Utils.validateString(mCustomCollection.user_img_path)) {
					//			Picasso.with(mActivity).load(mVenue.image).fit().centerCrop().into(mBannerImage);
					Transformation transformation = new Transformation() {

						@Override public Bitmap transform(Bitmap source) {
							int targetWidth = mDeviceWidth;

							double aspectRatio = (double) source.getHeight() / (double) source.getWidth();
							int targetHeight = (int) (targetWidth * aspectRatio);
							if(targetHeight > targetWidth) {
								targetHeight = targetWidth;
							}
							Bitmap result = Bitmap.createScaledBitmap(source, targetWidth, targetHeight, false);
							if (result != source) {
								// Same bitmap is returned if sizes are the same
								source.recycle();
							}
							return result;
						}

						@Override public String key() {
							return "transformation" + " desiredWidth";
						}
					};

					Picasso.with(mActivity)
					.load(Utils.IMAGE_URL + mCustomCollection.user_img_path)
					.placeholder(R.drawable.avtar_images)
					.error(R.drawable.avtar_images)
					.transform(transformation)
					.centerCrop()
					.resize(mDeviceWidth, (int) (mDeviceWidth))
					.into(mHanfymanprofileImg);
				}
				

				if(Utils.validateString(mCustomCollection.job_description)){
					job_Description = mCustomCollection.job_description;
					mRootView.findViewById(R.id.my_collection_details_job_description_textview).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.my_collection_details_job_description).setVisibility(View.VISIBLE);
					((TextView) mRootView.findViewById(R.id.my_collection_details_job_description)).setText(job_Description);
				}
				

				if(Utils.validateString(mCustomCollection.appointment_date)){
					mRootView.findViewById(R.id.my_collection_details_date_textview).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.my_collection_details_date).setVisibility(View.VISIBLE);
					String birth_date = mCustomCollection.appointment_date;
					((TextView) mRootView.findViewById(R.id.my_collection_details_date)).setText(birth_date);
				}
				

				if(Utils.validateString(mCustomCollection.appointment_time)){
					mRootView.findViewById(R.id.my_collection_details_time_textview).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.my_collection_details_time).setVisibility(View.VISIBLE);
					String time = mCustomCollection.appointment_time;
			        ((TextView) mRootView.findViewById(R.id.my_collection_details_time)).setText(time.substring(0, 2)+ ":"+time.substring(3, 5)+"" + time.substring(8, 11));
			          
				}
				

				if(Utils.validateString(mCustomCollection.contact_person)){
					mRootView.findViewById(R.id.my_collection_details_contact_person_textview).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.my_collection_details_contact_person).setVisibility(View.VISIBLE);
					((TextView) mRootView.findViewById(R.id.my_collection_details_contact_person)).setText(mCustomCollection.contact_person);
				}
				
				if(Utils.validateString(mCustomCollection.contact_no)){
					mobile_no = mCustomCollection.contact_no;
					mRootView.findViewById(R.id.my_collection_details_contact_no_textview).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.my_collection_details_contact_no).setVisibility(View.VISIBLE);
					((TextView) mRootView.findViewById(R.id.my_collection_details_contact_no)).setText(mCustomCollection.contact_no);
				}
				
				if(Utils.validateString(mCustomCollection.getAddress())){
					mRootView.findViewById(R.id.my_collection_details_address_textview).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.my_collection_details_address).setVisibility(View.VISIBLE);
					String address = "", street = "", landmark = "", city = "", state = "", pincode = "", floor = "", apartment= "";
					address =  mCustomCollection.getAddress();
//					if(Utils.validateString(mCustomCollection.getStreet()))
//						street = mCustomCollection.getStreet();
//					if(Utils.validateString(mCustomCollection.getLandmark()))
//						landmark = mCustomCollection.getLandmark();
//					if(Utils.validateString(mCustomCollection.getCity_name()))
//						city = mCustomCollection.getCity_name();
//					if(Utils.validateString(mCustomCollection.getPincode()))
//						pincode = mCustomCollection.getPincode();
					if(Utils.validateString(mCustomCollection.getFloor()))
						floor = mCustomCollection.getFloor() + ", ";
					if(Utils.validateString(mCustomCollection.getApartment()))
						apartment = mCustomCollection.getApartment() + ", ";
					
//					((TextView) mRootView.findViewById(R.id.my_collection_details_address)).setText(address + "\n" + street + "\n" + landmark + "\n" + city + " - " + pincode);
//					full_address = address + "," + street + "," + landmark + "," + city + " - " + pincode;
					((TextView) mRootView.findViewById(R.id.my_collection_details_address)).setText(floor + apartment + address);
					full_address = address ;
				}
				
				if(Utils.validateString(mCustomCollection.comment)){
					mRootView.findViewById(R.id.my_collection_details_requirment_textview).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.my_collection_details_requirment).setVisibility(View.VISIBLE);
					((TextView) mRootView.findViewById(R.id.my_collection_details_requirment)).setText(mCustomCollection.comment);
				}
				
//				if(Utils.validateString(mCustomCollection.total)){
//					mRootView.findViewById(R.id.my_collection_details_amount_txt).setVisibility(View.VISIBLE);
//					((TextView) mRootView.findViewById(R.id.my_collection_details_amount_txt)).setText(mCustomCollection.total + "/-");
//				}

				if(Utils.validateString(mCustomCollection.order_id)){
					mRootView.findViewById(R.id.livOrderId).setVisibility(View.VISIBLE);
					((TextView) mRootView.findViewById(R.id.my_collection_details_order_id_for_txt)).setText(mCustomCollection.order_id);
				} else {
					mRootView.findViewById(R.id.livOrderId).setVisibility(View.GONE);
				}
				if(Utils.validateString(mCustomCollection.created_date)){
					mRootView.findViewById(R.id.livOrderDate).setVisibility(View.VISIBLE);
					((TextView) mRootView.findViewById(R.id.my_collection_details_order_date_text)).setText(mCustomCollection.created_date);
				} else {
					mRootView.findViewById(R.id.livOrderDate).setVisibility(View.GONE);
				}

				if(Utils.validateString(mCustomCollection.amount)){
					mRootView.findViewById(R.id.livAmount).setVisibility(View.VISIBLE);
					((TextView) mRootView.findViewById(R.id.my_collection_details_amount_txt)).setText(mCustomCollection.amount + "/-");
				} else {
					mRootView.findViewById(R.id.livAmount).setVisibility(View.GONE);
				}

				if(Utils.validateString(mCustomCollection.discount)){
					mRootView.findViewById(R.id.livDiscount).setVisibility(View.VISIBLE);
					((TextView) mRootView.findViewById(R.id.my_collection_details_discount_txt)).setText(mCustomCollection.discount + "%");
				} else {
					mRootView.findViewById(R.id.livDiscount).setVisibility(View.GONE);
				}

				if(Utils.validateString(mCustomCollection.credit)){
					mRootView.findViewById(R.id.livCredits).setVisibility(View.VISIBLE);
					((TextView) mRootView.findViewById(R.id.my_collection_details_credit_txt)).setText(mCustomCollection.credit);
				} else {
					mRootView.findViewById(R.id.livCredits).setVisibility(View.GONE);
				}
				
				if(Utils.validateString(mCustomCollection.lat)){
					latitude = mCustomCollection.lat;
				}
				
				if(Utils.validateString(mCustomCollection.lng)){
					longitude = mCustomCollection.lng;
				}
				
			}
			
		}
		
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
			
		case R.id.my_collection_details_call_img:
			Uri number = Uri.parse("tel:"+ mobile_no);
			Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
			startActivity(callIntent);
			
			break;
			
		case R.id.my_collection_details_map_img:
			
			if(Utils.validateString(full_address)){
				String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?saddr=%f,%f&daddr=%f,%f (%s)", SplashActivity.latitude, SplashActivity.longitude, Double.parseDouble(latitude), Double.parseDouble(longitude), full_address );
				Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
				startActivity(i);
			} else {
				String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?saddr=%f,%f&daddr=%f,%f", SplashActivity.latitude, SplashActivity.longitude, Double.parseDouble(latitude), Double.parseDouble(longitude));
				Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
				startActivity(i);
			}
			break;
		}
	}
	
	
}
