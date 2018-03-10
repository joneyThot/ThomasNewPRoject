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

public class PushnotificationCustomerCompleteFragment extends BaseFragment implements OnClickListener{
	
	private static String TAG = "PushnotificationCustomerCompleteFragment";
	
	private SharedPreferences mSharedPreferences;
	
	String status_id = "", img = "", sub_category = "", service_updated_by = "", city = "", hire_status = "", appointment_date = "", discount = "", client_id = "", completed_date = "", total = "", is_deleted = "", contact_no = "", street = "", receiver_name = "", state = "", landmark = "", credit = "", lat = "", pincode = "", appointment_time = "", amount = "", address = "", lng = "", contact_person = "", is_outdated = "", job_description = "", img_path = "", comment = "", created_date = "", handyman_id = "", category = "", order_id = "", status = "", client_name = "", client_image = "",client_image_path = "", 
			handyman_name = "", handyman_image = "", handyman_image_path = "", handyman_email = "", handyman_rating = "", handyman_mobile = "";
	
	Fragment fr;
	View mRootView;
	ImageView mHanfymanprofileImg;
	private int mDeviceWidth = 480;
	TextView mCancel;

	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2) @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,	Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.fragment_push_notification_complete, container, false);
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
		((MainActivity) getActivity()).hideTitleRelativeLayout();
		
		mActivity.getSlidingMenu().setMode(SlidingMenu.LEFT);
		mActivity.getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		
		mHanfymanprofileImg = (ImageView) mRootView.findViewById(R.id.hanfyman_profile_img);
		
		mRootView.findViewById(R.id.push_backBtn).setOnClickListener(this);
		mRootView.findViewById(R.id.chat_img).setOnClickListener(this);
		mRootView.findViewById(R.id.call_img).setOnClickListener(this);
		mRootView.findViewById(R.id.rating_img).setOnClickListener(this);
		mRootView.findViewById(R.id.report_img).setOnClickListener(this);
		mCancel = (TextView) mRootView.findViewById(R.id.cancel_Button);
		mCancel.setOnClickListener(this);
		
		status_id = mSharedPreferences.getString(Utils.NOTI_ID, "");
        handyman_id = mSharedPreferences.getString(Utils.NOTI_HANDYMAN_ID, "");
        client_id = mSharedPreferences.getString(Utils.NOTI_CLIENT_ID, "");
        order_id = mSharedPreferences.getString(Utils.NOTI_ORDER_ID, "");
        job_description = mSharedPreferences.getString(Utils.NOTI_JOB_DESCRIPTION, "");
        category = mSharedPreferences.getString(Utils.NOTI_CATEGORY, "");
        sub_category = mSharedPreferences.getString(Utils.NOTI_SUB_CATEGORY, "");
        appointment_date = mSharedPreferences.getString(Utils.NOTI_APPOINTMENT_DATE, "");
        appointment_time = mSharedPreferences.getString(Utils.NOTI_APPOINTMENT_TIME, "");
        contact_person = mSharedPreferences.getString(Utils.NOTI_CONTACT_PERSON, "");
        contact_no = mSharedPreferences.getString(Utils.NOTI_CONTACT_NO, "");
        comment = mSharedPreferences.getString(Utils.NOTI_COMMENT, "");
        hire_status = mSharedPreferences.getString(Utils.NOTI_HIRE_STATUS, "");
        service_updated_by = mSharedPreferences.getString(Utils.NOTI_SERVICE_UPDATED_BY, "");
        is_outdated = mSharedPreferences.getString(Utils.NOTI_IS_OUTDATED, "");
        address = mSharedPreferences.getString(Utils.NOTI_ADDRESS, "");
        street = mSharedPreferences.getString(Utils.NOTI_STREET, "");
        landmark = mSharedPreferences.getString(Utils.NOTI_LANDMARK, "");
        city = mSharedPreferences.getString(Utils.NOTI_CITY, "");
        pincode = mSharedPreferences.getString(Utils.NOTI_PINCODE, "");
        state = mSharedPreferences.getString(Utils.NOTI_STATE, "");
        lat = mSharedPreferences.getString(Utils.NOTI_LAT, "");
        lng = mSharedPreferences.getString(Utils.NOTI_LNG, "");
        receiver_name = mSharedPreferences.getString(Utils.NOTI_RECEIVER_NAME, "");
        amount = mSharedPreferences.getString(Utils.NOTI_AMOUNT, "");
        completed_date = mSharedPreferences.getString(Utils.NOTI_COMPLETED_DATE, "");
        img = mSharedPreferences.getString(Utils.NOTI_IMG, "");
        img_path = mSharedPreferences.getString(Utils.NOTI_IMG_PATH, "");
        discount = mSharedPreferences.getString(Utils.NOTI_DISCOUNT, "");
        credit = mSharedPreferences.getString(Utils.NOTI_CREDIT, "");
        total = mSharedPreferences.getString(Utils.NOTI_TOTAL, "");
        is_deleted = mSharedPreferences.getString(Utils.NOTI_IS_DELETED, "");
        status = mSharedPreferences.getString(Utils.NOTI_STATUS, "");
        created_date = mSharedPreferences.getString(Utils.NOTI_CREATED_DATE, "");
        client_name = mSharedPreferences.getString(Utils.NOTI_CLIENT_NAME, "");
        client_image = mSharedPreferences.getString(Utils.NOTI_CLIENT_IMAGE, "");
        client_image_path = mSharedPreferences.getString(Utils.NOTI_CLIENT_IMAGE_PATH, "");
        handyman_name = mSharedPreferences.getString(Utils.NOTI_HANDYMAN_NAME, "");
        handyman_image = mSharedPreferences.getString(Utils.NOTI_HANDYMAN_IMAGE, "");
        handyman_image_path = mSharedPreferences.getString(Utils.NOTI_HANDYMAN_IMAGE_PATH, "");
        handyman_email = mSharedPreferences.getString(Utils.NOTI_HANDYMAN_EMAIL, "");
        handyman_rating = mSharedPreferences.getString(Utils.NOTI_HANDYMAN_RATING, "");
        handyman_mobile = mSharedPreferences.getString(Utils.NOTI_HANDYMAN_MOBILE, "");
					
