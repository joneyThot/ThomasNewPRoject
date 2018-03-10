package com.handy.fragment;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore.Images;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.handy.MainActivity;
import com.handy.R;
import com.handy.logger.Logger;
import com.handy.model.CoinAmountModel;
import com.handy.model.ApplyDiscountCreditsModel;
import com.handy.model.ApplyDiscountModel;
import com.handy.model.DisocuntCreditsModel;
import com.handy.model.HandymanCreditsModel;
import com.handy.model.JobCompeletedModel;
import com.handy.model.MyHiringsModel;
import com.handy.service.ApplyDiscountRequestTask;
import com.handy.service.AsyncCallListener;
import com.handy.service.GetCoinsRequestTask;
import com.handy.service.GetCustomerAddedCoinsRequestTask;
import com.handy.service.GetHandymanChangeStatusRequestTask;
import com.handy.service.HandymanAddCustomerCoinsRequestTask;
import com.handy.service.JobCompletedRequestTask;
import com.handy.service.Utils;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

@SuppressLint("CutPasteId")
public class SignatureFragment extends BaseFragment implements OnClickListener {

    private static String TAG = "SignatureFragment";

    private SharedPreferences mSharedPreferences;

    Fragment fr;
    View mRootView;
    EditText mJobTitle, mAmount, mYourName, mDiscount, mCoins, mTotalAmount;
    TextView mSigTextView, mSaveSignatureBtn;
    ImageView mSignatureImg;
    LinearLayout mImageLayout;
    private String job_description = "", job_id = "", status_id = "", client_id = "";
    String applyDiscount = "", applyCredit = "", writeDiscount = "", writeCredit = "";
    Dialog signatureDialog;
    private int mDeviceWidth = 480;

    LinearLayout mContent;
    signature mSignature;
    public static String tempDir;
    public int count = 1;
    public String current = null;
    private Bitmap mBitmap;
    View mView;
    File mypath;
    String img_url = null;
    public String coins_value = "";

    private String uniqueId = "signature";

    ArrayList<ApplyDiscountCreditsModel> mDiscountCreditList = new ArrayList<ApplyDiscountCreditsModel>();
    ArrayList<DisocuntCreditsModel> mDiscountList = new ArrayList<DisocuntCreditsModel>();
    ArrayList<DisocuntCreditsModel> mCreditsList = new ArrayList<DisocuntCreditsModel>();

