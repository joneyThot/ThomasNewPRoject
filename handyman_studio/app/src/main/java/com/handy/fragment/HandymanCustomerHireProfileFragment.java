package com.handy.fragment;

import java.util.Locale;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
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
import android.widget.Toast;

import com.handy.MainActivity;
import com.handy.R;
import com.handy.SplashActivity;
import com.handy.model.MyHiringsModel;
import com.handy.service.AsyncCallListener;
import com.handy.service.GetHandymanChangeStatusRequestTask;
import com.handy.service.Utils;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

public class HandymanCustomerHireProfileFragment extends BaseFragment implements OnClickListener{
	
	private static String TAG = "HandymanCustomerHireProfileFragment";
	
	private SharedPreferences mSharedPreferences;
	
	MyHiringsModel mPendingHirings = new MyHiringsModel();
	MyHiringsModel mCancelHirings = new MyHiringsModel();
	MyHiringsModel mCompleteHirings = new MyHiringsModel();
	MyHiringsModel mRejectedHirings = new MyHiringsModel(); 
	MyHiringsModel mAcceptedHirings = new MyHiringsModel();
	MyHiringsModel mStartingHirings = new MyHiringsModel();
	MyHiringsModel mAllHirings = new MyHiringsModel();
	
	String client_email = "", client_name = "",job_id = "",category_id, status, status_reject, status_id,latitude = "0.0",longitude = "0.0", h_name = "", h_email = "",job_Description = "", mobile_no = "", full_address = "";
	
	Fragment fr;
	View mRootView;
	ImageView mHanfymanprofileImg;
	ImageView mAccept,mStart,mCancel,mComplete,mReject;
	private int mDeviceWidth = 480;

	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2) @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,	Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.fragment_hm_profile, container, false);
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
//		((MainActivity) getActivity()).setTitleText(getString(R.string.home_searvice_at_home), "", View.GONE, View.VISIBLE, View.GONE);
		getActivity().findViewById(R.id.title).setVisibility(View.GONE);
		
		mActivity.getSlidingMenu().setMode(SlidingMenu.LEFT);
		mActivity.getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		
		mHanfymanprofileImg = (ImageView) mRootView.findViewById(R.id.hanfyman_profile_img);
		mAccept = (ImageView) mRootView.findViewById(R.id.accept_btn);
		mAccept.setOnClickListener(this);
		mStart = (ImageView) mRootView.findViewById(R.id.start_btn);
		mStart.setOnClickListener(this);
		mCancel = (ImageView) mRootView.findViewById(R.id.cancel_btn);
		mCancel.setOnClickListener(this);
		mComplete = (ImageView) mRootView.findViewById(R.id.completed_btn);
		mComplete.setOnClickListener(this);
		mReject = (ImageView) mRootView.findViewById(R.id.reject_btn);
		
		mRootView.findViewById(R.id.chat_img).setOnClickListener(this);
		mRootView.findViewById(R.id.call_img).setOnClickListener(this);
		mRootView.findViewById(R.id.map_img).setOnClickListener(this);
		mRootView.findViewById(R.id.report_img).setOnClickListener(this);
		
		if(getArguments() != null) {
			mPendingHirings =  (MyHiringsModel) getArguments().get(Utils.HANDYMAN_HIRE_PENDING_DETAILS);
			mAcceptedHirings =  (MyHiringsModel) getArguments().get(Utils.HANDYMAN_HIRE_ACCEPT_DETAILS);
			mStartingHirings =  (MyHiringsModel) getArguments().get(Utils.HANDYMAN_HIRE_START_DETAILS);
			mCancelHirings =  (MyHiringsModel) getArguments().get(Utils.HANDYMAN_HIRE_CANCEL_DETAILS);
			mCompleteHirings =  (MyHiringsModel) getArguments().get(Utils.HANDYMAN_HIRE_COMPLETE_DETAILS);
			mRejectedHirings =  (MyHiringsModel) getArguments().get(Utils.HANDYMAN_HIRE_REJECT_DETAILS);
			mAllHirings =  (MyHiringsModel) getArguments().get(Utils.HANDYMAN_HIRE_ALL_DETAILS);
			
			if(mPendingHirings != null){
				((MainActivity) getActivity()).setTitleText("", mPendingHirings.client_name, View.VISIBLE, View.GONE, View.GONE);
				((TextView) mRootView.findViewById(R.id.handyman_name_profile_txt)).setText(mPendingHirings.client_name);
				
				if(Utils.validateString(mPendingHirings.img_path)) {
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
					.load(Utils.IMAGE_URL + mPendingHirings.img_path)
					.placeholder(R.drawable.avtar_images)
					.error(R.drawable.avtar_images)
					.transform(transformation)
					.centerCrop()
					.resize(mDeviceWidth, (int) (mDeviceWidth))
					.into(mHanfymanprofileImg);
				}
				

				if(Utils.validateString(mPendingHirings.order_id)){
					mRootView.findViewById(R.id.hire_customer_order_id_textview).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.hire_customer_order_id).setVisibility(View.VISIBLE);
					((TextView) mRootView.findViewById(R.id.hire_customer_order_id)).setText(mPendingHirings.order_id);
				}
				
				if(Utils.validateString(mPendingHirings.created_date)){
					mRootView.findViewById(R.id.hire_customer_order_date_textview).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.hire_customer_order_date).setVisibility(View.VISIBLE);
					((TextView) mRootView.findViewById(R.id.hire_customer_order_date)).setText(mPendingHirings.created_date);
				}
				
				if(Utils.validateString(mPendingHirings.job_description)){
					job_Description = mPendingHirings.job_description;
					mRootView.findViewById(R.id.hire_customer_job_description_textview).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.hire_customer_job_description).setVisibility(View.VISIBLE);
					((TextView) mRootView.findViewById(R.id.hire_customer_job_description)).setText(job_Description);
				}
				

				if(Utils.validateString(mPendingHirings.appointment_date)){
					mRootView.findViewById(R.id.hire_customer_date_textview).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.hire_customer_date).setVisibility(View.VISIBLE);
					String birth_date = mPendingHirings.appointment_date;
					((TextView) mRootView.findViewById(R.id.hire_customer_date)).setText(birth_date);
				}
				

				if(Utils.validateString(mPendingHirings.appointment_time)){
					mRootView.findViewById(R.id.hire_customer_time_textview).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.hire_customer_time).setVisibility(View.VISIBLE);
					String time = mPendingHirings.appointment_time;
			        ((TextView) mRootView.findViewById(R.id.hire_customer_time)).setText(time.substring(0, 2)+ ":"+time.substring(3, 5)+"" + time.substring(8, 11));
			          
				}
				

				if(Utils.validateString(mPendingHirings.contact_person)){
					mRootView.findViewById(R.id.hire_customer_contact_person_textview).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.hire_customer_contact_person).setVisibility(View.VISIBLE);
					((TextView) mRootView.findViewById(R.id.hire_customer_contact_person)).setText(mPendingHirings.contact_person);
				}
				
				if(Utils.validateString(mPendingHirings.contact_no)){
					mobile_no = mPendingHirings.contact_no;
					mRootView.findViewById(R.id.hire_customer_contact_no_textview).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.hire_customer_contact_no).setVisibility(View.VISIBLE);
					((TextView) mRootView.findViewById(R.id.hire_customer_contact_no)).setText(mPendingHirings.contact_no);
				}
				
				if(Utils.validateString(mPendingHirings.getAddress())){
					mRootView.findViewById(R.id.hire_customer_address_textview).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.hire_customer_address).setVisibility(View.VISIBLE);
					String address = "", street = "", landmark = "", city = "", state = "", pincode = "", floor ="", apartment ="";

					if(Utils.validateString(mPendingHirings.getFloor()))
						floor = mPendingHirings.getFloor() + ", ";
					if(Utils.validateString(mPendingHirings.getApartment()))
						apartment = mPendingHirings.getApartment() + ", ";
					if(Utils.validateString(mPendingHirings.getAddress()))
						address =  mPendingHirings.getAddress();
