package com.android.handyman.fragment;

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

import com.android.handyman.MainActivity;
import com.android.handyman.R;
import com.android.handyman.model.HireHandymanModel;
import com.android.handyman.model.MyHiringsModel;
import com.android.handyman.service.AsyncCallListener;
import com.android.handyman.service.CheckRateHandymanRequestTask;
import com.android.handyman.service.GetHandymanChangeStatusRequestTask;
import com.android.handyman.service.Utils;
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
	MyHiringsModel mStartedHirings = new MyHiringsModel();
	MyHiringsModel mAllHirings = new MyHiringsModel();
	
	String category_id, status, status_id, handyman_id = "", handyman_name = "", handyman_email = "", handyman_rating = "",job_Description = "", /*closing_status,*/ mobile_no = "";
	
	Fragment fr;
	View mRootView;
	ImageView mHanfymanprofileImg;
	private int mDeviceWidth = 480;
	TextView mCancel;

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
		
		mRootView.findViewById(R.id.chat_img).setOnClickListener(this);
		mRootView.findViewById(R.id.call_img).setOnClickListener(this);
		mRootView.findViewById(R.id.rating_img).setOnClickListener(this);
		mRootView.findViewById(R.id.report_img).setOnClickListener(this);
		mCancel = (TextView) mRootView.findViewById(R.id.cancel_Button);
		mCancel.setOnClickListener(this);
		
		if(getArguments() != null) {
			mPendingHirings =  (MyHiringsModel) getArguments().get(Utils.HANDYMAN_HIRE_PENDING_DETAILS);
			mAcceptedHirings =  (MyHiringsModel) getArguments().get(Utils.HANDYMAN_HIRE_ACCEPT_DETAILS);
			mStartedHirings =  (MyHiringsModel) getArguments().get(Utils.HANDYMAN_HIRE_START_DETAILS);
			mCancelHirings =  (MyHiringsModel) getArguments().get(Utils.HANDYMAN_HIRE_CANCEL_DETAILS);
			mCompleteHirings =  (MyHiringsModel) getArguments().get(Utils.HANDYMAN_HIRE_COMPLETE_DETAILS);
			mRejectedHirings =  (MyHiringsModel) getArguments().get(Utils.HANDYMAN_HIRE_REJECT_DETAILS);
			mAllHirings =  (MyHiringsModel) getArguments().get(Utils.HANDYMAN_HIRE_ALL_DETAILS);
			
			if(mPendingHirings != null){
				((MainActivity) getActivity()).setTitleText("", mPendingHirings.handyman_name, View.VISIBLE, View.GONE, View.GONE, View.GONE);
				((TextView) mRootView.findViewById(R.id.handyman_name_profile_txt)).setText(mPendingHirings.handyman_name);
				
				if(Utils.validateString(mPendingHirings.handyman_img)) {
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
					.load(Utils.IMAGE_URL + mPendingHirings.handyman_img)
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
              	    ((TextView) mRootView.findViewById(R.id.hire_customer_time)).setText(time.substring(0, 2)+ ":"+time.substring(3, 5)+"" + time.substring(8,11));
				}
				

				if(Utils.validateString(mPendingHirings.contact_person)){
					mRootView.findViewById(R.id.hire_customer_contact_person_textview).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.hire_customer_contact_person).setVisibility(View.VISIBLE);
					((TextView) mRootView.findViewById(R.id.hire_customer_contact_person)).setText(mPendingHirings.contact_person);
				}
				
				if(Utils.validateString(mPendingHirings.contact_no)){
					mobile_no = mPendingHirings.hamdyman_mobile;
					mRootView.findViewById(R.id.hire_customer_contact_no_textview).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.hire_customer_contact_no).setVisibility(View.VISIBLE);
					((TextView) mRootView.findViewById(R.id.hire_customer_contact_no)).setText(mPendingHirings.contact_no);
				}
				
				if(Utils.validateString(mPendingHirings.getAddress())){
					mRootView.findViewById(R.id.hire_customer_address_textview).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.hire_customer_address).setVisibility(View.VISIBLE);
					String address = "", street = "", landmark = "", city = "", state = "", pincode = "";
					address =  mPendingHirings.getAddress();
					if(Utils.validateString(mPendingHirings.getStreet()))
						street = mPendingHirings.getStreet();
					if(Utils.validateString(mPendingHirings.getLandmark()))
						landmark = mPendingHirings.getLandmark();
					if(Utils.validateString(mPendingHirings.getCity_name()))
						city = mPendingHirings.getCity_name();
					if(Utils.validateString(mPendingHirings.getState_name()))
						state = mPendingHirings.getState_name();
					if(Utils.validateString(mPendingHirings.getPincode()))
						pincode = mPendingHirings.getPincode();
					((TextView) mRootView.findViewById(R.id.hire_customer_address)).setText(address + "\n" + street + "\n" + landmark + "\n" + city + " - " + pincode);
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
				
				if(Utils.validateString(mPendingHirings.handyman_id)){
					handyman_id = mPendingHirings.handyman_id;
				}
				
				if(Utils.validateString(mPendingHirings.handyman_name)){
					handyman_name = mPendingHirings.handyman_name;
				}
				
				if(Utils.validateString(mPendingHirings.email)){
					handyman_email = mPendingHirings.email;
				}
				
				if(Utils.validateString(mPendingHirings.rating)){
					handyman_rating = mPendingHirings.rating;
				}
				
