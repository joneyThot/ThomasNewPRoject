package com.android.handyman.fragment;

import java.util.ArrayList;

import android.annotation.SuppressLint;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.handyman.MainActivity;
import com.android.handyman.R;
import com.android.handyman.logger.Logger;
import com.android.handyman.model.DataModel;
import com.android.handyman.model.MyProfileModel;
import com.android.handyman.service.AsyncCallListener;
import com.android.handyman.service.GetMyProfileRequestListTask;
import com.android.handyman.service.Utils;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

public class MyProfileFragment extends BaseFragment implements OnClickListener{
	
	private static String TAG = "MyProfileFragment";
	
	private SharedPreferences mSharedPreferences;
	private ArrayList<MyProfileModel> mMyProfileArrayList = new ArrayList<MyProfileModel>();
	
	private int mDeviceWidth = 480;
	
	Fragment fr;
	View mRootView,footerView = null;
	ImageView mMyProfileImg;
	String url = "";
	Context context;
	ProgressBar progressBar = null;
	int currentPage = 1;
    boolean isLoading = false,isDataFinished = false;
	
	

	@SuppressLint("NewApi") @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,	Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.fragment_my_profile, container, false);
		
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
		((MainActivity) getActivity()).setTitleText(getString(R.string.menu_my_profile), "", View.GONE, View.VISIBLE, View.VISIBLE,View.GONE);
		getActivity().findViewById(R.id.title_back).setVisibility(View.GONE);
		getActivity().findViewById(R.id.menuBtn).setOnClickListener(this);	
		
		mActivity.getSlidingMenu().setMode(SlidingMenu.LEFT);
		mActivity.getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		
		mMyProfileImg = (ImageView) mRootView.findViewById(R.id.my_profile_img);
		mRootView.findViewById(R.id.my_profile_website_txt).setOnClickListener(this);
		
        
        getMyProfile(mSharedPreferences.getString(Utils.USER_ID, ""));
        
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.menuBtn:
			EditProfileFragment editProfileFragment = new EditProfileFragment();
			Bundle bundle = new Bundle();
			bundle.putSerializable(Utils.MY_PROFILE_LIST, mMyProfileArrayList);
			editProfileFragment.setArguments(bundle);
			getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, editProfileFragment).addToBackStack(TAG).commit();
			break;
			
		case R.id.my_profile_website_txt:
			if(Utils.validateString(url)){
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(url));
				startActivity(i);
			}
			break;
		}
		
	}
	
	private void getMyProfile(String id) {
		if(Utils.checkInternetConnection(getActivity())){
			GetMyProfileRequestListTask getMyProfileRequestListTask = new GetMyProfileRequestListTask(getActivity());
			getMyProfileRequestListTask.setAsyncCallListener(new AsyncCallListener() {

				@SuppressWarnings("unchecked")
				@Override
				public void onResponseReceived(Object response) {
						mMyProfileArrayList.clear();
						DataModel dataModel = (DataModel) response;
						
						if(dataModel.getSuccess().equalsIgnoreCase("1")) {

//							mMyProfileArrayList = (ArrayList<MyProfileModel>) response;
							mMyProfileArrayList.addAll(dataModel.getMyProfileModels());
							
							Logger.e(TAG, "mMyProfileArrayList SIZE -- " + mMyProfileArrayList.size());
							if (mMyProfileArrayList.size() > 0) {
								
								String firstname = mMyProfileArrayList.get(0).getFirstname(); 
								String lastname = mMyProfileArrayList.get(0).getLastname();
								
								((TextView) mRootView.findViewById(R.id.my_name_profile_txt)).setText(firstname + " " + lastname);
								mRootView.findViewById(R.id.v1).setVisibility(View.VISIBLE);
								if(Utils.validateString(mMyProfileArrayList.get(0).img_path)) {
									mMyProfileImg.setVisibility(View.VISIBLE);
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
									.load(Utils.IMAGE_URL + mMyProfileArrayList.get(0).img_path)
									.error(R.drawable.avtar_images)
									.transform(transformation)
									.centerCrop()
									.resize(mDeviceWidth, (int) (mDeviceWidth))
									.into(mMyProfileImg);
								}
								
								if(Utils.validateString(mMyProfileArrayList.get(0).getMobile())){
									mRootView.findViewById(R.id.mobile_number_textview).setVisibility(View.VISIBLE);
									mRootView.findViewById(R.id.my_profile_mobile_number_txt).setVisibility(View.VISIBLE);
									((TextView) mRootView.findViewById(R.id.my_profile_mobile_number_txt)).setText(mMyProfileArrayList.get(0).getMobile());
								}
								
								if(Utils.validateString(mMyProfileArrayList.get(0).getGender())){
									mRootView.findViewById(R.id.gender_textview).setVisibility(View.VISIBLE);
									mRootView.findViewById(R.id.my_profile_gender_txt).setVisibility(View.VISIBLE);
									((TextView) mRootView.findViewById(R.id.my_profile_gender_txt)).setText(mMyProfileArrayList.get(0).getGender());
								}
								
								if(Utils.validateString(mMyProfileArrayList.get(0).getDob())){
									mRootView.findViewById(R.id.birthday_textview).setVisibility(View.VISIBLE);
									mRootView.findViewById(R.id.my_profile_birthdate_txt).setVisibility(View.VISIBLE);
									
									String birth_date = mMyProfileArrayList.get(0).getDob().toString();
									((TextView) mRootView.findViewById(R.id.my_profile_birthdate_txt)).setText(birth_date);
								}
								
								if(Utils.validateString(mMyProfileArrayList.get(0).getEmail())){
									mRootView.findViewById(R.id.email_id_textview).setVisibility(View.VISIBLE);
									mRootView.findViewById(R.id.my_profile_email_id_txt).setVisibility(View.VISIBLE);
									((TextView) mRootView.findViewById(R.id.my_profile_email_id_txt)).setText(mMyProfileArrayList.get(0).getEmail());
								}
															
								if(Utils.validateString(mMyProfileArrayList.get(0).getWhatsapp_id())){
									mRootView.findViewById(R.id.watsapp_id_textview).setVisibility(View.VISIBLE);
									mRootView.findViewById(R.id.my_profile_watsapp_id_txt).setVisibility(View.VISIBLE);
									((TextView) mRootView.findViewById(R.id.my_profile_watsapp_id_txt)).setText(mMyProfileArrayList.get(0).getWhatsapp_id());
								}
								
								if(Utils.validateString(mMyProfileArrayList.get(0).getAddress())){
									mRootView.findViewById(R.id.address_textview).setVisibility(View.VISIBLE);
									mRootView.findViewById(R.id.my_profile_address_txt).setVisibility(View.VISIBLE);
									String address = "", street = "", landmark = "", city = "", state = "", pincode = "";
									address =  mMyProfileArrayList.get(0).getAddress().toString();
									
									if(Utils.validateString(mMyProfileArrayList.get(0).getStreet().toString()))
										street = mMyProfileArrayList.get(0).getStreet().toString();
									
									if(Utils.validateString(mMyProfileArrayList.get(0).getLandmark().toString()))
										landmark = mMyProfileArrayList.get(0).getLandmark().toString();
									
									if(Utils.validateString(mMyProfileArrayList.get(0).getCity_name().toString())){
										mRootView.findViewById(R.id.city_textview).setVisibility(View.VISIBLE);
										mRootView.findViewById(R.id.my_profile_city_txt).setVisibility(View.VISIBLE);
										city = mMyProfileArrayList.get(0).getCity_name().toString();
										((TextView) mRootView.findViewById(R.id.my_profile_city_txt)).setText(city);
									}
									if(Utils.validateString(mMyProfileArrayList.get(0).getState_name().toString())){
										mRootView.findViewById(R.id.state_textview).setVisibility(View.VISIBLE);
										mRootView.findViewById(R.id.my_profile_state_txt).setVisibility(View.VISIBLE);
										state = mMyProfileArrayList.get(0).getState_name().toString();
										((TextView) mRootView.findViewById(R.id.my_profile_state_txt)).setText(state);
										
									}
									if(Utils.validateString(mMyProfileArrayList.get(0).getPincode().toString())){
										mRootView.findViewById(R.id.pincode_textview).setVisibility(View.VISIBLE);
										mRootView.findViewById(R.id.my_profile_pincode_txt).setVisibility(View.VISIBLE);
										pincode = mMyProfileArrayList.get(0).getPincode().toString();
										((TextView) mRootView.findViewById(R.id.my_profile_pincode_txt)).setText(pincode);
									}
									((TextView) mRootView.findViewById(R.id.my_profile_address_txt)).setText(address + "\n" + street + "\n" + landmark + "\n" + city + " - " + pincode);
								}
								
							} 
						} else if(dataModel.getSuccess().equalsIgnoreCase("0")) {
								Toast.makeText(getActivity(), dataModel.getMessage(), Toast.LENGTH_SHORT).show();
						}
						
				}
				@Override
				public void onErrorReceived(String error) {
					Toast.makeText(getActivity(), getString(R.string.try_again), Toast.LENGTH_SHORT).show();
				}
			});
			getMyProfileRequestListTask.execute(id);
		}else{
			Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), 
					getResources().getString(R.string.connection));
		}
	}
	
	
}