//					if(Utils.validateString(mPendingHirings.getStreet()))
//						street = mPendingHirings.getStreet();
//					if(Utils.validateString(mPendingHirings.getLandmark()))
//						landmark = mPendingHirings.getLandmark();
//					if(Utils.validateString(mPendingHirings.getCity_name()))
//						city = mPendingHirings.getCity_name();
//					if(Utils.validateString(mPendingHirings.getState_name()))
//						state = mPendingHirings.getState_name();
//					if(Utils.validateString(mPendingHirings.getPincode()))
//						pincode = mPendingHirings.getPincode();
//					((TextView) mRootView.findViewById(R.id.hire_customer_address)).setText(floor + "\n" +address + "\n" + street + "\n" + landmark + "\n" + city + " - " + pincode);
					((TextView) mRootView.findViewById(R.id.hire_customer_address)).setText(floor + apartment +address);
					full_address = address;
				}
				
				if(Utils.validateString(mPendingHirings.comment)){
					mRootView.findViewById(R.id.hire_customer_requirment_textview).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.hire_customer_requirment).setVisibility(View.VISIBLE);
					((TextView) mRootView.findViewById(R.id.hire_customer_requirment)).setText(mPendingHirings.comment);
				}
				
				if(Utils.validateString(mPendingHirings.hire_status)){
					status = mPendingHirings.hire_status;
				}
				
				if(Utils.validateString(mPendingHirings.id)){
					status_id = mPendingHirings.id;
				}
				
				if(Utils.validateString(mPendingHirings.lat)){
					latitude = mPendingHirings.lat;
				}
				
				if(Utils.validateString(mPendingHirings.lng)){
					longitude = mPendingHirings.lng;
				}
				
				if(Utils.validateString(mPendingHirings.id)){
					job_id = mPendingHirings.id;
				}
				
				if(Utils.validateString(mPendingHirings.client_name)){
					client_name = mPendingHirings.client_name;
				}
				
				if(Utils.validateString(mPendingHirings.client_email)){
					client_email = mPendingHirings.client_email;
				}
				
				Utils.storeString(mSharedPreferences, Utils.CLIENT_ID, mPendingHirings.client_id);
				
			}
			
			if(mAcceptedHirings != null){
				((MainActivity) getActivity()).setTitleText("", mAcceptedHirings.client_name, View.VISIBLE, View.GONE, View.GONE);
				((TextView) mRootView.findViewById(R.id.handyman_name_profile_txt)).setText(mAcceptedHirings.client_name);
				
				if(Utils.validateString(mAcceptedHirings.img_path)) {
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
					.load(Utils.IMAGE_URL + mAcceptedHirings.img_path)
					.placeholder(R.drawable.avtar_images)
					.error(R.drawable.avtar_images)
					.transform(transformation)
					.centerCrop()
					.resize(mDeviceWidth, (int) (mDeviceWidth))
					.into(mHanfymanprofileImg);
				}
				
				if(Utils.validateString(mAcceptedHirings.order_id)){
					mRootView.findViewById(R.id.hire_customer_order_id_textview).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.hire_customer_order_id).setVisibility(View.VISIBLE);
					((TextView) mRootView.findViewById(R.id.hire_customer_order_id)).setText(mAcceptedHirings.order_id);
				}
				
				if(Utils.validateString(mAcceptedHirings.created_date)){
					mRootView.findViewById(R.id.hire_customer_order_date_textview).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.hire_customer_order_date).setVisibility(View.VISIBLE);
					((TextView) mRootView.findViewById(R.id.hire_customer_order_date)).setText(mAcceptedHirings.created_date);
				}

				if(Utils.validateString(mAcceptedHirings.job_description)){
					job_Description = mAcceptedHirings.job_description;
					mRootView.findViewById(R.id.hire_customer_job_description_textview).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.hire_customer_job_description).setVisibility(View.VISIBLE);
					((TextView) mRootView.findViewById(R.id.hire_customer_job_description)).setText(job_Description);
				}
				

				if(Utils.validateString(mAcceptedHirings.appointment_date)){
					mRootView.findViewById(R.id.hire_customer_date_textview).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.hire_customer_date).setVisibility(View.VISIBLE);
					String birth_date = mAcceptedHirings.appointment_date;
					((TextView) mRootView.findViewById(R.id.hire_customer_date)).setText(birth_date);
				}
				

				if(Utils.validateString(mAcceptedHirings.appointment_time)){
					mRootView.findViewById(R.id.hire_customer_time_textview).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.hire_customer_time).setVisibility(View.VISIBLE);
					String time = mAcceptedHirings.appointment_time;
					((TextView) mRootView.findViewById(R.id.hire_customer_time)).setText(time.substring(0, 2)+ ":"+time.substring(3, 5)+"" + time.substring(8, 11));
				}
				

				if(Utils.validateString(mAcceptedHirings.contact_person)){
					mRootView.findViewById(R.id.hire_customer_contact_person_textview).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.hire_customer_contact_person).setVisibility(View.VISIBLE);
					((TextView) mRootView.findViewById(R.id.hire_customer_contact_person)).setText(mAcceptedHirings.contact_person);
				}
				
				if(Utils.validateString(mAcceptedHirings.contact_no)){
					mobile_no = mAcceptedHirings.contact_no;
					mRootView.findViewById(R.id.hire_customer_contact_no_textview).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.hire_customer_contact_no).setVisibility(View.VISIBLE);
					((TextView) mRootView.findViewById(R.id.hire_customer_contact_no)).setText(mAcceptedHirings.contact_no);
				}
				
				if(Utils.validateString(mAcceptedHirings.getAddress())){
					mRootView.findViewById(R.id.hire_customer_address_textview).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.hire_customer_address).setVisibility(View.VISIBLE);
					String address = "", street = "", landmark = "", city = "", state = "", pincode = "", floor ="", apartment ="";

					if(Utils.validateString(mAcceptedHirings.getFloor()))
						floor = mAcceptedHirings.getFloor() + ", ";
					if(Utils.validateString(mAcceptedHirings.getApartment()))
						apartment = mAcceptedHirings.getApartment() + ", ";
					if(Utils.validateString(mAcceptedHirings.getAddress()))
						address =  mAcceptedHirings.getAddress();