//				if(Utils.validateString(mPendingHirings.closing_status)){
//					closing_status = mPendingHirings.closing_status;
//				}
				
				
//				Utils.storeString(mSharedPreferences, Utils.CLIENT_ID, mPendingHirings.client_id);
				
			}
			
			if(mAcceptedHirings != null){
				((MainActivity) getActivity()).setTitleText("", mAcceptedHirings.handyman_name, View.VISIBLE, View.GONE, View.GONE,View.GONE);
				((TextView) mRootView.findViewById(R.id.handyman_name_profile_txt)).setText(mAcceptedHirings.handyman_name);
				
				if(Utils.validateString(mAcceptedHirings.handyman_img)) {
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
					.load(Utils.IMAGE_URL + mAcceptedHirings.handyman_img)
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
					((TextView) mRootView.findViewById(R.id.hire_customer_time)).setText(time.substring(0, 2)+ ":"+time.substring(3, 5)+"" + time.substring(8,11));
				}
				

				if(Utils.validateString(mAcceptedHirings.contact_person)){
					mRootView.findViewById(R.id.hire_customer_contact_person_textview).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.hire_customer_contact_person).setVisibility(View.VISIBLE);
					((TextView) mRootView.findViewById(R.id.hire_customer_contact_person)).setText(mAcceptedHirings.contact_person);
				}
				
				if(Utils.validateString(mAcceptedHirings.contact_no)){
					mobile_no = mAcceptedHirings.hamdyman_mobile;
					mRootView.findViewById(R.id.hire_customer_contact_no_textview).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.hire_customer_contact_no).setVisibility(View.VISIBLE);
					((TextView) mRootView.findViewById(R.id.hire_customer_contact_no)).setText(mAcceptedHirings.contact_no);
				}
				
				if(Utils.validateString(mAcceptedHirings.getAddress())){
					mRootView.findViewById(R.id.hire_customer_address_textview).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.hire_customer_address).setVisibility(View.VISIBLE);
					String address = "", street = "", landmark = "", city = "", state = "", pincode = "";
					address =  mAcceptedHirings.getAddress();
					if(Utils.validateString(mAcceptedHirings.getStreet()))
						street = mAcceptedHirings.getStreet();
					if(Utils.validateString(mAcceptedHirings.getLandmark()))
						landmark = mAcceptedHirings.getLandmark();
					if(Utils.validateString(mAcceptedHirings.getCity_name()))
						city = mAcceptedHirings.getCity_name();
					if(Utils.validateString(mAcceptedHirings.getState_name()))
						state = mAcceptedHirings.getState_name();
					if(Utils.validateString(mAcceptedHirings.getPincode()))
						pincode = mAcceptedHirings.getPincode();
					((TextView) mRootView.findViewById(R.id.hire_customer_address)).setText(address + "\n" + street + "\n" + landmark + "\n" + city + " - " + pincode);
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
				
				if(Utils.validateString(mAcceptedHirings.handyman_id)){
					handyman_id = mAcceptedHirings.handyman_id;
				}
				
				if(Utils.validateString(mAcceptedHirings.handyman_name)){
					handyman_name = mAcceptedHirings.handyman_name;
				}
				
				if(Utils.validateString(mAcceptedHirings.email)){
					handyman_email = mAcceptedHirings.email;
				}
				
				if(Utils.validateString(mAcceptedHirings.rating)){
					handyman_rating = mAcceptedHirings.rating;
				}
				
//				if(Utils.validateString(mAcceptedHirings.closing_status)){
//					closing_status = mAcceptedHirings.closing_status;
//				}
				
