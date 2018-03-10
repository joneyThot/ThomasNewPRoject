package com.handyman.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.handyman.MainActivity;
import com.handyman.R;
import com.handyman.SmsListener;
import com.handyman.SmsReceiver;
import com.handyman.SplashActivity;
import com.handyman.crop.Util;
import com.handyman.logger.Logger;
import com.handyman.model.AdvertiseListModel;
import com.handyman.model.HireHandymanModel;
import com.handyman.model.RegisterModel;
import com.handyman.service.AsyncCallListener;
import com.handyman.service.CheckHandymanDateAndTimeRequestTask;
import com.handyman.service.CheckHireHandymanDateAndTimeRequestTask;
import com.handyman.service.GetCoinsRequestTask;
import com.handyman.service.GetKilometersRequestTask;
import com.handyman.service.HireHandymanRequestTask;
import com.handyman.service.HireOTPRequestTask;
import com.handyman.service.HireResendOTPRequestTask;
import com.handyman.service.OTPHireRequestTask;
import com.handyman.service.Utils;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class ConfirmDetailsFragment extends BaseFragment implements OnClickListener {

    private static String TAG = "ConfirmDetailsFragment";

    private SharedPreferences mSharedPreferences;

    Fragment fr;
    View mRootView;
    String handyman_id = "", handyman_name = "", handyman_email = "", job_description = "", date = "", time = "",
            person_name = "", person_number = "", address = "", landmark = "", city_id = "", city_name = "",
            state_id = "", state_name = "", pincode = "", requirment = "", category_id = "", sub_category_id = "", floor_no = "",
            apartment = "", latitude = "0.0", longitude = "0.0", order_id = ""/*, hireOTP = ""*/;
    Dialog creditDialog, otpDialog, otpNumberDialog;
    EditText mOtpNumber;
    int hwidth, hHeight;
    String database_time = "", number = "";
    String FavouriteHandymanFragment = "";
    public String coins_value = "";
    TextView txtResendOtp;
    private ProgressDialog mProgressDialog;
    Handler mHandler = new Handler();
    Runnable mRunnable;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_hire_confirm_detail, container, false);
        initview();

        SmsReceiver.bindListener(new SmsListener() {
            @Override
            public void messageReceived(String messageText) {
                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
                if (otpNumberDialog != null && otpNumberDialog.isShowing()) {
                    otpNumberDialog.dismiss();
                }
                mHandler.removeCallbacks(mRunnable);
                Logger.d("Text", messageText);
                String text = messageText.replaceAll("[^0-9]", "");
                number = text.substring(0, 4);
                openOTPNumberDialog(number);
            }
        });
        return mRootView;
    }

    private void initview() {
        mSharedPreferences = getActivity().getSharedPreferences(Utils.PREF_NAME, Context.MODE_PRIVATE);

        if (getArguments() != null) {
            FavouriteHandymanFragment = getArguments().getString("FavouriteHandymanFragment");
        }

        if (Utils.validateString(FavouriteHandymanFragment)) {
            ((MainActivity) getActivity()).setTitleText("", getString(R.string.hire_confirm), "", View.VISIBLE, View.GONE, View.GONE, View.GONE, View.GONE);
            getActivity().findViewById(R.id.title).setVisibility(View.GONE);
            getActivity().findViewById(R.id.txtUrl).setVisibility(View.GONE);
        } else {
            ((MainActivity) getActivity()).setTitleText("", getString(R.string.hire_confirm), "", View.VISIBLE, View.GONE, View.GONE, View.VISIBLE, View.GONE);
            getActivity().findViewById(R.id.title).setVisibility(View.GONE);
            getActivity().findViewById(R.id.txtUrl).setVisibility(View.GONE);
        }

        mActivity.getSlidingMenu().setMode(SlidingMenu.LEFT);
        mActivity.getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);

        getCoins();

        mRootView.findViewById(R.id.confirm_your_details_Button).setOnClickListener(this);

        handyman_id = mSharedPreferences.getString(Utils.HANDYMAN_ID, "");
        Utils.storeString(mSharedPreferences, Utils.ORDER_ID, "");

        if (getArguments() != null) {
            handyman_name = getArguments().getString(Utils.HANDYMAN_NAME);
            handyman_email = getArguments().getString(Utils.HANDYMAN_EMAIL);
            job_description = getArguments().getString(Utils.CONFIRM_JOB_DESCRIPTION);
            date = getArguments().getString(Utils.CONFIRM_DATE);
            time = getArguments().getString(Utils.CONFIRM_TIME);
            database_time = getArguments().getString(Utils.CONFIRM_TIME_DATABASE);
            person_name = getArguments().getString(Utils.CONFIRM_PERSON_NAME);
            person_number = getArguments().getString(Utils.CONFIRM_NUMBER);
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
//            hireOTP = getArguments().getString(Utils.OTP_CODE);

            ((TextView) mRootView.findViewById(R.id.hire_handyman_name_text)).setText(handyman_name);
            ((TextView) mRootView.findViewById(R.id.hire_handyman_email_text)).setText(handyman_email);
            ((TextView) mRootView.findViewById(R.id.hire_confirm_job_description)).setText(job_description);
            ((TextView) mRootView.findViewById(R.id.hire_confirm_date)).setText(date);
            ((TextView) mRootView.findViewById(R.id.hire_confirm_time)).setText(time);
            ((TextView) mRootView.findViewById(R.id.hire_confirm_contact_person)).setText(person_name);
            ((TextView) mRootView.findViewById(R.id.hire_confirm_contact_no)).setText(person_number);
            String floor = "", apartment_name = "";
            if (Utils.validateString(floor_no)) {
                floor = floor_no + ", ";
            }

            if (Utils.validateString(apartment)) {
                apartment_name = apartment + ", ";
            }
            String addr = floor + apartment_name + address /*+ "\n" + street + "\n" + landmark + "\n" + city_name + " - " + pincode*/;
            ((TextView) mRootView.findViewById(R.id.hire_confirm_address)).setText(addr);
            ((TextView) mRootView.findViewById(R.id.hire_confirm_contact_no)).setText(person_number);
//			((TextView) mRootView.findViewById(R.id.hire_confirm_state)).setText(state_name);
            ((TextView) mRootView.findViewById(R.id.hire_confirm_requirment)).setText(requirment);
        }

        otpDialog = new Dialog(getActivity());
        creditDialog = new Dialog(getActivity());

        Display display = getActivity().getWindowManager().getDefaultDisplay();

        hwidth = display.getWidth();  // deprecated
        hHeight = display.getHeight();

    }

    private void doTheAutoRefresh() {
        mRunnable = new Runnable() {

            @Override
            public void run() {
                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
                mHandler.removeCallbacks(mRunnable);
            }
        };
        mHandler.postDelayed(mRunnable, 15000);

    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.confirm_your_details_Button:

                if (!otpDialog.isShowing()) {
                    onOTPPassword(mSharedPreferences.getString(Utils.USER_ID, ""), person_number, mSharedPreferences.getString(Utils.ORDER_ID, ""));

                }
                break;

            case R.id.cradits_dialog_confirm_btn:
                String format_date = date.substring(6, 10) + "-" + date.substring(3, 5) + "-" + date.substring(0, 2);
                onHireHandymanCheck(handyman_id, format_date, database_time);

                break;

            case R.id.cradits_dialog_cancel_btn:
                creditDialog.dismiss();
                break;

            case R.id.otpSubmitButton:
                if (fieldValidation()) {
                    onOTPCheck(mOtpNumber.getText().toString().trim(), mSharedPreferences.getString(Utils.ORDER_ID, ""));
                }
                break;

            case R.id.txtResendOtp:
                ((MainActivity) getActivity()).hideSoftKeyboard();
                onOTPResend(person_number, order_id);
                break;

            case R.id.txtApprove:

                mOtpNumber.setText("" + number);
                mOtpNumber.setSelection(mOtpNumber.getText().length());
                otpNumberDialog.dismiss();
//                otpDialog.findViewById(R.id.otpSubmitButton).performClick();

                break;

        }
    }

    private void openCraditDialog() {
        creditDialog = new Dialog(getActivity());
        creditDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        creditDialog.setCanceledOnTouchOutside(false);
        creditDialog.setContentView(R.layout.credits_dialog);

        ((TextView) creditDialog.findViewById(R.id.name_text)).setText("Name : " + person_name);
        ((TextView) creditDialog.findViewById(R.id.birthdate_text)).setText("Date : " + date);
        ((TextView) creditDialog.findViewById(R.id.txtCoinsValue)).setText("* You will use " + coins_value + " coins.");
        creditDialog.findViewById(R.id.cradits_dialog_confirm_btn).setOnClickListener(this);
        creditDialog.findViewById(R.id.cradits_dialog_cancel_btn).setOnClickListener(this);
//		creditDialog.getWindow().setLayout((hwidth/2)+300, (hHeight/3)+130);
        creditDialog.show();
    }

    private void openOTPDialog(String orderId) {
        otpDialog = new Dialog(getActivity());
        otpDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        otpDialog.setCanceledOnTouchOutside(false);
        otpDialog.setContentView(R.layout.activity_otp_password);

        otpDialog.findViewById(R.id.title).setVisibility(View.VISIBLE);
        ((TextView) otpDialog.findViewById(R.id.title)).setText(getString(R.string.otp_dialog_text));

        mOtpNumber = (EditText) otpDialog.findViewById(R.id.otp_edittxt);
//        if (Utils.validateString(hireOTP)) {
//            mOtpNumber.setText(hireOTP);
//            mOtpNumber.setSelection(mOtpNumber.getText().length());
//        }

        otpDialog.findViewById(R.id.otpSubmitButton).setOnClickListener(this);
        ((TextView) otpDialog.findViewById(R.id.otpSubmitButton)).setText("CONFIRM");
        txtResendOtp = (TextView) otpDialog.findViewById(R.id.txtResendOtp);
        txtResendOtp.setVisibility(View.VISIBLE);
        txtResendOtp.setOnClickListener(this);
        otpDialog.show();

        if (!Utils.validateString(orderId)) {
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setMessage(getResources().getString(R.string.otp_msg));
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.setCancelable(false);
            if (mProgressDialog != null && !mProgressDialog.isShowing()) {
                mProgressDialog.show();
            }
            doTheAutoRefresh();
        } else {
            Utils.showMessageDialog(getActivity(), "OTP", "Please enter Recent OTP or press Resend OTP option to get new one.");
        }
    }


    private void openOTPNumberDialog(String otp) {
        otpNumberDialog = new Dialog(getActivity());
        otpNumberDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        otpNumberDialog.setContentView(R.layout.otp_approve_dialog);

        ((TextView) otpNumberDialog.findViewById(R.id.txtOtp)).setText(otp);
        otpNumberDialog.findViewById(R.id.txtApprove).setOnClickListener(this);

        otpNumberDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        otpNumberDialog.getWindow().setGravity(Gravity.BOTTOM);
        otpNumberDialog.show();
    }

    private void onHireHandymanCheck(String h_id, String appointment_date, String appointment_time) {
        try {
            if (Utils.checkInternetConnection(getActivity())) {
                CheckHandymanDateAndTimeRequestTask checkHandymanDateAndTimeRequestTask = new CheckHandymanDateAndTimeRequestTask(getActivity());
                checkHandymanDateAndTimeRequestTask.setAsyncCallListener(new AsyncCallListener() {

                    @Override
                    public void onResponseReceived(Object response) {
                        HireHandymanModel hireHandymanModel = (HireHandymanModel) response;
                        if (hireHandymanModel.success.equalsIgnoreCase("1")) {
                            String format_date = date.substring(6, 10) + "-" + date.substring(3, 5) + "-" + date.substring(0, 2);

                            onHireHandyman(handyman_id, mSharedPreferences.getString(Utils.USER_ID, ""), job_description,
                                    format_date, database_time, person_name, person_number, requirment, "Pending", address, floor_no, apartment,
                                    category_id, sub_category_id, latitude, longitude);

                        } else if (hireHandymanModel.success.equalsIgnoreCase("0")) {
                            creditDialog.dismiss();
                            Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), hireHandymanModel.message);
                        }
                    }

                    @Override
                    public void onErrorReceived(String error) {
                        Toast.makeText(getActivity(), getString(R.string.try_again), Toast.LENGTH_SHORT).show();
                    }
                });
                checkHandymanDateAndTimeRequestTask.execute(h_id, appointment_date, appointment_time);
            } else {
                Toast.makeText(getActivity(), getString(R.string.connection), Toast.LENGTH_SHORT).show();
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void onHireHandyman(String handyman_id, String client_id, String job_description,
                                String appointment_date, String appointment_time, String contact_person, String contact_no,
                                String comment, String hire_status, String address, String floor, String apartment,
                                String category, String sub_category, String latitude, String longitude) {

        try {
            if (Utils.checkInternetConnection(getActivity())) {
                HireHandymanRequestTask hireHandymanRequestTask = new HireHandymanRequestTask(getActivity());
                hireHandymanRequestTask.setAsyncCallListener(new AsyncCallListener() {

                    @Override
                    public void onResponseReceived(Object response) {
                        HireHandymanModel hireHandymanModel = (HireHandymanModel) response;
                        if (hireHandymanModel.success.equalsIgnoreCase("1")) {
//                            Toast.makeText(getActivity(), hireHandymanModel.message , Toast.LENGTH_SHORT).show();
//                            onConfirmCredits(mSharedPreferences.getString(Utils.USER_ID, ""));

//                            FragmentManager fm = getActivity().getSupportFragmentManager();
//                            for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {
//                                fm.popBackStack();
//                            }

//                            getActivity().getSupportFragmentManager().popBackStack();

                            Utils.storeString(mSharedPreferences, Utils.currentAddress, "");
                            Utils.storeString(mSharedPreferences, Utils.latitude, "");
                            Utils.storeString(mSharedPreferences, Utils.longitude, "");

                            getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                            SlidingMenuFragment.selectMenu(2);
                            MainActivity.doubleBackToExitPressedOnce = false;
                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new MyHiringsFragment()).commit();
                            creditDialog.dismiss();

                        } else if (hireHandymanModel.success.equalsIgnoreCase("0")) {
//                            Toast.makeText(getActivity(), hireHandymanModel.message , Toast.LENGTH_SHORT).show();
                            creditDialog.dismiss();
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
                        hire_status, address, floor, apartment, category, sub_category, latitude, longitude);
            } else {
                Toast.makeText(getActivity(), getString(R.string.connection), Toast.LENGTH_SHORT).show();
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }

    private void getCoins() {

        try {
            if (Utils.checkInternetConnection(getActivity())) {
                GetCoinsRequestTask getCoinsRequestTask = new GetCoinsRequestTask(getActivity());
                getCoinsRequestTask.setAsyncCallListener(new AsyncCallListener() {

                    @SuppressWarnings("unchecked")
                    @Override
                    public void onResponseReceived(Object response) {
                        AdvertiseListModel coinsModel = (AdvertiseListModel) response;
                        if (coinsModel.getSuccess().equalsIgnoreCase("1")) {

                            if (Utils.validateString(coinsModel.getData())) {
                                coins_value = coinsModel.getData();
                            }

                        } else if (coinsModel.getSuccess().equalsIgnoreCase("0")) {
                            Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), coinsModel.getMessage());
                        }

                    }

                    @Override
                    public void onErrorReceived(String error) {
                        Toast.makeText(getActivity(), getString(R.string.try_again), Toast.LENGTH_SHORT).show();
                    }
                });
                getCoinsRequestTask.execute();
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }

    public boolean fieldValidation() {
        boolean flag = true;
        if (!Utils.validateString(mOtpNumber.getText().toString())) {
            flag = false;
            Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), getResources().getString(R.string.enter_otp));
        } else if (mOtpNumber.getText().toString().trim().length() < 4) {
            flag = false;
            Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), "OTP Length minimum 4.");
        }
        return flag;
    }

    private void onOTPCheck(String otp, String orderId) {
        try {
            if (Utils.checkInternetConnection(getActivity())) {
                OTPHireRequestTask otpHireRequestTask = new OTPHireRequestTask(getActivity());
                otpHireRequestTask.setAsyncCallListener(new AsyncCallListener() {

                    @Override
                    public void onResponseReceived(Object response) {
                        RegisterModel registerModel = (RegisterModel) response;
                        if (registerModel.success.equalsIgnoreCase("1")) {

                            otpDialog.dismiss();
                            openCraditDialog();
                            mOtpNumber.setText("");


                        } else if (registerModel.success.equalsIgnoreCase("0")) {
                            Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), registerModel.message);
                        }

                        ((MainActivity) getActivity()).hideSoftKeyboard();
//                    otpDialog.dismiss();
                    }

                    @Override
                    public void onErrorReceived(String error) {
                        Toast.makeText(getActivity(), getString(R.string.try_again), Toast.LENGTH_SHORT).show();
                    }
                });
                otpHireRequestTask.execute(otp, orderId);
            } else {
                Toast.makeText(getActivity(), getString(R.string.connection), Toast.LENGTH_SHORT).show();
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }

    private void onOTPPassword(String user_id, String mobile, final String orderId) {

        try {
            if (Utils.checkInternetConnection(getActivity())) {
                HireOTPRequestTask hireOTPRequestTask = new HireOTPRequestTask(getActivity());
                hireOTPRequestTask.setAsyncCallListener(new AsyncCallListener() {

                    @Override
                    public void onResponseReceived(Object response) {
                        RegisterModel registerModel = (RegisterModel) response;
                        if (registerModel.success.equalsIgnoreCase("1")) {

                            openOTPDialog(orderId);

                       /* if (Utils.validateString(registerModel.user.getOtp())) {
//                            otpDialog.dismiss();
//                            hireOTP = registerModel.getHireotp();
                            mOtpNumber.setText(registerModel.user.getOtp());
                            mOtpNumber.setSelection(mOtpNumber.getText().length());
//                            openOTPDialog();
                        }*/

                            if (Utils.validateString(registerModel.user.getOrder_id())) {
                                order_id = registerModel.user.getOrder_id();
                                Utils.storeString(mSharedPreferences, Utils.ORDER_ID, registerModel.user.getOrder_id());
                            }


                        } else if (registerModel.success.equalsIgnoreCase("0")) {
                            Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), registerModel.message);
                            otpDialog.dismiss();
                        }
                    }

                    @Override
                    public void onErrorReceived(String error) {
                        Toast.makeText(getActivity(), getString(R.string.try_again), Toast.LENGTH_SHORT).show();
                    }
                });
                hireOTPRequestTask.execute(user_id, mobile, orderId);
            } else {
                Toast.makeText(getActivity(), getString(R.string.connection), Toast.LENGTH_SHORT).show();
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }

    private void onOTPResend(String mobile, String order_id) {

        try {
            if (Utils.checkInternetConnection(getActivity())) {
                HireResendOTPRequestTask hireResendOTPRequestTask = new HireResendOTPRequestTask(getActivity());
                hireResendOTPRequestTask.setAsyncCallListener(new AsyncCallListener() {

                    @Override
                    public void onResponseReceived(Object response) {
                        RegisterModel registerModel = (RegisterModel) response;
                        if (registerModel.success.equalsIgnoreCase("1")) {

                       /* if (Utils.validateString(registerModel.user.getOtp())) {
//                            otpDialog.dismiss();
//                            hireOTP = registerModel.getHireotp();
                            mOtpNumber.setText(registerModel.user.getOtp());
                            mOtpNumber.setSelection(mOtpNumber.getText().length());
//                            openOTPDialog();
//                            Utils.storeString(mSharedPreferences, Utils.OTP_CODE, registerModel.getHireotp());
                        }*/
                            txtResendOtp.setClickable(false);
                            txtResendOtp.setTextColor(Color.parseColor("#808080"));

                            mProgressDialog = new ProgressDialog(getActivity());
                            mProgressDialog.setMessage(getResources().getString(R.string.otp_msg));
                            mProgressDialog.setCanceledOnTouchOutside(false);
                            mProgressDialog.setCancelable(false);
                            if (mProgressDialog != null && !mProgressDialog.isShowing()) {
                                mProgressDialog.show();
                            }
                            doTheAutoRefresh();

                        } else if (registerModel.success.equalsIgnoreCase("0")) {
//                        Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), registerModel.message);
                            txtResendOtp.setClickable(true);
                            txtResendOtp.setTextColor(Color.parseColor("#1D7CB2"));
                            otpDialog.dismiss();

                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setTitle("Alert");
                            builder.setCancelable(false);
                            builder.setMessage(registerModel.message);
                            builder.setPositiveButton("OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

                                        }
                                    });

                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();

                        }
                    }

                    @Override
                    public void onErrorReceived(String error) {
                        Toast.makeText(getActivity(), getString(R.string.try_again), Toast.LENGTH_SHORT).show();
                    }
                });
                hireResendOTPRequestTask.execute(mobile, order_id);
            } else {
                Toast.makeText(getActivity(), getString(R.string.connection), Toast.LENGTH_SHORT).show();
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }
}
