package com.android.handyman.fragment;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.handyman.MainActivity;
import com.android.handyman.R;
import com.android.handyman.adapter.CityAdapter;
import com.android.handyman.adapter.JobDescriptionAdapter;
import com.android.handyman.adapter.StateAdapter;
import com.android.handyman.model.CityModel;
import com.android.handyman.model.HireHandymanModel;
import com.android.handyman.model.JobdescriptionModel;
import com.android.handyman.model.StateModel;
import com.android.handyman.service.AsyncCallListener;
import com.android.handyman.service.CheckHireHandymanDateAndTimeRequestTask;
import com.android.handyman.service.GetCityTask;
import com.android.handyman.service.GetJobDescriptionListRequestTask;
import com.android.handyman.service.GetStateTask;
import com.android.handyman.service.Utils;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class HireHandymanFragment extends BaseFragment implements OnClickListener{
	
	private static String TAG = "HireHandymanFragment";
	private SharedPreferences mSharedPreferences;
	private ArrayList<StateModel> mStateModelList = new ArrayList<StateModel>();
	private ArrayList<CityModel> mCityModelList = new ArrayList<CityModel>();
	private ArrayList<JobdescriptionModel> jobdescriptionModelList = new ArrayList<JobdescriptionModel>();
	int hwidth,hHeight;
	
	StateAdapter stateAdapter;
	CityAdapter cityAdapter;
	JobDescriptionAdapter jobDescriptionAdapter;
	String FavouriteHandymanFragment = "";
	
	Fragment fr;
	View mRootView;
	EditText mJobDescription,mDate,mTime,mContactPerson,mContactNo,mAddress,mStreet,mLandmark ,mPincode,mRequirement;
	Spinner mStateSpinner, mCitySpinner, mJobDescriptionSpinner;
	String handyman_name , handyman_email, handyman_id, category_id = "", sub_category_id = "",job_description = "";
	Dialog creditDialog;
	String timeformat = "";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,	Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.fragment_hire_handyman, container, false);
		initview();
		getJobDescription(mSharedPreferences.getString(Utils.HANDYMAN_ID, ""));
		getState();
		return mRootView;
	}

	private void initview() {
		mSharedPreferences = getActivity().getSharedPreferences(Utils.PREF_NAME, Context.MODE_PRIVATE); 
		
		if(getArguments() != null){
			handyman_name = getArguments().getString(Utils.HANDYMAN_NAME);
			((TextView) mRootView.findViewById(R.id.hire_handyman_name_text)).setText(handyman_name);
			handyman_email = getArguments().getString(Utils.HANDYMAN_EMAIL);
			((TextView) mRootView.findViewById(R.id.hire_handyman_email_text)).setText(handyman_email);
			FavouriteHandymanFragment = getArguments().getString("FavouriteHandymanFragment");
//			handyman_id = getArguments().getString(Utils.HANDYMAN_ID);
//			Utils.storeString(mSharedPreferences, Utils.HANDYMAN_ID, handyman_id);
		}
		
		if(Utils.validateString(FavouriteHandymanFragment)){
			((MainActivity) getActivity()).setTitleText("", getString(R.string.hire_handyman), View.VISIBLE, View.GONE, View.GONE, View.GONE);
			getActivity().findViewById(R.id.title).setVisibility(View.GONE);
		} else {
			((MainActivity) getActivity()).setTitleText("", getString(R.string.hire_handyman), View.VISIBLE, View.GONE, View.GONE, View.VISIBLE);
			getActivity().findViewById(R.id.title).setVisibility(View.GONE);
		}
		
		mActivity.getSlidingMenu().setMode(SlidingMenu.LEFT);
		mActivity.getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		
		handyman_id = mSharedPreferences.getString(Utils.HANDYMAN_ID, "");
		
		mJobDescription = (EditText) mRootView.findViewById(R.id.hire_handyman_job_description);
		mJobDescription.setText(mSharedPreferences.getString(Utils.CATE_NAME, "") + " - " + mSharedPreferences.getString(Utils.SUB_CATE_NAME, ""));
		mJobDescriptionSpinner = (Spinner) mRootView.findViewById(R.id.hire_handyman_spinner_job_description);
		mDate = (EditText) mRootView.findViewById(R.id.hire_handyman_date);
		mDate.setOnClickListener(this);
		mTime = (EditText) mRootView.findViewById(R.id.hire_handyman_time);
		mTime.setOnClickListener(this);
		mContactPerson = (EditText) mRootView.findViewById(R.id.hire_handyman_contact_person);
		mContactNo = (EditText) mRootView.findViewById(R.id.hire_handyman_contact_no);
		mAddress = (EditText) mRootView.findViewById(R.id.hire_handyman_address);
		mStreet = (EditText) mRootView.findViewById(R.id.hire_handyman_stree_road);
		mLandmark = (EditText) mRootView.findViewById(R.id.hire_handyman_landmark);
		mStateSpinner = (Spinner) mRootView.findViewById(R.id.hire_handyman_spinner_state);
		mCitySpinner = (Spinner) mRootView.findViewById(R.id.hire_handyman_spinner_city);
		mPincode = (EditText) mRootView.findViewById(R.id.hire_handyman_pincode);
		mRequirement = (EditText) mRootView.findViewById(R.id.hire_handyman_requirements);
		
		mRootView.findViewById(R.id.confirm_your_details_Button).setOnClickListener(this);
		
		mRequirement.setOnEditorActionListener(new OnEditorActionListener(){
            @Override
            public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
                if (arg1 == EditorInfo.IME_ACTION_GO) {
                    mRootView.findViewById(R.id.confirm_your_details_Button).performClick();
                }
                return false;
            }
        });
		
		if(Utils.validateString(getArguments().getString("FavouriteHandymanFragment"))){
			mRootView.findViewById(R.id.job_description_framelayout).setVisibility(View.VISIBLE);
			mJobDescription.setVisibility(View.GONE);
//			category_id = ((JobdescriptionModel)mJobDescriptionSpinner.getSelectedItem()).category_id.toString();
//			subcategory_id = ((JobdescriptionModel)mJobDescriptionSpinner.getSelectedItem()).sub_category_id.toString(); 
		} else {
			mRootView.findViewById(R.id.job_description_framelayout).setVisibility(View.GONE);
			mJobDescription.setVisibility(View.VISIBLE);
//			category_id = mSharedPreferences.getString(Utils.CATEGORY_ID, "");
//			subcategory_id =  mSharedPreferences.getString(Utils.SUB_CATEGORY_ID, "");
		}
		
		mStateSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				if(position > 0){
					getCity(((StateModel)mStateSpinner.getSelectedItem()).id);
				}
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}
		});
		
		Display display = getActivity().getWindowManager().getDefaultDisplay(); 

		hwidth = display.getWidth();  // deprecated
		hHeight = display.getHeight(); 
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.confirm_your_details_Button:
			if(fieldValidation()){
				InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
				 imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
				 
//				 String b_date = mDate.getText().toString();
//				 String date = b_date.substring(6, 10) + "-" + b_date.substring(3, 5) + "-" + b_date.substring(0, 2);
				 
//				 String job_description = "";
//				 String category_id = "";
//				 String sub_category_id = "";
				 
				 if(Utils.validateString(getArguments().getString("FavouriteHandymanFragment"))){
					job_description =  ((JobdescriptionModel)mJobDescriptionSpinner.getSelectedItem()).category + " - " +
						((JobdescriptionModel)mJobDescriptionSpinner.getSelectedItem()).subcategory;
					category_id = ((JobdescriptionModel)mJobDescriptionSpinner.getSelectedItem()).category_id;
					sub_category_id = ((JobdescriptionModel)mJobDescriptionSpinner.getSelectedItem()).sub_category_id;
					
//					 onHireHandyman(handyman_id,mSharedPreferences.getString(Utils.USER_ID, ""), job_description,
//							 date,mTime.getText().toString(),mContactPerson.getText().toString(), mContactNo.getText().toString(),
//							mRequirement.getText().toString(),"Pending",mAddress.getText().toString(),mStreet.getText().toString(),
//							mLandmark.getText().toString(),((CityModel)mCitySpinner.getSelectedItem()).id,mPincode.getText().toString(),
//							((StateModel)mStateSpinner.getSelectedItem()).id, ((JobdescriptionModel)mJobDescriptionSpinner.getSelectedItem()).category_id,
//							((JobdescriptionModel)mJobDescriptionSpinner.getSelectedItem()).sub_category_id);
				 } else {
					 job_description = mJobDescription.getText().toString();
					 category_id = mSharedPreferences.getString(Utils.CATEGORY_ID, "");
					 sub_category_id = mSharedPreferences.getString(Utils.SUB_CATEGORY_ID, "");
//					 onHireHandyman(handyman_id,mSharedPreferences.getString(Utils.USER_ID, ""),mJobDescription.getText().toString(),
//							 date,mTime.getText().toString(),mContactPerson.getText().toString(), mContactNo.getText().toString(),
//								mRequirement.getText().toString(),"Pending",mAddress.getText().toString(),mStreet.getText().toString(),
//								mLandmark.getText().toString(),((CityModel)mCitySpinner.getSelectedItem()).id,mPincode.getText().toString(),
//								((StateModel)mStateSpinner.getSelectedItem()).id,  mSharedPreferences.getString(Utils.CATEGORY_ID, ""),mSharedPreferences.getString(Utils.SUB_CATEGORY_ID, ""));
				 }
				 
				 String d = mDate.getText().toString();
				 String format_date = d.substring(6, 10) + "-" + d.substring(3, 5) + "-" + d.substring(0, 2);
				 
				 onHireHandymanCheck(handyman_id,format_date,mTime.getText().toString());
				 
				
				
			}
			break;
			
		case R.id.hire_handyman_date:
			DialogFragment newDateFragment = new SelectDateFragment();
			newDateFragment.show(getFragmentManager(), "DatePicker");
			break;
			
		case R.id.hire_handyman_time:
//			 DialogFragment newTimeFragment = new TimePickerFragment();
//			 newTimeFragment.show(getFragmentManager(),"TimePicker");
			
			OnTimeSetListener callback1 = new OnTimeSetListener() {
				
				@Override
				public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
					String h = utilTime(hourOfDay);
			    	String m = utilTime(minute);
			        mTime.setText("" + h + ":" + m + ":" + "00");
					updateTime(hourOfDay,minute);
				}
			};
			
			int hour, minute;
			if(mTime.getText().toString().trim().equalsIgnoreCase("")){
				Calendar c = Calendar.getInstance();
			    hour = c.get(Calendar.HOUR_OF_DAY);
			    minute = c.get(Calendar.MINUTE);
			} else {
				String time = mTime.getText().toString().trim();
				hour = Integer.parseInt(time.substring(0, 2));
				minute = Integer.parseInt(time.substring(3, 5));
			}

			CustomTimePickerDialog customTimePickerDialog = new CustomTimePickerDialog(getActivity(), callback1, hour, minute, /*DateFormat.is24HourFormat(getActivity())*/ true);
			customTimePickerDialog.setTitle("TimePicker");
			customTimePickerDialog.show();
		
			break;
			
		
		}
	}
	
	private void updateTime(int hours, int mins) {
        
        String timeSet = "";
        if (hours > 12) {
            hours -= 12;
            timeSet = "PM";
        } else if (hours == 0) {
            hours += 12;
            timeSet = "AM";
        } else if (hours == 12)
            timeSet = "PM";
        else
            timeSet = "AM";
         
//        timeformat = hours + ":" + mins + ":" + "00";
        String hourses = utilTime(hours);
        String minutes = utilTime(mins);
       	timeformat = new StringBuilder().append(hourses).append(':').append(minutes).append(" ").append(timeSet).toString();
 
//        mTime.setText("" + hourses + ":" + minutes + ":" + "00");
    }
	
	private static String utilTime(int value) {
        
        if (value < 10)
            return "0" + String.valueOf(value);
        else
            return String.valueOf(value);
    }
	
	private void getJobDescription(String handyman_id) {
		if(Utils.checkInternetConnection(getActivity())){
			GetJobDescriptionListRequestTask jobDescriptionListRequestTask = new GetJobDescriptionListRequestTask(getActivity());
			jobDescriptionListRequestTask.setAsyncCallListener(new AsyncCallListener() {

				@SuppressWarnings("unchecked")
				@Override
				public void onResponseReceived(Object response) {
//					jobdescriptionModelList.add(new JobdescriptionModel("","","","Select Job"));
					jobdescriptionModelList = ((ArrayList<JobdescriptionModel>) response);
					if(jobdescriptionModelList != null){
						jobDescriptionAdapter = new JobDescriptionAdapter(getActivity(), jobdescriptionModelList);
						mJobDescriptionSpinner.setAdapter(jobDescriptionAdapter);
					}
				}
				@Override
				public void onErrorReceived(String error) {
					Toast.makeText(getActivity(), getString(R.string.try_again), Toast.LENGTH_SHORT).show();
				}
			});
			jobDescriptionListRequestTask.execute(handyman_id);
		}else{
			Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), 
					getResources().getString(R.string.connection));
		}
	}
	
	private void getState() {
		if(Utils.checkInternetConnection(getActivity())){
			GetStateTask getStateTask = new GetStateTask(getActivity());
			getStateTask.setAsyncCallListener(new AsyncCallListener() {

				@SuppressWarnings("unchecked")
				@Override
				public void onResponseReceived(Object response) {
					mStateModelList.clear();
					mStateModelList.add(new StateModel("", "State","","","","",""));
					mStateModelList.addAll((ArrayList<StateModel>) response);
					if(mStateModelList.size() > 0){
						stateAdapter = new StateAdapter(getActivity(), mStateModelList);
						mStateSpinner.setAdapter(stateAdapter);
						
						mCityModelList.clear();
						mCityModelList.add(new CityModel("","","City","","","","",""));
						cityAdapter = new CityAdapter(getActivity(), mCityModelList);
						mCitySpinner.setAdapter(cityAdapter);
					}
				}
				@Override
				public void onErrorReceived(String error) {
					Toast.makeText(getActivity(), getString(R.string.try_again), Toast.LENGTH_SHORT).show();
				}
			});
			getStateTask.execute();
		}else{
			Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), 
					getResources().getString(R.string.connection));
		}
	}
	
	private void getCity(String state) {
		if(Utils.checkInternetConnection(getActivity())){
			GetCityTask getCityTask = new GetCityTask(getActivity());
			getCityTask.setAsyncCallListener(new AsyncCallListener() {

				@SuppressWarnings("unchecked")
				@Override
				public void onResponseReceived(Object response) {
					mCityModelList.clear();
					mCityModelList.add(new CityModel("","","City","","","","",""));
					mCityModelList.addAll((ArrayList<CityModel>) response);
					
					if(mCityModelList.size() > 0){
						cityAdapter = new CityAdapter(getActivity(), mCityModelList);
						mCitySpinner.setAdapter(cityAdapter);
						cityAdapter.notifyDataSetChanged();
					} 
					
				}
				@Override
				public void onErrorReceived(String error) {
					Toast.makeText(getActivity(), getString(R.string.try_again), Toast.LENGTH_SHORT).show();
				}
			});
			getCityTask.execute(state);
		}else{
			Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), 
					getResources().getString(R.string.connection));
		}
	}
	
	private void onHireHandymanCheck(String handyman_id, String appointment_date, String appointment_time) {
		
        if (Utils.checkInternetConnection(getActivity())) {
        	CheckHireHandymanDateAndTimeRequestTask checkHireHandymanDateAndTimeRequestTask = new CheckHireHandymanDateAndTimeRequestTask(getActivity());
        	checkHireHandymanDateAndTimeRequestTask.setAsyncCallListener(new AsyncCallListener() {

                @Override
                public void onResponseReceived(Object response) {
                    HireHandymanModel hireHandymanModel = (HireHandymanModel) response;
                    if(hireHandymanModel.success.equalsIgnoreCase("1")) {
                    	
                    	 ConfirmDetailsFragment confirmDetailsFragment = new ConfirmDetailsFragment();
        				 Bundle bundle = new Bundle();
        				 bundle.putString("FavouriteHandymanFragment", FavouriteHandymanFragment);
        				 bundle.putString(Utils.HANDYMAN_NAME, handyman_name);
        				 bundle.putString(Utils.HANDYMAN_EMAIL, handyman_email);
        				 bundle.putString(Utils.CONFIRM_JOB_DESCRIPTION, job_description);
        				 bundle.putString(Utils.CONFIRM_DATE, mDate.getText().toString());
        				 bundle.putString(Utils.CONFIRM_TIME, timeformat);
        				 bundle.putString(Utils.CONFIRM_TIME_DATABASE, mTime.getText().toString());
        				 bundle.putString(Utils.CONFIRM_PERSON_NAME, mContactPerson.getText().toString());
        				 bundle.putString(Utils.CONFIRM_NUMBER, mContactNo.getText().toString());
        				 bundle.putString(Utils.CONFIRM_ADDRESS, mAddress.getText().toString());
        				 bundle.putString(Utils.CONFIRM_STREET, mStreet.getText().toString());
        				 bundle.putString(Utils.CONFIRM_LANDMARK, mLandmark.getText().toString());
        				 bundle.putString(Utils.CONFIRM_STATE_ID, ((StateModel)mStateSpinner.getSelectedItem()).id);
        				 bundle.putString(Utils.CONFIRM_STATE_NAME, ((StateModel)mStateSpinner.getSelectedItem()).name);
        				 bundle.putString(Utils.CONFIRM_CITY_ID, ((CityModel)mCitySpinner.getSelectedItem()).id);
        				 bundle.putString(Utils.CONFIRM_CITY_NAME, ((CityModel)mCitySpinner.getSelectedItem()).name);
        				 bundle.putString(Utils.CONFIRM_PINCODE, mPincode.getText().toString());
        				 bundle.putString(Utils.CONFIRM_REQUIRMENT, mRequirement.getText().toString());
        				 bundle.putString(Utils.CONFIRM_CATEGORY_ID, category_id);
        				 bundle.putString(Utils.CONFIRM_SUB_CATEGORY_ID, sub_category_id);
        				 
        				 confirmDetailsFragment.setArguments(bundle);
//        				 getActivity().getSupportFragmentManager().popBackStack("ServiceAtHomeFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        				 getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, confirmDetailsFragment).addToBackStack(null).commit();
                            
                        } else if (hireHandymanModel.success.equalsIgnoreCase("0")) {
//                            Toast.makeText(getActivity(), hireHandymanModel.message , Toast.LENGTH_SHORT).show();
                        	mTime.requestFocus();
                        	Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), hireHandymanModel.message);
                        }
                    }

                @Override
                public void onErrorReceived(String error) {
                    Toast.makeText(getActivity(), getString(R.string.try_again), Toast.LENGTH_SHORT).show();
                }
            });
        	checkHireHandymanDateAndTimeRequestTask.execute(handyman_id, appointment_date, appointment_time);
        } else {
            Toast.makeText(getActivity(), getString(R.string.connection), Toast.LENGTH_SHORT).show();
        }
    }

	
	public boolean fieldValidation() {
		boolean flag = true;
		if (!Utils.validateString(mJobDescription.getText().toString())) {
			flag = false;
			Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), getResources().getString(R.string.enter_job_description));
			mJobDescription.requestFocus();
		} else if (!Utils.validateString(mDate.getText().toString())) {
			flag = false;
			Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), getResources().getString(R.string.enter_handyman_date));
			mDate.requestFocus();
		} else if (!Utils.validateString(mTime.getText().toString())) {
			flag = false;
			Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), getResources().getString(R.string.enter_handyman_time));
			mTime.requestFocus();
		} else if (!Utils.validateString(mContactPerson.getText().toString())) {
			flag = false;
			Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), getResources().getString(R.string.enter_contact_person));
			mContactPerson.requestFocus();
		} else if (!Utils.validateString(mContactNo.getText().toString())) {
			flag = false;
			Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), getResources().getString(R.string.enter_contact_no));
			mContactNo.requestFocus();
		} else if(mContactNo.getText().toString().trim().length() < 10 ){
			flag = false;
			Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), getResources().getString(R.string.enter_contact_no_length));
			mContactNo.requestFocus();
		} else if (!Utils.validateString(mAddress.getText().toString())) {
			flag = false;
			Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), getResources().getString(R.string.enter_address));
			mAddress.requestFocus();
		} else if (!Utils.validateString(mStreet.getText().toString())) {
			flag = false;
			Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), getResources().getString(R.string.enter_street_road));
			mStreet.requestFocus();
		} else if (!Utils.validateString(mLandmark.getText().toString())) {
			flag = false;
			Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), getResources().getString(R.string.enter_landmark));
			mLandmark.requestFocus();
		} else if (mStateSpinner.getSelectedItemPosition() == 0) {
			flag = false;
			Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), getResources().getString(R.string.select_state));
			mStateSpinner.requestFocus();
		} else if (mCitySpinner.getSelectedItemPosition() == 0) {
			flag = false;
			Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), getResources().getString(R.string.select_city));
			mCitySpinner.requestFocus();
		}  else if (!Utils.validateString(mPincode.getText().toString())) {
			flag = false;
			Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), getResources().getString(R.string.enter_pincode));
			mPincode.requestFocus();
		} else if (mPincode.getText().toString().trim().length() < 6) {
			flag = false;
			Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), getResources().getString(R.string.check_pincode_length));
		} else if (!Utils.validateString(mRequirement.getText().toString())) {
			flag = false;
			Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), getResources().getString(R.string.enter_requirements));
			mRequirement.requestFocus();
		} 
		return flag;
	}
	
	public class SelectDateFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			final Calendar c = Calendar.getInstance();
		    c.add(Calendar.DAY_OF_MONTH, + 0);
		    
		    int year = 0,month = 0,day = 0;
		    if(mDate.getText().toString().trim().equalsIgnoreCase("")){
		    	 year = c.get(Calendar.YEAR);
				 month = c.get(Calendar.MONTH);
				 day = c.get(Calendar.DAY_OF_MONTH);
		    } else {
		    	String d = mDate.getText().toString();
		    	year = Integer.parseInt(d.substring(6, 10)); 
		    	month = Integer.parseInt(d.substring(3, 5)) - 1; 
		    	day = Integer.parseInt(d.substring(0, 2));
		    }
		   
