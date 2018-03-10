package com.android.handyman.fragment;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Point;
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
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.android.handyman.LoginActivity;
import com.android.handyman.MainActivity;
import com.android.handyman.R;
import com.android.handyman.logger.Logger;
import com.android.handyman.model.RegisterModel;
import com.android.handyman.service.AsyncCallListener;
import com.android.handyman.service.LogoutRequestTask;
import com.android.handyman.service.Utils;
import com.android.handyman.view.TextViewPlus;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

public class SlidingMenuFragment extends BaseFragment implements
		OnClickListener {

	private static String TAG = "SlidingMenuFragment";
	public static View mRoot;
	private static LinearLayout mMenuLayout;
	public static ScrollView sc;
	public static int pos_x = 0, pos_y = 0;
	private int mDeviceWidth = 480;
	
	Fragment fr = null;
	TextViewPlus titleText;
	ImageView mUserImg;
	boolean locpressed = false;
	SharedPreferences sharedPreferences;

	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2) @SuppressLint("ResourceAsColor")
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mRoot = inflater.inflate(R.layout.list, null);
		sharedPreferences = getActivity().getSharedPreferences(Utils.PREF_NAME,	Context.MODE_PRIVATE);
		
		WindowManager w = ((Activity) getActivity()).getWindowManager();

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			Point size = new Point();
			w.getDefaultDisplay().getSize(size);
			mDeviceWidth = size.x;
		} else {
			Display d = w.getDefaultDisplay();
			mDeviceWidth = d.getWidth();
		}
		
		mMenuLayout = (LinearLayout) mRoot.findViewById(R.id.menuLayout);
		getActivity().findViewById(R.id.title).setVisibility(View.VISIBLE);
		titleText = (TextViewPlus) getActivity().findViewById(R.id.title);
		mUserImg = (ImageView) mRoot.findViewById(R.id.user_img);
		mUserImg.setOnClickListener(this);
		
		sc = (ScrollView) mRoot.findViewById(R.id.scrollLayout);

		sc.post(new Runnable() {
			@Override
			public void run() {
				pos_x = sc.getScrollX();
				pos_y = sc.getScrollY();
			}
		});

		for (int i = 1; i < mMenuLayout.getChildCount(); i++) {
			if (i != 14) {
				mMenuLayout.getChildAt(i).setOnClickListener(this);
			}
		}
		
		((LinearLayout) mRoot.findViewById(R.id.menu_homebtn_layout)).setBackgroundResource(R.color.light_red);
		
		if(!sharedPreferences.getString(Utils.IMAGEPATH, "").isEmpty()){
			
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
			.load(Utils.IMAGE_URL + sharedPreferences.getString(Utils.IMAGEPATH, ""))
			.placeholder(R.drawable.splash_logo_xxxhdpi)
			.error(R.drawable.splash_logo_xxxhdpi)
			.transform(transformation)
			.centerCrop()
			.resize(mDeviceWidth, (int) (mDeviceWidth))
			.into(mUserImg);
		}
		
//		if(sharedPreferences.getString(Utils.USER_TYPE, "").equalsIgnoreCase("customer")){
//		} else {
//			mRoot.findViewById(R.id.menu_homebtn_layout).setVisibility(View.GONE);
//			mRoot.findViewById(R.id.menu_my_cradits_layout).setVisibility(View.GONE);
//			mRoot.findViewById(R.id.menu_favourite_handyman_layout).setVisibility(View.GONE);
//		}

		return mRoot;
	}

	@Override
	public void onClick(View view) {
		sc.post(new Runnable() {
			@Override
			public void run() {
				pos_x = sc.getScrollX();
				pos_y = sc.getScrollY();
				// sc.scrollTo(pos_x, pos_y);
			}
		});

		switch (view.getId()) {
		
		case R.id.user_img:
			getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new MyProfileFragment()).commit();
			break;
		
		case R.id.menu_homebtn_layout:
			getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new ServiceAtHomeFragment()).commit();

			((LinearLayout) mRoot.findViewById(R.id.menu_homebtn_layout)).setBackgroundResource(R.color.light_red);
			((LinearLayout) mRoot.findViewById(R.id.menu_my_hirings_layout)).setBackgroundResource(android.R.color.transparent);
			((LinearLayout) mRoot.findViewById(R.id.menu_my_profile_layout)).setBackgroundResource(android.R.color.transparent);
			((LinearLayout) mRoot.findViewById(R.id.menu_my_cradits_layout)).setBackgroundResource(android.R.color.transparent);
			((LinearLayout) mRoot.findViewById(R.id.menu_favourite_handyman_layout)).setBackgroundResource(android.R.color.transparent);
			((LinearLayout) mRoot.findViewById(R.id.menu_feedback_suggestions_layout)).setBackgroundResource(android.R.color.transparent);
			((LinearLayout) mRoot.findViewById(R.id.menu_advertise_layout)).setBackgroundResource(android.R.color.transparent);
			((LinearLayout) mRoot.findViewById(R.id.menu_about_service_at_home_layout)).setBackgroundResource(android.R.color.transparent);
			((LinearLayout) mRoot.findViewById(R.id.menu_contact_support_layout)).setBackgroundResource(android.R.color.transparent);
			((LinearLayout) mRoot.findViewById(R.id.menu_privacy_policy_layout)).setBackgroundResource(android.R.color.transparent);
			((LinearLayout) mRoot.findViewById(R.id.menu_terms_and_conditions_layout)).setBackgroundResource(android.R.color.transparent);
			break;

	/*	case R.id.menu_find_near_by_handyman_layout:
			getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame,new FindNearByHandymanFragment()).commit();
			break;*/

		case R.id.menu_my_hirings_layout:
