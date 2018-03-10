package com.handy.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.handy.MainActivity;
import com.handy.R;
import com.handy.model.HandymanCreditsModel;
import com.handy.service.AsyncCallListener;
import com.handy.service.CoverCreditRequestTask;
import com.handy.service.GetHandymanCreditRequestTask;
import com.handy.service.HandymanCreditToCashRequestTask;
import com.handy.service.Utils;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;


public class RewardPointFragment extends BaseFragment implements OnClickListener {

    private static String TAG = "RewardPointFragment";
    private SharedPreferences mSharedPreferences;

    private EditText edtCreditAmount;
    private TextView txtDisplayCredits, txtTotalAmount;

    Fragment fr;
    View mRootView;
    String totalAmountCash = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_my_credits, container, false);
        initview();
        return mRootView;
    }

    private void initview() {
        mSharedPreferences = getActivity().getSharedPreferences(Utils.PREF_NAME, Context.MODE_PRIVATE);
        ((MainActivity) getActivity()).setTitleText(getString(R.string.menu_my_credits), "", View.GONE, View.VISIBLE, View.GONE);
        getActivity().findViewById(R.id.title_back).setVisibility(View.GONE);

        mActivity.getSlidingMenu().setMode(SlidingMenu.LEFT);
        mActivity.getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);

        txtDisplayCredits = (TextView) mRootView.findViewById(R.id.txtDisplayCredits);
        txtTotalAmount = (TextView) mRootView.findViewById(R.id.txtTotalAmount);
        edtCreditAmount = (EditText) mRootView.findViewById(R.id.edtCreditAmount);
        mRootView.findViewById(R.id.btnSubmit).setOnClickListener(this);

        edtCreditAmount.setFocusable(true);
        edtCreditAmount.setFocusableInTouchMode(true);

        edtCreditAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int credit = Integer.parseInt(txtDisplayCredits.getText().toString());
                if(credit > 0) {
                    String enterCredit = "";
                    if(Utils.validateString(edtCreditAmount.getText().toString().trim())){
                        enterCredit = edtCreditAmount.getText().toString().trim();
                    } else {
                        enterCredit = "0";
                    }
                    ConvertCredit(mSharedPreferences.getString(Utils.USER_ID, ""), enterCredit);
                } else {
                    Toast.makeText(getActivity(), "Please check total coins is invalid.", Toast.LENGTH_SHORT).show();
                    if (getActivity().getCurrentFocus() != null) {
                        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
                    }
                    edtCreditAmount.setText("");
                }
            }
        });

        getCredit(mSharedPreferences.getString(Utils.USER_ID, ""));
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.btnSubmit:
                if (getActivity().getCurrentFocus() != null) {
                    InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
                }
                if (fieldValidation()) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Alert");
                    builder.setCancelable(false);
                    builder.setMessage("Are you sure you want to convert coins into cash?");
                    builder.setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    CreditToCash(mSharedPreferences.getString(Utils.USER_ID, ""),edtCreditAmount.getText().toString().trim(),totalAmountCash);
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
        if (!Utils.validateString(edtCreditAmount.getText().toString())) {
            flag = false;
            Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), "Please enter number of coins.");
            edtCreditAmount.requestFocus();
        } else if (totalAmountCash.equalsIgnoreCase("0") && Utils.validateString(totalAmountCash)) {
            flag = false;
            Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), "Please enter valid coins balance.");
        }
        return flag;
    }

    private void getCredit(String id) {

        if (Utils.checkInternetConnection(getActivity())) {
            GetHandymanCreditRequestTask getHandymanCreditRequestTask = new GetHandymanCreditRequestTask(getActivity());
            getHandymanCreditRequestTask.setAsyncCallListener(new AsyncCallListener() {

                @Override
                public void onResponseReceived(Object response) {
                    HandymanCreditsModel handymanCreditsModel = (HandymanCreditsModel) response;
                    if (handymanCreditsModel.success.equalsIgnoreCase("1")) {
                        if (Utils.validateString(handymanCreditsModel.getTotal())) {
                            txtDisplayCredits.setText(handymanCreditsModel.getTotal());
                        }

                    } else if (handymanCreditsModel.success.equalsIgnoreCase("0")) {
						//Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), handymanCreditsModel.message);
//                        Toast.makeText(getActivity(), handymanCreditsModel.message, Toast.LENGTH_SHORT).show();
                        if (Utils.validateString(handymanCreditsModel.getTotal())) {
                            txtDisplayCredits.setText(handymanCreditsModel.getTotal());
                        }

                        edtCreditAmount.setFocusable(false);
                        edtCreditAmount.setFocusableInTouchMode(false);
                    }
                }

                @Override
                public void onErrorReceived(String error) {
                    Toast.makeText(getActivity(), getString(R.string.try_again), Toast.LENGTH_SHORT).show();
                }
            });
            getHandymanCreditRequestTask.execute(id);
        } else {
            Toast.makeText(getActivity(), getString(R.string.connection), Toast.LENGTH_SHORT).show();
        }
    }

    private void ConvertCredit(String id, String credit) {

        if (Utils.checkInternetConnection(getActivity())) {
            CoverCreditRequestTask coverCreditRequestTask = new CoverCreditRequestTask(getActivity());
            coverCreditRequestTask.setAsyncCallListener(new AsyncCallListener() {

                @Override
                public void onResponseReceived(Object response) {
                    HandymanCreditsModel handymanCreditsModel = (HandymanCreditsModel) response;
                    if (handymanCreditsModel.success.equalsIgnoreCase("1")) {

                        if (Utils.validateString(handymanCreditsModel.getTotal())) {
                            txtTotalAmount.setText(handymanCreditsModel.getTotal() + "/-");
                            totalAmountCash = handymanCreditsModel.getTotal();
                        }

                    } else if (handymanCreditsModel.success.equalsIgnoreCase("0")) {
                        Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), handymanCreditsModel.message);
                        edtCreditAmount.setText("");
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
            coverCreditRequestTask.execute(id, credit);
        } else {
            Toast.makeText(getActivity(), getString(R.string.connection), Toast.LENGTH_SHORT).show();
        }
    }

    private void CreditToCash(String handyman_id, String credit_used_for_convert, String cash) {

        if (Utils.checkInternetConnection(getActivity())) {
            HandymanCreditToCashRequestTask handymanCreditToCashRequestTask = new HandymanCreditToCashRequestTask(getActivity());
            handymanCreditToCashRequestTask.setAsyncCallListener(new AsyncCallListener() {

                @Override
                public void onResponseReceived(Object response) {
                    HandymanCreditsModel handymanCreditsModel = (HandymanCreditsModel) response;
                    if (handymanCreditsModel.success.equalsIgnoreCase("1")) {
//                        Toast.makeText(getActivity(), handymanCreditsModel.message, Toast.LENGTH_LONG).show();

//                        getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        SlidingMenuFragment.selectMenu(5);
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new RequestStatusFragment()).commit();

                    } else if (handymanCreditsModel.success.equalsIgnoreCase("0")) {
                        Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), handymanCreditsModel.message);
                        edtCreditAmount.setText("");
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
            handymanCreditToCashRequestTask.execute(handyman_id, credit_used_for_convert, cash);
        } else {
            Toast.makeText(getActivity(), getString(R.string.connection), Toast.LENGTH_SHORT).show();
        }
    }
}
