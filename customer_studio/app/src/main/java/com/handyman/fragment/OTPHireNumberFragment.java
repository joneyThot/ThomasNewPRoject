package com.handyman.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.handyman.MainActivity;
import com.handyman.R;
import com.handyman.model.RegisterModel;
import com.handyman.service.AsyncCallListener;
import com.handyman.service.OTPHireRequestTask;
import com.handyman.service.Utils;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class OTPHireNumberFragment extends BaseFragment implements OnClickListener {

    private static String TAG = "OTPHireNumberFragment";

    private SharedPreferences mSharedPreferences;

    Fragment fr;
    View mRootView;
    EditText mOTPNo;

    String handyman_id = "", handyman_name = "", handyman_email = "", job_description = "", date = "", time = "",
            person_name = "", number = "", address = "", database_time = "", requirment = "", category_id = "", sub_category_id = "", floor_no = "",
            apartment = "", latitude = "0.0", longitude = "0.0", FavouriteHandymanFragment = "";

    String hireOTP = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mRootView = inflater.inflate(R.layout.activity_otp_password, container, false);

        initview();
        return mRootView;
    }

    private void initview() {
        mSharedPreferences = getActivity().getSharedPreferences(Utils.PREF_NAME, Context.MODE_PRIVATE);
        mRootView.findViewById(R.id.titleLayout).setVisibility(View.GONE);

        if (getArguments() != null) {
            FavouriteHandymanFragment = getArguments().getString("FavouriteHandymanFragment");
        }

        if (Utils.validateString(FavouriteHandymanFragment)) {
            ((MainActivity) getActivity()).setTitleText("", getString(R.string.otp), "", View.VISIBLE, View.GONE, View.GONE, View.GONE, View.GONE);
            getActivity().findViewById(R.id.title).setVisibility(View.GONE);
            getActivity().findViewById(R.id.txtUrl).setVisibility(View.GONE);
        } else {
            ((MainActivity) getActivity()).setTitleText("", getString(R.string.otp),  "", View.VISIBLE, View.GONE, View.GONE, View.VISIBLE,View.GONE);
            getActivity().findViewById(R.id.title).setVisibility(View.GONE);
            getActivity().findViewById(R.id.txtUrl).setVisibility(View.GONE);
        }

//        ((MainActivity) getActivity()).setTitleText(getString(R.string.otp), "", View.GONE, View.VISIBLE, View.GONE, View.GONE);
//        getActivity().findViewById(R.id.title_back).setVisibility(View.GONE);

        mActivity.getSlidingMenu().setMode(SlidingMenu.LEFT);
        mActivity.getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);

        mOTPNo = (EditText) mRootView.findViewById(R.id.otp_edittxt);
        mRootView.findViewById(R.id.otpSubmitButton).setOnClickListener(this);
        ((TextView) mRootView.findViewById(R.id.otpSubmitButton)).setText("OTP Confirm");

        if (getArguments() != null) {
            handyman_name = getArguments().getString(Utils.HANDYMAN_NAME);
            handyman_email = getArguments().getString(Utils.HANDYMAN_EMAIL);
            job_description = getArguments().getString(Utils.CONFIRM_JOB_DESCRIPTION);
            date = getArguments().getString(Utils.CONFIRM_DATE);
            time = getArguments().getString(Utils.CONFIRM_TIME);
            database_time = getArguments().getString(Utils.CONFIRM_TIME_DATABASE);
            person_name = getArguments().getString(Utils.CONFIRM_PERSON_NAME);
            number = getArguments().getString(Utils.CONFIRM_NUMBER);
            address = getArguments().getString(Utils.CONFIRM_ADDRESS);
            floor_no = getArguments().getString(Utils.CONFIRM_FLOOR);
            apartment = getArguments().getString(Utils.CONFIRM_APARTMENT);
//			street = getArguments().getString(Utils.CONFIRM_STREET);
//			landmark = getArguments().getString(Utils.CONFIRM_LANDMARK);
//			city_id = getArguments().getString(Utils.CONFIRM_CITY_ID);
//			city_name = getArguments().getString(Utils.CONFIRM_CITY_NAME);
//			state_id = getArguments().getString(Utils.CONFIRM_STATE_ID);
//			state_name = getArguments().getString(Utils.CONFIRM_STATE_NAME);
//			pincode = getArguments().getString(Utils.CONFIRM_PINCODE);
            requirment = getArguments().getString(Utils.CONFIRM_REQUIRMENT);
            category_id = getArguments().getString(Utils.CONFIRM_CATEGORY_ID);
            sub_category_id = getArguments().getString(Utils.CONFIRM_SUB_CATEGORY_ID);
            latitude = getArguments().getString(Utils.CONFIRM_LATITUDE);
            longitude = getArguments().getString(Utils.CONFIRM_LONGITUDE);
            hireOTP = getArguments().getString(Utils.OTP_CODE);
            if (Utils.validateString(hireOTP)) {
                mOTPNo.setText(hireOTP);
//                Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), "OTP Code is : " + hireOTP);
            }

        }

        mOTPNo.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
                if (arg1 == EditorInfo.IME_ACTION_GO) {
                    mRootView.findViewById(R.id.otpSubmitButton).performClick();
                }
                return false;
            }
        });

    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.otpSubmitButton:
                if (fieldValidation()) {
                    if (mSharedPreferences.getString(Utils.OTP_CODE, "").equalsIgnoreCase(mOTPNo.getText().toString().trim())) {
                        onOTPPassword(number, mOTPNo.getText().toString().trim());
                    } else {
                        Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), "Otp not matched.");
                    }
                    ((MainActivity) getActivity()).hideSoftKeyboard();
                }
        }
    }

    public boolean fieldValidation() {
        boolean flag = true;
        if (!Utils.validateString(mOTPNo.getText().toString())) {
            flag = false;
            Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), getResources().getString(R.string.enter_mobile_no));
        } else if (mOTPNo.getText().toString().trim().length() < 4) {
            flag = false;
            Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), "OTP Length minimum 4.");
        }
        return flag;
    }

    private void onOTPPassword(String mobile, String otp) {

        if (Utils.checkInternetConnection(getActivity())) {
            OTPHireRequestTask otpHireRequestTask = new OTPHireRequestTask(getActivity());
            otpHireRequestTask.setAsyncCallListener(new AsyncCallListener() {

                @Override
                public void onResponseReceived(Object response) {
                    RegisterModel registerModel = (RegisterModel) response;
                    if (registerModel.success.equalsIgnoreCase("1")) {

                        ConfirmDetailsFragment confirmDetailsFragment = new ConfirmDetailsFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("FavouriteHandymanFragment", FavouriteHandymanFragment);
                        bundle.putString(Utils.HANDYMAN_NAME, handyman_name);
                        bundle.putString(Utils.HANDYMAN_EMAIL, handyman_email);
                        bundle.putString(Utils.CONFIRM_JOB_DESCRIPTION, job_description);
                        bundle.putString(Utils.CONFIRM_DATE, date);
                        bundle.putString(Utils.CONFIRM_TIME, time);
                        bundle.putString(Utils.CONFIRM_TIME_DATABASE, database_time);
                        bundle.putString(Utils.CONFIRM_PERSON_NAME, person_name);
                        bundle.putString(Utils.CONFIRM_NUMBER, number);
                        bundle.putString(Utils.CONFIRM_ADDRESS, address);
                        bundle.putString(Utils.CONFIRM_FLOOR, floor_no);
                        bundle.putString(Utils.CONFIRM_APARTMENT, apartment);
                        bundle.putString(Utils.CONFIRM_REQUIRMENT, requirment);
                        bundle.putString(Utils.CONFIRM_CATEGORY_ID, category_id);
                        bundle.putString(Utils.CONFIRM_SUB_CATEGORY_ID, sub_category_id);
                        bundle.putString(Utils.CONFIRM_LATITUDE, latitude);
                        bundle.putString(Utils.CONFIRM_LONGITUDE, longitude);

                        confirmDetailsFragment.setArguments(bundle);

                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, confirmDetailsFragment).addToBackStack(null).commit();

                        Utils.storeString(mSharedPreferences, Utils.OTP_CODE, "");
                        mOTPNo.setText("");


                    } else if (registerModel.success.equalsIgnoreCase("0")) {
                        Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), registerModel.message);
                    }
                }

                @Override
                public void onErrorReceived(String error) {
                    Toast.makeText(getActivity(), getString(R.string.try_again), Toast.LENGTH_SHORT).show();
                }
            });
            otpHireRequestTask.execute(mobile, otp);
        } else {
            Toast.makeText(getActivity(), getString(R.string.connection), Toast.LENGTH_SHORT).show();
        }
    }

}