//				Utils.storeString(mSharedPreferences, Utils.CLIENT_ID, mAcceptedHirings.client_id);
				
			}
			
			if(mStartedHirings != null){
				((MainActivity) getActivity()).setTitleText("", mStartedHirings.handyman_name, View.VISIBLE, View.GONE, View.GONE,View.GONE);
				((TextView) mRootView.findViewById(R.id.handyman_name_profile_txt)).setText(mStartedHirings.handyman_name);
				
				if(Utils.validateString(mStartedHirings.handyman_img)) {
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
					.load(Utils.IMAGE_URL + mStartedHirings.handyman_img)
					.placeholder(R.drawable.avtar_images)
					.error(R.drawable.avtar_images)
					.transform(transformation)
					.centerCrop()
					.resize(mDeviceWidth, (int) (mDeviceWidth))
					.into(mHanfymanprofileImg);
				}
				
				if(Utils.validateString(mStartedHirings.order_id)){
					mRootView.findViewById(R.id.hire_customer_order_id_textview).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.hire_customer_order_id).setVisibility(View.VISIBLE);
					((TextView) mRootView.findViewById(R.id.hire_customer_order_id)).setText(mStartedHirings.order_id);
				}
				
				if(Utils.validateString(mStartedHirings.created_date)){
					mRootView.findViewById(R.id.hire_customer_order_date_textview).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.hire_customer_order_date).setVisibility(View.VISIBLE);
					((TextView) mRootView.findViewById(R.id.hire_customer_order_date)).setText(mStartedHirings.created_date);
				}

				if(Utils.validateString(mStartedHirings.job_description)){
					job_Description = mStartedHirings.job_description;
					mRootView.findViewById(R.id.hire_customer_job_description_textview).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.hire_customer_job_description).setVisibility(View.VISIBLE);
					((TextView) mRootView.findViewById(R.id.hire_customer_job_description)).setText(job_Description);
				}
				

				if(Utils.validateString(mStartedHirings.appointment_date)){
					mRootView.findViewById(R.id.hire_customer_date_textview).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.hire_customer_date).setVisibility(View.VISIBLE);
					String birth_date = mStartedHirings.appointment_date;
					((TextView) mRootView.findViewById(R.id.hire_customer_date)).setText(birth_date);
				}
				

				if(Utils.validateString(mStartedHirings.appointment_time)){
					mRootView.findViewById(R.id.hire_customer_time_textview).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.hire_customer_time).setVisibility(View.VISIBLE);
					String time = mStartedHirings.appointment_time;
					((TextView) mRootView.findViewById(R.id.hire_customer_time)).setText(time.substring(0, 2)+ ":"+time.substring(3, 5)+"" + time.substring(8,11));
				}
				

				if(Utils.validateString(mStartedHirings.contact_person)){
					mRootView.findViewById(R.id.hire_customer_contact_person_textview).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.hire_customer_contact_person).setVisibility(View.VISIBLE);
					((TextView) mRootView.findViewById(R.id.hire_customer_contact_person)).setText(mStartedHirings.contact_person);
				}
				
				if(Utils.validateString(mStartedHirings.contact_no)){
					mobile_no = mStartedHirings.hamdyman_mobile;
					mRootView.findViewById(R.id.hire_customer_contact_no_textview).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.hire_customer_contact_no).setVisibility(View.VISIBLE);
					((TextView) mRootView.findViewById(R.id.hire_customer_contact_no)).setText(mStartedHirings.contact_no);
				}
				
				if(Utils.validateString(mStartedHirings.getAddress())){
					mRootView.findViewById(R.id.hire_customer_address_textview).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.hire_customer_address).setVisibility(View.VISIBLE);
					String address = "", street = "", landmark = "", city = "", state = "", pincode = "";
					address =  mStartedHirings.getAddress();
					if(Utils.validateString(mStartedHirings.getStreet()))
						street = mStartedHirings.getStreet();
					if(Utils.validateString(mStartedHirings.getLandmark()))
						landmark = mStartedHirings.getLandmark();
					if(Utils.validateString(mStartedHirings.getCity_name()))
						city = mStartedHirings.getCity_name();
					if(Utils.validateString(mStartedHirings.getState_name()))
						state = mStartedHirings.getState_name();
					if(Utils.validateString(mStartedHirings.getPincode()))
						pincode = mStartedHirings.getPincode();
					((TextView) mRootView.findViewById(R.id.hire_customer_address)).setText(address + "\n" + street + "\n" + landmark + "\n" + city + " - " + pincode);
				}
				
				if(Utils.validateString(mStartedHirings.comment)){
					mRootView.findViewById(R.id.hire_customer_requirment_textview).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.hire_customer_requirment).setVisibility(View.VISIBLE);
					((TextView) mRootView.findViewById(R.id.hire_customer_requirment)).setText(mStartedHirings.comment);
				}
				
				if(Utils.validateString(mStartedHirings.hire_status)){
					status = mStartedHirings.hire_status;
				}
				
				if(Utils.validateString(mStartedHirings.id)){
					status_id = mStartedHirings.id;
				}
				
				if(Utils.validateString(mStartedHirings.handyman_id)){
					handyman_id = mStartedHirings.handyman_id;
				}
				
				if(Utils.validateString(mStartedHirings.handyman_name)){
					handyman_name = mStartedHirings.handyman_name;
				}
				
				if(Utils.validateString(mStartedHirings.email)){
					handyman_email = mStartedHirings.email;
				}
				
				if(Utils.validateString(mStartedHirings.rating)){
					handyman_rating = mStartedHirings.rating;
				}
				
