package com.handyman.fragment;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.handyman.MainActivity;
import com.handyman.R;
import com.handyman.SplashActivity;
import com.handyman.logger.Logger;
import com.handyman.model.HireHandymanModel;
import com.handyman.model.MyHiringsModel;
import com.handyman.service.AsyncCallListener;
import com.handyman.service.CheckAddCoinsRequestTask;
import com.handyman.service.CheckRateHandymanRequestTask;
import com.handyman.service.FavouriteHandymanRequestTask;
import com.handyman.service.GetHandymanChangeStatusRequestTask;
import com.handyman.service.Utils;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

public class HandymanCustomerHireProfileFragment extends BaseFragment implements OnClickListener {

    private static String TAG = "HandymanCustomerHireProfileFragment";

    private SharedPreferences mSharedPreferences;

    MyHiringsModel mPendingHirings = new MyHiringsModel();
    MyHiringsModel mCancelHirings = new MyHiringsModel();
    MyHiringsModel mCompleteHirings = new MyHiringsModel();
    MyHiringsModel mRejectedHirings = new MyHiringsModel();
    MyHiringsModel mAcceptedHirings = new MyHiringsModel();
    MyHiringsModel mStartedHirings = new MyHiringsModel();
    MyHiringsModel mAllHirings = new MyHiringsModel();

    String category_id, status, status_id, handyman_id = "", handyman_name = "", handyman_email = "", h_rating = "", handyman_rating = "", job_Description = "", /*closing_status,*/
            mobile_no = "", full_address = "", latitude = "0.0", longitude = "0.0", handyman_lat = "0.0", handyman_lng = "0.0", contact_person_name = "",
            appointment_date = "", appointment_time = "";

