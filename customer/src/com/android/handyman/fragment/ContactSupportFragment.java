package com.android.handyman.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.handyman.MainActivity;
import com.android.handyman.R;
import com.android.handyman.model.RegisterModel;
import com.android.handyman.service.AsyncCallListener;
import com.android.handyman.service.GetContackSupportRequestTask;
import com.android.handyman.service.Utils;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class ContactSupportFragment extends BaseFragment implements OnClickListener{
	
	Fragment fr;
	View mRootView;
	String email,phone;
	TextView email_text,phone_text;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,	Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.fragment_contact_support, container, false);
		initview();
		return mRootView;
	}

	private void initview() {
		((MainActivity) getActivity()).setTitleText(getString(R.string.menu_contact_support), "", View.GONE, View.VISIBLE, View.GONE, View.GONE);
		getActivity().findViewById(R.id.title_back).setVisibility(View.GONE);	
		
		mActivity.getSlidingMenu().setMode(SlidingMenu.LEFT);
		mActivity.getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		
		mRootView.findViewById(R.id.call_logo).setOnClickListener(this);

		email_text = (TextView) mRootView.findViewById(R.id.email_text);
		email_text.setOnClickListener(this);
		
		phone_text = (TextView) mRootView.findViewById(R.id.rate_handyman_no_text);
		phone_text.setOnClickListener(this);
		
		onContactSupport();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.call_logo:
//			double no = Double.parseDouble(phone);
			Uri number = Uri.parse("tel:"+phone);
			Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
			startActivity(callIntent);
			break;

		case R.id.email_text:
			final Intent emailIntent = new Intent( android.content.Intent.ACTION_SEND);
			emailIntent.setType("plain/text");
			emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[] { email_text.getText().toString() });
			emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Email Subject");
			emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Email Body");
			startActivity(Intent.createChooser(emailIntent, "Send mail..."));

			break;
		}
	}
	
	private void onContactSupport() {
		
        if (Utils.checkInternetConnection(getActivity())) {
        	GetContackSupportRequestTask getContackSupportRequestTask = new GetContackSupportRequestTask(getActivity());
        	getContackSupportRequestTask.setAsyncCallListener(new AsyncCallListener() {

                @Override
                public void onResponseReceived(Object response) {
                    RegisterModel registerModel = (RegisterModel) response;
                    if(registerModel.success.equalsIgnoreCase("1")) {
//						Toast.makeText(getActivity(),registerModel.message, Toast.LENGTH_SHORT).show();
                    	phone = registerModel.phone;
                    	email = registerModel.email;
                    	email_text.setText(email);
                    	phone_text.setText(phone); 
                        } else if (registerModel.success.equalsIgnoreCase("0")) {
    						Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), registerModel.message);
                        }
                    }

                @Override
                public void onErrorReceived(String error) {
                    Toast.makeText(getActivity(), getString(R.string.try_again), Toast.LENGTH_SHORT).show();
                }
            });
        	getContackSupportRequestTask.execute();
        } else {
            Toast.makeText(getActivity(), getString(R.string.connection), Toast.LENGTH_SHORT).show();
        }
    }
	
	
	
	
}