//					if(Utils.validateString(mAcceptedHirings.getStreet()))
//						street = mAcceptedHirings.getStreet();
//					if(Utils.validateString(mAcceptedHirings.getLandmark()))
//						landmark = mAcceptedHirings.getLandmark();
//					if(Utils.validateString(mAcceptedHirings.getCity_name()))
//						city = mAcceptedHirings.getCity_name();
//					if(Utils.validateString(mAcceptedHirings.getState_name()))
//						state = mAcceptedHirings.getState_name();
//					if(Utils.validateString(mAcceptedHirings.getPincode()))
//						pincode = mAcceptedHirings.getPincode();
//					((TextView) mRootView.findViewById(R.id.hire_customer_address)).setText(floor + "\n" +address + "\n" + street + "\n" + landmark + "\n" + city + " - " + pincode);
					((TextView) mRootView.findViewById(R.id.hire_customer_address)).setText(floor + apartment  + address);
					full_address = address;
				}
				
				if(Utils.validateString(mAcceptedHirings.comment)){
					mRootView.findViewById(R.id.hire_customer_requirment_textview).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.hire_customer_requirment).setVisibility(View.VISIBLE);
					((TextView) mRootView.findViewById(R.id.hire_customer_requirment)).setText(mAcceptedHirings.comment);
				}
				
				if(Utils.validateString(mAcceptedHirings.hire_status)){
					status = mAcceptedHirings.hire_status;
				}
				
				if(Utils.validateString(mAcceptedHirings.id)){
					status_id = mAcceptedHirings.id;
				}
				
				if(Utils.validateString(mAcceptedHirings.lat)){
					latitude = mAcceptedHirings.lat;
				}
				
				if(Utils.validateString(mAcceptedHirings.lng)){
					longitude = mAcceptedHirings.lng;
				}
				
				if(Utils.validateString(mAcceptedHirings.id)){
					job_id = mAcceptedHirings.id;
				}
				
				if(Utils.validateString(mAcceptedHirings.client_name)){
					client_name = mAcceptedHirings.client_name;
				}
				
				if(Utils.validateString(mAcceptedHirings.client_email)){
					client_email = mAcceptedHirings.client_email;
				}
				
				Utils.storeString(mSharedPreferences, Utils.CLIENT_ID, mAcceptedHirings.client_id);
				
			}
			
			if(mStartingHirings != null){
				((MainActivity) getActivity()).setTitleText("", mStartingHirings.client_name, View.VISIBLE, View.GONE, View.GONE);
				((TextView) mRootView.findViewById(R.id.handyman_name_profile_txt)).setText(mStartingHirings.client_name);
				
				if(Utils.validateString(mStartingHirings.img_path)) {
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
					.load(Utils.IMAGE_URL + mStartingHirings.img_path)
					.placeholder(R.drawable.avtar_images)
					.error(R.drawable.avtar_images)
					.transform(transformation)
					.centerCrop()
					.resize(mDeviceWidth, (int) (mDeviceWidth))
					.into(mHanfymanprofileImg);
				}
				
				if(Utils.validateString(mStartingHirings.order_id)){
					mRootView.findViewById(R.id.hire_customer_order_id_textview).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.hire_customer_order_id).setVisibility(View.VISIBLE);
					((TextView) mRootView.findViewById(R.id.hire_customer_order_id)).setText(mStartingHirings.order_id);
				}
				
				if(Utils.validateString(mStartingHirings.created_date)){
					mRootView.findViewById(R.id.hire_customer_order_date_textview).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.hire_customer_order_date).setVisibility(View.VISIBLE);
					((TextView) mRootView.findViewById(R.id.hire_customer_order_date)).setText(mStartingHirings.created_date);
				}

				if(Utils.validateString(mStartingHirings.job_description)){
					job_Description = mStartingHirings.job_description;
					mRootView.findViewById(R.id.hire_customer_job_description_textview).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.hire_customer_job_description).setVisibility(View.VISIBLE);
					((TextView) mRootView.findViewById(R.id.hire_customer_job_description)).setText(job_Description);
				}
				

				if(Utils.validateString(mStartingHirings.appointment_date)){
					mRootView.findViewById(R.id.hire_customer_date_textview).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.hire_customer_date).setVisibility(View.VISIBLE);
					String birth_date = mStartingHirings.appointment_date;
					((TextView) mRootView.findViewById(R.id.hire_customer_date)).setText(birth_date);
				}
				

				if(Utils.validateString(mStartingHirings.appointment_time)){
					mRootView.findViewById(R.id.hire_customer_time_textview).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.hire_customer_time).setVisibility(View.VISIBLE);
					String time = mStartingHirings.appointment_time;
					((TextView) mRootView.findViewById(R.id.hire_customer_time)).setText(time.substring(0, 2)+ ":"+time.substring(3, 5)+"" + time.substring(8, 11));
				}
				

				if(Utils.validateString(mStartingHirings.contact_person)){
					mRootView.findViewById(R.id.hire_customer_contact_person_textview).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.hire_customer_contact_person).setVisibility(View.VISIBLE);
					((TextView) mRootView.findViewById(R.id.hire_customer_contact_person)).setText(mStartingHirings.contact_person);
				}
				
				if(Utils.validateString(mStartingHirings.contact_no)){
					mobile_no = mStartingHirings.contact_no;
					mRootView.findViewById(R.id.hire_customer_contact_no_textview).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.hire_customer_contact_no).setVisibility(View.VISIBLE);
					((TextView) mRootView.findViewById(R.id.hire_customer_contact_no)).setText(mStartingHirings.contact_no);
				}
				
				if(Utils.validateString(mStartingHirings.getAddress())){
					mRootView.findViewById(R.id.hire_customer_address_textview).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.hire_customer_address).setVisibility(View.VISIBLE);
					String address = "", street = "", landmark = "", city = "", state = "", pincode = "", floor ="", apartment ="";

					if(Utils.validateString(mStartingHirings.getFloor()))
						floor = mStartingHirings.getFloor() + ", ";
					if(Utils.validateString(mStartingHirings.getApartment()))
						apartment = mStartingHirings.getApartment() + ", ";
					if(Utils.validateString(mStartingHirings.getAddress()))
						address =  mStartingHirings.getAddress();