//				if(Utils.validateString(mStartedHirings.closing_status)){
//					closing_status = mStartedHirings.closing_status;
//				}
				
//				Utils.storeString(mSharedPreferences, Utils.CLIENT_ID, mStartedHirings.client_id);
				
			}
			
			if(mCancelHirings != null){
				((MainActivity) getActivity()).setTitleText("", mCancelHirings.handyman_name, View.VISIBLE, View.GONE, View.GONE,View.GONE);
				((TextView) mRootView.findViewById(R.id.handyman_name_profile_txt)).setText(mCancelHirings.handyman_name);
				
				if(Utils.validateString(mCancelHirings.handyman_img)) {
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
					.load(Utils.IMAGE_URL + mCancelHirings.handyman_img)
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
					((TextView) mRootView.findViewById(R.id.hire_customer_time)).setText(time.substring(0, 2)+ ":"+time.substring(3, 5)+"" + time.substring(8,11));
				}
				

				if(Utils.validateString(mCancelHirings.contact_person)){
					mRootView.findViewById(R.id.hire_customer_contact_person_textview).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.hire_customer_contact_person).setVisibility(View.VISIBLE);
					((TextView) mRootView.findViewById(R.id.hire_customer_contact_person)).setText(mCancelHirings.contact_person);
				}
				
				if(Utils.validateString(mCancelHirings.contact_no)){
					mobile_no = mCancelHirings.hamdyman_mobile;
					mRootView.findViewById(R.id.hire_customer_contact_no_textview).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.hire_customer_contact_no).setVisibility(View.VISIBLE);
					((TextView) mRootView.findViewById(R.id.hire_customer_contact_no)).setText(mCancelHirings.contact_no);
				}
				
				if(Utils.validateString(mCancelHirings.getAddress())){
					mRootView.findViewById(R.id.hire_customer_address_textview).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.hire_customer_address).setVisibility(View.VISIBLE);
					String address = "", street = "", landmark = "", city = "", state = "", pincode = "";
					address =  mCancelHirings.getAddress();
					if(Utils.validateString(mCancelHirings.getStreet()))
						street = mCancelHirings.getStreet();
					if(Utils.validateString(mCancelHirings.getLandmark()))
						landmark = mCancelHirings.getLandmark();
					if(Utils.validateString(mCancelHirings.getCity_name()))
						city = mCancelHirings.getCity_name();
					if(Utils.validateString(mCancelHirings.getState_name()))
						state = mCancelHirings.getState_name();
					if(Utils.validateString(mCancelHirings.getPincode()))
						pincode = mCancelHirings.getPincode();
					((TextView) mRootView.findViewById(R.id.hire_customer_address)).setText(address + "\n" + street + "\n" + landmark + "\n" + city + " - " + pincode);
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
				
				if(Utils.validateString(mCancelHirings.handyman_id)){
					handyman_id = mCancelHirings.handyman_id;
				}
				
				if(Utils.validateString(mCancelHirings.handyman_name)){
					handyman_name = mCancelHirings.handyman_name;
				}
				
				if(Utils.validateString(mCancelHirings.email)){
					handyman_email = mCancelHirings.email;
				}
				
				if(Utils.validateString(mCancelHirings.rating)){
					handyman_rating = mCancelHirings.rating;
				}
				