//				((MainActivity) getActivity()).setTitleText("", handyman_name, View.VISIBLE, View.GONE, View.GONE, View.GONE);
        
        if(Utils.validateString(handyman_name)){
        	 mRootView.findViewById(R.id.push_title_back).setVisibility(View.VISIBLE);
        	((TextView) mRootView.findViewById(R.id.push_title_back)).setText(handyman_name);
        	mRootView.findViewById(R.id.handyman_name_profile_txt).setVisibility(View.VISIBLE);
			((TextView) mRootView.findViewById(R.id.handyman_name_profile_txt)).setText(handyman_name);
        }
        	
				
				if(Utils.validateString(handyman_image_path)) {
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
					.load(Utils.IMAGE_URL + handyman_image_path)
					.placeholder(R.drawable.avtar_images)
					.error(R.drawable.avtar_images)
					.transform(transformation)
					.centerCrop()
					.resize(mDeviceWidth, (int) (mDeviceWidth))
					.into(mHanfymanprofileImg);
				}
				
				if(Utils.validateString(order_id)){
					mRootView.findViewById(R.id.hire_customer_order_id_textview).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.hire_customer_order_id).setVisibility(View.VISIBLE);
					((TextView) mRootView.findViewById(R.id.hire_customer_order_id)).setText(order_id);
				}
				
				if(Utils.validateString(created_date)){
					mRootView.findViewById(R.id.hire_customer_order_date_textview).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.hire_customer_order_date).setVisibility(View.VISIBLE);
					((TextView) mRootView.findViewById(R.id.hire_customer_order_date)).setText(created_date);
				}

				if(Utils.validateString(job_description)){
					mRootView.findViewById(R.id.hire_customer_job_description_textview).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.hire_customer_job_description).setVisibility(View.VISIBLE);
					((TextView) mRootView.findViewById(R.id.hire_customer_job_description)).setText(job_description);
				}
				

				if(Utils.validateString(appointment_date)){
					mRootView.findViewById(R.id.hire_customer_date_textview).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.hire_customer_date).setVisibility(View.VISIBLE);