//					if(Utils.validateString(mStartingHirings.getStreet()))
//						street = mStartingHirings.getStreet();
//					if(Utils.validateString(mStartingHirings.getLandmark()))
//						landmark = mStartingHirings.getLandmark();
//					if(Utils.validateString(mStartingHirings.getCity_name()))
//						city = mStartingHirings.getCity_name();
//					if(Utils.validateString(mStartingHirings.getState_name()))
//						state = mStartingHirings.getState_name();
//					if(Utils.validateString(mStartingHirings.getPincode()))
//						pincode = mStartingHirings.getPincode();
//					((TextView) mRootView.findViewById(R.id.hire_customer_address)).setText(address + "\n" + street + "\n" + landmark + "\n" + city + " - " + pincode);
					((TextView) mRootView.findViewById(R.id.hire_customer_address)).setText(floor + apartment + address );
					full_address = address ;
				}
				
				if(Utils.validateString(mStartingHirings.comment)){
					mRootView.findViewById(R.id.hire_customer_requirment_textview).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.hire_customer_requirment).setVisibility(View.VISIBLE);
					((TextView) mRootView.findViewById(R.id.hire_customer_requirment)).setText(mStartingHirings.comment);
				}
				
				if(Utils.validateString(mStartingHirings.hire_status)){
					status = mStartingHirings.hire_status;
				}
				
				if(Utils.validateString(mStartingHirings.id)){
					status_id = mStartingHirings.id;
				}
				
				if(Utils.validateString(mStartingHirings.lat)){
					latitude = mStartingHirings.lat;
				}
				
				if(Utils.validateString(mStartingHirings.lng)){
					longitude = mStartingHirings.lng;
				}
				
				if(Utils.validateString(mStartingHirings.id)){
					job_id = mStartingHirings.id;
				}
				
				if(Utils.validateString(mStartingHirings.client_name)){
					client_name = mStartingHirings.client_name;
				}
				
				if(Utils.validateString(mStartingHirings.client_email)){
					client_email = mStartingHirings.client_email;
				}
				
				Utils.storeString(mSharedPreferences, Utils.CLIENT_ID, mStartingHirings.client_id);
				
			}
			
			if(mCancelHirings != null){
				((MainActivity) getActivity()).setTitleText("", mCancelHirings.client_name, View.VISIBLE, View.GONE, View.GONE);
				((TextView) mRootView.findViewById(R.id.handyman_name_profile_txt)).setText(mCancelHirings.client_name);
				
				if(Utils.validateString(mCancelHirings.img_path)) {
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
					.load(Utils.IMAGE_URL + mCancelHirings.img_path)
					.placeholder(R.drawable.avtar_images)
					.error(R.drawable.avtar_images)
					.transform(transformation)
					.centerCrop()
					.resize(mDeviceWidth, (int) (mDeviceWidth))
					.into(mHanfymanprofileImg);
				}
				
				if(Utils.validateString(mCancelHirings.order_id)){
					mRootView.findViewById(R.id.hire_customer_order_id_textview).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.hire_customer_order_id).setVisibility(View.VISIBLE);
					((TextView) mRootView.findViewById(R.id.hire_customer_order_id)).setText(mCancelHirings.order_id);
				}
				
				if(Utils.validateString(mCancelHirings.created_date)){
					mRootView.findViewById(R.id.hire_customer_order_date_textview).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.hire_customer_order_date).setVisibility(View.VISIBLE);
					((TextView) mRootView.findViewById(R.id.hire_customer_order_date)).setText(mCancelHirings.created_date);
				}
				

				if(Utils.validateString(mCancelHirings.job_description)){
					job_Description = mCancelHirings.job_description;
					mRootView.findViewById(R.id.hire_customer_job_description_textview).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.hire_customer_job_description).setVisibility(View.VISIBLE);
					((TextView) mRootView.findViewById(R.id.hire_customer_job_description)).setText(job_Description);
				}
				

				if(Utils.validateString(mCancelHirings.appointment_date)){
					mRootView.findViewById(R.id.hire_customer_date_textview).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.hire_customer_date).setVisibility(View.VISIBLE);
					String birth_date = mCancelHirings.appointment_date;
					((TextView) mRootView.findViewById(R.id.hire_customer_date)).setText(birth_date);
				}
				

				if(Utils.validateString(mCancelHirings.appointment_time)){
					mRootView.findViewById(R.id.hire_customer_time_textview).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.hire_customer_time).setVisibility(View.VISIBLE);
					String time = mCancelHirings.appointment_time;
					((TextView) mRootView.findViewById(R.id.hire_customer_time)).setText(time.substring(0, 2)+ ":"+time.substring(3, 5)+"" + time.substring(8, 11));
				}
				

				if(Utils.validateString(mCancelHirings.contact_person)){
					mRootView.findViewById(R.id.hire_customer_contact_person_textview).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.hire_customer_contact_person).setVisibility(View.VISIBLE);
					((TextView) mRootView.findViewById(R.id.hire_customer_contact_person)).setText(mCancelHirings.contact_person);
				}
				
				if(Utils.validateString(mCancelHirings.contact_no)){
					mobile_no = mCancelHirings.contact_no;
					mRootView.findViewById(R.id.hire_customer_contact_no_textview).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.hire_customer_contact_no).setVisibility(View.VISIBLE);
					((TextView) mRootView.findViewById(R.id.hire_customer_contact_no)).setText(mCancelHirings.contact_no);
				}
				
				if(Utils.validateString(mCancelHirings.getAddress())){
					mRootView.findViewById(R.id.hire_customer_address_textview).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.hire_customer_address).setVisibility(View.VISIBLE);
					String address = "", street = "", landmark = "", city = "", state = "", pincode = "", floor ="", apartment ="";

					if(Utils.validateString(mCancelHirings.getFloor()))
						floor = mCancelHirings.getFloor() + ", ";
					if(Utils.validateString(mCancelHirings.getApartment()))
						apartment = mCancelHirings.getApartment() + ", ";
					if(Utils.validateString(mCancelHirings.getAddress()))
						address =  mCancelHirings.getAddress();
//					if(Utils.validateString(mCancelHirings.getStreet()))
//						street = mCancelHirings.getStreet();
//					if(Utils.validateString(mCancelHirings.getLandmark()))
//						landmark = mCancelHirings.getLandmark();
//					if(Utils.validateString(mCancelHirings.getCity_name()))
//						city = mCancelHirings.getCity_name();
//					if(Utils.validateString(mCancelHirings.getState_name()))
//						state = mCancelHirings.getState_name();
//					if(Utils.validateString(mCancelHirings.getPincode()))
//						pincode = mCancelHirings.getPincode();
//					((TextView) mRootView.findViewById(R.id.hire_customer_address)).setText(address + "\n" + street + "\n" + landmark + "\n" + city + " - " + pincode);
					((TextView) mRootView.findViewById(R.id.hire_customer_address)).setText(floor + apartment + address);
					full_address = address;
				}
				
				if(Utils.validateString(mCancelHirings.comment)){
					mRootView.findViewById(R.id.hire_customer_requirment_textview).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.hire_customer_requirment).setVisibility(View.VISIBLE);
					((TextView) mRootView.findViewById(R.id.hire_customer_requirment)).setText(mCancelHirings.comment);
				}
				
				if(Utils.validateString(mCancelHirings.hire_status)){
					status = mCancelHirings.hire_status;
				}
				
				if(Utils.validateString(mCancelHirings.id)){
					status_id = mCancelHirings.id;
				}
				
				if(Utils.validateString(mCancelHirings.lat)){
					latitude = mCancelHirings.lat;
				}
				
				if(Utils.validateString(mCancelHirings.lng)){
					longitude = mCancelHirings.lng;
				}
				
				if(Utils.validateString(mCancelHirings.id)){
					job_id = mCancelHirings.id;
				}
				
				if(Utils.validateString(mCancelHirings.client_name)){
					client_name = mCancelHirings.client_name;
				}
				
				if(Utils.validateString(mCancelHirings.client_email)){
					client_email = mCancelHirings.client_email;
				}
				
				Utils.storeString(mSharedPreferences, Utils.CLIENT_ID, mCancelHirings.client_id);
			}
			
			if(mCompleteHirings != null){
				((MainActivity) getActivity()).setTitleText("", mCompleteHirings.client_name, View.VISIBLE, View.GONE, View.GONE);
				((TextView) mRootView.findViewById(R.id.handyman_name_profile_txt)).setText(mCompleteHirings.client_name);
				
				if(Utils.validateString(mCompleteHirings.img_path)) {
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
					.load(Utils.IMAGE_URL + mCompleteHirings.img_path)
					.placeholder(R.drawable.avtar_images)
					.error(R.drawable.avtar_images)
					.transform(transformation)
					.centerCrop()
					.resize(mDeviceWidth, (int) (mDeviceWidth))
					.into(mHanfymanprofileImg);
				}
				
//				if(Utils.validateString(mCompleteHirings.amount)){
//					mRootView.findViewById(R.id.amount_layout).setVisibility(View.VISIBLE);
//					((TextView) mRootView.findViewById(R.id.amount_txt)).setText(mCompleteHirings.amount + "/-");
//				}
//				
//				if(Utils.validateString(mCompleteHirings.discount)){
//					mRootView.findViewById(R.id.discount_layout).setVisibility(View.VISIBLE);
//					((TextView) mRootView.findViewById(R.id.discount_txt)).setText(mCompleteHirings.discount + "%");
//					
//				}
//				
//				if(Utils.validateString(mCompleteHirings.credit)){
//					mRootView.findViewById(R.id.credit_layout).setVisibility(View.VISIBLE);
//					((TextView) mRootView.findViewById(R.id.credit_txt)).setText(mCompleteHirings.credit);
//					
//				}
				
				if(Utils.validateString(mCompleteHirings.order_id)){
					mRootView.findViewById(R.id.hire_customer_order_id_textview).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.hire_customer_order_id).setVisibility(View.VISIBLE);
					((TextView) mRootView.findViewById(R.id.hire_customer_order_id)).setText(mCompleteHirings.order_id);
				}
				
				if(Utils.validateString(mCompleteHirings.created_date)){
					mRootView.findViewById(R.id.hire_customer_order_date_textview).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.hire_customer_order_date).setVisibility(View.VISIBLE);
					((TextView) mRootView.findViewById(R.id.hire_customer_order_date)).setText(mCompleteHirings.created_date);
				}
				

				if(Utils.validateString(mCompleteHirings.job_description)){
					job_Description = mCompleteHirings.job_description;
					mRootView.findViewById(R.id.hire_customer_job_description_textview).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.hire_customer_job_description).setVisibility(View.VISIBLE);
					((TextView) mRootView.findViewById(R.id.hire_customer_job_description)).setText(job_Description);
				}
				

				if(Utils.validateString(mCompleteHirings.appointment_date)){
					mRootView.findViewById(R.id.hire_customer_date_textview).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.hire_customer_date).setVisibility(View.VISIBLE);
					String birth_date = mCompleteHirings.appointment_date;
					((TextView) mRootView.findViewById(R.id.hire_customer_date)).setText(birth_date);
				}
				

				if(Utils.validateString(mCompleteHirings.appointment_time)){
					mRootView.findViewById(R.id.hire_customer_time_textview).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.hire_customer_time).setVisibility(View.VISIBLE);
					String time = mCompleteHirings.appointment_time;
					((TextView) mRootView.findViewById(R.id.hire_customer_time)).setText(time.substring(0, 2)+ ":"+time.substring(3, 5)+"" + time.substring(8, 11));
				}
				

				if(Utils.validateString(mCompleteHirings.contact_person)){
					mRootView.findViewById(R.id.hire_customer_contact_person_textview).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.hire_customer_contact_person).setVisibility(View.VISIBLE);
					((TextView) mRootView.findViewById(R.id.hire_customer_contact_person)).setText(mCompleteHirings.contact_person);
				}
				
				if(Utils.validateString(mCompleteHirings.contact_no)){
					mobile_no = mCompleteHirings.contact_no;
					mRootView.findViewById(R.id.hire_customer_contact_no_textview).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.hire_customer_contact_no).setVisibility(View.VISIBLE);
					((TextView) mRootView.findViewById(R.id.hire_customer_contact_no)).setText(mCompleteHirings.contact_no);
				}
				
				if(Utils.validateString(mCompleteHirings.getAddress())){
					mRootView.findViewById(R.id.hire_customer_address_textview).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.hire_customer_address).setVisibility(View.VISIBLE);
					String address = "", street = "", landmark = "", city = "", state = "", pincode = "", floor ="", apartment ="";

					if(Utils.validateString(mCompleteHirings.getFloor()))
						floor = mCompleteHirings.getFloor() + ", ";
					if(Utils.validateString(mCompleteHirings.getApartment()))
						apartment = mCompleteHirings.getApartment() + ", ";
					if(Utils.validateString(mCompleteHirings.getAddress()))
						address =  mCompleteHirings.getAddress();
