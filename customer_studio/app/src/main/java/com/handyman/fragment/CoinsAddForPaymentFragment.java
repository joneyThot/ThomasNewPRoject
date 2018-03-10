package com.handyman.fragment;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.handyman.MainActivity;
import com.handyman.R;
import com.handyman.crop.Util;
import com.handyman.model.HireHandymanModel;
import com.handyman.service.AddCoinsForPaymentRequestTask;
import com.handyman.service.AsyncCallListener;
import com.handyman.service.CheckCustomerCoinsRequestTask;
import com.handyman.service.Utils;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class CoinsAddForPaymentFragment extends BaseFragment implements OnClickListener {

    private static String TAG = "CoinsAddForPaymentFragment";

    private SharedPreferences mSharedPreferences;
    private EditText edtCoins;

    Fragment fr;
    View mRootView;

    String hire_handyman_status_id = "", debit_value = "";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_send_coins, container, false);
        initview();
        return mRootView;
    }

    private void initview() {
        mSharedPreferences = getActivity().getSharedPreferences(Utils.PREF_NAME, Context.MODE_PRIVATE);
        ((MainActivity) getActivity()).setTitleText("", getString(R.string.send_coins_title), "", View.VISIBLE, View.GONE, View.GONE, View.GONE, View.GONE);
        getActivity().findViewById(R.id.title).setVisibility(View.GONE);
        getActivity().findViewById(R.id.txtUrl).setVisibility(View.GONE);

        mActivity.getSlidingMenu().setMode(SlidingMenu.LEFT);
        mActivity.getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);


        NotificationManager nMgr = (NotificationManager) getActivity().getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        nMgr.cancelAll();

        if (getArguments() != null) {
            hire_handyman_status_id = getArguments().getString(Utils.HIRE_HANDYMAN_STATUS_ID);
            debit_value = getArguments().getString(Utils.HIRE_DEBIT_COINS);
        }

        mRootView.findViewById(R.id.titlebar).setVisibility(View.GONE);
        edtCoins = (EditText) mRootView.findViewById(R.id.edtCoins);
        mRootView.findViewById(R.id.btnConfirm).setOnClickListener(this);

        if(Utils.validateString(debit_value) && !debit_value.equalsIgnoreCase("0")){
            edtCoins.setText(debit_value);
        }

        edtCoins.setFocusable(true);
        edtCoins.setFocusableInTouchMode(true);

        edtCoins.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (Utils.validateString(edtCoins.getText().toString().trim())) {
                    CheckCustomerCoins(mSharedPreferences.getString(Utils.USER_ID, ""), edtCoins.getText().toString().trim());
                }
            }
        });


    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {

            case R.id.btnConfirm:
                if (getActivity().getCurrentFocus() != null) {
                    InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
                }

                if (fieldValidation()) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Alert");
                    builder.setCancelable(false);
                    builder.setMessage("Are you sure you want to use coins for payment?");
                    builder.setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    sendCoinsForPayment(hire_handyman_status_id, mSharedPreferences.getString(Utils.USER_ID, ""), edtCoins.getText().toString().trim());
                                }
                            });
                    builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                        }

                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
                break;

        }
    }

    public boolean fieldValidation() {
        boolean flag = true;
        if (!Utils.validateString(edtCoins.getText().toString())) {
            flag = false;
            Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), "Please enter coins.");
            edtCoins.requestFocus();
        }
        return flag;
    }

    private void CheckCustomerCoins(String id, String credit) {

        if (Utils.checkInternetConnection(getActivity())) {
            CheckCustomerCoinsRequestTask checkCustomerCoinsRequestTask = new CheckCustomerCoinsRequestTask(getActivity());
            checkCustomerCoinsRequestTask.setAsyncCallListener(new AsyncCallListener() {

                @Override
                public void onResponseReceived(Object response) {
                    HireHandymanModel hireHandymanModel = (HireHandymanModel) response;
                    if (hireHandymanModel.success.equalsIgnoreCase("0")) {
                        Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), hireHandymanModel.message);
                        edtCoins.setText("");
                        if (getActivity().getCurrentFocus() != null) {
                            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                            inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
                        }
                    }
                }

                @Override
                public void onErrorReceived(String error) {
                    Toast.makeText(getActivity(), getString(R.string.try_again), Toast.LENGTH_SHORT).show();
                }
            });
            checkCustomerCoinsRequestTask.execute(id, credit);
        } else {
            Toast.makeText(getActivity(), getString(R.string.connection), Toast.LENGTH_SHORT).show();
        }
    }

    private void sendCoinsForPayment(String id, String client_id, String coins) {
        if (Utils.checkInternetConnection(getActivity())) {
            AddCoinsForPaymentRequestTask addCoinsForPaymentRequestTask = new AddCoinsForPaymentRequestTask(getActivity());
            addCoinsForPaymentRequestTask.setAsyncCallListener(new AsyncCallListener() {

                @SuppressWarnings("unchecked")
                @Override
                public void onResponseReceived(Object response) {
                    HireHandymanModel hireHandymanModel = (HireHandymanModel) response;
                    if (hireHandymanModel.success.equalsIgnoreCase("1")) {
                        Toast.makeText(getActivity(), hireHandymanModel.message, Toast.LENGTH_SHORT).show();
                        getActivity().getSupportFragmentManager().popBackStack();
                    } else if (hireHandymanModel.success.equalsIgnoreCase("0")) {
                        Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), hireHandymanModel.message);
                    }
                }

                @Override
                public void onErrorReceived(String error) {
                    Toast.makeText(getActivity(), getString(R.string.try_again), Toast.LENGTH_SHORT).show();
                }
            });
            addCoinsForPaymentRequestTask.execute(id, client_id, coins);
        } else {
            Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert),
                    getResources().getString(R.string.connection));
        }
    }


}
