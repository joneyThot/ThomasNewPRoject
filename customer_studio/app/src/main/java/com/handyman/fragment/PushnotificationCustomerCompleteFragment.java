package com.handyman.fragment;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.handyman.MainActivity;
import com.handyman.R;
import com.handyman.logger.Logger;
import com.handyman.model.AdvertiseListModel;
import com.handyman.model.HireHandymanModel;
import com.handyman.model.MyHiringsModel;
import com.handyman.service.AddCoinsForPaymentRequestTask;
import com.handyman.service.AsyncCallListener;
import com.handyman.service.CheckAddCoinsRequestTask;
import com.handyman.service.CheckCustomerCoinsRequestTask;
import com.handyman.service.CheckRateHandymanRequestTask;
import com.handyman.service.FavouriteHandymanRequestTask;
import com.handyman.service.GetHandymanChangeStatusRequestTask;
import com.handyman.service.SelectAdvertiseRequestTask;
import com.handyman.service.Utils;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import org.json.JSONException;
import org.json.JSONObject;

public class PushnotificationCustomerCompleteFragment extends BaseFragment implements OnClickListener {

    private static String TAG = "PushnotificationCustomerCompleteFragment";

    private SharedPreferences mSharedPreferences;

    String status_id = "", img = "", sub_category = "", service_updated_by = "", /*city = "",*/
            hire_status = "", appointment_date = "", discount = "", client_id = "", completed_date = "", total = "", is_deleted = "", contact_no = "", floor = "",/*street = "",*/
            receiver_name = "", /*state = "", landmark = "",*/
            credit = "", lat = "", /*pincode = "",*/
            appointment_time = "", amount = "", address = "", lng = "", contact_person = "", is_outdated = "", job_description = "", img_path = "", comment = "", created_date = "", handyman_id = "", category = "", order_id = "", status = "", client_name = "", client_image = "", client_image_path = "", full_address = "", latitude = "0.0", longitude = "0.0",
            handyman_name = "", handyman_image = "", handyman_image_path = "", handyman_email = "", handyman_rating = "", handyman_mobile = "",
            handyman_lat = "0.0", handyman_lng = "0.0", apartment = "", contact_person_name = "", coin_text = "";

    String adv_id = "", adv_banner_id = "", adv_description = "", adv_banner_name = "", adv_title = "", adv_banner_path = "", like_value, is_declined = "";

    Fragment fr;
    View mRootView;
    ImageView mHanfymanprofileImg, adve_details_img, mFaveImg, mCancelImg, mRejectImg;
    private int mDeviceWidth = 480;
    TextView mCancel, advr_details_more_desc, txtSendCoins;
    EditText edtCoins;
    boolean showingFirst = false;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mSharedPreferences = getActivity().getSharedPreferences(Utils.PREF_NAME, Context.MODE_PRIVATE);
        ((MainActivity) getActivity()).hideTitleRelativeLayout();
        mActivity.getSlidingMenu().setMode(SlidingMenu.LEFT);
        mActivity.getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);

        WindowManager w = ((Activity) getActivity()).getWindowManager();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            Point size = new Point();
            w.getDefaultDisplay().getSize(size);
            mDeviceWidth = size.x;
        } else {
            Display d = w.getDefaultDisplay();
            mDeviceWidth = d.getWidth();
        }

        if ((mSharedPreferences.getString(Utils.NOTI_ADVERTISE, "").isEmpty()) && (mSharedPreferences.getString(Utils.NOTI_COINS, "").isEmpty())) {
            mRootView = inflater.inflate(R.layout.fragment_push_notification_complete, container, false);
            initview_Hire();
        } else if ((!mSharedPreferences.getString(Utils.NOTI_COINS, "").isEmpty())) {
            mRootView = inflater.inflate(R.layout.fragment_send_coins, container, false);
            initview_Coins();
        } else {
            mRootView = inflater.inflate(R.layout.fragment_advertise_details, container, false);
            initview_Advertise();
        }