//					if(Utils.validateString(mCompleteHirings.getStreet()))
//						street = mCompleteHirings.getStreet();
//					if(Utils.validateString(mCompleteHirings.getLandmark()))
//						landmark = mCompleteHirings.getLandmark();
//					if(Utils.validateString(mCompleteHirings.getCity_name()))
//						city = mCompleteHirings.getCity_name();
//					if(Utils.validateString(mCompleteHirings.getState_name()))
//						state = mCompleteHirings.getState_name();
//					if(Utils.validateString(mCompleteHirings.getPincode()))
//						pincode = mCompleteHirings.getPincode();
//					((TextView) mRootView.findViewById(R.id.hire_customer_address)).setText(address + "\n" + street + "\n" + landmark + "\n" + city + " - " + pincode);
					((TextView) mRootView.findViewById(R.id.hire_customer_address)).setText(floor + apartment + address);
					full_address = address;
				}
				
				if(Utils.validateString(mCompleteHirings.comment)){
					mRootView.findViewById(R.id.hire_customer_requirment_textview).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.hire_customer_requirment).setVisibility(View.VISIBLE);
					((TextView) mRootView.findViewById(R.id.hire_customer_requirment)).setText(mCompleteHirings.comment);
				}
				
				if(Utils.validateString(mCompleteHirings.hire_status)){
					status = mCompleteHirings.hire_status;
				}
				
				if(Utils.validateString(mCompleteHirings.id)){
					status_id = mCompleteHirings.id;
				}
				
				if(Utils.validateString(mCompleteHirings.lat)){
					latitude = mCompleteHirings.lat;
				}
				
				if(Utils.validateString(mCompleteHirings.lng)){
					longitude = mCompleteHirings.lng;
				}
				
				if(Utils.validateString(mCompleteHirings.id)){
					job_id = mCompleteHirings.id;
				}
				
				if(Utils.validateString(mCompleteHirings.client_name)){
					client_name = mCompleteHirings.client_name;
				}
				
				if(Utils.validateString(mCompleteHirings.client_email)){
					client_email = mCompleteHirings.client_email;
				}
				
				Utils.storeString(mSharedPreferences, Utils.CLIENT_ID, mCompleteHirings.client_id);
				
			}
			
			if(mRejectedHirings != null){
				((MainActivity) getActivity()).setTitleText("", mRejectedHirings.client_name, View.VISIBLE, View.GONE, View.GONE);
				((TextView) mRootView.findViewById(R.id.handyman_name_profile_txt)).setText(mRejectedHirings.client_name);
				
				if(Utils.validateString(mRejectedHirings.img_path)) {
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
					.load(Utils.IMAGE_URL + mRejectedHirings.img_path)
					.placeholder(R.drawable.avtar_images)
					.error(R.drawable.avtar_images)
					.transform(transformation)
					.centerCrop()
					.resize(mDeviceWidth, (int) (mDeviceWidth))
					.into(mHanfymanprofileImg);
				}
				

				if(Utils.validateString(mRejectedHirings.order_id)){
					mRootView.findViewById(R.id.hire_customer_order_id_textview).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.hire_customer_order_id).setVisibility(View.VISIBLE);
					((TextView) mRootView.findViewById(R.id.hire_customer_order_id)).setText(mRejectedHirings.order_id);
				}
				
				if(Utils.validateString(mRejectedHirings.created_date)){
					mRootView.findViewById(R.id.hire_customer_order_date_textview).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.hire_customer_order_date).setVisibility(View.VISIBLE);
					((TextView) mRootView.findViewById(R.id.hire_customer_order_date)).setText(mRejectedHirings.created_date);
				}
				
				if(Utils.validateString(mRejectedHirings.job_description)){
					job_Description = mRejectedHirings.job_description;
					mRootView.findViewById(R.id.hire_customer_job_description_textview).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.hire_customer_job_description).setVisibility(View.VISIBLE);
					((TextView) mRootView.findViewById(R.id.hire_customer_job_description)).setText(job_Description);
				}
				

				if(Utils.validateString(mRejectedHirings.appointment_date)){
					mRootView.findViewById(R.id.hire_customer_date_textview).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.hire_customer_date).setVisibility(View.VISIBLE);
					String birth_date = mRejectedHirings.appointment_date;
					((TextView) mRootView.findViewById(R.id.hire_customer_date)).setText(birth_date);
				}
				

				if(Utils.validateString(mRejectedHirings.appointment_time)){
					mRootView.findViewById(R.id.hire_customer_time_textview).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.hire_customer_time).setVisibility(View.VISIBLE);
					String time = mRejectedHirings.appointment_time;
					((TextView) mRootView.findViewById(R.id.hire_customer_time)).setText(time.substring(0, 2)+ ":"+time.substring(3, 5)+"" + time.substring(8, 11));
				}
				

				if(Utils.validateString(mRejectedHirings.contact_person)){
					mRootView.findViewById(R.id.hire_customer_contact_person_textview).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.hire_customer_contact_person).setVisibility(View.VISIBLE);
					((TextView) mRootView.findViewById(R.id.hire_customer_contact_person)).setText(mRejectedHirings.contact_person);
				}
				
				if(Utils.validateString(mRejectedHirings.contact_no)){
					mobile_no = mRejectedHirings.contact_no;
					mRootView.findViewById(R.id.hire_customer_contact_no_textview).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.hire_customer_contact_no).setVisibility(View.VISIBLE);
					((TextView) mRootView.findViewById(R.id.hire_customer_contact_no)).setText(mRejectedHirings.contact_no);
				}
				
				if(Utils.validateString(mRejectedHirings.getAddress())){
					mRootView.findViewById(R.id.hire_customer_address_textview).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.hire_customer_address).setVisibility(View.VISIBLE);
					String address = "", street = "", landmark = "", city = "", state = "", pincode = "", floor ="", apartment ="";

					if(Utils.validateString(mRejectedHirings.getFloor()))
						floor = mRejectedHirings.getFloor() + ", ";
					if(Utils.validateString(mRejectedHirings.getApartment()))
						apartment = mRejectedHirings.getApartment() + ", ";
					if(Utils.validateString(mRejectedHirings.getAddress()))
						address =  mRejectedHirings.getAddress();