    Handler mHandler = new Handler();
    Runnable mRunnable;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_signature, container, false);
        WindowManager w = ((Activity) getActivity()).getWindowManager();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            Point size = new Point();
            w.getDefaultDisplay().getSize(size);
            mDeviceWidth = size.x;
        } else {
            Display d = w.getDefaultDisplay();
            mDeviceWidth = d.getWidth();
        }

        initview();
        getCoins();
        return mRootView;
    }

    private void initview() {
        mSharedPreferences = getActivity().getSharedPreferences(Utils.PREF_NAME, Context.MODE_PRIVATE);
        ((MainActivity) getActivity()).setTitleText("", getString(R.string.signature_title), View.VISIBLE, View.GONE, View.GONE);
        getActivity().findViewById(R.id.title).setVisibility(View.GONE);

        mActivity.getSlidingMenu().setMode(SlidingMenu.LEFT);
        mActivity.getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);

        mJobTitle = (EditText) mRootView.findViewById(R.id.signature_job_title_text);
        mAmount = (EditText) mRootView.findViewById(R.id.signature_amount_text);
        mTotalAmount = (EditText) mRootView.findViewById(R.id.signature_total_amount_text);
        mYourName = (EditText) mRootView.findViewById(R.id.signature_name_text);
        mDiscount = (EditText) mRootView.findViewById(R.id.signature_discount);

        mCoins = (EditText) mRootView.findViewById(R.id.signature_credits);
        mImageLayout = (LinearLayout) mRootView.findViewById(R.id.image_layout);
        mSigTextView = (TextView) mRootView.findViewById(R.id.signature_txt);
        mSignatureImg = (ImageView) mRootView.findViewById(R.id.signature_image);

        mRootView.findViewById(R.id.apply).setOnClickListener(this);
        mRootView.findViewById(R.id.signature_btn).setOnClickListener(this);
        mRootView.findViewById(R.id.signature_submit_Button).setOnClickListener(this);

        mAmount.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
                try {
                    if (Utils.validateString(mAmount.getText().toString().trim())) {
                        int coins = 0, totalAmount;
                        float credit = 0.0f;
                        int amount = Integer.parseInt(mAmount.getText().toString().trim());
                        if (Utils.validateString(mCoins.getText().toString().trim())) {
                            coins = Integer.parseInt(mCoins.getText().toString().trim());
                        }

                        if (Utils.validateString(mDiscount.getText().toString().trim())) {
                            credit = Float.parseFloat(mDiscount.getText().toString());
                        }
                        if (amount > 0) {
                            if (coins > 0) {
                                totalAmount = amount - (coins * Integer.parseInt(coins_value));
                            } else {
                                totalAmount = amount;
                            }

                            if (totalAmount >= 0) {
                                if (credit > 0) {
                                    float discountAmount = (amount * credit) / 100;
                                    int finalAmount = (int) (totalAmount - discountAmount);
                                    if (finalAmount >= 0) {
                                        mTotalAmount.setText("" + finalAmount);
                                    }
                                } else {
                                    mTotalAmount.setText("" + totalAmount);
                                }
                            }
                        } else {
                            mTotalAmount.setText("");
                        }
                    } else {
                        mTotalAmount.setText("");
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

            }
        });

        mDiscount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    int totalAmount, coins = 0, amount = 0;
                    if (Utils.validateString(mDiscount.getText().toString().trim())) {
                        float credit = Float.parseFloat(mDiscount.getText().toString());
                        if (credit > 0 && credit < 100) {
                            if (Utils.validateString(mCoins.getText().toString().trim())) {
                                coins = Integer.parseInt(mCoins.getText().toString().trim());
                            }

                            if (Utils.validateString(mAmount.getText().toString().trim())) {
                                amount = Integer.parseInt(mAmount.getText().toString().trim());
                            }

                            if (amount > 0) {
                                if (coins > 0) {
                                    totalAmount = amount - (coins * Integer.parseInt(coins_value));
                                } else {
                                    totalAmount = amount;
                                }

                                if (totalAmount >= 0) {
                                    if (credit > 0) {
                                        float discountAmount = (amount * credit) / 100;
                                        int finalAmount = (int) (totalAmount - discountAmount);
                                        if (finalAmount >= 0) {
                                            mTotalAmount.setText("" + finalAmount);
                                        }
                                    } else {
                                        mTotalAmount.setText("" + totalAmount);
                                    }
                                }
                            } else {
                                mTotalAmount.setText("");
                            }

                        } else {
                            Toast.makeText(getActivity(), "You enter invalid discount value.", Toast.LENGTH_SHORT).show();
                            if (getActivity().getCurrentFocus() != null) {
                                InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                                inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
                            }
                            mDiscount.setText("");

                            if (Utils.validateString(mCoins.getText().toString().trim())) {
                                coins = Integer.parseInt(mCoins.getText().toString().trim());
                            }
                            if (Utils.validateString(mAmount.getText().toString().trim())) {
                                amount = Integer.parseInt(mAmount.getText().toString().trim());
                            }

                            if (amount > 0) {
                                if (coins > 0) {
                                    totalAmount = amount - (coins * Integer.parseInt(coins_value));
                                    mTotalAmount.setText("" + totalAmount);
                                } else {
                                    mTotalAmount.setText("" + amount);
                                }

                            } else {
                                mTotalAmount.setText("");
                            }
                        }
                    } else {
                        if (Utils.validateString(mCoins.getText().toString().trim())) {
                            coins = Integer.parseInt(mCoins.getText().toString().trim());
                        }
                        if (Utils.validateString(mAmount.getText().toString().trim())) {
                            amount = Integer.parseInt(mAmount.getText().toString().trim());
                        }

                        if (amount > 0) {
                            if (coins > 0) {
                                totalAmount = amount - (coins * Integer.parseInt(coins_value));
                                mTotalAmount.setText("" + totalAmount);
                            } else {
                                mTotalAmount.setText("" + amount);
                            }

                        } else {
                            mTotalAmount.setText("");
                        }
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }


            }
        });

        if (getArguments() != null) {
            job_description = getArguments().getString(Utils.HANDYMAN_JOB_DESCRIPTION);
            job_id = getArguments().getString(Utils.JOB_ID);
            status_id = getArguments().getString(Utils.STATUS_ID);
            client_id = getArguments().getString(Utils.CLIENT_ID);
            mJobTitle.setText(job_description);
        }

        Utils.storeString(mSharedPreferences, "COINS", "");

        addCoinsNotification(job_id, client_id);
        doTheAutoRefresh();

    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {

            case R.id.apply:
                if (fieldValidation_1()) {
                    if (Utils.validateString(mDiscount.getText().toString()) || Utils.validateString(mCoins.getText().toString())) {
                        mYourName.requestFocus();
                        onApplyDiscount(client_id, mAmount.getText().toString(), mDiscount.getText().toString(), mCoins.getText().toString());
                    }
//                    } else {
//                        Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), "Please enter discount value.");
//                        mTotalAmount.setText(mAmount.getText().toString().trim());
//                        mDiscount.requestFocus();
//                    }
                }

                break;

            case R.id.signature_btn:
                if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                } else {
                    openSignatureDialog();
                }
                break;

            case R.id.signature_submit_Button:
                if (fieldValidation()) {

                    if (Utils.validateString(img_url)) {
                        Logger.d(TAG, "Image Path:: " + String.valueOf(mypath));
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
                        builder1.setTitle("Alert");
                        builder1.setCancelable(false);
                        builder1.setMessage("Are you sure for complete.");
                        builder1.setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
//                                        mHandler.removeCallbacks(mRunnable);
                                        onJobCompeleted(job_description, mYourName.getText().toString(), mAmount.getText().toString(), job_id, String.valueOf(mypath),
                                                mTotalAmount.getText().toString(), mDiscount.getText().toString(), mCoins.getText().toString());


                                    }
                                });
                        builder1.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                Utils.storeString(mSharedPreferences, Utils.COMPELETE, "");
                            }

                        });
                        AlertDialog alertDialog1 = builder1.create();
                        alertDialog1.show();


                    } else {
                        Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), "Please Check signature");
                    }
                }
                break;

            case R.id.cancel:
                signatureDialog.dismiss();
                break;

            case R.id.clear:
                Logger.v("log_tag", "Panel Cleared");
                mSignature.clear();
                mSaveSignatureBtn.setEnabled(false);
                break;

            case R.id.getsign:

                Logger.v("log_tag", "Panel Saved");
                Logger.v("log_tag", "Image Url:" + img_url);
                mView.setDrawingCacheEnabled(true);
                mSignature.save(mView);

                if (Utils.validateString(img_url)) {
                    mSigTextView.setVisibility(View.GONE);
                    mImageLayout.setVisibility(View.VISIBLE);

                    Transformation transformation = new Transformation() {

                        @Override
                        public Bitmap transform(Bitmap source) {
                            int targetWidth = mDeviceWidth;

                            double aspectRatio = (double) source.getHeight() / (double) source.getWidth();
                            int targetHeight = (int) (targetWidth * aspectRatio);
                            if (targetHeight > targetWidth) {
                                targetHeight = targetWidth;
                            }
                            Bitmap result = Bitmap.createScaledBitmap(source, targetWidth, targetHeight, false);
                            if (result != source) {
                                // Same bitmap is returned if sizes are the same
                                source.recycle();
                            }
                            return result;
                        }

                        @Override
                        public String key() {
                            return "transformation" + " desiredWidth";
                        }
                    };

                    Picasso.with(getActivity())
                            .load(img_url)
                            .error(R.drawable.avtar_images)
                            .transform(transformation)
                            .centerCrop()
                            .resize(mDeviceWidth, (int) (mDeviceWidth))
                            .into(mSignatureImg);

                    Logger.e(TAG, "Sign Path--" + img_url + "");
                }

                signatureDialog.dismiss();
                break;

        }
    }

    public boolean fieldValidation() {
        boolean flag = true;
        if (!Utils.validateString(mAmount.getText().toString())) {
            flag = false;
//			mAmount.setError("Please Enter Amount");
            Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), "Please Enter Amount");
            mAmount.requestFocus();
        } /*else if(!Utils.validateString(mDiscount.getText().toString())) {
            flag = false;
//			mYourName.setError("Please Enter Name");
			Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), "Please Enter Discount");
			mDiscount.requestFocus();
		} else if(!Utils.validateString(mCoins.getText().toString())) {
			flag = false;
//			mYourName.setError("Please Enter Name");
			Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), "Please Enter Credits");
			mCoins.requestFocus();
		} */ else if (!Utils.validateString(mYourName.getText().toString())) {
            flag = false;
//			mYourName.setError("Please Enter Name");
            Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), "Please Enter Name");
            mYourName.requestFocus();
        }
        return flag;
    }

    public boolean fieldValidation_1() {
        boolean flag = true;
        if (!Utils.validateString(mAmount.getText().toString())) {
            flag = false;
            Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), "Please Enter Amount");
            mAmount.requestFocus();
        }
        return flag;
    }


    private void onChangeStatusComplete(String id, String hire_status, String service_updated_by) {

        if (Utils.checkInternetConnection(getActivity())) {
            GetHandymanChangeStatusRequestTask getHandymanChangeStatusRequestTask = new GetHandymanChangeStatusRequestTask(getActivity());
            getHandymanChangeStatusRequestTask.setAsyncCallListener(new AsyncCallListener() {

                @Override
                public void onResponseReceived(Object response) {
                    MyHiringsModel myHiringsModel = (MyHiringsModel) response;
                    if (myHiringsModel.success.equalsIgnoreCase("1")) {
//                            Toast.makeText(getActivity(), myHiringsModel.message , Toast.LENGTH_SHORT).show();
//                        if(Utils.validateString(img_url)) {
//                            File fdelete = new File(img_url);
//                            if (fdelete.exists()) {
//                                if (fdelete.delete()) {
//                                    System.out.println("file Deleted :" + img_url);
//                                } else {
//                                    System.out.println("file not Deleted :" + img_url);
//                                }
//                            }
//                        }

                    } else if (myHiringsModel.success.equalsIgnoreCase("0")) {
//                            Toast.makeText(getActivity(), myHiringsModel.message , Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onErrorReceived(String error) {
                    Toast.makeText(getActivity(), getString(R.string.try_again), Toast.LENGTH_SHORT).show();
                }
            });
            getHandymanChangeStatusRequestTask.execute(id, hire_status, service_updated_by);
        } else {
            Toast.makeText(getActivity(), getString(R.string.connection), Toast.LENGTH_SHORT).show();
        }
    }

    private void onApplyDiscount(String client_id, String amount, String discount, String credit) {

        if (Utils.checkInternetConnection(getActivity())) {
            ApplyDiscountRequestTask applyDiscountRequestTask = new ApplyDiscountRequestTask(getActivity());
            applyDiscountRequestTask.setAsyncCallListener(new AsyncCallListener() {

                @Override
                public void onResponseReceived(Object response) {
                    mDiscountCreditList.clear();
                    ApplyDiscountModel applyDiscountModel = (ApplyDiscountModel) response;
                    if (applyDiscountModel.success.equalsIgnoreCase("1")) {
//                         Toast.makeText(getActivity(), applyDiscountModel.message , Toast.LENGTH_SHORT).show();
                        mDiscountCreditList.addAll(applyDiscountModel.getApplyDiscountCreditsModels());

                        if (mDiscountCreditList.size() > 0 && mDiscountCreditList.size() <= 1) {
                            Logger.e(TAG, "mDiscountCreditList Size ::" + mDiscountCreditList.size());

                            if (mDiscountCreditList.get(0).getmDiscountModel() != null) {
                                mDiscountList.clear();
                                mDiscountList.addAll(mDiscountCreditList.get(0).getmDiscountModel());
                                if (mDiscountList.size() > 0) {
                                    if (mDiscountList.get(0).getSuccess().equalsIgnoreCase("1")) {
                                        applyDiscount = mDiscountList.get(0).total;
                                        writeDiscount = mDiscountList.get(0).discount;
                                        mTotalAmount.setText(applyDiscount);

                                    } else if (mDiscountList.get(0).getSuccess().equalsIgnoreCase("0")) {
//	                        			 Toast.makeText(getActivity(), mDiscountList.get(0).message , Toast.LENGTH_SHORT).show();
                                        if (Utils.validateString(mDiscountList.get(0).message)) {
                                            Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), mDiscountList.get(0).message);
                                        }

                                    }

                                }
                            }

                            if (mDiscountCreditList.get(0).getmCreditModel() != null) {
                                mCreditsList.clear();
                                mCreditsList.addAll(mDiscountCreditList.get(0).getmCreditModel());
                                if (mCreditsList.size() > 0) {
                                    if (mCreditsList.get(0).getSuccess().equalsIgnoreCase("1")) {
                                        applyCredit = mCreditsList.get(0).total;
                                        writeCredit = mCreditsList.get(0).credit;
                                        mTotalAmount.setText(applyCredit);

                                    } else if (mCreditsList.get(0).getSuccess().equalsIgnoreCase("0")) {
//                            			 Toast.makeText(getActivity(), mCreditsList.get(0).message , Toast.LENGTH_SHORT).show();
                                        if (Utils.validateString(mCreditsList.get(0).message)) {
                                            Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), mCreditsList.get(0).message);
                                        }

                                    }
                                }
                            }

                        }

                        if (mDiscountCreditList.size() == 2) {
                            Logger.e(TAG, "mDiscountCreditList Size ::" + mDiscountCreditList.size());

                            if (mDiscountCreditList.get(0).getmDiscountModel() != null) {
                                mDiscountList.clear();
                                mDiscountList.addAll(mDiscountCreditList.get(0).getmDiscountModel());
                                if (mDiscountList.size() > 0) {
                                    if (mDiscountList.get(0).getSuccess().equalsIgnoreCase("1")) {
                                        applyDiscount = mDiscountList.get(0).total;
                                        writeDiscount = mDiscountList.get(0).discount;
                                        mTotalAmount.setText(applyDiscount);

                                    } else if (mDiscountList.get(0).getSuccess().equalsIgnoreCase("0")) {
//	                        			 Toast.makeText(getActivity(), mDiscountList.get(0).message , Toast.LENGTH_SHORT).show();
                                        if (Utils.validateString(mDiscountList.get(0).message)) {
                                            Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), mDiscountList.get(0).message);
                                        }

                                    }

                                }
                            }

                            if (mDiscountCreditList.get(1).getmCreditModel() != null) {
                                mCreditsList.clear();
                                mCreditsList.addAll(mDiscountCreditList.get(1).getmCreditModel());
                                if (mCreditsList.size() > 0) {
                                    if (mCreditsList.get(0).getSuccess().equalsIgnoreCase("1")) {
                                        applyCredit = mCreditsList.get(0).total;
                                        writeCredit = mCreditsList.get(0).credit;
                                        mTotalAmount.setText(applyCredit);

                                    } else if (mCreditsList.get(0).getSuccess().equalsIgnoreCase("0")) {
//                            			 Toast.makeText(getActivity(), mCreditsList.get(0).message , Toast.LENGTH_SHORT).show();
                                        if (Utils.validateString(mCreditsList.get(0).message)) {
                                            Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), mCreditsList.get(0).message);
                                        }

                                    }
                                }
                            }
                        }

                    } else if (applyDiscountModel.success.equalsIgnoreCase("0")) {
//                          Toast.makeText(getActivity(), applyDiscountModel.message , Toast.LENGTH_SHORT).show();
                        Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), applyDiscountModel.message);