//        if ((mSharedPreferences.getString(Utils.NOTI_ADVERTISE, "").isEmpty())) {
//
//        } else {
//
//        }

        return mRootView;
    }

    private void initview_Hire() {
//        mSharedPreferences = getActivity().getSharedPreferences(Utils.PREF_NAME, Context.MODE_PRIVATE);
//        ((MainActivity) getActivity()).hideTitleRelativeLayout();
//
//        mActivity.getSlidingMenu().setMode(SlidingMenu.LEFT);
//        mActivity.getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);

        SlidingMenuFragment.selectMenu(2);
        mHanfymanprofileImg = (ImageView) mRootView.findViewById(R.id.hanfyman_profile_img);

        mRootView.findViewById(R.id.push_backBtn).setOnClickListener(this);
        mRootView.findViewById(R.id.map_img).setOnClickListener(this);
        mRootView.findViewById(R.id.call_img).setOnClickListener(this);
        mRootView.findViewById(R.id.rating_img).setOnClickListener(this);
        mRootView.findViewById(R.id.report_img).setOnClickListener(this);
        mFaveImg = (ImageView) mRootView.findViewById(R.id.fave_img);
        mFaveImg.setOnClickListener(this);
        mCancel = (TextView) mRootView.findViewById(R.id.cancel_Button);
        mCancel.setOnClickListener(this);
        txtSendCoins = (TextView) mRootView.findViewById(R.id.txtSendCoins);
        txtSendCoins.setOnClickListener(this);
        mCancelImg = (ImageView) mRootView.findViewById(R.id.cancel_btn);
        mRejectImg = (ImageView) mRootView.findViewById(R.id.reject_btn);

        if (getArguments() != null) {
            String result = getArguments().getString("resposne");
            JSONObject jObj = null;
            try {
                jObj = new JSONObject(result.toString());
                if (!jObj.isNull("data")) {
                    JSONObject data_jobj = jObj.getJSONObject("data");

                    status_id = data_jobj.getString("id");
                    handyman_id = data_jobj.getString("handyman_id");
                    client_id = data_jobj.getString("client_id");
                    order_id = data_jobj.getString("order_id");
                    job_description = data_jobj.getString("job_description");
                    category = data_jobj.getString("category");
                    sub_category = data_jobj.getString("sub_category");
                    appointment_date = data_jobj.getString("appointment_date");
                    appointment_time = data_jobj.getString("appointment_time");
                    contact_person = data_jobj.getString("contact_person");
                    contact_no = data_jobj.getString("contact_no");
                    comment = data_jobj.getString("comment");
                    hire_status = data_jobj.getString("hire_status");
                    service_updated_by = data_jobj.getString("service_updated_by");
                    is_outdated = data_jobj.getString("is_outdated");
                    address = data_jobj.getString("address");
                    floor = data_jobj.getString("floor");
                    apartment = data_jobj.getString("apartment");
                    lat = data_jobj.getString("lat");
                    lng = data_jobj.getString("lng");
                    receiver_name = data_jobj.getString("receiver_name");
                    amount = data_jobj.getString("amount");
                    completed_date = data_jobj.getString("completed_date");
                    img = data_jobj.getString("img");
                    img_path = data_jobj.getString("img_path");
                    discount = data_jobj.getString("discount");
                    credit = data_jobj.getString("credit");
                    total = data_jobj.getString("total");
                    is_deleted = data_jobj.getString("is_deleted");
                    status = data_jobj.getString("status");
                    created_date = data_jobj.getString("created_date");
                    client_name = data_jobj.getString("client_name");
                    client_image = data_jobj.getString("client_image");
                    client_image_path = data_jobj.getString("client_image_path");
                    handyman_name = data_jobj.getString("handyman_name");
                    handyman_image = data_jobj.getString("handyman_image");
                    handyman_image_path = data_jobj.getString("handyman_image_path");
                    handyman_email = data_jobj.getString("handyman_email");
                    handyman_mobile = data_jobj.getString("handyman_mobile");
//                    handyman_rating = data_jobj.getString("handyman_rating");
                    handyman_rating = data_jobj.getString("handyman_rate");
                    handyman_lat = data_jobj.getString("handyman_lat");
                    handyman_lng = data_jobj.getString("handyman_lng");
                    if (!data_jobj.isNull("user_like")) {
                        like_value = data_jobj.getString("user_like");
                    }
                    if (!data_jobj.isNull("is_declined")) {
                        is_declined = data_jobj.getString("is_declined");
                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        if (Utils.validateString(handyman_name)) {
            mRootView.findViewById(R.id.push_title_back).setVisibility(View.VISIBLE);
            ((TextView) mRootView.findViewById(R.id.push_title_back)).setText(handyman_name);
            mRootView.findViewById(R.id.handyman_name_profile_txt).setVisibility(View.VISIBLE);
            ((TextView) mRootView.findViewById(R.id.handyman_name_profile_txt)).setText(handyman_name);
        }


        if (Utils.validateString(handyman_image_path)) {
            //			Picasso.with(mActivity).load(mVenue.image).fit().centerCrop().into(mBannerImage);
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

            Picasso.with(mActivity)
                    .load(Utils.IMAGE_URL + handyman_image_path)
                    .placeholder(R.drawable.avtar_images)
                    .error(R.drawable.avtar_images)
                    .transform(transformation)
                    .centerCrop()
                    .resize(mDeviceWidth, (int) (mDeviceWidth))
                    .into(mHanfymanprofileImg);
        }

        if (Utils.validateString(order_id)) {
            mRootView.findViewById(R.id.hire_customer_order_id_textview).setVisibility(View.VISIBLE);
            mRootView.findViewById(R.id.hire_customer_order_id).setVisibility(View.VISIBLE);
            ((TextView) mRootView.findViewById(R.id.hire_customer_order_id)).setText(order_id);
        }

        if (Utils.validateString(created_date)) {
            mRootView.findViewById(R.id.hire_customer_order_date_textview).setVisibility(View.VISIBLE);
            mRootView.findViewById(R.id.hire_customer_order_date).setVisibility(View.VISIBLE);
            ((TextView) mRootView.findViewById(R.id.hire_customer_order_date)).setText(created_date);
        }

        if (Utils.validateString(job_description)) {
            mRootView.findViewById(R.id.hire_customer_job_description_textview).setVisibility(View.VISIBLE);
            mRootView.findViewById(R.id.hire_customer_job_description).setVisibility(View.VISIBLE);
            ((TextView) mRootView.findViewById(R.id.hire_customer_job_description)).setText(job_description);
        }


        if (Utils.validateString(appointment_date)) {
            mRootView.findViewById(R.id.hire_customer_date_textview).setVisibility(View.VISIBLE);
            mRootView.findViewById(R.id.hire_customer_date).setVisibility(View.VISIBLE);
//					String birth_date = mPendingHirings.appointment_date;
            ((TextView) mRootView.findViewById(R.id.hire_customer_date)).setText(appointment_date);
        }


        if (Utils.validateString(appointment_time)) {
            mRootView.findViewById(R.id.hire_customer_time_textview).setVisibility(View.VISIBLE);
            mRootView.findViewById(R.id.hire_customer_time).setVisibility(View.VISIBLE);
            String time = appointment_time;
            ((TextView) mRootView.findViewById(R.id.hire_customer_time)).setText(time.substring(0, 2) + ":" + time.substring(3, 5) + "" + time.substring(8, 11));
        }


        if (Utils.validateString(contact_person)) {
            mRootView.findViewById(R.id.hire_customer_contact_person_textview).setVisibility(View.VISIBLE);
            mRootView.findViewById(R.id.hire_customer_contact_person).setVisibility(View.VISIBLE);
            ((TextView) mRootView.findViewById(R.id.hire_customer_contact_person)).setText(contact_person);
            contact_person_name = contact_person;
        }

        if (Utils.validateString(contact_no)) {
//					mobile_no = mPendingHirings.hamdyman_mobile;
            mRootView.findViewById(R.id.hire_customer_contact_no_textview).setVisibility(View.VISIBLE);
            mRootView.findViewById(R.id.hire_customer_contact_no).setVisibility(View.VISIBLE);
            ((TextView) mRootView.findViewById(R.id.hire_customer_contact_no)).setText(contact_no);
        }

        if (Utils.validateString(address)) {
            mRootView.findViewById(R.id.hire_customer_address_textview).setVisibility(View.VISIBLE);
            mRootView.findViewById(R.id.hire_customer_address).setVisibility(View.VISIBLE);
            String f = "", apt = "";
            if (Utils.validateString(floor))
                f = floor + ", ";
            if (Utils.validateString(apartment))
                apt = apartment + ", ";
            ((TextView) mRootView.findViewById(R.id.hire_customer_address)).setText(f + apt + address);
            full_address = address;
        }

        if (Utils.validateString(comment)) {
            mRootView.findViewById(R.id.hire_customer_requirment_textview).setVisibility(View.VISIBLE);
            mRootView.findViewById(R.id.hire_customer_requirment).setVisibility(View.VISIBLE);
            ((TextView) mRootView.findViewById(R.id.hire_customer_requirment)).setText(comment);
        }

        if (Utils.validateString(lat)) {
            latitude = lat;
        }

        if (Utils.validateString(lng)) {
            longitude = lng;
        }


        if (Utils.validateString(hire_status)) {
            if (hire_status.equalsIgnoreCase("pending")) {
                mRootView.findViewById(R.id.rating_img).setVisibility(View.GONE);
                mRootView.findViewById(R.id.map_img).setVisibility(View.GONE);
                mCancel.setVisibility(View.VISIBLE);
                mFaveImg.setVisibility(View.GONE);
                mCancelImg.setVisibility(View.GONE);
                mRejectImg.setVisibility(View.GONE);
                txtSendCoins.setVisibility(View.GONE);

            } else if (hire_status.equalsIgnoreCase("active")) {
                mRootView.findViewById(R.id.rating_img).setVisibility(View.GONE);
                mRootView.findViewById(R.id.map_img).setVisibility(View.GONE);
                mCancel.setVisibility(View.VISIBLE);
                mFaveImg.setVisibility(View.GONE);
                mCancelImg.setVisibility(View.GONE);
                mRejectImg.setVisibility(View.GONE);
                txtSendCoins.setVisibility(View.GONE);

            } else if (hire_status.equalsIgnoreCase("start")) {
                mRootView.findViewById(R.id.rating_img).setVisibility(View.GONE);
                mRootView.findViewById(R.id.map_img).setVisibility(View.VISIBLE);
                mCancel.setVisibility(View.VISIBLE);
                mFaveImg.setVisibility(View.GONE);
                mCancelImg.setVisibility(View.GONE);
                mRejectImg.setVisibility(View.GONE);
                txtSendCoins.setVisibility(View.VISIBLE);


            } else if (hire_status.equalsIgnoreCase("cancelled")) {
                mRootView.findViewById(R.id.rating_img).setVisibility(View.GONE);
                mRootView.findViewById(R.id.map_img).setVisibility(View.GONE);
                mCancel.setVisibility(View.GONE);
                mFaveImg.setVisibility(View.GONE);
                mCancelImg.setVisibility(View.VISIBLE);
                mRejectImg.setVisibility(View.GONE);
                txtSendCoins.setVisibility(View.GONE);

//                if (Utils.validateString(is_declined)) {
//                    if (is_declined.equalsIgnoreCase("0")) {
//                        mRootView.findViewById(R.id.rating_img).setVisibility(View.GONE);
//                        mRootView.findViewById(R.id.map_img).setVisibility(View.GONE);
//                        mCancel.setVisibility(View.GONE);
//                        mFaveImg.setVisibility(View.GONE);
//                        mCancelImg.setVisibility(View.VISIBLE);
//                        mRejectImg.setVisibility(View.GONE);
//
//                    } else if (is_declined.equalsIgnoreCase("1")) {
//                        mRootView.findViewById(R.id.rating_img).setVisibility(View.GONE);
//                        mRootView.findViewById(R.id.map_img).setVisibility(View.GONE);
//                        mCancel.setVisibility(View.GONE);
//                        mFaveImg.setVisibility(View.GONE);
//                        mCancelImg.setVisibility(View.GONE);
//                        mRejectImg.setVisibility(View.VISIBLE);
//                    }
//                }

            } else if (hire_status.equalsIgnoreCase("completed")) {
                mRootView.findViewById(R.id.rating_img).setVisibility(View.VISIBLE);
                mRootView.findViewById(R.id.map_img).setVisibility(View.GONE);
                mCancel.setVisibility(View.GONE);
                mFaveImg.setVisibility(View.VISIBLE);
                mCancelImg.setVisibility(View.GONE);
                mRejectImg.setVisibility(View.GONE);
                txtSendCoins.setVisibility(View.GONE);

            } else if (hire_status.equalsIgnoreCase("declined")) {
                mRootView.findViewById(R.id.rating_img).setVisibility(View.GONE);
                mRootView.findViewById(R.id.map_img).setVisibility(View.GONE);
                mCancel.setVisibility(View.GONE);
                mFaveImg.setVisibility(View.GONE);
                mCancelImg.setVisibility(View.GONE);
                mRejectImg.setVisibility(View.VISIBLE);
                txtSendCoins.setVisibility(View.GONE);
            }
        }

        if (hire_status.equalsIgnoreCase("completed")) {

            if (Utils.validateString(amount)) {
                mRootView.findViewById(R.id.amount_layout).setVisibility(View.VISIBLE);
                ((TextView) mRootView.findViewById(R.id.amount_txt)).setText(amount + "/-");
            }

            if (Utils.validateString(discount)) {
                mRootView.findViewById(R.id.discount_layout).setVisibility(View.VISIBLE);
                ((TextView) mRootView.findViewById(R.id.discount_txt)).setText(discount + "%");

            }

            if (Utils.validateString(credit)) {
                mRootView.findViewById(R.id.credit_layout).setVisibility(View.VISIBLE);
                ((TextView) mRootView.findViewById(R.id.credit_txt)).setText(credit);

            }

            if (Utils.validateString(total)) {
                mRootView.findViewById(R.id.total_paid_layout).setVisibility(View.VISIBLE);
                ((TextView) mRootView.findViewById(R.id.total_paid_txt)).setText(total);

            }

            if (Utils.validateString(like_value)) {
                if (like_value.equalsIgnoreCase("1")) {
                    mFaveImg.setImageResource(R.drawable.icon_fav);
                    showingFirst = true;
                    like_value = "0";
                } else {
                    mFaveImg.setImageResource(R.drawable.icon_nonfav);
                    showingFirst = false;
                    like_value = "1";
                }
            }

        }

    }

    // Add Coins View
    private void initview_Coins() {

//        Utils.storeString(mSharedPreferences, Utils.NOTI_COINS, "");

        edtCoins = (EditText) mRootView.findViewById(R.id.edtCoins);
        mRootView.findViewById(R.id.btnConfirm).setOnClickListener(this);
        mRootView.findViewById(R.id.push_backBtn).setOnClickListener(this);

        if (getArguments() != null) {
            String result = getArguments().getString("resposne");
            JSONObject jObj = null;
            try {
                jObj = new JSONObject(result.toString());
                if (!jObj.isNull("data")) {
                    JSONObject data_jobj = jObj.getJSONObject("data");
                    status_id = data_jobj.getString("id");
                    coin_text = data_jobj.getString("coin_text");
//                    Utils.storeString(mSharedPreferences, Utils.NOTI_COINS, coin_text);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

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

    public boolean fieldValidation() {
        boolean flag = true;
        if (!Utils.validateString(edtCoins.getText().toString())) {
            flag = false;
            Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), "Please enter number of coins.");
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
//                        getActivity().getSupportFragmentManager().popBackStack();
                        MyHiringsFragment myHiringsFragment = new MyHiringsFragment();
                        Bundle bundle1 = new Bundle();
                        bundle1.putString("Start", "Start");
                        myHiringsFragment.setArguments(bundle1);
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, myHiringsFragment).commit();
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

    // Advertise View
    private void initview_Advertise() {

        Utils.storeString(mSharedPreferences, Utils.NOTI_HIRE_STATUS, "");
//        Utils.storeString(mSharedPreferences, Utils.NOTI_ADVERTISE, "");

        SlidingMenuFragment.selectMenu(7);
        adve_details_img = (ImageView) mRootView.findViewById(R.id.adve_details_img);
        advr_details_more_desc = (TextView) mRootView.findViewById(R.id.advr_details_more_desc);
        mRootView.findViewById(R.id.push_backBtn).setOnClickListener(this);

        if (getArguments() != null) {
            String result = getArguments().getString("resposne");
            JSONObject jObj = null;
            try {
                jObj = new JSONObject(result.toString());
                if (!jObj.isNull("data")) {
                    JSONObject data_jobj = jObj.getJSONObject("data");

                    adv_id = data_jobj.getString("id");
                    adv_banner_id = data_jobj.getString("banner_id");
                    adv_description = data_jobj.getString("description");
                    adv_banner_name = data_jobj.getString("banner_name");
                    adv_banner_path = data_jobj.getString("banner_path");
                    adv_title = data_jobj.getString("title");

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        if (Utils.validateString(adv_banner_path) || Utils.validateString(adv_banner_id)) {
            mRootView.findViewById(R.id.push_title_back).setVisibility(View.VISIBLE);
        }

        if (Utils.validateString(adv_title)) {
            ((TextView) mRootView.findViewById(R.id.advr_details_more_title)).setText(adv_title);
        }

        if (Utils.validateString(adv_description)) {
//            advr_details_more_desc.setMovementMethod(LinkMovementMethod.getInstance());
//            advr_details_more_desc.setText(Html.fromHtml(adv_description));
//            advr_details_more_desc.setLinkTextColor(Color.parseColor("#0000ff"));
//            Linkify.addLinks(advr_details_more_desc, Linkify.WEB_URLS);
            setTextViewHTML(advr_details_more_desc, adv_description);
        }

        if (Utils.validateString(adv_banner_path)) {
            adve_details_img.setVisibility(View.VISIBLE);
            Picasso.with(getActivity()).load(Utils.IMAGE_URL + adv_banner_path).resize(mDeviceWidth, (int) (mDeviceWidth))./*centerCrop().*/error(R.drawable.placeholder).into(adve_details_img);
        } else {
            adve_details_img.setVisibility(View.GONE);
        }

        selectAdvertise(mSharedPreferences.getString(Utils.USER_ID, ""), adv_banner_id, "1");
    }

    protected void makeLinkClickable(SpannableStringBuilder strBuilder, final URLSpan span) {
        int start = strBuilder.getSpanStart(span);
        int end = strBuilder.getSpanEnd(span);
        int flags = strBuilder.getSpanFlags(span);
        ClickableSpan clickable = new ClickableSpan() {
            public void onClick(View view) {
                // Do something with span.getURL() to handle the link click...
                Logger.d(TAG, "" + span.getURL());
                if (Utils.validateString(span.getURL())) {
                    OpenWebviewFragment openWebviewFragment = new OpenWebviewFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString(Utils.URL, span.getURL());
                    openWebviewFragment.setArguments(bundle);
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, openWebviewFragment).commit();
                } else {
                    Toast.makeText(getActivity(), "Your link is not valid.", Toast.LENGTH_SHORT).show();
                }
            }
        };
        strBuilder.setSpan(clickable, start, end, flags);
        strBuilder.removeSpan(span);
    }

    protected void setTextViewHTML(TextView text, String html) {
        CharSequence sequence = Html.fromHtml(html);
        SpannableStringBuilder strBuilder = new SpannableStringBuilder(sequence);
        URLSpan[] urls = strBuilder.getSpans(0, sequence.length(), URLSpan.class);
        for (URLSpan span : urls) {
            makeLinkClickable(strBuilder, span);
        }
        text.setText(strBuilder);
        text.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void selectAdvertise(String users_id, String banner_id, String is_read) {
        if (Utils.checkInternetConnection(getActivity())) {
            SelectAdvertiseRequestTask getAdvertiseRequestTask = new SelectAdvertiseRequestTask(getActivity());
            getAdvertiseRequestTask.setAsyncCallListener(new AsyncCallListener() {

                @Override
                public void onResponseReceived(Object response) {
                    AdvertiseListModel advertiseModel = (AdvertiseListModel) response;

                    if (advertiseModel.success.equalsIgnoreCase("0")) {
                        Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), advertiseModel.message);
                    }
                }

                @Override
                public void onErrorReceived(String error) {
                    Toast.makeText(getActivity(), getString(R.string.try_again), Toast.LENGTH_SHORT).show();
                }
            });
            getAdvertiseRequestTask.execute(users_id, banner_id, is_read);
        } else {
            Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert),
                    getResources().getString(R.string.connection));
        }
    }


    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {

            case R.id.push_backBtn:
                Utils.notificationFlag = false;
                if (Utils.validateString(hire_status)) {
                    if (hire_status.equalsIgnoreCase("pending")) {
                        MyHiringsFragment myHiringsFragment = new MyHiringsFragment();
                        Bundle bundle1 = new Bundle();
                        bundle1.putString("Pending", "Pending");
                        myHiringsFragment.setArguments(bundle1);
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, myHiringsFragment).commit();

                    } else if (hire_status.equalsIgnoreCase("active")) {
                        MyHiringsFragment myHiringsFragment = new MyHiringsFragment();
                        Bundle bundle1 = new Bundle();
                        bundle1.putString("Active", "Active");
                        myHiringsFragment.setArguments(bundle1);
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, myHiringsFragment).commit();

                    } else if (hire_status.equalsIgnoreCase("start")) {
                        MyHiringsFragment myHiringsFragment = new MyHiringsFragment();
                        Bundle bundle1 = new Bundle();
                        bundle1.putString("Start", "Start");
                        myHiringsFragment.setArguments(bundle1);
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, myHiringsFragment).commit();

                    } else if (hire_status.equalsIgnoreCase("cancelled")) {
                        MyHiringsFragment myHiringsFragment = new MyHiringsFragment();
                        Bundle bundle1 = new Bundle();
                        bundle1.putString("Cancel", "Cancel");
                        myHiringsFragment.setArguments(bundle1);
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, myHiringsFragment).commit();

                    } else if (hire_status.equalsIgnoreCase("completed")) {
                        MyHiringsFragment myHiringsFragment = new MyHiringsFragment();
                        Bundle bundle1 = new Bundle();
                        bundle1.putString("Complete", "Complete");
                        myHiringsFragment.setArguments(bundle1);
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, myHiringsFragment).commit();

                    } else if (hire_status.equalsIgnoreCase("declined")) {
                        MyHiringsFragment myHiringsFragment = new MyHiringsFragment();
                        Bundle bundle1 = new Bundle();
                        bundle1.putString("declined", "declined");
                        myHiringsFragment.setArguments(bundle1);
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, myHiringsFragment).commit();
                    }
                } else if (Utils.validateString(coin_text)) {
                    SlidingMenuFragment.selectMenu(2);
                    MyHiringsFragment myHiringsFragment = new MyHiringsFragment();
                    Bundle bundle1 = new Bundle();
                    bundle1.putString("Start", "Start");
                    myHiringsFragment.setArguments(bundle1);
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, myHiringsFragment).commit();
                } else {
                    AdvertiseFragment mAdvertiseFragment = new AdvertiseFragment();
//                    Bundle bundle1 = new Bundle();
//                    bundle1.putString("Complete", "Complete");
//                    mAdvertiseFragment.setArguments(bundle1);
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, mAdvertiseFragment).commit();
                }

                break;

            case R.id.map_img:
            /*if(Utils.validateString(full_address) && Utils.validateString(handyman_lat) && Utils.validateString(handyman_lng)){
                String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?saddr=%f,%f&daddr=%f,%f (%s)", Double.parseDouble(handyman_lat), Double.parseDouble(handyman_lng),Double.parseDouble(latitude), Double.parseDouble(longitude), full_address);
//				String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?saddr=%f,%f&daddr=%s", Double.parseDouble(handyman_lat),Double.parseDouble(handyman_lng),full_address);
				Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
				startActivity(i);
			} *//*else {
                String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?saddr=%f,%f&daddr=%f,%f", Double.parseDouble(handyman_lat), Double.parseDouble(handyman_lng),Double.parseDouble(latitude), Double.parseDouble(longitude));
				Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
				startActivity(i);
			}*/

                StartJobMapFragment startJobMapFragment = new StartJobMapFragment();
                Bundle bundle1 = new Bundle();
                bundle1.putString(Utils.HIRE_HANDYMAN_ID, handyman_id);
                bundle1.putString(Utils.HIRE_HANDYMAN_NAME, handyman_name);
                bundle1.putString(Utils.latitude, latitude);
                bundle1.putString(Utils.longitude, longitude);
                bundle1.putString(Utils.CONTACT_PERSON_NAME, contact_person_name);
                startJobMapFragment.setArguments(bundle1);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, startJobMapFragment).addToBackStack(TAG).commit();
                break;

            case R.id.call_img:
                if (Utils.validateString(handyman_mobile)) {
                    Uri number = Uri.parse("tel:" + handyman_mobile);
                    Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
                    startActivity(callIntent);
                }

                break;

            case R.id.rating_img:
                getRateCheck(status_id, mSharedPreferences.getString(Utils.USER_ID, ""));
                break;

            case R.id.report_img:
                if (Utils.validateString(handyman_rating)) {
                    RegisterComplainFragment registerComplainFragment = new RegisterComplainFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString(Utils.HIRE_HANDYMAN_ID, handyman_id);
                    bundle.putString(Utils.HIRE_HANDYMAN_NAME, handyman_name);
                    bundle.putString(Utils.HIRE_HANDYMAN_EMAIL, handyman_email);
                    bundle.putString(Utils.HIRE_HANDYMAN_RATING, handyman_rating);
                    registerComplainFragment.setArguments(bundle);
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, registerComplainFragment).addToBackStack(TAG).commit();
                } else {
                    Toast.makeText(getActivity(), "Rating is " + handyman_rating, Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.fave_img:
                if (like_value.equalsIgnoreCase("1")) {
                    mFaveImg.setImageResource(R.drawable.icon_fav);
                    FavoriteHandyman(handyman_id, mSharedPreferences.getString(Utils.USER_ID, ""), like_value);
                    like_value = "0";
                } else {
                    mFaveImg.setImageResource(R.drawable.icon_nonfav);
                    FavoriteHandyman(handyman_id, mSharedPreferences.getString(Utils.USER_ID, ""), like_value);
                    like_value = "1";
                }

                break;

            case R.id.cancel_Button:

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Alert");
                builder.setCancelable(false);
                builder.setMessage("Credit points will be deducted while cancelling this order. Are you sure you want to cancel this order.");
                builder.setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                if (hire_status.equalsIgnoreCase("pending")) {
                                    Utils.storeString(mSharedPreferences, Utils.CANCEL, "CANCEL");
//                                    onChangeStatus(status_id, "cancelled", mSharedPreferences.getString(Utils.USER_ID, ""));
                                    onChangeStatus(status_id, "cancelled", "2");

                                } else if (hire_status.equalsIgnoreCase("active")) {
                                    Utils.storeString(mSharedPreferences, Utils.CANCEL_A, "CANCEL_A");
//                                    onChangeStatus(status_id, "cancelled", mSharedPreferences.getString(Utils.USER_ID, ""));
                                    onChangeStatus(status_id, "cancelled", "2");

                                } else if (hire_status.equalsIgnoreCase("start")) {
                                    Utils.storeString(mSharedPreferences, Utils.CANCEL_S, "CANCEL_S");
//                                    onChangeStatus(status_id, "cancelled", mSharedPreferences.getString(Utils.USER_ID, ""));
                                    onChangeStatus(status_id, "cancelled", "2");
                                }
                            }
                        });
                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (hire_status.equalsIgnoreCase("pending")) {
                            Utils.storeString(mSharedPreferences, Utils.CANCEL, "");
                        } else if (hire_status.equalsIgnoreCase("active")) {
                            Utils.storeString(mSharedPreferences, Utils.CANCEL_A, "");
                        } else if (hire_status.equalsIgnoreCase("start")) {
                            Utils.storeString(mSharedPreferences, Utils.CANCEL_S, "");
                        }
                    }

                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

                break;

            case R.id.txtSendCoins:
                getCoinsCheck(status_id, mSharedPreferences.getString(Utils.USER_ID, ""));
                break;

            case R.id.btnConfirm:
                if (getActivity().getCurrentFocus() != null) {
                    InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
                }

                if (fieldValidation()) {

                    AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
                    builder1.setTitle("Alert");
                    builder1.setCancelable(false);
                    builder1.setMessage("Are you sure you want to send coins for payment?");
                    builder1.setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    sendCoinsForPayment(status_id, mSharedPreferences.getString(Utils.USER_ID, ""), edtCoins.getText().toString().trim());
                                }
                            });
                    builder1.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                        }

                    });
                    AlertDialog alertDialog1 = builder1.create();
                    alertDialog1.show();
                }
                break;

        }
    }

    private void onChangeStatus(String id, String h_status, String service_updated_by) {

        if (Utils.checkInternetConnection(getActivity())) {
            GetHandymanChangeStatusRequestTask getHandymanChangeStatusRequestTask = new GetHandymanChangeStatusRequestTask(getActivity());
            getHandymanChangeStatusRequestTask.setAsyncCallListener(new AsyncCallListener() {

                @Override
                public void onResponseReceived(Object response) {
                    MyHiringsModel myHiringsModel = (MyHiringsModel) response;
                    if (myHiringsModel.success.equalsIgnoreCase("1")) {
                        Toast.makeText(getActivity(), myHiringsModel.message, Toast.LENGTH_SHORT).show();
//                            getActivity().getSupportFragmentManager().popBackStack();

                        if (hire_status.equalsIgnoreCase("pending")) {
                            MyHiringsFragment myHiringsFragment = new MyHiringsFragment();
                            Bundle bundle1 = new Bundle();
                            bundle1.putString("Pending", "Pending");
                            myHiringsFragment.setArguments(bundle1);
                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, myHiringsFragment).commit();

                        } else if (hire_status.equalsIgnoreCase("active")) {
                            MyHiringsFragment myHiringsFragment = new MyHiringsFragment();
                            Bundle bundle1 = new Bundle();
                            bundle1.putString("Active", "Active");
                            myHiringsFragment.setArguments(bundle1);
                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, myHiringsFragment).commit();

                        } else if (hire_status.equalsIgnoreCase("start")) {
                            MyHiringsFragment myHiringsFragment = new MyHiringsFragment();
                            Bundle bundle1 = new Bundle();
                            bundle1.putString("Start", "Start");
                            myHiringsFragment.setArguments(bundle1);
                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, myHiringsFragment).commit();

                        } else if (hire_status.equalsIgnoreCase("cancelled")) {
                            MyHiringsFragment myHiringsFragment = new MyHiringsFragment();
                            Bundle bundle1 = new Bundle();
                            bundle1.putString("Cancel", "Cancel");
                            myHiringsFragment.setArguments(bundle1);
                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, myHiringsFragment).commit();

                        } else if (hire_status.equalsIgnoreCase("completed")) {
                            MyHiringsFragment myHiringsFragment = new MyHiringsFragment();
                            Bundle bundle1 = new Bundle();
                            bundle1.putString("Complete", "Complete");
                            myHiringsFragment.setArguments(bundle1);
                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, myHiringsFragment).commit();

                        }

                    } else if (myHiringsModel.success.equalsIgnoreCase("0")) {
                        //Toast.makeText(getActivity(), myHiringsModel.message , Toast.LENGTH_SHORT).show();
                        Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), myHiringsModel.message);
                    }
                }

                @Override
                public void onErrorReceived(String error) {
                    Toast.makeText(getActivity(), getString(R.string.try_again), Toast.LENGTH_SHORT).show();
                }
            });
            getHandymanChangeStatusRequestTask.execute(id, h_status, service_updated_by);
        } else {
            Toast.makeText(getActivity(), getString(R.string.connection), Toast.LENGTH_SHORT).show();
        }
    }

    private void getCoinsCheck(String hire_id, String client_id) {
        if (Utils.checkInternetConnection(getActivity())) {
            CheckAddCoinsRequestTask checkAddCoinsRequestTask = new CheckAddCoinsRequestTask(getActivity());
            checkAddCoinsRequestTask.setAsyncCallListener(new AsyncCallListener() {

                @SuppressWarnings("unchecked")
                @Override
                public void onResponseReceived(Object response) {

                    HireHandymanModel hireHandymanModel = (HireHandymanModel) response;
                    if (hireHandymanModel.success.equalsIgnoreCase("1")) {
//                        Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), hireHandymanModel.message);

                        if(Utils.validateString(hireHandymanModel.debit) && !hireHandymanModel.debit.equalsIgnoreCase("0")){

                            CoinsAddForPaymentFragment coinsAddForPaymentFragment = new CoinsAddForPaymentFragment();
                            Bundle bundleCoins = new Bundle();
                            bundleCoins.putString(Utils.HIRE_HANDYMAN_STATUS_ID, status_id);
                            bundleCoins.putString(Utils.HIRE_DEBIT_COINS,hireHandymanModel.debit);
                            coinsAddForPaymentFragment.setArguments(bundleCoins);
                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, coinsAddForPaymentFragment).addToBackStack(TAG).commit();
                        }

                    } else if (hireHandymanModel.success.equalsIgnoreCase("0")) {

                        CoinsAddForPaymentFragment coinsAddForPaymentFragment = new CoinsAddForPaymentFragment();
                        Bundle bundleCoins = new Bundle();
                        bundleCoins.putString(Utils.HIRE_HANDYMAN_STATUS_ID, status_id);
                        coinsAddForPaymentFragment.setArguments(bundleCoins);
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, coinsAddForPaymentFragment).addToBackStack(TAG).commit();

                    }

                }

                @Override
                public void onErrorReceived(String error) {
                    Toast.makeText(getActivity(), getString(R.string.try_again), Toast.LENGTH_SHORT).show();
                }
            });
            checkAddCoinsRequestTask.execute(hire_id, client_id);
        } else {
            Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), getResources().getString(R.string.connection));
        }
    }

    private void getRateCheck(String hire_id, String client_id) {
        if (Utils.checkInternetConnection(getActivity())) {
            CheckRateHandymanRequestTask checkRateHandymanRequestTask = new CheckRateHandymanRequestTask(getActivity());
            checkRateHandymanRequestTask.setAsyncCallListener(new AsyncCallListener() {

                @SuppressWarnings("unchecked")
                @Override
                public void onResponseReceived(Object response) {

                    HireHandymanModel hireHandymanModel = (HireHandymanModel) response;
                    if (hireHandymanModel.success.equalsIgnoreCase("1")) {
                        Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert),
                                hireHandymanModel.message);

                    } else if (hireHandymanModel.success.equalsIgnoreCase("0")) {
                        RatingFragment ratingFragment = new RatingFragment();
                        Bundle bundle1 = new Bundle();
                        bundle1.putString(Utils.HIRE_HANDYMAN_ID, handyman_id);
                        bundle1.putString(Utils.HIRE_HANDYMAN_STATUS_ID, status_id);
                        bundle1.putString(Utils.HIRE_HANDYMAN_NAME, handyman_name);
                        bundle1.putString(Utils.HIRE_HANDYMAN_EMAIL, handyman_email);
                        if (Utils.validateString(handyman_rating))
                            bundle1.putString(Utils.HIRE_HANDYMAN_RATING, handyman_rating);
                        ratingFragment.setArguments(bundle1);
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, ratingFragment).addToBackStack(TAG).commit();
                    }

                }

                @Override
                public void onErrorReceived(String error) {
                    Toast.makeText(getActivity(), getString(R.string.try_again), Toast.LENGTH_SHORT).show();
                }
            });
            checkRateHandymanRequestTask.execute(hire_id, client_id);
        } else {
            Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert),
                    getResources().getString(R.string.connection));
        }
    }

    private void FavoriteHandyman(String handyman_id, String client_id, final String user_like) {
        if (Utils.checkInternetConnection(getActivity())) {
            FavouriteHandymanRequestTask favouriteHandymanRequestTask = new FavouriteHandymanRequestTask(getActivity());
            favouriteHandymanRequestTask.setAsyncCallListener(new AsyncCallListener() {

                @SuppressWarnings("unchecked")
                @Override
                public void onResponseReceived(Object response) {
                    HireHandymanModel hireHandymanModel = (HireHandymanModel) response;
                    if (hireHandymanModel.success.equalsIgnoreCase("1")) {

                    } else if (hireHandymanModel.success.equalsIgnoreCase("0")) {
                        Toast.makeText(getActivity(), hireHandymanModel.message, Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onErrorReceived(String error) {
                    Toast.makeText(getActivity(), getString(R.string.try_again), Toast.LENGTH_SHORT).show();
                }
            });
            favouriteHandymanRequestTask.execute(handyman_id, client_id, user_like);
        } else {
            Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), getResources().getString(R.string.connection));
        }
    }

}
