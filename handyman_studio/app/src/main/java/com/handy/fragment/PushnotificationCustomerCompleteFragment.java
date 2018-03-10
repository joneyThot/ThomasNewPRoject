package com.handy.fragment;

import java.util.Locale;

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
import android.support.v4.app.FragmentManager;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.handy.MainActivity;
import com.handy.R;
import com.handy.SplashActivity;
import com.handy.logger.Logger;
import com.handy.model.MyHiringsModel;
import com.handy.service.AsyncCallListener;
import com.handy.service.GetHandymanChangeStatusRequestTask;
import com.handy.service.Utils;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import org.json.JSONException;
import org.json.JSONObject;

public class PushnotificationCustomerCompleteFragment extends BaseFragment implements OnClickListener {

    private static String TAG = "PushnotificationCustomerCompleteFragment";

    private SharedPreferences mSharedPreferences;

    String full_address = "", status_id = "", img = "", sub_category = "", service_updated_by = "", /*city = "",*/
            hire_status = "", appointment_date = "", discount = "", client_id = "", completed_date = "", total = "", is_deleted = "", contact_no = "", floor = "",/*street = "",*/
            receiver_name = "", /*state = "", landmark = "",*/
            credit = "", lat = "", /*pincode = "",*/
            appointment_time = "", amount = "", address = "", lng = "", contact_person = "", is_outdated = "", job_description = "",
            apartment = "", img_path = "", comment = "", created_date = "", handyman_id = "", category = "", order_id = "", status = "", client_name = "", client_image = "", client_email = "", client_image_path = "", handyman_name = "", handyman_image = "", handyman_image_path = "", handyman_email = "", handyman_mobile = "", handyman_rating = "", latitude = "0.0", longitude = "0.0",
            is_declined = "";