//			long minDate = c.getTime().getTime();
			
			DatePickerDialog dpd = new DatePickerDialog(getActivity(), this,year,month,day);
	        dpd.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            
	        return  dpd;
		}

		public void onDateSet(DatePicker view, int yy, int mm, int dd) {
			String day = String.valueOf(dd);
			String month = String.valueOf(mm + 1);
			if(day.trim().length() == 1 )
				day = "0" + day;
			if(month.trim().length() == 1)
				month = "0"+ month;
			mDate.setText(new StringBuilder().append(day).append("/").append(month).append("/").append(yy).append(" "));
		}

	}
	
	/*public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener{
	    @Override
	    public Dialog onCreateDialog(Bundle savedInstanceState){
	    	
	    	
	        final Calendar c = Calendar.getInstance();
	        int hour = c.get(Calendar.HOUR_OF_DAY);
	        int minute = c.get(Calendar.MINUTE);

	        return new TimePickerDialog(getActivity(),this, hour, minute , DateFormat.is24HourFormat(getActivity()));
	    }

	    public void onTimeSet(TimePicker view, int hourOfDay, int minute){
	    	int seconds = 0;
	    	if(hourOfDay != 0 && minute != 0){
	    		int time = (int)(((hourOfDay * 3600)/60)/minute);
	    		seconds = time % 60;
	    	}
	    	String h = String.valueOf(hourOfDay);
	    	String m = String.valueOf(minute);
	    	String s = String.valueOf(seconds);
	    	if(h.trim().length() == 1)
	    		h = "0" + h;
	    	if(m.trim().length() == 1)
	    		m = "0" + m;
	    	if(s.trim().length() == 1)
	    		s = "0" + s;
	        mTime.setText("" + h + ":" + m + ":" + s);
	    }
	}*/
	
	
	public class CustomTimePickerDialog extends TimePickerDialog {

	    private final static int TIME_PICKER_INTERVAL = 15;
	    private TimePicker timePicker;
	    private final OnTimeSetListener callback;

	    public CustomTimePickerDialog(Context context, OnTimeSetListener callBack,
	            int hourOfDay, int minute, boolean is24HourView) {
	        super(context, TimePickerDialog.THEME_HOLO_LIGHT, callBack, hourOfDay, minute / TIME_PICKER_INTERVAL,
	                is24HourView);
	        this.callback = callBack;
	    }

	    @Override
	    public void onClick(DialogInterface dialog, int which) {
	        if (callback != null && timePicker != null) {
	            timePicker.clearFocus();
	            callback.onTimeSet(timePicker, timePicker.getCurrentHour(), timePicker.getCurrentMinute() * TIME_PICKER_INTERVAL);
	            
	        }
	    }
	    
	    
		@Override
	    protected void onStop() {
	    }

	    @Override
	    public void onAttachedToWindow() {
	        super.onAttachedToWindow();
	        try {
	            Class<?> classForid = Class.forName("com.android.internal.R$id");
	            Field timePickerField = classForid.getField("timePicker");
	            this.timePicker = (TimePicker) findViewById(timePickerField.getInt(null));
	            Field field = classForid.getField("minute");

	            NumberPicker mMinuteSpinner = (NumberPicker) timePicker.findViewById(field.getInt(null));
	            mMinuteSpinner.setMinValue(0);
	            mMinuteSpinner.setMaxValue((60 / TIME_PICKER_INTERVAL) - 1);
	            List<String> displayedValues = new ArrayList<String>();
	            for (int i = 0; i < 60; i += TIME_PICKER_INTERVAL) {
	                displayedValues.add(String.format("%02d", i));
	            }
	            mMinuteSpinner.setDisplayedValues(displayedValues.toArray(new String[0]));
	            
	        } catch (Exception e) {
	            e.printStackTrace();
	        }

	    }
	    
	}
	
}
