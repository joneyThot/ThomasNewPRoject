package com.handy.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.handy.MainActivity;
import com.handy.R;
import com.handy.model.HireHandymanModel;
import com.handy.service.AsyncCallListener;
import com.handy.service.ComplainHandymanRequestTask;
import com.handy.service.Utils;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class RegisterComplainFragment extends BaseFragment implements OnClickListener{
	
	private static String TAG = "RegisterComplainFragment";
	
	private SharedPreferences mSharedPreferences;
	
	Fragment fr;
	View mRootView;
	EditText mTitle,mDescription;
	String client_name = "", client_email = "";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,	Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.fragment_complain, container, false);
		initview();
		return mRootView;
	}

	private void initview() {
		mSharedPreferences = getActivity().getSharedPreferences(Utils.PREF_NAME, Context.MODE_PRIVATE); 
		((MainActivity) getActivity()).setTitleText("", getString(R.string.register_complain), View.VISIBLE, View.GONE, View.GONE);
		getActivity().findViewById(R.id.title).setVisibility(View.GONE);	
		
		mActivity.getSlidingMenu().setMode(SlidingMenu.LEFT);
		mActivity.getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		
		if(getArguments() != null){
			client_name = getArguments().getString(Utils.CLIENT_NAME);
			client_email = getArguments().getString(Utils.CLIENT_EMAIL);
		}
		
		if(Utils.validateString(client_name)){
			((TextView) mRootView.findViewById(R.id.complain_handyman_name_text)).setText(client_name);	
		}
		
		if(Utils.validateString(client_email)){
			((TextView) mRootView.findViewById(R.id.complain_handyman_email_text)).setText(client_email);			
		}
		
		mRootView.findViewById(R.id.complain_handyman_rating).setVisibility(View.GONE);
		
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
					ComplainHandyman(mSharedPreferences.getString(Utils.USER_ID, ""),mSharedPreferences.getString(Utils.CLIENT_ID, ""),mTitle.getText().toString(),mDescription.getText().toString());
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