    Fragment fr;
    View mRootView;
    ImageView mHanfymanprofileImg;
    private int mDeviceWidth = 480;
    ImageView mAccept, mStart, mCancel, mComplete, mReject;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_push_notification_complete, container, false);
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
        ((MainActivity) getActivity()).hideTitleRelativeLayout();

        mActivity.getSlidingMenu().setMode(SlidingMenu.LEFT);
        mActivity.getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);

        SlidingMenuFragment.selectMenu(1);
        mHanfymanprofileImg = (ImageView) mRootView.findViewById(R.id.hanfyman_profile_img);

        mRootView.findViewById(R.id.push_backBtn).setOnClickListener(this);
        mAccept = (ImageView) mRootView.findViewById(R.id.accept_btn);
        mAccept.setOnClickListener(this);
        mStart = (ImageView) mRootView.findViewById(R.id.start_btn);
        mStart.setOnClickListener(this);
        mCancel = (ImageView) mRootView.findViewById(R.id.cancel_btn);
        mCancel.setOnClickListener(this);
        mComplete = (ImageView) mRootView.findViewById(R.id.completed_btn);
        mComplete.setOnClickListener(this);
        mReject = (ImageView) mRootView.findViewById(R.id.reject_btn);

        mRootView.findViewById(R.id.chat_img).setOnClickListener(this);
        mRootView.findViewById(R.id.call_img).setOnClickListener(this);
        mRootView.findViewById(R.id.map_img).setOnClickListener(this);
        mRootView.findViewById(R.id.report_img).setOnClickListener(this);

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
                    client_email = data_jobj.getString("client_email");
                    client_image = data_jobj.getString("client_image");
                    client_image_path = data_jobj.getString("client_image_path");
                    handyman_name = data_jobj.getString("handyman_name");
                    handyman_image = data_jobj.getString("handyman_image");
                    handyman_image_path = data_jobj.getString("handyman_image_path");
                    handyman_email = data_jobj.getString("handyman_email");
                    handyman_mobile = data_jobj.getString("handyman_mobile");
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
            ((TextView) mRootView.findViewById(R.id.push_title_back)).setText(client_name);
            mRootView.findViewById(R.id.handyman_name_profile_txt).setVisibility(View.VISIBLE);
            ((TextView) mRootView.findViewById(R.id.handyman_name_profile_txt)).setText(client_name);
        }


        if (Utils.validateString(client_image_path)) {
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
                    .load(Utils.IMAGE_URL + client_image_path)
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
                mAccept.setVisibility(View.VISIBLE);
                mStart.setVisibility(View.GONE);
                mCancel.setVisibility(View.VISIBLE);
                mComplete.setVisibility(View.GONE);
                mReject.setVisibility(View.GONE);

            } else if (hire_status.equalsIgnoreCase("active")) {
                mAccept.setVisibility(View.GONE);
                mStart.setVisibility(View.VISIBLE);
                mCancel.setVisibility(View.VISIBLE);
                mComplete.setVisibility(View.GONE);
                mReject.setVisibility(View.GONE);

            } else if (hire_status.equalsIgnoreCase("start")) {
                mAccept.setVisibility(View.GONE);
                mStart.setVisibility(View.GONE);
                mCancel.setVisibility(View.VISIBLE);
                mComplete.setVisibility(View.VISIBLE);
                mReject.setVisibility(View.GONE);

            } else if (hire_status.equalsIgnoreCase("cancelled")) {

//                if (Utils.validateString(is_declined)) {
//                    if (is_declined.equalsIgnoreCase("0")) {
//                        mCancel.setClickable(false);
//                        mAccept.setVisibility(View.GONE);
//                        mStart.setVisibility(View.GONE);
//                        mCancel.setVisibility(View.VISIBLE);
//                        mComplete.setVisibility(View.GONE);
//                        mReject.setVisibility(View.GONE);
//
//                    } else if (is_declined.equalsIgnoreCase("1")) {
//                        mAccept.setVisibility(View.GONE);
//                        mStart.setVisibility(View.GONE);
//                        mCancel.setVisibility(View.GONE);
//                        mComplete.setVisibility(View.GONE);
//                        mReject.setVisibility(View.VISIBLE);
//                    }
//                }
                mCancel.setClickable(false);
                mAccept.setVisibility(View.GONE);
                mStart.setVisibility(View.GONE);
                mCancel.setVisibility(View.VISIBLE);
                mComplete.setVisibility(View.GONE);
                mReject.setVisibility(View.GONE);

            } else if (hire_status.equalsIgnoreCase("completed")) {
                mComplete.setClickable(false);
                mAccept.setVisibility(View.GONE);
                mStart.setVisibility(View.GONE);
                mCancel.setVisibility(View.GONE);
                mComplete.setVisibility(View.VISIBLE);
                mReject.setVisibility(View.GONE);

            } else if (hire_status.equalsIgnoreCase("declined")) {
                mAccept.setVisibility(View.GONE);
                mStart.setVisibility(View.GONE);
                mCancel.setVisibility(View.GONE);
                mComplete.setVisibility(View.GONE);
                mReject.setVisibility(View.VISIBLE);
            }
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
                        MyHiringsFragment_new myHiringsFragment_new = new MyHiringsFragment_new();
                        Bundle bundle1 = new Bundle();
                        bundle1.putString("Pending", "Pending");
                        myHiringsFragment_new.setArguments(bundle1);
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, myHiringsFragment_new).commit();

                    } else if (hire_status.equalsIgnoreCase("active")) {
                        MyHiringsFragment_new myHiringsFragment_new = new MyHiringsFragment_new();
                        Bundle bundle1 = new Bundle();
                        bundle1.putString("Active", "Active");
                        myHiringsFragment_new.setArguments(bundle1);
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, myHiringsFragment_new).commit();

                    } else if (hire_status.equalsIgnoreCase("start")) {
                        MyHiringsFragment_new myHiringsFragment_new = new MyHiringsFragment_new();
                        Bundle bundle1 = new Bundle();
                        bundle1.putString("Start", "Start");
                        myHiringsFragment_new.setArguments(bundle1);
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, myHiringsFragment_new).commit();

                    } else if (hire_status.equalsIgnoreCase("cancelled")) {
                        MyHiringsFragment_new myHiringsFragment_new = new MyHiringsFragment_new();
                        Bundle bundle1 = new Bundle();
                        bundle1.putString("Cancel", "Cancel");
                        myHiringsFragment_new.setArguments(bundle1);
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, myHiringsFragment_new).commit();

                    } else if (hire_status.equalsIgnoreCase("completed")) {
                        MyHiringsFragment_new myHiringsFragment_new = new MyHiringsFragment_new();
                        Bundle bundle1 = new Bundle();
                        bundle1.putString("Complete", "Complete");
                        myHiringsFragment_new.setArguments(bundle1);
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, myHiringsFragment_new).commit();

                    } else if (hire_status.equalsIgnoreCase("declined")) {
                        MyHiringsFragment_new myHiringsFragment_new = new MyHiringsFragment_new();
                        Bundle bundle1 = new Bundle();
                        bundle1.putString("declined", "declined");
                        myHiringsFragment_new.setArguments(bundle1);
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, myHiringsFragment_new).commit();
                    }
                }

                break;

            case R.id.accept_btn:
                if (hire_status.equalsIgnoreCase("pending")) {
                    Utils.storeString(mSharedPreferences, Utils.ACCEPT, "ACCEPT");
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Alert");
                builder.setCancelable(false);
                builder.setMessage("Are you sure for accept.");
                builder.setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int i) {

//							mAccept.setVisibility(View.GONE);
//							mStart.setVisibility(View.VISIBLE);
//							mCancel.setVisibility(View.VISIBLE);
//							mComplete.setVisibility(View.GONE);

                                if (Utils.validateString(status_id)) {
                                    onChangeStatus(status_id, "active", "1");

                                }


                            }
                        });
                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (hire_status.equalsIgnoreCase("pending")) {
                            Utils.storeString(mSharedPreferences, Utils.ACCEPT, "");

                        }
                    }

                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                break;

            case R.id.cancel_btn:

                if (hire_status.equalsIgnoreCase("pending")) {
                    Utils.storeString(mSharedPreferences, Utils.CANCEL, "CANCEL");
                } else if (hire_status.equalsIgnoreCase("active")) {
                    Utils.storeString(mSharedPreferences, Utils.CANCEL_A, "CANCEL_A");
                } else if (hire_status.equalsIgnoreCase("start")) {
                    Utils.storeString(mSharedPreferences, Utils.CANCEL_S, "CANCEL_S");
                }

                AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
                builder1.setTitle("Alert");
                builder1.setCancelable(false);
                builder1.setMessage("Are you sure for cancel.");
                builder1.setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                mCancel.setClickable(false);