//					String birth_date = mPendingHirings.appointment_date;
					((TextView) mRootView.findViewById(R.id.hire_customer_date)).setText(appointment_date);
				}
				

				if(Utils.validateString(appointment_time)){
					mRootView.findViewById(R.id.hire_customer_time_textview).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.hire_customer_time).setVisibility(View.VISIBLE);
					String time = appointment_time;
              	    ((TextView) mRootView.findViewById(R.id.hire_customer_time)).setText(time.substring(0, 2)+ ":"+time.substring(3, 5)+"" + time.substring(8,11));
				}
				

				if(Utils.validateString(contact_person)){
					mRootView.findViewById(R.id.hire_customer_contact_person_textview).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.hire_customer_contact_person).setVisibility(View.VISIBLE);
					((TextView) mRootView.findViewById(R.id.hire_customer_contact_person)).setText(contact_person);
				}
				
				if(Utils.validateString(contact_no)){
//					mobile_no = mPendingHirings.hamdyman_mobile;
					mRootView.findViewById(R.id.hire_customer_contact_no_textview).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.hire_customer_contact_no).setVisibility(View.VISIBLE);
					((TextView) mRootView.findViewById(R.id.hire_customer_contact_no)).setText(contact_no);
				}
				
				if(Utils.validateString(address)){
					mRootView.findViewById(R.id.hire_customer_address_textview).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.hire_customer_address).setVisibility(View.VISIBLE);
					((TextView) mRootView.findViewById(R.id.hire_customer_address)).setText(address + "\n" + street + "\n" + landmark + "\n" + city + " - " + pincode);
				}
				
				if(Utils.validateString(comment)){
					mRootView.findViewById(R.id.hire_customer_requirment_textview).setVisibility(View.VISIBLE);
					mRootView.findViewById(R.id.hire_customer_requirment).setVisibility(View.VISIBLE);
					((TextView) mRootView.findViewById(R.id.hire_customer_requirment)).setText(comment);
				}
				
				
			if(Utils.validateString(hire_status)){
				if (hire_status.equalsIgnoreCase("pending")) {
					mRootView.findViewById(R.id.rating_img).setVisibility(View.GONE);
					mCancel.setVisibility(View.VISIBLE);
					
				} else if (hire_status.equalsIgnoreCase("active")) {
					mRootView.findViewById(R.id.rating_img).setVisibility(View.GONE);
					mCancel.setVisibility(View.VISIBLE);
					
				} else if (hire_status.equalsIgnoreCase("start")) { 
					mRootView.findViewById(R.id.rating_img).setVisibility(View.GONE);
					mCancel.setVisibility(View.VISIBLE);
			
				} else if (hire_status.equalsIgnoreCase("cancelled")) {
					mRootView.findViewById(R.id.rating_img).setVisibility(View.GONE);
					mCancel.setVisibility(View.GONE);

				} else if (hire_status.equalsIgnoreCase("completed")) {
					mRootView.findViewById(R.id.rating_img).setVisibility(View.VISIBLE);
					mCancel.setVisibility(View.GONE);

				} else if (hire_status.equalsIgnoreCase("rejected")) {
					mRootView.findViewById(R.id.rating_img).setVisibility(View.GONE);
					mCancel.setVisibility(View.GONE);
				}
			}
		
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		
		case R.id.push_backBtn:
			
			if(Utils.validateString(hire_status)){
				if (hire_status.equalsIgnoreCase("pending")) {
					MyHiringsFragment_new myHiringsFragment_new = new MyHiringsFragment_new();
					Bundle bundle1 = new Bundle();
					bundle1.putString("Pending", "Pending");
					myHiringsFragment_new.setArguments(bundle1);
					getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, myHiringsFragment_new).commit();
					
				} else if (hire_status.equalsIgnoreCase("active")) {
					MyHiringsFragment_new myHiringsFragment_new = new MyHiringsFragment_new();
					Bundle bundle1 = new Bundle();
					bundle1.putString("Active", "Active");
					myHiringsFragment_new.setArguments(bundle1);
					getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, myHiringsFragment_new).commit();
					
				} else if (hire_status.equalsIgnoreCase("start")) { 
					MyHiringsFragment_new myHiringsFragment_new = new MyHiringsFragment_new();
					Bundle bundle1 = new Bundle();
					bundle1.putString("Start", "Start");
					myHiringsFragment_new.setArguments(bundle1);
					getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, myHiringsFragment_new).commit();
			
				} else if (hire_status.equalsIgnoreCase("cancelled")) {
					MyHiringsFragment_new myHiringsFragment_new = new MyHiringsFragment_new();
					Bundle bundle1 = new Bundle();
					bundle1.putString("Cancel", "Cancel");
					myHiringsFragment_new.setArguments(bundle1);
					getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, myHiringsFragment_new).commit();

				} else if (hire_status.equalsIgnoreCase("completed")) {
					MyHiringsFragment_new myHiringsFragment_new = new MyHiringsFragment_new();
					Bundle bundle1 = new Bundle();
					bundle1.putString("Complete", "Complete");
					myHiringsFragment_new.setArguments(bundle1);
					getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, myHiringsFragment_new).commit();

				} 
			}
			
			break;
		
		case R.id.call_img:
			if(Utils.validateString(handyman_mobile)){
				Uri number = Uri.parse("tel:"+ handyman_mobile);
				Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
				startActivity(callIntent);
			}
			
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
			if(Utils.validateString(handyman_rating))
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
								onChangeStatus(status_id,"cancelled",mSharedPreferences.getString(Utils.USER_ID, ""));
							} else if (status.equalsIgnoreCase("active")) {
								Utils.storeString(mSharedPreferences, Utils.CANCEL_A, "CANCEL_A");
								onChangeStatus(status_id,"cancelled",mSharedPreferences.getString(Utils.USER_ID, ""));
							} else if (status.equalsIgnoreCase("start")) {
								Utils.storeString(mSharedPreferences, Utils.CANCEL_S, "CANCEL_S");
								onChangeStatus(status_id,"cancelled",mSharedPreferences.getString(Utils.USER_ID, ""));
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
                            //Toast.makeText(getActivity(), myHiringsModel.message , Toast.LENGTH_SHORT).show();
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
        			if(Utils.validateString(handyman_rating))
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