//				Utils.storeString(mSharedPreferences, Utils.CLIENT_ID, mCancelHirings.client_id);
			}
			
			if(mCompleteHirings != null){
				((MainActivity) getActivity()).setTitleText("", mCompleteHirings.handyman_name, View.VISIBLE, View.GONE, View.GONE,View.GONE);
				((TextView) mRootView.findViewById(R.id.handyman_name_profile_txt)).setText(mCompleteHirings.handyman_name);
				
				if(Utils.validateString(mCompleteHirings.handyman_img)) {
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
					.load(Utils.IMAGE_URL + mCompleteHirings.handyman_img)
					.placeholder(R.drawable.avtar_images)
					.error(R.drawable.avtar_images)
					.transform(transformation)
					.centerCrop()
					.resize(mDeviceWidth, (int) (mDeviceWidth))
					.into(mHanfymanprofileImg);
				}
				

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
					((TextView) mRootView.findViewById(R.id.hire_customer_time)).setText(time.substring(0, 2)+ ":"+time.substring(3, 5)+"" + time.substring(8,11));
				}
				

				if(Utils.validateString(mCompleteHirings.contact_person)){
					mRootView.findViewById(R.id.hire_customer_contact_person_textview).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.hire_customer_contact_person).setVisibility(View.VISIBLE);
					((TextView) mRootView.findViewById(R.id.hire_customer_contact_person)).setText(mCompleteHirings.contact_person);
				}
				
				if(Utils.validateString(mCompleteHirings.contact_no)){
					mobile_no = mCompleteHirings.hamdyman_mobile;
					mRootView.findViewById(R.id.hire_customer_contact_no_textview).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.hire_customer_contact_no).setVisibility(View.VISIBLE);
					((TextView) mRootView.findViewById(R.id.hire_customer_contact_no)).setText(mCompleteHirings.contact_no);
				}
				
				if(Utils.validateString(mCompleteHirings.getAddress())){
					mRootView.findViewById(R.id.hire_customer_address_textview).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.hire_customer_address).setVisibility(View.VISIBLE);
					String address = "", street = "", landmark = "", city = "", state = "", pincode = "";
					address =  mCompleteHirings.getAddress();
					if(Utils.validateString(mCompleteHirings.getStreet()))
						street = mCompleteHirings.getStreet();
					if(Utils.validateString(mCompleteHirings.getLandmark()))
						landmark = mCompleteHirings.getLandmark();
					if(Utils.validateString(mCompleteHirings.getCity_name()))
						city = mCompleteHirings.getCity_name();
					if(Utils.validateString(mCompleteHirings.getState_name()))
						state = mCompleteHirings.getState_name();
					if(Utils.validateString(mCompleteHirings.getPincode()))
						pincode = mCompleteHirings.getPincode();
					((TextView) mRootView.findViewById(R.id.hire_customer_address)).setText(address + "\n" + street + "\n" + landmark + "\n" + city + " - " + pincode);
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
				
				if(Utils.validateString(mCompleteHirings.handyman_id)){
					handyman_id = mCompleteHirings.handyman_id;
				}
				
				if(Utils.validateString(mCompleteHirings.handyman_name)){
					handyman_name = mCompleteHirings.handyman_name;
				}
				
				if(Utils.validateString(mCompleteHirings.email)){
					handyman_email = mCompleteHirings.email;
				}
				
				if(Utils.validateString(mCompleteHirings.rating)){
					handyman_rating = mCompleteHirings.rating;
				}
				
