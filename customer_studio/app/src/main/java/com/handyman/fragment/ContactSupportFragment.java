package com.handyman.fragment;

import android.content.Intent;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.handyman.MainActivity;
import com.handyman.R;
import com.handyman.logger.Logger;
import com.handyman.model.ContactsSupportModel;
import com.handyman.model.RegisterModel;
import com.handyman.service.AsyncCallListener;
import com.handyman.service.GetContackSupportRequestTask;
import com.handyman.service.Utils;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import java.util.ArrayList;

public class ContactSupportFragment extends BaseFragment implements OnClickListener{

	public static final String TAG = "ContactSupportFragment";
	
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
		((MainActivity) getActivity()).setTitleText(getString(R.string.menu_contact_support), "","", View.GONE, View.VISIBLE, View.GONE, View.GONE,View.GONE);
		getActivity().findViewById(R.id.title_back).setVisibility(View.GONE);
		getActivity().findViewById(R.id.txtUrl).setVisibility(View.GONE);
		
		mActivity.getSlidingMenu().setMode(SlidingMenu.LEFT);
		mActivity.getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		
		/*mRootView.findViewById(R.id.call_logo).setOnClickListener(this);

		email_text = (TextView) mRootView.findViewById(R.id.email_text);
		email_text.setOnClickListener(this);
		
		phone_text = (TextView) mRootView.findViewById(R.id.rate_handyman_no_text);
		phone_text.setOnClickListener(this);*/
		
		onContactSupport();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.linPhoneLayout:
			String tagPhone = (String) v.getTag();
			Uri number = Uri.parse("tel:"+tagPhone);
			Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
			startActivity(callIntent);
			break;


		case R.id.linEmailLayout:
			String tagEmail = (String) v.getTag();
			final Intent emailIntent = new Intent( android.content.Intent.ACTION_SEND);
			emailIntent.setType("plain/text");
			emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[] { tagEmail });
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
					ContactsSupportModel contactsSupportModel = (ContactsSupportModel) response;
					if (contactsSupportModel.success.equalsIgnoreCase("1")) {
						getPhoneNumber(contactsSupportModel.getPhone());
						getEmailAddress(contactsSupportModel.getEmail());

					} else if (contactsSupportModel.success.equalsIgnoreCase("0")) {
						Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), contactsSupportModel.message);
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

	public void getPhoneNumber(ArrayList<String> mPhoneNumberList){
		ArrayList<String> phoneList = new ArrayList<>();
		if(mPhoneNumberList != null && !mPhoneNumberList.isEmpty()) {
			phoneList.addAll(mPhoneNumberList);
			for (int i = 0; i < phoneList.size(); i++) {
				phone = phoneList.get(i).toString();
				LinearLayout linPhoneNumber = (LinearLayout) mRootView.findViewById(R.id.linPhoneNumber);
				View viewPhoneNumber  =  getActivity().getLayoutInflater().inflate(R.layout.fragment_contact_phone, null);
				LinearLayout linPhoneLayout = (LinearLayout) viewPhoneNumber.findViewById(R.id.linPhoneLayout);
				linPhoneLayout.setVisibility(View.VISIBLE);
				linPhoneLayout.setOnClickListener(this);
				linPhoneLayout.setTag(phone);
				TextView txtPhoneNumber = (TextView) viewPhoneNumber.findViewById(R.id.txtPhoneNumber);
				txtPhoneNumber.setText(phone);
				Logger.e(TAG,"Phone :: " + phone);
				linPhoneNumber.addView(viewPhoneNumber);
			}
		}

	}

	public void getEmailAddress(ArrayList<String> mEmailAddressList){
		ArrayList<String> emailList = new ArrayList<>();
		if(mEmailAddressList != null && !mEmailAddressList.isEmpty()) {
			emailList.addAll(mEmailAddressList);
			for (int i = 0; i < emailList.size(); i++) {
				email = emailList.get(i).toString();
				LinearLayout linEmail = (LinearLayout) mRootView.findViewById(R.id.linEmail);
				View viewEmailId  =  getActivity().getLayoutInflater().inflate(R.layout.fragment_contact_email, null);
				LinearLayout linEmailLayout = (LinearLayout) viewEmailId.findViewById(R.id.linEmailLayout);
				linEmailLayout.setVisibility(View.VISIBLE);
				linEmailLayout.setOnClickListener(this);
				linEmailLayout.setTag(email);
				TextView txtEmailAddress = (TextView) viewEmailId.findViewById(R.id.txtEmailAddress);
				txtEmailAddress.setText(email);
				Logger.e(TAG,"Email :: " + email);
				linEmail.addView(viewEmailId);
			}
		}
	}

}