//					if(Utils.validateString(mRejectedHirings.getStreet()))
//						street = mRejectedHirings.getStreet();
//					if(Utils.validateString(mRejectedHirings.getLandmark()))
//						landmark = mRejectedHirings.getLandmark();
//					if(Utils.validateString(mRejectedHirings.getCity_name()))
//						city = mRejectedHirings.getCity_name();
//					if(Utils.validateString(mRejectedHirings.getState_name()))
//						state = mRejectedHirings.getState_name();
//					if(Utils.validateString(mRejectedHirings.getPincode()))
//						pincode = mRejectedHirings.getPincode();
//					((TextView) mRootView.findViewById(R.id.hire_customer_address)).setText(address + "\n" + street + "\n" + landmark + "\n" + city + " - " + pincode);
					((TextView) mRootView.findViewById(R.id.hire_customer_address)).setText(floor  + apartment + address);
					full_address = address;
				}
				
				if(Utils.validateString(mRejectedHirings.comment)){
					mRootView.findViewById(R.id.hire_customer_requirment_textview).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.hire_customer_requirment).setVisibility(View.VISIBLE);
					((TextView) mRootView.findViewById(R.id.hire_customer_requirment)).setText(mRejectedHirings.comment);
				}
				
				if(Utils.validateString(mRejectedHirings.hire_status)){
					status = mRejectedHirings.hire_status;
					status_reject = mRejectedHirings.hire_status;
				}
				
				if(Utils.validateString(mRejectedHirings.id)){
					status_id = mRejectedHirings.id;
				}
				
				if(Utils.validateString(mRejectedHirings.lat)){
					latitude = mRejectedHirings.lat;
				}
				
				if(Utils.validateString(mRejectedHirings.lng)){
					longitude = mRejectedHirings.lng;
				}
				
				if(Utils.validateString(mRejectedHirings.id)){
					job_id = mRejectedHirings.id;
				}
				
				if(Utils.validateString(mRejectedHirings.client_name)){
					client_name = mRejectedHirings.client_name;
				}
				
				if(Utils.validateString(mRejectedHirings.client_email)){
					client_email = mRejectedHirings.client_email;
				}
				
				Utils.storeString(mSharedPreferences, Utils.CLIENT_ID, mRejectedHirings.client_id);
			}
			
			if(mAllHirings != null){
				((MainActivity) getActivity()).setTitleText("", mAllHirings.client_name, View.VISIBLE, View.GONE, View.GONE);
				((TextView) mRootView.findViewById(R.id.handyman_name_profile_txt)).setText(mAllHirings.client_name);
				
				if(Utils.validateString(mAllHirings.img_path)) {
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
					.load(Utils.IMAGE_URL + mAllHirings.img_path)
					.placeholder(R.drawable.avtar_images)
					.error(R.drawable.avtar_images)
					.transform(transformation)
					.centerCrop()
					.resize(mDeviceWidth, (int) (mDeviceWidth))
					.into(mHanfymanprofileImg);
				}
				
//				if(Utils.validateString(mAllHirings.hire_status)){
//					status = mAllHirings.hire_status;
//					if(status.equalsIgnoreCase("completed")){
//						if(Utils.validateString(mAllHirings.amount)){
//							mRootView.findViewById(R.id.amount_layout).setVisibility(View.VISIBLE);
//							((TextView) mRootView.findViewById(R.id.amount_txt)).setText(mAllHirings.amount + "/-");
//						}
//						
//						if(Utils.validateString(mAllHirings.discount)){
//							mRootView.findViewById(R.id.discount_layout).setVisibility(View.VISIBLE);
//							((TextView) mRootView.findViewById(R.id.discount_txt)).setText(mAllHirings.discount + "%");
//							
//						}
//						
//						if(Utils.validateString(mAllHirings.credit)){
//							mRootView.findViewById(R.id.credit_layout).setVisibility(View.VISIBLE);
//							((TextView) mRootView.findViewById(R.id.credit_txt)).setText(mAllHirings.credit);
//							
//						}
//					}
//				}
				
				if(Utils.validateString(mAllHirings.order_id)){
					mRootView.findViewById(R.id.hire_customer_order_id_textview).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.hire_customer_order_id).setVisibility(View.VISIBLE);
					((TextView) mRootView.findViewById(R.id.hire_customer_order_id)).setText(mAllHirings.order_id);
				}
				
				if(Utils.validateString(mAllHirings.created_date)){
					mRootView.findViewById(R.id.hire_customer_order_date_textview).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.hire_customer_order_date).setVisibility(View.VISIBLE);
					((TextView) mRootView.findViewById(R.id.hire_customer_order_date)).setText(mAllHirings.created_date);
				}

				if(Utils.validateString(mAllHirings.job_description)){
					job_Description = mAllHirings.job_description;
					mRootView.findViewById(R.id.hire_customer_job_description_textview).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.hire_customer_job_description).setVisibility(View.VISIBLE);
					((TextView) mRootView.findViewById(R.id.hire_customer_job_description)).setText(job_Description);
				}
				

				if(Utils.validateString(mAllHirings.appointment_date)){
					mRootView.findViewById(R.id.hire_customer_date_textview).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.hire_customer_date).setVisibility(View.VISIBLE);
					String birth_date = mAllHirings.appointment_date;
					((TextView) mRootView.findViewById(R.id.hire_customer_date)).setText(birth_date);
				}
				

				if(Utils.validateString(mAllHirings.appointment_time)){
					mRootView.findViewById(R.id.hire_customer_time_textview).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.hire_customer_time).setVisibility(View.VISIBLE);
					String time = mAllHirings.appointment_time;
					((TextView) mRootView.findViewById(R.id.hire_customer_time)).setText(time.substring(0, 2)+ ":"+time.substring(3, 5)+"" + time.substring(8, 11));
				}
				

				if(Utils.validateString(mAllHirings.contact_person)){
					mRootView.findViewById(R.id.hire_customer_contact_person_textview).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.hire_customer_contact_person).setVisibility(View.VISIBLE);
					((TextView) mRootView.findViewById(R.id.hire_customer_contact_person)).setText(mAllHirings.contact_person);
				}
				
				if(Utils.validateString(mAllHirings.contact_no)){
					mobile_no = mAllHirings.contact_no;
					mRootView.findViewById(R.id.hire_customer_contact_no_textview).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.hire_customer_contact_no).setVisibility(View.VISIBLE);
					((TextView) mRootView.findViewById(R.id.hire_customer_contact_no)).setText(mAllHirings.contact_no);
				}
				
				if(Utils.validateString(mAllHirings.getAddress())){
					mRootView.findViewById(R.id.hire_customer_address_textview).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.hire_customer_address).setVisibility(View.VISIBLE);
					String address = "", street = "", landmark = "", city = "", state = "", pincode = "", floor ="", apartment ="";

					if(Utils.validateString(mAllHirings.getFloor()))
						floor = mAllHirings.getFloor() + ", ";
					if(Utils.validateString(mAllHirings.getApartment()))
						apartment = mAllHirings.getApartment() + ", ";
					if(Utils.validateString(mAllHirings.getAddress()))
						address =  mAllHirings.getAddress();