//				Utils.storeString(mSharedPreferences, Utils.CLIENT_ID, mCompleteHirings.client_id);
				
			}
			
			if(mRejectedHirings != null){
				((MainActivity) getActivity()).setTitleText("", mRejectedHirings.handyman_name, View.VISIBLE, View.GONE, View.GONE,View.GONE);
				((TextView) mRootView.findViewById(R.id.handyman_name_profile_txt)).setText(mRejectedHirings.handyman_name);
				
				if(Utils.validateString(mRejectedHirings.handyman_img)) {
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
					.load(Utils.IMAGE_URL + mRejectedHirings.handyman_img)
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
					((TextView) mRootView.findViewById(R.id.hire_customer_time)).setText(time.substring(0, 2)+ ":"+time.substring(3, 5)+"" + time.substring(8,11));
				}
				

				if(Utils.validateString(mRejectedHirings.contact_person)){
					mRootView.findViewById(R.id.hire_customer_contact_person_textview).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.hire_customer_contact_person).setVisibility(View.VISIBLE);
					((TextView) mRootView.findViewById(R.id.hire_customer_contact_person)).setText(mRejectedHirings.contact_person);
				}
				
				if(Utils.validateString(mRejectedHirings.contact_no)){
					mobile_no = mRejectedHirings.hamdyman_mobile;
					mRootView.findViewById(R.id.hire_customer_contact_no_textview).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.hire_customer_contact_no).setVisibility(View.VISIBLE);
					((TextView) mRootView.findViewById(R.id.hire_customer_contact_no)).setText(mRejectedHirings.contact_no);
				}
				
				if(Utils.validateString(mRejectedHirings.getAddress())){
					mRootView.findViewById(R.id.hire_customer_address_textview).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.hire_customer_address).setVisibility(View.VISIBLE);
					String address = "", street = "", landmark = "", city = "", state = "", pincode = "";
					address =  mRejectedHirings.getAddress();
					if(Utils.validateString(mRejectedHirings.getStreet()))
						street = mRejectedHirings.getStreet();
					if(Utils.validateString(mRejectedHirings.getLandmark()))
						landmark = mRejectedHirings.getLandmark();
					if(Utils.validateString(mRejectedHirings.getCity_name()))
						city = mRejectedHirings.getCity_name();
					if(Utils.validateString(mRejectedHirings.getState_name()))
						state = mRejectedHirings.getState_name();
					if(Utils.validateString(mRejectedHirings.getPincode()))
						pincode = mRejectedHirings.getPincode();
					((TextView) mRootView.findViewById(R.id.hire_customer_address)).setText(address + "\n" + street + "\n" + landmark + "\n" + city + " - " + pincode);
				}
				
				if(Utils.validateString(mRejectedHirings.comment)){
					mRootView.findViewById(R.id.hire_customer_requirment_textview).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.hire_customer_requirment).setVisibility(View.VISIBLE);
					((TextView) mRootView.findViewById(R.id.hire_customer_requirment)).setText(mRejectedHirings.comment);
				}
				
				if(Utils.validateString(mRejectedHirings.hire_status)){
					status = mRejectedHirings.hire_status;
				}
				
				if(Utils.validateString(mRejectedHirings.id)){
					status_id = mRejectedHirings.id;
				}
				
				if(Utils.validateString(mRejectedHirings.handyman_id)){
					handyman_id = mRejectedHirings.handyman_id;
				}
				
				if(Utils.validateString(mRejectedHirings.handyman_name)){
					handyman_name = mRejectedHirings.handyman_name;
				}
				
				if(Utils.validateString(mRejectedHirings.email)){
					handyman_email = mRejectedHirings.email;
				}
				
				if(Utils.validateString(mRejectedHirings.rating)){
					handyman_rating = mRejectedHirings.rating;
				}
				
//				Utils.storeString(mSharedPreferences, Utils.CLIENT_ID, mRejectedHirings.client_id);
			}
			
			if(mAllHirings != null){
				((MainActivity) getActivity()).setTitleText("", mAllHirings.handyman_name, View.VISIBLE, View.GONE, View.GONE,View.GONE);
				((TextView) mRootView.findViewById(R.id.handyman_name_profile_txt)).setText(mAllHirings.handyman_name);
				
				if(Utils.validateString(mAllHirings.handyman_img)) {
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
					.load(Utils.IMAGE_URL + mAllHirings.handyman_img)
					.placeholder(R.drawable.avtar_images)
					.error(R.drawable.avtar_images)
					.transform(transformation)
					.centerCrop()
					.resize(mDeviceWidth, (int) (mDeviceWidth))
					.into(mHanfymanprofileImg);
				}
				
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
					((TextView) mRootView.findViewById(R.id.hire_customer_time)).setText(time.substring(0, 2)+ ":"+time.substring(3, 5)+"" + time.substring(8,11));
				}
				

				if(Utils.validateString(mAllHirings.contact_person)){
					mRootView.findViewById(R.id.hire_customer_contact_person_textview).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.hire_customer_contact_person).setVisibility(View.VISIBLE);
					((TextView) mRootView.findViewById(R.id.hire_customer_contact_person)).setText(mAllHirings.contact_person);
				}
				
				if(Utils.validateString(mAllHirings.contact_no)){
					mobile_no = mAllHirings.hamdyman_mobile;
					mRootView.findViewById(R.id.hire_customer_contact_no_textview).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.hire_customer_contact_no).setVisibility(View.VISIBLE);
					((TextView) mRootView.findViewById(R.id.hire_customer_contact_no)).setText(mAllHirings.contact_no);
				}
				
				if(Utils.validateString(mAllHirings.getAddress())){
					mRootView.findViewById(R.id.hire_customer_address_textview).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.hire_customer_address).setVisibility(View.VISIBLE);
					String address = "", street = "", landmark = "", city = "", state = "", pincode = "";
					address =  mAllHirings.getAddress();
					if(Utils.validateString(mAllHirings.getStreet()))
						street = mAllHirings.getStreet();
					if(Utils.validateString(mAllHirings.getLandmark()))
						landmark = mAllHirings.getLandmark();
					if(Utils.validateString(mAllHirings.getCity_name()))
						city = mAllHirings.getCity_name();
					if(Utils.validateString(mAllHirings.getState_name()))
						state = mAllHirings.getState_name();
					if(Utils.validateString(mAllHirings.getPincode()))
						pincode = mAllHirings.getPincode();
					((TextView) mRootView.findViewById(R.id.hire_customer_address)).setText(address + "\n" + street + "\n" + landmark + "\n" + city + " - " + pincode);
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
				
				if(Utils.validateString(mAllHirings.handyman_id)){
					handyman_id = mAllHirings.handyman_id;
				}
				
				if(Utils.validateString(mAllHirings.handyman_name)){
					handyman_name = mAllHirings.handyman_name;
				}
				
				if(Utils.validateString(mAllHirings.email)){
					handyman_email = mAllHirings.email;
				}
				
				if(Utils.validateString(mAllHirings.rating)){
					handyman_rating = mAllHirings.rating;
				}
				