//							mAccept.setVisibility(View.GONE);
//							mStart.setVisibility(View.GONE);
//							mCancel.setVisibility(View.VISIBLE);
//							mComplete.setVisibility(View.GONE);

                                if (Utils.validateString(status_id)) {
                                    onChangeStatus(status_id, "cancelled", "1");

                                }

                            }
                        });
                builder1.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {

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
                AlertDialog alertDialog1 = builder1.create();
                alertDialog1.show();
                break;

            case R.id.completed_btn:
                mComplete.setClickable(false);
                mAccept.setVisibility(View.GONE);
                mStart.setVisibility(View.GONE);
                mCancel.setVisibility(View.GONE);
                mComplete.setVisibility(View.VISIBLE);

                if (hire_status.equalsIgnoreCase("start")) {
                    Utils.storeString(mSharedPreferences, Utils.COMPELETE, "COMPELETE");
                }

                SignatureFragment signatureFragment = new SignatureFragment();
                Bundle bundle = new Bundle();
                bundle.putString(Utils.HANDYMAN_JOB_DESCRIPTION, job_description);
                if (Utils.validateString(status_id)) {
                    bundle.putString(Utils.JOB_ID, status_id);
                    bundle.putString(Utils.STATUS_ID, status_id);
                }
                if (Utils.validateString(client_id)) {
                    bundle.putString(Utils.CLIENT_ID, client_id);
                }

                signatureFragment.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, signatureFragment).addToBackStack(TAG).commit();
                break;

            case R.id.call_img:
                if (Utils.validateString(contact_no)) {
                    Uri number = Uri.parse("tel:" + contact_no);
                    Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
                    startActivity(callIntent);
                }

                break;

            case R.id.map_img:
                if (Utils.validateString(lat) && Utils.validateString(lng)) {
                    if (Utils.validateString(full_address)) {
//					String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?saddr=%f,%f&daddr=%s", SplashActivity.latitude, SplashActivity.longitude, full_address );
                        String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?saddr=%f,%f&daddr=%f,%f (%s)", SplashActivity.latitude, SplashActivity.longitude, Double.parseDouble(latitude), Double.parseDouble(longitude), full_address);
                        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                        startActivity(i);
                    } /*else {
                    String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?saddr=%f,%f&daddr=%f,%f", SplashActivity.latitude, SplashActivity.longitude, Double.parseDouble(lat), Double.parseDouble(lng));
					Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
					startActivity(i);
				}*/
                }

                break;

            case R.id.report_img:
                RegisterComplainFragment registerComplainFragment = new RegisterComplainFragment();
                Bundle bundle1 = new Bundle();
                if (Utils.validateString(client_name))
                    bundle1.putString(Utils.CLIENT_NAME, client_name);
                if (Utils.validateString(client_email))
                    bundle1.putString(Utils.CLIENT_EMAIL, client_email);
                registerComplainFragment.setArguments(bundle1);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, registerComplainFragment).addToBackStack(TAG).commit();