//					if(Utils.validateString(mAllHirings.getStreet()))
//						street = mAllHirings.getStreet();
//					if(Utils.validateString(mAllHirings.getLandmark()))
//						landmark = mAllHirings.getLandmark();
//					if(Utils.validateString(mAllHirings.getCity_name()))
//						city = mAllHirings.getCity_name();
//					if(Utils.validateString(mAllHirings.getState_name()))
//						state = mAllHirings.getState_name();
//					if(Utils.validateString(mAllHirings.getPincode()))
//						pincode = mAllHirings.getPincode();
//					((TextView) mRootView.findViewById(R.id.hire_customer_address)).setText(address + "\n" + street + "\n" + landmark + "\n" + city + " - " + pincode);
					((TextView) mRootView.findViewById(R.id.hire_customer_address)).setText(floor + apartment + address);
					full_address = address;
				}
				
				if(Utils.validateString(mAllHirings.comment)){
					mRootView.findViewById(R.id.hire_customer_requirment_textview).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.hire_customer_requirment).setVisibility(View.VISIBLE);
					((TextView) mRootView.findViewById(R.id.hire_customer_requirment)).setText(mAllHirings.comment);
				}
				
				if(Utils.validateString(mAllHirings.hire_status)){
					status = mAllHirings.hire_status;
				}
				
				if(Utils.validateString(mAllHirings.id)){
					status_id = mAllHirings.id;
				}
				
				if(Utils.validateString(mAllHirings.lat)){
					latitude = mAllHirings.lat;
				}
				
				if(Utils.validateString(mAllHirings.lng)){
					longitude = mAllHirings.lng;
				}
				
				if(Utils.validateString(mAllHirings.id)){
					job_id = mAllHirings.id;
				}
				
				if(Utils.validateString(mAllHirings.client_name)){
					client_name = mAllHirings.client_name;
				}
				
				if(Utils.validateString(mAllHirings.client_email)){
					client_email = mAllHirings.client_email;
				}
				
				Utils.storeString(mSharedPreferences, Utils.CLIENT_ID, mAllHirings.client_id);
			}
			
		}
		
		if (status.equalsIgnoreCase("pending")) {
			mAccept.setVisibility(View.VISIBLE);
			mStart.setVisibility(View.GONE);
			mCancel.setVisibility(View.VISIBLE);
			mComplete.setVisibility(View.GONE);
			mReject.setVisibility(View.GONE);

		} else if (status.equalsIgnoreCase("active")) {
			mAccept.setVisibility(View.GONE);
			mStart.setVisibility(View.VISIBLE);
			mCancel.setVisibility(View.VISIBLE);
			mComplete.setVisibility(View.GONE);
			mReject.setVisibility(View.GONE);

		} else if (status.equalsIgnoreCase("start")) {
			mAccept.setVisibility(View.GONE);
			mStart.setVisibility(View.GONE);
			mCancel.setVisibility(View.VISIBLE);
			mComplete.setVisibility(View.VISIBLE);
			mReject.setVisibility(View.GONE);

		} else if (status.equalsIgnoreCase("cancelled")) {
			mCancel.setClickable(false);
			mAccept.setVisibility(View.GONE);
			mStart.setVisibility(View.GONE);
			mCancel.setVisibility(View.VISIBLE);
			mComplete.setVisibility(View.GONE);
			mReject.setVisibility(View.GONE);

		} else if (status.equalsIgnoreCase("completed")) {
			mComplete.setClickable(false);
			mAccept.setVisibility(View.GONE);
			mStart.setVisibility(View.GONE);
			mCancel.setVisibility(View.GONE);
			mComplete.setVisibility(View.VISIBLE);
			mReject.setVisibility(View.GONE);

		} else if(status.equalsIgnoreCase("declined")){
			mAccept.setVisibility(View.GONE);
			mStart.setVisibility(View.GONE);
			mCancel.setVisibility(View.GONE);
			mComplete.setVisibility(View.GONE);
			mReject.setVisibility(View.VISIBLE);
		}
//
//		if(Utils.validateString(status_reject)) {
//			if (status_reject.equalsIgnoreCase("cancelled")) {
//				mAccept.setVisibility(View.GONE);
//				mStart.setVisibility(View.GONE);
//				mCancel.setVisibility(View.GONE);
//				mComplete.setVisibility(View.GONE);
//				mReject.setVisibility(View.VISIBLE);
//			}
//		}
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.accept_btn:
			
			if (status.equalsIgnoreCase("pending")) {
				Utils.storeString(mSharedPreferences, Utils.ACCEPT, "ACCEPT");
			}
			
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle("Alert");
			builder.setCancelable(false);
			builder.setMessage("Are you sure for accept.");
			builder.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							
//							mAccept.setVisibility(View.GONE);
//							mStart.setVisibility(View.VISIBLE);
//							mCancel.setVisibility(View.VISIBLE);
//							mComplete.setVisibility(View.GONE);

							onChangeStatus(status_id,"active","1");
							
						}
					});
			builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					if (status.equalsIgnoreCase("pending")) {
						Utils.storeString(mSharedPreferences, Utils.ACCEPT, "");
					}
				}
				
			});
			AlertDialog alertDialog = builder.create();
			alertDialog.show();
			break;
			
		case R.id.start_btn:
			
			if (status.equalsIgnoreCase("active")) {
				Utils.storeString(mSharedPreferences, Utils.START, "START");
			}
			
			AlertDialog.Builder builder2 = new AlertDialog.Builder(getActivity());
			builder2.setTitle("Alert");
			builder2.setCancelable(false);
			builder2.setMessage("Are you sure for start.");
			builder2.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							
