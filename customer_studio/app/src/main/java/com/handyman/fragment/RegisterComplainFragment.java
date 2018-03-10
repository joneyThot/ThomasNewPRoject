package com.handyman.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
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
import android.widget.TextView;
import android.widget.Toast;

import com.handyman.MainActivity;
import com.handyman.R;
import com.handyman.model.HireHandymanModel;
import com.handyman.service.AsyncCallListener;
import com.handyman.service.ComplainHandymanRequestTask;
import com.handyman.service.Utils;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class RegisterComplainFragment extends BaseFragment implements OnClickListener{
	
	private static String TAG = "RegisterComplainFragment";
	
	private SharedPreferences mSharedPreferences;
	
	Fragment fr;
	View mRootView;
	EditText mTitle,mDescription;
	String hire_handyman_id = "",hire_handyman_name = "",hire_handyman_rating = "", hire_handyman_email = "";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,	Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.fragment_complain, container, false);
		initview();
		return mRootView;
	}

	private void initview() {
		mSharedPreferences = getActivity().getSharedPreferences(Utils.PREF_NAME, Context.MODE_PRIVATE); 
		((MainActivity) getActivity()).setTitleText("", getString(R.string.register_complain), "", View.VISIBLE, View.GONE, View.GONE,View.GONE,View.GONE);
		getActivity().findViewById(R.id.title).setVisibility(View.GONE);
		getActivity().findViewById(R.id.txtUrl).setVisibility(View.GONE);
		
		mActivity.getSlidingMenu().setMode(SlidingMenu.LEFT);
		mActivity.getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		
		if(getArguments() != null){
			hire_handyman_id = getArguments().getString(Utils.HIRE_HANDYMAN_ID);
			hire_handyman_name = getArguments().getString(Utils.HIRE_HANDYMAN_NAME);
			hire_handyman_email = getArguments().getString(Utils.HIRE_HANDYMAN_EMAIL);
			hire_handyman_rating = getArguments().getString(Utils.HIRE_HANDYMAN_RATING);
		}
		
		if(Utils.validateString(hire_handyman_id)){
			((TextView) mRootView.findViewById(R.id.complain_handyman_name_text)).setText(hire_handyman_name);
			((TextView) mRootView.findViewById(R.id.complain_handyman_email_text)).setText(hire_handyman_email);
			if(Utils.validateString(hire_handyman_rating) && hire_handyman_rating.equalsIgnoreCase("0")){
				((RatingBar) mRootView.findViewById(R.id.complain_handyman_rating)).setRating(0);
			} else {
				((RatingBar) mRootView.findViewById(R.id.complain_handyman_rating)).setRating(Float.parseFloat(hire_handyman_rating));
			}
		} else {
			((TextView) mRootView.findViewById(R.id.complain_handyman_name_text)).setText(mSharedPreferences.getString(Utils.HANDYMAN_NAME, ""));
			((TextView) mRootView.findViewById(R.id.complain_handyman_email_text)).setText(mSharedPreferences.getString(Utils.HANDYMAN_EMAIL, ""));
			((RatingBar) mRootView.findViewById(R.id.complain_handyman_rating)).setRating(Float.parseFloat(mSharedPreferences.getString(Utils.HANDYMAN_RATING, "")));
		}
		
		mRootView.findViewById(R.id.complain_handyman_rating);
		mTitle = (EditText) mRootView.findViewById(R.id.complain_handyman_title_text);
		mDescription = (EditText) mRootView.findViewById(R.id.complain_handyman_description_text);
		mRootView.findViewById(R.id.complain_submit_Button).setOnClickListener(this);
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.complain_submit_Button:
			 
			if(fieldValidation()){
				if(Utils.validateString(hire_handyman_id)){
					ComplainHandyman(mSharedPreferences.getString(Utils.USER_ID, ""),hire_handyman_id,mTitle.getText().toString(),mDescription.getText().toString());
				} else {
					ComplainHandyman(mSharedPreferences.getString(Utils.USER_ID, ""),mSharedPreferences.getString(Utils.HANDYMAN_ID, ""),mTitle.getText().toString(),mDescription.getText().toString());
				}
				
			}
			break;
		}
	}
	
	public boolean fieldValidation() {
		boolean flag = true;
		if (!Utils.validateString(mTitle.getText().toString())) {
			flag = false;
			Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), getResources().getString(R.string.enter_register_complain_title));
			mTitle.requestFocus();
		} else if(!Utils.validateString(mDescription.getText().toString())){
			flag = false;
			Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), getResources().getString(R.string.enter_register_complain_description));
			mDescription.requestFocus();
		}
		return flag;
	}
	
	private void ComplainHandyman(String complain_from, String complain_to, String title, String description) {
		if(Utils.checkInternetConnection(getActivity())){
			ComplainHandymanRequestTask complainHandymanRequestTask = new ComplainHandymanRequestTask(getActivity());
			complainHandymanRequestTask.setAsyncCallListener(new AsyncCallListener() {

				@SuppressWarnings("unchecked")
				@Override
				public void onResponseReceived(Object response) {
					InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
					 
					 HireHandymanModel hireHandymanModel = (HireHandymanModel) response;
	                    if(hireHandymanModel.success.equalsIgnoreCase("1")) {
	                            Toast.makeText(getActivity(), hireHandymanModel.message , Toast.LENGTH_SHORT).show();
//	                            getActivity().getSupportFragmentManager().popBackStack("HandymanProfileFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
//	                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new HandymanProfileFragment()).commit();
	                            getActivity().getSupportFragmentManager().popBackStack();
	                        } else if (hireHandymanModel.success.equalsIgnoreCase("0")) {
	                            Toast.makeText(getActivity(), hireHandymanModel.message , Toast.LENGTH_SHORT).show();
	                        }
					
				}
				@Override
				public void onErrorReceived(String error) {
					Toast.makeText(getActivity(), getString(R.string.try_again), Toast.LENGTH_SHORT).show();
				}
			});
			complainHandymanRequestTask.execute(complain_from,complain_to,title,description);
		}else{
			Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), getResources().getString(R.string.connection));
		}
	}	
	
}
