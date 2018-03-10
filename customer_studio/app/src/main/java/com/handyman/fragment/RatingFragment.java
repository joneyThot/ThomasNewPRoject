package com.handyman.fragment;

import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.handyman.MainActivity;
import com.handyman.R;
import com.handyman.adapter.CategoryAdapter;
import com.handyman.logger.Logger;
import com.handyman.model.CheckHandymanRatingModel;
import com.handyman.model.HireHandymanModel;
import com.handyman.model.StateModel;
import com.handyman.service.AsyncCallListener;
import com.handyman.service.CheckRateHandymanRequestTask;
import com.handyman.service.RateHandymanRequestTask;
import com.handyman.service.Utils;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class RatingFragment extends BaseFragment implements OnClickListener{
	
	private static String TAG = "RateFragment";
	
	private SharedPreferences mSharedPreferences;
	ArrayList<CheckHandymanRatingModel> checkHandymanRatingModelList = new ArrayList<CheckHandymanRatingModel>();
	
	Fragment fr;
	View mRootView;
	EditText mDescription;
	RatingBar mHandymanRating;
	String rateStr;
	String rating_check, rating_dec;
	String hire_handyman_id = "",hire_handyman_status_id = "", hire_handyman_name = "",hire_handyman_rating = "", hire_handyman_email = "";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,	Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.fragment_rate, container, false);
		initview();
		return mRootView;
	}

	private void initview() {
		mSharedPreferences = getActivity().getSharedPreferences(Utils.PREF_NAME, Context.MODE_PRIVATE); 
		((MainActivity) getActivity()).setTitleText("", getString(R.string.rate_handyman), "", View.VISIBLE, View.GONE, View.GONE,View.GONE, View.GONE);
		getActivity().findViewById(R.id.title).setVisibility(View.GONE);
		getActivity().findViewById(R.id.txtUrl).setVisibility(View.GONE);
		
		mActivity.getSlidingMenu().setMode(SlidingMenu.LEFT);
		mActivity.getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		
		if(getArguments() != null){
			hire_handyman_id = getArguments().getString(Utils.HIRE_HANDYMAN_ID);
			hire_handyman_status_id = getArguments().getString(Utils.HIRE_HANDYMAN_STATUS_ID);
			hire_handyman_name = getArguments().getString(Utils.HIRE_HANDYMAN_NAME);
			hire_handyman_email = getArguments().getString(Utils.HIRE_HANDYMAN_EMAIL);
			hire_handyman_rating = getArguments().getString(Utils.HIRE_HANDYMAN_RATING);
		}
	
		if(Utils.validateString(hire_handyman_id)){
//			getRateCheck(hire_handyman_id, mSharedPreferences.getString(Utils.USER_ID, ""));
			((TextView) mRootView.findViewById(R.id.rate_handyman_name_text)).setText(hire_handyman_name);
			((TextView) mRootView.findViewById(R.id.rate_handyman_email_text)).setText(hire_handyman_email);
			if(Utils.validateString(hire_handyman_rating) && hire_handyman_rating.equalsIgnoreCase("0")){
				((RatingBar) mRootView.findViewById(R.id.rate_handyman_rating)).setRating(0);
			} else {
				((RatingBar) mRootView.findViewById(R.id.rate_handyman_rating)).setRating(Float.parseFloat(hire_handyman_rating));
			}
			
		}
//		else {
////			getRateCheck(mSharedPreferences.getString(Utils.HANDYMAN_ID, ""),mSharedPreferences.getString(Utils.USER_ID, ""));
//			((TextView) mRootView.findViewById(R.id.rate_handyman_name_text)).setText(mSharedPreferences.getString(Utils.HANDYMAN_NAME, ""));
//			((TextView) mRootView.findViewById(R.id.rate_handyman_email_text)).setText(mSharedPreferences.getString(Utils.HANDYMAN_EMAIL, ""));
//			((RatingBar) mRootView.findViewById(R.id.rate_handyman_rating)).setRating(Float.parseFloat(mSharedPreferences.getString(Utils.HANDYMAN_RATING, "")));
//		}
		
		
		mRootView.findViewById(R.id.complain_handyman_rating);
		mDescription = (EditText) mRootView.findViewById(R.id.rate_handyman_description_text);
		mRootView.findViewById(R.id.rate_submit_Button).setOnClickListener(this);
		mHandymanRating = (RatingBar) mRootView.findViewById(R.id.handyman_rating);
		mHandymanRating.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
			
			@Override
			public void onRatingChanged(RatingBar ratingBar, float rating,	boolean fromUser) {
				Logger.i(TAG, "Rating:: "+ rating);
				rateStr = String.valueOf(rating);
			}
		});
		