//							mAccept.setVisibility(View.GONE);
//							mStart.setVisibility(View.GONE);
//							mCancel.setVisibility(View.VISIBLE);
//							mComplete.setVisibility(View.VISIBLE);
							
							onChangeStatus(status_id,"start","1");
							
						}
					});
			builder2.setNegativeButton("CANCEL", new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					if (status.equalsIgnoreCase("active")) {
						Utils.storeString(mSharedPreferences, Utils.START, "");
					}
				}
				
			});
			AlertDialog alertDialog2 = builder2.create();
			alertDialog2.show();
			
			break;
			
		case R.id.cancel_btn:
			
			if (status.equalsIgnoreCase("pending")) {
				Utils.storeString(mSharedPreferences, Utils.CANCEL, "CANCEL");
			} else if (status.equalsIgnoreCase("active")) {
				Utils.storeString(mSharedPreferences, Utils.CANCEL_A, "CANCEL_A");
			} else if (status.equalsIgnoreCase("start")) {
				Utils.storeString(mSharedPreferences, Utils.CANCEL_S, "CANCEL_S");
			}
			
			AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
			builder1.setTitle("Alert");
			builder1.setCancelable(false);
			builder1.setMessage("Are you sure for cancel.");
			builder1.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							
							mCancel.setClickable(false);
//							mAccept.setVisibility(View.GONE);
//							mStart.setVisibility(View.GONE);
//							mCancel.setVisibility(View.VISIBLE);
//							mComplete.setVisibility(View.GONE);
							
							onChangeStatus(status_id,"cancelled","1");
							
						}
					});
			builder1.setNegativeButton("CANCEL", new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					if (status.equalsIgnoreCase("pending")) {
						Utils.storeString(mSharedPreferences, Utils.CANCEL, "");
					} else if (status.equalsIgnoreCase("active")) {
						Utils.storeString(mSharedPreferences, Utils.CANCEL_A, "");
					} else if (status.equalsIgnoreCase("start")) {
						Utils.storeString(mSharedPreferences, Utils.CANCEL_S, "");
					}
				}
				
			});
			AlertDialog alertDialog1 = builder1.create();
			alertDialog1.show();
			break;
			
		case R.id.completed_btn:
			mComplete.setClickable(false);
			mAccept.setVisibility(View.GONE);
			mStart.setVisibility(View.GONE);
			mCancel.setVisibility(View.GONE);
			mComplete.setVisibility(View.VISIBLE);

			if (status.equalsIgnoreCase("start")) {
				Utils.storeString(mSharedPreferences, Utils.COMPELETE, "COMPELETE");
			} 
//			else if (status.equalsIgnoreCase("active")) {
//				Utils.storeString(mSharedPreferences, Utils.COMPELETE_A, "COMPELETE_A");
//			}
			
			SignatureFragment signatureFragment = new SignatureFragment();
			Bundle bundle = new Bundle();
			bundle.putString(Utils.HANDYMAN_JOB_DESCRIPTION, job_Description);
			bundle.putString(Utils.JOB_ID, job_id);
			bundle.putString(Utils.STATUS_ID, status_id);
			bundle.putString(Utils.CLIENT_ID, mSharedPreferences.getString(Utils.CLIENT_ID, ""));
			signatureFragment.setArguments(bundle);
			getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, signatureFragment).addToBackStack(TAG).commit();
			break;
			
		case R.id.chat_img:
			PackageManager pm = getActivity().getPackageManager();
//			String appPackageName = getActivity().getPackageName();
			try {

				Intent waIntent = new Intent(Intent.ACTION_SEND);
				waIntent.setType("text/plain");
				String text = "Hi, this is your handyman";

				PackageInfo info = pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);
				// Check if package exists or not. If not then code in catch block will be called
				waIntent.setPackage("com.whatsapp");

				waIntent.putExtra(Intent.EXTRA_TEXT, text);
				startActivity(Intent.createChooser(waIntent, "Share with"));

			} catch (NameNotFoundException e) {
				Toast.makeText(getActivity(), "WhatsApp not Installed",	Toast.LENGTH_SHORT).show();
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.whatsapp&hl=en")));
			} 
			break;
			
		case R.id.call_img:
			Uri number = Uri.parse("tel:"+ mobile_no);
			Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
			startActivity(callIntent);
			
			break;
			
		case R.id.map_img:
			
//			MapHandymanFragment mapHandymanFragment = new MapHandymanFragment();
//			Bundle bundle = new Bundle();
//			bundle.putString(Utils.HANDYMAN_LATITUDE, latitude);
//			bundle.putString(Utils.HANDYMAN_LONGITUDE, longitude);
//			mapHandymanFragment.setArguments(bundle);
//			getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, mapHandymanFragment).addToBackStack(null).commit();
			
//			Intent intent = new Intent(android.content.Intent.ACTION_VIEW, 
//			Uri.parse("http://maps.google.com/maps?saddr=20.344,34.34&daddr=23.5666,45.345"));
//			startActivity(intent);
//			
//			String uri = String.format(Locale.ENGLISH, "geo:%f,%f", latitude, longitude);
//			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
//			startActivity(intent);
		
			if(Utils.validateString(full_address)){
				String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?saddr=%f,%f&daddr=%f,%f (%s)", SplashActivity.latitude, SplashActivity.longitude,Double.parseDouble(latitude), Double.parseDouble(longitude), full_address );
//				String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?saddr=%f,%f&daddr=%s", SplashActivity.latitude, SplashActivity.longitude, full_address );
				Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
				startActivity(i);
			} /*else {
				String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?saddr=%f,%f&daddr=%f,%f", SplashActivity.latitude, SplashActivity.longitude, Double.parseDouble(latitude), Double.parseDouble(longitude));
				Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
				startActivity(i);
			}*/
			break;
			
		case R.id.report_img:
			RegisterComplainFragment registerComplainFragment = new RegisterComplainFragment();
			Bundle bundle1 = new Bundle();
			bundle1.putString(Utils.CLIENT_NAME, client_name);
			bundle1.putString(Utils.CLIENT_EMAIL, client_email);
			registerComplainFragment.setArguments(bundle1);
			getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, registerComplainFragment).addToBackStack(TAG).commit();
			break;
			
		}
	}	
	
	private void onChangeStatus(String id, String hire_status, String service_updated_by) {
		
        if (Utils.checkInternetConnection(getActivity())) {
        	GetHandymanChangeStatusRequestTask getHandymanChangeStatusRequestTask = new GetHandymanChangeStatusRequestTask(getActivity());
        	getHandymanChangeStatusRequestTask.setAsyncCallListener(new AsyncCallListener() {

                @Override
                public void onResponseReceived(Object response) {
                	MyHiringsModel myHiringsModel = (MyHiringsModel) response;
                    if(myHiringsModel.success.equalsIgnoreCase("1")) {
//                            Toast.makeText(getActivity(), myHiringsModel.message , Toast.LENGTH_SHORT).show();
                            getActivity().getSupportFragmentManager().popBackStack();
                        
                        } else if (myHiringsModel.success.equalsIgnoreCase("0")) {
//                            Toast.makeText(getActivity(), myHiringsModel.message , Toast.LENGTH_SHORT).show();
                        	Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), myHiringsModel.message);
                        }
                    }

                @Override
                public void onErrorReceived(String error) {
                    Toast.makeText(getActivity(), getString(R.string.try_again), Toast.LENGTH_SHORT).show();
                }
            });
        	getHandymanChangeStatusRequestTask.execute(id, hire_status,service_updated_by);
        } else {
            Toast.makeText(getActivity(), getString(R.string.connection), Toast.LENGTH_SHORT).show();
        }
    }
	
}