    Fragment fr;
    View mRootView;
    ImageView mHanfymanprofileImg, mFaveImg, mCancelImg, mRejectImg;
    private int mDeviceWidth = 480;
    TextView mCancel, txtSendCoins;
    String like_value;
    boolean showingFirst = false;
    SimpleDateFormat dfDate;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_hm_profile, container, false);
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
        return mRootView;
    }

    private void initview() {
        mSharedPreferences = getActivity().getSharedPreferences(Utils.PREF_NAME, Context.MODE_PRIVATE);
//		((MainActivity) getActivity()).setTitleText(getString(R.string.home_searvice_at_home), "", View.GONE, View.VISIBLE, View.GONE);
        getActivity().findViewById(R.id.title).setVisibility(View.GONE);
        getActivity().findViewById(R.id.txtUrl).setVisibility(View.GONE);

        mActivity.getSlidingMenu().setMode(SlidingMenu.LEFT);
        mActivity.getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);

        dfDate = new SimpleDateFormat("dd/MM/yyyy");

        mHanfymanprofileImg = (ImageView) mRootView.findViewById(R.id.hanfyman_profile_img);

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
            mPendingHirings = (MyHiringsModel) getArguments().get(Utils.HANDYMAN_HIRE_PENDING_DETAILS);
            mAcceptedHirings = (MyHiringsModel) getArguments().get(Utils.HANDYMAN_HIRE_ACCEPT_DETAILS);
            mStartedHirings = (MyHiringsModel) getArguments().get(Utils.HANDYMAN_HIRE_START_DETAILS);
            mCancelHirings = (MyHiringsModel) getArguments().get(Utils.HANDYMAN_HIRE_CANCEL_DETAILS);
            mCompleteHirings = (MyHiringsModel) getArguments().get(Utils.HANDYMAN_HIRE_COMPLETE_DETAILS);
            mRejectedHirings = (MyHiringsModel) getArguments().get(Utils.HANDYMAN_HIRE_REJECT_DETAILS);
            mAllHirings = (MyHiringsModel) getArguments().get(Utils.HANDYMAN_HIRE_ALL_DETAILS);

            if (mPendingHirings != null) {
                ((MainActivity) getActivity()).setTitleText("", mPendingHirings.handyman_name, "", View.VISIBLE, View.GONE, View.GONE, View.GONE, View.GONE);
                ((TextView) mRootView.findViewById(R.id.handyman_name_profile_txt)).setText(mPendingHirings.handyman_name);

                if (Utils.validateString(mPendingHirings.handyman_img)) {
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
                            .load(Utils.IMAGE_URL + mPendingHirings.handyman_img)
                            .placeholder(R.drawable.avtar_images)
                            .error(R.drawable.avtar_images)
                            .transform(transformation)
                            .centerCrop()
                            .resize(mDeviceWidth, (int) (mDeviceWidth))
                            .into(mHanfymanprofileImg);
                }

                if (Utils.validateString(mPendingHirings.order_id)) {
                    mRootView.findViewById(R.id.hire_customer_order_id_textview).setVisibility(View.VISIBLE);
                    mRootView.findViewById(R.id.hire_customer_order_id).setVisibility(View.VISIBLE);
                    ((TextView) mRootView.findViewById(R.id.hire_customer_order_id)).setText(mPendingHirings.order_id);
                }

                if (Utils.validateString(mPendingHirings.created_date)) {
                    mRootView.findViewById(R.id.hire_customer_order_date_textview).setVisibility(View.VISIBLE);
                    mRootView.findViewById(R.id.hire_customer_order_date).setVisibility(View.VISIBLE);
                    ((TextView) mRootView.findViewById(R.id.hire_customer_order_date)).setText(mPendingHirings.created_date);
                }

                if (Utils.validateString(mPendingHirings.job_description)) {
                    job_Description = mPendingHirings.job_description;
                    mRootView.findViewById(R.id.hire_customer_job_description_textview).setVisibility(View.VISIBLE);
                    mRootView.findViewById(R.id.hire_customer_job_description).setVisibility(View.VISIBLE);
                    ((TextView) mRootView.findViewById(R.id.hire_customer_job_description)).setText(job_Description);
                }


                if (Utils.validateString(mPendingHirings.appointment_date)) {
                    mRootView.findViewById(R.id.hire_customer_date_textview).setVisibility(View.VISIBLE);
                    mRootView.findViewById(R.id.hire_customer_date).setVisibility(View.VISIBLE);
                    appointment_date = mPendingHirings.appointment_date;
                    ((TextView) mRootView.findViewById(R.id.hire_customer_date)).setText(appointment_date);
                }


                if (Utils.validateString(mPendingHirings.appointment_time)) {
                    mRootView.findViewById(R.id.hire_customer_time_textview).setVisibility(View.VISIBLE);
                    mRootView.findViewById(R.id.hire_customer_time).setVisibility(View.VISIBLE);
                    String time = mPendingHirings.appointment_time;
                    appointment_time = time.substring(0, 8).trim();
                    ((TextView) mRootView.findViewById(R.id.hire_customer_time)).setText(time.substring(0, 2) + ":" + time.substring(3, 5) + "" + time.substring(8, 11));
                }


                if (Utils.validateString(mPendingHirings.contact_person)) {
                    mRootView.findViewById(R.id.hire_customer_contact_person_textview).setVisibility(View.VISIBLE);
                    mRootView.findViewById(R.id.hire_customer_contact_person).setVisibility(View.VISIBLE);
                    ((TextView) mRootView.findViewById(R.id.hire_customer_contact_person)).setText(mPendingHirings.contact_person);
                }

                if (Utils.validateString(mPendingHirings.contact_no)) {
                    mobile_no = mPendingHirings.hamdyman_mobile;
                    mRootView.findViewById(R.id.hire_customer_contact_no_textview).setVisibility(View.VISIBLE);
                    mRootView.findViewById(R.id.hire_customer_contact_no).setVisibility(View.VISIBLE);
                    ((TextView) mRootView.findViewById(R.id.hire_customer_contact_no)).setText(mPendingHirings.contact_no);
                }

                if (Utils.validateString(mPendingHirings.getAddress())) {
                    mRootView.findViewById(R.id.hire_customer_address_textview).setVisibility(View.VISIBLE);
                    mRootView.findViewById(R.id.hire_customer_address).setVisibility(View.VISIBLE);
                    String address = "", street = "", landmark = "", city = "", state = "", pincode = "", floor = "", apartment = "";

                    if (Utils.validateString(mPendingHirings.getFloor()))
                        floor = mPendingHirings.getFloor() + ", ";
                    if (Utils.validateString(mPendingHirings.getApartment()))
                        apartment = mPendingHirings.getApartment() + ", ";
                    if (Utils.validateString(mPendingHirings.getAddress()))
                        address = mPendingHirings.getAddress();
//					if(Utils.validateString(mPendingHirings.getStreet()))
//						street = mPendingHirings.getStreet();
//					if(Utils.validateString(mPendingHirings.getLandmark()))
//						landmark = mPendingHirings.getLandmark();
//					if(Utils.validateString(mPendingHirings.getCity_name()))
//						city = mPendingHirings.getCity_name();
//					if(Utils.validateString(mPendingHirings.getState_name()))
//						state = mPendingHirings.getState_name();
//					if(Utils.validateString(mPendingHirings.getPincode()))
//						pincode = mPendingHirings.getPincode();
//					((TextView) mRootView.findViewById(R.id.hire_customer_address)).setText(floor + "\n" +address + "\n" + street + "\n" + landmark + "\n" + city + " - " + pincode);
                    ((TextView) mRootView.findViewById(R.id.hire_customer_address)).setText(floor + apartment + address);
                }

                if (Utils.validateString(mPendingHirings.comment)) {
                    mRootView.findViewById(R.id.hire_customer_requirment_textview).setVisibility(View.VISIBLE);
                    mRootView.findViewById(R.id.hire_customer_requirment).setVisibility(View.VISIBLE);
                    ((TextView) mRootView.findViewById(R.id.hire_customer_requirment)).setText(mPendingHirings.comment);
                }

                if (Utils.validateString(mPendingHirings.hire_status)) {
                    status = mPendingHirings.hire_status;
                }

                if (Utils.validateString(mPendingHirings.id)) {
                    status_id = mPendingHirings.id;
                }

                if (Utils.validateString(mPendingHirings.handyman_id)) {
                    handyman_id = mPendingHirings.handyman_id;
                }

                if (Utils.validateString(mPendingHirings.handyman_name)) {
                    handyman_name = mPendingHirings.handyman_name;
                }

                if (Utils.validateString(mPendingHirings.email)) {
                    handyman_email = mPendingHirings.email;
                }

                if (Utils.validateString(mPendingHirings.rating)) {
                    h_rating = mPendingHirings.rating;
                }

                if (Utils.validateString(mPendingHirings.handyman_rate)) {
                    handyman_rating = mPendingHirings.handyman_rate;
                }

//                if(Utils.validateString(mPendingHirings.getLike())){
//                    like_value = mProfileHandymanModelsList.get(0).getLike().toString();
//                    if(like_value.equalsIgnoreCase("1")){
//                        mFaveImg.setImageResource(R.drawable.icon_fav);
//                        showingFirst = true;
//                        like_value = "0";
//                    } else {
//                        mFaveImg.setImageResource(R.drawable.icon_nonfav);
//                        showingFirst = false;
//                        like_value = "1";
//                    }
//                }

//				if(Utils.validateString(mPendingHirings.closing_status)){
//					closing_status = mPendingHirings.closing_status;
//				}


//				Utils.storeString(mSharedPreferences, Utils.CLIENT_ID, mPendingHirings.client_id);

            }

            if (mAcceptedHirings != null) {
                ((MainActivity) getActivity()).setTitleText("", mAcceptedHirings.handyman_name, "", View.VISIBLE, View.GONE, View.GONE, View.GONE, View.GONE);
                ((TextView) mRootView.findViewById(R.id.handyman_name_profile_txt)).setText(mAcceptedHirings.handyman_name);

                if (Utils.validateString(mAcceptedHirings.handyman_img)) {
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
                            .load(Utils.IMAGE_URL + mAcceptedHirings.handyman_img)
                            .placeholder(R.drawable.avtar_images)
                            .error(R.drawable.avtar_images)
                            .transform(transformation)
                            .centerCrop()
                            .resize(mDeviceWidth, (int) (mDeviceWidth))
                            .into(mHanfymanprofileImg);
                }

                if (Utils.validateString(mAcceptedHirings.order_id)) {
                    mRootView.findViewById(R.id.hire_customer_order_id_textview).setVisibility(View.VISIBLE);
                    mRootView.findViewById(R.id.hire_customer_order_id).setVisibility(View.VISIBLE);
                    ((TextView) mRootView.findViewById(R.id.hire_customer_order_id)).setText(mAcceptedHirings.order_id);
                }

                if (Utils.validateString(mAcceptedHirings.created_date)) {
                    mRootView.findViewById(R.id.hire_customer_order_date_textview).setVisibility(View.VISIBLE);
                    mRootView.findViewById(R.id.hire_customer_order_date).setVisibility(View.VISIBLE);
                    ((TextView) mRootView.findViewById(R.id.hire_customer_order_date)).setText(mAcceptedHirings.created_date);
                }


                if (Utils.validateString(mAcceptedHirings.job_description)) {
                    job_Description = mAcceptedHirings.job_description;
                    mRootView.findViewById(R.id.hire_customer_job_description_textview).setVisibility(View.VISIBLE);
                    mRootView.findViewById(R.id.hire_customer_job_description).setVisibility(View.VISIBLE);
                    ((TextView) mRootView.findViewById(R.id.hire_customer_job_description)).setText(job_Description);
                }


                if (Utils.validateString(mAcceptedHirings.appointment_date)) {
                    mRootView.findViewById(R.id.hire_customer_date_textview).setVisibility(View.VISIBLE);
                    mRootView.findViewById(R.id.hire_customer_date).setVisibility(View.VISIBLE);
                    appointment_date = mAcceptedHirings.appointment_date;
                    ((TextView) mRootView.findViewById(R.id.hire_customer_date)).setText(appointment_date);
                }


                if (Utils.validateString(mAcceptedHirings.appointment_time)) {
                    mRootView.findViewById(R.id.hire_customer_time_textview).setVisibility(View.VISIBLE);
                    mRootView.findViewById(R.id.hire_customer_time).setVisibility(View.VISIBLE);
                    String time = mAcceptedHirings.appointment_time;
                    appointment_time = time.substring(0, 8).trim();
                    ((TextView) mRootView.findViewById(R.id.hire_customer_time)).setText(time.substring(0, 2) + ":" + time.substring(3, 5) + "" + time.substring(8, 11));
                }


                if (Utils.validateString(mAcceptedHirings.contact_person)) {
                    mRootView.findViewById(R.id.hire_customer_contact_person_textview).setVisibility(View.VISIBLE);
                    mRootView.findViewById(R.id.hire_customer_contact_person).setVisibility(View.VISIBLE);
                    ((TextView) mRootView.findViewById(R.id.hire_customer_contact_person)).setText(mAcceptedHirings.contact_person);
                }

                if (Utils.validateString(mAcceptedHirings.contact_no)) {
                    mobile_no = mAcceptedHirings.hamdyman_mobile;
                    mRootView.findViewById(R.id.hire_customer_contact_no_textview).setVisibility(View.VISIBLE);
                    mRootView.findViewById(R.id.hire_customer_contact_no).setVisibility(View.VISIBLE);
                    ((TextView) mRootView.findViewById(R.id.hire_customer_contact_no)).setText(mAcceptedHirings.contact_no);
                }

                if (Utils.validateString(mAcceptedHirings.getAddress())) {
                    mRootView.findViewById(R.id.hire_customer_address_textview).setVisibility(View.VISIBLE);
                    mRootView.findViewById(R.id.hire_customer_address).setVisibility(View.VISIBLE);
                    String address = "", street = "", landmark = "", city = "", state = "", pincode = "", floor = "", apartment = "";

                    if (Utils.validateString(mAcceptedHirings.getFloor()))
                        floor = mAcceptedHirings.getFloor() + ", ";
                    if (Utils.validateString(mAcceptedHirings.getApartment()))
                        apartment = mAcceptedHirings.getApartment() + ", ";
                    if (Utils.validateString(mAcceptedHirings.getAddress()))
                        address = mAcceptedHirings.getAddress();
//					if(Utils.validateString(mAcceptedHirings.getStreet()))
//						street = mAcceptedHirings.getStreet();
//					if(Utils.validateString(mAcceptedHirings.getLandmark()))
//						landmark = mAcceptedHirings.getLandmark();
//					if(Utils.validateString(mAcceptedHirings.getCity_name()))
//						city = mAcceptedHirings.getCity_name();
//					if(Utils.validateString(mAcceptedHirings.getState_name()))
//						state = mAcceptedHirings.getState_name();
//					if(Utils.validateString(mAcceptedHirings.getPincode()))
//						pincode = mAcceptedHirings.getPincode();
//					((TextView) mRootView.findViewById(R.id.hire_customer_address)).setText(floor + "\n" +address + "\n" + street + "\n" + landmark + "\n" + city + " - " + pincode);
                    ((TextView) mRootView.findViewById(R.id.hire_customer_address)).setText(floor + apartment + address);
                }

                if (Utils.validateString(mAcceptedHirings.comment)) {
                    mRootView.findViewById(R.id.hire_customer_requirment_textview).setVisibility(View.VISIBLE);
                    mRootView.findViewById(R.id.hire_customer_requirment).setVisibility(View.VISIBLE);
                    ((TextView) mRootView.findViewById(R.id.hire_customer_requirment)).setText(mAcceptedHirings.comment);
                }

                if (Utils.validateString(mAcceptedHirings.hire_status)) {
                    status = mAcceptedHirings.hire_status;
                }

                if (Utils.validateString(mAcceptedHirings.id)) {
                    status_id = mAcceptedHirings.id;
                }

                if (Utils.validateString(mAcceptedHirings.handyman_id)) {
                    handyman_id = mAcceptedHirings.handyman_id;
                }

                if (Utils.validateString(mAcceptedHirings.handyman_name)) {
                    handyman_name = mAcceptedHirings.handyman_name;
                }

                if (Utils.validateString(mAcceptedHirings.email)) {
                    handyman_email = mAcceptedHirings.email;
                }

                if (Utils.validateString(mAcceptedHirings.rating)) {
                    h_rating = mAcceptedHirings.rating;
                }

                if (Utils.validateString(mAcceptedHirings.handyman_rate)) {
                    handyman_rating = mAcceptedHirings.handyman_rate;
                }

//				if(Utils.validateString(mAcceptedHirings.closing_status)){
//					closing_status = mAcceptedHirings.closing_status;
//				}

//				Utils.storeString(mSharedPreferences, Utils.CLIENT_ID, mAcceptedHirings.client_id);

            }

            if (mStartedHirings != null) {
                ((MainActivity) getActivity()).setTitleText("", mStartedHirings.handyman_name, "", View.VISIBLE, View.GONE, View.GONE, View.GONE, View.GONE);
                ((TextView) mRootView.findViewById(R.id.handyman_name_profile_txt)).setText(mStartedHirings.handyman_name);

                if (Utils.validateString(mStartedHirings.handyman_img)) {
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
                            .load(Utils.IMAGE_URL + mStartedHirings.handyman_img)
                            .placeholder(R.drawable.avtar_images)
                            .error(R.drawable.avtar_images)
                            .transform(transformation)
                            .centerCrop()
                            .resize(mDeviceWidth, (int) (mDeviceWidth))
                            .into(mHanfymanprofileImg);
                }

                if (Utils.validateString(mStartedHirings.order_id)) {
                    mRootView.findViewById(R.id.hire_customer_order_id_textview).setVisibility(View.VISIBLE);
                    mRootView.findViewById(R.id.hire_customer_order_id).setVisibility(View.VISIBLE);
                    ((TextView) mRootView.findViewById(R.id.hire_customer_order_id)).setText(mStartedHirings.order_id);
                }

                if (Utils.validateString(mStartedHirings.created_date)) {
                    mRootView.findViewById(R.id.hire_customer_order_date_textview).setVisibility(View.VISIBLE);
                    mRootView.findViewById(R.id.hire_customer_order_date).setVisibility(View.VISIBLE);
                    ((TextView) mRootView.findViewById(R.id.hire_customer_order_date)).setText(mStartedHirings.created_date);
                }

                if (Utils.validateString(mStartedHirings.job_description)) {
                    job_Description = mStartedHirings.job_description;
                    mRootView.findViewById(R.id.hire_customer_job_description_textview).setVisibility(View.VISIBLE);
                    mRootView.findViewById(R.id.hire_customer_job_description).setVisibility(View.VISIBLE);
                    ((TextView) mRootView.findViewById(R.id.hire_customer_job_description)).setText(job_Description);
                }


                if (Utils.validateString(mStartedHirings.appointment_date)) {
                    mRootView.findViewById(R.id.hire_customer_date_textview).setVisibility(View.VISIBLE);
                    mRootView.findViewById(R.id.hire_customer_date).setVisibility(View.VISIBLE);
                    appointment_date = mStartedHirings.appointment_date;
                    ((TextView) mRootView.findViewById(R.id.hire_customer_date)).setText(appointment_date);
                }


                if (Utils.validateString(mStartedHirings.appointment_time)) {
                    mRootView.findViewById(R.id.hire_customer_time_textview).setVisibility(View.VISIBLE);
                    mRootView.findViewById(R.id.hire_customer_time).setVisibility(View.VISIBLE);
                    String time = mStartedHirings.appointment_time;
                    appointment_time = time.substring(0, 8).trim();
                    ((TextView) mRootView.findViewById(R.id.hire_customer_time)).setText(time.substring(0, 2) + ":" + time.substring(3, 5) + "" + time.substring(8, 11));
                }


                if (Utils.validateString(mStartedHirings.contact_person)) {
                    mRootView.findViewById(R.id.hire_customer_contact_person_textview).setVisibility(View.VISIBLE);
                    mRootView.findViewById(R.id.hire_customer_contact_person).setVisibility(View.VISIBLE);
                    ((TextView) mRootView.findViewById(R.id.hire_customer_contact_person)).setText(mStartedHirings.contact_person);
                    contact_person_name = mStartedHirings.contact_person;
                }

                if (Utils.validateString(mStartedHirings.contact_no)) {
                    mobile_no = mStartedHirings.hamdyman_mobile;
                    mRootView.findViewById(R.id.hire_customer_contact_no_textview).setVisibility(View.VISIBLE);
                    mRootView.findViewById(R.id.hire_customer_contact_no).setVisibility(View.VISIBLE);
                    ((TextView) mRootView.findViewById(R.id.hire_customer_contact_no)).setText(mStartedHirings.contact_no);
                }

                if (Utils.validateString(mStartedHirings.getAddress())) {
                    mRootView.findViewById(R.id.hire_customer_address_textview).setVisibility(View.VISIBLE);
                    mRootView.findViewById(R.id.hire_customer_address).setVisibility(View.VISIBLE);
                    String address = "", street = "", landmark = "", city = "", state = "", pincode = "", floor = "", apartment = "";
                    if (Utils.validateString(mStartedHirings.getFloor()))
                        floor = mStartedHirings.getFloor() + ", ";
                    if (Utils.validateString(mStartedHirings.getApartment()))
                        apartment = mStartedHirings.getApartment() + ", ";
                    if (Utils.validateString(mStartedHirings.getAddress()))
                        address = mStartedHirings.getAddress();
//					if(Utils.validateString(mStartedHirings.getStreet()))
//						street = mStartedHirings.getStreet();
//					if(Utils.validateString(mStartedHirings.getLandmark()))
//						landmark = mStartedHirings.getLandmark();
//					if(Utils.validateString(mStartedHirings.getCity_name()))
//						city = mStartedHirings.getCity_name();
//					if(Utils.validateString(mStartedHirings.getState_name()))
//						state = mStartedHirings.getState_name();
//					if(Utils.validateString(mStartedHirings.getPincode()))
//						pincode = mStartedHirings.getPincode();
//					((TextView) mRootView.findViewById(R.id.hire_customer_address)).setText(address + "\n" + street + "\n" + landmark + "\n" + city + " - " + pincode);
                    ((TextView) mRootView.findViewById(R.id.hire_customer_address)).setText(floor + apartment + address);
                    full_address = address;
                }

                if (Utils.validateString(mStartedHirings.comment)) {
                    mRootView.findViewById(R.id.hire_customer_requirment_textview).setVisibility(View.VISIBLE);
                    mRootView.findViewById(R.id.hire_customer_requirment).setVisibility(View.VISIBLE);
                    ((TextView) mRootView.findViewById(R.id.hire_customer_requirment)).setText(mStartedHirings.comment);
                }

                if (Utils.validateString(mStartedHirings.hire_status)) {
                    status = mStartedHirings.hire_status;
                }

                if (Utils.validateString(mStartedHirings.id)) {
                    status_id = mStartedHirings.id;
                }

                if (Utils.validateString(mStartedHirings.handyman_id)) {
                    handyman_id = mStartedHirings.handyman_id;
                }

                if (Utils.validateString(mStartedHirings.handyman_name)) {
                    handyman_name = mStartedHirings.handyman_name;
                }

                if (Utils.validateString(mStartedHirings.email)) {
                    handyman_email = mStartedHirings.email;
                }

                if (Utils.validateString(mStartedHirings.rating)) {
                    h_rating = mStartedHirings.rating;
                }

                if (Utils.validateString(mStartedHirings.handyman_rate)) {
                    handyman_rating = mStartedHirings.handyman_rate;
                }

                if (Utils.validateString(mStartedHirings.lat)) {
                    latitude = mStartedHirings.lat;
                }

                if (Utils.validateString(mStartedHirings.lng)) {
                    longitude = mStartedHirings.lng;
                }

                if (Utils.validateString(mStartedHirings.h_lat)) {
                    handyman_lat = mStartedHirings.h_lat;
                }

                if (Utils.validateString(mStartedHirings.h_lng)) {
                    handyman_lng = mStartedHirings.h_lng;
                }

//				if(Utils.validateString(mStartedHirings.closing_status)){
//					closing_status = mStartedHirings.closing_status;
//				}

//				Utils.storeString(mSharedPreferences, Utils.CLIENT_ID, mStartedHirings.client_id);

            }

            if (mCancelHirings != null) {
                ((MainActivity) getActivity()).setTitleText("", mCancelHirings.handyman_name, "", View.VISIBLE, View.GONE, View.GONE, View.GONE, View.GONE);
                ((TextView) mRootView.findViewById(R.id.handyman_name_profile_txt)).setText(mCancelHirings.handyman_name);

                if (Utils.validateString(mCancelHirings.handyman_img)) {
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
                            .load(Utils.IMAGE_URL + mCancelHirings.handyman_img)
                            .placeholder(R.drawable.avtar_images)
                            .error(R.drawable.avtar_images)
                            .transform(transformation)
                            .centerCrop()
                            .resize(mDeviceWidth, (int) (mDeviceWidth))
                            .into(mHanfymanprofileImg);
                }

                if (Utils.validateString(mCancelHirings.order_id)) {
                    mRootView.findViewById(R.id.hire_customer_order_id_textview).setVisibility(View.VISIBLE);
                    mRootView.findViewById(R.id.hire_customer_order_id).setVisibility(View.VISIBLE);
                    ((TextView) mRootView.findViewById(R.id.hire_customer_order_id)).setText(mCancelHirings.order_id);
                }

                if (Utils.validateString(mCancelHirings.created_date)) {
                    mRootView.findViewById(R.id.hire_customer_order_date_textview).setVisibility(View.VISIBLE);
                    mRootView.findViewById(R.id.hire_customer_order_date).setVisibility(View.VISIBLE);
                    ((TextView) mRootView.findViewById(R.id.hire_customer_order_date)).setText(mCancelHirings.created_date);
                }

                if (Utils.validateString(mCancelHirings.job_description)) {
                    job_Description = mCancelHirings.job_description;
                    mRootView.findViewById(R.id.hire_customer_job_description_textview).setVisibility(View.VISIBLE);
                    mRootView.findViewById(R.id.hire_customer_job_description).setVisibility(View.VISIBLE);
                    ((TextView) mRootView.findViewById(R.id.hire_customer_job_description)).setText(job_Description);
                }


                if (Utils.validateString(mCancelHirings.appointment_date)) {
                    mRootView.findViewById(R.id.hire_customer_date_textview).setVisibility(View.VISIBLE);
                    mRootView.findViewById(R.id.hire_customer_date).setVisibility(View.VISIBLE);
                    appointment_date = mCancelHirings.appointment_date;
                    ((TextView) mRootView.findViewById(R.id.hire_customer_date)).setText(appointment_date);
                }


                if (Utils.validateString(mCancelHirings.appointment_time)) {
                    mRootView.findViewById(R.id.hire_customer_time_textview).setVisibility(View.VISIBLE);
                    mRootView.findViewById(R.id.hire_customer_time).setVisibility(View.VISIBLE);
                    String time = mCancelHirings.appointment_time;
                    appointment_time = time.substring(0, 8).trim();
                    ((TextView) mRootView.findViewById(R.id.hire_customer_time)).setText(time.substring(0, 2) + ":" + time.substring(3, 5) + "" + time.substring(8, 11));
                }


                if (Utils.validateString(mCancelHirings.contact_person)) {
                    mRootView.findViewById(R.id.hire_customer_contact_person_textview).setVisibility(View.VISIBLE);
                    mRootView.findViewById(R.id.hire_customer_contact_person).setVisibility(View.VISIBLE);
                    ((TextView) mRootView.findViewById(R.id.hire_customer_contact_person)).setText(mCancelHirings.contact_person);
                }

                if (Utils.validateString(mCancelHirings.contact_no)) {
                    mobile_no = mCancelHirings.hamdyman_mobile;
                    mRootView.findViewById(R.id.hire_customer_contact_no_textview).setVisibility(View.VISIBLE);
                    mRootView.findViewById(R.id.hire_customer_contact_no).setVisibility(View.VISIBLE);
                    ((TextView) mRootView.findViewById(R.id.hire_customer_contact_no)).setText(mCancelHirings.contact_no);
                }

                if (Utils.validateString(mCancelHirings.getAddress())) {
                    mRootView.findViewById(R.id.hire_customer_address_textview).setVisibility(View.VISIBLE);
                    mRootView.findViewById(R.id.hire_customer_address).setVisibility(View.VISIBLE);
                    String address = "", street = "", landmark = "", city = "", state = "", pincode = "", floor = "", apartment = "";
                    if (Utils.validateString(mCancelHirings.getFloor()))
                        floor = mCancelHirings.getFloor() + ", ";
                    if (Utils.validateString(mCancelHirings.getApartment()))
                        apartment = mCancelHirings.getApartment() + ", ";
                    if (Utils.validateString(mCancelHirings.getAddress()))
                        address = mCancelHirings.getAddress();
//					if(Utils.validateString(mCancelHirings.getStreet()))
//						street = mCancelHirings.getStreet();
//					if(Utils.validateString(mCancelHirings.getLandmark()))
//						landmark = mCancelHirings.getLandmark();
//					if(Utils.validateString(mCancelHirings.getCity_name()))
//						city = mCancelHirings.getCity_name();
//					if(Utils.validateString(mCancelHirings.getState_name()))
//						state = mCancelHirings.getState_name();
//					if(Utils.validateString(mCancelHirings.getPincode()))
//						pincode = mCancelHirings.getPincode();
//					((TextView) mRootView.findViewById(R.id.hire_customer_address)).setText(address + "\n" + street + "\n" + landmark + "\n" + city + " - " + pincode);
                    ((TextView) mRootView.findViewById(R.id.hire_customer_address)).setText(floor + apartment + address);
                }

                if (Utils.validateString(mCancelHirings.comment)) {
                    mRootView.findViewById(R.id.hire_customer_requirment_textview).setVisibility(View.VISIBLE);
                    mRootView.findViewById(R.id.hire_customer_requirment).setVisibility(View.VISIBLE);
                    ((TextView) mRootView.findViewById(R.id.hire_customer_requirment)).setText(mCancelHirings.comment);
                }

                if (Utils.validateString(mCancelHirings.hire_status)) {
                    status = mCancelHirings.hire_status;
                }

                if (Utils.validateString(mCancelHirings.id)) {
                    status_id = mCancelHirings.id;
                }

                if (Utils.validateString(mCancelHirings.handyman_id)) {
                    handyman_id = mCancelHirings.handyman_id;
                }

                if (Utils.validateString(mCancelHirings.handyman_name)) {
                    handyman_name = mCancelHirings.handyman_name;
                }

                if (Utils.validateString(mCancelHirings.email)) {
                    handyman_email = mCancelHirings.email;
                }

                if (Utils.validateString(mCancelHirings.rating)) {
                    h_rating = mCancelHirings.rating;
                }

                if (Utils.validateString(mCancelHirings.handyman_rate)) {
                    handyman_rating = mCancelHirings.handyman_rate;
                }

//				Utils.storeString(mSharedPreferences, Utils.CLIENT_ID, mCancelHirings.client_id);
            }

            if (mCompleteHirings != null) {
                ((MainActivity) getActivity()).setTitleText("", mCompleteHirings.handyman_name, "", View.VISIBLE, View.GONE, View.GONE, View.GONE, View.GONE);
                ((TextView) mRootView.findViewById(R.id.handyman_name_profile_txt)).setText(mCompleteHirings.handyman_name);

                if (Utils.validateString(mCompleteHirings.handyman_img)) {
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
                            .load(Utils.IMAGE_URL + mCompleteHirings.handyman_img)
                            .placeholder(R.drawable.avtar_images)
                            .error(R.drawable.avtar_images)
                            .transform(transformation)
                            .centerCrop()
                            .resize(mDeviceWidth, (int) (mDeviceWidth))
                            .into(mHanfymanprofileImg);
                }

                if (Utils.validateString(mCompleteHirings.amount)) {
                    mRootView.findViewById(R.id.amount_layout).setVisibility(View.VISIBLE);
                    ((TextView) mRootView.findViewById(R.id.amount_txt)).setText(mCompleteHirings.amount + "/-");
                }

                if (Utils.validateString(mCompleteHirings.discount)) {
                    mRootView.findViewById(R.id.discount_layout).setVisibility(View.VISIBLE);
                    ((TextView) mRootView.findViewById(R.id.discount_txt)).setText(mCompleteHirings.discount + "%");

                }

                if (Utils.validateString(mCompleteHirings.credit)) {
                    mRootView.findViewById(R.id.credit_layout).setVisibility(View.VISIBLE);
                    ((TextView) mRootView.findViewById(R.id.credit_txt)).setText(mCompleteHirings.credit);

                }

                if (Utils.validateString(mCompleteHirings.total)) {
                    mRootView.findViewById(R.id.total_paid_layout).setVisibility(View.VISIBLE);
                    ((TextView) mRootView.findViewById(R.id.total_paid_txt)).setText(mCompleteHirings.total + "/-");

                }


                if (Utils.validateString(mCompleteHirings.order_id)) {
                    mRootView.findViewById(R.id.hire_customer_order_id_textview).setVisibility(View.VISIBLE);
                    mRootView.findViewById(R.id.hire_customer_order_id).setVisibility(View.VISIBLE);
                    ((TextView) mRootView.findViewById(R.id.hire_customer_order_id)).setText(mCompleteHirings.order_id);
                }

                if (Utils.validateString(mCompleteHirings.created_date)) {
                    mRootView.findViewById(R.id.hire_customer_order_date_textview).setVisibility(View.VISIBLE);
                    mRootView.findViewById(R.id.hire_customer_order_date).setVisibility(View.VISIBLE);
                    ((TextView) mRootView.findViewById(R.id.hire_customer_order_date)).setText(mCompleteHirings.created_date);
                }

                if (Utils.validateString(mCompleteHirings.job_description)) {
                    job_Description = mCompleteHirings.job_description;
                    mRootView.findViewById(R.id.hire_customer_job_description_textview).setVisibility(View.VISIBLE);
                    mRootView.findViewById(R.id.hire_customer_job_description).setVisibility(View.VISIBLE);
                    ((TextView) mRootView.findViewById(R.id.hire_customer_job_description)).setText(job_Description);
                }


                if (Utils.validateString(mCompleteHirings.appointment_date)) {
                    mRootView.findViewById(R.id.hire_customer_date_textview).setVisibility(View.VISIBLE);
                    mRootView.findViewById(R.id.hire_customer_date).setVisibility(View.VISIBLE);
                    appointment_date = mCompleteHirings.appointment_date;
                    ((TextView) mRootView.findViewById(R.id.hire_customer_date)).setText(appointment_date);
                }


                if (Utils.validateString(mCompleteHirings.appointment_time)) {
                    mRootView.findViewById(R.id.hire_customer_time_textview).setVisibility(View.VISIBLE);
                    mRootView.findViewById(R.id.hire_customer_time).setVisibility(View.VISIBLE);
                    String time = mCompleteHirings.appointment_time;
                    appointment_time = time.substring(0, 8).trim();
                    ((TextView) mRootView.findViewById(R.id.hire_customer_time)).setText(time.substring(0, 2) + ":" + time.substring(3, 5) + "" + time.substring(8, 11));
                }


                if (Utils.validateString(mCompleteHirings.contact_person)) {
                    mRootView.findViewById(R.id.hire_customer_contact_person_textview).setVisibility(View.VISIBLE);
                    mRootView.findViewById(R.id.hire_customer_contact_person).setVisibility(View.VISIBLE);
                    ((TextView) mRootView.findViewById(R.id.hire_customer_contact_person)).setText(mCompleteHirings.contact_person);
                }

                if (Utils.validateString(mCompleteHirings.contact_no)) {
                    mobile_no = mCompleteHirings.hamdyman_mobile;
                    mRootView.findViewById(R.id.hire_customer_contact_no_textview).setVisibility(View.VISIBLE);
                    mRootView.findViewById(R.id.hire_customer_contact_no).setVisibility(View.VISIBLE);
                    ((TextView) mRootView.findViewById(R.id.hire_customer_contact_no)).setText(mCompleteHirings.contact_no);
                }

                if (Utils.validateString(mCompleteHirings.getAddress())) {
                    mRootView.findViewById(R.id.hire_customer_address_textview).setVisibility(View.VISIBLE);
                    mRootView.findViewById(R.id.hire_customer_address).setVisibility(View.VISIBLE);
                    String address = "", street = "", landmark = "", city = "", state = "", pincode = "", floor = "", apartment = "";

                    if (Utils.validateString(mCompleteHirings.getFloor()))
                        floor = mCompleteHirings.getFloor() + ", ";
                    if (Utils.validateString(mCompleteHirings.getApartment()))
                        apartment = mCompleteHirings.getApartment() + ", ";
                    if (Utils.validateString(mCompleteHirings.getAddress()))
                        address = mCompleteHirings.getAddress();
//					if(Utils.validateString(mCompleteHirings.getStreet()))
//						street = mCompleteHirings.getStreet();
//					if(Utils.validateString(mCompleteHirings.getLandmark()))
//						landmark = mCompleteHirings.getLandmark();
//					if(Utils.validateString(mCompleteHirings.getCity_name()))
//						city = mCompleteHirings.getCity_name();
//					if(Utils.validateString(mCompleteHirings.getState_name()))
//						state = mCompleteHirings.getState_name();
//					if(Utils.validateString(mCompleteHirings.getPincode()))
//						pincode = mCompleteHirings.getPincode();
//					((TextView) mRootView.findViewById(R.id.hire_customer_address)).setText(address + "\n" + street + "\n" + landmark + "\n" + city + " - " + pincode);
                    ((TextView) mRootView.findViewById(R.id.hire_customer_address)).setText(floor + apartment + address);
                }

                if (Utils.validateString(mCompleteHirings.comment)) {
                    mRootView.findViewById(R.id.hire_customer_requirment_textview).setVisibility(View.VISIBLE);
                    mRootView.findViewById(R.id.hire_customer_requirment).setVisibility(View.VISIBLE);
                    ((TextView) mRootView.findViewById(R.id.hire_customer_requirment)).setText(mCompleteHirings.comment);
                }

                if (Utils.validateString(mCompleteHirings.hire_status)) {
                    status = mCompleteHirings.hire_status;
                }

                if (Utils.validateString(mCompleteHirings.id)) {
                    status_id = mCompleteHirings.id;
                }

                if (Utils.validateString(mCompleteHirings.handyman_id)) {
                    handyman_id = mCompleteHirings.handyman_id;
                }

                if (Utils.validateString(mCompleteHirings.handyman_name)) {
                    handyman_name = mCompleteHirings.handyman_name;
                }

                if (Utils.validateString(mCompleteHirings.email)) {
                    handyman_email = mCompleteHirings.email;
                }

                if (Utils.validateString(mCompleteHirings.rating)) {
                    h_rating = mCompleteHirings.rating;
                }

                if (Utils.validateString(mCompleteHirings.handyman_rate)) {
                    handyman_rating = mCompleteHirings.handyman_rate;
                }

                if (Utils.validateString(mCompleteHirings.user_like)) {
                    like_value = mCompleteHirings.user_like;
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

//				Utils.storeString(mSharedPreferences, Utils.CLIENT_ID, mCompleteHirings.client_id);

            }

            if (mRejectedHirings != null) {
                ((MainActivity) getActivity()).setTitleText("", mRejectedHirings.handyman_name, "", View.VISIBLE, View.GONE, View.GONE, View.GONE, View.GONE);
                ((TextView) mRootView.findViewById(R.id.handyman_name_profile_txt)).setText(mRejectedHirings.handyman_name);

                if (Utils.validateString(mRejectedHirings.handyman_img)) {
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
                            .load(Utils.IMAGE_URL + mRejectedHirings.handyman_img)
                            .placeholder(R.drawable.avtar_images)
                            .error(R.drawable.avtar_images)
                            .transform(transformation)
                            .centerCrop()
                            .resize(mDeviceWidth, (int) (mDeviceWidth))
                            .into(mHanfymanprofileImg);
                }

                if (Utils.validateString(mRejectedHirings.order_id)) {
                    mRootView.findViewById(R.id.hire_customer_order_id_textview).setVisibility(View.VISIBLE);
                    mRootView.findViewById(R.id.hire_customer_order_id).setVisibility(View.VISIBLE);
                    ((TextView) mRootView.findViewById(R.id.hire_customer_order_id)).setText(mRejectedHirings.order_id);
                }

                if (Utils.validateString(mRejectedHirings.created_date)) {
                    mRootView.findViewById(R.id.hire_customer_order_date_textview).setVisibility(View.VISIBLE);
                    mRootView.findViewById(R.id.hire_customer_order_date).setVisibility(View.VISIBLE);
                    ((TextView) mRootView.findViewById(R.id.hire_customer_order_date)).setText(mRejectedHirings.created_date);
                }

                if (Utils.validateString(mRejectedHirings.job_description)) {
                    job_Description = mRejectedHirings.job_description;
                    mRootView.findViewById(R.id.hire_customer_job_description_textview).setVisibility(View.VISIBLE);
                    mRootView.findViewById(R.id.hire_customer_job_description).setVisibility(View.VISIBLE);
                    ((TextView) mRootView.findViewById(R.id.hire_customer_job_description)).setText(job_Description);
                }


                if (Utils.validateString(mRejectedHirings.appointment_date)) {
                    mRootView.findViewById(R.id.hire_customer_date_textview).setVisibility(View.VISIBLE);
                    mRootView.findViewById(R.id.hire_customer_date).setVisibility(View.VISIBLE);
                    appointment_date = mRejectedHirings.appointment_date;
                    ((TextView) mRootView.findViewById(R.id.hire_customer_date)).setText(appointment_date);
                }


                if (Utils.validateString(mRejectedHirings.appointment_time)) {
                    mRootView.findViewById(R.id.hire_customer_time_textview).setVisibility(View.VISIBLE);
                    mRootView.findViewById(R.id.hire_customer_time).setVisibility(View.VISIBLE);
                    String time = mRejectedHirings.appointment_time;
                    appointment_time = time.substring(0, 8).trim();
                    ((TextView) mRootView.findViewById(R.id.hire_customer_time)).setText(time.substring(0, 2) + ":" + time.substring(3, 5) + "" + time.substring(8, 11));
                }


                if (Utils.validateString(mRejectedHirings.contact_person)) {
                    mRootView.findViewById(R.id.hire_customer_contact_person_textview).setVisibility(View.VISIBLE);
                    mRootView.findViewById(R.id.hire_customer_contact_person).setVisibility(View.VISIBLE);
                    ((TextView) mRootView.findViewById(R.id.hire_customer_contact_person)).setText(mRejectedHirings.contact_person);
                }

                if (Utils.validateString(mRejectedHirings.contact_no)) {
                    mobile_no = mRejectedHirings.hamdyman_mobile;
                    mRootView.findViewById(R.id.hire_customer_contact_no_textview).setVisibility(View.VISIBLE);
                    mRootView.findViewById(R.id.hire_customer_contact_no).setVisibility(View.VISIBLE);
                    ((TextView) mRootView.findViewById(R.id.hire_customer_contact_no)).setText(mRejectedHirings.contact_no);
                }

                if (Utils.validateString(mRejectedHirings.getAddress())) {
                    mRootView.findViewById(R.id.hire_customer_address_textview).setVisibility(View.VISIBLE);
                    mRootView.findViewById(R.id.hire_customer_address).setVisibility(View.VISIBLE);
                    String address = "", street = "", landmark = "", city = "", state = "", pincode = "", floor = "", apartment = "";
                    if (Utils.validateString(mRejectedHirings.getFloor()))
                        floor = mRejectedHirings.getFloor() + ", ";
                    if (Utils.validateString(mRejectedHirings.getApartment()))
                        apartment = mRejectedHirings.getApartment() + ", ";
                    if (Utils.validateString(mRejectedHirings.getAddress()))
                        address = mRejectedHirings.getAddress();
//					if(Utils.validateString(mRejectedHirings.getStreet()))
//						street = mRejectedHirings.getStreet();
//					if(Utils.validateString(mRejectedHirings.getLandmark()))
//						landmark = mRejectedHirings.getLandmark();
//					if(Utils.validateString(mRejectedHirings.getCity_name()))
//						city = mRejectedHirings.getCity_name();
//					if(Utils.validateString(mRejectedHirings.getState_name()))
//						state = mRejectedHirings.getState_name();
//					if(Utils.validateString(mRejectedHirings.getPincode()))
//						pincode = mRejectedHirings.getPincode();
//					((TextView) mRootView.findViewById(R.id.hire_customer_address)).setText(address + "\n" + street + "\n" + landmark + "\n" + city + " - " + pincode);
                    ((TextView) mRootView.findViewById(R.id.hire_customer_address)).setText(floor + apartment + address);
                }

                if (Utils.validateString(mRejectedHirings.comment)) {
                    mRootView.findViewById(R.id.hire_customer_requirment_textview).setVisibility(View.VISIBLE);
                    mRootView.findViewById(R.id.hire_customer_requirment).setVisibility(View.VISIBLE);
                    ((TextView) mRootView.findViewById(R.id.hire_customer_requirment)).setText(mRejectedHirings.comment);
                }

                if (Utils.validateString(mRejectedHirings.hire_status)) {
                    status = mRejectedHirings.hire_status;
                }

                if (Utils.validateString(mRejectedHirings.id)) {
                    status_id = mRejectedHirings.id;
                }

                if (Utils.validateString(mRejectedHirings.handyman_id)) {
                    handyman_id = mRejectedHirings.handyman_id;
                }

                if (Utils.validateString(mRejectedHirings.handyman_name)) {
                    handyman_name = mRejectedHirings.handyman_name;
                }

                if (Utils.validateString(mRejectedHirings.email)) {
                    handyman_email = mRejectedHirings.email;
                }

                if (Utils.validateString(mRejectedHirings.rating)) {
                    h_rating = mRejectedHirings.rating;
                }

                if (Utils.validateString(mRejectedHirings.handyman_rate)) {
                    handyman_rating = mRejectedHirings.handyman_rate;
                }

//				Utils.storeString(mSharedPreferences, Utils.CLIENT_ID, mRejectedHirings.client_id);
            }

            if (mAllHirings != null) {
                ((MainActivity) getActivity()).setTitleText("", mAllHirings.handyman_name, "", View.VISIBLE, View.GONE, View.GONE, View.GONE, View.GONE);
                ((TextView) mRootView.findViewById(R.id.handyman_name_profile_txt)).setText(mAllHirings.handyman_name);

                if (Utils.validateString(mAllHirings.handyman_img)) {
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
                            .load(Utils.IMAGE_URL + mAllHirings.handyman_img)
                            .placeholder(R.drawable.avtar_images)
                            .error(R.drawable.avtar_images)
                            .transform(transformation)
                            .centerCrop()
                            .resize(mDeviceWidth, (int) (mDeviceWidth))
                            .into(mHanfymanprofileImg);
                }

                if (Utils.validateString(mAllHirings.order_id)) {
                    mRootView.findViewById(R.id.hire_customer_order_id_textview).setVisibility(View.VISIBLE);
                    mRootView.findViewById(R.id.hire_customer_order_id).setVisibility(View.VISIBLE);
                    ((TextView) mRootView.findViewById(R.id.hire_customer_order_id)).setText(mAllHirings.order_id);
                }

                if (Utils.validateString(mAllHirings.created_date)) {
                    mRootView.findViewById(R.id.hire_customer_order_date_textview).setVisibility(View.VISIBLE);
                    mRootView.findViewById(R.id.hire_customer_order_date).setVisibility(View.VISIBLE);
                    ((TextView) mRootView.findViewById(R.id.hire_customer_order_date)).setText(mAllHirings.created_date);
                }

                if (Utils.validateString(mAllHirings.job_description)) {
                    job_Description = mAllHirings.job_description;
                    mRootView.findViewById(R.id.hire_customer_job_description_textview).setVisibility(View.VISIBLE);
                    mRootView.findViewById(R.id.hire_customer_job_description).setVisibility(View.VISIBLE);
                    ((TextView) mRootView.findViewById(R.id.hire_customer_job_description)).setText(job_Description);
                }


                if (Utils.validateString(mAllHirings.appointment_date)) {
                    mRootView.findViewById(R.id.hire_customer_date_textview).setVisibility(View.VISIBLE);
                    mRootView.findViewById(R.id.hire_customer_date).setVisibility(View.VISIBLE);
                    appointment_date = mAllHirings.appointment_date;
                    ((TextView) mRootView.findViewById(R.id.hire_customer_date)).setText(appointment_date);
                }


                if (Utils.validateString(mAllHirings.appointment_time)) {
                    mRootView.findViewById(R.id.hire_customer_time_textview).setVisibility(View.VISIBLE);
                    mRootView.findViewById(R.id.hire_customer_time).setVisibility(View.VISIBLE);
                    String time = mAllHirings.appointment_time;
                    appointment_time = time.substring(0, 8).trim();
                    ((TextView) mRootView.findViewById(R.id.hire_customer_time)).setText(time.substring(0, 2) + ":" + time.substring(3, 5) + "" + time.substring(8, 11));
                }


                if (Utils.validateString(mAllHirings.contact_person)) {
                    mRootView.findViewById(R.id.hire_customer_contact_person_textview).setVisibility(View.VISIBLE);
                    mRootView.findViewById(R.id.hire_customer_contact_person).setVisibility(View.VISIBLE);
                    ((TextView) mRootView.findViewById(R.id.hire_customer_contact_person)).setText(mAllHirings.contact_person);
                }

                if (Utils.validateString(mAllHirings.contact_no)) {
                    mobile_no = mAllHirings.hamdyman_mobile;
                    mRootView.findViewById(R.id.hire_customer_contact_no_textview).setVisibility(View.VISIBLE);
                    mRootView.findViewById(R.id.hire_customer_contact_no).setVisibility(View.VISIBLE);
                    ((TextView) mRootView.findViewById(R.id.hire_customer_contact_no)).setText(mAllHirings.contact_no);
                }

                if (Utils.validateString(mAllHirings.getAddress())) {
                    mRootView.findViewById(R.id.hire_customer_address_textview).setVisibility(View.VISIBLE);
                    mRootView.findViewById(R.id.hire_customer_address).setVisibility(View.VISIBLE);
                    String address = "", street = "", landmark = "", city = "", state = "", pincode = "", floor = "", apartment = "";
                    if (Utils.validateString(mAllHirings.getFloor()))
                        floor = mAllHirings.getFloor() + ", ";
                    if (Utils.validateString(mAllHirings.getApartment()))
                        apartment = mAllHirings.getApartment() + ", ";
                    if (Utils.validateString(mAllHirings.getAddress()))
                        address = mAllHirings.getAddress();
//					if(Utils.validateString(mAllHirings.getStreet()))
//						street = mAllHirings.getStreet();
//					if(Utils.validateString(mAllHirings.getLandmark()))
//						landmark = mAllHirings.getLandmark();
//					if(Utils.validateString(mAllHirings.getCity_name()))
//						city = mAllHirings.getCity_name();
//					if(Utils.validateString(mAllHirings.getState_name()))
//						state = mAllHirings.getState_name();
//					if(Utils.validateString(mAllHirings.getPincode()))
//						pincode = mAllHirings.getPincode();
//					((TextView) mRootView.findViewById(R.id.hire_customer_address)).setText(address + "\n" + street + "\n" + landmark + "\n" + city + " - " + pincode);
                    ((TextView) mRootView.findViewById(R.id.hire_customer_address)).setText(floor + apartment + address);
                }

                if (Utils.validateString(mAllHirings.comment)) {
                    mRootView.findViewById(R.id.hire_customer_requirment_textview).setVisibility(View.VISIBLE);
                    mRootView.findViewById(R.id.hire_customer_requirment).setVisibility(View.VISIBLE);
                    ((TextView) mRootView.findViewById(R.id.hire_customer_requirment)).setText(mAllHirings.comment);
                }


                if (Utils.validateString(mAllHirings.hire_status)) {
                    status = mAllHirings.hire_status;
                    if (status.equalsIgnoreCase("completed")) {
                        if (Utils.validateString(mAllHirings.amount)) {
                            mRootView.findViewById(R.id.amount_layout).setVisibility(View.VISIBLE);
                            ((TextView) mRootView.findViewById(R.id.amount_txt)).setText(mAllHirings.amount + "/-");
                        }

                        if (Utils.validateString(mAllHirings.discount)) {
                            mRootView.findViewById(R.id.discount_layout).setVisibility(View.VISIBLE);
                            ((TextView) mRootView.findViewById(R.id.discount_txt)).setText(mAllHirings.discount + "%");

                        }

                        if (Utils.validateString(mAllHirings.credit)) {
                            mRootView.findViewById(R.id.credit_layout).setVisibility(View.VISIBLE);
                            ((TextView) mRootView.findViewById(R.id.credit_txt)).setText(mAllHirings.credit);

                        }

                        if (Utils.validateString(mAllHirings.total)) {
                            mRootView.findViewById(R.id.total_paid_layout).setVisibility(View.VISIBLE);
                            ((TextView) mRootView.findViewById(R.id.total_paid_txt)).setText(mAllHirings.total + "/-");

                        }

                        if (Utils.validateString(mAllHirings.user_like)) {
                            like_value = mAllHirings.user_like;
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

                if (Utils.validateString(mAllHirings.id)) {
                    status_id = mAllHirings.id;
                }

                if (Utils.validateString(mAllHirings.handyman_id)) {
                    handyman_id = mAllHirings.handyman_id;
                }

                if (Utils.validateString(mAllHirings.handyman_name)) {
                    handyman_name = mAllHirings.handyman_name;
                }

                if (Utils.validateString(mAllHirings.email)) {
                    handyman_email = mAllHirings.email;
                }

                if (Utils.validateString(mAllHirings.rating)) {
                    h_rating = mAllHirings.rating;
                }

                if (Utils.validateString(mAllHirings.handyman_rate)) {
                    handyman_rating = mAllHirings.handyman_rate;
                }

//				if(Utils.validateString(mAllHirings.closing_status)){
//					closing_status = mAllHirings.closing_status;
//				}

//				Utils.storeString(mSharedPreferences, Utils.CLIENT_ID, mAllHirings.client_id);
            }

        }

		/*new CountDownTimer(7200000, 1000) {

			@Override
			public void onTick(long millisUntilFinished) {
				long seconds = millisUntilFinished / 1000;
				 mTimer.setText("Time: " + String.format("%02d:%02d:%02d", seconds / 3600,
			                (seconds % 3600) / 60, (seconds % 60)));
				
			}
			
			@Override
			public void onFinish() {
				mTimer.setText("Time's up!");
				
			}
		}.start();*/


        if (status.equalsIgnoreCase("pending")) {
            mRootView.findViewById(R.id.rating_img).setVisibility(View.GONE);
            mRootView.findViewById(R.id.map_img).setVisibility(View.GONE);
            mCancel.setVisibility(View.VISIBLE);
            mFaveImg.setVisibility(View.GONE);
            mCancelImg.setVisibility(View.GONE);
            mRejectImg.setVisibility(View.GONE);
            txtSendCoins.setVisibility(View.GONE);

        } else if (status.equalsIgnoreCase("active")) {
            mRootView.findViewById(R.id.rating_img).setVisibility(View.GONE);
            mRootView.findViewById(R.id.map_img).setVisibility(View.GONE);
            mCancel.setVisibility(View.VISIBLE);
            mFaveImg.setVisibility(View.GONE);
            mCancelImg.setVisibility(View.GONE);
            mRejectImg.setVisibility(View.GONE);
            txtSendCoins.setVisibility(View.GONE);

        } else if (status.equalsIgnoreCase("start")) {
            mRootView.findViewById(R.id.rating_img).setVisibility(View.GONE);
            mRootView.findViewById(R.id.map_img).setVisibility(View.VISIBLE);
            mCancel.setVisibility(View.VISIBLE);
            mFaveImg.setVisibility(View.GONE);
            mCancelImg.setVisibility(View.GONE);
            mRejectImg.setVisibility(View.GONE);
            txtSendCoins.setVisibility(View.VISIBLE);

        } else if (status.equalsIgnoreCase("cancelled")) {
            mRootView.findViewById(R.id.rating_img).setVisibility(View.GONE);
            mRootView.findViewById(R.id.map_img).setVisibility(View.GONE);
            mCancel.setVisibility(View.GONE);
            mFaveImg.setVisibility(View.GONE);
            mCancelImg.setVisibility(View.VISIBLE);
            mRejectImg.setVisibility(View.GONE);
            txtSendCoins.setVisibility(View.GONE);

        } else if (status.equalsIgnoreCase("completed")) {
            mRootView.findViewById(R.id.rating_img).setVisibility(View.VISIBLE);
            mRootView.findViewById(R.id.map_img).setVisibility(View.GONE);
            mCancel.setVisibility(View.GONE);
            mFaveImg.setVisibility(View.VISIBLE);
            mCancelImg.setVisibility(View.GONE);
            mRejectImg.setVisibility(View.GONE);
            txtSendCoins.setVisibility(View.GONE);

        } else if (status.equalsIgnoreCase("declined")) {
            mRootView.findViewById(R.id.rating_img).setVisibility(View.GONE);
            mRootView.findViewById(R.id.map_img).setVisibility(View.GONE);
            mCancel.setVisibility(View.GONE);
            mFaveImg.setVisibility(View.GONE);
            mCancelImg.setVisibility(View.GONE);
            mRejectImg.setVisibility(View.VISIBLE);
            txtSendCoins.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {

		/*case R.id.chat_img:
            PackageManager pm = getActivity().getPackageManager();
//			String appPackageName = getActivity().getPackageName();
			try {

				Intent waIntent = new Intent(Intent.ACTION_SEND);
				waIntent.setType("text/plain");
				String text = "Hi, this is your handyman";

				PackageInfo info = pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);
				// Check if package exists or not. If not then code in catch block will be called
				waIntent.setPackage("com.whatsapp");

				waIntent.putExtra(Intent.EXTRA_TEXT, text);
				startActivity(Intent.createChooser(waIntent, "Share with"));

			} catch (NameNotFoundException e) {
				Toast.makeText(getActivity(), "WhatsApp not Installed",	Toast.LENGTH_SHORT).show();
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.whatsapp&hl=en")));
			} 
			
			Uri mUri = Uri.parse("smsto:"+ mSharedPreferences.getString(Utils.MOBILE_NO, "") + "@s.whatsapp.net");
			Intent mIntent = new Intent(Intent.ACTION_SENDTO, mUri);
			mIntent.setPackage("com.whatsapp");
			mIntent.putExtra("sms_body", "The text goes here");
			mIntent.putExtra("chat",true);
			startActivity(mIntent);
			break;*/

            case R.id.map_img:

		/*	if(Utils.validateString(full_address)){
                String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?saddr=%f,%f&daddr=%f,%f (%s)", Double.parseDouble(handyman_lat),Double.parseDouble(handyman_lng),Double.parseDouble(latitude), Double.parseDouble(longitude), full_address );
//				String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?saddr=%f,%f&daddr=%s", Double.parseDouble(handyman_lat),Double.parseDouble(handyman_lng),full_address);
				Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
				startActivity(i);
			} *//*else {
                String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?saddr=%f,%f&daddr=%f,%f", Double.parseDouble(handyman_lat),Double.parseDouble(handyman_lng),Double.parseDouble(latitude), Double.parseDouble(longitude));
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
                Uri number = Uri.parse("tel:" + mobile_no);
                Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
                startActivity(callIntent);

                break;

            case R.id.rating_img:
                getRateCheck(status_id, mSharedPreferences.getString(Utils.USER_ID, ""));
                break;

            case R.id.report_img:
                RegisterComplainFragment registerComplainFragment = new RegisterComplainFragment();
                Bundle bundle = new Bundle();
                bundle.putString(Utils.HIRE_HANDYMAN_ID, handyman_id);
                bundle.putString(Utils.HIRE_HANDYMAN_NAME, handyman_name);
                bundle.putString(Utils.HIRE_HANDYMAN_EMAIL, handyman_email);
                bundle.putString(Utils.HIRE_HANDYMAN_RATING, handyman_rating);
                registerComplainFragment.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, registerComplainFragment).addToBackStack(TAG).commit();
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

                /*Calendar cal = Calendar.getInstance();
                Date currentLocalTime = cal.getTime();
                DateFormat date = new SimpleDateFormat("dd/MM/yyyy");
                DateFormat time = new SimpleDateFormat("HH:mm:ss");
                String currentDate = date.format(currentLocalTime);
                String currentTime = time.format(currentLocalTime);
                Logger.e(TAG, "Current Date :: " + currentDate + " Current Time :: " + currentTime);

                Date t = cal.getTime();
                t.setTime(t.getTime() - 7200000);
                DateFormat timep = new SimpleDateFormat("HH:mm:ss");
                String pastTime = timep.format(t);
                Logger.e(TAG, "New Value : " + pastTime);

                boolean timeFlag = false;
                boolean flag = CheckDates(appointment_date, currentDate);
                if (flag) {
                    Logger.e(TAG, "Success");
                    try {
                        timeFlag = isTimeBetweenTwoTime(pastTime, appointment_time, currentTime);
                        if (timeFlag) {
                            Logger.e(TAG, "Success time");
                        } else {
                            Logger.e(TAG, "Un-Success time");
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                }*/

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Alert");
                builder.setCancelable(false);
//                if (timeFlag)
                    builder.setMessage("Credit points will be deducted while cancelling this order. Are you sure you want to cancel this order.");
//                else
//                    builder.setMessage("Your points will be credited into your account.");
                builder.setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                if (status.equalsIgnoreCase("pending")) {
                                    Utils.storeString(mSharedPreferences, Utils.CANCEL, "CANCEL");
                                    onChangeStatus(status_id, "cancelled", "2");
                                } else if (status.equalsIgnoreCase("active")) {
                                    Utils.storeString(mSharedPreferences, Utils.CANCEL_A, "CANCEL_A");
                                    onChangeStatus(status_id, "cancelled", "2");
                                } else if (status.equalsIgnoreCase("start")) {
                                    Utils.storeString(mSharedPreferences, Utils.CANCEL_S, "CANCEL_S");
                                    onChangeStatus(status_id, "cancelled", "2");
                                }
                            }
                        });
                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (status.equalsIgnoreCase("pending")) {
                            Utils.storeString(mSharedPreferences, Utils.CANCEL, "");
                        } else if (status.equalsIgnoreCase("active")) {
                            Utils.storeString(mSharedPreferences, Utils.CANCEL_A, "");
                        } else if (status.equalsIgnoreCase("start")) {
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

        }
    }

    public boolean CheckDates(String d1, String d2) {
        boolean b = false;
        try {
            if (dfDate.parse(d1).before(dfDate.parse(d2))) {
                b = false;//If start date is before end date
            } else if (dfDate.parse(d1).equals(dfDate.parse(d2))) {
                b = true;//If two dates are equal
            } else {
                b = false; //If start date is after the end date
            }
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return b;
    }

    public static boolean isTimeBetweenTwoTime(String initialTime, String finalTime, String currentTime) throws ParseException {
        String reg = "^([0-1][0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9])$";
        if (initialTime.matches(reg) && finalTime.matches(reg) && currentTime.matches(reg)) {
            boolean valid = false;
            //Start Time
            java.util.Date inTime = new SimpleDateFormat("HH:mm:ss").parse(initialTime);
            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTime(inTime);

            //Current Time
            java.util.Date checkTime = new SimpleDateFormat("HH:mm:ss").parse(currentTime);
            Calendar calendar3 = Calendar.getInstance();
            calendar3.setTime(checkTime);

            //End Time
            java.util.Date finTime = new SimpleDateFormat("HH:mm:ss").parse(finalTime);
            Calendar calendar2 = Calendar.getInstance();
            calendar2.setTime(finTime);

            if (finalTime.compareTo(initialTime) < 0) {
                calendar2.add(Calendar.DATE, 1);
                calendar3.add(Calendar.DATE, 1);
            }

            java.util.Date actualTime = calendar3.getTime();
            if ((actualTime.after(calendar1.getTime()) || actualTime.compareTo(calendar1.getTime()) == 0)
                    && actualTime.before(calendar2.getTime())) {
                valid = true;
            }
            return valid;
        } else {
            throw new IllegalArgumentException("Not a valid time, expecting HH:MM:SS format");
        }

    }


    private void onChangeStatus(String id, String hire_status, String service_updated_by) {

        if (Utils.checkInternetConnection(getActivity())) {
            GetHandymanChangeStatusRequestTask getHandymanChangeStatusRequestTask = new GetHandymanChangeStatusRequestTask(getActivity());
            getHandymanChangeStatusRequestTask.setAsyncCallListener(new AsyncCallListener() {

                @Override
                public void onResponseReceived(Object response) {
                    MyHiringsModel myHiringsModel = (MyHiringsModel) response;
                    if (myHiringsModel.success.equalsIgnoreCase("1")) {
                        Toast.makeText(getActivity(), myHiringsModel.message, Toast.LENGTH_SHORT).show();
                        getActivity().getSupportFragmentManager().popBackStack();

                    } else if (myHiringsModel.success.equalsIgnoreCase("0")) {
//                            Toast.makeText(getActivity(), myHiringsModel.message , Toast.LENGTH_SHORT).show();
                        Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), myHiringsModel.message);
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
            Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), getResources().getString(R.string.connection));
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
                        if (Utils.validateString(hireHandymanModel.debit) && !hireHandymanModel.debit.equalsIgnoreCase("0")) {

                            CoinsAddForPaymentFragment coinsAddForPaymentFragment = new CoinsAddForPaymentFragment();
                            Bundle bundleCoins = new Bundle();
                            bundleCoins.putString(Utils.HIRE_HANDYMAN_STATUS_ID, status_id);
                            bundleCoins.putString(Utils.HIRE_DEBIT_COINS, hireHandymanModel.debit);
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
