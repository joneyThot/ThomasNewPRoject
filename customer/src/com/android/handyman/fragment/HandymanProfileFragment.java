package com.android.handyman.fragment;

import java.util.ArrayList;
import java.util.Calendar;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
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
import android.support.v4.app.FragmentManager;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.handyman.MainActivity;
import com.android.handyman.R;
import com.android.handyman.logger.Logger;
import com.android.handyman.model.DataModel;
import com.android.handyman.model.HandymanModel;
import com.android.handyman.model.HireHandymanModel;
import com.android.handyman.model.ProfileHandymanModel;
import com.android.handyman.service.AsyncCallListener;
import com.android.handyman.service.ComplainHandymanRequestTask;
import com.android.handyman.service.FavouriteHandymanRequestTask;
import com.android.handyman.service.GetProfileHandymanListRequestTask;
import com.android.handyman.service.Utils;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

public class HandymanProfileFragment extends BaseFragment implements OnClickListener{
	
	private static String TAG = "HandymanProfileFragment";
	private SharedPreferences mSharedPreferences;
//	private HandymanModel mHandymanModels = new HandymanModel();
//	private ArrayList<HandymanModel> mHandymanModelsList = new ArrayList<HandymanModel>(); 
	private int mDeviceWidth = 480;
	private ArrayList<ProfileHandymanModel> mProfileHandymanModelsList = new ArrayList<ProfileHandymanModel>(); 
	
	Fragment fr;
	View mRootView;
	TextView mAgetxt;
	String handyman_id,handyman_name,like_value, url = "";
	ImageView mHanfymanprofileImg, mFaveImg;
	String FavouriteHandymanFragment = "" , mobile_no = "";
	boolean showingFirst = false;
	

	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2) @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,	Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.fragment_handyman_profile, container, false);
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

	@SuppressWarnings("unchecked")
	private void initview() {
		mSharedPreferences = getActivity().getSharedPreferences(Utils.PREF_NAME, Context.MODE_PRIVATE); 
		
		mActivity.getSlidingMenu().setMode(SlidingMenu.LEFT);
		mActivity.getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		
		mHanfymanprofileImg = (ImageView) mRootView.findViewById(R.id.hanfyman_profile_img);
		mHanfymanprofileImg.setOnClickListener(this);
		mRootView.findViewById(R.id.hireme_Button).setOnClickListener(this);
//		rating = mSharedPreferences.getString(Utils.HANDYMAN_RATING, "");

		mRootView.findViewById(R.id.chat_img).setOnClickListener(this);
		mRootView.findViewById(R.id.call_img).setOnClickListener(this);
		mRootView.findViewById(R.id.rating_img).setOnClickListener(this);
		mRootView.findViewById(R.id.report_img).setOnClickListener(this);
		mRootView.findViewById(R.id.hire_website_txt).setOnClickListener(this);
		mFaveImg = (ImageView) mRootView.findViewById(R.id.fave_img);
		mFaveImg.setOnClickListener(this);
		
//		if(getArguments() != null) {
			handyman_id = mSharedPreferences.getString(Utils.HANDYMAN_ID, "");
//			Utils.storeString(mSharedPreferences, Utils.HANDYMAN_ID, handyman_id);
//		}
			if(getArguments() != null){
				FavouriteHandymanFragment = getArguments().getString("FavouriteHandymanFragment");
			}
		
		if(Utils.validateString(handyman_id))
		getProfileHandyman(handyman_id, mSharedPreferences.getString(Utils.USER_ID, ""));
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.hanfyman_profile_img:
			break;
		
		case R.id.hireme_Button:
			HireHandymanFragment hireHandymanFragment = new HireHandymanFragment();
			Bundle bundle = new Bundle();
			bundle.putString(Utils.HANDYMAN_NAME, mProfileHandymanModelsList.get(0).firstname + " " + mProfileHandymanModelsList.get(0).lastname);
			bundle.putString(Utils.HANDYMAN_EMAIL, mProfileHandymanModelsList.get(0).email);
//			bundle.putString(Utils.HANDYMAN_ID, handyman_id);
			bundle.putString("FavouriteHandymanFragment", FavouriteHandymanFragment);
			hireHandymanFragment.setArguments(bundle);
//			getActivity().getSupportFragmentManager().popBackStack("FindNearByHandymanFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
			getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, hireHandymanFragment).addToBackStack(TAG).commit();
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
			
		case R.id.rating_img:
//			getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new RatingFragment()).addToBackStack(TAG).commit();
			break;
			
		case R.id.report_img:
			getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new RegisterComplainFragment()).addToBackStack(TAG).commit();
			break;
			
		case R.id.fave_img:
			 if(like_value.equalsIgnoreCase("1")){
					mFaveImg.setImageResource(R.drawable.icon_fav);
					FavoriteHandyman(handyman_id,mSharedPreferences.getString(Utils.USER_ID, ""),like_value);
					like_value = "0";
			} else {
		        	mFaveImg.setImageResource(R.drawable.icon_nonfav);
		        	FavoriteHandyman(handyman_id,mSharedPreferences.getString(Utils.USER_ID, ""),like_value);
					like_value = "1";
			}
			
			break;
			
		case R.id.hire_website_txt:
			if(Utils.validateString(url)){
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(url));
				startActivity(i);
			}
			break;
		}
	}
	
	private void getProfileHandyman(String handyman_id, String client_id) {
		if(Utils.checkInternetConnection(getActivity())){
			GetProfileHandymanListRequestTask getProfileHandymanListRequestTask = new GetProfileHandymanListRequestTask(getActivity());
			getProfileHandymanListRequestTask.setAsyncCallListener(new AsyncCallListener() {

				@SuppressWarnings("unchecked")
				@Override
				public void onResponseReceived(Object response) {
					mProfileHandymanModelsList.clear();
					DataModel dataModel = (DataModel) response;
					if(dataModel.getSuccess().equalsIgnoreCase("1")){
						
//						Toast.makeText(getActivity(), dataModel.getMessage(), Toast.LENGTH_SHORT).show();
						
//						mProfileHandymanModelsList =  (ArrayList<ProfileHandymanModel>) response;
						mProfileHandymanModelsList.addAll(dataModel.getProfileHandymanModels());
						Logger.e(TAG, "mProfileHandymanModelsList SIZE -- "	+ mProfileHandymanModelsList.size());
						if (mProfileHandymanModelsList.size() > 0) {
//							for (int i = 0; i < mProfileHandymanModelsList.size(); i++) {
								
								String firstname = ""; 
								String lastname = "";
								if(Utils.validateString(mProfileHandymanModelsList.get(0).getFirstname())){
									firstname = mProfileHandymanModelsList.get(0).getFirstname(); 
								}
								if(Utils.validateString(mProfileHandymanModelsList.get(0).getFirstname())){
									lastname = mProfileHandymanModelsList.get(0).getLastname();
								}
								
								if(Utils.validateString(FavouriteHandymanFragment)){
									((MainActivity) getActivity()).setTitleText("", firstname + " " + lastname, View.VISIBLE, View.GONE, View.GONE, View.GONE);
									getActivity().findViewById(R.id.title).setVisibility(View.GONE);
								} else {
									((MainActivity) getActivity()).setTitleText("", firstname + " " + lastname, View.VISIBLE, View.GONE, View.GONE, View.VISIBLE);
									getActivity().findViewById(R.id.title).setVisibility(View.GONE);
								}
								
								((TextView) mRootView.findViewById(R.id.handyman_name_profile_txt)).setText(firstname + " " + lastname);
								
								if(Utils.validateString(mProfileHandymanModelsList.get(0).img_path)) {
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
									.load(Utils.IMAGE_URL + mProfileHandymanModelsList.get(0).img_path)
									.placeholder(R.drawable.avtar_images)
									.error(R.drawable.avtar_images)
									.transform(transformation)
									.centerCrop()
									.resize(mDeviceWidth, (int) (mDeviceWidth))
//									.resize(mDeviceWidth, (int) (mDeviceWidth * 0.50))
									.into(mHanfymanprofileImg);
								}
								
								if(Utils.validateString(mProfileHandymanModelsList.get(0).getLike())){
									like_value = mProfileHandymanModelsList.get(0).getLike().toString();
									if(like_value.equalsIgnoreCase("1")){
										mFaveImg.setImageResource(R.drawable.icon_fav);
										showingFirst = true;
										like_value = "0";
									} else {
										mFaveImg.setImageResource(R.drawable.icon_nonfav);
										showingFirst = false;
										like_value = "1";
									}
								}
								
								if(Utils.validateString(mProfileHandymanModelsList.get(0).getDob())){
									mRootView.findViewById(R.id.age_textview).setVisibility(View.VISIBLE);
									mRootView.findViewById(R.id.hire_age_txt).setVisibility(View.VISIBLE);
									
									String birth_date = mProfileHandymanModelsList.get(0).getDob().toString();
									int day = Integer.parseInt(birth_date.substring(0, 2));
									int month = Integer.parseInt(birth_date.substring(3, 5));
									int year = Integer.parseInt(birth_date.substring(6, 10));
									String age = getAge(year, month, day);
									int int_age = Integer.parseInt(age);
									if(int_age > 0){
										((TextView) mRootView.findViewById(R.id.hire_age_txt)).setText(age + " " + "yrs");
									} else {
										((TextView) mRootView.findViewById(R.id.hire_age_txt)).setText("0" + " " + "yrs");
									}
								}
								
								if(Utils.validateString(mProfileHandymanModelsList.get(0).getQualification())){
									mRootView.findViewById(R.id.qualification_textview).setVisibility(View.VISIBLE);
									mRootView.findViewById(R.id.hire_qualification_txt).setVisibility(View.VISIBLE);
									((TextView) mRootView.findViewById(R.id.hire_qualification_txt)).setText(mProfileHandymanModelsList.get(0).getQualification());
								}
								
								if(Utils.validateString(mProfileHandymanModelsList.get(0).getExperience())){
									mRootView.findViewById(R.id.expertise_textview).setVisibility(View.VISIBLE);
									mRootView.findViewById(R.id.hire_expertise_txt).setVisibility(View.VISIBLE);
									((TextView) mRootView.findViewById(R.id.hire_expertise_txt)).setText(mProfileHandymanModelsList.get(0).getExperience());
								}
								
//								if(Utils.validateString(mProfileHandymanModelsList.get(0).getExperience())){
//									mRootView.findViewById(R.id.experience_textview).setVisibility(View.VISIBLE);
//									mRootView.findViewById(R.id.hire_experience_txt).setVisibility(View.VISIBLE);
//									((TextView) mRootView.findViewById(R.id.hire_experience_txt)).setText(mProfileHandymanModelsList.get(0).getExperience());
//								}
								
								if(Utils.validateString(mProfileHandymanModelsList.get(0).getMobile())){
									mobile_no = mProfileHandymanModelsList.get(0).getMobile();
									mRootView.findViewById(R.id.mobile_number_textview).setVisibility(View.VISIBLE);
									mRootView.findViewById(R.id.hire_mobile_number_txt).setVisibility(View.VISIBLE);
									((TextView) mRootView.findViewById(R.id.hire_mobile_number_txt)).setText(mProfileHandymanModelsList.get(0).getMobile());
								}
								
								if(Utils.validateString(mProfileHandymanModelsList.get(0).getWhatsapp_id())){
									mRootView.findViewById(R.id.watsapp_id_textview).setVisibility(View.VISIBLE);
									mRootView.findViewById(R.id.hire_watsapp_id_txt).setVisibility(View.VISIBLE);
									((TextView) mRootView.findViewById(R.id.hire_watsapp_id_txt)).setText(mProfileHandymanModelsList.get(0).getWhatsapp_id());
								}
								
								if(Utils.validateString(mProfileHandymanModelsList.get(0).getEmail())){
									mRootView.findViewById(R.id.email_id_textview).setVisibility(View.VISIBLE);
									mRootView.findViewById(R.id.hire_email_id_txt).setVisibility(View.VISIBLE);
									((TextView) mRootView.findViewById(R.id.hire_email_id_txt)).setText(mProfileHandymanModelsList.get(0).getEmail());
								}
								
								if(Utils.validateString(mProfileHandymanModelsList.get(0).getProvider())){
									mRootView.findViewById(R.id.service_provider_textview).setVisibility(View.VISIBLE);
									mRootView.findViewById(R.id.hire_service_provider_txt).setVisibility(View.VISIBLE);
									((TextView) mRootView.findViewById(R.id.hire_service_provider_txt)).setText(mProfileHandymanModelsList.get(0).getProvider());
								}
								
								if(Utils.validateString(mProfileHandymanModelsList.get(0).getAddress())){
									mRootView.findViewById(R.id.address_textview).setVisibility(View.VISIBLE);
									mRootView.findViewById(R.id.hire_address_txt).setVisibility(View.VISIBLE);
									String address = "", street = "", landmark = "", city = "", state = "", pincode = "";
									address =  mProfileHandymanModelsList.get(0).getAddress().toString();
									if(Utils.validateString(mProfileHandymanModelsList.get(0).getStreet().toString()))
										street = mProfileHandymanModelsList.get(0).getStreet().toString();
									if(Utils.validateString(mProfileHandymanModelsList.get(0).getLandmark().toString()))
										landmark = mProfileHandymanModelsList.get(0).getLandmark().toString();
									if(Utils.validateString(mProfileHandymanModelsList.get(0).getCity_name().toString()))
										city = mProfileHandymanModelsList.get(0).getCity_name().toString();
									if(Utils.validateString(mProfileHandymanModelsList.get(0).getState_name().toString()))
										state = mProfileHandymanModelsList.get(0).getState_name().toString();
									if(Utils.validateString(mProfileHandymanModelsList.get(0).getPincode().toString()))
										pincode = mProfileHandymanModelsList.get(0).getPincode().toString();
									((TextView) mRootView.findViewById(R.id.hire_address_txt)).setText(address + "\n" + street + "\n" + landmark + "\n" + city + " - " + pincode);
								}
								
								if(Utils.validateString(mProfileHandymanModelsList.get(0).getWebsite())){
									mRootView.findViewById(R.id.website_textview).setVisibility(View.VISIBLE);
									mRootView.findViewById(R.id.hire_website_txt).setVisibility(View.VISIBLE);
									url = mProfileHandymanModelsList.get(0).getWebsite();
									((TextView) mRootView.findViewById(R.id.hire_website_txt)).setText(url);
								}
								String rating;
								if(Utils.validateString(mProfileHandymanModelsList.get(0).rating)){
									rating = mProfileHandymanModelsList.get(0).rating.trim();
								} else {
									rating = "0";
								}
								((RatingBar)mRootView.findViewById(R.id.handyman_rating_star)).setRating(Float.parseFloat(rating));
								
								Utils.storeString(mSharedPreferences, Utils.HANDYMAN_NAME, mProfileHandymanModelsList.get(0).firstname + " " + mProfileHandymanModelsList.get(0).lastname);
								Utils.storeString(mSharedPreferences, Utils.HANDYMAN_EMAIL, mProfileHandymanModelsList.get(0).email);
								Utils.storeString(mSharedPreferences, Utils.HANDYMAN_RATING,rating );
//							}
							
						} 
						
					} else if(dataModel.getSuccess().equalsIgnoreCase("0")){
							Toast.makeText(getActivity(), dataModel.getMessage(), Toast.LENGTH_SHORT).show();
//							Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), "User profile not found.");
					}
					
					
				}
				@Override
				public void onErrorReceived(String error) {
					Toast.makeText(getActivity(), getString(R.string.try_again), Toast.LENGTH_SHORT).show();
				}
			});
			getProfileHandymanListRequestTask.execute(handyman_id,client_id);
		}else{
			Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), 
					getResources().getString(R.string.connection));
		}
	}
	
	private void FavoriteHandyman(String handyman_id, String client_id, final String user_like) {
		if(Utils.checkInternetConnection(getActivity())){
			FavouriteHandymanRequestTask favouriteHandymanRequestTask = new FavouriteHandymanRequestTask(getActivity());
			favouriteHandymanRequestTask.setAsyncCallListener(new AsyncCallListener() {

				@SuppressWarnings("unchecked")
				@Override
				public void onResponseReceived(Object response) {
					 HireHandymanModel hireHandymanModel = (HireHandymanModel) response;
	                    if(hireHandymanModel.success.equalsIgnoreCase("1")) {
//	                            Toast.makeText(getActivity(), hireHandymanModel.message , Toast.LENGTH_SHORT).show();
	                           
	                        } else if (hireHandymanModel.success.equalsIgnoreCase("0")) {
	                            Toast.makeText(getActivity(), hireHandymanModel.message , Toast.LENGTH_SHORT).show();
	                        }
					
				}
				@Override
				public void onErrorReceived(String error) {
					Toast.makeText(getActivity(), getString(R.string.try_again), Toast.LENGTH_SHORT).show();
				}
			});
			favouriteHandymanRequestTask.execute(handyman_id,client_id, user_like);
		}else{
			Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), getResources().getString(R.string.connection));
		}
	}
	
	
	@SuppressLint("UseValueOf") private String getAge(int year, int month, int day){
	    Calendar dob = Calendar.getInstance();
	    Calendar today = Calendar.getInstance();

	    dob.set(year, month, day); 

	    int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

	    if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)){
	        age--; 
	    }

	    Integer ageInt = new Integer(age);
	    String ageS = ageInt.toString();

	    return ageS;  
	}
	
	
}