//				if(Utils.validateString(mAllHirings.closing_status)){
//					closing_status = mAllHirings.closing_status;
//				}
				
//				Utils.storeString(mSharedPreferences, Utils.CLIENT_ID, mAllHirings.client_id);
			}
			
		}
		
		/*new CountDownTimer(7200000, 1000) {
			
			@Override
			public void onTick(long millisUntilFinished) {
				long seconds = millisUntilFinished / 1000;
				 mTimer.setText("Time: " + String.format("%02d:%02d:%02d", seconds / 3600,
			                (seconds % 3600) / 60, (seconds % 60)));
				
			}
			
			@Override
			public void onFinish() {
				mTimer.setText("Time's up!");
				
			}
		}.start();*/

		
		if (status.equalsIgnoreCase("pending")) {
			mRootView.findViewById(R.id.rating_img).setVisibility(View.GONE);
			
//			if(closing_status.equalsIgnoreCase("1")){
//				mCancel.setBackgroundResource(R.drawable.btn_xxxhdpi);
//				mCancel.setClickable(true);
			mCancel.setVisibility(View.VISIBLE);
//			} else {
//				mCancel.setBackgroundResource(R.drawable.hover_btn_xxxhdpi);
//				mCancel.setClickable(false);
//			}
			
		} else if (status.equalsIgnoreCase("active")) {
			mRootView.findViewById(R.id.rating_img).setVisibility(View.GONE);
			
//			if(closing_status.equalsIgnoreCase("1")){
//				mCancel.setBackgroundResource(R.drawable.btn_xxxhdpi);
//				mCancel.setClickable(true);
			mCancel.setVisibility(View.VISIBLE);
//			} else {
//				mCancel.setBackgroundResource(R.drawable.hover_btn_xxxhdpi);
//				mCancel.setClickable(false);
//			}
			
		} else if (status.equalsIgnoreCase("start")) { 
			mRootView.findViewById(R.id.rating_img).setVisibility(View.GONE);
			mCancel.setVisibility(View.VISIBLE);
	
		} else if (status.equalsIgnoreCase("cancelled")) {
			mRootView.findViewById(R.id.rating_img).setVisibility(View.GONE);
//			mCancel.setBackgroundResource(R.drawable.hover_btn_xxxhdpi);
//			mCancel.setClickable(false);
			mCancel.setVisibility(View.GONE);

		} else if (status.equalsIgnoreCase("completed")) {
			mRootView.findViewById(R.id.rating_img).setVisibility(View.VISIBLE);
//			mCancel.setBackgroundResource(R.drawable.hover_btn_xxxhdpi);
//			mCancel.setClickable(false);
			mCancel.setVisibility(View.GONE);

		} else if (status.equalsIgnoreCase("rejected")) {
			mRootView.findViewById(R.id.rating_img).setVisibility(View.GONE);
//			mCancel.setBackgroundResource(R.drawable.hover_btn_xxxhdpi);
//			mCancel.setClickable(false);
			mCancel.setVisibility(View.GONE);
		}
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
			
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
			
			/*Uri mUri = Uri.parse("smsto:"+ mSharedPreferences.getString(Utils.MOBILE_NO, "") + "@s.whatsapp.net");
			Intent mIntent = new Intent(Intent.ACTION_SENDTO, mUri);
			mIntent.setPackage("com.whatsapp");
			mIntent.putExtra("sms_body", "The text goes here");
			mIntent.putExtra("chat",true);
			startActivity(mIntent);*/
			break;
			
		case R.id.call_img:
			Uri number = Uri.parse("tel:"+ mobile_no);
			Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
			startActivity(callIntent);
			
			break;
			
		case R.id.rating_img:
			getRateCheck(status_id, mSharedPreferences.getString(Utils.USER_ID, ""));
			break;
			
		case R.id.report_img:
			RegisterComplainFragment registerComplainFragment = new RegisterComplainFragment();
			Bundle bundle = new Bundle();
			bundle.putString(Utils.HIRE_HANDYMAN_ID, handyman_id);
			bundle.putString(Utils.HIRE_HANDYMAN_NAME, handyman_name);
			bundle.putString(Utils.HIRE_HANDYMAN_EMAIL, handyman_email);
			bundle.putString(Utils.HIRE_HANDYMAN_RATING, handyman_rating);
			registerComplainFragment.setArguments(bundle);
			getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, registerComplainFragment).addToBackStack(TAG).commit();
			break;
			
		case R.id.cancel_Button:
			
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle("Alert");
			builder.setCancelable(false);
			builder.setMessage("Are you sure for cancel.");
			builder.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							
							if (status.equalsIgnoreCase("pending")) {
								Utils.storeString(mSharedPreferences, Utils.CANCEL, "CANCEL");
								onChangeStatus(status_id,"cancelled","2");
							} else if (status.equalsIgnoreCase("active")) {
								Utils.storeString(mSharedPreferences, Utils.CANCEL_A, "CANCEL_A");
								onChangeStatus(status_id,"cancelled","2");
							} else if (status.equalsIgnoreCase("start")) {
								Utils.storeString(mSharedPreferences, Utils.CANCEL_S, "CANCEL_S");
								onChangeStatus(status_id,"cancelled","2");
							}
						}
					});
			builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener(){

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
			AlertDialog alertDialog = builder.create();
			alertDialog.show();
			
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
                            Toast.makeText(getActivity(), myHiringsModel.message , Toast.LENGTH_SHORT).show();
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
        	getHandymanChangeStatusRequestTask.execute(id, hire_status, service_updated_by);
        } else {
            Toast.makeText(getActivity(), getString(R.string.connection), Toast.LENGTH_SHORT).show();
        }
    }
	
	private void getRateCheck(String hire_id, String client_id) {
	if(Utils.checkInternetConnection(getActivity())){
		CheckRateHandymanRequestTask checkRateHandymanRequestTask = new CheckRateHandymanRequestTask(getActivity());
		checkRateHandymanRequestTask.setAsyncCallListener(new AsyncCallListener() {

			@SuppressWarnings("unchecked")
			@Override
			public void onResponseReceived(Object response) {
				
				HireHandymanModel hireHandymanModel = (HireHandymanModel) response;
                if(hireHandymanModel.success.equalsIgnoreCase("1")) {
                	Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), 
            				hireHandymanModel.message);
                	
                } else if(hireHandymanModel.success.equalsIgnoreCase("0")) {
                	RatingFragment ratingFragment = new RatingFragment();
        			Bundle bundle1 = new Bundle();
        			bundle1.putString(Utils.HIRE_HANDYMAN_ID, handyman_id);
        			bundle1.putString(Utils.HIRE_HANDYMAN_STATUS_ID, status_id);
        			bundle1.putString(Utils.HIRE_HANDYMAN_NAME, handyman_name);
        			bundle1.putString(Utils.HIRE_HANDYMAN_EMAIL, handyman_email);
        			bundle1.putString(Utils.HIRE_HANDYMAN_RATING, handyman_rating);
        			ratingFragment.setArguments(bundle1);
        			getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, ratingFragment).addToBackStack(TAG).commit();
                }
                          
			}
			@Override
			public void onErrorReceived(String error) {
				Toast.makeText(getActivity(), getString(R.string.try_again), Toast.LENGTH_SHORT).show();
			}
		});
		checkRateHandymanRequestTask.execute(hire_id,client_id);
	}else{
		Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), 
				getResources().getString(R.string.connection));
	}
}
}