//			getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new MyHiringsFragment()).commit();
			getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new MyHiringsFragment_new()).commit();
			
			((LinearLayout) mRoot.findViewById(R.id.menu_homebtn_layout)).setBackgroundResource(android.R.color.transparent);
			((LinearLayout) mRoot.findViewById(R.id.menu_my_hirings_layout)).setBackgroundResource(R.color.light_red);
			((LinearLayout) mRoot.findViewById(R.id.menu_my_profile_layout)).setBackgroundResource(android.R.color.transparent);
			((LinearLayout) mRoot.findViewById(R.id.menu_my_cradits_layout)).setBackgroundResource(android.R.color.transparent);
			((LinearLayout) mRoot.findViewById(R.id.menu_favourite_handyman_layout)).setBackgroundResource(android.R.color.transparent);
			((LinearLayout) mRoot.findViewById(R.id.menu_feedback_suggestions_layout)).setBackgroundResource(android.R.color.transparent);
			((LinearLayout) mRoot.findViewById(R.id.menu_advertise_layout)).setBackgroundResource(android.R.color.transparent);
			((LinearLayout) mRoot.findViewById(R.id.menu_about_service_at_home_layout)).setBackgroundResource(android.R.color.transparent);
			((LinearLayout) mRoot.findViewById(R.id.menu_contact_support_layout)).setBackgroundResource(android.R.color.transparent);
			((LinearLayout) mRoot.findViewById(R.id.menu_privacy_policy_layout)).setBackgroundResource(android.R.color.transparent);
			((LinearLayout) mRoot.findViewById(R.id.menu_terms_and_conditions_layout)).setBackgroundResource(android.R.color.transparent);
			break;

		case R.id.menu_my_profile_layout:
			getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new MyProfileFragment()).commit();

			((LinearLayout) mRoot.findViewById(R.id.menu_homebtn_layout)).setBackgroundResource(android.R.color.transparent);
			((LinearLayout) mRoot.findViewById(R.id.menu_my_hirings_layout)).setBackgroundResource(android.R.color.transparent);
			((LinearLayout) mRoot.findViewById(R.id.menu_my_profile_layout)).setBackgroundResource(R.color.light_red);
			((LinearLayout) mRoot.findViewById(R.id.menu_my_cradits_layout)).setBackgroundResource(android.R.color.transparent);
			((LinearLayout) mRoot.findViewById(R.id.menu_favourite_handyman_layout)).setBackgroundResource(android.R.color.transparent);
			((LinearLayout) mRoot.findViewById(R.id.menu_feedback_suggestions_layout)).setBackgroundResource(android.R.color.transparent);
			((LinearLayout) mRoot.findViewById(R.id.menu_advertise_layout)).setBackgroundResource(android.R.color.transparent);
			((LinearLayout) mRoot.findViewById(R.id.menu_about_service_at_home_layout)).setBackgroundResource(android.R.color.transparent);
			((LinearLayout) mRoot.findViewById(R.id.menu_contact_support_layout)).setBackgroundResource(android.R.color.transparent);
			((LinearLayout) mRoot.findViewById(R.id.menu_privacy_policy_layout)).setBackgroundResource(android.R.color.transparent);
			((LinearLayout) mRoot.findViewById(R.id.menu_terms_and_conditions_layout)).setBackgroundResource(android.R.color.transparent);
			
			break;

		case R.id.menu_my_cradits_layout:
			getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame,new MyCreditsFragment()).commit();

			((LinearLayout) mRoot.findViewById(R.id.menu_homebtn_layout)).setBackgroundResource(android.R.color.transparent);
			((LinearLayout) mRoot.findViewById(R.id.menu_my_hirings_layout)).setBackgroundResource(android.R.color.transparent);
			((LinearLayout) mRoot.findViewById(R.id.menu_my_profile_layout)).setBackgroundResource(android.R.color.transparent);
			((LinearLayout) mRoot.findViewById(R.id.menu_my_cradits_layout)).setBackgroundResource(R.color.light_red);
			((LinearLayout) mRoot.findViewById(R.id.menu_favourite_handyman_layout)).setBackgroundResource(android.R.color.transparent);
			((LinearLayout) mRoot.findViewById(R.id.menu_feedback_suggestions_layout)).setBackgroundResource(android.R.color.transparent);
			((LinearLayout) mRoot.findViewById(R.id.menu_advertise_layout)).setBackgroundResource(android.R.color.transparent);
			((LinearLayout) mRoot.findViewById(R.id.menu_about_service_at_home_layout)).setBackgroundResource(android.R.color.transparent);
			((LinearLayout) mRoot.findViewById(R.id.menu_contact_support_layout)).setBackgroundResource(android.R.color.transparent);
			((LinearLayout) mRoot.findViewById(R.id.menu_privacy_policy_layout)).setBackgroundResource(android.R.color.transparent);
			((LinearLayout) mRoot.findViewById(R.id.menu_terms_and_conditions_layout)).setBackgroundResource(android.R.color.transparent);
			break;

		case R.id.menu_favourite_handyman_layout:
			getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame,new FavouriteHandymanFragment()).commit();

			((LinearLayout) mRoot.findViewById(R.id.menu_homebtn_layout)).setBackgroundResource(android.R.color.transparent);
			((LinearLayout) mRoot.findViewById(R.id.menu_my_hirings_layout)).setBackgroundResource(android.R.color.transparent);
			((LinearLayout) mRoot.findViewById(R.id.menu_my_profile_layout)).setBackgroundResource(android.R.color.transparent);
			((LinearLayout) mRoot.findViewById(R.id.menu_my_cradits_layout)).setBackgroundResource(android.R.color.transparent);
			((LinearLayout) mRoot.findViewById(R.id.menu_favourite_handyman_layout)).setBackgroundResource(R.color.light_red);
			((LinearLayout) mRoot.findViewById(R.id.menu_feedback_suggestions_layout)).setBackgroundResource(android.R.color.transparent);
			((LinearLayout) mRoot.findViewById(R.id.menu_advertise_layout)).setBackgroundResource(android.R.color.transparent);
			((LinearLayout) mRoot.findViewById(R.id.menu_about_service_at_home_layout)).setBackgroundResource(android.R.color.transparent);
			((LinearLayout) mRoot.findViewById(R.id.menu_contact_support_layout)).setBackgroundResource(android.R.color.transparent);
			((LinearLayout) mRoot.findViewById(R.id.menu_privacy_policy_layout)).setBackgroundResource(android.R.color.transparent);
			((LinearLayout) mRoot.findViewById(R.id.menu_terms_and_conditions_layout)).setBackgroundResource(android.R.color.transparent);
			
			break;

		/*case R.id.menu_settings_layout:
			titleText.setText(R.string.menu_settings);
			// titleText.setText(R.string.title_editshoppinglisttext);
			// ((DashboardLoginActivity)
			// getActivity()).pushFragments(AppConstants.TAB_SHOPPING, new
			// ShoppingListEditFragment(), false, true);

			break;*/

		case R.id.menu_feedback_suggestions_layout:
//			Toast.makeText(getActivity(), "When store app on play store than give directly feedback same as other apps.",	Toast.LENGTH_LONG).show();
//			startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.whatsapp&hl=en")));
			getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame,new FeedbackFragment()).commit();
			
			((LinearLayout) mRoot.findViewById(R.id.menu_homebtn_layout)).setBackgroundResource(android.R.color.transparent);
			((LinearLayout) mRoot.findViewById(R.id.menu_my_hirings_layout)).setBackgroundResource(android.R.color.transparent);
			((LinearLayout) mRoot.findViewById(R.id.menu_my_profile_layout)).setBackgroundResource(android.R.color.transparent);
			((LinearLayout) mRoot.findViewById(R.id.menu_my_cradits_layout)).setBackgroundResource(android.R.color.transparent);
			((LinearLayout) mRoot.findViewById(R.id.menu_favourite_handyman_layout)).setBackgroundResource(android.R.color.transparent);
			((LinearLayout) mRoot.findViewById(R.id.menu_feedback_suggestions_layout)).setBackgroundResource(R.color.light_red);
			((LinearLayout) mRoot.findViewById(R.id.menu_advertise_layout)).setBackgroundResource(android.R.color.transparent);
			((LinearLayout) mRoot.findViewById(R.id.menu_about_service_at_home_layout)).setBackgroundResource(android.R.color.transparent);
			((LinearLayout) mRoot.findViewById(R.id.menu_contact_support_layout)).setBackgroundResource(android.R.color.transparent);
			((LinearLayout) mRoot.findViewById(R.id.menu_privacy_policy_layout)).setBackgroundResource(android.R.color.transparent);
			((LinearLayout) mRoot.findViewById(R.id.menu_terms_and_conditions_layout)).setBackgroundResource(android.R.color.transparent);
			break;
			
		case R.id.menu_advertise_layout:
			
			getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame,new AdvertiseFragment()).commit();
			
			((LinearLayout) mRoot.findViewById(R.id.menu_homebtn_layout)).setBackgroundResource(android.R.color.transparent);
			((LinearLayout) mRoot.findViewById(R.id.menu_my_hirings_layout)).setBackgroundResource(android.R.color.transparent);
			((LinearLayout) mRoot.findViewById(R.id.menu_my_profile_layout)).setBackgroundResource(android.R.color.transparent);
			((LinearLayout) mRoot.findViewById(R.id.menu_my_cradits_layout)).setBackgroundResource(android.R.color.transparent);
			((LinearLayout) mRoot.findViewById(R.id.menu_favourite_handyman_layout)).setBackgroundResource(android.R.color.transparent);
			((LinearLayout) mRoot.findViewById(R.id.menu_feedback_suggestions_layout)).setBackgroundResource(android.R.color.transparent);
			((LinearLayout) mRoot.findViewById(R.id.menu_advertise_layout)).setBackgroundResource(R.color.light_red);
			((LinearLayout) mRoot.findViewById(R.id.menu_about_service_at_home_layout)).setBackgroundResource(android.R.color.transparent);
			((LinearLayout) mRoot.findViewById(R.id.menu_contact_support_layout)).setBackgroundResource(android.R.color.transparent);
			((LinearLayout) mRoot.findViewById(R.id.menu_privacy_policy_layout)).setBackgroundResource(android.R.color.transparent);
			((LinearLayout) mRoot.findViewById(R.id.menu_terms_and_conditions_layout)).setBackgroundResource(android.R.color.transparent);
			break;

		case R.id.menu_logout_layout:
			
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle("Logout");
			builder.setCancelable(false);
			builder.setMessage("Are you sure for logout.");
			builder.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							
							onLogout(sharedPreferences.getString(Utils.USER_ID, ""),"0");
						}
					});
			builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					
				}
				
			});
			AlertDialog alertDialog = builder.create();
			alertDialog.show();
			

			break;

		case R.id.view_layout:
			break;

		case R.id.menu_about_service_at_home_layout:
			getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame,new AboutServiceFragment()).commit();
			
			((LinearLayout) mRoot.findViewById(R.id.menu_homebtn_layout)).setBackgroundResource(android.R.color.transparent);
			((LinearLayout) mRoot.findViewById(R.id.menu_my_hirings_layout)).setBackgroundResource(android.R.color.transparent);
			((LinearLayout) mRoot.findViewById(R.id.menu_my_profile_layout)).setBackgroundResource(android.R.color.transparent);
			((LinearLayout) mRoot.findViewById(R.id.menu_my_cradits_layout)).setBackgroundResource(android.R.color.transparent);
			((LinearLayout) mRoot.findViewById(R.id.menu_favourite_handyman_layout)).setBackgroundResource(android.R.color.transparent);
			((LinearLayout) mRoot.findViewById(R.id.menu_feedback_suggestions_layout)).setBackgroundResource(android.R.color.transparent);
			((LinearLayout) mRoot.findViewById(R.id.menu_advertise_layout)).setBackgroundResource(android.R.color.transparent);
			((LinearLayout) mRoot.findViewById(R.id.menu_about_service_at_home_layout)).setBackgroundResource(R.color.light_red);
			((LinearLayout) mRoot.findViewById(R.id.menu_contact_support_layout)).setBackgroundResource(android.R.color.transparent);
			((LinearLayout) mRoot.findViewById(R.id.menu_privacy_policy_layout)).setBackgroundResource(android.R.color.transparent);
			((LinearLayout) mRoot.findViewById(R.id.menu_terms_and_conditions_layout)).setBackgroundResource(android.R.color.transparent);
			break;

		case R.id.menu_contact_support_layout:
			getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame,new ContactSupportFragment()).commit();

			((LinearLayout) mRoot.findViewById(R.id.menu_homebtn_layout)).setBackgroundResource(android.R.color.transparent);
			((LinearLayout) mRoot.findViewById(R.id.menu_my_hirings_layout)).setBackgroundResource(android.R.color.transparent);
			((LinearLayout) mRoot.findViewById(R.id.menu_my_profile_layout)).setBackgroundResource(android.R.color.transparent);
			((LinearLayout) mRoot.findViewById(R.id.menu_my_cradits_layout)).setBackgroundResource(android.R.color.transparent);
			((LinearLayout) mRoot.findViewById(R.id.menu_favourite_handyman_layout)).setBackgroundResource(android.R.color.transparent);
			((LinearLayout) mRoot.findViewById(R.id.menu_feedback_suggestions_layout)).setBackgroundResource(android.R.color.transparent);
			((LinearLayout) mRoot.findViewById(R.id.menu_advertise_layout)).setBackgroundResource(android.R.color.transparent);
			((LinearLayout) mRoot.findViewById(R.id.menu_about_service_at_home_layout)).setBackgroundResource(android.R.color.transparent);
			((LinearLayout) mRoot.findViewById(R.id.menu_contact_support_layout)).setBackgroundResource(R.color.light_red);
			((LinearLayout) mRoot.findViewById(R.id.menu_privacy_policy_layout)).setBackgroundResource(android.R.color.transparent);
			((LinearLayout) mRoot.findViewById(R.id.menu_terms_and_conditions_layout)).setBackgroundResource(android.R.color.transparent);
			break;

		case R.id.menu_privacy_policy_layout:
			getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame,new PrivacyPolicyFragment()).commit();

			((LinearLayout) mRoot.findViewById(R.id.menu_homebtn_layout)).setBackgroundResource(android.R.color.transparent);
			((LinearLayout) mRoot.findViewById(R.id.menu_my_hirings_layout)).setBackgroundResource(android.R.color.transparent);
			((LinearLayout) mRoot.findViewById(R.id.menu_my_profile_layout)).setBackgroundResource(android.R.color.transparent);
			((LinearLayout) mRoot.findViewById(R.id.menu_my_cradits_layout)).setBackgroundResource(android.R.color.transparent);
			((LinearLayout) mRoot.findViewById(R.id.menu_favourite_handyman_layout)).setBackgroundResource(android.R.color.transparent);
			((LinearLayout) mRoot.findViewById(R.id.menu_feedback_suggestions_layout)).setBackgroundResource(android.R.color.transparent);
			((LinearLayout) mRoot.findViewById(R.id.menu_advertise_layout)).setBackgroundResource(android.R.color.transparent);
			((LinearLayout) mRoot.findViewById(R.id.menu_about_service_at_home_layout)).setBackgroundResource(android.R.color.transparent);
			((LinearLayout) mRoot.findViewById(R.id.menu_contact_support_layout)).setBackgroundResource(android.R.color.transparent);
			((LinearLayout) mRoot.findViewById(R.id.menu_privacy_policy_layout)).setBackgroundResource(R.color.light_red);
			((LinearLayout) mRoot.findViewById(R.id.menu_terms_and_conditions_layout)).setBackgroundResource(android.R.color.transparent);
			break;

		case R.id.menu_terms_and_conditions_layout:
			getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame,new TermsAndConditionFragment()).commit();

			((LinearLayout) mRoot.findViewById(R.id.menu_homebtn_layout)).setBackgroundResource(android.R.color.transparent);
			((LinearLayout) mRoot.findViewById(R.id.menu_my_hirings_layout)).setBackgroundResource(android.R.color.transparent);
			((LinearLayout) mRoot.findViewById(R.id.menu_my_profile_layout)).setBackgroundResource(android.R.color.transparent);
			((LinearLayout) mRoot.findViewById(R.id.menu_my_cradits_layout)).setBackgroundResource(android.R.color.transparent);
			((LinearLayout) mRoot.findViewById(R.id.menu_favourite_handyman_layout)).setBackgroundResource(android.R.color.transparent);
			((LinearLayout) mRoot.findViewById(R.id.menu_feedback_suggestions_layout)).setBackgroundResource(android.R.color.transparent);
			((LinearLayout) mRoot.findViewById(R.id.menu_advertise_layout)).setBackgroundResource(android.R.color.transparent);
			((LinearLayout) mRoot.findViewById(R.id.menu_about_service_at_home_layout)).setBackgroundResource(android.R.color.transparent);
			((LinearLayout) mRoot.findViewById(R.id.menu_contact_support_layout)).setBackgroundResource(android.R.color.transparent);
			((LinearLayout) mRoot.findViewById(R.id.menu_privacy_policy_layout)).setBackgroundResource(android.R.color.transparent);
			((LinearLayout) mRoot.findViewById(R.id.menu_terms_and_conditions_layout)).setBackgroundResource(R.color.light_red);
			break;

		default:
			break;

		}
		((MainActivity) getActivity()).getSlidingMenu().toggle();

	}

	@Override
	public void onResume() {
		super.onResume();
		Logger.e("", "onResume menu");
	}

	public static void viewSelectedLayout(boolean org, int pos, int pos2) {
		if (org) {
			// ((TextView)
			// mMenuLayout.getChildAt(pos)).setBackgroundResource(R.drawable.sidemenu_active_bg1);
			// ((TextView)
			// mMenuLayout.getChildAt(21)).setCompoundDrawablesWithIntrinsicBounds(R.drawable.menuitem_arrow_active,
			// 0, 0, 0);
		}
		/*
		 * else if(!org) { ((TextView)
		 * mMenuLayout.getChildAt(pos)).setBackgroundResource
		 * (R.drawable.sidemenu_active_bg1); if(pos != pos2) { ((TextView)
		 * mMenuLayout
		 * .getChildAt(pos2)).setBackgroundResource(R.drawable.sidemenu_normal_bg1
		 * ); } // ((TextView)
		 * mMenuLayout.getChildAt(1)).setCompoundDrawablesWithIntrinsicBounds
		 * (R.drawable.menuitem_arrow_active, 0, 0, 0); }
		 */
	}

	public static void viewSelectedPosition() {
		sc.post(new Runnable() {
			public void run() {
				// pos_x = sc.getScrollX();
				// pos_y = sc.getScrollY();
				sc.scrollTo(pos_x, pos_y);
			}
		});
	}
	
	private void onLogout(String id, String is_login) {
		
        if (Utils.checkInternetConnection(getActivity())) {
        	LogoutRequestTask logoutRequestTask = new LogoutRequestTask(getActivity());
        	logoutRequestTask.setAsyncCallListener(new AsyncCallListener() {

                @Override
                public void onResponseReceived(Object response) {
                    RegisterModel registerModel = (RegisterModel) response;
                    if(registerModel.success.equalsIgnoreCase("1")) {
						Toast.makeText(getActivity(),registerModel.message, Toast.LENGTH_SHORT).show();
						
							Utils.storeString(sharedPreferences, Utils.MOBILE_NO, "");
							Utils.storeString(sharedPreferences, Utils.PASSWORD, "");
							Utils.storeString(sharedPreferences, Utils.USER_ID, "");
							Utils.storeString(sharedPreferences, Utils.FIRSTNAME, "");
							Utils.storeString(sharedPreferences, Utils.LASTNAME, "");
							Utils.storeString(sharedPreferences, Utils.IMAGEPATH, "");
							Utils.storeString(sharedPreferences, Utils.EMAIL, "");
							Utils.storeString(sharedPreferences, Utils.USER_PROFILE, "");
//							Utils.storeString(sharedPreferences, Utils.DEVICE_ID, "");
							startActivity(new Intent(getActivity(), LoginActivity.class)); 
							getActivity().finish();
							
                        } else if (registerModel.success.equalsIgnoreCase("0")) {
                            Toast.makeText(getActivity(), registerModel.message , Toast.LENGTH_SHORT).show();
                        }
                    }

                @Override
                public void onErrorReceived(String error) {
                    Toast.makeText(getActivity(), getString(R.string.try_again), Toast.LENGTH_SHORT).show();
                }
            });
            logoutRequestTask.execute(id, is_login);
        } else {
            Toast.makeText(getActivity(), getString(R.string.connection), Toast.LENGTH_SHORT).show();
        }
    }
}