//                    	  mDiscount.requestFocus();
                        mDiscountCreditList.addAll(applyDiscountModel.getApplyDiscountCreditsModels());

                        if (mDiscountCreditList.size() > 0 && mDiscountCreditList.size() <= 1) {
                            Logger.e(TAG, "mDiscountCreditList Size ::" + mDiscountCreditList.size());

                            if (mDiscountCreditList.get(0).getmDiscountModel() != null) {
                                mDiscountList.clear();
                                mDiscountList.addAll(mDiscountCreditList.get(0).getmDiscountModel());
                                if (mDiscountList.size() > 0) {
                                    if (mDiscountList.get(0).getSuccess().equalsIgnoreCase("1")) {
                                        applyDiscount = mDiscountList.get(0).total;
                                        writeDiscount = mDiscountList.get(0).discount;
                                        mTotalAmount.setText(applyDiscount);

                                    } else if (mDiscountList.get(0).getSuccess().equalsIgnoreCase("0")) {
// 	                        			 Toast.makeText(getActivity(), mDiscountList.get(0).message , Toast.LENGTH_SHORT).show();
                                        if (Utils.validateString(mDiscountList.get(0).message)) {
                                            Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), mDiscountList.get(0).message);
                                        }
                                    }

                                }
                            }

                            if (mDiscountCreditList.get(0).getmCreditModel() != null) {
                                mCreditsList.clear();
                                mCreditsList.addAll(mDiscountCreditList.get(0).getmCreditModel());
                                if (mCreditsList.size() > 0) {
                                    if (mCreditsList.get(0).getSuccess().equalsIgnoreCase("1")) {
                                        applyCredit = mCreditsList.get(0).total;
                                        writeCredit = mCreditsList.get(0).credit;
                                        mTotalAmount.setText(applyCredit);

                                    } else if (mCreditsList.get(0).getSuccess().equalsIgnoreCase("0")) {
//                             			 Toast.makeText(getActivity(), mCreditsList.get(0).message , Toast.LENGTH_SHORT).show();

                                        if (Utils.validateString(mCreditsList.get(0).message)) {
                                            Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), mCreditsList.get(0).message);
                                        }

                                    }
                                }
                            }

                        }

                        if (mDiscountCreditList.size() == 2) {
                            Logger.e(TAG, "mDiscountCreditList Size ::" + mDiscountCreditList.size());

                            if (mDiscountCreditList.get(0).getmDiscountModel() != null) {
                                mDiscountList.clear();
                                mDiscountList.addAll(mDiscountCreditList.get(0).getmDiscountModel());
                                if (mDiscountList.size() > 0) {
                                    if (mDiscountList.get(0).getSuccess().equalsIgnoreCase("1")) {
                                        applyDiscount = mDiscountList.get(0).total;
                                        writeDiscount = mDiscountList.get(0).discount;
                                        mTotalAmount.setText(applyDiscount);

                                    } else if (mDiscountList.get(0).getSuccess().equalsIgnoreCase("0")) {
// 	                        			 Toast.makeText(getActivity(), mDiscountList.get(0).message , Toast.LENGTH_SHORT).show();
                                        if (Utils.validateString(mDiscountList.get(0).message)) {
                                            Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), mDiscountList.get(0).message);
                                        }
                                    }

                                }
                            }

                            if (mDiscountCreditList.get(1).getmCreditModel() != null) {
                                mCreditsList.clear();
                                mCreditsList.addAll(mDiscountCreditList.get(1).getmCreditModel());
                                if (mCreditsList.size() > 0) {
                                    if (mCreditsList.get(0).getSuccess().equalsIgnoreCase("1")) {
                                        applyCredit = mCreditsList.get(0).total;
                                        writeCredit = mCreditsList.get(0).credit;
                                        mTotalAmount.setText(applyCredit);

                                    } else if (mCreditsList.get(0).getSuccess().equalsIgnoreCase("0")) {
//                             			 Toast.makeText(getActivity(), mCreditsList.get(0).message , Toast.LENGTH_SHORT).show();
                                        if (Utils.validateString(mCreditsList.get(0).message)) {
                                            Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), mCreditsList.get(0).message);
                                        }

                                    }
                                }
                            }
                        }
                    }
                }

                @Override
                public void onErrorReceived(String error) {
                    Toast.makeText(getActivity(), getString(R.string.try_again), Toast.LENGTH_SHORT).show();
                }
            });
            applyDiscountRequestTask.execute(client_id, amount, discount, credit);
        } else {
            Toast.makeText(getActivity(), getString(R.string.connection), Toast.LENGTH_SHORT).show();
        }
    }


    private void onJobCompeleted(String job_description, String receiver_name, String amount, String id, String img, String TOTAL, String DISCOUNT, String CREDITS) {

        if (Utils.checkInternetConnection(getActivity())) {
            JobCompletedRequestTask jobCompletedRequestTask = new JobCompletedRequestTask(getActivity());
            jobCompletedRequestTask.setAsyncCallListener(new AsyncCallListener() {

                @Override
                public void onResponseReceived(Object response) {
                    JobCompeletedModel jobCompeletedModel = (JobCompeletedModel) response;
                    if (jobCompeletedModel.success.equalsIgnoreCase("1")) {
//                            Toast.makeText(getActivity(), jobCompeletedModel.message , Toast.LENGTH_SHORT).show();
                        onChangeStatusComplete(status_id, "completed", "1");
                        getActivity().getSupportFragmentManager().popBackStack("HandymanCustomerHireProfileFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        getActivity().getSupportFragmentManager().popBackStack();

                    } else if (jobCompeletedModel.success.equalsIgnoreCase("0")) {
                        Toast.makeText(getActivity(), jobCompeletedModel.message, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onErrorReceived(String error) {
                    Toast.makeText(getActivity(), getString(R.string.try_again), Toast.LENGTH_SHORT).show();
                }
            });
            jobCompletedRequestTask.execute(job_description, receiver_name, amount, id, img, TOTAL, DISCOUNT, CREDITS);
        } else {
            Toast.makeText(getActivity(), getString(R.string.connection), Toast.LENGTH_SHORT).show();
        }
    }

    private void onGetCustomerAddedCoins(String id, final String client_id, String handyman_id) {

        if (Utils.checkInternetConnection(getActivity())) {
            GetCustomerAddedCoinsRequestTask getCustomerAddedCoinsRequestTask = new GetCustomerAddedCoinsRequestTask(getActivity());
            getCustomerAddedCoinsRequestTask.setAsyncCallListener(new AsyncCallListener() {

                @Override
                public void onResponseReceived(Object response) {
                    HandymanCreditsModel handymanCreditsModel = (HandymanCreditsModel) response;
                    if (handymanCreditsModel.success.equalsIgnoreCase("1")) {
                        if (Utils.validateString(handymanCreditsModel.getCoins()) && !handymanCreditsModel.getCoins().equalsIgnoreCase("0")) {
                            mCoins.setText(handymanCreditsModel.getCoins());

                            if (!mSharedPreferences.getString("COINS", "").equalsIgnoreCase(mCoins.getText().toString())) {
                                if (Utils.validateString(mAmount.getText().toString()) && mAmount.getText().toString().length() >= 2
                                        && Utils.validateString(mCoins.getText().toString())) {

                                    Utils.storeString(mSharedPreferences, "COINS", handymanCreditsModel.getCoins());
                                    mCoins.setText(handymanCreditsModel.getCoins());
                                    onApplyDiscount(client_id, mAmount.getText().toString(), mDiscount.getText().toString(), mCoins.getText().toString());
                                }

                            }

                        } else {
                            mCoins.setHint(R.string.signature_credits);
                        }

                    } else if (handymanCreditsModel.success.equalsIgnoreCase("0")) {

                    }
                }

                @Override
                public void onErrorReceived(String error) {
                    Toast.makeText(getActivity(), getString(R.string.try_again), Toast.LENGTH_SHORT).show();
                }
            });
            getCustomerAddedCoinsRequestTask.execute(id, client_id, handyman_id);
        } else

        {
            Toast.makeText(getActivity(), getString(R.string.connection), Toast.LENGTH_SHORT).show();
        }
    }

    private void addCoinsNotification(String id, String client_id) {

        if (Utils.checkInternetConnection(getActivity())) {
            HandymanAddCustomerCoinsRequestTask handymanAddCustomerCoinsRequestTask = new HandymanAddCustomerCoinsRequestTask(getActivity());
            handymanAddCustomerCoinsRequestTask.setAsyncCallListener(new AsyncCallListener() {

                @Override
                public void onResponseReceived(Object response) {

                }

                @Override
                public void onErrorReceived(String error) {
                    Toast.makeText(getActivity(), getString(R.string.try_again), Toast.LENGTH_SHORT).show();
                }
            });
            handymanAddCustomerCoinsRequestTask.execute(id, client_id);
        } else {
            Toast.makeText(getActivity(), getString(R.string.connection), Toast.LENGTH_SHORT).show();
        }
    }

    private void getCoins() {
        if (Utils.checkInternetConnection(getActivity())) {
            GetCoinsRequestTask getCoinsRequestTask = new GetCoinsRequestTask(getActivity());
            getCoinsRequestTask.setAsyncCallListener(new AsyncCallListener() {

                @SuppressWarnings("unchecked")
                @Override
                public void onResponseReceived(Object response) {
                    CoinAmountModel coinsModel = (CoinAmountModel) response;
                    if (coinsModel.getSuccess().equalsIgnoreCase("1")) {

                        if (Utils.validateString(coinsModel.getOption_value())) {
                            coins_value = coinsModel.getOption_value();
                        }

                    } /*else if (coinsModel.getSuccess().equalsIgnoreCase("0")) {
//                        Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), coinsModel.getMessage());
                    }*/

                }

                @Override
                public void onErrorReceived(String error) {
                    Toast.makeText(getActivity(), getString(R.string.try_again), Toast.LENGTH_SHORT).show();
                }
            });
            getCoinsRequestTask.execute();
        }
    }

    private void doTheAutoRefresh() {
        mRunnable = new Runnable() {

            @Override
            public void run() {
                onGetCustomerAddedCoins(status_id, client_id, mSharedPreferences.getString(Utils.USER_ID, ""));
                doTheAutoRefresh();
            }
        };
        mHandler.postDelayed(mRunnable, 2000);

    }

    @Override
    public void onDestroy() {

        mHandler.removeCallbacks(mRunnable);
        super.onDestroy();

    }

    private void openSignatureDialog() {
        signatureDialog = new Dialog(getActivity());
        signatureDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        signatureDialog.setContentView(R.layout.signature_dialog);

        tempDir = Environment.getExternalStorageDirectory() + "/" + "GetSignature" + "/";
        ContextWrapper cw = new ContextWrapper(getActivity());
        File directory = cw.getDir("GetSignature", Context.MODE_PRIVATE);

        prepareDirectory();
//        uniqueId = getTodaysDate() + "_" + getCurrentTime() + "_" + Math.random();
        current = uniqueId + ".png";
        mypath = new File(directory, current);

        mContent = (LinearLayout) signatureDialog.findViewById(R.id.linearLayout3);
        mSignature = new signature(getActivity(), null);
        mSignature.setBackgroundColor(Color.WHITE);
        mContent.addView(mSignature, LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);

        signatureDialog.findViewById(R.id.cancel).setOnClickListener(this);
        signatureDialog.findViewById(R.id.clear).setOnClickListener(this);

        mSaveSignatureBtn = (TextView) signatureDialog.findViewById(R.id.getsign);
        mSaveSignatureBtn.setEnabled(false);
        mSaveSignatureBtn.setBackgroundResource(R.drawable.hover_btn_xxxhdpi);
        mSaveSignatureBtn.setOnClickListener(this);
        mView = mContent;

//		yourName = (EditText) signatureDialog.findViewById(R.id.yourName);

        signatureDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        signatureDialog.show();
    }

	
	 
	 /*private boolean captureSignature() {

	        boolean error = false;
	        String errorMessage = "";


	        if(yourName.getText().toString().equalsIgnoreCase("")){
	            errorMessage = errorMessage + "Please enter your Name\n";
	            error = true;
	        }   

	        if(error){
	            yourName.setError(errorMessage);
	            yourName.requestFocus();
	        }

	        return error;
	    }*/

    private String getTodaysDate() {

        final Calendar c = Calendar.getInstance();
        int todaysDate = (c.get(Calendar.YEAR) * 10000) +
                ((c.get(Calendar.MONTH) + 1) * 100) +
                (c.get(Calendar.DAY_OF_MONTH));
        Logger.w("DATE:", String.valueOf(todaysDate));
        return (String.valueOf(todaysDate));

    }

    private String getCurrentTime() {

        final Calendar c = Calendar.getInstance();
        int currentTime = (c.get(Calendar.HOUR_OF_DAY) * 10000) +
                (c.get(Calendar.MINUTE) * 100) +
                (c.get(Calendar.SECOND));
        Logger.w("TIME:", String.valueOf(currentTime));
        return (String.valueOf(currentTime));

    }

    private boolean prepareDirectory() {
        try {
            if (makedirs()) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Could not initiate File System.. Is Sdcard mounted properly?", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private boolean makedirs() {
        File tempdir = new File(tempDir);
        if (!tempdir.exists())
            tempdir.mkdirs();

        if (tempdir.isDirectory()) {
            File[] files = tempdir.listFiles();
            for (File file : files) {
                if (!file.delete()) {
                    System.out.println("Failed to delete " + file);
                }
            }
        }
        return (tempdir.isDirectory());
    }


    public class signature extends View {
        private static final float STROKE_WIDTH = 5f;
        private static final float HALF_STROKE_WIDTH = STROKE_WIDTH / 2;
        private Paint paint = new Paint();
        private Path path = new Path();

        private float lastTouchX;
        private float lastTouchY;
        private final RectF dirtyRect = new RectF();

        public signature(Context context, AttributeSet attrs) {
            super(context, attrs);
            paint.setAntiAlias(true);
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeWidth(STROKE_WIDTH);
        }

        public void save(View v) {
            Logger.v("log_tag", "Width: " + v.getWidth());
            Logger.v("log_tag", "Height: " + v.getHeight());
            if (mBitmap == null) {
                mBitmap = Bitmap.createBitmap(mContent.getWidth(), mContent.getHeight(), Bitmap.Config.RGB_565);
                ;
            }
            Canvas canvas = new Canvas(mBitmap);
            try {
                FileOutputStream mFileOutStream = new FileOutputStream(mypath);

                v.draw(canvas);
                mBitmap.compress(Bitmap.CompressFormat.PNG, 90, mFileOutStream);
                mFileOutStream.flush();
                mFileOutStream.close();
                img_url = Images.Media.insertImage(getActivity().getContentResolver(), mBitmap, "title", null);
                Logger.v("log_tag", "img_url: " + img_url);
                //In case you want to delete the file
                //boolean deleted = mypath.delete();
                //Log.v("log_tag","deleted: " + mypath.toString() + deleted);
                //If you want to convert the image to string use base64 converter

            } catch (Exception e) {
                Logger.v("log_tag", e.toString());
            }
        }

        public void clear() {
            path.reset();
            invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawPath(path, paint);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float eventX = event.getX();
            float eventY = event.getY();
            mSaveSignatureBtn.setEnabled(true);
            mSaveSignatureBtn.setBackgroundResource(R.drawable.btn_xxxhdpi);

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    path.moveTo(eventX, eventY);
                    lastTouchX = eventX;
                    lastTouchY = eventY;
                    return true;

                case MotionEvent.ACTION_MOVE:

                case MotionEvent.ACTION_UP:

                    resetDirtyRect(eventX, eventY);
                    int historySize = event.getHistorySize();
                    for (int i = 0; i < historySize; i++) {
                        float historicalX = event.getHistoricalX(i);
                        float historicalY = event.getHistoricalY(i);
                        expandDirtyRect(historicalX, historicalY);
                        path.lineTo(historicalX, historicalY);
                    }
                    path.lineTo(eventX, eventY);
                    break;

                default:
                    debug("Ignored touch event: " + event.toString());
                    return false;
            }

            invalidate((int) (dirtyRect.left - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.top - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.right + HALF_STROKE_WIDTH),
                    (int) (dirtyRect.bottom + HALF_STROKE_WIDTH));

            lastTouchX = eventX;
            lastTouchY = eventY;

            return true;
        }

        private void debug(String string) {
        }

        private void expandDirtyRect(float historicalX, float historicalY) {
            if (historicalX < dirtyRect.left) {
                dirtyRect.left = historicalX;
            } else if (historicalX > dirtyRect.right) {
                dirtyRect.right = historicalX;
            }

            if (historicalY < dirtyRect.top) {
                dirtyRect.top = historicalY;
            } else if (historicalY > dirtyRect.bottom) {
                dirtyRect.bottom = historicalY;
            }
        }

        private void resetDirtyRect(float eventX, float eventY) {
            dirtyRect.left = Math.min(lastTouchX, eventX);
            dirtyRect.right = Math.max(lastTouchX, eventX);
            dirtyRect.top = Math.min(lastTouchY, eventY);
            dirtyRect.bottom = Math.max(lastTouchY, eventY);
        }
    }

}