package com.handy.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.handy.MainActivity;
import com.handy.R;
import com.handy.model.RegisterModel;
import com.handy.service.AsyncCallListener;
import com.handy.service.ChangePasswordRequestTask;
import com.handy.service.Utils;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class ChangePasswordFragment extends BaseFragment implements OnClickListener {

    private static String TAG = "ChangePasswordFragment";
    private SharedPreferences mSharedPreferences;

    Fragment fr;
    View mRootView;
    EditText mNewPass, mConfirmPass;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.activity_change_password, container, false);
        initview();
        return mRootView;
    }

    private void initview() {
        mSharedPreferences = getActivity().getSharedPreferences(Utils.PREF_NAME, Context.MODE_PRIVATE);
        ((MainActivity) getActivity()).setTitleText("", getString(R.string.change_password), View.VISIBLE, View.GONE, View.GONE);
        getActivity().findViewById(R.id.title).setVisibility(View.GONE);

        mRootView.findViewById(R.id.livTop).setVisibility(View.GONE);

        mActivity.getSlidingMenu().setMode(SlidingMenu.LEFT);
        mActivity.getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);

        mNewPass = (EditText) mRootView.findViewById(R.id.cahnge_new_pass_edittxt);
        mConfirmPass = (EditText) mRootView.findViewById(R.id.change_confirm_pass_edittxt);
        mRootView.findViewById(R.id.updatePassButton).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.updatePassButton:
                if (fieldValidation()) {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);

                    onChangePassword(mSharedPreferences.getString(Utils.USER_ID, ""), mNewPass.getText().toString());
                }
                break;
        }
    }


    private void onChangePassword(String id, String password) {

        if (Utils.checkInternetConnection(getActivity())) {
            ChangePasswordRequestTask changePasswordRequestTask = new ChangePasswordRequestTask(getActivity());
            changePasswordRequestTask.setAsyncCallListener(new AsyncCallListener() {

                @Override
                public void onResponseReceived(Object response) {
                    RegisterModel registerModel = (RegisterModel) response;
                    if (registerModel.success.equalsIgnoreCase("1")) {

                        getActivity().getSupportFragmentManager().popBackStack("MyProfileFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new MyProfileFragment()).commit();

                    } else if (registerModel.success.equalsIgnoreCase("0")) {
                        Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), registerModel.message);
                    }
                }

                @Override
                public void onErrorReceived(String error) {
                    Toast.makeText(getActivity(), getString(R.string.try_again), Toast.LENGTH_SHORT).show();
                }
            });
            changePasswordRequestTask.execute(id, password);
        } else {
            Toast.makeText(getActivity(), getString(R.string.connection), Toast.LENGTH_SHORT).show();
        }
    }

    public boolean fieldValidation() {
        boolean flag = true;
        if (!Utils.validateString(mNewPass.getText().toString())) {
            flag = false;
            Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), getResources().getString(R.string.enter_password));
            mNewPass.requestFocus();
        } else if (mNewPass.getText().toString().trim().length() < 8) {
            flag = false;
            Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), getResources().getString(R.string.check_password_length));
            mNewPass.requestFocus();
        } else if (!mNewPass.getText().toString().equalsIgnoreCase(mConfirmPass.getText().toString())) {
            flag = false;
            Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), getResources().getString(R.string.password_not_match));
            mConfirmPass.requestFocus();
        }
        return flag;
    }

}