//		if(!mSharedPreferences.getString("des", "").isEmpty() && !mSharedPreferences.getString("rate", "").isEmpty()){
//			mHandymanRating.setRating(Float.parseFloat(mSharedPreferences.getString("rate", "")));
//			mDescription.setText(mSharedPreferences.getString("des", ""));
//			((TextView)mRootView.findViewById(R.id.rate_submit_Button)).setText("UPDATE");
//		} else {
//			mHandymanRating.setRating(0);
//			mDescription.setText("");
//			((TextView)mRootView.findViewById(R.id.rate_submit_Button)).setText("SUBMIT");
//		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.rate_submit_Button:
			if(fieldValidation()){
				if(Utils.validateString(hire_handyman_id)){
					getRate(hire_handyman_id, mSharedPreferences.getString(Utils.USER_ID, ""),hire_handyman_status_id,rateStr,mDescription.getText().toString());
				}
				/*else {
					getRate(mSharedPreferences.getString(Utils.HANDYMAN_ID, ""),mSharedPreferences.getString(Utils.USER_ID, ""),rateStr,mDescription.getText().toString());
				}*/
			}
			break;
		}
	}
	
	public boolean fieldValidation() {
		boolean flag = true;
		if(!Utils.validateString(mDescription.getText().toString())){
			flag = false;
			Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), getResources().getString(R.string.enter_register_complain_description));
			mDescription.requestFocus();
		}
		return flag;
	}
	
	private void getRate(String handyman_id, String client_id,String hire_id, String rate, String description) {
		if(Utils.checkInternetConnection(getActivity())){
			RateHandymanRequestTask rateHandymanRequestTask = new RateHandymanRequestTask(getActivity());
			rateHandymanRequestTask.setAsyncCallListener(new AsyncCallListener() {

				@SuppressWarnings("unchecked")
				@Override
				public void onResponseReceived(Object response) {
					 HireHandymanModel hireHandymanModel = (HireHandymanModel) response;
	                    if(hireHandymanModel.success.equalsIgnoreCase("1")) {
	                            Toast.makeText(getActivity(), hireHandymanModel.message , Toast.LENGTH_SHORT).show();
	                            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
	               			 	imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
//	                            getActivity().getSupportFragmentManager().popBackStack("HandymanProfileFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
	                            getActivity().getSupportFragmentManager().popBackStack();
//	                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new HandymanProfileFragment()).commit();
	                        } else if (hireHandymanModel.success.equalsIgnoreCase("0")) {
	                            Toast.makeText(getActivity(), hireHandymanModel.message , Toast.LENGTH_SHORT).show();
	                        }
				}
				@Override
				public void onErrorReceived(String error) {
					Toast.makeText(getActivity(), getString(R.string.try_again), Toast.LENGTH_SHORT).show();
				}
			});
			rateHandymanRequestTask.execute(handyman_id,client_id,hire_id,rate,description);
		}else{
			Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), 
					getResources().getString(R.string.connection));
		}
	}
	
	/*private void getRateCheck(String handyman_id, String client_id) {
		if(Utils.checkInternetConnection(getActivity())){
			CheckRateHandymanRequestTask checkRateHandymanRequestTask = new CheckRateHandymanRequestTask(getActivity());
			checkRateHandymanRequestTask.setAsyncCallListener(new AsyncCallListener() {

				@SuppressWarnings("unchecked")
				@Override
				public void onResponseReceived(Object response) {
//					checkHandymanRatingModelList = (ArrayList<CheckHandymanRatingModel>) response;
//					Logger.e(TAG, "checkHandymanRatingModelList SIZE -- " + checkHandymanRatingModelList.size());
//					if (checkHandymanRatingModelList != null) {
//						for (int i = 0; i < checkHandymanRatingModelList.size(); i++) {
//							rating_check = checkHandymanRatingModelList.get(i).getRate();
//							rating_dec = checkHandymanRatingModelList.get(i).getDescription();
//							
//						}
//						
//						
//					} else {
//						Toast.makeText(getActivity(), "Category not found. ", Toast.LENGTH_SHORT).show();
//					}
					
					CheckHandymanRatingModel checkHandymanRatingModel = (CheckHandymanRatingModel) response;
	                    if(checkHandymanRatingModel.success.equalsIgnoreCase("1")) {
	                            Toast.makeText(getActivity(), checkHandymanRatingModel.message , Toast.LENGTH_SHORT).show();
//	                            rating_check = checkHandymanRatingModel.user.getRate();
//								rating_dec = checkHandymanRatingModel.user.getDescription();
//								Utils.storeString(mSharedPreferences, "rate", checkHandymanRatingModel.user.getRate());
//								Utils.storeString(mSharedPreferences, "des", checkHandymanRatingModel.user.getDescription());
	                        } else if (checkHandymanRatingModel.success.equalsIgnoreCase("0")) {
	                            Toast.makeText(getActivity(), checkHandymanRatingModel.message , Toast.LENGTH_SHORT).show();
	                    }
				}
				@Override
				public void onErrorReceived(String error) {
					Toast.makeText(getActivity(), getString(R.string.try_again), Toast.LENGTH_SHORT).show();
				}
			});
			checkRateHandymanRequestTask.execute(handyman_id,client_id);
		}else{
			Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), 
					getResources().getString(R.string.connection));
		}
	}*/
	
/*	OnClickListener onCategoryClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if(v.getTag() != null){
				int index = (Integer) v.getTag();
				Logger.e(TAG, "CATEGORY CLICK INDEX ::" + index);
				Utils.storeString(mSharedPreferences, Utils.CATEGORY_ID, mCategoryArrayList.get(index).getId());				
				ServiceAtHomeSubCategoryFragment serviceAtHomeSubCategoryFragment = new ServiceAtHomeSubCategoryFragment();
				Bundle bundle = new Bundle();
				bundle.putSerializable(Utils.CATEGORY_ITEM_DETAILS, mCategoryArrayList.get(index));
//				bundle.putString("ID", String.valueOf(index++));
				serviceAtHomeSubCategoryFragment.setArguments(bundle);
				getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, serviceAtHomeSubCategoryFragment).addToBackStack(null).commit();
			}
		}
	};*/
	
	
}
