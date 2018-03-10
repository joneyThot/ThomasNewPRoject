package com.android.handyman.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.handyman.MainActivity;
import com.android.handyman.R;
import com.android.handyman.model.HireHandymanModel;
import com.android.handyman.service.AsyncCallListener;
import com.android.handyman.service.HireHandymanRequestTask;
import com.android.handyman.service.Utils;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class ConfirmDetailsFragment extends BaseFragment implements OnClickListener{
	
	private static String TAG = "ConfirmDetailsFragment";
	
	private SharedPreferences mSharedPreferences;
	
	Fragment fr;
	View mRootView;
	String handyman_id = "", handyman_name = "", handyman_email = "", job_description = "", date = "", time = "",
			person_name = "", number = "", address = "", street = "", landmark = "", city_id = "", city_name = "",
			state_id = "", state_name = "", pincode = "", requirment = "", category_id = "", sub_category_id = "";
	Dialog creditDialog;
	int hwidth,hHeight;
	String database_time = "";
	String FavouriteHandymanFragment = "";
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,	Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.fragment_hire_confirm_detail, container, false);
		initview();
		return mRootView;
	}

	private void initview() {
		mSharedPreferences = getActivity().getSharedPreferences(Utils.PREF_NAME, Context.MODE_PRIVATE); 
		
		if(getArguments() != null){
			FavouriteHandymanFragment = getArguments().getString("FavouriteHandymanFragment");
		}
		
		if(Utils.validateString(FavouriteHandymanFragment)){
			((MainActivity) getActivity()).setTitleText("", getString(R.string.hire_confirm), View.VISIBLE, View.GONE, View.GONE, View.GONE);
			getActivity().findViewById(R.id.title).setVisibility(View.GONE);
		} else {
			((MainActivity) getActivity()).setTitleText("", getString(R.string.hire_confirm), View.VISIBLE, View.GONE, View.GONE, View.VISIBLE);
			getActivity().findViewById(R.id.title).setVisibility(View.GONE);
		}
		
		mActivity.getSlidingMenu().setMode(SlidingMenu.LEFT);
		mActivity.getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		
		mRootView.findViewById(R.id.confirm_your_details_Button).setOnClickListener(this);
		
		handyman_id = mSharedPreferences.getString(Utils.HANDYMAN_ID, "");
		
		if(getArguments() != null){
			handyman_name = getArguments().getString(Utils.HANDYMAN_NAME);
			handyman_email = getArguments().getString(Utils.HANDYMAN_EMAIL);
			job_description = getArguments().getString(Utils.CONFIRM_JOB_DESCRIPTION);
			date = getArguments().getString(Utils.CONFIRM_DATE);
			time = getArguments().getString(Utils.CONFIRM_TIME);
			database_time  = getArguments().getString(Utils.CONFIRM_TIME_DATABASE);
			person_name = getArguments().getString(Utils.CONFIRM_PERSON_NAME);
			number = getArguments().getString(Utils.CONFIRM_NUMBER);
			address = getArguments().getString(Utils.CONFIRM_ADDRESS);
			street = getArguments().getString(Utils.CONFIRM_STREET);
			landmark = getArguments().getString(Utils.CONFIRM_LANDMARK);
			city_id = getArguments().getString(Utils.CONFIRM_CITY_ID);
			city_name = getArguments().getString(Utils.CONFIRM_CITY_NAME);
			state_id = getArguments().getString(Utils.CONFIRM_STATE_ID);
			state_name = getArguments().getString(Utils.CONFIRM_STATE_NAME);
			pincode = getArguments().getString(Utils.CONFIRM_PINCODE);
			requirment = getArguments().getString(Utils.CONFIRM_REQUIRMENT);
			category_id  = getArguments().getString(Utils.CONFIRM_CATEGORY_ID);
			sub_category_id = getArguments().getString(Utils.CONFIRM_SUB_CATEGORY_ID);
			
			((TextView) mRootView.findViewById(R.id.hire_handyman_name_text)).setText(handyman_name);
			((TextView) mRootView.findViewById(R.id.hire_handyman_email_text)).setText(handyman_email);
			((TextView) mRootView.findViewById(R.id.hire_confirm_job_description)).setText(job_description);
			((TextView) mRootView.findViewById(R.id.hire_confirm_date)).setText(date);
			((TextView) mRootView.findViewById(R.id.hire_confirm_time)).setText(time);
			((TextView) mRootView.findViewById(R.id.hire_confirm_contact_person)).setText(person_name);
			((TextView) mRootView.findViewById(R.id.hire_confirm_contact_no)).setText(number);
			String addr = address + "\n" + street + "\n" + landmark + "\n" + city_name + " - " + pincode;
			((TextView) mRootView.findViewById(R.id.hire_confirm_address)).setText(addr);
			((TextView) mRootView.findViewById(R.id.hire_confirm_contact_no)).setText(number);
			((TextView) mRootView.findViewById(R.id.hire_confirm_state)).setText(state_name);
			((TextView) mRootView.findViewById(R.id.hire_confirm_requirment)).setText(requirment);
		}
		

		Display display = getActivity().getWindowManager().getDefaultDisplay(); 

		hwidth = display.getWidth();  // deprecated
		hHeight = display.getHeight(); 
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.confirm_your_details_Button:
			openCraditDialog();
			break;
			
		case R.id.cradits_dialog_confirm_btn:
			 String format_date = date.substring(6, 10) + "-" + date.substring(3, 5) + "-" + date.substring(0, 2);
			 
			 onHireHandyman(handyman_id,mSharedPreferences.getString(Utils.USER_ID, ""),job_description,
					 format_date,database_time,person_name, number, requirment,"Pending",address,street,
					 landmark,city_id,pincode, state_id,  category_id,sub_category_id);
			break;
			
		case R.id.cradits_dialog_cancel_btn:
			creditDialog.dismiss();
			break;
		
		}
	}
	
	private void openCraditDialog()
	{
		creditDialog = new Dialog(getActivity());
		creditDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		creditDialog.setContentView(R.layout.credits_dialog);

		((TextView)creditDialog.findViewById(R.id.name_text)).setText("Name : " + person_name);
		((TextView)creditDialog.findViewById(R.id.birthdate_text)).setText("Date : " + date);
		creditDialog.findViewById(R.id.cradits_dialog_confirm_btn).setOnClickListener(this);
		creditDialog.findViewById(R.id.cradits_dialog_cancel_btn).setOnClickListener(this);
		creditDialog.getWindow().setLayout((hwidth/2)+300, (hHeight/3)+125);
		creditDialog.show();
	}
	
	private void onHireHandyman(String handyman_id, String client_id, String job_description,
			String appointment_date, String appointment_time, String contact_person, String contact_no,
			String comment, String hire_status, String address, String street, String landmark,
			String city, String pincode, String state, String category, String sub_category) {
		
        if (Utils.checkInternetConnection(getActivity())) {
            HireHandymanRequestTask hireHandymanRequestTask = new HireHandymanRequestTask(getActivity());
            hireHandymanRequestTask.setAsyncCallListener(new AsyncCallListener() {

                @Override
                public void onResponseReceived(Object response) {
                    HireHandymanModel hireHandymanModel = (HireHandymanModel) response;
                    if(hireHandymanModel.success.equalsIgnoreCase("1")) {
//                            Toast.makeText(getActivity(), hireHandymanModel.message , Toast.LENGTH_SHORT).show();
//                            onConfirmCredits(mSharedPreferences.getString(Utils.USER_ID, ""));
                            
//                            FragmentManager fm = getActivity().getSupportFragmentManager();
//                            for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {    
//                                fm.popBackStack();
//                            }
                          
//                            getActivity().getSupportFragmentManager().popBackStack();
                          getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                          getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new MyHiringsFragment_new()).commit();
                          creditDialog.dismiss();
                            
                        } else if (hireHandymanModel.success.equalsIgnoreCase("0")) {
//                            Toast.makeText(getActivity(), hireHandymanModel.message , Toast.LENGTH_SHORT).show();
                        	Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), hireHandymanModel.message);
                        }
                    }

                @Override
                public void onErrorReceived(String error) {
                    Toast.makeText(getActivity(), getString(R.string.try_again), Toast.LENGTH_SHORT).show();
                }
            });
            hireHandymanRequestTask.execute(handyman_id, client_id, job_description,
        			appointment_date, appointment_time, contact_person, contact_no, comment, 
        			hire_status, address, street, landmark, city, pincode, state, category, sub_category);
        } else {
            Toast.makeText(getActivity(), getString(R.string.connection), Toast.LENGTH_SHORT).show();
        }
    }
}