//			getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new RegisterComplainFragment()).addToBackStack(TAG).commit();
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
                            MyHiringsFragment_new myHiringsFragment_new = new MyHiringsFragment_new();
                            Bundle bundle1 = new Bundle();
                            bundle1.putString("Pending", "Pending");
                            myHiringsFragment_new.setArguments(bundle1);
                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, myHiringsFragment_new).commit();

                        } else if (hire_status.equalsIgnoreCase("active")) {
                            MyHiringsFragment_new myHiringsFragment_new = new MyHiringsFragment_new();
                            Bundle bundle1 = new Bundle();
                            bundle1.putString("Active", "Active");
                            myHiringsFragment_new.setArguments(bundle1);
                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, myHiringsFragment_new).commit();

                        } else if (hire_status.equalsIgnoreCase("start")) {
                            MyHiringsFragment_new myHiringsFragment_new = new MyHiringsFragment_new();
                            Bundle bundle1 = new Bundle();
                            bundle1.putString("Start", "Start");
                            myHiringsFragment_new.setArguments(bundle1);
                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, myHiringsFragment_new).commit();

                        } else if (hire_status.equalsIgnoreCase("cancelled")) {
                            MyHiringsFragment_new myHiringsFragment_new = new MyHiringsFragment_new();
                            Bundle bundle1 = new Bundle();
                            bundle1.putString("Cancel", "Cancel");
                            myHiringsFragment_new.setArguments(bundle1);
                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, myHiringsFragment_new).commit();

                        } else if (hire_status.equalsIgnoreCase("completed")) {
                            MyHiringsFragment_new myHiringsFragment_new = new MyHiringsFragment_new();
                            Bundle bundle1 = new Bundle();
                            bundle1.putString("Complete", "Complete");
                            myHiringsFragment_new.setArguments(bundle1);
                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, myHiringsFragment_new).commit();

                        } else if (hire_status.equalsIgnoreCase("rejected")) {
                        }

                    } else if (myHiringsModel.success.equalsIgnoreCase("0")) {
                        Toast.makeText(getActivity(), myHiringsModel.message, Toast.LENGTH_SHORT).show();
//                        	Utils.showMessageDialog(getActivity(), getResources().getString(R.string.alert), myHiringsModel.message);
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

}
