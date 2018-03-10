package com.handyman.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;

import com.handyman.MainActivity;
import com.handyman.R;
import com.handyman.service.Utils;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class PurchaseCreditsFragment extends BaseFragment implements OnClickListener{
	
	private static String TAG = "PurchaseCreditsFragment";
	
	private SharedPreferences mSharedPreferences;
	
	Fragment fr;
	View mRootView;
	EditText mPutCredits,mGetCredits;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,	Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.fragment_purchase_credits, container, false);
		initview();
		return mRootView;
	}

	private void initview() {
		mSharedPreferences = getActivity().getSharedPreferences(Utils.PREF_NAME, Context.MODE_PRIVATE); 
		((MainActivity) getActivity()).setTitleText("", "Purcahse Credits", "", View.VISIBLE, View.GONE, View.GONE,View.GONE,View.GONE);
		getActivity().findViewById(R.id.title).setVisibility(View.GONE);
		getActivity().findViewById(R.id.txtUrl).setVisibility(View.GONE);
		
		mActivity.getSlidingMenu().setMode(SlidingMenu.LEFT);
		mActivity.getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		
		mPutCredits = (EditText) mRootView.findViewById(R.id.put_credits_edit_text);
		mGetCredits = (EditText) mRootView.findViewById(R.id.get_credits_edit_text);
		mRootView.findViewById(R.id.payment_Button).setOnClickListener(this);
		mRootView.findViewById(R.id.get_credit_Button).setOnClickListener(this);
		
		mPutCredits.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
					
				mGetCredits.setText(mPutCredits.getText().toString().trim());
				
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,	int after) {
			}
			
			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		
		case R.id.payment_Button:
			if(fieldValidation()){
				
			}
			break;
			
		case R.id.get_credit_Button:
//			mGetCredits.setText(""+Integer.parseInt(mPutCredits.getText().toString().trim()) * 2);
			break;
		}
	
	}
	
	
	public boolean fieldValidation() {
		boolean flag = true;
		 if (!Utils.validateString(mPutCredits.getText().toString())) {
			flag = false;
			Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), "Please Enter Credits");
			mPutCredits.requestFocus();
		} 
		return flag;
	}
	
}
